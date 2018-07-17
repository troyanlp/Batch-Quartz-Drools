package com.manuel.bootquartz.springbatch;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.time.LocalDate;
import org.springframework.jdbc.core.RowMapper;

import com.manuel.bootquartz.model.ExamResult;

public class ExamResultRowMapper implements RowMapper<ExamResult> {

	@Override
	public ExamResult mapRow(ResultSet rs, int rowNum) throws SQLException {

		ExamResult result = new ExamResult();
		result.setStudentName(rs.getString("student_name"));
		// result.setDob(new LocalDate(rs.getDate("dob")));
		result.setDob(new LocalDate(rs.getString("dob")));
		result.setPercentage(rs.getDouble("percentage"));

		return result;
	}

}