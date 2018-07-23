package com.manuel.bootquartz.springbatch;

import org.springframework.batch.item.ItemProcessor;

import com.manuel.bootquartz.model.ExamResult;

public class ItemCatchProcessor implements ItemProcessor<ExamResult, ExamResult> {

	@Override
	public ExamResult process(ExamResult item) throws Exception {
		System.out.println("Catch Processor");
		System.out.println(item);
		return item;
	}

}
