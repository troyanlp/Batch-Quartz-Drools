package com.everis.bootquartz.springbatch.writer.preparedstatement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import com.everis.bootquartz.model.ExamResult;

public class ExamResultPreparedStatementSetter implements ItemPreparedStatementSetter<ExamResult> {

	@Override
	public void setValues(ExamResult item, PreparedStatement ps) throws SQLException {
		ps.setString(1, item.getStudentName());
		ps.setInt(2, item.getAge());
		ps.setDouble(3, item.getPercentage());

	}

}
