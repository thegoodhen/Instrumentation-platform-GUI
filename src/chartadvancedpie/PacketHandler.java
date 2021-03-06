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
import javafx.scene.paint.Color;
import shuntingyard.HelpByteMethods;

/**
 * Class for processing requests, sent by the module.
 * @author thegoodhen
 */
public class PacketHandler {

    GUIPanel gp;
    boolean processingRequest;
    LinkedBlockingQueue<RequestNew> queue = new LinkedBlockingQueue(10);//10 requests max
    ArrayList<Resolver> resolverList;

    /**
     * Abstraction of a Class for resolving a request.
     */
    private abstract class Resolver {

	public abstract void resolve(RequestNew rn);
    }

    public PacketHandler() {
	resolverList = new ArrayList<>();

	for (int i = 0; i < 30; i++) {
	    resolverList.add(null);
	}

	resolverList.add(0, new Resolver()//OK
	{
	    @Override
	    public void resolve(RequestNew rn) {
		gp.getSerialCommunicator().getWriter().informAboutProcessedRequest();
		System.out.println("Informing about processed request");
	    }
	}
	);//This is number 0

	resolverList.add(3, new Resolver() {
	    @Override
	    public void resolve(RequestNew rn) {
		System.out.println("new set req");
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
		for (int i = 1; i < data.length - 5; i += 6) {
		    byte guiElementID = (byte) data[i];
		    byte propertyID = (byte) data[i + 1];
		    byte fl1 = (byte) data[i + 2];
		    byte fl2 = (byte) data[i + 3];
		    byte fl3 = (byte) data[i + 4];
		    byte fl4 = (byte) data[i + 5];

		    byte[] theArr = {fl1, fl2, fl3, fl4};
		    float f = HelpByteMethods.constructFloat(theArr);
		    byte[] s2ndArr=HelpByteMethods.getFloatBytes(f);
		    System.out.println("guiElementID is: " + guiElementID);
		    System.out.println("propertyID is: " + propertyID);
		    if (propertyID == 7) {

			System.out.println("value is: " + f);
			for(byte b:theArr)
			{
			    System.out.println("kokodak prdel"+b);
			}
		    }
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

	resolverList.add(5, new Resolver() {
	    @Override
	    public void resolve(RequestNew rn) {
		System.out.println("new string set req");
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
		byte guiElementID = (byte) data[1];
		byte propertyID = (byte) data[2];
		StringBuilder sb = new StringBuilder();
		for (int i = 3; i < data.length; i++) {
		    char currentChar = (char) data[i];
		    if (currentChar == 0)//null terminator
		    {
			break;
		    }
		    sb.append(currentChar);

		}
		System.out.println(sb.toString() + "shall be saved to " + guiElementID + "into" + propertyID);
		GUIelement ge = gp.ID2GUIMap.get((int) guiElementID);
		if (ge != null) {
		    Property p = ge.getPropertyById(propertyID);//TODO: maaaaybe handle integer properthies and byte and and...
		    if (p != null) {
			if (p instanceof StringProperty) {
			    p.setValue(sb.toString());
			}
			//p.setValue(f);
		    }
		}
	    }

	}
	);

	resolverList.add(12, new Resolver()//add GUI element
	{
	    @Override
	    public void resolve(RequestNew rn) {
		int[] data = rn.getData();
		byte GUIType = (byte) data[1];
		byte ModuleGUIID = (byte) data[2];//ID assigned to the GUI element by the module

		GUITab gt = gp.getCurrentGUITab();
		//gt.insertGUIelement(gt.getFocusedGUIElementIndex(), gp.getCurrentRegisterContentAndReset(), false);
		GUIelement ge;

		switch (GUIType)//TODO: handle using reflection
		{
		    case 0://SLIDER
			ge = new GUISlider(gt);
			break;
		    case 1:// NUD
			ge = new GUINumericUpDown(gt);
			break;
		    case 2: //chkbox
			ge = new GUICheckBox(gt);
			break;
		    case 10:
			ge = new GUIDisplay(gt);
			break;
		    case 11:
			ge = new GUIStatsDisplay(gt);
			break;
		    case 12:
			ge = new GUIChart(gt);
			break;
		    case 20:
			ge = new GUITimer(gt);
			break;
		    case 21:
			ge = new GUIPID(gt);
			break;
		    default:
			ge = null;

		}

		if (ge != null) {
		    gt.addGUIelement(ge, gt.getFocusedGUIElementIndex());
		    ge.setModuleGUIID(ModuleGUIID);
		    int resultingID = gp.GUIIDMap.get(ge);
		    //ge.getPropertyByName("Color1").setValue(ColorManager.get().floatFromColor(Color.RED));
		    gp.getSerialCommunicator().getWriter().sendGUIElementRenumber(ModuleGUIID, (byte) resultingID);
		    System.out.println("type: " + GUIType + "ID: " + ModuleGUIID + ", assigned ID: " + resultingID);
		}
		//gp.getSerialCommunicator().getWriter().informAboutProcessedRequest();
	    }
	}
	);//This is number 12

	resolverList.add(20, new Resolver()//schedule automatic property update (the property should send its value to the module).
	{
	    @Override
	    public void resolve(RequestNew rn) {

		int[] data = rn.getData();
		byte GUIID = (byte) data[1];
		byte propertyID = (byte) data[2];//ID assigned to the GUI element by the module

		GUIelement ge = gp.ID2GUIMap.get((int) GUIID);
		Property p = ge.getPropertyById(propertyID);
		p.setIfIShouldUpdateToModule(true);

		System.out.println("Got request to inform about property change");
	    }
	}
	);//This is number 0

    }

    /**
     * 
     * @return Whether the thread, which is processing the requests is running
     * @see #offer(chartadvancedpie.RequestNew) 
     */
    private synchronized boolean isProcessingRequests() {
	return this.processingRequest;
    }

    /**
     * 
     * Set the flag, determining whether the thread, which is processing the requests is running.
     * This just controls the flag (true/false), but has no other effects.
     * Not intended to be called outside {@link #offer(chartadvancedpie.RequestNew) }
     * @see #offer(chartadvancedpie.RequestNew) 
     */
    private synchronized void setProcessingRequests(boolean sw) {
	this.processingRequest = sw;
    }

    /**
     * Offer a new {@link RequestNew} to be processed. 
     * Then start a new {@link Thread}, which processes all unprocessed requests and then exits, if such
     * a thread is not already running.
     * It may be rejected, if too many requests are waiting in the internal queue.
     * @param rn the {@link RequestNew} to be processed
     * @return true if accepted, false otherwise.
     */
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

    /**
     * Analyze the {@link RequestNew} provided, and decide, which
     * {@link Resolver} should be used to resolve it. Then resolve it, using
     * this {@link Resolver}.
     * @param rn the {@link RequestNew}, which should be resolved.
     */
    public void resolveRequest(RequestNew rn) {
	int requestType = rn.getData()[0];
	//System.out.println("should I send for resolution? hmm...");
	if (requestType < this.resolverList.size()) {
	    //System.out.println("Sending for resolution");
	    Resolver r = this.resolverList.get(requestType);
	    if (r != null) {
		r.resolve(rn);
	    }
	}
    }

    /**
     * Start a new {@link Thread}, which will resolve all {@link RequestNew}s
     * in the queue and exit.
     */
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
