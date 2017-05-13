package chartadvancedpie;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thegoodhen
 * @deprecated
 */
public class VariableFactory {

	private static int index;

	public static int getNewIndex() {
		return index;
	}

	public static Variable create(byte[] xml, int index2) {
		index = index2;
		byte temp = xml[index];
		boolean read = isReadable(temp);
		boolean write = isWritable(temp);
		byte addrRange = addrLength(temp);
		index++;
		int priority = getLowerNibble(xml[index]);
		int variableNumber = getUpperNibble(xml[index]);
		//TODO: support for more address bytes here
		index++;
		int varType=getVariableType(temp)&0xFF;
		System.out.println(varType);
		switch (varType) {
			case Variable.VAR_BYTE:
		int address = xml[index];
				return new VariableByte(read, write, priority, addrRange, address, variableNumber);

			case Variable.VAR_STRING:
				char[] theString=new char[256];
				int index3=0; 
				while(xml[index]!=13)
				{
					theString[index3]=(char) xml[index];
					index3++;
					index++;
				}
				VariableString vs=new VariableString(read,write,priority,addrRange,0,variableNumber);
				String theStringString=new String(theString);
				theStringString=theStringString.substring(0, theStringString.indexOf(0));
				vs.setString(theStringString);
				return vs;
		}
		return null;
	}

	private static byte addrLength(byte b) {
		return (byte) (b & ~(0b11111100));
	}

	private static boolean isReadable(byte b) {
		return (((b >>> 3) & 1) != 0);
	}

	private static boolean isWritable(byte b) {
		return (((b >>> 2) & 1) != 0);
	}

	private static byte getVariableType(byte v) {
		System.out.println("kokodak: "+Integer.toBinaryString(getUpperNibble(v)));
		return getUpperNibble(v);
	}

	private static byte getUpperNibble(byte b) {

		return  (byte)((b >>> 4)&~0b11110000);
	}

	private static byte getLowerNibble(byte b) {
		return  (byte)(b & ~(0b11110000));
	}
}
