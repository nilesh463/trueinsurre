package com.trueinsurre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TrueinsurreApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrueinsurreApplication.class, args);
	}

}
