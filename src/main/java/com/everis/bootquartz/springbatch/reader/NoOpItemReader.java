package com.everis.bootquartz.springbatch.reader;

import java.util.ArrayList;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.everis.bootquartz.model.ExamResult;

public class NoOpItemReader implements ItemReader<ExamResult> {
	private StepExecution stepExecution;
	int lineNumber = 0;
	ArrayList<ExamResult> resultList = new ArrayList<ExamResult>();

	@Override
	public ExamResult read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		System.out.println("A leer!");
		System.out.println(resultList.size());
		if (!resultList.isEmpty()) {
			lineNumber++;
			ExamResult exam = resultList.get(0);
			resultList.remove(0);
			return exam;
		} else {
			resultList.clear();
			lineNumber = 0;
			System.out.println("Vacio!");
			// this.stepExecution.setExitStatus(ExitStatus.COMPLETED);
			return null;
		}
	}

	@BeforeStep
	public void retrieveInterstepData(StepExecution stepExecution) throws Exception {
		this.stepExecution = stepExecution;
		JobExecution jobExecution = stepExecution.getJobExecution();
		ExecutionContext jobContext = jobExecution.getExecutionContext();
		resultList = (ArrayList<ExamResult>) jobContext.get("results");
		System.out.println("Before Step");
	}

	// @AfterStep
	// public void putData() {
	// ExecutionContext stepContext = this.stepExecution.getExecutionContext();
	// stepContext.put("status", "END");
	// }

}
