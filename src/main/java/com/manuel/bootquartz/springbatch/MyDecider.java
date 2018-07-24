package com.manuel.bootquartz.springbatch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class MyDecider implements JobExecutionDecider {

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		String status = (String) stepExecution.getExecutionContext().get("writingMode");
		if (status.compareTo("XML") == 0) {
			System.out.println("------------------------------");
			System.out.println("Completed -> Go to step 2");
			System.out.println("------------------------------");
			status = "XML";
		} else if (status.compareTo("DB") == 0) {
			System.out.println("------------------------------");
			System.out.println("Completed -> Go to step 3");
			System.out.println("------------------------------");
			status = "DB";
		}

		FlowExecutionStatus flow = new FlowExecutionStatus(status);
		return flow;
	}

}
