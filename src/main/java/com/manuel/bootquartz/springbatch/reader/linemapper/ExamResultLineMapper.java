package com.manuel.bootquartz.springbatch.reader.linemapper;

import org.joda.time.LocalDate;
import org.springframework.batch.item.file.LineMapper;

import com.manuel.bootquartz.model.ExamResult;

public class ExamResultLineMapper implements LineMapper<ExamResult> {

	@Override
	public ExamResult mapLine(String line, int lineNumber) throws Exception {
		// TODO Auto-generated method stub
		ExamResult result = new ExamResult();
		String[] content = line.split(",");
		result.setStudentName(content[0]);
		result.setPercentage(Double.parseDouble(content[2]));
		String[] date = content[1].split("-");
		LocalDate dob = new LocalDate(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
		result.setDob(dob);
		return result;
	}

}
