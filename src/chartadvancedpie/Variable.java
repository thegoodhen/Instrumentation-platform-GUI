/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;

/**
 *
 * @author thegoodhen
 */
@Deprecated
public abstract class Variable {

	Request readRequest;
	Request writeRequest;
	private boolean changedFromGUI;
	private int value;

	private ArrayList<Subscriber> subscriberList;

	public Variable(boolean read, boolean write, int priority, byte addrRange, int address, int number) {
		this.readRequest=new Request();
		this.writeRequest=new Request();
		this.subscriberList=new ArrayList<Subscriber>();
		this.setRead(read);
		this.setWrite(write);
		this.setPriority((byte) priority);
		this.setAddrRange(addrRange);
		this.setAddress(address);
		this.setNumber(number);
		if (!write && !read) {
			setValue(address);
		}

	}
	public static final byte VAR_BYTE = 4;
	public static final byte VAR_INT = 5;
	//TODO: fill in the rest
	public static final byte VAR_STRING = 11;//0b00001011;
	private boolean read;
	private boolean write;
	private byte priority;

	private byte addrRange;
	private int address;
	private int number;

	public void postRequests() {
		if (hasSubscribers()) {
			if (getWrite()) {
				if (changedFromGUI) {
					changedFromGUI = false;
					if (writeRequest.isResolved()) {
						Request.offerIfResolved(writeRequest);
					}
				}
			}
			if (getRead()) {

				/*if (readRequest.isResolved()) {
					Request.offerIfResolved(readRequest);
				}*/
			}
		}
	}

	public ArrayList<Subscriber> getSubscriberList() {
		return this.subscriberList;
	}

	public boolean hasSubscribers() {
		return !subscriberList.isEmpty();
	}

	public void addSubscriber(Subscriber s) {
		this.subscriberList.add(s);
	}

	public void removeSubscriber(Subscriber s) {
		this.subscriberList.remove(s);
	}

	public void notifyAboutChangeFromGUI() {
		changedFromGUI = true;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public int getAddress() {
		return address;
	}

	public int getValue()//TODO: make the return type generic
	{
		return value;
	}

	public void setValue(int value) //TODO: make the return type generic
	{
		this.value = value;
	}

	public boolean getWrite() {
		return write;
	}

	public void setWrite(boolean write) {
		this.write = write;
	}

	public boolean getRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public byte getPriority() {
		return priority;
	}

	public void setPriority(byte priority) {
		this.priority = priority;
	}

	public void setAddrRange(byte addrRange) {
		this.addrRange = addrRange;
	}

	public byte getAddrRange() {
		return this.addrRange;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getNumber() {
		return this.number;
	}

	public String getDesc() {

		return "Number: " + this.getNumber() + " read: " + this.getRead() + " write: " + this.getWrite() + " priority: " + this.getPriority() + " addr range: " + this.getAddrRange() + " address: " + this.getAddress();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("numeric variable: ");
		if (!this.getRead() && !this.getWrite()) {
			sb.append(Integer.toString(this.getAddress()));
		} else {
			sb.append("(address): ").append(this.getAddress());
		}
		//return "Number: "+this.getNumber()+" read: " +this.getRead()+" write: "+this.getWrite()+" priority: "+this.getPriority()+" addr range: "+this.getAddrRange()+" address: "+this.getAddress();
		return sb.toString();
	}

}
