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

    /**
     *      */
    public static final long serialVersionUID = 42L;
    /**
     * The name of the attribute.
     */
    private String name;
    /**
     * The default value of the attribute.
     */
    private String defaultValue;
    /**
     * The list available options, as a String.
     */
    private String options;
    /**
     * Flags if a class has a default value.
     */
    private boolean hasDefault;
    /**
     * FLags if a class has a finite set of options.
     */
    private boolean hasOptions;

    /**
     * Constructor takes a string and parses the information.
     *
     * @param s a string, usually read from a file.
     */
    public TclAttribute(String s) {
        this.parseSelf(s);
    }

    /**
     * @return the name of the attribute.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return the default value of the attribute.
     */
    public String getDefault() {
        return this.defaultValue;
    }

    /**
     * @return the options from the attribute.
     */
    public String getOptions() {
        return this.options;
    }

    /**
     *
     * @return
     */
    public boolean hasOptions() {
        return this.hasOptions;
    }

    /**
     *
     * @return
     */
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Procedure that parses a string looking for the attribute name, its
     * default value, and (optionally) list of options.
     *
     * @param field the string containing the information.
     */
    private void parseSelf(String field) {
        int defaultIndex, optionsIndex;

        this.defaultValue = "";
        this.options = "";

        String s = field.trim();
        defaultIndex = s.indexOf('=');
        optionsIndex = s.indexOf(':');
        if (defaultIndex >= 0) {
            this.hasDefault = true;
        } else {
            this.hasDefault = false;
        }
        if (optionsIndex >= 0) {
            this.hasOptions = true;
        } else {
            this.hasOptions = false;
        }

        // Ok, now read
        if (this.hasDefault) {
            this.name = s.substring(0, defaultIndex);
            if (this.hasOptions) {
                this.defaultValue = s.substring(defaultIndex + 1, optionsIndex);
                this.options = s.substring(optionsIndex + 1);
            } else {

                this.defaultValue = s.substring(defaultIndex + 1);
            }
        } else {
            if (this.hasOptions) {
                this.name = s.substring(0, optionsIndex);
                this.options = s.substring(optionsIndex + 1);
            } else {
                this.name = s;
            }
        }
    }

    /**
     * Puts the attribute information as a single string.
     *
     * @return
     */
    @Override
    public String toString() {
        return this.name + " = " + this.defaultValue + " : " + this.options;
    }
    private static final Logger LOG = Logger.getLogger(TclAttribute.class.getName());
}
