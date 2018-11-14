/**
 * 
 */
package eu.vargasoft.temperaturlogger;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.HidServicesSpecification;
import org.hid4java.ScanMode;
import org.hid4java.event.HidServicesEvent;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author buxi
 *
 */
@Service
@Slf4j
public class SensorManager implements HidServicesListener {
	private static final Integer VENDOR_ID = 0x16c0;
	private static final Integer PRODUCT_ID = 0x0480;
	private static final int PACKET_LENGTH = 64;
	public static final String SERIAL_NUMBER = null;
	HidServices hidServices;
	HidDevice hidDevice;

	SensorManager() {
		// Configure to use custom specification
		HidServicesSpecification hidServicesSpecification = new HidServicesSpecification();
		hidServicesSpecification.setAutoShutdown(true);
		hidServicesSpecification.setScanInterval(500);
		hidServicesSpecification.setPauseInterval(5000);
		hidServicesSpecification.setScanMode(ScanMode.SCAN_AT_FIXED_INTERVAL_WITH_PAUSE_AFTER_WRITE);

		// Get HID services using custom specification
		hidServices = HidManager.getHidServices(hidServicesSpecification);
		hidServices.addHidServicesListener(this);

		// Open the device device by Vendor ID and Product ID with wildcard serial
		// number
		hidDevice = hidServices.getHidDevice(VENDOR_ID, PRODUCT_ID, SERIAL_NUMBER);
	}

	public List<InfoRecord> readData() {

		List<InfoRecord> data = null;
		if (hidDevice != null) {
			// Consider overriding dropReportIdZero on Windows
			// if you see "The parameter is incorrect"
			// HidApi.dropReportIdZero = true;

			// Device is already attached and successfully opened so send message
			data = sendMessage(hidDevice);
		}

		// Stop the main thread to demonstrate attach and detach events
		sleepUninterruptibly(1, TimeUnit.SECONDS);
		return data;
	}

	@Override
	public void hidDeviceAttached(HidServicesEvent event) {
		log.debug("Device attached: " + event);

		// Add serial number when more than one device with the same
		// vendor ID and product ID will be present at the same time
		if (event.getHidDevice().isVidPidSerial(VENDOR_ID, PRODUCT_ID, null)) {
			sendMessage(event.getHidDevice());
		}
	}

	@Override
	public void hidDeviceDetached(HidServicesEvent event) {
		log.error("Device detached: " + event);

	}

	@Override
	public void hidFailure(HidServicesEvent event) {
		log.error("HID failure: " + event);

	}

	private List<InfoRecord> sendMessage(HidDevice hidDevice) {

		// Ensure device is open after an attach/detach event
		if (!hidDevice.isOpen()) {
			hidDevice.open();
		}
		ArrayList<InfoRecord> infoRecords = new ArrayList<InfoRecord>();

		// Send the Initialise message
		byte[] message = new byte[PACKET_LENGTH];
		message[0] = 0x3f; // USB: Payload 63 bytes
		message[1] = 0x23; // Device: '#'
		message[2] = 0x23; // Device: '#'
		message[3] = 0x00; // INITIALISE

		int val = hidDevice.write(message, PACKET_LENGTH, (byte) 0x00);
		if (val >= 0) {
			log.debug("> [" + val + "]");
		} else {
			log.error(hidDevice.getLastErrorMessage());
		}

		// Prepare to read a single data packet
		boolean moreData = true;
		while (moreData) {
			byte data[] = new byte[PACKET_LENGTH];
			// This method will now block for 500ms or until data is read
			val = hidDevice.read(data, 500);
			switch (val) {
			case -1:
				log.error(hidDevice.getLastErrorMessage());
				break;
			case 0:
				moreData = false;
				break;
			default:
				if (log.isDebugEnabled()) {
					StringBuilder str = new StringBuilder();
					str.append("< [");
					for (byte b : data) {
						str.append(String.format("%02x", b));
					}
					str.append("]");
				}
				infoRecords.add(InfoRecordCreator.createInforRecord(data));
				break;
			}
		}
		return infoRecords;
	}

	/**
	 * Invokes {@code unit.}{@link java.util.concurrent.TimeUnit#sleep(long)
	 * sleep(sleepFor)} uninterruptibly.
	 */
	public void sleepUninterruptibly(long sleepFor, TimeUnit unit) {
		boolean interrupted = false;
		try {
			long remainingNanos = unit.toNanos(sleepFor);
			long end = System.nanoTime() + remainingNanos;
			while (true) {
				try {
					// TimeUnit.sleep() treats negative timeouts just like zero.
					NANOSECONDS.sleep(remainingNanos);
					return;
				} catch (InterruptedException e) {
					interrupted = true;
					remainingNanos = end - System.nanoTime();
				}
			}
		} finally {
			if (interrupted) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@PreDestroy
	void shutdown() {
		// Shut down and rely on auto-shutdown hook to clear HidApi resources
		hidServices.shutdown();
		log.debug("destroying SensorManager");
	}
}
