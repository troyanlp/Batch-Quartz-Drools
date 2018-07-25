package com.manuel.bootquartz.springbatch.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.manuel.bootquartz.model.ExamResult;
import com.manuel.bootquartz.springbatch.reader.linemapper.ExamResultLineMapper;

public class ExamResultItemReader implements ItemReader<ExamResult> {

	boolean loaded = false;
	int lineNumber = 0;
	ArrayList<String> fileContent;

	@Override
	public ExamResult read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (!loaded) {
			setUp();
		}
		if (!fileContent.isEmpty()) {
			ExamResultLineMapper lineMapper = new ExamResultLineMapper();
			ExamResult exam = lineMapper.mapLine(fileContent.get(0), lineNumber);
			System.out.println(fileContent.get(0));
			lineNumber++;
			fileContent.remove(0);
			return exam;
		} else {
			fileContent.clear();
			lineNumber = 0;
			loaded = false;
			return null;
		}
	}

	public void setUp() throws IOException {
		String fileName = "csv/examResult.txt";
		ClassLoader classLoader = new ExamResultItemReader().getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		// File is found
		System.out.println("File Found : " + file.exists());

		// Read File Content
		String content = new String(Files.readAllBytes(file.toPath()));
		fileContent = new ArrayList<String>(Arrays.asList(content.split("\n")));

		loaded = true;
	}

}
