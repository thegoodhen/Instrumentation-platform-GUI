package chartadvancedpie;

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
@Deprecated
public class Module extends Container {

	@Override
	public String shortDesc() {
		return "Module";
	}

	public ArrayList<GUIelement> getGUIelements() {
		ArrayList<GUIelement> returnList = new ArrayList<>();
		for(Container c:getContainerList())
		{
			if(c instanceof Register)
			{
				returnList.addAll(((Register)c).getGUIelements());
			}
		}
		return returnList;
	}
}
