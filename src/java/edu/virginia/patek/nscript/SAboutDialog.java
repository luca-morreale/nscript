/* --------------------------------------------------------------------------------------------
 * NSCRIPT
 * version 1.0.2 
 * Author: 		Enrique Campos-Nanez
 * Contact information: Dr. Stephen D. Patek
 *			Department of Systems and Information Engineering
 *            		101-C Olsson Hall
 *			Charlottesville, VA 22904
 * 			University of Virginia
 * 			e-mail: patek@virginia.edu
 * -------------------------------------------------------------------------------------------- */

package edu.virginia.patek.nscript;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import edu.virginia.patek.nscript.NSModel;

/** Implements an "About..." dialog box that serves two purposes: 
 *  1) Work as a presentation dialog box showing the progress of the startup process. 
 *  2) Work as an "About" box that shows information about the program itself. */
public class SAboutDialog extends JDialog implements ActionListener
{
	/** The close button (for the "About..." functionality). */
    JButton closeBtn;
    /** A status string (for the progress dialog functionality). */
    JLabel ad;

	/** Only constructor. Creates the frame and the interface elements depending
	 *  on the kind of dialog required.
	 *  @param pFrame the main frame of the application. 
	 *  @param model a flag indicating the type of dialog that must be shown. true
	 *     implies that a regular "About" dialog box (one that will wait for the 
	 *     user to hit "OK". Otherwise a 'modaless' dialog will be created. */
    public SAboutDialog( JFrame pFrame, boolean modal )
    {
   	// Calls the father constructor.
	super( pFrame, "nscript-1.0a...", modal );
	// Creates the interface
	Icon icon = new ImageIcon("figs/start.gif");

	this . getContentPane() . add( new JLabel(icon), BorderLayout.CENTER );
	if (modal) {
	    JPanel closePane = new JPanel();
	    closePane . setLayout( new GridLayout(1,3) );
	    closePane . add( new JLabel("") );
	    closeBtn = new JButton("Close");
	    closeBtn.addActionListener( this );
	    closePane . add( closeBtn );
	    closePane . add( new JLabel("") );
	    this . getContentPane() . add( closePane, BorderLayout.SOUTH );
	    setSize(294,241);
	} else {
	    ad = new JLabel("Loading...");
	    this . getContentPane() . add( ad, BorderLayout.SOUTH );
	    setSize(294,241);
	}
	
	Dimension dialogDim = getSize();
	Dimension screenSize = getToolkit().getScreenSize();
	setLocation( (screenSize.width - dialogDim.width)/2, 
		     (screenSize.height - dialogDim.height)/2 );
	show();
    }

	/** Sets the message of the status label when the dialog box is used as a progress
	 *  bar (at startup to be specific. */
    public void setMessage( String inMsg )
    {
	ad . setText( inMsg );
    }

	/** Responds to the button click (Continue button) action. 
	 *  @param ae information about the event. */
    public void actionPerformed( ActionEvent ae )
    {
	JButton b = (JButton) ae.getSource();
	if (b==closeBtn) {
	    dispose();
	}
    }
}

