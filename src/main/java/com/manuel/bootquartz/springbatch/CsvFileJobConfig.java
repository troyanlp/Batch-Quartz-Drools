package com.manuel.bootquartz.springbatch;

//@Configuration
public class CsvFileJobConfig {

	// @Bean
	// ItemReader<ExamResult> csvFileItemReader() {
	// FlatFileItemReader<ExamResult> csvFileReader = new FlatFileItemReader<>();
	// csvFileReader.setResource(new ClassPathResource("csv/examResult.txt"));
	// csvFileReader.setLinesToSkip(1);
	//
	// LineMapper<ExamResult> studentLineMapper = createStudentLineMapper();
	// csvFileReader.setLineMapper(studentLineMapper);
	//
	// return csvFileReader;
	// }
	//
	// private LineMapper<ExamResult> createStudentLineMapper() {
	// DefaultLineMapper<ExamResult> studentLineMapper = new DefaultLineMapper<>();
	//
	// LineTokenizer studentLineTokenizer = createStudentLineTokenizer();
	// studentLineMapper.setLineTokenizer(studentLineTokenizer);
	//
	// FieldSetMapper<ExamResult> studentInformationMapper =
	// createStudentInformationMapper();
	// studentLineMapper.setFieldSetMapper(studentInformationMapper);
	//
	// return studentLineMapper;
	// }
	//
	// private LineTokenizer createStudentLineTokenizer() {
	// DelimitedLineTokenizer studentLineTokenizer = new DelimitedLineTokenizer();
	// studentLineTokenizer.setDelimiter(";");
	// studentLineTokenizer.setNames(new String[] { "studentName", "percentage",
	// "dob" });
	// return studentLineTokenizer;
	// }
	//
	// private FieldSetMapper<ExamResult> createStudentInformationMapper() {
	// BeanWrapperFieldSetMapper<ExamResult> studentInformationMapper = new
	// BeanWrapperFieldSetMapper<>();
	// studentInformationMapper.setTargetType(ExamResult.class);
	// return studentInformationMapper;
	// }

}
