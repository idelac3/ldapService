package hr.ericsson.pegasus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <H1>Get options</H1>
 * <HR>
 * This class should read <I>args</I> argument from <I>public static void main(String args[])</I><BR>
 * and build a table with switches and values. Also, it should take care of arguments (those elements which
 * do not start with - (dash).<BR>
 * <BR>
 * <HR>
 * @author eigorde
 *
 */
public class GetOpts {

    /**
     * Switch list, with their values.
     */
    private HashMap<String, String> paramTable = new HashMap<String, String>();

    /**
     * Argument list contains all arguments that are not a switch. 
     */
    private List<String> argList = new ArrayList<String>();
    
    /**
     * Build a list of arguments and switches.<BR>
     * Eg.<BR>
     * <I>GetOpts op = new GetOpts(args); </I><BR>
     * <B>NOTE:</B><BR>
     * Argument list should precede switches. Eg.<BR>
     * <I>java -jar test.jar arg1 arg2 -sw1 value1 -sw2 value2 ...</I><BR>
     * It is wrong to use it this way:<BR>
     * <I>java -jar test.jar -sw1 value1 -sw2 value2 arg1 arg2 ...</I><BR>
     * @param args should be passed from <I>main()</I> function.
     */
    public GetOpts(String[] args) {
        /*
         * Number of elements.
         */
        int argc = args.length;        
                
        /*
         * Check that there is at lease one element.
         */
        if (argc > 0) {
            for(int index = 0; index < argc; index++) {
                /*
                 * Current and next elements.
                 */
                String currentEl = "", nextEl = "";
                currentEl = args[index];
                if (index < argc - 1)
                    nextEl = args[index + 1];
                
                /*
                 * Is it a switch ?
                 */
                if (currentEl.startsWith("-")) {
                    /*
                     * Is next element a switch, empty, or something else ?
                     */
                    if (nextEl.startsWith("-")) {
                        paramTable.put(currentEl, "");
                    }
                    else if (nextEl.isEmpty()) {                    
                        paramTable.put(currentEl, "");
                    }
                    else {
                        paramTable.put(currentEl, nextEl);
                        index++;
                    }
                }
                else {
                    argList.add(currentEl);
                }
                
            }
        }
    }


    /**
     * Return switch value.<BR>
     * <B>NOTE:</B>An empty string is returned if switch does not exist or has no value.
     * @param switchName is the name of the switch. Eg. <I>-r</I>
     * @return switch value.
     */
    public String getSwitch(String switchName) {
        String retVal = "";
        if (paramTable.containsKey(switchName))
            retVal = paramTable.get(switchName);
            
        return retVal;
    }
    
    /**
     * Check for the existence of switch.
     * @param switchName is the name of the switch. Eg. <I>-r</I>
     * @return <I>true</I> if switch named <I>switchName</I> exist.
     */
    public boolean isSwitch(String switchName) {
        return paramTable.containsKey(switchName);
    }
    
    /**
     * Get argument list.
     * @return array of arguments.
     */
    public String[] getArguments() {
        String[] argListArray = argList.toArray(new String[argList.size()]);        
        return argListArray;
    }
    
    /**
     * Get switch list.
     * @return array of switches which could be later used for getting switch values.
     */
    public String[] getSwitches() {
        String[] switchListArray = paramTable.keySet().toArray(new String[paramTable.size()]);        
        return switchListArray;
    }
}
