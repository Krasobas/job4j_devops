package ru.job4j.devops;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class
 */
@Log4j2
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
		new Thread(
			() -> {
				while (true) {
					log.error("Check");
					log.error("NPE");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
			}
		).start();
	}
}
