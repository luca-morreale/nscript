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
 * An attribute of a TclSnippet.
 */
public class TclAttribute extends Object implements Serializable {
    /** */
    public static final long serialVersionUID = 42L;

    /**
     * The name of the attribute.
     */
    String name;
    /**
     * The default value of the attribute.
     */
    String defaultValue;
    /**
     * The list available options, as a String.
     */
    String options;
    /**
     * Flags if a class has a default value.
     */
    public boolean hasDefault;
    /**
     * FLags if a class has a finite set of options.
     */
    public boolean hasOptions;

    /**
     * Constructor takes a string and parses the information.
     *
     * @param s a string, usually read from a file.
     */
    public TclAttribute(String s) {
        parseSelf(s);
    }

    /**
     * Default constructor to implement Serializable protocol.
     */
    private TclAttribute() {
        // nop
    }

    /**
     * @return the name of the attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the default value of the attribute.
     */
    public String getDefault() {
        return defaultValue;
    }

    /**
     * @return the options from the attribute.
     */
    public String getOptions() {
        return options;
    }

    /**
     * Procedure that parses a string looking for the attribute name, its
     * default value, and (optionally) list of options.
     *
     * @param field the string containing the information.
     */
    private void parseSelf(String field) {
        int defaultIndex, optionsIndex;

        defaultValue = "";
        options = "";

        String s = field.trim();
        defaultIndex = s.indexOf('=');
        optionsIndex = s.indexOf(':');
        if (defaultIndex >= 0) {
            hasDefault = true;
        } else {
            hasDefault = false;
        }
        if (optionsIndex >= 0) {
            hasOptions = true;
        } else {
            hasOptions = false;
        }

        // Ok, now read
        if (hasDefault) {
            name = s.substring(0, defaultIndex);
            if (hasOptions) {
                defaultValue = s.substring(defaultIndex + 1, optionsIndex);
                options = s.substring(optionsIndex + 1);
            } else {

                defaultValue = s.substring(defaultIndex + 1);
            }
        } else {
            if (hasOptions) {
                name = s.substring(0, optionsIndex);
                options = s.substring(optionsIndex + 1);
            } else {
                name = s;
            }
        }
    }

    /**
     * Puts the attribute information as a single string.
     * @return
     */
    @Override
    public String toString() {
        return name + " = " + defaultValue + " : " + options;
    }
    private static final Logger LOG = Logger.getLogger(TclAttribute.class.getName());
}
