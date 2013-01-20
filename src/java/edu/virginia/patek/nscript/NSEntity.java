/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */

package edu.virginia.patek.nscript;

import java.io.*;
import java.awt.*;

import edu.virginia.patek.nscript.NSEditableObject;
import edu.virginia.patek.nscript.TclSnippet;
import edu.virginia.patek.nscript.Messages;

/** Implements the abstract class NSEditableObject to behave as an icon on the
 *  screen.
 *  This object fully implements position translation, detecting mouse clicks,
 *  and a function to discover if the object is 'engulfed' in a rectangle.
 *  The node can appear with any of the following icons:
 *  NODE: a circle.
 *  AGENT: a stack.
 *  APPLICATION: a diamond like figure.
 *  TIMER: a clock like figure.
 *  GENERIC: a generic figure, a circle inside a square. */
public class NSEntity extends NSEditableObject {
  /** The x coordinate of the position of the object.
   *  x is in the [0,1] interval.
   */
  double x;
  /** The y coordinate of the position of the object.
   *  y is in the [0,1] interval.
   */
  double y;

  /** Constant representing the NODE shape. */
  public static final int NODE = 0;
  /** Constant representing the AGENT shape. */
  public static final int AGENT = 1;
  /** Constant representing the APPLICATION shape. */
  public static final int APPLICATION = 2;
  /** Constant representing the TIMER shape. */
  public static final int TIMER = 3;
  /** Constant representing the GENERIC shape. */
  public static final int GENERIC = 4;
  /** The normal width of a letter-size page. */
  public static final int NOMINAL_WIDTH = 612;       // = 8.5" x 72 dpi
  /** The size of the icon under a scale of 1:1 */
  public static final int SIZE = 16;             // Size under scale = 1.0

  /** Basic constructor that calls the father's contructor, and
   *  initializes the icons (x,y) coordinates.
   *  @param inSnippet contains the base class information of the object.
   *  @param inName the name of the object.
   *  @param inX the initial x coordinate of the object.
   *  @param inY the initial y coordinate of the object. */
  public NSEntity(TclSnippet inSnippet, String inName, double inX, double inY)
  {
    super(inSnippet, inName);

    setX(inX);
    setY(inY);
  }

  /** Sets the x coordinate position of the object. */
  public void setX(double inX)
  {
    x = inX;
  }

  /** Obtains the x coordinate position of the object.
   *  @return the x position as a double in [0,1]. */
  public double getX()
  {
    return x;
  }

  /** Sets the y coordinate position of the object.
   *  @param inY the new position. */
  public void setY(double inY)
  {
    y = inY;
  }

  /** Obtains the y coordinate position of the object.
   *  @return the y position as a double in [0,1]. */
  public double getY()
  {
    return y;
  }

  /** Draw itself on the screen taken into consideration the 'selected'
   *  status (selected object are drawn in blue color), the scale of the
   *  drawing, the size of the output view, the icon used to represent the
   *  object in the screen, and the multiplicity of the object (through the
   *  use of arrays).
   *  @param g_ a graphics context.
   *  @param r the size of the view pane.*/
  public void drawSelf(Graphics g_, Dimension r)
  {

    Graphics2D g = (Graphics2D)g_;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                       RenderingHints.VALUE_ANTIALIAS_ON);

    Color color = Color.black;
    Color oc = Color.white;
    double scaleX = r.width;
    double scaleY = r.height;


    // Change the color to blue if the object has the 'Selected'
    // status (i.e. it is being modified).
    if (isSelected()) {
      color = g.getColor();
      g.setColor(Color.blue);
    }

    // Transform the position of the object from the interval [0,1]
    // using the height and width of the view pane.
    int dx = (int) Math.round(r.width * x);
    int dy = (int) Math.round(r.height * y);
    // Calculate the size of the element according to the scale.
    int size = Math.round(NSEntity.SIZE * r.width / NSEntity.NOMINAL_WIDTH);
    int size2 = Math.round(size / 2);

    // If the element is indexed by an array, display it with a 'shadow'
    // to give the impresion of a stack of objects.
    if (arrayIndex >= 0) {
      switch (getSnippet().getIcon()) {
        case NSEntity.NODE:
          g.drawOval(dx - size2 + 2, dy - size2 + 2, size, size);
          g.setColor(Color.white);
          g.fillOval(dx - size2, dy - size2, size, size);
          break;

        case NSEntity.AGENT:
          g.drawRect(dx - size2 + 2, dy - size2 + 2, size, size);
          g.drawLine(dx - size2 + 2, dy + 2, dx + size2 + 2, dy + 2);
          g.setColor(Color.white);
          g.fillRect(dx - size2, dy - size2, size, size);
          break;

        case NSEntity.APPLICATION:
          g.drawLine(dx, dy - size2 + 2, dx + size2, dy + 2);
          g.drawLine(dx + size2, dy + 2, dx, dy + size2 + 2);
          g.drawLine(dx, dy + size2 + 2, dx - size2, dy + 2);
          g.drawLine(dx - size2, dy + 2, dx, dy - size2 + 2);
          break;

        case NSEntity.TIMER:
          g.drawOval(dx - size2 + 2, dy - size2 + 2, size, size);
          g.drawLine(dx + size2 / 2 + 2, dy - size2 / 2 + 2, dx + 2, dy + 2);
          g.drawLine(dx + 2, dy + 2, dx + 2, dy + size2 + 2);
          g.setColor(Color.white);
          g.fillOval(dx - size2, dy - size2, size, size);
          break;

        case NSEntity.GENERIC:
          g.drawRect(dx - size2 + 2, dy - size2 + 2, size, size);
          g.setColor(Color.white);
          g.fillRect(dx - size2, dy - size2, size, size);
          break;
      }
    }

    if (isSelected()) {
      g.setColor(Color.blue);
    } else {
      g.setColor(color);
    }

    // Obtain the icon of this element, and display the element accordingly.
    switch (getSnippet().getIcon()) {
      case NSEntity.NODE:
        g.drawOval(dx - size2, dy - size2, size, size);
        break;

      case NSEntity.AGENT:
        g.drawRect(dx - size2, dy - size2, size, size);
        g.drawLine(dx - size2, dy, dx + size2, dy);
        break;

      case NSEntity.APPLICATION:
        g.drawLine(dx, dy - size2, dx + size2, dy);
        g.drawLine(dx + size2, dy, dx, dy + size2);
        g.drawLine(dx, dy + size2, dx - size2, dy);
        g.drawLine(dx - size2, dy, dx, dy - size2);
        break;

      case NSEntity.TIMER:
        g.drawOval(dx - size2, dy - size2, size, size);
        g.drawLine(dx + size2 / 2, dy - size2 / 2, dx, dy);
        g.drawLine(dx, dy, dx, dy + size2);
        break;

      case NSEntity.GENERIC:
        g.drawOval(dx - size2, dy - size2, size, size);
        g.drawRect(dx - size2, dy - size2, size, size);
        break;

    }

    // Draw the name of the object to its right.
    g.drawString(getName(), dx + size, dy);
    if (isSelected())
      g.setColor(color);
  }

  /** True if the position of the object is 'close' to the given mouse point.
   *  It is used in select operations.
   *  @param r the size of the view pane.
   *  @param p the position of the mouse in view coordinates.
   *  @return true if the element was hit by the point p, false otherwise. */
  public boolean isHit(Dimension r, Point p)
  {
    double inX, inY, distance, size2 = (double) NSEntity.SIZE / NSEntity.NOMINAL_WIDTH / 2;

    inX = p.getX() / r.width;
    inY = p.getY() / r.height;
    distance = Math.sqrt(Math.pow(getX() - inX, 2) + Math.pow(getY() - inY, 2));

    if (distance < size2) {
      return true;
    } else
      return false;
  }

  /** Returns true if the element is contained in the rectangle contained in
   *  the rectangle described by the points given.
   *  @param r the size of the view pane.
   *  @param p1 the first point of the rectangle.
   *  @param p2 the second point marking the rectangle.
   *  @return true if the element is contained by the rectangle,
   *          false otherwise.
   */
  public boolean isContained(Dimension r, Point p1, Point p2)
  {
    double x1, y1, x2, y2;
    double minX, maxX, minY, maxY;

    x1 = p1.getX() / r.width;
    y1 = p1.getY() / r.height;
    x2 = p2.getX() / r.width;
    y2 = p2.getY() / r.height;

    if (x1 < x2) {
      minX = x1;
      maxX = x2;
    } else {
      minX = x2;
      maxX = x1;
    }
    if (y1 < y2) {
      minY = y1;
      maxY = y2;
    } else {
      minY = y2;
      maxY = y1;
    }

    if (getX() > minX && getX() < maxX && getY() > minY && getY() < maxY)
      return true;
    else
      return false;
  }

  /** Moves the position of the object by the given amount.
   *  @param r the size of the view pane.
   *  @param byWhat the size of the translation. */
  public void moveBy(Dimension r, Dimension byWhat)
  {
    double dx, dy;

    dx = (double) byWhat.width / r.width;
    dy = (double) byWhat.height / r.height;

    setX(getX() + dx);
    setY(getY() + dy);
  }

  /** Returns the element represented as a string. This routine is used to
   *  store the element to disk.
   *  @return the element written as a string. */
  public String toString()
  {
    String s = "";

    s = super.toString();
    s = s + Double.toString(x) + "\n" + Double.toString(y) + "\n";

    return s;
  }

  /** Reads the element from a budderedReader (stream), basically reading its
   *  coordinates. The rest of the information is carried on higher levels of
   *  the hierarchy.
   *  @param br the stream that contains the object information, tipically a
   *            file.
   */
  public void fromString(BufferedReader br)
  {
    super.fromString(br);
    try {
      setX(Double.parseDouble(br.readLine()));
      setY(Double.parseDouble(br.readLine()));
    } catch (IOException ioe) {
      System.err.println(Messages.tr("reading_object_error") + ioe.toString());
    }
  }
}
