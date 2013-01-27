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
 * A TclPatterns embodies the part of a snippet that translates the information
 * into Tcl code. A snippet can have one or more patterns.
 */
public class TclPattern extends Object {

    /**
     * A flag that tells if the pattern is conditional (depends on a value) or
     * not.
     */
    public boolean isConditional;
    /**
     * The pattern is a string with in-lines where the values of attributes will
     * substitute. For example the string 'set #name# [new Application/TCP]'
     * contains the in-line 'name' that corresponds to the name of the object.
     * When Tcl output is generated the actual value of the field 'name'
     * substitutes the inline to produce Tcl code.
     */
    public String pattern;
    /**
     * The alternative pattern is used in a conditional pattern.
     */
    public String alternativePattern;
    /**
     * Attribute is used in a conditional pattern. If the given attribute has
     * value 'attributeValue' pattern is used, otherwise the
     * 'alternativePattern' is used.
     */
    public String attribute;
    /**
     * The value of the attribute that triggers the use of the default pattern.
     */
    public String attributeValue;

    /**
     * Constructor requires a string from which the pattern is parsed.
     *
     * @param s a string containing the pattern information.
     */
    public TclPattern(String s) {
        parseSelf(s);
    }

    /**
     * Parses the pattern from a string.
     *
     * @param s a string with the pattern information.
     */
    private void parseSelf(String s) {
        int equals, firstColon, secondColon;
        s = s.trim();
        if (s.charAt(0) == '?') {
            isConditional = true;
            equals = s.indexOf('=');
            firstColon = s.indexOf(':');
            secondColon = s.indexOf(':', firstColon + 1);
            attribute = s.substring(1, equals).trim();
            attributeValue = s.substring(equals + 1, firstColon).trim();
            pattern = s.substring(firstColon + 1, secondColon);
            alternativePattern = s.substring(secondColon + 1);
        } else {
            isConditional = false;
            pattern = s;
        }
    }

    /**
     * Represents the pattern as a String.
     *
     * @return the representation of the pattern as a String.
     */
    @Override
    public String toString() {
        if (isConditional) {
            return attribute + " = " + attributeValue + " : " + pattern + " : " + alternativePattern;
        } else {
            return pattern;
        }
    }
}
