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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Defines the basic behavior of an object that will be part of a simulation
 * script.
 */
public class NSObject extends Object implements Serializable, Comparable<NSObject> {

    /**
     *      */
    public static final long serialVersionUID = 42L;
    /**
     * The snippet is basically the Class of the object. It contains the
     * information that defines the object.
     */
    private TclSnippet snippet;
    /**
     * The name of the object.
     */
    private String name;
    /**
     * Attributes of the current object.
     */
    private ArrayList<String> attributes;
    /**
     * An object can be indexed by an array. arrayIndex stores a reference to
     * this array, or -1 if no array is in use.
     */
    private int arrayIndex;
    
    /**
     * Precedence to print neatly the tcl script
     */
    private int precedence;

    /**
     * Only constructor that requires a name and a class definition (Snippet).
     *
     * @param inSnippet
     * @param inName
     */
    public NSObject(TclSnippet inSnippet, String inName) {
        this.snippet = inSnippet;

        // Initialize the objects data structure
        this.attributes = new ArrayList<String>();
        this.arrayIndex = -1;
        this.name = inName;
    }
    
    /**
     * Obtains the precedence of the object.
     * 
     * @return int	precedence of the object {2, 4, 6, 8, 10}.
     */
    public int getPrecedence(){
        return this.precedence;
    }
    
    /**
     * Set the precedence of the object according to the type of entity
     * 
     * 1 is for Node Entity
     * 2 is for Relation between two Node
     * 3 is for Agent Entity
     * 4 is for Relation between Node and Agent or Agent and Agent
     * 5 is for Application Entity
     * 6 is for Relation between Agent and Application
     * 7 is for Timer Entity
     * 8 is for Relation between Timer and Application
     * 9 is for Generic Entity
     * 10 is for any other Relation
     * 11 is for any other Entity
     */
    protected void setPrecedence(int v){
        this.precedence = v;
    }
    
    /**
     * Returns the class definition snippet for this object.
     *
     * @return a reference to the TclSnippet object.
     */
    public TclSnippet getSnippet() {
        return this.snippet;
    }

    /**
     * Changes the name of the object.
     *
     * @param inName the new name for the object.
     */
    public void setName(String inName) {
        this.name = inName;
    }

    /**
     * Obtains the current name for the object.
     *
     * @return the name of the object as a string.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the index of the array that indexes the current object.
     *
     * @return a number in 0..(numberOfIndices), or -1 if object is not indexed
     * by any array (default value).
     */
    public int getArrayIndex() {
        return this.arrayIndex;
    }

    /**
     * Changes the array reference for the object.
     *
     * @param newArrayIndex the (new) number of the array that indices.
     */
    public void setArrayIndex(int newArrayIndex) {
        this.arrayIndex = newArrayIndex;
    }

    /**
     * Returns the number of attributes that a given object has.
     *
     * @return the array index.
     */
    public int getAttributeCount() {
        return this.attributes.size();
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
        if (this.arrayIndex > aig) {
            this.arrayIndex--;
        } else if (this.arrayIndex == aig) {
            this.arrayIndex = -1;
        }
    }

    /**
     * Sets the value of an attribute stored at a given position.
     *
     * @param inAttrIndex the index of the attribute being edited.
     * @param inNewValue a String containing the new value.
     */
    public void setAttribute(int inAttrIndex, String inNewValue) {
        if (inAttrIndex >= this.attributes.size()) {
            this.attributes.add(inAttrIndex, inNewValue);
        } else {
            this.attributes.set(inAttrIndex, inNewValue);
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
        if (inAttrIndex >= this.attributes.size()) {
            return "";
        } else {
            return this.attributes.get(inAttrIndex);
        }
    }
    
    /**
     * Compare this object to another according the attribute precedence
     * @param nsobj     object to compare with
     * @return int      0 if these objects are equal, less than 0 if this comes first, more than 0 if is after the other object.
     */
    public int compareTo(NSObject nsobj){
        return this.getPrecedence() - nsobj.getPrecedence();
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
        StringBuilder str = new StringBuilder(this.attributes.size() + 1);

        str.append(this.snippet.getName()).append("\n")
                .append(this.name).append("\n")
                .append(Integer.toString(this.arrayIndex)).append("\n");

        for (int i = 0; i < this.attributes.size(); i++) {
            str.append(this.attributes.get(i)).append("\n");
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
            this.setArrayIndex(Integer.parseInt(br.readLine()));
            for (int i = 0; i < this.attributes.size(); i++) {
                this.setAttribute(i, br.readLine());
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
