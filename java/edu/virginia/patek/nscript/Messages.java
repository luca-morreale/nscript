/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package edu.virginia.patek.nscript;

import java.util.*;
import java.util.logging.Logger;

/**
 * Messages singleton for holding all translations
 */
public class Messages {

    private static ResourceBundle resources;
    private static Messages instance;

    /**
     * Determines suitable locale and instantiate singleton
     */
    private Messages() {
        // TODO
        Locale locale = Locale.getDefault();
        Messages.resources = ResourceBundle.getBundle("translations.strings", locale);
    }

    /**
     * Returns appropriate string for supplied ID
     */
    public static String tr(String identifier) {
        if (Messages.instance == null) {
            Messages.instance = new Messages();
        }

        return Messages.resources.getString(identifier);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    private static final Logger LOG = Logger.getLogger(Messages.class.getName());
}
