package com.everis.bootquartz.springbatch.reader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.everis.bootquartz.model.Employee;
import com.everis.bootquartz.springbatch.reader.linemapper.EmployeeLineMapper;

public class EmployeeItemReader implements ItemReader<Employee> {

	boolean loaded = false;
	int lineNumber = 0;
	ArrayList<String> fileContent;

	@Override
	public Employee read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (!loaded) {
			setUp();
		}
		if (!fileContent.isEmpty()) {
			EmployeeLineMapper lineMapper = new EmployeeLineMapper();
			Employee employee = lineMapper.mapLine(fileContent.get(0), lineNumber);
			lineNumber++;
			fileContent.remove(0);
			return employee;
		} else {
			fileContent.clear();
			lineNumber = 0;
			loaded = false;
			return null;
		}
	}

	public void setUp() throws IOException {
		String fileName = "csv/employees2.txt";
		ClassLoader classLoader = new EmployeeItemReader().getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());

		if (file.exists()) {
			// Read File Content
			String content = new String(Files.readAllBytes(file.toPath()));
			fileContent = new ArrayList<String>(Arrays.asList(content.split("\n")));

			loaded = true;
		}
	}
}
