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
import java.util.*;

import edu.virginia.patek.nscript.NSWorld;
import edu.virginia.patek.nscript.NSObject;
import edu.virginia.patek.nscript.DMModel;
import edu.virginia.patek.nscript.SObjectBrowser;
import edu.virginia.patek.nscript.SToolBar;

/** Holds the current simulation script information. Inherits the object
 *  storing capabilities from NSWorld, and implements the DMModel interface,
 *  in order to work correctly with the DMControl, and DMView objects. Also
 *  contains references to all the views of the model to inform them of model
 *  changes.
 */
public class NSModel extends NSWorld implements DMModel {
  /** Reference to the tool box that stores the currently opened libraries. */
  SToolBar toolBox;
  /** Reference to the main modeling and visualization view. */
  DMView editView;
  /** Reference to the view where the Tcl version of the simulation
   *  script is represented.
   */
  JTextArea tclView;
  /** Reference to the panel where individual objects can be edited. */
  SObjectBrowser objectPanel;
  /** Reference to the view that lists all the objects in the simulation
   *  (not yet implemented).
   */
  NSWorldView worldView;
  /** Flag that tells if the model has been modified and needs to be saved
   *  to a file.
   */
  boolean isDirty;

  /** The onlye constructor for this class of objects. Takes an initial
   *  environment object (the ns object, that stores the simulation
   *  environment), and a reference to the toolbox.
   */
  public NSModel(NSObject inEnv, SToolBar inTBar)
  {
    super(inEnv);
    toolBox = inTBar;
    isDirty = false;
  }

  /** Stores the references to all the views of the model. */
  public void setViews(DMView inEView, JTextArea inTclView,  SObjectBrowser inObjectPanel, NSWorldView inWorldView)
  {
    editView = inEView;
    tclView = inTclView;
    objectPanel = inObjectPanel;
    worldView = inWorldView;
  }

  /** Returns the current edition mode for the model (perhaps this should
   *  be moved to the view).
   *  @return the current edition mode (integer).
   */
  public int getEditionMode()
  {
    String inst;
    TclSnippet s = toolBox.getSelectedSnippet();

    if (s == null)
      return DMModel.SELECT_MODE;

    if (s.isRelation)
      return DMModel.RELATION_MODE;
    else
      return DMModel.ICON_MODE;
  }

  /** Adds a simple object (an entity object, not a relation) to the model.
   *  @param r the size of the view.
   *  @param p the position of the new object.
   *  @param unselectOther if true all other objects should be unselected
   *                       after adding the new object.
   */
  public void addSimpleObject(Dimension r, Point p, boolean unselectOther)
  {
    String inst, newName;
    TclSnippet s = toolBox.getSelectedSnippet();
    NSObject o;
    double x, y;

    x = p.getX() / r.width;
    y = p.getY() / r.height;

    if (s == null)
      return;

    // Check for uniqueness
    if (s.isUnique && objectOfClassExists(s.getName()))
      return;

    isDirty = true;
    // Get a new unique generic name
    int i = -1;
    do {
      i++;
      newName = s.getName() + i;
    } while (!isValidName(newName));

    o = new NSEntity(s, newName, x, y);
    s.instantiateNSObject(o);
    addObject(o);
    tclView.setText(toTcl());
    worldView.updateList();
  }

  /** Adds a new relation.
   *  @param oFrom the object where the relation starts.
   *  @param oTo the objects where the relation ends.
   *  @param unSelectOthers if true the editor all other objects should
   *                        be unselected after the addition of the new object.
   */
  public void addRelationObject(DMObject oFrom, DMObject oTo, boolean unSelectOthers)
  {
    NSObject oF, oT;
    String inst, newName;
    int i;
    TclSnippet s = toolBox.getSelectedSnippet();
    NSRelation o;

    if (s == null || !s.isRelation)
      return;

    if (s.isFromBaseUnique && relationOfClassExists(oFrom, s.getName())) {
      return;
    }
    if (s.isToBaseUnique && relationOfClassExists(oTo, s.getName())) {
      return;
    }

    isDirty = true;
    i = -1;
    do {
      i++;
      newName = s.getName() + i;
    } while (!isValidName(newName));

    if (((NSEntity) oFrom).getSnippet().getBase().equals(s.getFromBase()) &&
        ((NSEntity)  oTo).getSnippet().getBase().equals(s.getToBase())) {
      if (oFrom == oTo) {
        return;
      }
      o = new NSRelation(s, newName, (NSEntity) oFrom, (NSEntity) oTo);
      s.instantiateNSObject(o);
      addObject(o);
      tclView.setText(toTcl());
      worldView.updateList();
    }
  }

  /** Tells all its dependent views to update their representation,
   *  as a response to a change in the model information.
   */
  public void updateAllViews()
  {
    editView.repaint();
    objectPanel.selectionChanged();
    tclView.setText(toTcl());
    worldView.updateList();
  }

  /** Verifies if an object of a given type exists.
   *  @return true if such object exists, false otherwise.
   */
  boolean objectOfClassExists(String theClass)
  {
    NSObject o;
    int i;

    for (i = 0; i < getSize(); i++) {
      o = (NSObject)getObjectAt(i);
      if (o.getSnippet().getName().equals(theClass))
        return true;
    }
    return false;
  }

  /** Verifies if a relation of a given class exists.
   *  @return true if such a relation exists, false otherise.
   */
  boolean relationOfClassExists(DMObject dmo, String theClass)
  {
    NSObject o, O;
    NSRelation or;
    int i;

    O = (NSObject) dmo;
    for (i = 0; i < getSize(); i++) {
      o = (NSObject)getObjectAt(i);
      if (o.getSnippet().isRelation) {
        or = (NSRelation) o;
        if (or.getFrom() == O && or.getSnippet().getName().equals(theClass) ||
            or.getTo() == O && or.getSnippet().getName().equals(theClass)) {
          return true;
        }
      }
    }
    return false;
  }

  /** Obtains the number of objects currently in the simulation.
   *  @return the number of objects (1 or more).
   */
  public int getSize()
  {
    return objects.size();
  }

  /** Obtains the object at a given index. */
  public DMObject getObjectAt(int inIndex)
  {
    if (inIndex >= 0 && inIndex < objects.size())
      return (DMObject)(objects.get(inIndex));
    else
      return null;
  }

  /** Removes the currently selected objects. */
  public void removeSelected()
  {
    int i;
    NSEditableObject o;
    NSRelation or;

    // First, select also arcs attached to a entity to be deleted
    for (i = 0; i < getSize(); i++) {
      o = (NSEditableObject)getObjectAt(i);
      if (o.getSnippet().isRelation) {
        or = (NSRelation) o;
        if (or.getFrom().isSelected() || or.getTo().isSelected()) {
          or.select();
        }
      }
    }

    editView.repaint();

    Iterator<NSObject> iter = objects.iterator();

    iter.next();
    while (iter.hasNext()) {
      o = (NSEditableObject)iter.next();
      if (o.isSelected()) {
        isDirty = true;
        iter.remove();
      }
    }

    updateAllViews();
  }

  /** Creates a new model by flushing all the stored objects, and
   *  initializing the ns environment object to its default values.
   */
  public void newModel()
  {
    // Clean arrays
    while (arrays.size() > 0)
      arrays.remove(0);

    Iterator<NSObject> iter = objects.iterator();

    iter.next();
    while (iter.hasNext()) {
      iter.next();
      iter.remove();
    }

    ((NSObject) getObjectAt(0)).getSnippet().instantiateNSObject((NSObject)getObjectAt(0));
    ((NSObject) getObjectAt(0)).setName("ns");
    updateAllViews();
    setDirty(false);
  }

  /** Creates a Tcl representation of the current object. This call creates
   *  the TclView used in the interface.
   *  @return a String containing the Tcl script of the model.
   */
  public String toTcl()
  {
    int i;
    String s = "";

    for (i = 0; i < getSize(); i++)
      s = s + ((NSObject)getObjectAt(i)).getSnippet().toTcl(this, (NSObject)getObjectAt(i), '#') + '\n';

    s = s + "$" + ((NSObject)getObjectAt(0)).getName() + " run";
    return s;
  }

  /** Represents the current simulation model as a string.
   *  This representation is used to store the model on disk.
   *
   *  @return a String with the representation of the object as a string.
   */
  public String toString()
  {
    String s = "";
    int i;

    s = Integer.toString(getArrayCount()) + "\n";
    for (i = 0; i < getArrayCount(); i++) {
      s = s + getArray(i).toString();
    }
    s = s + Integer.toString(objects.size()) + "\n";
    for (i = 0; i < objects.size(); i++) {
      s = s + ((NSObject)getObjectAt(i)).toString();
    }
    return s;
  }

  /** True if the model needs to be saved to disk (changes have been made
   *  to it since it was opened).
   *  @return true if the model needs to be saved, false otherwise.
   */
  public boolean dirty()
  {
    return isDirty;
  }

  /** Sets the state of the model to dirty.
   *  Called by all the editing methods.
   */
  public void setDirty(boolean dirtyState)
  {
    isDirty = dirtyState;
  }
}
