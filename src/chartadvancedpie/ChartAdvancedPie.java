/*
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package chartadvancedpie;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * An advanced pie chart with a variety of actions and settable properties.
 *
 * @see javafx.scene.chart.PieChart
 * @see javafx.scene.chart.Chart
 */
public class ChartAdvancedPie extends Application{

	final byte BEGIN_TAG = 0;
	final byte BEGIN_TAG_EX = 1;
	final byte BEGIN_TAG_EX2 = 2;
	final byte END_TAG = 3;

	final byte TAG_PLATFORM = 0;
	final byte TAG_MODULE = 1;
	final byte TAG_HUB = 2;
	final byte TAG_CHANNELGROUP = 3;
	final byte TAG_REGISTER = 4;

	private final String xml = "kokodak";
	private byte[] xmlAsBytes = {BEGIN_TAG, TAG_MODULE, BEGIN_TAG, TAG_REGISTER, (byte) 184, 0b00010010, 86, 79, 76, 84, 83, 13, 0b01000100, (byte) 0b10000000, 0b00000010, BEGIN_TAG, GUIelement.SLIDER, END_TAG, END_TAG, END_TAG};
	private int index = 0;
	//private final int BEGIN_TAG = 0;
	//private final int END_TAG = 3;
	LinkedList<Integer> tagStack;

	public class HelloRunnable implements Runnable {

		private Module m;

		public HelloRunnable(Module m) {
			this.m = m;
		}

		public void run() {
			while (true) {
//TODO: optimize
				for (Variable v : m.getVariableListRecursive()) {
					v.postRequests();
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException ex) {
					Logger.getLogger(ChartAdvancedPie.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}

	}

	private void init(Stage primaryStage) {
		/*
		 Group root = new Group();
		 primaryStage.setScene(new Scene(root));
		 root.getChildren().add(createChart());
		 //xmlAsBytes=xml.getBytes(StandardCharsets.US_ASCII);

		 //System.out.println("Kokodak");
		 //System.out.println((int) ((byte) 184));
		 byte a = (byte) 184;
		 int b = a & 0xFF;
		 //System.out.println(b);
		 //Variable v = VariableFactory.create(xmlAsBytes, 0);
		 tagStack=new LinkedList<>();
		 //ContainerFactory cf=new ContainerFactory();
		 //Container c=cf.create(xmlAsBytes, -1);
		 */

		primaryStage.setTitle("Canvas Doodle Test");
		//Group root = new Group();

		// Draw background with gradient
		//Rectangle rect = new Rectangle(400, 400);
		//drawBackground(rect);
		///root.getChildren().add(rect);

		// Create the Canvas, filled in with Blue
		//reset(canvas, Color.BLUE);
		// Add the Canvas to the Scene, and show the Stage
		GUIPanel gp = new GUIPanel();

		SplitPane sp=new SplitPane();

		BorderPane root=new BorderPane(gp.getCanvasPane());
		sp.getItems().add(root);
		sp.getItems().add(gp.getVbox());
		sp.setOrientation(Orientation.VERTICAL);

		//root.getChildren().add(gp.getVbox());
		//root.setBottom(gp.getVbox());
		primaryStage.setResizable(true);
		primaryStage.setScene(new Scene(sp, 400, 400));
		primaryStage.show();

		ModuleFactory mf = new ModuleFactory();
		Module m = mf.create(xmlAsBytes, 1);
		ArrayList<GUIelement> elementList = m.getGUIelements();
		for (GUIelement ge : elementList) {
			gp.addGUIelement(ge);
			//System.out.println("KOKODAK"+ge.toString());
		}
		System.out.println(m.toString());

		/*
		Request.setGUIPanel(gp);
		try {
			//new Request(null);
			SerialCommunicatorObsolete sC = new SerialCommunicatorObsolete("COM3", 9600);
		Request.startComms(sC);
		} catch (Exception ex) {
			Logger.getLogger(ChartAdvancedPie.class.getName()).log(Level.SEVERE, null, ex);
		}
		//Request.startComms(null);
		Request.offerIfResolved(new SyncRequest());
		(new Thread(new HelloRunnable(m))).start();
		*/
		//parseXml();
		//System.out.println(v);
	}

	private void parseXml() {
		byte currentByte = 0;
		int currentTag = -1;
		for (index = 0; index < xmlAsBytes.length; index++) {
			currentByte = xmlAsBytes[index];
			if (isTagMark(currentByte)) {
				if (currentByte != END_TAG) {
					tagStack.push(currentTag);
					currentTag = processTag();
				} else//ending tag here
				{
					currentTag = tagStack.pop();
				}

			} else //not a tag, but a variable
			{
				Variable v = VariableFactory.create(xmlAsBytes, index);
				System.out.println(v.toString());
				index = VariableFactory.getNewIndex();
			}
		}
	}

	int processTag() {
		if (xmlAsBytes[index] == BEGIN_TAG) {
			index++;
			return xmlAsBytes[index];//TODO: implement extended tag, etc.

		}
		return -1;
	}

	/**
	 *
	 * @return whether the byte in question is a tag start or tag end mark
	 * or not
	 */
	private boolean isTagMark(byte mark) {
		return (mark & 0xFF) <= END_TAG;
	}

	protected PieChart createChart() {
		final PieChart pc = new PieChart(FXCollections.observableArrayList(
			new PieChart.Data("Sun", 20),
			new PieChart.Data("IBM", 12),
			new PieChart.Data("HP", 25),
			new PieChart.Data("Dell", 22),
			new PieChart.Data("Apple", 30)
		));
		// setup chart
		pc.setId("BasicPie");
		pc.setTitle("Pie Chart Example");
		return pc;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		init(primaryStage);
		primaryStage.show();
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX
	 * application. main() serves only as fallback in case the application
	 * can not be launched through deployment artifacts, e.g., in IDEs with
	 * limited FX support. NetBeans ignores main().
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
