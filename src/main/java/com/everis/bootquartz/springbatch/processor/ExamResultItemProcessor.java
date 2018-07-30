package com.everis.bootquartz.springbatch.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.everis.bootquartz.model.ExamResult;
import com.everis.bootquartz.service.DroolsService;

public class ExamResultItemProcessor implements ItemProcessor<ExamResult, ExamResult> {
	@Autowired
	private DroolsService service;
	private StepExecution stepExecution;
	private List<ExamResult> results = new ArrayList<ExamResult>();

	@Override
	public ExamResult process(ExamResult result) throws Exception {

		System.out.println("Processing result :" + result);

		service.execute(result);

		// System.out.println("Age is: " + result.getAge());
		// System.out.println("Percentage is: " + result.getPercentage());
		// System.out.println("Milenial is: " + result.isMilenial());
		// System.out.println("Passed is: " + result.isPassed());

		if (!result.isPassed()) {
			return null;
		}

		results.add(result);

		return result;
	}

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@AfterStep
	public void putData() {
		ExecutionContext stepContext = this.stepExecution.getExecutionContext();
		stepContext.put("results", results);
		stepContext.put("writingMode", "DB");
	}

}
