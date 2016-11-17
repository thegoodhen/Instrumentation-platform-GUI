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
public class RegexUtils {

	public static final int ADD_ON_MATCH = 0;
	public static final int ADD_ON_MISMATCH = 1;
	public static final int SUBTRACT_ON_MATCH = 2;
	public static final int SUBTRACT_ON_MISMATCH = 3;
	public static final int SET_ON_MATCH = 4;
	public static final int SET_ON_MISMATCH = 5;

	public static String makeRegexLiteral(String regex) {
		return "\\Q" + regex + "\\E";
	}

	public static boolean isMismatchOperation(int setOperation) {
		boolean[] boolArray = {false, true, false, true, false, true};
		if (setOperation >= 0 && setOperation < boolArray.length) {
			return boolArray[setOperation];
		}
		return false;
	}

	public static boolean isAdditiveOperation(int setOperation) {
		boolean[] boolArray = {true, true, false, false, false, false};
		if (setOperation >= 0 && setOperation < boolArray.length) {
			return boolArray[setOperation];
		}
		return false;
	}

	public static boolean isSubtractiveOperation(int setOperation) {
		boolean[] boolArray = {false, false, true, true, false , false};
		if (setOperation >= 0 && setOperation < boolArray.length) {
			return boolArray[setOperation];
		}
		return false;
	}

	public static boolean isSettingOperation(int setOperation) {
		boolean[] boolArray = {false, false, false, false, true, true};//setting something means unsetting on mismatch
		if (setOperation >= 0 && setOperation < boolArray.length) {
			return boolArray[setOperation];
		}
		return false;
	}

	public static String makeStringFuzzy(String regex) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < regex.length(); i++) {
			sb.append(".*");
			sb.append("\\Q");
			sb.append(regex.charAt(i));
			sb.append("\\E");
			sb.append(".*");
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

}
