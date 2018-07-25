package com.manuel.bootquartz.springbatch.employee;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class EmployeeJobListener implements JobExecutionListener {

	private DateTime startTime, stopTime;

	@Override
	public void beforeJob(JobExecution jobExecution) {
		startTime = new DateTime();
		System.out.println("Employee Job starts at :" + startTime);
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		stopTime = new DateTime();
		System.out.println("Employee Job stops at :" + stopTime);
		System.out.println("Total time take in millis :" + getTimeInMillis(startTime, stopTime));

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			System.out.println("Employee job completed successfully");
			// Here you can perform some other business logic like cleanup
		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			System.out.println("Employee job failed with following exceptions ");
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
