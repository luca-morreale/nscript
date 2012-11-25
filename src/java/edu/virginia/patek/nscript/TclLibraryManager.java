/* ----------------------------------------------------------------------------
 * NSCRIPT
 * version 1.0.2
 * Author:              Enrique Campos-Nanez
 * Contact information: Dr. Stephen D. Patek
 *                      Department of Systems and Information Engineering
 *                      101-C Olsson Hall
 *                      Charlottesville, VA 22904
 *                      University of Virginia
 *                      e-mail: patek@virginia.edu
 * ------------------------------------------------------------------------- */

package edu.virginia.patek.nscript;

import edu.virginia.patek.nscript.TclLibrary;
import edu.virginia.patek.nscript.TclSnippet;

import java.util.*;
import java.io.*;

/** Manages the set of opened libraries in the environment. Among other things,
 *  the TclLibraryManager is responsible for verifying unique TclSnippet names
 *  to prevent naming conflicts, verify uniqueness of library names, find a
 *  reference, and finding a snippet, either by name or entry number.
 */
public class TclLibraryManager extends Object {
  /** The collection of libraries. */
  ArrayList libraries;

  /** Default constructor, creates the container for libraries. */
  public TclLibraryManager()
  {
    libraries = new ArrayList();
  }

  /** Verifies if a given name is already being used for a snippet.
   *  @param snippetName the name of the snippet.
   *  @return true if the supplied name is being used by any opened snippet,
   *          false otherwise.
   */
  public boolean snippetNameExists(String snippetName)
  {
    TclLibrary l;
    Iterator i;

    i = libraries.iterator();
    while (i.hasNext()) {
      l = (TclLibrary) i.next();
      if (l.nameExists(snippetName))
        return true;
    }
    return false;
  }

  /** Verifies if a given library name is already loaded in the library.
   *  @param libName the name to verify.
   *  @return true if the supplied name is being taken by any of the
   *          opened libraries, false otherwise.
   */
  public boolean libNameExists(String libName)
  {
    TclLibrary l;
    Iterator i;

    i = libraries.iterator();
    while (i.hasNext()) {
      l = (TclLibrary) i.next();
      if (libName.equals(l.getName()))
        return true;
    }
    return false;
  }

  /** Adds a new library using the fileName. This method will try to
   *  opened the given file, and parse the library from it, creating a
   *  new library object, which then will be added to the library manager
   *  collection.
   *
   *  @param fileName the file location.
   *  @return true if the file was succesfully opened and library
   *          correctly parsed, false otherwise.
   */
  public boolean addLibrary( String fileName )
  {
    File f;
    FileReader fr;
    BufferedReader br;
    TclLibrary tl;
    int i;
    boolean go;

    try {
      f = new File(fileName);
      fr = new FileReader(f);
      br = new BufferedReader(fr);
      tl = new TclLibrary( br );
      go = true;
      go = !libNameExists(tl.getName());
      for (i=0; i<tl.getSnippetCount(); i++) {
        if (snippetNameExists(tl.getSnippet(i).getName()))
          go = false;
      }
      if (go) {
        libraries.add( tl );
        return true;
      } else
        return false;
    } catch (FileNotFoundException e) {
      System.out.println("Library file not found:"+e.toString());
      return false;
    }
  }

  /** Returns the number of opened libraries.
   *  @return the number of opened libraries. */
  public int getLibraryCount()
  {
    return libraries.size();
  }

  /** Obtains a library by its reference number.
   *  @param inIndex 0-based index of the library.
   *  @return a reference to the library. Note that if the index is incorrect
   *  NULL is returned. */
  public TclLibrary getLibrary( int inIndex )
  {
    if (inIndex >=0 && inIndex<libraries.size())
      return (TclLibrary)libraries.get(inIndex);
    else
      return null;
  }

  /** Obtains a library by its name.
   *  @param inLibName the name of the library.
   *  @return a reference to the library with the requested name,
   *          NULL if the name does not exist.
   */
  public TclLibrary getLibrary( String inLibName )
  {
    Iterator i;
    TclLibrary l;

    i = libraries.iterator();
    while (i.hasNext()) {
      l = (TclLibrary)i.next();
      if (inLibName.equals(l.getName()))
        return l;
    }
    return null;
  }

  /** Obtains a snippet by providing the reference to the library, and a
   *  reference index to the snippen within the library.
   *  @param inLibIndex the 0-based index of the library of interest.
   *  @param inSnippetIndex the 0-based index of the snippet within the
   *                        library.
   *  @return a reference to the requested snippet if the references are
   *          correct, NULL otherwise.
   */
  public TclSnippet getSnippet( int inLibIndex, int inSnippetIndex)
  {
    TclLibrary l;

    if (inLibIndex>=0 && inLibIndex<libraries.size()) {
      l = (TclLibrary)libraries.get(inLibIndex);
      if (inSnippetIndex>=0 && inSnippetIndex<l.getSnippetCount())
        return l.getSnippet(inSnippetIndex);
      else
        return null;
    } else {
      return null;
    }
  }

  /** Obtains a snippet by its name. The method will look in every opened
   *  library for a library, and return a reference to the requested
   *  snippet if found.
   *
   *  @param sName the name of the requested snippet.
   *  @return a reference to the snippet if the name is found, NULL otherwise.
   */
  public TclSnippet getSnippet( String sName )
  {
    Iterator i = libraries.iterator();
    TclLibrary l;
    TclSnippet s;

    while (i.hasNext()) {
      l = (TclLibrary)i.next();
      s = l.getSnippet(sName);
      if (s!=null)
        return s;
    }
    return null;
  }
}
