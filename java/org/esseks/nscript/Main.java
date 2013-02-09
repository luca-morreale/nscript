/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package org.esseks.nscript;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author stefano
 */
public class Main {

    /**
     * Main procedure, simply creates a new NScript instance to initiate the
     * program.
     *
     * @param args
     */
    public static void main(String[] args) {
        // Set System LAF
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
            LOG.log(Level.WARNING, "{0}: {1}", new Object[]{
                        Messages.tr("no_native_laf_error"), e.toString()});
        } catch (ClassNotFoundException e) {
            LOG.log(Level.WARNING, "{0}: {1}", new Object[]{
                        Messages.tr("no_native_laf_error"), e.toString()});
        } catch (InstantiationException e) {
            LOG.log(Level.WARNING, "{0}: {1}", new Object[]{
                        Messages.tr("no_native_laf_error"), e.toString()});
        } catch (IllegalAccessException e) {
            LOG.log(Level.WARNING, "{0}: {1}", new Object[]{
                        Messages.tr("no_native_laf_error"), e.toString()});
        }

        // Disabling INFO messages (debug)
        Logger.getLogger("").setLevel(Level.WARNING);

        new NScript();
    }
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
}
