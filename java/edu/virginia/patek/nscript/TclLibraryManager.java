/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package edu.virginia.patek.nscript;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the set of opened libraries in the environment. Among other things,
 * the TclLibraryManager is responsible for verifying unique TclSnippet names to
 * prevent naming conflicts, verify uniqueness of library names, find a
 * reference, and finding a snippet, either by name or entry number.
 */
public class TclLibraryManager extends Object implements Serializable {
    /** */
    public static final long serialVersionUID = 42L;

    /**
     * The collection of libraries.
     */
    ArrayList<TclLibrary> libraries;

    /**
     * Default constructor, creates the container for libraries.
     */
    public TclLibraryManager() {
        libraries = new ArrayList<TclLibrary>();
    }

    /**
     * Verifies if a given name is already being used for a snippet.
     *
     * @param snippetName the name of the snippet.
     * @return true if the supplied name is being used by any opened snippet,
     * false otherwise.
     */
    public boolean snippetNameExists(String snippetName) {
        TclLibrary l;
        Iterator<TclLibrary> i;

        i = libraries.iterator();
        while (i.hasNext()) {
            l = i.next();
            if (l.nameExists(snippetName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifies if a given library name is already loaded in the library.
     *
     * @param libName the name to verify.
     * @return true if the supplied name is being taken by any of the opened
     * libraries, false otherwise.
     */
    public boolean libNameExists(String libName) {
        TclLibrary l;
        Iterator<TclLibrary> i;

        i = libraries.iterator();
        while (i.hasNext()) {
            l = i.next();
            if (libName.equals(l.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a new library using the fileName. This method will try to opened the
     * given file, and parse the library from it, creating a new library object,
     * which then will be added to the library manager collection.
     *
     * @param fileName the file location.
     * @return true if the file was succesfully opened and library correctly
     * parsed, false otherwise.
     */
    public boolean addLibrary(String fileName) {
        File f;
        FileReader fr;
        BufferedReader br;
        TclLibrary tl;
        int i;
        boolean go;

        try {
            Reader reader = new InputStreamReader(
                    new FileInputStream(fileName), "utf-8");
            br = new BufferedReader(reader);
            tl = new TclLibrary(br);
            go = !libNameExists(tl.getName());
            for (i = 0; i < tl.getSnippetCount(); i++) {
                if (snippetNameExists(tl.getSnippet(i).getName())) {
                    go = false;
                }
            }
            if (go) {
                libraries.add(tl);
                return true;
            } else {
                return false;
            }
        } catch (FileNotFoundException e) {
            LOG.log(Level.WARNING, "{0}{1}", new Object[]{
                Messages.tr("library_not_found"), e.toString()});
            return false;
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "{0} {1}", new Object[]{
                        Messages.tr("file_read_error"), ioe.toString()});
            return false;
        }
    }

    /**
     * Returns the number of opened libraries.
     *
     * @return the number of opened libraries.
     */
    public int getLibraryCount() {
        return libraries.size();
    }

    /**
     * Obtains a library by its reference number.
     *
     * @param inIndex 0-based index of the library.
     * @return a reference to the library. Note that if the index is incorrect
     * NULL is returned.
     */
    public TclLibrary getLibrary(int inIndex) {
        if (inIndex >= 0 && inIndex < libraries.size()) {
            return libraries.get(inIndex);
        } else {
            return null;
        }
    }

    /**
     * Obtains a library by its name.
     *
     * @param inLibName the name of the library.
     * @return a reference to the library with the requested name, NULL if the
     * name does not exist.
     */
    public TclLibrary getLibrary(String inLibName) {
        Iterator<TclLibrary> i;
        TclLibrary l;

        i = libraries.iterator();
        while (i.hasNext()) {
            l = i.next();
            if (inLibName.equals(l.getName())) {
                return l;
            }
        }
        return null;
    }

    /**
     * Obtains a snippet by providing the reference to the library, and a
     * reference index to the snippen within the library.
     *
     * @param inLibIndex the 0-based index of the library of interest.
     * @param inSnippetIndex the 0-based index of the snippet within the
     * library.
     * @return a reference to the requested snippet if the references are
     * correct, NULL otherwise.
     */
    public TclSnippet getSnippet(int inLibIndex, int inSnippetIndex) {
        TclLibrary l;

        if (inLibIndex >= 0 && inLibIndex < libraries.size()) {
            l = libraries.get(inLibIndex);
            if (inSnippetIndex >= 0 && inSnippetIndex < l.getSnippetCount()) {
                return l.getSnippet(inSnippetIndex);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Obtains a snippet by its name. The method will look in every opened
     * library for a library, and return a reference to the requested snippet if
     * found.
     *
     * @param sName the name of the requested snippet.
     * @return a reference to the snippet if the name is found, NULL otherwise.
     */
    public TclSnippet getSnippet(String sName) {
        Iterator<TclLibrary> i = libraries.iterator();
        TclLibrary l;
        TclSnippet s;

        while (i.hasNext()) {
            l = i.next();
            s = l.getSnippet(sName);
            if (s != null) {
                return s;
            }
        }
        return null;
    }
    private static final Logger LOG = Logger.getLogger(TclLibraryManager.class.getName());
}
