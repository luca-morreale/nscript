/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */

package edu.virginia.patek.nscript;

import javax.swing.*;
import java.awt.*;

import edu.virginia.patek.nscript.DMObject;
import edu.virginia.patek.nscript.DMModel;
import edu.virginia.patek.nscript.DMControl;

/** Implements the View part of the MVC paradigm by extending the JPanel object
 *  to handle mouse and key event handlers.  Implements the functionality
 *  related to rendering the view by setting the attributes of the output
 *  display, calculating the size of the output area and passing the
 *  information to each of the objects in the model, each of which implements
 *  the DMObject interface and knows how to draw itself in the provided
 *  context.
 */
public class DMView extends JPanel {
  static final long serialVersionUID = 42L;

  /** A reference to the model object. */
  DMModel M;
  /** A referebce to the control object. */
  DMControl C;
  /** The scale of the drawing */
  double scale;

  /** This constructor requires a reference to the model. It creates a new
   *  controller object, and registers it as a listener for mouse, mouse
   *  motion, and keyboard events.
   */
  public DMView(DMModel inM)
  {
    M = inM;
    C = new DMControl(this, inM); // Creates a new controller object.
    // Register the new control as a handler for mouse and key events.
    addMouseListener(C);
    addMouseMotionListener(C);
    addKeyListener(C);
    scale = 1.0; // Set the default scale of the drawing.
    setBackground(Color.white); // Set the colors for the background of the view.
    setForeground(Color.black); // Set the foreground color.
  }

  /** Here the rendering of the model takes places by: obtaining context
   *  information, and passing it to all of the register objects in the model.
   *  It also calls the control so that it can display any auxiliary lines
   *  used in the edition process.
   */
  public void paint(Graphics g)
  {
    super.paint(g);
    Dimension r;
    int i;

    // Set the font, and obtain the context information so that each
    // of the objects can draw themselves.
    g.setFont(new Font("Helvetica", Font.PLAIN, 9));
    // System.err.println("Starting paint...");
    r = getBounds().getSize();

    // Call each of the objects in the model, and tell them to draw
    // themselves on the screen.
    for (i = 0; i < M.getSize(); i++)
      M.getObjectAt(i).drawSelf(g, r);
    // System.err.println("Ending paint...");

    // Draws the auxiliary controls (line or rectangle being drawn).
    C.drawControls(g);
  }

  /** Set a new scale for the drawing. Effectively, it resets the size of the
   *  drawing pane, which is the used by the objects to rescale themselves
   *  accordingly.
   */
  public void changeScale(double inNewScale)
  {
    Dimension r = getBounds().getSize();

    setSize((int)(r.getWidth() * inNewScale / scale), (int)(r.getHeight() * inNewScale / scale));
    scale = inNewScale;
    validate();
  }
}
