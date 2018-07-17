package com.manuel.bootquartz.springbatch;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

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
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl(
				"jdbc:mysql://localhost:3306/test?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}

	@Bean
	public JdbcCursorItemReader<ExamResult> databseItemReader(DataSource dataSource) {
		JdbcCursorItemReader<ExamResult> jdbcCursorItemReader = new JdbcCursorItemReader<>();
		jdbcCursorItemReader.setDataSource(dataSource);
		jdbcCursorItemReader.setSql("SELECT STUDENT_NAME, DOB, PERCENTAGE FROM EXAM_RESULT");
		jdbcCursorItemReader.setRowMapper(new ExamResultRowMapper());
		return jdbcCursorItemReader;

	}

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
		line.setDelimiter("/");
		line.setFieldExtractor(beanWrapperFieldExtractor());
		return line;

	}

	@Bean
	public ItemWriter<ExamResult> flatFileItemWriter() {
		FlatFileItemWriter<ExamResult> csvFileWriter = new FlatFileItemWriter<>();
		csvFileWriter.setResource(new FileSystemResource("csv/examResult.txt"));
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
	public Step step1(JdbcCursorItemReader<ExamResult> jdbcCursorItemReader,
			ExamResultItemProcessor examResultItemProcessor, ItemWriter<ExamResult> itemWriter) {
		return stepBuilderFactory.get("step1").<ExamResult, ExamResult>chunk(10).reader(jdbcCursorItemReader)
				.processor(examResultItemProcessor).writer(itemWriter).build();
	}

	@Bean
	public Job examResultJob(Step step, ExamResultJobListener examResultJobListener) {
		return jobBuilderFactory.get("examResultJob").incrementer(new RunIdIncrementer())
				.listener(examResultJobListener).flow(step).end().build();
	}

}
