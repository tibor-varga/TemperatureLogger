package eu.vargasoft.temperaturlogger;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadSensorQuartzJob extends QuartzJobBean {
	@Autowired
	SensorManager sensorManager;

	private String name;

	// Invoked if a Job data map entry with that name
	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		List<InfoRecord> datas = sensorManager.readData();
		if (datas != null) {
			for (InfoRecord data : datas) {
				log.info(data.toString());
			}
		}
	}

}
