package com.everis.bootquartz.springbatch.listener;

import org.joda.time.DateTime;
import org.springframework.batch.core.ItemProcessListener;

import com.everis.bootquartz.model.Employee;

public class EmployeeItemProcessorListener implements ItemProcessListener<Employee, Employee> {

	private DateTime startTime, stopTime;

	@Override
	public void beforeProcess(Employee item) {
		startTime = new DateTime();

	}

	@Override
	public void afterProcess(Employee item, Employee result) {
		stopTime = new DateTime();
		System.out.println("PROCESSING TIME: " + getTimeInMillis(startTime, stopTime));

	}

	@Override
	public void onProcessError(Employee item, Exception e) {
		// TODO Auto-generated method stub

	}

	private long getTimeInMillis(DateTime start, DateTime stop) {
		return stop.getMillis() - start.getMillis();
	}

}
