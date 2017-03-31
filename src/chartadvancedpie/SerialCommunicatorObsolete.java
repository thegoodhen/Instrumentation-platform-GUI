package chartadvancedpie;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *This class is a simple utility class that estabilishes connection over a given port and then allows 
 * Other classes to read/write to it using blocking calls.
 * It has been remade from scratch now.
 * @author thegoodhen
 */
@Deprecated
public class SerialCommunicatorObsolete implements ICommunicator{
	InputStream in;
	OutputStream out;
	public SerialCommunicatorObsolete()
	{
		super();
	}

	SerialCommunicatorObsolete(String port, int baudRate)throws Exception {
		super();
		connect(port,baudRate);

	}


public void writeByte(byte b) throws IOException
{
	out.write((byte)b);
}

	@Override
	public void write(byte[] bytes) throws IOException

{
	out.write(bytes);
}
public int readByte() throws IOException
{
	return in.read();
}


public byte [] read()
{

 byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > 0 )
                {
                    System.out.print(new String(buffer,0,len));
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }         
	    return buffer;
}


public int available()
{
		try {
			return in.available();
		} catch (IOException ex) {
			Logger.getLogger(SerialCommunicatorObsolete.class.getName()).log(Level.SEVERE, null, ex);
		}
		return 0;
}

final void connect(String portName, int speed) throws Exception
{

  CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(speed,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();
                

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
}

	@Override
	public byte[] readBytes(int count, int timeout) throws IOException {

		byte[] data=new byte[count];
		for(int i=0;i<count;i++)
		{
			long startTime = System.currentTimeMillis();
			while(in.available()==0)
			{
				if(System.currentTimeMillis()>startTime+timeout)
				{
					throw new IOException("Operation timed out.");
				}
			}
			data[i]=(byte)in.read();
		}

		return data;

	}

	
}
