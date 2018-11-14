package eu.vargasoft.temperaturlogger;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.amazonaws.services.iot.client.AWSIotException;

public class ReadSensorQuartzJob extends QuartzJobBean {
	@Autowired
	AwsClient iotClient;

	private String name;

	// Invoked if a Job data map entry with that name
	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		iotClient.init();
		TemperatureSensorShadow device;
		try {
			device = iotClient.createDevice();
		} catch (AWSIotException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
