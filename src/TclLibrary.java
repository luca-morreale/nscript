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

import java.io.*;
import java.util.*;
import TclSnippet;

/** A library is a collection of classes or TclSnippets, that can be used to build simulation
 *  scripts. nscript has four default libraries: "Topology", "Transport", "Application", "Utilities".
 *  The library class knows how to parse itself from a file. */
public class TclLibrary extends Object{
	/** The name of the library. */
    String name;
    /** Internal name */
    String TBName;
    /** The version number of the library. */
    String version;
    /** The collection of TclSnippets. */
    ArrayList snippets;

	/** Constructs itself from a buffer. 
	 *  @param br a buffer (file) from which the library parses itself. */
    public TclLibrary( BufferedReader br )
    {
	snippets = new ArrayList();
	readFromDisk(br);
    }
    
    /** Constructs itself as an empty library. This was used for testing only, not 
     *  in use anymore. */
    public TclLibrary( String libName, String tbName )
    {
	setName( libName );
	setTBName( tbName );
	snippets = new ArrayList();
    }

    public String getTBName() {
	return TBName;
    }

    public void setTBName(String  newTBName) {
	TBName = newTBName;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String  newName)
    {
	name = newName;
    }

    public String getVersion()
    {
	return version;
    }

    public int getSnippetCount()
    {
	return snippets.size();
    }

    public TclSnippet getSnippet( int index )
    {
	if (index>=0 && index<snippets.size())
	    return (TclSnippet) snippets.get(index);
	else
	    return null;
    }

    public TclSnippet getSnippet( String theName )
    {
	TclSnippet t;
	Iterator iter = snippets.iterator();
	while (iter.hasNext()) {
	    t = (TclSnippet) iter.next();
	    if (t.getName().equals(theName))
		return t;
	}
	return null;
    }

    public void setSnippet( int index, TclSnippet  snippet )
    {
	snippets.set( index, snippet );
    }

    public void addSnippet( TclSnippet snippet )
    {
	snippets . add( snippet );
    }

    public boolean nameExists( String theName )
    {
	TclSnippet t;
	Iterator iter = snippets.iterator();
	if (iter==null) return false;

	while (iter.hasNext()) {
	    t = (TclSnippet) iter.next();
	    if (t.getName().equals(theName))
		return true;
	}
	return false;
    }

    public boolean readFromDisk( BufferedReader br )
    {
	try {
	    name = br.readLine();
	    TBName = br.readLine();
	    version = br.readLine();
	    
	    while (readSnippet(br))
		;
	} catch (IOException ioe) {
	    System.out.println("Error reading library: "+ioe.toString());
	    return false;
	}
	return true;
    }

    public boolean readSnippet( BufferedReader br )
    {
	TclSnippet t;
	String s, newLine;
	s = "";
	try {
	    do {
		newLine = br.readLine();
		if (newLine==null)
		    return false;
		else
		    s = s+newLine;
	    } while (newLine.indexOf("end")<0);
	} catch (IOException ioe) {
	    System.out.println("Error reading: "+ioe.toString());
	}
	t = new TclSnippet(s);
      	if (!nameExists(t.getName()))
	    addSnippet(t);
	else
	    System.out.println("Error: snippet("+t.getName()+") already in library");
	return true;
    }

    public String toString()
    {
	String s;
	int i;

	s = getName() + " : " + getTBName() + " : " + getVersion() + "\n";
	for (i=0;i<snippets.size();i++) {
	    s = s + ((TclSnippet)snippets.get(i)).toString() + "\n";
	}
	return s;
    }
}// NSLibrary

