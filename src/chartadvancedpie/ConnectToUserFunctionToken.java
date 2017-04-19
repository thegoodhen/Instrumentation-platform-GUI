/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import shuntingyard.AbstractBuiltInFunctionToken;
import shuntingyard.ByteNumberToken;
import shuntingyard.IntegerNumberToken;
import shuntingyard.VariableToken;
import shuntingyard.VirtualMachine;

/**
 *
 * @author thegoodhen
 */
public class ConnectToUserFunctionToken extends AbstractBuiltInFunctionToken {

    public ConnectToUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new ByteNumberToken("0"));//TODO: introduce void
	VariableToken param1 = new shuntingyard.VariableToken("text");
	param1.setType(new IntegerNumberToken());
	this.addArgument(param1);
    }

    @Override
    public void run(VirtualMachine vm) {
	int stringAddress = vm.popIntFromStack();

	String commPort = vm.fetchStringFromHeap(stringAddress);
	if (vm instanceof GUIVirtualMachine) {
	    //((GUIVirtualMachine) vm).getGUIPanel().showText(sb.toString() + "\n");
	    GUIPanel gp = ((GUIVirtualMachine) vm).getGUIPanel();

	    SerialCommunicator sc = new SerialCommunicator(gp);
	    gp.setSerialCommunicator(sc);

	    try {
		sc.connect(commPort);
		//Thread.sleep(5000);

		Timer t1 = new Timer();

		t1.schedule(new TimerTask() {
		    @Override
		    public void run() {
			sc.getWriter().sendInit();
		    }
		}, 3000);

		Timer t2 = new Timer();

		t2.schedule(new TimerTask() {
		    @Override
		    public void run() {
			sc.getWriter().sendInit2();
		    }
		}, 3500);

	    } catch (Exception ex) {
		Logger.getLogger(GUIPanel.class.getName()).log(Level.SEVERE, null, ex);
		gp.showError("ERROR connecting to: "+commPort+"! "+ex.getMessage());
	    }

	}
	vm.pushByteOnStack((byte) 0);//TODO: remove when we introduce void
    }

    @Override
    public byte getBaseCode() {
	return (byte) 129;
    }

}
