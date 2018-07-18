package com.manuel.bootquartz.springbatch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.manuel.bootquartz.model.ExamResult;
import com.manuel.bootquartz.service.DroolsService;

public class ExamResultItemProcessor implements ItemProcessor<ExamResult, ExamResult> {
	@Autowired
	private DroolsService service;

	@Override
	public ExamResult process(ExamResult result) throws Exception {

		System.out.println("Processing result :" + result);

		service.execute(result);

		System.out.println("Age is: " + result.getAge());
		System.out.println("Percentage is: " + result.getPercentage());
		System.out.println("Milenial is: " + result.isMilenial());
		System.out.println("Passed is: " + result.isPassed());

		if (!result.isPassed()) {
			return null;
		}

		return result;
	}

}
