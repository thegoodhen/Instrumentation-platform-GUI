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

/**
 * Class for managing properties of all the different GUI elements...
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

    

    public Color colorFromFloat(float source) {
	byte[] arr = ByteBuffer.allocate(4).putFloat(source).array();
	int r = arr[0] + 128;
	int g = arr[1] + 128;
	int b = arr[2] + 128;
	int o = arr[3] + 128;
	Color c = Color.rgb(r, g, b, o/255.0F);
	return c;
    }

    public float floatFromColor(Color source) {
	double d=source.getRed();
	byte r = (byte) ((source.getRed() * 255)-128);
	byte g = (byte) ((source.getGreen() * 255)-128);
	byte b = (byte) ((source.getBlue() * 255)-128);
	byte o = (byte) ((source.getOpacity() * 255)-128);
	byte[] arr = new byte[]{r, g, b, o};
	float f = ByteBuffer.wrap(arr).getFloat();
	return f;
    }

}
