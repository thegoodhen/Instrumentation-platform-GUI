package chartadvancedpie;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *@deprecated
 * An old class related to the old way communications were handled
 * @author thegoodhen
 */
public class SyncRequest extends Request {

	public SyncRequest() {

	}

	@Override
	public void resolve(ICommunicator c) {
		c.read();//get rid of the previous data
		byte[] data = new byte[3];
		data[0] = 1;
		data[1] = 2;
		data[2] = 4;
		int attemptNumber=10;
		//fillCheckSum(data);
		try {
			while (true) {
				System.out.println("Trying to estabilish connection..."+attemptNumber);
				c.read();
				//c.write(data);
				c.writeByte((byte)1);
				Thread.sleep(attemptNumber*10);
				c.writeByte((byte)2);
				Thread.sleep(attemptNumber*10);
				c.read();
				Thread.sleep(attemptNumber*10);
				c.writeByte((byte)4);
				Thread.sleep(attemptNumber*10);
				if(c.available()>0)
				{
					byte dataIn=(byte) c.readByte();
					if(dataIn==ACK)
					{
						c.writeByte((byte)8);
						this.setResolved(true);
						System.out.println("Connection estabilished succesfully. Main module online.");
						break;
					}
					else
					{
						System.out.println("Bad response: Expected ACK (6), but got: "+(byte)dataIn);
					}
				}
				else
				{
					System.out.println("Operation timed out with no reply for sync request. Adjusting communication speed for sync requests and retrying...");
				}
				attemptNumber++;
			}

		} catch (IOException ex) {
			Logger.getLogger(ByteRequest.class.getName()).log(Level.SEVERE, null, ex);
		} catch (InterruptedException ex) {
			Logger.getLogger(SyncRequest.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
