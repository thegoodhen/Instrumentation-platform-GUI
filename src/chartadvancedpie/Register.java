package chartadvancedpie;

import static java.lang.Integer.min;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thegoodhen
 */
public class Register extends Container{
	private Variable name;
	private Variable unit;
	private Variable min;
	private Variable max;

final static int NAME=1;
final static int UNIT=2;
final static int MIN=3;
final static int MAX=4;
final static int STEPS=5;
final static int MAXERR=6;
final static int UPDATERATE=7;
final static int VALUE=8;


Variable getName()
{
	return this.getVariableList().get(NAME);
}

Variable getValue()
{
	return this.getVariableList().get(VALUE);
}

ArrayList<GUIelement> getGUIelements()
{
	ArrayList<GUIelement> returnList=new ArrayList<>();
	for(Container c:getContainerList())
	{
	    /*
		if (c instanceof GUIelement)
		{
			returnList.add((GUIelement)c);
		}
	    */
	}
	return returnList;
}

	void setName(Variable v) {
		this.name=v;
	}

	void setUnit(Variable v) {
		this.unit=v;
	}

	void setMin(Variable v) {
		this.min=v;
	}

	void setMax(Variable v) {
		this.max=v;
	}
	
@Override
public String shortDesc()
{
	return "Register";
}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
	sb.append("Begin Register\n" + "Name: ").append(getName().toString()).append("\n" + "Value: ").append(getValue().getDesc()).append("\n End Register\n");
		//sb.append(getVariablesString());
		sb.append(getContainersString());
		sb.append(shortDesc()).append(" end\n");
		return sb.toString();
	}
/*
@Override
public String toString()
{
	
	return "Begin Register\n"+"Name: "+getName().toString()+"\n"+"Value: "+getValue().toString()+"\n End Register";
}*/
}
