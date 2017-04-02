/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import shuntingyard.HelpByteMethods;

/**
 *
 * @author thegoodhen
 */
public class PacketHandler {

    GUIPanel gp;
    boolean processingRequest;
    LinkedBlockingQueue<RequestNew> queue = new LinkedBlockingQueue(10);//10 requests max
    ArrayList<Resolver> resolverList;

    private abstract class Resolver {

	public abstract void resolve(RequestNew rn);
    }

    public PacketHandler() {
	resolverList = new ArrayList<>();
	resolverList.add(new Resolver()//This is number 0
	{
	    @Override
	    public void resolve(RequestNew rn) {
		//System.out.println("resolving...");
		int[] data = rn.getData();
		/*
		 if(Math.random()>0.9)
		 {
		 try {
		 Thread.sleep((long) (Math.random()*100));
		 } catch (InterruptedException ex) {
		 Logger.getLogger(PacketHandler.class.getName()).log(Level.SEVERE, null, ex);
		 }
			
		 }*/
		for (int i = 1; i < data.length - 5; i++) {
		    byte guiElementID = (byte) data[i];
		    byte propertyID = (byte) data[i + 1];
		    byte fl1 = (byte) data[i + 2];
		    byte fl2 = (byte) data[i + 3];
		    byte fl3 = (byte) data[i + 4];
		    byte fl4 = (byte) data[i + 5];

		    byte[] theArr = {fl1, fl2, fl3, fl4};
		    float f = HelpByteMethods.constructFloat(theArr);
		    //System.out.println("guiElementID is: " + guiElementID);
		    //System.out.println("propertyID is: " + propertyID);
		    //System.out.println("value is: " + f);
		    //guiElementID=8;
		    //propertyID=0;
		    GUIelement ge = gp.ID2GUIMap.get((int) guiElementID);
		    if (ge != null) {
			Property p = ge.getPropertyById(propertyID);//TODO: maaaaybe handle integer properthies and byte and and...
			if (p != null) {
			    p.setValue(f);
			}
		    }
		}
	    }

	}
	);

    }

    private synchronized boolean isProcessingRequests() {
	return this.processingRequest;
    }

    private synchronized void setProcessingRequests(boolean sw) {
	this.processingRequest = sw;
    }

    public boolean offer(RequestNew rn) {
	boolean accepted = queue.offer(rn);
	if (accepted) {
	    //System.out.println("new request, adding to queue, which already has " + queue.size() + " elements in it!");
	    //System.out.println("REJECTED!");
	}
	if (!isProcessingRequests()) {
	    startProcessingRequests();
	}
	return accepted;

    }

    public void resolveRequest(RequestNew rn) {
	int requestType = rn.getData()[0];
	//System.out.println("should I send for resolution? hmm...");
	if (requestType < this.resolverList.size()) {
	    //System.out.println("Sending for resolution");
	    this.resolverList.get(requestType).resolve(rn);
	}
    }

    private void startProcessingRequests() {
	class RequestProcessor implements Runnable {

	    @Override
	    public void run() {
		setProcessingRequests(true);//we are now processing the requests;
		while (!queue.isEmpty()) {
		    RequestNew theRequest = queue.poll();
		    resolveRequest(theRequest);

		}
		setProcessingRequests(false);//we are not processing the requests any more;
	    }
	}
	if (isProcessingRequests()) {
	    return;
	}
	Thread t1 = new Thread(new RequestProcessor());
	t1.start();
    }

    public PacketHandler(GUIPanel gp) {
	this();
	this.gp = gp;
    }

    public void handlePacket(RequestNew rn) {
    }

}
