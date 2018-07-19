package com.manuel.bootquartz.springbatch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.manuel.bootquartz.model.ExamResult;
import com.manuel.bootquartz.service.DroolsService;

@Configuration
@EnableBatchProcessing
public class Config {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	// @Autowired
	// public ResourcelessTransactionManager resourcelessTransactionManager;

	@Autowired
	private Environment env;

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl(
				"jdbc:mysql://localhost:3306/test?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}

	// @Bean
	// public JdbcCursorItemReader<ExamResult> databseItemReader(DataSource
	// dataSource) {
	// JdbcCursorItemReader<ExamResult> jdbcCursorItemReader = new
	// JdbcCursorItemReader<>();
	// jdbcCursorItemReader.setDataSource(dataSource);
	// jdbcCursorItemReader.setSql("SELECT STUDENT_NAME, DOB, PERCENTAGE FROM
	// EXAM_RESULT");
	// jdbcCursorItemReader.setRowMapper(new ExamResultRowMapper());
	// return jdbcCursorItemReader;
	//
	// }

	// READER

	@Bean
	public ExamResultItemReader examResultItemReader() {
		return new ExamResultItemReader();
	}

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
	// studentLineTokenizer.setDelimiter(",");
	// studentLineTokenizer.setNames(new String[] { "studentName", "dob",
	// "percentage" });
	// return studentLineTokenizer;
	// }
	//
	// private FieldSetMapper<ExamResult> createStudentInformationMapper() {
	// BeanWrapperFieldSetMapper<ExamResult> studentInformationMapper = new
	// BeanWrapperFieldSetMapper<>();
	// studentInformationMapper.setTargetType(ExamResult.class);
	// return studentInformationMapper;
	// }

	// END READER

	// START WRITTER DB

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Bean
	ItemWriter<ExamResult> DatabaseItemWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
		JdbcBatchItemWriter<ExamResult> databaseItemWriter = new JdbcBatchItemWriter<>();
		databaseItemWriter.setDataSource(dataSource);
		databaseItemWriter.setJdbcTemplate(jdbcTemplate);

		databaseItemWriter.setSql("INSERT INTO exam_result(student_name, age, percentage) VALUES (?, ?, ?)");

		ItemPreparedStatementSetter<ExamResult> valueSetter = new ExamResultPreparedStatementSetter();
		databaseItemWriter.setItemPreparedStatementSetter(valueSetter);

		return databaseItemWriter;
	}

	// END WRITTER DB

	// START WRITTER XML

	@Bean
	public Jaxb2Marshaller empMarshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		// Class[] list = new ArrayList<Class>();
		// list.add(com.manuel.bootquartz.model.ExamResult.class);
		marshaller.setClassesToBeBound(new Class[] { com.manuel.bootquartz.model.ExamResult.class });
		return marshaller;
	}

	@Bean
	public StaxEventItemWriter<ExamResult> userUnmarshaller() {
		StaxEventItemWriter<ExamResult> xml = new StaxEventItemWriter<ExamResult>();
		xml.setResource(new FileSystemResource("output/examResult.xml"));
		xml.setMarshaller(empMarshaller());
		xml.setRootTagName("ExamResult");
		return xml;
	}

	// END WRITTER XML

	@Bean
	public BeanWrapperFieldExtractor beanWrapperFieldExtractor() {
		BeanWrapperFieldExtractor<ExamResult> field = new BeanWrapperFieldExtractor();
		String[] names = { "studentName", "percentage", "dob" };
		field.setNames(names);
		return field;
	}

	@Bean
	public DelimitedLineAggregator delimitedLineAgregator() {
		DelimitedLineAggregator line = new DelimitedLineAggregator();
		line.setDelimiter(",");
		line.setFieldExtractor(beanWrapperFieldExtractor());
		return line;

	}

	@Bean
	public ItemWriter<ExamResult> flatFileItemWriter() {
		FlatFileItemWriter<ExamResult> csvFileWriter = new FlatFileItemWriter<>();
		csvFileWriter.setResource(new FileSystemResource("output/examResult.txt"));
		csvFileWriter.setLineAggregator(delimitedLineAgregator());
		return csvFileWriter;
	}

	@Bean
	public ExamResultJobListener examResultJobListener() {
		return new ExamResultJobListener();
	}

	@Bean
	public DroolsService droolsService() {
		return new DroolsService();
	}

	@Bean
	public ExamResultItemProcessor examResultItemProcessor() {
		return new ExamResultItemProcessor();
	}

	@Bean
	public Step step1(ExamResultItemReader examResultItemReader, ExamResultItemProcessor examResultItemProcessor,
			StaxEventItemWriter<ExamResult> userUnmarshaller) {
		return stepBuilderFactory.get("step1").<ExamResult, ExamResult>chunk(10).reader(examResultItemReader)
				.processor(examResultItemProcessor).writer(userUnmarshaller).build();
	}

	@Bean
	public Job examResultJob(Step step, ExamResultJobListener examResultJobListener) {
		return jobBuilderFactory.get("examResultJob").incrementer(new RunIdIncrementer())
				.listener(examResultJobListener).flow(step).end().build();
	}

}
