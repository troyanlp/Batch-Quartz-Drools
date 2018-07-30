package com.everis.bootquartz.springbatch.listener;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.batch.core.ItemWriteListener;

import com.everis.bootquartz.model.Employee;

public class EmployeeItemWriterListener implements ItemWriteListener<Employee> {

	private DateTime startTime, stopTime;

	@Override
	public void beforeWrite(List<? extends Employee> items) {
		// System.out.println("Antes de escribir!");
		startTime = new DateTime();
	}

	@Override
	public void afterWrite(List<? extends Employee> items) {
		stopTime = new DateTime();
		System.out.println("WRITING TIME: " + getTimeInMillis(startTime, stopTime));
	}

	@Override
	public void onWriteError(Exception exception, List<? extends Employee> items) {
		// TODO Auto-generated method stub

	}

	private long getTimeInMillis(DateTime start, DateTime stop) {
		return stop.getMillis() - start.getMillis();
	}

}
