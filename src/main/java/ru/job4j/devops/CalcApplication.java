package ru.job4j.devops;

import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class
 */
@SpringBootApplication
public class CalcApplication {
	/**
	 * Default constructor
	 */
	public CalcApplication() {
	}

	/**
	 * Runs the app
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(CalcApplication.class, args);
	}
}
