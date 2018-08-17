package com.everis.bootquartz.springbatch.decider;

import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

import com.everis.bootquartz.model.ExamResult;

public class MyDecider implements JobExecutionDecider {

	String status = null;
	List<ExamResult> results = null;

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		if (status == null)
			status = (String) stepExecution.getExecutionContext().get("writingMode");

		if (results == null)
			results = (List<ExamResult>) stepExecution.getExecutionContext().get("results");

		// if (results != null && !results.isEmpty()) {
		// ExamResult result = results.get(0);
		// if (result != null) {
		// if (result.getAge() < 30) {
		// System.out.println("Tiene " + result.getAge() + " asi que es CSV.");
		// status = "CSV";
		// } else if (result.getAge() >= 30 && result.getAge() <= 50) {
		// System.out.println("Tiene " + result.getAge() + " asi que es XML.");
		// status = "XML";
		// } else {
		// System.out.println("Tiene " + result.getAge() + " asi que es DB.");
		// status = "DB";
		// }
		// }
		// // results.remove(0);
		// } else if (results.isEmpty()) {
		// status = "END";
		// }
		//
		// status = "CSV";

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
		} else if (status.compareTo("CSV") == 0) {
			System.out.println("------------------------------");
			System.out.println("Completed -> Go to step CSV");
			System.out.println("------------------------------");
			status = "CSV";
			// return FlowExecutionStatus.FAILED;
		}

		FlowExecutionStatus flow = new FlowExecutionStatus(status);
		return flow;
	}

}
