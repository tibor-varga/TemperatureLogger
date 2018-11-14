/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package eu.vargasoft.temperaturlogger;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotDeviceProperty;

import lombok.extern.slf4j.Slf4j;

/**
 * This class encapsulates an actual device. It extends {@link AWSIotDevice} to
 * define properties that are to be kept in sync with the AWS IoT shadow.
 */
@Slf4j
public class TemperatureSensorShadow extends AWSIotDevice {
	SensorManager sensorManager;

	@Autowired
	private IotConfig config;

	@AWSIotDeviceProperty
	private float temperature;

	@AWSIotDeviceProperty
	private String sensorId;

	@AWSIotDeviceProperty
	private long timestamp;

	public TemperatureSensorShadow(String thingName, String sensorId, SensorManager sensorManager) {
		super(thingName);
		this.sensorId = sensorId;
		this.sensorManager = sensorManager;
	}

	@PostConstruct
	private void init() {
		long sampleRate = Long.parseLong(config.getProperty("sampleRate"));
		log.info("Setting sampleRate to:" + sampleRate);
		this.setReportInterval(sampleRate);
	}

	public float getTemperature() {
		try {
			Thread.sleep(this.getReportInterval());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<InfoRecord> datas = sensorManager.readData();
		if (datas != null) {
			for (InfoRecord data : datas) {

				if (data.sensorId.equals(this.sensorId)) {
					log.info(data.toString());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					this.temperature = data.getValue();
					// this.sensorId = data.getSensorId();
					this.timestamp = data.getTimestamp();
				} else {
					log.warn("dropping record:" + data.toString());
				}
			}
		}

		return this.temperature;
	}

	public String getSensorId() {
		return sensorId;
	}

	public long getTimestamp() {
		return timestamp;
	}

}
