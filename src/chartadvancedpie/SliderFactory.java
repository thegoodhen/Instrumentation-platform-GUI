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
public class SliderFactory extends ContainerFactory {

	final static byte REG_NAME = 1;
	final static byte REG_UNIT = 2;
	final static byte REG_MIN = 3;
	final static byte REG_MAX = 4;
	final static byte REG_STEPS = 5;
	final static byte REG_MAXERR = 6;
	final static byte REG_UPDATERATE = 7;

	private GUISlider gs;

	@Override
	//TODO: utilize generics in create to avoid code duplication in subclasses
	public GUISlider create(byte[] xml, int index2) {

		setStartIndex(index2);
		this.setXml(xml);
		gs = new GUISlider();
		this.setContainer(gs);
		parseXml();
		return gs;
	}

	@Override
	int resolveTag(int tagNumber) {
		switch (tagNumber) {
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


		gs.addVariable(v);
		//System.out.println(v.toString());
		return VariableFactory.getNewIndex();
	}

}
