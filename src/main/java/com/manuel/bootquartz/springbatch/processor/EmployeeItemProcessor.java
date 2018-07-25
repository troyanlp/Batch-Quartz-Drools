package com.manuel.bootquartz.springbatch.processor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.manuel.bootquartz.model.Employee;
import com.manuel.bootquartz.model.ExamResult;
import com.manuel.bootquartz.service.DroolsService;

public class EmployeeItemProcessor implements ItemProcessor<Employee, Employee> {
	@Autowired
	private DroolsService service;
	private StepExecution stepExecution;
	private List<ExamResult> results = new ArrayList<ExamResult>();

	@Override
	public Employee process(Employee result) throws Exception {

		// System.out.println("Processing result :" + result);

		return result;
	}

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

}
