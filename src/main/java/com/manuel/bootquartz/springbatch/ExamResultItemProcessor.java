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

		if (!result.isPassed()) {
			return null;
		}

		return result;
	}

}
