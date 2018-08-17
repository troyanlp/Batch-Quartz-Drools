package com.everis.bootquartz.springbatch.reader;

import org.joda.time.LocalDate;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import com.everis.bootquartz.model.ExamResult;

public class ExamResultMapper implements FieldSetMapper<ExamResult> {

	@Override
	public ExamResult mapFieldSet(FieldSet fieldSet) throws BindException {
		ExamResult result = new ExamResult();
		result.setStudentName(fieldSet.readString("studentName"));
		result.setPercentage(fieldSet.readDouble("percentage"));
		String content = fieldSet.readString("dob");
		String[] date = content.split("-");
		LocalDate dob = new LocalDate(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));
		result.setDob(dob);
		return result;
	}

}
