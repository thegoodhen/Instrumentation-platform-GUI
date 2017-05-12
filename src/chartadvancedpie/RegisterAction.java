/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 *@deprecated 
 * Now deprecated class, related to the old way register access was handled.
 * Still used in some places of the code, but should be removed ASAP.
 * @author thegoodhen
 */
public class RegisterAction extends NamedGUIAction{

	public RegisterAction()
	{
		this("");
	}
	public RegisterAction(String name) {
		super(name);
	}

	public void doAction(String register)
	{

	}
	public void doAction(String register, IRepetitionCounter irc)
	{
		doAction(register);
	}

	public void doActionWithCounterHandling(String register, IRepetitionCounter irc)
	{
		doAction(register, irc);
		irc.resetRepeatCount();
	}

	
}
