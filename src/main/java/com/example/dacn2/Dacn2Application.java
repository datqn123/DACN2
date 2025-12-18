package com.example.dacn2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Dacn2Application {

	public static void main(String[] args) {
		SpringApplication.run(Dacn2Application.class, args);
	}

}
