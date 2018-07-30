package com.manuel.bootquartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;

import com.manuel.bootquartz.service.SampleService;

public class QuartzJob implements Job {
	@Autowired
	private SampleService service;

	@Autowired
	JobLauncher jobLauncher;

	@Autowired
	org.springframework.batch.core.Job job;

	@Override
	public void execute(JobExecutionContext jobExecutionContext) {
		service.hello();
		try {
			JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
					.toJobParameters();
			JobExecution execution = jobLauncher.run(job, jobParameters);
			System.out.println("Exit Status : " + execution.getStatus());
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}

	}
}
