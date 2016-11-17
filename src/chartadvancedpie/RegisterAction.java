/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

/**
 *
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
