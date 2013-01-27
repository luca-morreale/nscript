/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package edu.virginia.patek.nscript;

/**
 * Holds the information about an array in nscript. An array is used in nscript
 * to do repetitive tasks such as creating 100 TCP sessions, all with the same
 * parameters.
 */
public class NSArray extends Object {

    /**
     * The name of the array.
     */
    public String name;
    /**
     * The number of instances that an array variable generates.
     */
    public int elements;

    /**
     * Constructor that takes the name and size as parameters.
     *
     * @param inName the name of the array, which be listed in the object
     * browser view.
     * @param inNumberOfElements is the size of the array.
     */
    public NSArray(String inName, int inNumberOfElements) {
        name = inName;
        elements = inNumberOfElements;
    }

    /**
     * Used to store the array on a stream of characters.
     */
    @Override
    public String toString() {
        return name + "\n" + Integer.toString(elements) + "\n";
    }
}
