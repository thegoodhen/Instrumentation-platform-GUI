/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartadvancedpie;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import shuntingyard.ExpressionParser;
import shuntingyard.Token;

/**
 * Extended expression parser that uses a fun little hack to support dot
 * notation on precompiler level, that is, calling OBJECT.method();
 *
 * @author thegoodhen
 */
public class DotNotationParser extends ExpressionParser {

    @Override
    public ArrayList<Token> Tokenize(String s) {
	s = super.stripSpaces(s);
	Pattern argumentLessFunctionPattern = Pattern.compile("([A-Za-z0-9_]*[0-9]*)\\.([^.()]*)\\(\\)");
	Pattern argumentFunctionPattern = Pattern.compile("([A-Za-z0-9_]*[0-9]*)\\.([^.()]*)\\((.*)\\)");
	Matcher m1 = argumentLessFunctionPattern.matcher(s);
	Matcher m2 = argumentFunctionPattern.matcher(s);
	String replacement = "";
	while (m1.find()) {
	    String GEName = m1.group(1);
	    String GEFunction = m1.group(2);
	    replacement = GEFunction + "(findGE(\"" + GEName + "\"))";
	    s = s.replaceAll("\\Q" + m1.group(0) + "\\E", replacement);
	    m1 = argumentLessFunctionPattern.matcher(s);
	}

	while (m2.find()) {
	    String GEName = m2.group(1);
	    String GEFunction = m2.group(2);
	    String GEFunctionParams = m2.group(3);
	    replacement = GEFunction + "(findGE(\"" + GEName + "\"), " + GEFunctionParams + ")";
	    String wholeCall=m2.group(0);
	    wholeCall=removeTrailingParenthesis(wholeCall);
	    replacement = removeTrailingParenthesis(replacement);//hotfix
	    s = s.replaceAll("\\Q" + wholeCall + "\\E", replacement);//woah, this will be buggy! TODO: FIX THIS! NOW! This will fail when the same method is called multiple times on one line!
	    m2 = argumentFunctionPattern.matcher(s);
	}

	//Now let's handle the "getProperty" and "setProperty", where "Property" is something generic...
	Pattern getPropertyPattern = Pattern.compile("get([A-Z][a-zA-Z0-9]*)\\((.*)\\)");
	Pattern setPropertyPattern = Pattern.compile("set([A-Z][a-zA-Z0-9]*)\\((.*)\\)");
	String getterCallString = "";
	String setterCallString = "";
	String propertyName = "";
	String arguments = "";
	Matcher getPropertyMatcher = getPropertyPattern.matcher(s);
	Matcher setPropertyMatcher = setPropertyPattern.matcher(s);

	while (getPropertyMatcher.find()) {
	    getterCallString = this.removeTrailingParenthesis(getPropertyMatcher.group(0));
	    propertyName = getPropertyMatcher.group(1);
	    if (propertyName.contains("Property"))//the thingy is "getProperty" already
	    {
		continue;
	    } else {
		arguments = this.removeTrailingParenthesis(getPropertyMatcher.group(2));
		String typeString = PropertyManager.get().getPropertyTypeString(propertyName);

		if (typeString == null) {
		    System.err.println("unknown property: " + propertyName);
		}
		replacement = "get" + typeString + "Property(" + arguments + ", getPropertyIdByName(\"" + propertyName + "\"))";
		s = s.replaceFirst("\\Q" + getterCallString + "\\E", replacement);
		getPropertyMatcher = getPropertyPattern.matcher(s);
	    }
	}

	while (setPropertyMatcher.find()) {
	    setterCallString = this.removeTrailingParenthesis(setPropertyMatcher.group(0));
	    propertyName = setPropertyMatcher.group(1);
	    if (propertyName.contains("Property"))//the thingy is "getProperty" already
	    {
		continue;
	    } else {
		String slepice = setPropertyMatcher.group(2);
		arguments = this.removeTrailingParenthesis(setPropertyMatcher.group(2));
		String typeString = PropertyManager.get().getPropertyTypeString(propertyName);

		if (typeString == null) {
		    System.err.println("unknown property: " + propertyName);
		}
		replacement = "set" + typeString + "Property(" + arguments + ", getPropertyIdByName(\"" + propertyName + "\"))";
		s = s.replaceFirst("\\Q" + setterCallString + "\\E", replacement);
		setPropertyMatcher = setPropertyPattern.matcher(s);
	    }
	}

	System.out.println(
		"SENDING FOR TOKENIZATION: " + s);

	return super.Tokenize(s);

    }

    /**
     * Java regex cannot match arbitrarily nested brackets - so we need this
     * hotfix to match "too much" and then cut something away...
     */
    private String removeTrailingParenthesis(String originalString) {
	int leftParenCount = 0;
	int rightParenCount = 0;
	int finalIndex = originalString.length();
	for (int i = 0; i < originalString.length(); i++) {
	    if (originalString.charAt(i) == '(') {
		leftParenCount++;
	    }
	    if (originalString.charAt(i) == ')') {
		rightParenCount++;
		if (rightParenCount > leftParenCount) {
		    finalIndex=i;
		    break;
		}
	    }
	}
	if(finalIndex==originalString.length()+1)
	{
	    finalIndex+=1;
	}
	return originalString.substring(0, finalIndex);
    }

}
