package com.manuel.bootquartz.model;

import org.joda.time.LocalDate;

public class Employee {
	private String name;
	private String lastName;
	private String gender;
	private LocalDate dob;
	private LocalDate startDate;
	private LocalDate endDate;
	private String position;
	private double salary;
	private boolean restaurantTicket;
	private String growth; // horizontal or vertical

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public boolean isRestaurantTicket() {
		return restaurantTicket;
	}

	public void setRestaurantTicket(boolean restaurantTicket) {
		this.restaurantTicket = restaurantTicket;
	}

	public String getGrowth() {
		return growth;
	}

	public void setGrowth(String growth) {
		this.growth = growth;
	}

}
