/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 *
 * @author thegoodhen
 * @deprecated
 */
public class VariableByte extends Variable{

		public VariableByte(boolean read, boolean write, int priority, byte addrRange, int address, int number)
		{
			super(read,write,priority,addrRange,address,number);
		this.readRequest=new Request();
		this.writeRequest=new ByteWriteRequest((byte)0,(byte)15,this);//TODO: set the device number.
		}
}
