/**
 * 
 */
package eu.vargasoft.temperaturlogger;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;

import lombok.extern.slf4j.Slf4j;

/**
 * @author buxi
 *
 */

@Slf4j
public class KinesisProducer {
	public static void main(String[] args) {
		SpringApplication.run(KinesisProducer.class, args);

		log.info("env:" + System.getenv().toString());
		log.info("props:" + System.getProperties().toString());

		AmazonKinesisClientBuilder clientBuilder = AmazonKinesisClientBuilder.standard();
		AmazonKinesis kinesisClient = clientBuilder.build();

		PutRecordsRequest putRecordsRequest = new PutRecordsRequest();
		putRecordsRequest.setStreamName("TemperatureMonitorStream");
		List<PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			PutRecordsRequestEntry putRecordsRequestEntry = new PutRecordsRequestEntry();
			putRecordsRequestEntry.setData(ByteBuffer.wrap(String.valueOf(i).getBytes()));
			putRecordsRequestEntry.setPartitionKey(String.format("partitionKey-%d", i));
			putRecordsRequestEntryList.add(putRecordsRequestEntry);
		}

		putRecordsRequest.setRecords(putRecordsRequestEntryList);
		PutRecordsResult putRecordsResult = kinesisClient.putRecords(putRecordsRequest);
		System.out.println("Put Result" + putRecordsResult);
	}
}
