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
 * Defines the basic behavior of an object that will be part of a simulation
 * script.
 */
public class NSObject extends Object {

    /**
     * The snippet is basically the Class of the object. It contains the
     * information that defines the object.
     */
    TclSnippet snippet;
    /**
     * The name of the object.
     */
    String name;
    /**
     * Attributes of the current object.
     */
    ArrayList<String> attributes;
    /**
     * An object can be indexed by an array. arrayIndex stores a reference to
     * this array, or -1 if no array is in use.
     */
    int arrayIndex;

    /**
     * Only constructor that requires a name and a class definition (Snippet).
     */
    public NSObject(TclSnippet inSnippet, String inName) {
        snippet = inSnippet;

        // Initialize the objects data structure
        attributes = new ArrayList<String>();
        arrayIndex = -1;
        this.name = inName;
    }

    /**
     * Returns the class definition snippet for this object.
     *
     * @return a reference to the TclSnippet object.
     */
    public TclSnippet getSnippet() {
        return snippet;
    }

    /**
     * Changes the name of the object.
     *
     * @param inName the new name for the object.
     */
    public void setName(String inName) {
        name = inName;
    }

    /**
     * Obtains the current name for the object.
     *
     * @return the name of the object as a string.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the index of the array that indexes the current object.
     *
     * @return a number in 0..(numberOfIndices), or -1 if object is not indexed
     * by any array (default value).
     */
    public int getArrayIndex() {
        return arrayIndex;
    }

    /**
     * Changes the array reference for the object.
     *
     * @param newArrayIndex the (new) number of the array that indices.
     */
    public void setArrayIndex(int newArrayIndex) {
        arrayIndex = newArrayIndex;
    }

    /**
     * Returns the number of attributes that a given object has.
     *
     * @return the array index.
     */
    public int getAttributeCount() {
        return attributes.size();
    }

    /**
     * These method is used when the user erases an array, using the 'Edit
     * array...' option. If the array of the index being erased (aig) is being
     * used by the object its index should be reset to -1. Otherwise if the
     * index is adjusted so that the object does not lose track of its index.
     *
     * @param aig the index that was erased.
     */
    public void arrayIndexGone(int aig) {
        if (arrayIndex > aig) {
            arrayIndex--;
        } else if (arrayIndex == aig) {
            arrayIndex = -1;
        }
    }

    /**
     * Sets the value of an attribute stored at a given position.
     *
     * @param inAttrIndex the index of the attribute being edited.
     * @param inNewValue a String containing the new value.
     */
    public void setAttribute(int inAttrIndex, String inNewValue) {
        if (inAttrIndex >= attributes.size()) {
            attributes.add(inAttrIndex, inNewValue);
        } else {
            attributes.set(inAttrIndex, inNewValue);
        }
    }

    /**
     * Returns the value of the attribute stored at a given position.
     *
     * @param inAttrIndex the attribute index.
     * @return a String with the value of the attribute, if the index is valid,
     * and an empty String otherwise.
     */
    public String getAttribute(int inAttrIndex) {
        if (inAttrIndex >= attributes.size()) {
            return "";
        } else {
            return attributes.get(inAttrIndex);
        }
    }

    /**
     * Converts the object to a string. This option is used to store the object
     * to disk.
     *
     * @return the string containing the representation of the object as a
     * string.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        str.append(snippet.getName()).append("\n")
                .append(name).append("\n")
                .append(Integer.toString(arrayIndex)).append("\n");

        for (int i = 0; i < attributes.size(); i++) {
            str.append(attributes.get(i)).append("\n");
        }
        return str.toString();
    }

    /**
     * Reads the attributes for the object from an input stream.
     *
     * @param br a BufferedReader object from which the values are read.
     */
    public void fromString(BufferedReader br) {
        try {
            setArrayIndex(Integer.parseInt(br.readLine()));
            for (int i = 0; i < attributes.size(); i++) {
                setAttribute(i, br.readLine());
            }
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "{0}{1}", new Object[]{
                Messages.tr("reading_object_error"),
                ioe.toString()
            });
        }
    }
    private static final Logger LOG = Logger.getLogger(NSObject.class.getName());
}
