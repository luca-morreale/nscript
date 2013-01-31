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
 * A library is a collection of classes or TclSnippets, that can be used to
 * build simulation scripts. nscript has four default libraries: "Topology",
 * "Transport", "Application", "Utilities". The library class knows how to parse
 * itself from a file.
 */
public class TclLibrary extends Object {

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
     */
    public TclLibrary(String libName, String tbName) {
        setName(libName);
        setTBName(tbName);
        snippets = new ArrayList<TclSnippet>();
    }

    public String getTBName() {
        return TBName;
    }

    private void setTBName(String newTBName) {
        TBName = newTBName;
    }

    public String getName() {
        return name;
    }

    private void setName(String newName) {
        name = newName;
    }

    public String getVersion() {
        return version;
    }

    public int getSnippetCount() {
        return snippets.size();
    }

    public TclSnippet getSnippet(int index) {
        if (index >= 0 && index < snippets.size()) {
            return snippets.get(index);
        } else {
            return null;
        }
    }

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

    public void setSnippet(int index, TclSnippet snippet) {
        snippets.set(index, snippet);
    }

    public void addSnippet(TclSnippet snippet) {
        snippets.add(snippet);
    }

    public boolean nameExists(String theName) {
        TclSnippet t;
        Iterator<TclSnippet> iter = snippets.iterator();
        if (iter == null) {
            return false;
        }

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

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
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
