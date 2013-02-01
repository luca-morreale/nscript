/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package org.esseks.nscript;

import java.util.*;
import java.util.logging.Logger;

/**
 * Messages singleton for holding all translations
 */
public class Messages {

    private static ResourceBundle resources =
            ResourceBundle.getBundle("translations.strings",
                Locale.getDefault());
    private static Messages instance;

    /**
     * Determines suitable locale and instantiate singleton
     */
    private Messages() {
        // nop
    }

    /**
     * Returns appropriate string for supplied ID
     * @param identifier
     * @return
     */
    public synchronized static String tr(String identifier) {
        if (Messages.instance == null) {
            Messages.instance = new Messages();
        }

        return Messages.resources.getString(identifier);
    }

    /**
     *
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    private static final Logger LOG = Logger.getLogger(Messages.class.getName());
}
