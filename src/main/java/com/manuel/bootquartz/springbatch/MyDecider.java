package com.manuel.bootquartz.springbatch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class MyDecider implements JobExecutionDecider {

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		String status;

		if (1 == 1) {
			status = "XML";
		} else {
			status = "DB";
		}
		return new FlowExecutionStatus(status);
	}

}
