package chartadvancedpie;


import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This version of the TwoWaySerialComm example makes use of the
 * SerialPortEventListener to avoid polling.
 * @deprecated 
 * An obsolete class for testing purposes.
 */
public class TwoWaySerialComm {

	final static byte REQ_MODULE_DATA = 1;
	final static byte REQ_MODULE_XML = 2;
	final static byte REQ_SYNC=0;
	final static byte REQ_BYTE = 1;
	final static byte WR_BYTE = 2;
	final static byte CMD_NONE=3;
	static ArrayList<Byte> dataOutBuffer;
	static ArrayList<Byte> dataInBuffer;
	static volatile boolean isWriteBufferWritten = false;
	static volatile boolean isWriteBufferSent = true;
	static volatile boolean isReadBufferWritten = true;
	static volatile boolean isReadBufferSent;
	static volatile boolean commsOn;
	static volatile byte lastCommand=REQ_SYNC;
	static int commandIndex=0;

	public TwoWaySerialComm() {
		super();
	}

	void connect(String portName) throws Exception {
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();

				(new Thread(new SerialWriter(out))).start();

				serialPort.addEventListener(new SerialReader(in));
				serialPort.notifyOnDataAvailable(true);

			} else {
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	/**
	 * Handles the input coming from the serial port. A new line character
	 * is treated as the end of a block in this example.
	 */
	public static class SerialReader implements SerialPortEventListener {

		private InputStream in;
		private byte[] buffer = new byte[1024];

		public SerialReader(InputStream in) {
			this.in = in;
		}
		public void endCommand()
		{
			commandIndex=0;
			lastCommand=CMD_NONE;
		}

		public void serialEvent(SerialPortEvent arg0) { //TODO: I should make the communication separate from the data interpretation
			int data;

			try {
				int len = 0;
				while ((data = in.read()) > -1) {
					switch(lastCommand)
					{
						case REQ_SYNC:
							if(data==6)
							{

								System.out.println("Slave reply: In sync.");
								commsOn=true;
							}
							break;
						case REQ_MODULE_XML:
							System.out.println(data);

							break;
					}
					/*
					if (data == '\n') {
						break;
					}
					*/
					buffer[len++] = (byte) data;
					commandIndex++;
				}
				System.out.println(new String(buffer, 0, len));
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

	}

	/**
	 *
	 */
	public static class SerialWriter implements Runnable {

		OutputStream out;

		public SerialWriter(OutputStream out) {
			this.out = out;
		}

		public void run() {
			try {
				while (true)//loop forever
				{
					//System.out.println("written: "+isWriteBufferWritten);
					//System.out.println("sent: "+isWriteBufferSent);
					if (isWriteBufferWritten && !isWriteBufferSent) {
						System.out.println("sending");
						//isWriteBufferSent = false;
						for (Byte b : dataOutBuffer) {
							this.out.write(b);
						}
						isWriteBufferSent = true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	public static void main(String[] args) {
		dataOutBuffer = new ArrayList<>();
		dataInBuffer = new ArrayList<>();
		try {
			(new TwoWaySerialComm()).connect("COM3");

			startComms();
			while (true) {
				System.out.println("written: " + isWriteBufferWritten);
				System.out.println("sent: " + isWriteBufferSent);
				if (isWriteBufferSent) {
					System.out.println("Kvok");

					//startComms();
					byte[] a={2, 4, 6,8,10};
					requestXml((byte)0);
					//sendData(a);
					//requestByte((byte)0,(byte)1);
				}
				System.out.println("Kokodak");
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendData(byte[] theData) {
		isWriteBufferWritten = false;
		//Byte[] theData=(Byte[])theData2;//{1,2,3};
		dataOutBuffer = new ArrayList<Byte>();
		for (byte b : theData) {
			dataOutBuffer.add(b);
		}
		//Collections.addAll(dataOutBuffer,theData);
		isWriteBufferWritten = true;
		isWriteBufferSent = false;
	}

	public static void startComms() {

		lastCommand=REQ_SYNC;
		for (int i = 0; i < 100; i++)//100 tries to start
		{
			if (isWriteBufferSent) {
				if(commsOn)
				{
					System.out.println("Synchronization succesfull.");
					break;
				}
				System.out.println("Attempting to sync... "+i);
				byte[] data = new byte[4];
				data[0] = 3;
				data[1] = 2;
				data[2] = 1;
				fillCheckSum(data);
				sendData(data);
				try {
					Thread.sleep(500);
				} catch (InterruptedException ex) {
					Logger.getLogger(TwoWaySerialComm.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

	}

	public static void requestByte(byte deviceNum, byte addr) {
		byte[] data = new byte[5];
		data[0] = REQ_MODULE_DATA;
		data[1] = deviceNum;
		data[2] = REQ_BYTE;
		data[3] = addr;
		fillCheckSum(data);
		sendData(data);

	}

	public static void requestXml(byte deviceNum)
	{
		lastCommand=REQ_MODULE_XML;
		byte[] data = new byte[3];
		data[0] = REQ_MODULE_XML;
		data[1] = deviceNum;
		fillCheckSum(data);
		sendData(data);


	}

	public static void fillCheckSum(byte[] data) {
		byte chksum = 0;
		for (int i = 0; i < data.length - 1; i++) {
			chksum = (byte) (chksum ^ data[i]);
		}
		data[data.length - 1] = chksum;
	}

}
