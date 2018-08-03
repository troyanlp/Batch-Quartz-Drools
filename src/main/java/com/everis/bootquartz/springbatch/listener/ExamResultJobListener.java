package com.everis.bootquartz.springbatch.listener;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class ExamResultJobListener implements JobExecutionListener {

	private DateTime startTime, stopTime;
	private String jobName;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		startTime = new DateTime();

		jobName = jobExecution.getJobInstance().getJobName();

		System.out.println(jobName + " starts at :" + startTime);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		stopTime = new DateTime();
		System.out.println(jobName + " stops at :" + stopTime);
		System.out.println("Total time take in millis :" + getTimeInMillis(startTime, stopTime));

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println(jobName + " job completed successfully");
			// Here you can perform some other business logic like cleanup
		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			System.out.println(jobName + " job failed with following exceptions ");
			List<Throwable> exceptionList = jobExecution.getAllFailureExceptions();
			for (Throwable th : exceptionList) {
				System.err.println("exception :" + th.getLocalizedMessage());
			}
		}
	}

	private long getTimeInMillis(DateTime start, DateTime stop) {
		return stop.getMillis() - start.getMillis();
	}

}
