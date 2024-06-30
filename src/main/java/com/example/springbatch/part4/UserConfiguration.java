package com.example.springbatch.part4;

import com.example.springbatch.part5.JobParametersDecide;
import com.example.springbatch.part5.OrderStatistics;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class UserConfiguration {

    private final String JOB_NAME = "userJob";
    private final int CHUNK = 1000;
    private final UserREpository userREpository;
    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;

    @Bean(JOB_NAME)
    public Job userJob(JobRepository jobRepository, Step saveUserStep, Step userLevelUpStep, Step orderStatisticsStep) {
        return new JobBuilder(JOB_NAME, jobRepository)
                .incrementer(new RunIdIncrementer()) // 항상 다른 JobInstance가 생성된다
                .start(saveUserStep)
                .next(userLevelUpStep)
                .listener(new LevelUpJobExecutionListener(userREpository))
//                .next(new JobParametersDecide("date"))
//                .on(JobParametersDecide.CONTINUE.getName())
//                .to(orderStatisticsStep)
                .next(orderStatisticsStep)
//              .build()
                .build();
    }

    @Bean(JOB_NAME+"_orderStatisticsStep")
    @JobScope
    public Step orderStatisticsStep(@Value("#{jobParameters[date]}") String date,
                                    @Value("#{jobParameters[path]}") String path,
                                    JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
        return new StepBuilder(JOB_NAME+"_orderStatisticsStep", jobRepository)
                .<OrderStatistics, OrderStatistics>chunk(CHUNK, platformTransactionManager)
                .reader(orderStatisticsItemReader(date))
                .writer(orderStatisticsItemWriter(date, path))
                .build();
    }

    private ItemWriter<? super OrderStatistics> orderStatisticsItemWriter(String date, String path) throws Exception {
        YearMonth yearMonth = YearMonth.parse(date);
        String fileName = yearMonth.getYear() + "년_" + yearMonth.getMonthValue() + "월_일별_주문_금액.csv";

        BeanWrapperFieldExtractor<OrderStatistics> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"amount", "date"});

        DelimitedLineAggregator<OrderStatistics> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<OrderStatistics> itemWriter = new FlatFileItemWriterBuilder<OrderStatistics>()
                .resource(new FileSystemResource(path+fileName))
                .lineAggregator(lineAggregator)
                .name(JOB_NAME+"_orderStatisticsItemWriter")
                .encoding("UTF-8")
                .headerCallback(writer -> writer.write("total_amount,date"))
                .build();

        itemWriter.afterPropertiesSet();
        return itemWriter;

    }

    private ItemReader<? extends OrderStatistics> orderStatisticsItemReader(String date) throws Exception {
        YearMonth yearMonth = YearMonth.parse(date);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", yearMonth.atDay(1));
        parameters.put("endDate", yearMonth.atEndOfMonth());

        Map<String, Order> sortKey = new HashMap<>();
        sortKey.put("created_date", Order.ASCENDING);

        JdbcPagingItemReader<OrderStatistics> itemReader = new JdbcPagingItemReaderBuilder<OrderStatistics>()
                .dataSource(dataSource)
                .rowMapper((resultSet, i) -> OrderStatistics.builder()
                        .amount(resultSet.getString(1))
                        .date(LocalDate.parse(resultSet.getString(2), DateTimeFormatter.ISO_DATE))
                        .build())
                .pageSize(CHUNK)
                .name(JOB_NAME+"_orderStatisticsItemReader")
                .selectClause("sum(amount), created_date")
                .fromClause("orders")
                .whereClause("created_date >= :startDate and created_date <= :endDate")
                .groupClause("created_date")
                .parameterValues(parameters)
                .sortKeys(sortKey)
                .build();

        itemReader.afterPropertiesSet();

        return itemReader;
    }


    @Bean(JOB_NAME+"_saveUserStep")
    public Step saveUserStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder(JOB_NAME+"_saveUserStep", jobRepository)
                .tasklet(new SaveUserTasklet(userREpository), platformTransactionManager)
                .build();

    }

    @Bean(JOB_NAME+"_userLevelUpStep")
    public Step userLevelUpStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
        return new StepBuilder(JOB_NAME+"_userLevelUpStep", jobRepository)
                .<User, User>chunk(CHUNK, platformTransactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    private ItemWriter<? super User> itemWriter() {
        return users -> {
            users.forEach(x -> {
                x.levelUp();
                userREpository.save(x);
            });
        };

    }

    private ItemProcessor<? super User,? extends User> itemProcessor() {
        return user -> {
            if (user.availableLevelUp()) {
                return user;
            }
            return null;
        };
    }

    private ItemReader<? extends User> itemReader() throws Exception {
        JpaPagingItemReader<User> itemReader = new JpaPagingItemReaderBuilder<User>()
                .queryString("select u from User u")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK)
                .name(JOB_NAME+"_userItemReader")
                .build();

        itemReader.afterPropertiesSet();

        return itemReader;
    }
}
