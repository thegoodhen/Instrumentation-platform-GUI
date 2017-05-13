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
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This version of the TwoWaySerialComm example makes use of the
 * SerialPortEventListener to avoid polling.
 *
 */
public class SerialCommunicator {

    /*
     final static byte REQ_MODULE_DATA = 1;
     final static byte REQ_MODULE_XML = 2;
     final static byte REQ_SYNC = 0;
     final static byte REQ_BYTE = 1;
     final static byte WR_BYTE = 2;
     final static byte CMD_NONE = 3;
     */
    static ArrayList<Byte> dataOutBuffer;
    static ArrayList<Byte> dataInBuffer;
    static volatile boolean isWriteBufferWritten = false;
    static volatile boolean isWriteBufferSent = true;
    static volatile boolean isReadBufferWritten = true;
    static volatile boolean isReadBufferSent;
    static volatile boolean commsOn;
    //static volatile byte lastCommand = REQ_SYNC;
    static int commandIndex = 0;

    boolean idle = true;//awaiting the beginning of next packet
    private SerialReader sr;
    private SerialWriter sw;
    private PacketHandler ph;
    private GUIPanel gp;

    public SerialCommunicator(GUIPanel gp) {
	super();
	this.gp = gp;
    }

    public SerialReader getReader() {
	return this.sr;
    }

    public SerialWriter getWriter() {
	return this.sw;
    }

    /**
     *
     * @return the {@link PacketHandler } object assigned to this
     * {@code SerialCommunicator}
     */
    public PacketHandler getPacketHandler() {
	return this.ph;
    }

    /**
     * Estabilish connection with a module, located on serial port of the given
     * name (such as "COM1" on win or "/dev/ttyUSB0/" on Linux)
     *
     * @param portName the name of the port to open
     * @throws Exception when no such port exists or its busy or something else
     * went wrong
     * @see CommPortIdentifier#getPortIdentifier(java.lang.String)
     */
    void connect(String portName) throws Exception {
	CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
	if (portIdentifier.isCurrentlyOwned()) {
	    throw new Exception("Error: Port is currently in use");
	} else {
	    CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

	    if (commPort instanceof SerialPort) {
		SerialPort serialPort = (SerialPort) commPort;
		serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

		InputStream in = serialPort.getInputStream();
		OutputStream out = serialPort.getOutputStream();

		//(new Thread(new SerialWriter(out))).start();
		ph = new PacketHandler(this.gp);
		sr = new SerialReader(ph, in);
		sw = new SerialWriter(out);
		serialPort.addEventListener(sr);
		serialPort.notifyOnDataAvailable(true);

	    } else {
		System.out.println("Error: Only serial ports are handled by this example.");
	    }
	}
    }

    /**
     * Handles the input coming from the serial port.
     */
    public class SerialReader implements SerialPortEventListener {

	private InputStream in;
	private byte[] buffer = new byte[1024];
	private PacketHandler ph;
	private boolean idle = true;//whether we are now waiting for the packet

	public SerialReader(PacketHandler ph, InputStream in) {
	    this.in = in;
	    this.ph = ph;
	}

	/**
	 * Clear the input buffer; This function should be called when a
	 * communication error occurs. It effectively discards all data stored
	 * in the incoming buffer, resetting it in its default state.
	 */
	public void clearBuffer() {
	    try {
		while (in.available() > 0) {
		    in.read();
		}
	    } catch (IOException ex) {
		Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

	/**
	 * Try reading some bytes from the serial port we are connected to,
	 * returning them as an array of integers from 0 to 255; If we have been
	 * waiting for too long, throw a {@link TimeoutException} instead
	 *
	 * @param howMany how many bytes to wait for
	 * @param timeoutBetweenBytes the maximum time period for which to wait
	 * for another byte before throwing a {@link TimeoutException}
	 * @return an array of ints from 0 to 255, representing the received
	 * bytes.
	 * @throws TimeoutException
	 * @throws IOException
	 */
	public int[] readOrDie(int howMany, long timeoutBetweenBytes) throws TimeoutException, IOException {
	    int[] returnArr = new int[howMany];
	    for (int i = 0; i < howMany; i++) {
		long startTime = System.currentTimeMillis();
		boolean timeout = true;
		while (System.currentTimeMillis() < startTime + timeoutBetweenBytes) {
		    if (in.available() > 0)//data is available
		    {
			returnArr[i] = in.read();
			timeout = false;
			break;
		    } else {
			try {
			    Thread.sleep(Math.min(1, timeoutBetweenBytes / 100));
			} catch (InterruptedException ex) {
			    Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
			}
		    }
		}
		if (timeout)//we failed to get new data in the time given
		{
		    throw new TimeoutException();
		}
	    }
	    return returnArr;
	}

	/**
	 * Link layer method, which interprets the incoming data as a packet,
	 * checks the integrity using the {@link checkIntegrity} method and then
	 * forms a {@link RequestNew} from this packet and sends it to be
	 * resolved, using {@link PacketHandler#offer(chartadvancedpie.RequestNew)
	 * }. Also sends an acknowledge ({@link sendOk}) or nonacknowledge
	 * ({@link sendNOTOk}) on success or fail of the integrity check
	 * respectively.
	 */
	private void handlePacket() {
	    try {
		//System.out.println("new Packet!");
		int exl[] = readOrDie(1, 250);
		int expectedLength = exl[0];
		//System.out.println("Expected length:" + expectedLength);
		int data[] = readOrDie(expectedLength, 5000);
		int chksum[] = readOrDie(1, 5000);
		//System.out.println("Chksum: " + chksum[0]);
		if (!checkIntegrity(data, chksum[0])) {
		    //System.out.println("Integrity failure!");
		    this.clearBuffer();
		    SerialCommunicator.this.getWriter().sendNOTOk();
		    idle = true;
		} else {//Integrity OK
		    if (data[0] == 12) {
			System.out.println("KOKOKOKOKOKODAK!!!! KOKON KOKON");
		    }
		    //System.out.println("Integrity ok!");
		    while (!ph.offer(new RequestNew(data, this.ph.gp))) {
			try {
			    Thread.sleep(1);
			} catch (InterruptedException ex) {
			    Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
			}
		    }
		    //System.out.println("Sending OK");
		    if (data[0] != 0) {
			SerialCommunicator.this.getWriter().sendOk();//only send "OK" if we haven't just received "OK", otherwise ignore, so we don't ping "OK" back and forth
		    } else {
			System.out.println("GOT OK!");
		    }

		    idle = true;
		}
	    } catch (IOException | TimeoutException ex) {
		SerialCommunicator.this.getReader().clearBuffer();
		SerialCommunicator.this.getWriter().sendNOTOk();
		Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

	/**
	 * Check the integrity of the data provided, by calculating a crc-8 checksum and comparing it with the
	 * one provided.
	 * @param data the data to calculate the crc-8 checksum from.
	 * @param chksum the checksum to compare the calculated checskum with
	 * @return whether the integrity check passed.
	 */
	private boolean checkIntegrity(int[] data, int chksum) {
	    if (chksum == 6) {//TODO: actually implement chksum
		return true;
	    }
	    return false;
	}

	/**
	 * This method is a listener method, which automatically gets called
	 * when a new SerialPortEvent occurs. (When new data is received).
	 *
	 * @param arg0 the {@link SerialPortEvent} object, containing the
	 * information about the event that occured.
	 */
	public void serialEvent(SerialPortEvent arg0) {
	    if (!idle) {//only handle the start of a packet!
		return;
	    }
	    //TODO: I should make the communication separate from the data interpretation
	    handlePacket();

	}

    }

    /**
     * Take the data array and append a crc-8 checksum to the end.
     * This method modifies the input argument.
     */
    public static void fillCheckSum(byte[] data) {
	byte chksum = 0;
	for (int i = 0; i < data.length - 1; i++) {
	    chksum = (byte) (chksum ^ data[i]);
	}
	data[data.length - 1] = chksum;
    }

    /**
     * Class for sending data to the serial port.
     */
    public class SerialWriter {

	private int unprocessedRequestCount = 0;//number of outgoing requests we haven't received "OK" to.
	private int maxUnprocessedRequestCount = 5;//maximum number of outgoing request that we haven't received confirmation to, for which we still allow sending new request
	private final long outgoingRequestTimeout = 500;//how long we are willing to wait for a reply before giving up
	private final Object lock1 = new Object();
	private final Object reqCountLock = new Object();

	/**
	 * Inform this writer that one of the requests it send have been resolved.
	 */
	public void informAboutProcessedRequest() {
	    synchronized (reqCountLock) {
		if (unprocessedRequestCount != 0) {//should never happen tho
		    unprocessedRequestCount--;
		}
	    }
	}

	/**
	 * Increase the counter that determines the amount of currently sent, but unresolved requests.
	 */
	private void increaseUnprocessedRequestCount() {
	    synchronized (reqCountLock) {
		unprocessedRequestCount += 1;
	    }
	}

	/**
	   * Return the value of the counter that determines the amount of currently sent, but unresolved requests.
	 */
	public int getUnprocessedRequestCount() {
	    synchronized (reqCountLock) {
		return unprocessedRequestCount;
	    }
	}

	OutputStream out;

	public SerialWriter(OutputStream out) {
	    this.out = out;
	}

	/**
	 * Send an OK (positive ACK) command to the module.
	 */
	public void sendOk() {
	    System.out.println("sending ok");
	    final int OK = 0;
	    int[] a = {OK};
	    try {
		sendData(a, false);
	    } catch (IOException ex) {
		Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

	/**
	 * Send an NotOK (Negative ACK) command to the module.
	 */
	public void sendNOTOk() {
	    final int NOTOK = 1;
	    int[] a = {NOTOK};
	    try {
		sendData(a, false);
	    } catch (IOException ex) {
		Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
	    }

	}


	/**
	 * Send an INIT command to the module.
	 */
	public void sendInit() {
	    int[] data = {10};
	    try {
		sendData(data, true);
	    } catch (IOException ex) {
		Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

	/**
	 * Send an INIT2 command to the module.
	 */
	public void sendInit2() {

	    int[] data = {9};
	    try {
		sendData(data, true);
	    } catch (IOException ex) {
		Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
	    }

	}

	/**
	 * Send an GUI_ELEMENT_RENUMBER command to the module.
	 */
	void sendGUIElementRenumber(byte origNumber, byte targetNumber)//TODO: maybe it should be int, int or byte, int
	{
	    int[] data = {12, origNumber, targetNumber};
	    try {
		sendData(data, true);
	    } catch (IOException ex) {
		Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}

	/**
	 * Send the data provided to the module, returning true on success, false otherwise.
	 * @param data the data to send, array of ints from 0 to 255.
	 * @param waitForConfirm whether we should actively wait for the reply (true) or not (false)
	 * @return true on success, false otherwise.
	 * @throws IOException when something goes wrong
	 */
	public boolean sendData(int[] data, boolean waitForConfirm) throws IOException {
	    synchronized (lock1) {
		if (getUnprocessedRequestCount() >= maxUnprocessedRequestCount) {
		    if (!waitForConfirm) {
			return false;
		    } else {
			long startTime = System.currentTimeMillis();
			while (getUnprocessedRequestCount() >= maxUnprocessedRequestCount) {
			    if (System.currentTimeMillis() > startTime + outgoingRequestTimeout) {
				return false;
			    }
			    try {
				Thread.sleep(1);//TODO: fix 
			    } catch (InterruptedException ex) {
				Logger.getLogger(SerialCommunicator.class.getName()).log(Level.SEVERE, null, ex);
			    }
			}
		    }
		}

		byte[] byteData = new byte[data.length];
		out.write(data.length);
		for (int i = 0; i < byteData.length; i++) {
		    byteData[i] = (byte) data[i];
		}
		out.write(byteData);
		int chksum = (int) calculateChecksum(data);
		out.write(chksum);

		if (data[0] != 0) {//if we are just sending "OK", we shouldn't expect a reply
		    this.increaseUnprocessedRequestCount();
		}
		return true;
	    }
	}

	/**
	 * Calculate the crc-8 checksum of the data provided
	 * @param data
	 * @return the crc-8 checksum of the data provided
	 */
	private int calculateChecksum(int[] data) {
	    return 6;//TODO: actually calculate the checksum
	}

    }

    /*
     public static void main(String[] args) {
     dataOutBuffer = new ArrayList<>();
     dataInBuffer = new ArrayList<>();
     try {
     SerialCommunicator comm = new SerialCommunicator();
     comm.connect("COM3");
     SerialReader sr = comm.getReader();

     //startComms();
     } catch (Exception e) {
     // TODO Auto-generated catch block
     e.printStackTrace();
     }
     }
     */
}
