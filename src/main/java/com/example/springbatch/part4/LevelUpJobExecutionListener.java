package com.example.springbatch.part4;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.LocalDate;
import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
public class LevelUpJobExecutionListener implements JobExecutionListener {

    private final UserREpository userREpository;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Collection<User> users = userREpository.findAllByUpdatedDate(LocalDate.now());

        //long time = jobExecution.getEndTime().toLocalDate() -  jobExecution.getStartTime().toLocalDate();

        log.info("회원등급 업데이트 배치 프로그램");
        log.info("-----------------------");
        log.info("총 데이터 처리 {}건, 처리 시간 {}millis", users.size(), 10);
    }
}
