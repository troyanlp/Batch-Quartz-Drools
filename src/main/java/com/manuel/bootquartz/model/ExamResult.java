package com.manuel.bootquartz.model;

import org.joda.time.LocalDate;

public class ExamResult {

	private String studentName;
	private LocalDate dob;
	private double percentage;
	private boolean passed = false;
	private int age = 0;

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	@Override
	public String toString() {
		return "ExamResult [studentName=" + studentName + ", dob=" + dob + ", percentage=" + percentage + "]";
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public void calculateAge() {
		LocalDate now = new LocalDate().now();
		age = now.getYear() - dob.getYear();
		if ((now.getMonthOfYear() <= dob.getMonthOfYear()) && (now.getDayOfMonth() <= dob.getDayOfMonth()))
			age--;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}