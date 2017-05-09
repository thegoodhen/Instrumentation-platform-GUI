/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.paint.Color;
import shuntingyard.HelpByteMethods;

/**
 * Utility class for managing colors. Implemented as a singleton.
 *
 * @author thegoodhen
 */
public class ColorManager {

    private static ArrayList<GUIelement> elementList = new ArrayList<>();
    private static boolean setup = false;
    private static ColorManager pm = null;
    final int GUI_NAME = 0;
    final int GUI_FUNCTION = 1;
    final int GUI_CALLBACK = 2;
    int currentMode = GUI_NAME;

    private ColorManager() {

    }

    /**
     * Singleton design pattern getter.
     * @return 
     */
    public static ColorManager get() {

	if (pm == null) {
	    pm=new ColorManager();
	    Color c=Color.rgb(255, 10,0, 1);
	    float f=pm.floatFromColor(c);
	    Color c2=pm.colorFromFloat(f);
	    return pm;

	} else {
	    return pm;
	}


    }

    /**
     * Returns a color with full brightness and saturation, with the hue being
     * n*fibbonaccis angle (~137.5°). This should ensure that the colors are
     * as distinct as possible for growing n.
     * @param n
     * @return Color with brightness=1, saturation=1 and hue=~n*137.5.
     */
    public Color getNthColor(int n)
    {
	final double fibboAngle=137.50776405003784;
	double hue=(fibboAngle*n)%360;
	return Color.hsb(hue, 1, 1);
    }

    

    /**
     * Creates and returns a Color, given a float. It takes the first byte
     * of the said float, interprets it as a number from 0 to 255, and sets
     * the red channel of the resulting color to this number. Then it does the same 
     * thing for the green, blue and alpha channels, in this order.
     * @param source
     * @return 
     */
    public Color colorFromFloat(float source) {
	//byte[] arr = ByteBuffer.allocate(4).putFloat(source).array();
	byte [] arr=HelpByteMethods.getFloatBytes(source);
	int r = arr[0] + 128;
	int g = arr[1] + 128;
	int b = arr[2] + 128;
	int o = arr[3] + 128;

	//System.out.println("r:"+r+";g:"+g+"b:"+g);
	Color c = Color.rgb(r, g, b, o/255.0F);
	return c;
    }


    /**
     * Method for generating a float from Color. It's compatible with the
     * colorFromFloat method (and does the exact opposite thing).
     * Warning: This being said, colorFromFloat(floatFromColor(c)) where c is some
     * color, may not exactly equal c, due to the finite precision of floats
     * and algorithms used.
     * 
     * @see colorFromFloat
     * @param source
     * @return 
     */
    public float floatFromColor(Color source) {
	double d=source.getRed();
	byte r = (byte) ((source.getRed() * 255)-128);
	byte g = (byte) ((source.getGreen() * 255)-128);
	byte b = (byte) ((source.getBlue() * 255)-128);
	byte o = (byte) ((source.getOpacity() * 255)-128);
	byte[] arr = new byte[]{r, g, b, o};
	float f = HelpByteMethods.constructFloat(arr);//ByteBuffer.wrap(arr).getFloat();
	return f;
    }

}
