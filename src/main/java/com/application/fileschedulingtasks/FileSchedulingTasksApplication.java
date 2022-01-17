package com.application.fileschedulingtasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FileSchedulingTasksApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileSchedulingTasksApplication.class, args);
	}

}
