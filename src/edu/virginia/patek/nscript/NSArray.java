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

/** Holds the information about an array in nscript. An array is used in nscript
 *  to do repetitive tasks such as creating 100 TCP sessions, all with the same
 *  parameters. */
public class NSArray extends Object 
{
    /** The name of the array. */
    public String name;
    /** The number of instances that an array variable generates. */
    public int elements;

    /** Constructor that takes the name and size as parameters.
     *  @param inName the name of the array, which be listed in the object browser view. 
     *  @param inNumberOfElements is the size of the array. */
    public NSArray(String inName, int inNumberOfElements )
    {
	name = inName;
	elements = inNumberOfElements;
    }

    /** Used to store the array on a stream of characters. */
    public String toString()
    {
	return name + "\n" + Integer.toString(elements) + "\n";
    }
}
