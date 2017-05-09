/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *@deprecated 
 * Deprecated class, related to the old way GUI was loaded from the connected
 * child module
 * @author thegoodhen
 */
public class Container {

	ArrayList<Container> containerList;
	HashMap<Integer, Variable> variableList;

	public HashMap<Integer, Variable> getVariableList() {
		return this.variableList;
	}

	public ArrayList<Variable> getVariableListRecursive() {
		ArrayList<Variable> returnList = new ArrayList<Variable>();
		returnList.addAll(getVariableList().values());
		for (Container c : containerList) {
			returnList.addAll(c.getVariableListRecursive());
		}
		return returnList;
	}

	public ArrayList<Container> getContainerList() {
		return this.containerList;
	}

	public Container() {
		containerList = new ArrayList<>();
		variableList = new HashMap<>();
	}

	public void addContainer(Container c) {
		this.containerList.add(c);
	}

	public void addVariable(Variable v) {
		this.variableList.put(v.getNumber(), v);
	}

	public String shortDesc() {
		return "Unknown container";
	}

	public String getVariablesString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Variables stored inside:\n");
		for (Variable v : variableList.values()) {
			sb.append(v.getDesc()).append("\n");
		}

		if (variableList.isEmpty()) {
			sb.append("(NONE)\n");
		}
		return sb.toString();
	}

	public String getContainersString() {

		StringBuilder sb = new StringBuilder();
		sb.append("Containers stored inside:\n");
		for (Container c : containerList) {
			sb.append(c.toString()).append("\n");
		}
		if (containerList.isEmpty()) {
			sb.append("(NONE)\n");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(shortDesc()).append(" begin\n");
		sb.append(getVariablesString());
		sb.append(getContainersString());
		sb.append(shortDesc()).append(" end\n");
		return sb.toString();
	}

}
