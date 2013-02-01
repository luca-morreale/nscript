/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */
package org.esseks.nscript;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Implements an "About..." dialog box that serves two purposes: 1) Work as a
 * presentation dialog box showing the progress of the startup process. 2) Work
 * as an "About" box that shows information about the program itself.
 */
public class SAboutDialog extends JDialog implements ActionListener {

    static final long serialVersionUID = 42L;
    /**
     * The close button (for the "About..." functionality).
     */
    private JButton closeBtn;
    /**
     * A status string (for the progress dialog functionality).
     */
    private JLabel ad;

    /**
     * Only constructor. Creates the frame and the interface elements depending
     * on the kind of dialog required.
     *
     * @param pFrame the main frame of the application.
     * @param modal a flag indicating the type of dialog that must be shown.
     * true implies that a regular "About" dialog box (one that will wait for
     * the user to hit "OK"). Otherwise a 'modaless' dialog will be created.
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public SAboutDialog(JFrame pFrame, boolean modal) {
        // Calls the father constructor.
        super(pFrame, Messages.tr("about"), modal);
        // Creates the interface
        Icon icon = new ImageIcon(SAboutDialog.class.getResource("/pixmaps/start.png"));
        this.setResizable(false);

        this.getContentPane().add(new JLabel(icon), BorderLayout.CENTER);
        if (modal) {
            JPanel closePane = new JPanel();
            closePane.setLayout(new GridLayout(1, 3));
            closePane.add(new JLabel(""));
            closeBtn = new JButton(Messages.tr("close"));
            closeBtn.addActionListener(this);
            closePane.add(closeBtn);
            closePane.add(new JLabel(""));
            this.getContentPane().add(closePane, BorderLayout.SOUTH);
            setSize(400, 210);
        } else {
            ad = new JLabel(Messages.tr("loading"));
            this.getContentPane().add(ad, BorderLayout.SOUTH);
            setSize(400, 210);
        }

        Dimension dialogDim = getSize();
        Dimension screenSize = getToolkit().getScreenSize();
        setLocation((screenSize.width - dialogDim.width) / 2,
                (screenSize.height - dialogDim.height) / 2);
        setVisible(true);
    }

    /**
     * Sets the message of the status label when the dialog box is used as a
     * progress bar (at startup to be specific.
     *
     * @param inMsg
     */
    public void setMessage(String inMsg) {
        ad.setText(inMsg);
    }

    /**
     * Responds to the button click (Continue button) action.
     *
     * @param ae information about the event.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        JButton b = (JButton) ae.getSource();
        if (b == closeBtn) {
            dispose();
        }
    }
    private static final Logger LOG = Logger.getLogger(SAboutDialog.class.getName());
}
