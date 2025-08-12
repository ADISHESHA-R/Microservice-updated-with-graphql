package com.quiz.quiz_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class QuizServiceApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(QuizServiceApplication.class, args);
		} catch (Exception e) {
			System.err.println("Application failed to start: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
