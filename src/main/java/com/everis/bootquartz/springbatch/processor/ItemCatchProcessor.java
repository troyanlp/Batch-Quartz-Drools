package com.everis.bootquartz.springbatch.processor;

import org.springframework.batch.item.ItemProcessor;

import com.everis.bootquartz.model.ExamResult;

public class ItemCatchProcessor implements ItemProcessor<ExamResult, ExamResult> {

	@Override
	public ExamResult process(ExamResult item) throws Exception {
		System.out.println("Catch Processor");
		System.out.println(item);
		return item;
	}

}
