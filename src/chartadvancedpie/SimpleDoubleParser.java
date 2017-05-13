/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for parsing text containing multiple Doubles at once.
 * @author thegoodhen
 */
public class SimpleDoubleParser {
    /**
     * Parse doubles from a string; there are many supported formats of the input string:
     * 
     * 1 2 3 4
     * <br>
     * 1,3; 3,5; 2,8
     * <br>
     * 1.2 2.3 4.6
     * <br>
     * 1.1, 2.4, 5.6
     * <br>
     * 1,2 2,3 4,564
     * <br>
     * 1 000, 2 000, 3 000
     * <br>
     * 1,000,000; 2,000,000
     * <br>
     * 
     * 
     * @param s
     * @return 
     */
    public static ArrayList<Double> parseDoublesFromString(String s) {
	Matcher m1 = Pattern.compile("^([^0-9.,-])*(.*?)(([^0-9e.,-]))*$").matcher(s);
	String nonNumericCharsRegex = "[^0-9.,e-\\s]";

	String prefix = "";
	String body = "";
	String postfix = "";

	ArrayList<Double> returnList = new ArrayList<>();
	if (m1.find()) {
	    prefix = m1.group(1); //characters before the numbers themselves
	    body = m1.group(2); //the numbers
	    postfix = m1.group(3); //characters after the numbers
	}

	boolean containsDot = body.contains(".");
	boolean containsComma = body.contains(",");

	Matcher m = Pattern.compile(",\\s+").matcher(body);
	Matcher m2 = Pattern.compile("[^\\s,.e0-9-]").matcher(body);

	boolean containsCommaSpace = m.find();
	boolean containsOtherChars = m2.find();

	if (containsDot) { //1.2;2.3;3.4 or 1.2, 2.3, 3.4, 2e03, 150e3, 12, 28 or 1.2 something 2.3 something else - numbers with an optional decimal dot; separated by whatever
	    Matcher m3 = Pattern.compile("[0-9-]*\\.?[0-9]+e?[0-9]*").matcher(body);
	    while (m3.find()) {
		try {

		    returnList.add(Double.parseDouble(m3.group(0)));
		} catch (Exception e) {

		}
	    }
	} else {
	    if (containsComma) {
		if (containsCommaSpace) {
		    if (containsOtherChars) {// 1, 000; 2, 000; 
			body = body.replaceAll(",\\s*", "");
			Matcher m3 = Pattern.compile("[0-9-]*\\.?[0-9]+e?[0-9]*").matcher(body);
			while (m3.find()) {

			    try {

				returnList.add(Double.parseDouble(m3.group(0)));
			    } catch (Exception e) {

			    }
			}

		    } else { //1, 2, 3 or 1e02, 1e05, 2e03 - no decimal dots in the input; just a bunch of numbers separated by commas, followed by blank space, OR!! for fools: some guy actually gave us list of numbers using floating comma, separated by comma. Wow, what a dork.
			body = body.replaceAll(",\\s+", "xxx");
			body = body.replaceAll("\\s+", "");
			body = body.replaceAll(",", ".");
			Matcher m3 = Pattern.compile("-?[0-9]*\\.?[0-9]+e?[0-9]*").matcher(body);
			while (m3.find()) {
			    try {

				returnList.add(Double.parseDouble(m3.group(0)));
			    } catch (Exception e) {

			    }
			}
		    }
		} else //contains comma, but it's not followed by a space; doesn't contain any dots.
		{
		    if (containsOtherChars) {
			body = body.replaceAll(",", "");//remove all commas TODO: FIX BUG HERE
			Matcher m3 = Pattern.compile("-?[0-9]*e?[0-9]*").matcher(body);
			while (m3.find()) {
			    try {

				returnList.add(Double.parseDouble(m3.group(0)));
			    } catch (Exception e) {

			    }
			}
		    } else {

			Matcher m3 = Pattern.compile("\\s+").matcher(body);
			if (m3.find())//contains spaces, so it's like 1,2 3,4 5,6
			{

			    body = body.replaceAll(",", ".");//for the parser

			    Matcher m4 = Pattern.compile("-?[0-9]*\\.?[0-9]+e?[0-9]*").matcher(body);
			    while (m4.find()) {
				try {

				    returnList.add(Double.parseDouble(m4.group(0)));
				} catch (Exception e) {

				}
			    }
			} else//no spaces, no dots, no other characters, just numbers and commas, so it's like: 1,2,3,4,5,1e05,2e03
			{
			    Matcher m4 = Pattern.compile("[0-9-]+e?[0-9]*").matcher(body);
			    while (m4.find()) {
				try {

				    returnList.add(Double.parseDouble(m4.group(0)));
				} catch (Exception e) {

				}
			    }
			}
		    }
		}
	    }
	}
	return returnList;
    }
    
}
