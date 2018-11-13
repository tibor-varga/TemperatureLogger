/**
 * 
 */
package eu.vargasoft.temperaturlogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * @author buxi
 *
 */
@Configuration
@PropertySource("classpath:aws-iot-sdk-samples.properties")
public class IotConfig {
	@Autowired
	private Environment env;

	public String getProperty(String name) {
		return env.getProperty(name);
	}
}
