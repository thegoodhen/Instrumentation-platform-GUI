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

/**
 * @deprecated 
 * @author thegoodhen
 */

public interface ICommunicator {
	
/**
 * Read as single byte.
 * @return the byte read, expressed as Integer from 0 to 255.
 * @throws IOException 
 */
int readByte() throws IOException;
/**
 * Write a signle byte
 * @param b the byte to write
 * @throws IOException 
 */
void writeByte(byte b) throws IOException;

byte [] read();
void write(byte[] b) throws IOException;
int available();
byte[] readBytes(int count, int timeout) throws IOException;
}
