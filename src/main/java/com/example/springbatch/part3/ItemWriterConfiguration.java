package com.example.springbatch.part3;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ItemWriterConfiguration {

    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    @Bean(name = "itemWriterJob")
    public Job itemWriterJob(JobRepository jobRepository, Step csvItemWriterStep, Step jdbcBatchItemWriterStep, Step jpaItemWriterStep) {
        return new JobBuilder("itemWriterJob", jobRepository)
                .incrementer(new RunIdIncrementer()) // 항상 다른 JobInstance가 생성된다
                .start(csvItemWriterStep)
                .next(jdbcBatchItemWriterStep)
                .next(jpaItemWriterStep)
                .build();
    }

    @Bean
    public Step csvItemWriterStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
        return new StepBuilder("csvItemWriterStep", jobRepository)
                .<Person, Person>chunk(10, platformTransactionManager)
                .reader(itemReader())
                .writer(csvFileItemWriter())
                .writer(jdbcBatchItemWriter())
                .build();
    }

    @Bean
    public Step jdbcBatchItemWriterStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
        return new StepBuilder("jdbcItemWriterStep", jobRepository)
                .<Person, Person>chunk(10, platformTransactionManager)
                .reader(itemReader())
                .writer(jdbcBatchItemWriter())
                .build();
    }

    @Bean
    public Step jpaItemWriterStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) throws Exception {
        return new StepBuilder("jpaItemWriterStep", jobRepository)
                .<Person, Person>chunk(10, platformTransactionManager)
                .reader(itemReader())
                .writer(jpaItemWriter())
                .build();
    }

    private ItemWriter<Person> jpaItemWriter() throws Exception {
        JpaItemWriter<Person> itemWriter = new JpaItemWriterBuilder<Person>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true)
                .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;

    }

    private ItemWriter<Person> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<Person> itemWriter = new JdbcBatchItemWriterBuilder<Person>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("insert into person(name, age, address) values (:name, :age, :address) ")
                .build();

        itemWriter.afterPropertiesSet();

        return itemWriter;
    }


    private ItemWriter<Person> csvFileItemWriter() throws Exception {
        BeanWrapperFieldExtractor<Person> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[] {"id", "name", "age", "address"});

        DelimitedLineAggregator<Person> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        FlatFileItemWriter<Person> itemWriter = new FlatFileItemWriterBuilder<Person>()
                .name("csvFileWriter")
                .encoding("UTF-8")
                .resource(new FileSystemResource("output/test-output.csv"))
                .lineAggregator(lineAggregator)
                .headerCallback(writer -> writer.write("id,이름,나이,거주지"))
                .footerCallback(writer -> writer.write("----------------\n"))
                .append(true)       // 매번 실행할때마다 새로운 데이터가 들어간다
                .build();
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    private ItemReader<Person> itemReader() {
        return new CustomItemReader<>(getItems());
    }

    private List<Person> getItems() {
        List<Person> items = new ArrayList<>();

        for (int i=0; i< 100; i++) {
            items.add(new Person("test name" +i, "test age", "test address"));
        }

        return items;
    }
}
