/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package org.esseks.nscript;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * Implements the representation of a class of objects (TclSnippet) in the
 * ToolBox view.
 */
public class NSIconPane extends JLabel implements ListCellRenderer<TclSnippet> {

    static final long serialVersionUID = 42L;
    /**
     * Constant to represent an OVAL icon.
     */
    public static final int OVAL = 0;
    /**
     * Constant to represent an AGENT icon.
     */
    public static final int AGENT = 1;
    /**
     * Constant to represent an APPLICATION icon.
     */
    public static final int APPLICATION = 2;
    /**
     * Constant to represent an TRACE icon.
     */
    public static final int TRACE = 3;
    /**
     * Constant to represent an TIMER icon.
     */
    public static final int TIMER = 4;
    /**
     * Constant to represent an UTILS icon.
     */
    public static final int UTILS = 5;
    /**
     * Constant to represent an LINK relation.
     */
    public static final int LINK = 6;
    /**
     * Constant to represent an ATTACH relation.
     */
    public static final int ATTACH = 7;
    /**
     * Constant to represent an CONNECT relation.
     */
    public static final int CONNECT = 8;
    /**
     * The icon of the object.
     */
    private int icon;
    /**
     * The label of this entry.
     */
    private String label;

    /**
     * Constructor that calls the father's constructor (JLabel), and initializes
     * the values of the icon and the label.
     *
     * @param inIconID
     * @param inLabel
     */
    public NSIconPane(int inIconID, String inLabel) {
        super(inLabel);
        this.icon = inIconID;
        this.label = inLabel;
        this.setOpaque(true);
    }

    /**
     * Displays the entry as an icon.
     *
     * @param g
     */
    public void paintMe(Graphics g) {
        this.paintIcon(g, new Point(0, 0), this.getBounds().getSize(), this.icon);
        int w = (int) this.getBounds().getHeight();
        g.drawString(this.label, w + (w / 10), (w + 2) - 10);
    }

    /**
     * Returns a component in which the element is displayed.
     *
     * @param list
     * @param value
     * @param index
     * @param cellHasFocus
     * @param isSelected
     * @return components that displays the element.
     */
    @Override
    public Component getListCellRendererComponent(
            JList<? extends TclSnippet> list,
            TclSnippet value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        this.icon = value.getIcon();
        if (!value.isRelation()) {
            this.setText(value.getName() + "(" + value.getBase() + ")");
        } else {
            this.setText(value.getName() + "(" + value.getFromBase() + "," + value.getToBase() + ")");
        }
        this.label = value.getName();

        if (isSelected) {
            this.setBackground(list.getSelectionBackground());
            this.setForeground(list.getSelectionForeground());
        } else {
            this.setBackground(list.getBackground());
            this.setForeground(list.getForeground());
        }

        return this;
    }

    /**
     * Displays the icon in the screen.
     *
     * @param g
     * @param r
     * @param p
     * @param iconIndex
     */
    public void paintIcon(Graphics g, Point p, Dimension r, int iconIndex) {
        int x, y;
        int h = (int) r.getHeight();
        int h2 = h / 2;
        int h10 = h / 10;
        x = (int) p.getX();
        y = (int) p.getY();

        switch (iconIndex) {
            case OVAL:
                g.drawOval(x, y, h, h);
                break;

            case AGENT:
                g.drawRect(x, y, h, h);
                g.drawLine(x, y + h2, x + h, y + h2);
                break;

            case APPLICATION:
                g.drawLine(x + h2, y, x + h, y + h2);
                g.drawLine(x + h, y + h2, x + h2, y + h);
                g.drawLine(x + h2, y + h, x, y + h2);
                g.drawLine(x, y + h2, x + h2, y);
                break;

            case TRACE:
                g.drawRect(x, y, h - 1, h - 1);
                g.drawLine(x + 1, y + h, x + h, y + h);
                g.drawLine(x + h, y + h, x + h, y + 1);
                g.drawLine(x + h10, y + h10, x + h10, (y + h) - h10);
                break;

            case TIMER:
                g.drawRect(x, y, h, h);
                g.drawString("0:00", x + h10, y + h10);
                break;

            case UTILS:
                g.drawRoundRect(x, y, h, h, h10, h10);
                break;

            case LINK:
                g.drawLine(x, y + h, x + h, y);
                break;

            case ATTACH:
                g.drawLine(x, y + h, x + h, y);
                g.drawLine(x + h, y, (x + h) - h10, y);
                g.drawLine(x + h, y, x + h, y + h10);
                g.drawOval(x + h10, (y - h10) + h, h10, h10);
                break;

            case CONNECT:
                g.drawLine(x, y + h, x + h, y);
                g.drawLine(x + h, y, (x + h) - h10, y);
                g.drawLine(x + h, y, x + h, y + h10);
                break;

            default:
                break;
        }
    }
    private static final Logger LOG = Logger.getLogger(NSIconPane.class.getName());
}
