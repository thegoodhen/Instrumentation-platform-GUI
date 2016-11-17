/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 *Factory class for creating Containers. This class doesn't use static methods, as doing so would 
 * render it difficult to put a container into another container.
 * @author thegoodhen
 */
public class ContainerFactory_backup {

	private Container cont;

	final static byte BEGIN_TAG = 0;
	final static byte BEGIN_TAG_EX = 1;
	final static byte BEGIN_TAG_EX2 = 2;
	final static byte END_TAG = 3;

	final static byte TAG_PLATFORM = 0;
	final static byte TAG_MODULE = 1;
	final static byte TAG_HUB = 2;
	final static byte TAG_CHANNELGROUP = 3;
	final static byte TAG_REGISTER = 4;
	private static int startIndex = 0;
	private static int index = 0;
	private static byte[] xmlAsBytes;

	/**
	 * Eats all the bytes belonging to a tag, returning the tag value
	 *
	 * @return position of the last byte in the xml,which belong to the tag declaration
	 */

	int resolveTag(int tagNumber) {
		ContainerFactory_backup cf=new ContainerFactory_backup();
		Container newCont=cf.create(xmlAsBytes, index);
		cont.addContainer(newCont);
		return ContainerFactory_backup.getNewIndex();
	}

	/**
	 * This method should call the correct setter in subclass, setting the correct property based on
	 * Variable.getNumber();
	 * @return 
	 */
	int resolveVariable() {

		Variable v = VariableFactory.create(xmlAsBytes, index);
		cont.addVariable(v);
		//System.out.println(v.toString());
		return VariableFactory.getNewIndex();
	}


	public int getIndex()
	{
		return index;
	}


	final static int loadTag() {
		if (xmlAsBytes[index] == BEGIN_TAG) {
			index++;
			return xmlAsBytes[index];//TODO: implement extended tag, etc.

		}
		return -1;
	}

public void setXml(byte[] xml)
{
	ContainerFactory_backup.xmlAsBytes=xml;
}

 public byte[] getXml()
 {
	 return xmlAsBytes;
 }

	final protected void parseXml() {
		byte currentByte = 0;
		int currentTag = -1;
		for (index = startIndex + 1; index < xmlAsBytes.length; index++) {
			currentByte = xmlAsBytes[index];
			if (isTagMark(currentByte)) {
				if (currentByte != END_TAG) {
					//tagStack.push(currentTag);
					currentTag = loadTag();
					index = resolveTag(currentTag);
				} else//ending tag here
				{
					//currentTag=tagStack.pop();
					break;
				}

			} else //not a tag, but a variable
			{
				Variable v = VariableFactory.create(xmlAsBytes, index);
				System.out.println(v.toString());
				index = resolveVariable();
			}
		}
	}

	final private static boolean isTagMark(byte mark) {
		return (mark & 0xFF) <= END_TAG;
	}

	final public static int getNewIndex() {
		return index;
	}

	public Container create(byte[] xml, int index2) {
		startIndex = index2;
		xmlAsBytes = xml;
		cont = new Container();
		parseXml();
		return cont;
	}
public final void setContainer(Container cont)
{
	this.cont=cont;
}
	public void setStartIndex(int index)
	{
		ContainerFactory_backup.startIndex=index;
	}
}
