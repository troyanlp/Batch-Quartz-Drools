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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import com.everis.bootquartz.model.ExamResult;
import com.everis.bootquartz.service.DroolsService;
import com.everis.bootquartz.springbatch.decider.MyDecider;
import com.everis.bootquartz.springbatch.listener.ExamResultJobListener;
import com.everis.bootquartz.springbatch.processor.ExamResultItemProcessor;
import com.everis.bootquartz.springbatch.processor.ItemCatchProcessor;
import com.everis.bootquartz.springbatch.reader.ExamResultItemReader;
import com.everis.bootquartz.springbatch.reader.ExamResultMapper;
import com.everis.bootquartz.springbatch.reader.NoOpItemReader;
import com.everis.bootquartz.springbatch.writer.preparedstatement.ExamResultPreparedStatementSetter;

@Configuration
@EnableBatchProcessing
public class Config {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	private Environment env;

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl(
				"jdbc:mysql://localhost:3306/test?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}

	// READER

	@Bean
	public ExamResultItemReader examResultItemReader() {
		return new ExamResultItemReader();
	}

	// @Bean
	// public ExamResultLineMapper examResultLineMapper() {
	// return new ExamResultLineMapper();
	// }

	@Bean
	public ExamResultMapper examResultMapper() {
		return new ExamResultMapper();
	}

	@Bean
	ItemReader<ExamResult> csvFileItemReader() {
		FlatFileItemReader<ExamResult> csvFileReader = new FlatFileItemReader<>();
		csvFileReader.setResource(new ClassPathResource("csv/examResult.txt"));
		csvFileReader.setLinesToSkip(0);

		LineMapper<ExamResult> studentLineMapper = createStudentLineMapper();
		csvFileReader.setLineMapper(studentLineMapper);

		return csvFileReader;
	}

	private LineMapper<ExamResult> createStudentLineMapper() {
		DefaultLineMapper<ExamResult> studentLineMapper = new DefaultLineMapper<>();

		LineTokenizer studentLineTokenizer = createStudentLineTokenizer();
		studentLineMapper.setLineTokenizer(studentLineTokenizer);

		FieldSetMapper<ExamResult> studentInformationMapper = createStudentInformationMapper();
		studentLineMapper.setFieldSetMapper(examResultMapper());

		return studentLineMapper;
	}

	private LineTokenizer createStudentLineTokenizer() {
		DelimitedLineTokenizer studentLineTokenizer = new DelimitedLineTokenizer();
		studentLineTokenizer.setDelimiter(",");
		studentLineTokenizer.setNames(new String[] { "studentName", "dob", "percentage" });
		return studentLineTokenizer;
	}

	private FieldSetMapper<ExamResult> createStudentInformationMapper() {
		BeanWrapperFieldSetMapper<ExamResult> studentInformationMapper = new BeanWrapperFieldSetMapper<>();
		studentInformationMapper.setTargetType(ExamResult.class);
		// studentInformationMapper.setCustomEditors(customEditors);
		return studentInformationMapper;
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
	public DroolsService droolsService() {
		return new DroolsService();
	}

	@Bean
	public ExamResultItemProcessor examResultItemProcessor() {
		return new ExamResultItemProcessor();
	}

	@Bean
	public NoOpItemReader noOpItemReader() {
		return new NoOpItemReader();
	}

	@Bean
	public ItemCatchProcessor itemCatchProcessor() {
		return new ItemCatchProcessor();
	}

	@Bean
	public Step compositeStep() {
		return stepBuilderFactory.get("stepComposite").allowStartIfComplete(true).<ExamResult, ExamResult>chunk(10)
				.reader(csvFileItemReader()).processor(examResultItemProcessor()).writer(compositeItemWriter()).build();
	}

	@Bean
	public Step initialStep() {
		return stepBuilderFactory.get("Initial Step").allowStartIfComplete(true).<ExamResult, ExamResult>chunk(1)
				.reader(csvFileItemReader()).processor(examResultItemProcessor()).listener(promotionListener()).build();
	}

	@Bean
	public ExecutionContextPromotionListener promotionListener() {
		ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();

		listener.setKeys(new String[] { "results" });

		return listener;
	}

	@Bean
	public Step cvsStep() {
		return stepBuilderFactory.get("CVSStep").allowStartIfComplete(true).<ExamResult, ExamResult>chunk(1)
				.reader(noOpItemReader()).processor(itemCatchProcessor()).writer(flatFileItemWriter()).build();
	}

	@Bean
	public Step xmlStep() {
		return stepBuilderFactory.get("XMLStep").allowStartIfComplete(true).<ExamResult, ExamResult>chunk(1)
				.reader(noOpItemReader()).processor(itemCatchProcessor()).writer(userUnmarshaller()).build();
	}

	@Bean
	public Step databaseStep() {
		return stepBuilderFactory.get("DBStep").allowStartIfComplete(true).<ExamResult, ExamResult>chunk(1)
				.reader(noOpItemReader()).processor(itemCatchProcessor())
				.writer(DatabaseItemWriter(dataSource(), jdbcTemplate)).build();
	}

	// @Bean(name = "singleStepJob")
	// public Job singleStepJob() {
	// return jobBuilderFactory.get("singleStepJob").incrementer(new
	// RunIdIncrementer())
	// .listener(examResultJobListener()).start(compositeStep()).build();
	// }

	@Bean(name = "deciderJob")
	public Job deciderJob() {
		MyDecider decider = new MyDecider();
		return jobBuilderFactory.get("deciderJob").incrementer(new RunIdIncrementer()).listener(examResultJobListener())
				.start(initialStep()).next(decider).on("XML").to(xmlStep()).next(decider).on("DB").to(databaseStep())
				.next(decider).on("CSV").to(cvsStep()).next(decider).on("END").end().build().build();
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
