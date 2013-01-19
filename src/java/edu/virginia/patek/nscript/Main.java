/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */


package edu.virginia.patek.nscript;

import edu.virginia.patek.nscript.NScript;

import javax.swing.*;

public class Main {
  /** Main procedure, simply creates a new NScript instance to initiate
   *  the program.
   */
  public static void main(String[] args)
  {
    // Set System LAF
    try {
      UIManager.setLookAndFeel(
        UIManager.getSystemLookAndFeelClassName());
    } catch (UnsupportedLookAndFeelException e) {
      System.err.println("Couldn't set native LAF!");
    } catch (ClassNotFoundException e) {
      System.err.println("Couldn't set native LAF!");
    } catch (InstantiationException e) {
      System.err.println("Couldn't set native LAF!");
    } catch (IllegalAccessException e) {
      System.err.println("Couldn't set native LAF!");
    }

    NScript s = new NScript();
  }
}
