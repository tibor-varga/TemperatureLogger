/**
 * 
 */
package eu.vargasoft.temperaturlogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil;
import com.amazonaws.services.iot.client.sample.sampleUtil.SampleUtil.KeyStorePasswordPair;

import eu.vargasoft.temperaturlogger.awsiot.ConnectedWindow;
import lombok.extern.slf4j.Slf4j;

/**
 * @author buxi
 *
 */
@Component
@Slf4j
public class AwsIotDevice {
	@Autowired
	private IotConfig config;

	private AWSIotMqttClient awsIotClient = null;

	void init() {
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
	}

	public void sendData(InfoRecord infoRecord) throws AWSIotException, InterruptedException {
		awsIotClient.setWillMessage(new AWSIotMessage("client/disconnect", AWSIotQos.QOS0, awsIotClient.getClientId()));

		String thingName = config.getProperty("thingName");
		ConnectedWindow connectedWindow = new ConnectedWindow(thingName);

		awsIotClient.attach(connectedWindow);
		awsIotClient.connect();

		// Delete existing document if any
		connectedWindow.delete();

		AWSIotConnectionStatus status = AWSIotConnectionStatus.DISCONNECTED;
		while (true) {
			String jsonDocument = connectedWindow.get();
			System.out.println("json from Device:" + jsonDocument);
			AWSIotConnectionStatus newStatus = awsIotClient.getConnectionStatus();
			if (!status.equals(newStatus)) {
				System.out.println(System.currentTimeMillis() + " Connection status changed to " + newStatus);
				status = newStatus;
			}

			Thread.sleep(1000);
		}
	}
}