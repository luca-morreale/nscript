/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package edu.virginia.patek.nscript;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Main {

    /**
     * Main procedure, simply creates a new NScript instance to initiate the
     * program.
     */
    public static void main(String[] args) {
        // Set System LAF
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            LOG.warning(Messages.tr("no_native_laf_error"));
        } catch (ClassNotFoundException e) {
            LOG.warning(Messages.tr("no_native_laf_error"));
        } catch (InstantiationException e) {
            LOG.warning(Messages.tr("no_native_laf_error"));
        } catch (IllegalAccessException e) {
            LOG.warning(Messages.tr("no_native_laf_error"));
        }

        // Disabling INFO messages (debug)
        Logger.getLogger("").setLevel(Level.WARNING);

        new NScript();
    }
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
}
