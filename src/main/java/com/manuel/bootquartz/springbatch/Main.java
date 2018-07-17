package com.manuel.bootquartz.springbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@ImportResource("classpath:spring-batch-context.xml")
@SpringBootApplication
// @EnableBatchProcessing
public class Main {

	@SuppressWarnings("resource")
	public static void main(String args[]) {
		SpringApplication.run(Main.class, args);
	}

}