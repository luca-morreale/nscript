/* ----------------------------------------------------------------------------
 * NSCRIPT
 * version 1.0.2
 * Author:              Enrique Campos-Nanez
 * Contact information: Dr. Stephen D. Patek
 *                      Department of Systems and Information Engineering
 *                      101-C Olsson Hall
 *                      Charlottesville, VA 22904
 *                      University of Virginia
 *                      e-mail: patek@virginia.edu
 * ------------------------------------------------------------------------- */

package edu.virginia.patek.nscript;

import java.awt.*;

import edu.virginia.patek.nscript.DMObject;
import edu.virginia.patek.nscript.NSObject;

/** A generic extension to NSObject that implements the DMObject interface to
 *  make the object editable by the DMControl-DMView components. This extension
 *  will be used through inheritance by other objects (NSRelation and
 *  NSIconic).
 */
public class NSEditableObject extends NSObject implements DMObject {
  /** Flag to keep the 'Selected' state of the object. */
  boolean selected;

  /** Uses the constructor from NSObject. */
  public NSEditableObject( TclSnippet inSnippet, String inName )
  {
    super( inSnippet, inName );
  }

  /** True if the object is selected for edition, false otherwise.
   *  @return true if selected, false otherwise. */
  public boolean isSelected()
  {
    return selected;
  }

  /** Selects the object for edition. After calling this method, method
   *  isSelected() will return true. */
  public void select()
  {
    selected = true;
  }

  /** Removes the selected status, so that edition commands do not affect
   *  this object anymore. */
  public void unselect()
  {
    selected = false;
  }

  /** Changes the 'Selected' status. If the object was selected, it will
   *  become unselected, and vice versa. */
  public void toggleSelect()
  {
    selected = !selected;
  }

  /** Command to draw itself on a view. Empty implementation, since these
   *  class is still abstract.
   */
  public void drawSelf( Graphics g, Dimension r )
  {
  }

  /** False by default, since the class is still abstract.
   *  @return false. */
  public boolean isHit( Dimension r, Point p )
  {
    return false;
  }

  /** Returns false by default, since the class is still abstract.
   *  @param r the size of the drawing canvas.
   *  @param p1 one of the corners of the rectangle.
   *  @param p2 the other corner of the rectangle.
   *  @return false (abstract). */
  public boolean isContained( Dimension r, Point p1, Point p2 )
  {
    return false;
  }

  /** Translates the position of the object by a given dimension.
   *  @param r the size of the drawing canvas.
   *  @param byWhat the amount of translation. */
  public void moveBy( Dimension r, Dimension byWhat )
  {
  }
}
