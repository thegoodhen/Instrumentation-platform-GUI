package chartadvancedpie;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * A helper class for searching and filtering between {@link GUIelement}s.
 * Whenever the user types in a query string, for instance to perform a
 * selection, there are multiple way the matching components can be used to
 * perform the selection. For instance, the {@link GUIelement}s that were
 * already selected in advance, but did not match this last search query can get
 * unselected, or remain selected, or something else. The way the elements that
 * matched the last search query will be used can be expressed by the public
 * constants, stored in this class:
 * 
* ADD_ON_MATCH ADD_ON_MISMATCH SUBTRACT_ON_MATCH SUBTRACT_ON_MISMATCH
 * SET_ON_MATCH SET_ON_MISMATCH
 *
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

    /**
     * @param setOperation
     * @return Whether the operation, expressed by one of the public constants
     * of this class is a mismatch operation.
     */
    public static boolean isMismatchOperation(int setOperation) {
	boolean[] boolArray = {false, true, false, true, false, true};
	if (setOperation >= 0 && setOperation < boolArray.length) {
	    return boolArray[setOperation];
	}
	return false;
    }

    /**
     * @param setOperation
     * @return Whether the operation, expressed by one of the public constants
     * of this class is an additive operation(select/show those that match,
     * dont change the attributes of the ones that don't match).
.
     */
    public static boolean isAdditiveOperation(int setOperation) {
	boolean[] boolArray = {true, true, false, false, false, false};
	if (setOperation >= 0 && setOperation < boolArray.length) {
	    return boolArray[setOperation];
	}
	return false;
    }

    /**
     * @param setOperation
     * @return Whether the operation, expressed by one of the public constants
     * of this class is a subtractive operation (unselect/hide those that match,
     * dont change the attributes of the ones that don't match).
     */
    public static boolean isSubtractiveOperation(int setOperation) {
	boolean[] boolArray = {false, false, true, true, false, false};
	if (setOperation >= 0 && setOperation < boolArray.length) {
	    return boolArray[setOperation];
	}
	return false;
    }

    /**
     * @param setOperation
     * @return Whether the operation, expressed by one of the public constants
     * of this class is a setting operation (unselect/hide all that dont match,
     * select/show all that match).
     */
    public static boolean isSettingOperation(int setOperation) {
	boolean[] boolArray = {false, false, false, false, true, true};//setting something means unsetting on mismatch
	if (setOperation >= 0 && setOperation < boolArray.length) {
	    return boolArray[setOperation];
	}
	return false;
    }

    /**
     * Turn a provided string into a fuzzy string regexp, by placing ".*"
     * between each 2 letters.
     *
     * @param regex
     * @return
     */
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
