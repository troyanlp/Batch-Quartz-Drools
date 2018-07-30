package com.everis.bootquartz.springbatch.config;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import com.everis.bootquartz.model.Employee;
import com.everis.bootquartz.model.ExamResult;
import com.everis.bootquartz.service.DroolsService;
import com.everis.bootquartz.springbatch.decider.MyDecider;
import com.everis.bootquartz.springbatch.listener.EmployeeItemProcessorListener;
import com.everis.bootquartz.springbatch.listener.EmployeeItemReaderListener;
import com.everis.bootquartz.springbatch.listener.EmployeeItemWriterListener;
import com.everis.bootquartz.springbatch.listener.EmployeeJobListener;
import com.everis.bootquartz.springbatch.listener.ExamResultJobListener;
import com.everis.bootquartz.springbatch.processor.EmployeeItemProcessor;
import com.everis.bootquartz.springbatch.processor.ExamResultItemProcessor;
import com.everis.bootquartz.springbatch.processor.ItemCatchProcessor;
import com.everis.bootquartz.springbatch.reader.EmployeeItemReader;
import com.everis.bootquartz.springbatch.reader.ExamResultItemReader;
import com.everis.bootquartz.springbatch.reader.NoOpItemReader;
import com.everis.bootquartz.springbatch.writer.preparedstatement.EmployeePreparedStatementSetter;
import com.everis.bootquartz.springbatch.writer.preparedstatement.ExamResultPreparedStatementSetter;

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

	@Bean
	public EmployeeItemReader EmployeeItemReader() {
		return new EmployeeItemReader();
	}

	// END READER

	// START WRITTER DB

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource());
	}

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

	@Bean
	ItemWriter<Employee> DatabaseEmployeeWriter(DataSource dataSource, NamedParameterJdbcTemplate jdbcTemplate) {
		JdbcBatchItemWriter<Employee> databaseItemWriter = new JdbcBatchItemWriter<>();
		databaseItemWriter.setDataSource(dataSource);
		databaseItemWriter.setJdbcTemplate(jdbcTemplate);

		databaseItemWriter.setSql(
				"INSERT INTO employee(name, lastName, gender, dob, startDate, endDate, position, salary, restaurantTicket, growth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		ItemPreparedStatementSetter<Employee> valueSetter = new EmployeePreparedStatementSetter();
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
		marshaller.setClassesToBeBound(new Class[] { ExamResult.class });
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
	public CompositeItemWriter compositeItemWriter() {
		List<ItemWriter> writers = new ArrayList<>(3);
		writers.add(flatFileItemWriter());
		writers.add(userUnmarshaller());
		writers.add(DatabaseItemWriter(dataSource(), jdbcTemplate));

		CompositeItemWriter itemWriter = new CompositeItemWriter();

		itemWriter.setDelegates(writers);

		return itemWriter;
	}

	@Bean
	public ExamResultJobListener examResultJobListener() {
		return new ExamResultJobListener();
	}

	@Bean
	public EmployeeJobListener employeeJobListener() {
		return new EmployeeJobListener();
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
	public EmployeeItemProcessor employeeItemProcessor() {
		return new EmployeeItemProcessor();
	}

	@Bean
	public NoOpItemReader noOpItemReader() {
		return new NoOpItemReader();
	}

	@Bean
	public ItemCatchProcessor itemCatchProcessor() {
		return new ItemCatchProcessor();
	}

	// @Bean
	// public Step step1(ExamResultItemReader examResultItemReader,
	// ExamResultItemProcessor examResultItemProcessor,
	// ItemWriter<ExamResult> databaseItemWriter) {
	// return
	// stepBuilderFactory.get("step1").allowStartIfComplete(true).<ExamResult,
	// ExamResult>chunk(10)
	// .reader(examResultItemReader).processor(examResultItemProcessor).writer(databaseItemWriter).build();
	// }

	// @Bean
	// public Step step1() {
	// return
	// stepBuilderFactory.get("step1").allowStartIfComplete(true).<ExamResult,
	// ExamResult>chunk(10)
	// .reader(examResultItemReader()).processor(examResultItemProcessor()).writer(compositeItemWriter())
	// .build();
	// }

	@Bean
	public Step stepEmployee() {
		return stepBuilderFactory.get("stepEmployee").allowStartIfComplete(true).<Employee, Employee>chunk(10)
				.reader(EmployeeItemReader()).listener(employeeItemReaderListener()).processor(employeeItemProcessor())
				.listener(employeeItemProcessorListener()).writer(DatabaseEmployeeWriter(dataSource(), jdbcTemplate))
				.listener(employeeItemWriterListener()).build();
	}

	@Bean
	public Job employeeJob() {
		return jobBuilderFactory.get("employeeJob").incrementer(new RunIdIncrementer()).listener(employeeJobListener())
				.start(stepEmployee()).build();
	}

	@Bean
	public EmployeeItemReaderListener employeeItemReaderListener() {
		return new EmployeeItemReaderListener();
	}

	@Bean
	public EmployeeItemProcessorListener employeeItemProcessorListener() {
		return new EmployeeItemProcessorListener();
	}

	@Bean
	public EmployeeItemWriterListener employeeItemWriterListener() {
		return new EmployeeItemWriterListener();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("Initial Step").allowStartIfComplete(true).<ExamResult, ExamResult>chunk(10)
				.reader(examResultItemReader()).processor(examResultItemProcessor()).listener(promotionListener())
				.build();
	}

	@Bean
	public ExecutionContextPromotionListener promotionListener() {
		ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();

		listener.setKeys(new String[] { "results" });

		return listener;
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("XMLStep").allowStartIfComplete(true).<ExamResult, ExamResult>chunk(10)
				.reader(noOpItemReader()).processor(itemCatchProcessor()).writer(userUnmarshaller()).build();
	}

	//
	@Bean
	public Step step3() {
		return stepBuilderFactory.get("DBStep").allowStartIfComplete(true).<ExamResult, ExamResult>chunk(10)
				.reader(noOpItemReader()).processor(itemCatchProcessor())
				.writer(DatabaseItemWriter(dataSource(), jdbcTemplate)).build();
	}

	// @Bean
	// public Job examResultJob(Step step, ExamResultJobListener
	// examResultJobListener) {
	// return jobBuilderFactory.get("examResultJob").incrementer(new
	// RunIdIncrementer())
	// .listener(examResultJobListener).flow(step).end().build();
	// }

	// @Bean
	// public Job examResultJob() {
	// Flow flow1 = new FlowBuilder<Flow>("Flow1").start(step1()).next(decider()) //
	// Note 1
	// .on("XML").to(step3()).from(decider()) // Note 1
	// .on("DB").to(step2()).build();
	// // return jobBuilderFactory.get("examResultJob").incrementer(new
	// // RunIdIncrementer()).start(step1()).next(decider())
	// // .on("DB").to(step2()).next(decider()).on("*").to(step3()).end().build();
	// return jobBuilderFactory.get("examResultJob").incrementer(new
	// RunIdIncrementer()).start(flow1).build().build();
	// // return jobBuilderFactory.get("examResultJob").incrementer(new
	// // RunIdIncrementer());
	// // .listener(examResultJobListener()).start(step1()).next(step2()).build();
	// }

	@Autowired
	public MyDecider decider() {
		return new MyDecider();
	}

	@Autowired
	SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

	@Bean
	public SimpleAsyncTaskExecutor SimpleAsyncTaskExecutor() {
		SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
		return simpleAsyncTaskExecutor;
	}

	@Bean
	public JobRepository jobRepositoryFactoryBean() throws Exception {
		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
		jobRepositoryFactoryBean.setDatabaseType("MYSQL");
		jobRepositoryFactoryBean.setDataSource(dataSource());
		jobRepositoryFactoryBean.setTransactionManager(transactionManager());
		return jobRepositoryFactoryBean.getObject();
	}

	@Bean
	public SimpleJobLauncher simpleJobLauncher() throws Exception {
		SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
		simpleJobLauncher.setJobRepository(jobRepositoryFactoryBean());
		simpleJobLauncher.setTaskExecutor(simpleAsyncTaskExecutor);
		return simpleJobLauncher;
	}

}
