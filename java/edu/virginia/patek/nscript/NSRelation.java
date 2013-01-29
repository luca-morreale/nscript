/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package edu.virginia.patek.nscript;

import java.awt.*;
import java.io.*;

/**
 * Implements the generic behavior of a relation element on the diagram. These
 * objects are used to represent links, attach, and connect operations.
 */
class NSRelation extends NSEditableObject implements DMObject {

    /**
     * The entity where the relation starts.
     */
    NSEntity from;
    /**
     * The entity where the relation ends.
     */
    NSEntity to;
    /**
     * A constant that represents the selection distance to a line that
     * qualifies a click as a selection. In these case the radios is in terms of
     * the width of the line. The default value is 2.0 which means that if a
     * line has a width of 1 pixel, the selection tolerance is 2 pixels.
     */
    public static final double SELECTION_RADIUS = 5.0;

    /**
     * Requires a base class definition (TclSnippet), a name, and the origin and
     * destination entity that it relates.
     *
     * @param inSnippet the base class.
     * @param inName the name of the relation object.
     * @param inFrom the object where the relation starts.
     * @param inTo the object where the relation ends.
     */
    NSRelation(TclSnippet inSnippet, String inName, NSEntity inFrom, NSEntity inTo) {
        super(inSnippet, inName);
        from = inFrom;
        to = inTo;
    }

    /**
     * Returns the object where the relation starts.
     *
     * @return a reference to a NSEntity object where the relation starts.
     */
    public NSEntity getFrom() {
        return from;
    }

    /**
     * Returns the object where the relation ends.
     *
     * @return a reference to a NSEntity object where the relation ends.
     */
    public NSEntity getTo() {
        return to;
    }

    /**
     * A routine where the relation draws itself on the provided graphic context
     * (Graphics), and using the dimension of the current view pane. It uses
     * class definition information to select the drawing attributes, such as
     * line width, and the style of the beginning and ending of the line.
     *
     * @param g a Graphics display context.
     * @param r the dimension of the view pane.
     */
    @Override
    public void drawSelf(Graphics g, Dimension r) {
        Graphics2D g2 = (Graphics2D) g;
        Color color = Color.black;
        if (isSelected()) {
            color = g.getColor();
            g.setColor(Color.blue);
        }
        int baseStyle, lineStyle, lineWidth, endStyle;

        baseStyle = getSnippet().getBaseStyle();
        endStyle = getSnippet().getEndStyle();
        lineStyle = getSnippet().getLineStyle();
        lineWidth = getSnippet().getLineWidth();
        int aSize = lineWidth * 2;

        double size = (double) NSEntity.SIZE / NSEntity.NOMINAL_WIDTH;
        double size2 = size / 2.0;

        // Get start and end points
        double x1, x2, y1, y2;

        double ux, uy, vsize;
        double dx, dy;
        float dash[] = new float[2];

        dx = getTo().getX() - getFrom().getX();
        dy = getTo().getY() - getFrom().getY();

        vsize = Math.sqrt(dx * dx + dy * dy);
        ux = dx / vsize;
        uy = dy / vsize;

        x1 = getFrom().getX() + size2 * ux;
        y1 = getFrom().getY() + size2 * uy;
        x2 = getTo().getX() - size2 * ux;
        y2 = getTo().getY() - size2 * uy;

        // Now, create the stroke

        BasicStroke pen;

        switch (lineStyle) {
            case TclSnippet.SOLID:
                pen = new BasicStroke(lineWidth);
                break;
            case TclSnippet.DASHED:
                dash[0] = 3;
                dash[1] = 2;
                pen = new BasicStroke(lineWidth, 0, 0, 1, dash, 0);
                break;
            case TclSnippet.DOTTED:
            default:
                dash[0] = 1;
                dash[1] = 5;
                pen = new BasicStroke(lineWidth, 0, 0, 1, dash, 0);
                break;
        }
        g2.setStroke(pen);

        // Draw the freekin' line

        g2.drawLine((int) Math.round(x1 * r.width), (int) Math.round(y1 * r.height),
                (int) Math.round(x2 * r.width), (int) Math.round(y2 * r.height));

        g2.setStroke(new BasicStroke(lineWidth));
        switch (baseStyle) {
            case 0:
                break;
            case 1:
                g2.drawLine((int) Math.round(x1 * r.width), (int) Math.round(y1 * r.height),
                        (int) Math.round(x1 * r.width + 2 * ux - 2 * uy), (int) Math.round(y1 * r.height + 2 * uy + aSize * ux));
                g2.drawLine((int) Math.round(x1 * r.width), (int) Math.round(y1 * r.height),
                        (int) Math.round(x1 * r.width + 2 * ux + 2 * uy), (int) Math.round(y1 * r.height + 2 * uy - aSize * ux));
                break;
            case 2:
                g2.fillRect((int) Math.round(x1 * r.width) - aSize - 1, (int) Math.round(y1 * r.height) - aSize - 1, (aSize + 1) * 2, (aSize + 1) * 2);
                break;
            case 3:
            default:
                g2.fillOval((int) Math.round(x1 * r.width) - aSize - 1, (int) Math.round(y1 * r.height) - aSize - 1, (aSize + 1) * 2, (aSize + 1) * 2);
                break;
        }

        switch (endStyle) {
            case 0:
                break;
            case 1:
                g2.drawLine((int) Math.round(x2 * r.width), (int) Math.round(y2 * r.height),
                        (int) Math.round(x2 * r.width - aSize * ux - aSize * uy), (int) Math.round(y2 * r.height - aSize * uy + aSize * ux));
                g2.drawLine((int) Math.round(x2 * r.width), (int) Math.round(y2 * r.height),
                        (int) Math.round(x2 * r.width - aSize * ux + aSize * uy), (int) Math.round(y2 * r.height - aSize * uy - aSize * ux));
                break;
            case 2:
                g2.drawRect((int) Math.round(x1 * r.width) - aSize, (int) Math.round(y1 * r.height) - aSize, 2 * aSize + 1, 2 * aSize + 1);
                break;
            case 3:
            default:
                g2.drawOval((int) Math.round(x1 * r.width) - aSize, (int) Math.round(y1 * r.height) - aSize, 2 * aSize + 1, 2 * aSize + 1);
                break;
        }

        g2.setStroke(new BasicStroke());

        if (isSelected()) {
            g.setColor(color);
        }
    }

    /**
     * Translates the object by translating the objects that it relates. To
     * achieve this it selects the from-to objects is they were not selected to
     * move. This can be seen during the edition. Selecting a link and moving it
     * around the view pane automatically selects the entities that define its
     * extremes.
     *
     * @param r the size of the view pane.
     * @param byWhat the amount of translation.
     */
    @Override
    public void moveBy(Dimension r, Dimension byWhat) {
        if (!getFrom().isSelected()) {
            getFrom().select();
        }
        if (!getTo().isSelected()) {
            getTo().select();
        }
    }

    /**
     * Returns true if the provided coordinate is close enough to the line that
     * represents the relation.
     *
     * @param r the size of the view pane.
     * @param p a set of coordinates.
     * @return true if the line is hit by the point.
     */
    @Override
    public boolean isHit(Dimension r, Point p) {
        double t0;
        double x1, y1, x2, y2, xp, yp;
        double inX = p.getX() / r.width;
        double inY = p.getY() / r.height;
        double radius = NSRelation.SELECTION_RADIUS / NSEntity.NOMINAL_WIDTH;

        x1 = getFrom().getX();
        y1 = getFrom().getY();
        x2 = getTo().getX();
        y2 = getTo().getY();

        // If F is the origin point of the line, and D the destination point,
        // and P the provided point, this procedures calculates the projection
        // of vector (P-F) into the vector (D-F), this projection is then
        // expressed as a multiple (t0) of the (D-F) vector. If t0 is in the
        // interval [0,1], and the distance to the line is less than the
        // radius, return true, otherwise false.
        t0 = ((inX - x1) * (x2 - x1) + (inY - y1) * (y2 - y1))
                / (Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        if (t0 > 0 && t0 < 1) {
            xp = x1 + t0 * (x2 - x1);
            yp = y1 + t0 * (y2 - y1);
            if (Math.sqrt(Math.pow(inX - xp, 2) + Math.pow(inY - yp, 2)) < radius) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the relation is contained in the rectangle delimited by
     * the provided corners (2). Simply checks if both of the provided points
     * are contained in the rectangle.
     *
     * @param r the size of the view pane.
     * @p1 the first point (corner) of a rectangle.
     * @p2 the opposite corner of the rectangle.
     * @return true is both extremes of the relation are contained, false
     * otherwise.
     */
    @Override
    public boolean isContained(Dimension r, Point p1, Point p2) {
        if (getFrom().isContained(r, p1, p2) && getTo().isContained(r, p1, p2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates a representation of the relation object as a String. Used to
     * store the object on disk.
     *
     * @return a string with the representation of the element object.
     */
    @Override
    public String toString() {
        String str;
        str = super.toString();
        str = str + from.getName() + "\n" + to.getName() + "\n";
        return str;
    }

    /**
     * Reads an object from a model. Requires the model to query it for the
     * origin and destination objects, which are assumed to exist in memory by
     * the time an attempt to create relation between them is made.
     *
     * @param br a BufferedReader object, usually wrapping a disk input stream.
     * @param M a reference to the model containing the objects related by it.
     */
    public void fromString(BufferedReader br, NSModel M) {
        try {
            super.fromString(br);
            from = (NSEntity) M.getObject(br.readLine());
            to = (NSEntity) M.getObject(br.readLine());
        } catch (IOException ioe) {
            System.out.println(Messages.tr("reading_object_error") + ioe.toString());
        }
    }
}
