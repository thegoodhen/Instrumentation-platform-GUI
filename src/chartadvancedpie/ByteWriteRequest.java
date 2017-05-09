/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Class related to handling requests from the child module when communicating with it.
 * The way requests are handled has since been reimplemented.
 * @author thegoodhen
 */
public class ByteWriteRequest extends Request{

private Variable v;

	private byte deviceNum;
	private byte addr;

	public ByteWriteRequest(byte deviceNum, byte addr, VariableByte v)
	{
		this.deviceNum=deviceNum;
		this.addr=addr;
		this.v=v;
	}

	@Override
	public void resolve(ICommunicator c)
	{

		c.read();//get rid of the previous data
		byte[] data = new byte[5];
		data[0] = REQ_MODULE_DATA;
		data[1] = deviceNum;
		data[2] = WR_BYTE;
		data[3] = 0;
		data[4] = (byte)v.getValue();
		/*
			System.out.println("Sending: "+(byte)v.getValue());
		fillCheckSum(data);
		try {
			c.write(data);
			byte ack =(byte)c.readByte();
			byte dataIn=(byte)c.readByte();
			byte chkSum=(byte)c.readByte();
			System.out.println("Got response: "+dataIn);
			
		} catch (IOException ex) {
			Logger.getLogger(ByteRequest.class.getName()).log(Level.SEVERE, null, ex);
		}

			*/
	try {
		byte[] response=Request.exchangeData(data, c);
		if(response.length<4)
		{
			System.out.println("Unexpected incoming packet length.");
		}
		System.out.println("Response: "+response[2]);
		for(int i=0;i<response.length;i++)
		{
			System.out.println(response[i]);
		}
			


	} catch (IOException ex) {
		Logger.getLogger(ByteWriteRequest.class.getName()).log(Level.SEVERE, null, ex);
	}
		this.setResolved(true);
	}
	
}
