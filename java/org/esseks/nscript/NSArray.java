/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package org.esseks.nscript;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Holds the information about an array in nscript. An array is used in nscript
 * to do repetitive tasks such as creating 100 TCP sessions, all with the same
 * parameters.
 */
public class NSArray extends Object implements Serializable {

    private static final long serialVersionUID = 42L;
    /**
     * The name of the array.
     */
    private String name;
    /**
     * The number of instances that an array variable generates.
     */
    private int elements;

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
     *
     * @return
     */
    @Override
    public String toString() {
        return name + "\n" + Integer.toString(elements) + "\n";
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public int getSize() {
        return elements;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @param elements
     */
    public void setSize(int elements) {
        this.elements = elements;
    }
    private static final Logger LOG = Logger.getLogger(NSArray.class.getName());
}
