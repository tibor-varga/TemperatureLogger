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

	@AWSIotDeviceProperty
	private float temperature;

	@AWSIotDeviceProperty
	private String sensorId;

	@AWSIotDeviceProperty
	private long timestamp;

	public TemperatureSensorShadow(String thingName, SensorManager sensorManager) {
		super(thingName);
		this.sensorManager = sensorManager;
	}

	public float getTemperature() {
		List<InfoRecord> datas = sensorManager.readData();
		if (datas != null) {
			for (InfoRecord data : datas) {
				log.info(data.toString());
				this.temperature = data.getValue();
				this.sensorId = data.getSensorId();
				this.timestamp = data.getTimestamp();
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
