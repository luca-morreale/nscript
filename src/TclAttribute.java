/* --------------------------------------------------------------------------------------------
 * NSCRIPT
 * version 1.0.2 
 * Author: 		Enrique Campos-Nanez
 * Contact information: Dr. Stephen D. Patek
 *			Department of Systems and Information Engineering
 *            		101-C Olsson Hall
 *			Charlottesville, VA 22904
 * 			University of Virginia
 * 			e-mail: patek@virginia.edu
 * -------------------------------------------------------------------------------------------- */

import java.lang.*;

/** An attribute of a TclSnippet. */
public class TclAttribute extends Object
{
	/** The name of the attribute. */
    String name;
    /** The default value of the attribute. */
    String defaultValue; 
    /** The list available options, as a String. */
    String options;
    /** Flags if a class has a default value. */
    public boolean hasDefault;
    /** FLags if a class has a finite set of options. */
    public boolean hasOptions;
    
    /** Constructor takes a string and parses the information. 
     *  @param s a string, usually read from a file. */
    public TclAttribute( String s )
    {
	parseSelf(s);
    }

    // Access methods
    /** @return the name  of the attribute. */
    public String getName( )
    {
	return name;
    }
    
    /** @return the default value of the attribute. */
    public String getDefault()
    {
	return defaultValue;
    }

	/** @return the options from the attribute. */
    public String getOptions()
    {
	return options;
    }

	/** Procedure that parses a string looking for the 
	 *  attribute name, its default value, and (optionally) list of options. 
	 *  @param s the string containing the information. */
    void parseSelf( String s )
    {
	int defaultIndex, optionsIndex;

	defaultValue = "";
	options = "";

	s = s.trim();
	defaultIndex = s.indexOf('=');
	optionsIndex = s.indexOf(':');
	if (defaultIndex>=0) {
	    hasDefault = true;
	} else {
	    hasDefault = false;
	}
	if (optionsIndex>=0) {
	    hasOptions = true;
	} else {
	    hasOptions = false;
	}
	
	// Ok, now read
	if (hasDefault) {
	    name = s.substring(0,defaultIndex);
	    if (hasOptions) { 
		defaultValue = s.substring(defaultIndex+1,optionsIndex);
		options = s.substring(optionsIndex+1);
	    } else {
		
		defaultValue = s.substring(defaultIndex+1);
	    }
	} else {
	    if (hasOptions) {
		name = s.substring(0,optionsIndex);
		options = s.substring(optionsIndex+1);
	    } else {
		name = s;
	    }
	}
    }
    
    /** Puts the attribute information as a single string. */
    public String toString()
    {
	return name + " = " + defaultValue + " : " + options;
    }
}




