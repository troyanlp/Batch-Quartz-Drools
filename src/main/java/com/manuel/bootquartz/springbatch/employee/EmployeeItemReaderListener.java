package com.manuel.bootquartz.springbatch.employee;

import org.joda.time.DateTime;
import org.springframework.batch.core.ItemReadListener;

import com.manuel.bootquartz.model.Employee;

public class EmployeeItemReaderListener implements ItemReadListener<Employee> {

	private DateTime startTime, stopTime;

	@Override
	public void beforeRead() {
		// System.out.println("Antes de leer!");
		startTime = new DateTime();

	}

	@Override
	public void afterRead(Employee item) {
		stopTime = new DateTime();
		System.out.println("READING TIME: " + getTimeInMillis(startTime, stopTime));
	}

	@Override
	public void onReadError(Exception ex) {
		// TODO Auto-generated method stub

	}

	private long getTimeInMillis(DateTime start, DateTime stop) {
		return stop.getMillis() - start.getMillis();
	}

}
