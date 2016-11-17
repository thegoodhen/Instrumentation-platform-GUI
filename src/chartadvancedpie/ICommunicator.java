package chartadvancedpie;


import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thegoodhen
 */
public interface ICommunicator {
	
int readByte() throws IOException;
void writeByte(byte b) throws IOException;
byte [] read();
void write(byte[] b) throws IOException;
int available();
byte[] readBytes(int count, int timeout) throws IOException;
}
