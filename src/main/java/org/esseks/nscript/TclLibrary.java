/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package org.esseks.nscript;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
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
    private String name;
    /**
     * Internal name
     */
    private String TBName;
    /**
     * The version number of the library.
     */
    private String version;
    /**
     * The collection of TclSnippets.
     */
    private ArrayList<TclSnippet> snippets;

    /**
     * Constructs itself from a buffer.
     *
     * @param br a buffer (file) from which the library parses itself.
     */
    public TclLibrary(BufferedReader br) {
        this.snippets = new ArrayList<TclSnippet>();
        this.readFromDisk(br);
    }

    /**
     * Constructs itself as an empty library. This was used for testing only,
     * not in use anymore.
     *
     * @param libName
     * @param tbName
     */
    public TclLibrary(String libName, String tbName) {
        this.setName(libName);
        this.setTBName(tbName);
        this.snippets = new ArrayList<TclSnippet>();
    }

    /**
     * Get Library name as displayed in the toolbar.
     *
     * @return name of the Lib as displayed in the toolbar
     */
    public String getTBName() {
        return this.TBName;
    }

    private void setTBName(String newTBName) {
        this.TBName = newTBName;
    }

    /**
     * Library name getter.
     *
     * @return library name
     */
    public String getName() {
        return this.name;
    }

    private void setName(String newName) {
        this.name = newName;
    }

    /**
     * Get Library version.
     *
     * @return version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Get number of elements contained in the library.
     *
     * @return size
     */
    public int getSnippetCount() {
        return this.snippets.size();
    }

    /**
     * Get the snippet at a given index.
     *
     * @param index the index of the required snippet, zero based.
     * @return TclSnipper or null if not found
     */
    public TclSnippet getSnippet(int index) {
        if ((index >= 0) && (index < this.snippets.size())) {
            return this.snippets.get(index);
        } else {
            return null;
        }
    }

    /**
     * Get snippet by name. In case of multiple matches, only the first one is
     * returned.
     *
     * @param theName name of the snippet
     * @return TCLSnippet or null if not found
     */
    public TclSnippet getSnippet(String theName) {
        TclSnippet t;
        Iterator<TclSnippet> iter = this.snippets.iterator();
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
        this.snippets.set(index, snippet);
    }

    /**
     * Add a snippet to the library.
     *
     * @param snippet the snippet to add
     */
    public void addSnippet(TclSnippet snippet) {
        this.snippets.add(snippet);
    }

    /**
     * Checks if a snippets with the same name is already registered.
     *
     * @param theName name to check against
     * @return true if the name is already owned by a registred snippet
     */
    public boolean nameExists(String theName) {
        TclSnippet t;
        Iterator<TclSnippet> iter = this.snippets.iterator();

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
            this.name = br.readLine();
            this.TBName = br.readLine();
            this.version = br.readLine();

            while (this.readSnippet(br)) {
            }
        } catch (IOException ioe) {
            LOG.log(Level.WARNING, "{0}{1}", new Object[]{
                        Messages.tr("library_read_error"), ioe.toString()});
            return false;
        }
        return true;
    }

    private boolean readSnippet(BufferedReader br) {
        TclSnippet t;
        StringBuilder s = new StringBuilder(10); //FIXME magic number
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
        if (!this.nameExists(t.getName())) {
            this.addSnippet(t);
        } else {
            LOG.log(Level.WARNING, "{0}{1}", new Object[]{
                        Messages.tr("snippet_already_in_library"), t.getName()});
        }
        return true;
    }

    /**
     * Gives a human readable representation of the Library. Current format is
     * "$name:$toolbarname:$version"
     *
     * @return String representation
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(this.snippets.size() + 1);
        int i;

        s.append(this.getName()).append(" : ").append(this.getTBName())
                .append(" : ").append(this.getVersion()).append("\n");
        for (i = 0; i < this.snippets.size(); i++) {
            s.append(this.snippets.get(i).toString()).append("\n");
        }
        return s.toString();
    }
    private static final Logger LOG = Logger.getLogger(TclLibrary.class.getName());
}
