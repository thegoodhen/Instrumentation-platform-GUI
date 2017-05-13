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
public class VariableString extends Variable{
	private String stringValue;
		public VariableString(boolean read, boolean write, int priority, byte addrRange, int address, int number)
		{
			super(read, write, priority, addrRange, address, number);
		}
	public void setString(String s)
	{
this.stringValue=s;
	}

@Override
public String getDesc()
{
		return "String: "+ this.stringValue+" "+super.getDesc();
}

	@Override
	public String toString()
	{
		return "String: "+ this.stringValue;//+" "+super.toString();
	}
}
