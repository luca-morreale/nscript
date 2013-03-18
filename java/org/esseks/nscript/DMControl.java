/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package org.esseks.nscript;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * DMControl.java
 *
 * This object implements the Control part of the MVC framework. Takes care of
 * handling events (keys + mouse + mouseMotion). <b>nscript</b> is structured
 * around a Model-View-Controller Architecture DMControl represents an abstract
 * controller which requires the existance of an abstract model (role played by
 * DMModel) to work. Implements the logic of selection and movement of objects,
 * which are stored in the model, including the use of key modifiers like shift.
 */
public class DMControl extends KeyAdapter
        implements MouseListener, MouseMotionListener, Serializable {

    /**
     *      */
    public static final long serialVersionUID = 42L;
    /**
     * A reference to the view object in charge of rendering the view.
     */
    private DMView V;
    /**
     * A reference to the model which stores the actual information.
     */
    private DMModel M;
    /**
     * A reference where a link object starts, for the case when lines are being
     * created.
     */
    private DMObject startO;
    /**
     * Action taken by the user.
     */
    private int action;
    /**
     * Coordinates of the start of a dragging action.
     */
    private Point start;
    /**
     * Coordinates of the end of a dragging action.
     */
    private Point end;
    private static final int NONE = 0;
    private static final int MOVING = 1;
    private static final int SELECTING = 2;
    private static final int LINKING = 3;

    /**
     * The only constructor requires references to the view object as well as
     * the model.
     *
     * @param inV the view (DMView) object.
     * @param inM the model (DMModel) object.
     */
    public DMControl(DMView inV, DMModel inM) {
        // Store the references
        this.V = inV;
        this.M = inM;
        this.action = DMControl.NONE;
    }

    /**
     * A function that returns which of the model objects is hit my a mouse
     * click.
     *
     * @param r the dimension of the view.
     * @param p the location of the mouse when the button was pressed.
     * @return
     */
    public DMObject modelIsHit(Dimension r, Point p) {
        int i;
        for (i = 0; i < this.M.getSize(); i++) {
            if (this.M.getObjectAt(i).isHit(r, p)) {
                return this.M.getObjectAt(i);
            }
        }
        return null;
    }

    /**
     * Counts the number of selected objects in the model.
     *
     * @return int the number of objects selected.
     */
    public int getSelectedCount() {
        int i, count;

        count = 0;
        for (i = 0; i < this.M.getSize(); i++) {
            if (this.M.getObjectAt(i).isSelected()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns a reference to the i-th selected object in the model.
     *
     * @param index to the desired object. index must be between 1 and
     * DMControl.getSelectedCount().
     * @return a reference to the object, if the index is correct, <b>Null</b>
     * otherwise.
     */
    public DMObject getIthSelected(int index) {
        int i, count;

        count = 1;

        for (i = 0; i < this.M.getSize(); i++) {
            if (this.M.getObjectAt(i).isSelected()) {
                if (count == index) {
                    return this.M.getObjectAt(i);
                } else {
                    count++;
                }
            }
        }
        // Not found
        return null;
    }

    /**
     * Clears the whole selection.
     */
    public void unselectAll() {
        int i;

        for (i = 0; i < this.M.getSize(); i++) {
            this.M.getObjectAt(i).unselect();
        }
    }

    /**
     * Action on mouse entering the control.
     *
     * @param me
     */
    @Override
    public void mouseEntered(MouseEvent me) {
    }

    /**
     * Action on mouse leaving the control.
     *
     * @param me
     */
    @Override
    public void mouseExited(MouseEvent me) {
    }

    /**
     * Processes the click event, where the user presses and releases the mouse
     * button in the same location. Implementation of the mouseListener
     * interface.
     *
     * @param me the mouse event object.
     */
    @Override
    public void mouseClicked(MouseEvent me) {
        DMObject e;
        boolean shift = me.isShiftDown();

        switch (this.M.getEditionMode()) {

            case DMModel.SELECT_MODE:
                e = this.modelIsHit(this.V.getBounds().getSize(), me.getPoint());
                if (e != null) {
                    if (shift) {
                        e.toggleSelect();
                    } else {
                        this.unselectAll();
                        e.select();
                    }
                } else {
                    if (!shift) {
                        this.unselectAll();
                    }
                }
                this.M.updateAllViews(false);
                break;

            case DMModel.ICON_MODE:
                this.M.addSimpleObject(this.V.getBounds().getSize(), me.getPoint(), true);
                this.M.updateAllViews(false);
                break;

            case DMModel.RELATION_MODE:
                break;

            default:
                throw new RuntimeException("Unexpected edit state.");
        }
    }

    /**
     * Process the event of the button being pressed, and prepares, if necessary
     * for a dragging action. The actual behavior depends on the mode the Model
     * is working on. Specifically, it responds to SELECT_MODE, by preparing for
     * a movement of the selected items if one of them is hit when no <shift>
     * key is pressed. If <shift> key is pressed, the response is to start a
     * dragging action to create a rectangle. The 'selected' state of all
     * elements of the model encompassed by the resulting rectangle will be
     * toggled.
     *
     * @param me a MouseEvent object containing the mouse state information.
     */
    @Override
    public void mousePressed(MouseEvent me) {
        DMObject e;
        boolean shift = me.isShiftDown();

        this.V.requestFocus();
        // Check if someone is hit
        e = this.modelIsHit(this.V.getBounds().getSize(), me.getPoint());

        switch (this.M.getEditionMode()) {

            case DMModel.SELECT_MODE:
                if ((e != null) && e.isSelected()) { // We have a hit
                    this.action = DMControl.MOVING;
                    this.start = me.getPoint();
                } else {
                    if (!shift) {
                        this.unselectAll();
                    }
                    this.action = DMControl.SELECTING;
                    this.start = me.getPoint();
                    this.end = this.start;
                }
                break;

            case DMModel.RELATION_MODE:
                if (e != null) {  // we have a hit
                    this.action = DMControl.LINKING;
                    this.start = me.getPoint();
                    this.end = this.start;
                    this.startO = e;
                } else {
                    this.action = DMControl.NONE;
                }
                break;

            case DMModel.ICON_MODE:
                break;

            default:
                throw new RuntimeException("Unexpected object mode.");
        }

    }

    /**
     * Draws an auxiliary figure to aid the user in the editing process. It
     * draws the line partially being drawn when creating a new link between
     * objects, or a rectangle, when a selection rectangle is created.
     *
     * @param g a Graphics object with the information about how to draw on the
     * screen provided by the calling routine (in this case DMView).
     */
    public void drawControls(Graphics g) {
        int x1, x2, y1, y2;

        switch (this.action) {
            case (DMControl.SELECTING):
                if (this.start.getX() < this.end.getX()) {
                    x1 = (int) this.start.getX();
                    x2 = (int) this.end.getX();
                } else {
                    x2 = (int) this.start.getX();
                    x1 = (int) this.end.getX();
                }
                if (this.start.getY() < this.end.getY()) {
                    y1 = (int) this.start.getY();
                    y2 = (int) this.end.getY();
                } else {
                    y2 = (int) this.start.getY();
                    y1 = (int) this.end.getY();
                }
                g.drawRect(x1, y1, x2 - x1, y2 - y1);
                break;

            case (DMControl.LINKING):
                x1 = (int) this.start.getX();
                y1 = (int) this.start.getY();
                x2 = (int) this.end.getX();
                y2 = (int) this.end.getY();
                g.drawLine(x1, y1, x2, y2);
                break;

            default:
                break;
        }
    }

    /**
     * Process the mouse released event that occurs when the user has finished
     * its dragging action. This can happen when the editing mode is SELECTING,
     * MOVING, or LINKING (creation of a link between to iconic objects, such as
     * a network link).
     *
     * @param me a MouseEvent object with the information about the mouse
     * position.
     */
    @Override
    public void mouseReleased(MouseEvent me) {
        DMObject e = this.modelIsHit(this.V.getBounds().getSize(), me.getPoint());
        int i;
        boolean shift = me.isShiftDown();

        switch (this.action) {
            case (DMControl.NONE):
                break;

            case (DMControl.SELECTING):
                for (i = 0; i < this.M.getSize(); i++) {
                    if (this.M.getObjectAt(i).isContained(this.V.getBounds().getSize(), this.start, me.getPoint())) {
                        if (shift) {
                            this.M.getObjectAt(i).toggleSelect();
                        } else {
                            this.M.getObjectAt(i).select();
                        }
                    }
                }
                this.M.updateAllViews(false);
                break;

            case (DMControl.MOVING):
                Dimension r = new Dimension((int) (me.getPoint().getX() - this.start.getX()),
                        (int) (me.getPoint().getY() - this.start.getY()));
                for (i = 0; i < this.M.getSize(); i++) {
                    if (this.M.getObjectAt(i).isSelected()) {
                        this.M.getObjectAt(i).moveBy(this.V.getBounds().getSize(), r);
                    }
                    this.M.setDirty(true);
                }
                this.M.updateAllViews(false);
                break;

            case (DMControl.LINKING):
                if (this.action == DMControl.LINKING) {
                    if (e != null) {
                        this.M.addRelationObject(this.startO, e, true);
                        this.M.updateAllViews(false);
                    } else {
                        this.V.repaint();
                    }
                }
                break;

            default:
                throw new RuntimeException("Unexpected action.");
        }
        this.action = DMControl.NONE;
    }

    /**
     * Action on mouse moving inside the control.
     *
     * @param me
     */
    @Override
    public void mouseMoved(MouseEvent me) {
    }

    /**
     * Reponse to the movement of the mouse. Only relevant if a link is being
     * created (LINKING), a set of selected objects is being moved (MOVING), or
     * a selection rectangle is being drawn (SELECTING).
     *
     * @param me a MouseEvent with the information about the mouse position and
     * state.
     */
    @Override
    public void mouseDragged(MouseEvent me) {
        Dimension r;
        int i;

        if (this.action == DMControl.MOVING) {
            r = new Dimension((int) (me.getPoint().getX() - this.start.getX()),
                    (int) (me.getPoint().getY() - this.start.getY()));
            for (i = 0; i < this.M.getSize(); i++) {
                if (this.M.getObjectAt(i).isSelected()) {
                    this.M.getObjectAt(i).moveBy(this.V.getBounds().getSize(), r);
                }
            }
            this.M.updateAllViews(false);
            this.start = me.getPoint();
        } else {
            this.end = me.getPoint();
            this.V.repaint();
        }

    }

    /**
     * Implementation of selected key events. Specifically the 'backspace' key
     * that marks object removal.
     *
     * @param e a KeyEvent with the information about the key being pressed.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
            this.M.removeSelected();
        }
    }
    private static final Logger LOG = Logger.getLogger(DMControl.class.getName());
}
