package chartadvancedpie;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author thegoodhen
 */
//TODO: use generics for Factory, as in ModuleFactory extends ContainerFactory<Module>
public class RegisterFactory extends ContainerFactory {

	final static byte REG_NAME = 1;
	final static byte REG_UNIT = 2;
	final static byte REG_MIN = 3;
	final static byte REG_MAX = 4;
	final static byte REG_STEPS = 5;
	final static byte REG_MAXERR = 6;
	final static byte REG_UPDATERATE = 7;

	private Register reg;

	@Override
	//TODO: utilize generics in create to avoid code duplication in subclasses
	public Register create(byte[] xml, int index2) {

		setStartIndex(index2);
		this.setXml(xml);
		reg = new Register();
		this.setContainer(reg);
		parseXml();
		return reg;
	}

	@Override
	int resolveTag(int tagNumber) {
		switch (tagNumber) {
			case GUIelement.SLIDER:

				SliderFactory sf = new SliderFactory();
				GUISlider newCont = sf.create(getXml(), getIndex());
				newCont.setRegister(reg);
				newCont.subscribeToAll();
				reg.addContainer(newCont);
				return ContainerFactory.getNewIndex();
			default:
				super.resolveTag(tagNumber);
				return ContainerFactory.getNewIndex();
		}
	}

	//TODO: leave all the code in ContainerFactory. Then use generics. Implement getters and setters
	//For specific variables using Hashmaps.
	@Override
	int resolveVariable() {

		Variable v = VariableFactory.create(getXml(), getIndex());

		/*
		 switch(v.getNumber())
		 {
		 case REG_NAME:
		 reg.setName(v);
		 break;
		 case REG_UNIT:
		 reg.setUnit(v);
		 break;
		 case REG_MIN:
		 reg.setMin(v);
		 case REG_MAX:
		 reg.setMax(v);
		 break;
		 }
		 */
		reg.addVariable(v);
		//System.out.println(v.toString());
		return VariableFactory.getNewIndex();
	}

}
