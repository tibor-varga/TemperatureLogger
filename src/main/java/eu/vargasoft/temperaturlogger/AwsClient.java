/**
 * 
 */
package eu.vargasoft.temperaturlogger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

import lombok.extern.slf4j.Slf4j;

/**
 * @author buxi
 *
 */
@Component
@Slf4j
public class AwsClient {
	@Autowired
	private IotConfig config;

	@Autowired
	private SensorManager sensorManager;

	private AWSIotMqttClient awsIotClient = null;

	@PostConstruct
	void init() throws AWSIotException, AWSIotTimeoutException {
		String clientEndpoint = config.getProperty("clientEndpoint");
		String clientId = config.getProperty("clientId");

		String certificateFile = config.getProperty("certificateFile");
		String privateKeyFile = config.getProperty("privateKeyFile");
		if (awsIotClient == null && certificateFile != null && privateKeyFile != null) {
			log.debug("Creating client with certificate");
			String algorithm = config.getProperty("keyAlgorithm");
			KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile, algorithm);

			awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);
		}

		if (awsIotClient == null) {
			log.debug("Creating client with API key");
			String awsAccessKeyId = config.getProperty("awsAccessKeyId");
			String awsSecretAccessKey = config.getProperty("awsSecretAccessKey");
			String sessionToken = config.getProperty("sessionToken");

			if (awsAccessKeyId != null && awsSecretAccessKey != null) {
				awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, awsAccessKeyId, awsSecretAccessKey,
						sessionToken);
			}
		}

		if (awsIotClient == null) {
			throw new IllegalArgumentException("Failed to construct client due to missing certificate or credentials.");
		}

		awsIotClient.setWillMessage(new AWSIotMessage("client/disconnect", AWSIotQos.QOS0, awsIotClient.getClientId()));
		String thingName = config.getProperty("thingName");
		String sensorId = config.getProperty("sensorId");
		TemperatureSensorShadow sensor = new TemperatureSensorShadow(thingName, sensorId, sensorManager);
		awsIotClient.attach(sensor);

		String thingName2 = config.getProperty("thingName2");
		String sensorId2 = config.getProperty("sensorId2");
		TemperatureSensorShadow sensor2 = new TemperatureSensorShadow(thingName2, sensorId2, sensorManager);
		awsIotClient.attach(sensor2);

		awsIotClient.connect();
		sensor.delete(1000);
		sensor2.delete(1000);
	}
}