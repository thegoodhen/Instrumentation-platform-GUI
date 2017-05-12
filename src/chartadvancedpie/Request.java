package chartadvancedpie;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import shuntingyard.Token;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *@deprecated
 * This class has now been replaced by the {@link RequestNew class}
 * @author thegoodhen
 * @param <type> the return data type of the request
 */
public class Request<type> {

	final static byte REQ_MODULE_DATA = 1;
	final static byte REQ_MODULE_XML = 2;
	final static byte REQ_SYNC = 0;
	final static byte REQ_BYTE = 1;
	final static byte WR_BYTE = 7;
	final static byte CMD_NONE = 3;
	final static byte ACK = 6;
	private static ICommunicator c;
	private static byte lastStatus = ACK;
	private static GUIPanel gup;
	private static ArrayList<Token> stepCallBack;

	static void startComms(ICommunicator c) {
		Request.c = c;
		(new Thread(new RequestPoster())).start();

	}

	public static void setGUIPanel(GUIPanel gp) {
		gup = gp;
		GUICompiler gc =gp.getGUICompiler();
		compileCallbacks(gc);
	}

	private static void compileCallbacks(GUICompiler gc) {
		//gc.compile("step();\n");
		stepCallBack=gc.getByteCodeAL();
	}

	/**
	 * Static constructor, sets the communicator
	 *
	 * @param c
	 */
	public Request(ICommunicator c) {
		Request.c = c;
		(new Thread(new RequestPoster())).start();
	}

	public static class RequestPoster implements Runnable {

		public void run() {
			while (true) {
				gup.handleCallBack(stepCallBack);
				if (!isEmpty()) {
					Request r = pop();
					System.out.println("Going to resolve request: " + r.getClass().getSimpleName());
					r.resolve(c);
					System.out.println("Request resolved");
				} else {
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException ex) {
					Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

	}

	public Request() {

	}

	public static byte[] exchangeData(byte[] data, ICommunicator c) throws IOException {
		byte dataWithCheckSum[] = new byte[data.length + 2];
		for (int i = 0; i < data.length; i++) {
			dataWithCheckSum[i + 1] = data[i];
		}
		byte tempArray[] = new byte[1];
		dataWithCheckSum[0] = (byte) (data.length + 1);//TODO: add support for longer messages
		fillCheckSum(dataWithCheckSum);
		try {
			c.write(dataWithCheckSum);
			System.out.println("start");
			for (int i = 0; i < dataWithCheckSum.length; i++) {
				System.out.println(dataWithCheckSum[i]);
			}
			System.out.println("end");
			//Thread.sleep(100);
			byte expectedBytesCount = c.readBytes(1, 250)[0];
			//byte expectedBytesCount = (byte) c.readByte();
			System.out.print("expectedBytesCount: ");
			System.out.println(expectedBytesCount);

			tempArray = c.readBytes((int) expectedBytesCount & 0xFF, 1000);

			byte chksum = expectedBytesCount;
			for (int i = 0; i < tempArray.length - 1; i++) {
				chksum = (byte) (chksum ^ tempArray[i]);
			}

			if (chksum != tempArray[tempArray.length - 1])//checksum mismatch
			{
				for (int i = 0; i < tempArray.length; i++) {
					System.out.println(tempArray[i]);
				}
				throw new IOException("Checksum error, expected " + chksum + ", but got " + tempArray[tempArray.length - 1]);
			}

			lastStatus = tempArray[0];

		} catch (IOException ex) {
			Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
			throw ex;
		}
		//catch (InterruptedException ex) {
		//Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
		//}

		byte returnArray[] = new byte[tempArray.length - 2];

		for (int i = 1; i <= tempArray.length - 2; i++) {
			returnArray[i - 1] = tempArray[i];
		}

		return returnArray;
	}

	public static void fillCheckSum(byte[] data) {
		byte chksum = 0;
		for (int i = 0; i < data.length - 1; i++) {
			chksum = (byte) (chksum ^ data[i]);
		}
		data[data.length - 1] = chksum;
	}
	private boolean isResolved = true;//false; because we need to load it, and we only load it when it's been resolved
	static LinkedList<Request> queue = new LinkedList<>();

	static synchronized void offerIfResolved(Request r) {
		if (r.isResolved()) {
			queue.add(r);
			r.setResolved(false);
			System.out.println("Adding request to queue: " + r.getClass().getSimpleName());
		}
	}

	static synchronized boolean isEmpty() {
		return queue.isEmpty();
	}

	static synchronized Request peek() {
		return queue.peek();
	}

	static synchronized Request pop() {
		return queue.removeLast();
	}

	public boolean isResolved() {
		return isResolved;
	}

	public void setResolved(boolean isResolved) {
		this.isResolved = isResolved;
	}

	public void resolve(ICommunicator c) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
		}
		System.err.println("WARNING: attempt to resolve a generic Request. Procedure to resolve request was unknown! Ignoring.");
		this.setResolved(true);
	}
}
