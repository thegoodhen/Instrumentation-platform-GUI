package chartadvancedpie;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @deprecated 
 * Class related to handling requests from the child module when communicating with it.
 * The way requests are handled has since been reimplemented.
 * @author thegoodhen
 */
public class ByteRequest extends Request<Byte>{

	private byte deviceNum;
	private byte addr;
	
	public ByteRequest(ICommunicator c) {
		super(c);

	}

public ByteRequest(byte deviceNum,byte addr)
{
this.deviceNum=deviceNum;
this.addr=addr;

}
	@Override
	public void resolve(ICommunicator c)
	{
		c.read();//get rid of the previous data
		byte[] data = new byte[5];
		data[0] = REQ_MODULE_DATA;
		data[1] = deviceNum;
		data[2] = REQ_BYTE;
		data[3] = addr;
		fillCheckSum(data);
		try {
			c.write(data);
			byte ack =(byte)c.readByte();
			byte dataIn=(byte)c.readByte();
			byte chkSum=(byte)c.readByte();
			System.out.println(dataIn);
			
		} catch (IOException ex) {
			Logger.getLogger(ByteRequest.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
	
}
