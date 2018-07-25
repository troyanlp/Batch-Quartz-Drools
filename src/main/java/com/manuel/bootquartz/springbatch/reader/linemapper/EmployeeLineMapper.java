package com.manuel.bootquartz.springbatch.reader.linemapper;

import org.joda.time.LocalDate;
import org.springframework.batch.item.file.LineMapper;

import com.manuel.bootquartz.model.Employee;

public class EmployeeLineMapper implements LineMapper<Employee> {

	@Override
	public Employee mapLine(String line, int lineNumber) throws Exception {
		// TODO Auto-generated method stub
		Employee result = new Employee();
		String[] content = line.split(",");
		result.setName(content[0]);
		result.setLastName(content[1]);
		result.setGender(content[2]);
		String[] date1 = content[3].split("-");

		LocalDate dob = new LocalDate(Integer.parseInt(date1[2]), Integer.parseInt(date1[1]),
				Integer.parseInt(date1[0]));
		result.setDob(dob);

		String[] date2 = content[4].split("-");
		LocalDate startDate = new LocalDate(Integer.parseInt(date2[2]), Integer.parseInt(date2[1]),
				Integer.parseInt(date2[0]));
		result.setStartDate(startDate);

		if (content[5].compareTo("null") == 0) {
			result.setEndDate(null);
		} else {
			String[] date3 = content[5].split("-");
			LocalDate endDate = new LocalDate(Integer.parseInt(date3[2]), Integer.parseInt(date3[1]),
					Integer.parseInt(date3[0]));
			result.setEndDate(endDate);
		}

		result.setPosition(content[6]);
		result.setSalary(Double.parseDouble(content[7]));

		if (content[8].compareTo("true") == 0) {
			result.setRestaurantTicket(true);
		} else {
			result.setRestaurantTicket(false);
		}

		result.setGrowth(content[9].replace("\r", ""));

		return result;
	}

}
