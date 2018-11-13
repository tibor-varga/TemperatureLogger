package eu.vargasoft.temperaturlogger;

public class InfoRecord {
	long timestamp;
	String sensorId;
	float value;
	
	public InfoRecord(long timestamp, String sensorId, float value) {
		super();
		this.timestamp = timestamp;
		this.sensorId = sensorId;
		this.value = value;
	}

	@Override
	public String toString() {
		return "InfoRecord [timestamp=" + timestamp + ", sensorId=" + sensorId + ", value=" + value + "]";
	}
}
