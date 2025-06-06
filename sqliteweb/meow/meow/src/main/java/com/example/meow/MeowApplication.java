package com.example.meow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MeowApplication {
	//http://localhost:8080/
	public static void main(String[] args) {
		DatabaseInitializer.initialize();
		SpringApplication.run(MeowApplication.class, args);
	}

}
