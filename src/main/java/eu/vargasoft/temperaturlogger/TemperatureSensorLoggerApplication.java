package eu.vargasoft.temperaturlogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TemperatureSensorLoggerApplication {
	@Autowired
	TemperatureSensorShadow device;

	public static void main(String[] args) {
		SpringApplication.run(TemperatureSensorLoggerApplication.class, args);
	}
}
