package chartadvancedpie;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *@deprecated
 * @author thegoodhen
 */
//TODO: use generics for Factory, as in ModuleFactory extends ContainerFactory<Module>
public class ModuleFactory extends ContainerFactory {

	private Module mod;

	@Override
	//TODO: utilize generics in create to avoid code duplication in subclasses
	public Module create(byte[] xml, int index2) {

		setStartIndex(index2);
		this.setXml(xml);
		mod = new Module();
		this.setContainer(mod);
		parseXml();
		return mod;
	}

	@Override
	int resolveTag(int tagNumber) {
		switch (tagNumber) {
			case TAG_REGISTER:
				RegisterFactory rf = new RegisterFactory();
				Container newCont = rf.create(getXml(),getIndex());
				mod.addContainer(newCont);
				return ContainerFactory.getNewIndex();
			default:
				super.resolveTag(tagNumber);
				return ContainerFactory.getNewIndex();
		}
	}

	@Override
	int resolveVariable() {

		Variable v = VariableFactory.create(getXml(), getIndex());
		mod.addVariable(v);
		//System.out.println(v.toString());
		return VariableFactory.getNewIndex();
	}

}
