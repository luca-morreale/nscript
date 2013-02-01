/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package org.esseks.nscript;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A library is a collection of classes or TclSnippets, that can be used to
 * build simulation scripts. nscript has four default libraries: "Topology",
 * "Transport", "Application", "Utilities". The library class knows how to parse
 * itself from a file.
 */
public class TclLibrary extends Object implements Serializable {
    private static final long serialVersionUID = 42L;

    /**
     * The name of the library.
     */
    String name;
    /**
     * Internal name
     */
    String TBName;
    /**
     * The version number of the library.
     */
    String version;
    /**
     * The collection of TclSnippets.
     */
    ArrayList<TclSnippet> snippets;

    /**
     * Constructs itself from a buffer.
     *
     * @param br a buffer (file) from which the library parses itself.
     */
    public TclLibrary(BufferedReader br) {
        snippets = new ArrayList<TclSnippet>();
        readFromDisk(br);
    }

    /**
     * Constructs itself as an empty library. This was used for testing only,
     * not in use anymore.
     * @param libName
     * @param tbName
     */
    public TclLibrary(String libName, String tbName) {
        setName(libName);
        setTBName(tbName);
        snippets = new ArrayList<TclSnippet>();
    }

    /**
     * Default constructor to implement Serializable protocol.
     */
    private TclLibrary()  {
        this("", "");
    }

    /**
     * Get Library name as displayed in the toolbar.
     *
     * @return name of the Lib as displayed in the toolbar
     */
    public String getTBName() {
        return TBName;
    }

    private void setTBName(String newTBName) {
        TBName = newTBName;
    }

    /**
     * Library name getter.
     *
     * @return library name
     */
    public String getName() {
        return name;
    }

    private void setName(String newName) {
        name = newName;
    }

    /**
     * Get Library version.
     *
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get number of elements contained in the library.
     *
     * @return size
     */
    public int getSnippetCount() {
        return snippets.size();
    }

    /**
     * Get the snippet at a given index.
     *
     * @param index the index of the required snippet, zero based.
     * @return TclSnipper or null if not found
     */
    public TclSnippet getSnippet(int index) {
        if (index >= 0 && index < snippets.size()) {
            return snippets.get(index);
        } else {
            return null;
        }
    }

    /**
     * Get snippet by name. In case of multiple matches, only the first one
     * is returned.
     *
     * @param theName name of the snippet
     * @return TCLSnippet or null if not found
     */
    public TclSnippet getSnippet(String theName) {
        TclSnippet t;
        Iterator<TclSnippet> iter = snippets.iterator();
        while (iter.hasNext()) {
            t = iter.next();
            if (t.getName().equals(theName)) {
                return t;
            }
        }
        return null;
    }

    /**
     *
     * @param index
     * @param snippet
     */
    public void setSnippet(int index, TclSnippet snippet) {
        snippets.set(index, snippet);
    }

    /**
     * Add a snippet to the library.
     *
     * @param snippet the snippet to add
     */
    public void addSnippet(TclSnippet snippet) {
        snippets.add(snippet);
    }

    /**
     * Checks if a snippets with the same name is already registered.
     *
     * @param theName name to check against
     * @return true if the name is already owned by a registred snippet
     */
    public boolean nameExists(String theName) {
        TclSnippet t;
        Iterator<TclSnippet> iter = snippets.iterator();

        while (iter.hasNext()) {
            t = iter.next();
            if (t.getName().equals(theName)) {
                return true;
            }
        }
        return false;
    }

    private boolean readFromDisk(BufferedReader br) {
        try {
            name = br.readLine();
            TBName = br.readLine();
            version = br.readLine();

            while (readSnippet(br)) {}
        } catch (IOException ioe) {
            LOG.log(Level.WARNING, "{0}{1}", new Object[]{
                Messages.tr("library_read_error"), ioe.toString()});
            return false;
        }
        return true;
    }

    private boolean readSnippet(BufferedReader br) {
        TclSnippet t;
        StringBuilder s = new StringBuilder();
        String newLine;
        try {
            do {
                newLine = br.readLine();
                if (newLine == null) {
                    return false;
                } else {
                    s.append(newLine);
                }
            } while (newLine.indexOf("end") < 0);
        } catch (IOException ioe) {
            LOG.log(Level.WARNING, "{0}{1}", new Object[]{
                Messages.tr("snippet_read_error"), ioe.toString()});
        }
        t = new TclSnippet(s.toString());
        if (!nameExists(t.getName())) {
            addSnippet(t);
        } else {
            LOG.log(Level.WARNING, "{0}{1}", new Object[]{
                Messages.tr("snippet_already_in_library"), t.getName()});
        }
        return true;
    }

    /**
     * Gives a human readable representation of the Library.
     * Current format is "$name:$toolbarname:$version"
     *
     * @return String representation
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(snippets.size() + 1);
        int i;

        s.append(getName()).append(" : ").append(getTBName())
                .append(" : ").append(getVersion()).append("\n");
        for (i = 0; i < snippets.size(); i++) {
            s.append(snippets.get(i).toString()).append("\n");
        }
        return s.toString();
    }

    private static final Logger LOG = Logger.getLogger(TclLibrary.class.getName());
}
