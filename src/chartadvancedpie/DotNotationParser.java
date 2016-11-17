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
 *
 * @author thegoodhen
 */
public class DotNotationParser extends ExpressionParser {

	@Override
	public ArrayList<Token> Tokenize(String s) {
		s=super.stripSpaces(s);
		Pattern argumentLessFunctionPattern=Pattern.compile("([A-Za-z0-9_]*[0-9]*)\\.([^.()]*)\\(\\)");
		Pattern argumentFunctionPattern=Pattern.compile("([A-Za-z0-9_]*[0-9]*)\\.([^.()]*)\\((.*)\\)");
		Matcher m1=argumentLessFunctionPattern.matcher(s);
		Matcher m2=argumentFunctionPattern.matcher(s);
		String replacement="";
		while(m1.find())
		{
			String GEName=m1.group(1);
			String GEFunction=m1.group(2);
			replacement=GEFunction+"(findGE(\""+GEName+"\"))";
			s=s.replaceAll("\\Q"+m1.group(0)+"\\E",replacement);
			m1=argumentLessFunctionPattern.matcher(s);
		}

		
		while(m2.find())
		{
			String GEName=m2.group(1);
			String GEFunction=m2.group(2);
			String GEFunctionParams=m2.group(3);
			replacement=GEFunction+"(findGE(\""+GEName+"\"), "+GEFunctionParams+")";
			replacement=removeTrailingParenthesis(replacement);//hotfix
			s=s.replaceAll("\\Q"+m2.group(0)+"\\E",replacement);
			m2=argumentFunctionPattern.matcher(s);
		}
		System.out.println("SENDING FOR TOKENIZATION: "+s);
			
		return super.Tokenize(s);
		

	}

	/**
	 * Java regex cannot match arbitrarily nested brackets - so we need this hotfix to match "too much" and then
	 * cut something away...
	 */
	private String removeTrailingParenthesis(String originalString)
		{
			int leftParenCount=0;
			int rightParenCount=0;
			int finalIndex=originalString.length();
			for(int i=0;i<originalString.length();i++)
			{
				if(originalString.charAt(i)=='(')
				{
					leftParenCount++;
				}
				if(originalString.charAt(i)==')')
				{
					rightParenCount++;
					if(rightParenCount>leftParenCount)
					{
						finalIndex=i;
						break;
					}
				}
			}
			return originalString.substring(0, finalIndex);
		}

}
