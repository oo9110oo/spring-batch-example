package com.example.springbatch.part3;

import com.example.springbatch.TestConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { SavePersonConfiguration.class, TestConfiguration.class })
public class SavePersonConfigurationTest {

    @Autowired private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired private PersonRepository personRepository;

    @After
    public void tearDown() throws Exception {
        personRepository.deleteAll();
    }

    @Test
    public void test_step() {
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("savePersonStep");

        Assertions.assertThat(jobExecution.getStepExecutions().stream()
                        .mapToLong(StepExecution::getWriteCount)
                        .sum())
                .isEqualTo(personRepository.count())
                .isEqualTo(3);
    }


    @Test
    public void test_allow_duplicate() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("allow_duplicate", "false")
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        Assertions.assertThat(jobExecution.getStepExecutions().stream()
                .mapToLong(StepExecution::getWriteCount)
                .sum())
                .isEqualTo(personRepository.count())
                .isEqualTo(3);
    }

    @Test
    public void test_not_allow_duplicate() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("allow_duplicate", "true")
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        Assertions.assertThat(jobExecution.getStepExecutions().stream()
                        .mapToLong(StepExecution::getWriteCount)
                        .sum())
                .isEqualTo(personRepository.count())
                .isEqualTo(100);
    }
}
