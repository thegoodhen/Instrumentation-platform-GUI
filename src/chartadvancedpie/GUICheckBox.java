/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
/**
 *
 * @author thegoodhen
 */
public class GUICheckBox extends GUIelement{

public GUICheckBox()
{

}

	public GUICheckBox(GUITab gut)
	{
		super(gut);
		//super(r);

		//actionMap.put("l", testAction);
		//actionMap.put("h", testAction2);
	}

	//private int value=50;
@Override
	public void setValue(float value)//TODO: change this to like IntegerProperty or something and make it generic! :3
	{
		super.setValue(value);
		super.update();
	}

@Override
public String shortDesc()
{
	return "GUI element - slider";
}


@Override
public void setGUIPanel(GUIPanel gup)
{
super.setGUIPanel(gup);

		NamedGUIAction testAction = new NamedGUIAction("increase value") {
			@Override
			public void doAction() {
				increaseValue(true,false);
			}
		};

		NamedGUIAction testAction2 = new NamedGUIAction("decrease value") {
			@Override
			public void doAction() {
				increaseValue(false,false);
			}
		};

		this.setMenu(new Menu(gup,"slider menu",true));
		this.getMenu().addAction("l", testAction);
		this.getMenu().addAction("h", testAction2);
}

public void increaseValue(boolean forward, boolean fast)
{
	byte increase=1;
	if(fast)
	{
		increase=10;
	}
	if(!forward)
	{
		increase*=-1;
	}
	this.setValue(this.getValue()+increase);
		super.update();
}
	public void paint(GraphicsContext gc, double x, double y)
	{
		super.paint(gc,x,y);
		gc.setFill(Color.GREENYELLOW);
		if(getRegister()!=null)
		{
			gc.strokeText(getRegister().getName().toString(), x, y+20);
		}
		gc.fillRect(x, y, 10, 10);
		gc.setFill(Color.RED);
		gc.fillRect(x+2, y+2,6, 6);
	}

	@Override
	public GUIelement makeCopy() {
		GUICheckBox cb=new GUICheckBox();
		this.copyPropertiesTo(cb);
		return cb;
	}
}
