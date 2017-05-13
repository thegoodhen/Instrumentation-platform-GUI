/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import shuntingyard.AbstractBuiltInFunctionToken;
import shuntingyard.ByteNumberToken;
import shuntingyard.IntegerNumberToken;
import shuntingyard.VariableToken;
import shuntingyard.VirtualMachine;


/**
 * CLUC function for creating a new {@link GUISlider} {@link GUIElement}.
 * Has a single argument, which is the name of the new {@link GUIDisplay}.
 * It also places this new {@link GUIElement} on the current tab, under the 
 * currently focused one.
 * @see GUITab#getFocusedGUIElementIndex() 
 * @see GUITab#addGUIelement(chartadvancedpie.GUIelement, int) 
 * @author thegoodhen
 * @see GUIElement#getName()
 */
public class newSliderUserFunctionToken extends AbstractBuiltInFunctionToken {

    public newSliderUserFunctionToken(String tokenString) {
	super(tokenString);
	this.setReturnType(new ByteNumberToken("0"));//TODO: introduce void
	VariableToken param1 = new shuntingyard.VariableToken("text");
	param1.setType(new IntegerNumberToken());
	this.addArgument(param1);
    }

    @Override
    public void run(VirtualMachine vm) {
	int stringAddress = vm.popIntFromStack();
	String name = vm.fetchStringFromHeap(stringAddress);
	if (vm instanceof GUIVirtualMachine) {
	    GUIPanel gp = ((GUIVirtualMachine) vm).getGUIPanel();

	    GUITab gt = gp.getCurrentGUITab();
	    //gt.insertGUIelement(gt.getFocusedGUIElementIndex(), gp.getCurrentRegisterContentAndReset(), false);
	    GUISlider gs=new GUISlider(gt);
	    gs.setName(name);
	    gt.addGUIelement(gs, gt.getFocusedGUIElementIndex());
	}
	vm.pushByteOnStack((byte) 0);//TODO: remove when we introduce void
    }

    @Override
    public byte getBaseCode() {
	return (byte) 129;
    }

}
