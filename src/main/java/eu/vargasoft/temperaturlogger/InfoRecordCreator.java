package eu.vargasoft.temperaturlogger;

import java.time.Instant;

public class InfoRecordCreator {
	/**
	 * 02 01 01 00 e5 00 00 00 28 1b 3d 77 91 18 02 5e 00 00 00 00 00 00 00 00 00 00
	 * 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
	 * 00 00 00 00 00 00 00 00 00 00 00 00
	 */
	static public InfoRecord createInforRecord(byte[] bytesFromDevice) {
		StringBuilder builder = new StringBuilder();
		for (int i = 8; i < 16; i++) {
			builder.append(String.format("%02x", bytesFromDevice[i]));
		}
		String sensorId = builder.toString();

		Instant instant = Instant.now();
		long timeStampMillis = instant.toEpochMilli();

		float value = ((bytesFromDevice[5] & 0xff) << 8) | (bytesFromDevice[4] & 0xff);
		InfoRecord record = new InfoRecord(timeStampMillis, sensorId, value / 10);
		return record;
	}
}
