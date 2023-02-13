package com.portal.datamig;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
public class DatamigApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatamigApplication.class, args);
	}

}
