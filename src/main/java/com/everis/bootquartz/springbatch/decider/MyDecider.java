package com.everis.bootquartz.springbatch.decider;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class MyDecider implements JobExecutionDecider {

	String status = null;

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		if (status == null)
			status = (String) stepExecution.getExecutionContext().get("writingMode");
		if (status.compareTo("XML") == 0) {
			System.out.println("------------------------------");
			System.out.println("Completed -> Go to step XML");
			System.out.println("------------------------------");
			status = "XML";
			// return FlowExecutionStatus.COMPLETED;
		} else if (status.compareTo("DB") == 0) {
			System.out.println("------------------------------");
			System.out.println("Completed -> Go to step DB");
			System.out.println("------------------------------");
			status = "DB";
			// return FlowExecutionStatus.FAILED;
		} else if (status.compareTo("CVS") == 0) {
			System.out.println("------------------------------");
			System.out.println("Completed -> Go to step CVS");
			System.out.println("------------------------------");
			status = "CVS";
			// return FlowExecutionStatus.FAILED;
		}

		FlowExecutionStatus flow = new FlowExecutionStatus(status);
		return flow;
	}

}
