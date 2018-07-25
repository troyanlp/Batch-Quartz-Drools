package com.manuel.bootquartz.springbatch.employee;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.joda.time.DateTimeZone;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import com.manuel.bootquartz.model.Employee;

public class EmployeePreparedStatementSetter implements ItemPreparedStatementSetter<Employee> {

	public static final DateTimeZone jodaTzUTC = DateTimeZone.forID("UTC");

	@Override
	public void setValues(Employee item, PreparedStatement ps) throws SQLException {
		ps.setString(1, item.getName());
		ps.setString(2, item.getLastName());
		ps.setString(3, item.getGender());

		ps.setDate(4, new java.sql.Date(item.getDob().toDateTimeAtStartOfDay(jodaTzUTC).getMillis()));
		ps.setDate(5, new java.sql.Date(item.getStartDate().toDateTimeAtStartOfDay(jodaTzUTC).getMillis()));
		if (item.getEndDate() == null) {
			ps.setNull(6, java.sql.Types.NULL);
		} else {
			ps.setDate(6, new java.sql.Date(item.getEndDate().toDateTimeAtStartOfDay(jodaTzUTC).getMillis()));
		}

		ps.setString(7, item.getPosition());
		ps.setDouble(8, item.getSalary());
		ps.setBoolean(9, item.isRestaurantTicket());
		ps.setString(10, item.getGrowth());
	}

}
