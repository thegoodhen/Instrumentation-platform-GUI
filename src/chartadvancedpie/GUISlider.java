/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import shuntingyard.Token;

/**
 *
 * @author thegoodhen
 */
public class GUISlider extends GUIelement {

	public GUISlider() {

	}

	public GUISlider(GUITab gut) {
		super(gut);
		this.setGUIPanel(gut.getGUIPanel());

		//super(r);
		//actionMap.put("l", testAction);
		//actionMap.put("h", testAction2);
	}


	private class setValueFromTextAction extends NamedGUIAction {


		public setValueFromTextAction(String name) {
			super(name);
		}

		public void doAction() {
			getGUIPanel().getCmdLine().requestFocus();
			/*getGUIPanel().addCmdLineListener((observable, oldValue, newValue) -> {
				performSearch(modifyInputString(newValue));
			});*/
			getGUIPanel().enterPressAction = new NamedGUIAction("confirm query") {
				@Override
				public void doAction() {
					confirmSetValueAction();
				}
			};
			System.out.println("searching");

		}


		public void confirmSetValueAction() {
			String program="CGE.setValue("+getGUIPanel().getCmdLine().getText()+");\n";
			getGUIPanel().getGUICompiler().compile(program);
			ArrayList<Token> programList=getGUIPanel().getGUICompiler().getByteCodeAL();
			getGUIPanel().handleCallBack(programList);
		}

	}

	//private int value=50;
	public void setValue(int value)//TODO: change this to like IntegerProperty or something and make it generic! :3
	{
		super.setValue(value);
		super.update();
	}

	@Override
	public String shortDesc() {
		return "GUI element - slider";
	}

	@Override
	public void setGUITab(GUITab gut) {
		super.setGUITab(gut);

		NamedGUIAction testAction = new NamedGUIAction("increase value") {
			@Override
			public void doAction() {
				increaseValue(this.getCount());
			}
		};

		NamedGUIAction testAction2 = new NamedGUIAction("decrease value") {
			@Override
			public void doAction() {
				increaseValue(-this.getCount());
			}
		};

		RegisterAction yankAction = new RegisterAction("yank (copy)") {

			@Override
			public void doAction(String register) {
				if ((register.charAt(0) >= 'a' && register.charAt(0) <= 'z') || (register.charAt(0) >= 'A' && register.charAt(0) <= 'Z')) {
					GUISlider.this.getGUIPanel().setRegisterContent(register, Float.toString(GUISlider.this.getValue()));
				}
				//GUIPanel.this.setMark(register);
			}

			@Override
			public void doAction(String register, IRepetitionCounter irc) {
				doAction(register);
			}
		};

		NamedGUIAction unnamedRegisterYankAction = new NamedGUIAction("yank (copy)") {
			@Override
			public void doAction() {

				GUISlider.this.getGUIPanel().setRegisterContent("%", Float.toString(GUISlider.this.getValue()));
			}

			@Override
			public void doAction(IRepetitionCounter irc) {
				GUISlider.this.getGUIPanel().setRegisterContent("%", Float.toString(GUISlider.this.getValue()));
			}

		};

		NamedGUIAction unnamedRegisterPasteAction = new NamedGUIAction("paste") {
			@Override
			public void doAction() {

				String content = GUISlider.this.getGUIPanel().getRegisterContent("%");
				try {
					GUISlider.this.setValue((int) Float.parseFloat(content));
				} catch (Exception e) {

				}

			}

			@Override
			public void doAction(IRepetitionCounter irc) {
				doAction();
			}

		};

		RegisterAction pasteAction = new RegisterAction("paste") {
			@Override
			public void doAction(String register) {

				if ((register.charAt(0) >= 'a' && register.charAt(0) <= 'z') || (register.charAt(0) >= 'A' && register.charAt(0) <= 'Z')) {
					String content = GUISlider.this.getGUIPanel().getRegisterContent(register);
					try {
						GUISlider.this.setValue((int) Float.parseFloat(content));
					} catch (Exception e) {

					}
				}
			}

		};

		RegisterActionMenu ram = new RegisterActionMenu(this.getGUIPanel(), "kokodak");
		ram.addRegisterAction("y", yankAction);
		ram.addRegisterAction("p", pasteAction);
		Menu selectRegMenu = new RegisterSelectionMenu(GUISlider.this.getGUIPanel(), "access register", ram);

		NamedGUIAction gotoAction = new NamedGUIAction("value") {
			@Override
			public void doAction(IRepetitionCounter irc) {
				setValue(irc.getRepeatCount());
			}
		};

		this.setMenu(new Menu(gut.getGUIPanel(), "slider menu", true));
		this.getMenu().addAction("l", testAction);
		this.getMenu().addAction("h", testAction2);
		this.getMenu().addAction("c", new setValueFromTextAction("set value to"));
		this.getMenu().addAction("y", unnamedRegisterYankAction);
		this.getMenu().addAction("p", unnamedRegisterPasteAction);
		Menu gotoMenu = new Menu(this.getGUIPanel(), "set", false);
		this.getMenu().addSubMenu("g", gotoMenu);
		this.getMenu().addSubMenu("\"", selectRegMenu);
		gotoMenu.addAction("g", gotoAction);
		ram.setSuperMenu(this.getMenu());
	}

	public String getName() {
		return "gs";
	}

	public void increaseValue(float step)
	{
		this.setValue(this.getValue()+step);
		super.update();
	}

	public void increaseValue(boolean forward, boolean fast) {
		byte increase = 1;
		if (fast) {
			increase = 10;
		}
		if (!forward) {
			increase *= -1;
		}
		this.setValue(this.getValue() + increase);
		super.update();
	}

	@Override
	public String getGUIelementName() {
		return "SLIDER";
	}

	public void paint(GraphicsContext gc, double x, double y) {
		super.paint(gc, x, y);
		gc.setFill(Color.BROWN);
		/*
		 if (getRegister() != null) {
		 gc.strokeText(getRegister().getName().toString(), x, y + 20);
		 }
		 */
		gc.fillRect(x, y, 100, this.getHeight());
		gc.setFill(Color.WHITE);
		gc.fillRect(x, y, this.getValue(), this.getHeight());
	}

	@Override
	public GUIelement makeCopy() {
		GUISlider cb = new GUISlider();
		this.copyPropertiesTo(cb);
		return cb;
	}
}
