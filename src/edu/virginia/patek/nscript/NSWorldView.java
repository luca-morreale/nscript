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

import java.awt.*;
import javax.swing.*;
import NSWorld;
import NSWorldPane;

/** Implements a view that lists the elements (objects) that are part of the current simulation
 *  script. */
public class NSWorldView extends JPanel
{
	/** A reference to the simulation model. */
    NSWorld w;
    /** The list GUI element. */
    JList l;
    
    /** Constructor that copies a reference to the simulation model. */
    public NSWorldView( NSWorld inW )
    {
	super();

	w = inW;
	l = new JList( new SWPListModel(w) );
	l . setCellRenderer( new NSWorldPane() );
	JScrollPane sp = new JScrollPane( l );
	setLayout( new BorderLayout() );
	add(sp,BorderLayout.CENTER);
    }

	/** Updates the view when the model suffers changes. */
    public void updateList()
    {
	l . setModel( new SWPListModel( w ) );
        updateUI();
    }

	/** Implements the abstract list model to render the elements in the list. */
    class SWPListModel extends AbstractListModel
    {
    /** A reference to the model. */
	NSWorld W;
	/** The constructor takes a reference to the model. 
	 *  @param inW a reference to the simulation model. */
	public SWPListModel( NSWorld inW )
	{
	    W = inW;  
	}

	/** Returns a string with a description of the ith model. 
	 *  @return the description of the ith model as text. */
	public Object getElementAt( int index )
	{
	    return W.getObject(index).getName()+'('+W.getObject(index).getSnippet().getName()+')';
	}

	/** Obtains the size of the array (in this case the size of the model.
	 *  @return the number of objects in the current simulation model. */
	public int getSize()
	{
	    return W.getObjectCount();
	}
    }
}





