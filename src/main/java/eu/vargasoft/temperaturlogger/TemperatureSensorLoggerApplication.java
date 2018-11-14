package eu.vargasoft.temperaturlogger;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TemperatureSensorLoggerApplication {
	public static void main(String[] args) {
		SpringApplication.run(TemperatureSensorLoggerApplication.class, args);
	}

	@Bean
	public JobDetail sampleJobDetail() {

		return JobBuilder.newJob(ReadSensorQuartzJob.class).withIdentity("readSensorJob").usingJobData("name", "World")
				.storeDurably().build();
	}

	@Bean
	public Trigger sampleJobTrigger() {
		SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1)
		// .repeatForever()
		;

		return TriggerBuilder.newTrigger().forJob(sampleJobDetail()).withIdentity("readSensorTrigger")
				.withSchedule(scheduleBuilder).build();
	}

}
