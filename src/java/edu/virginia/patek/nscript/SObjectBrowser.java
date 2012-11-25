/*
 * This source file is part of NScript, released under BSD-modern.
 *
 * Copyright (C) 2000-2001 Enrique Campos-Nanez
 * Copyright (C) 2012 Stefano Sanfilippo
 *
 * See README.* at top level for copying, contacts, history and notes.
 */

package edu.virginia.patek.nscript;

import edu.virginia.patek.nscript.NSModel;
import edu.virginia.patek.nscript.SObjectTableModel;
import edu.virginia.patek.nscript.SObjectCellEditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;

/** Creates a panel that allows the user to modify the properties of an
 *  object. */
public class SObjectBrowser extends JPanel implements ActionListener {
  /** A reference to the simulation model. */
  NSModel M;
  /** The default button from the interface. */
  JButton defaultBtn;
  /** The 'apply' button from the interface. */
  JButton applyBtn;
  /** A label that describes the current object. Typically its class. */
  JLabel description;
  /** The name field, where the user sets and edits the name of the selected
   *  object.
   */
  JTextField nameField;
  /** The name label. "Name: " */
  JLabel name;
  /** A drop down index containing the available indices + the 'None' entry.
   *  Through this element, the user can specify if a given object is
   *  indexed by a given array.
   */
  JComboBox arrayIndex;
  /** The attribute table, where the object properties are listed, and the
   *  user can modify.
   */
  JTable attrTable;
  /** A reference to the current object being edited. */
  NSObject o = null;

  /** The default constructor receives a reference to the current
   *  simulation model.
   */
  public SObjectBrowser( NSModel inModel )
  {
    super();
    M = inModel;
    name = new JLabel("Name: ");
    nameField = new JTextField();
    nameField . addActionListener( this );
    description = new JLabel("No selection.");
    JPanel p = new JPanel(new GridLayout(1,2));
    JPanel aiPanel = new JPanel(new BorderLayout() );
    arrayIndex = new JComboBox();
    arrayIndex . addActionListener( this );
    /*	 	arrayIndex . addItemListener( this );*/
    aiPanel . add( arrayIndex, BorderLayout.CENTER );
    aiPanel . add( new JLabel("Indexed by:"), BorderLayout.WEST );
    p.add( name );
    p.add( nameField );
    JPanel p2 = new JPanel( new GridLayout(3,1));
    p2.add(description);
    p2.add(p);
    p2.add( aiPanel );

    setLayout(new BorderLayout());
    add(p2,BorderLayout.NORTH);

    defaultBtn = new JButton("Use defaults");
    defaultBtn.addActionListener(this);
    applyBtn = new JButton("Apply");
    applyBtn.addActionListener(this);

    JPanel p3 = new JPanel( new GridLayout(1,2) );
    p3 . add( defaultBtn );
    p3 . add( applyBtn );

    add(p3,BorderLayout.SOUTH);

    // OK, the table
    attrTable = new JTable(new SObjectTableModel(null));
    JScrollPane sp_at = new JScrollPane(attrTable);
    add(sp_at,BorderLayout.CENTER);
    setBorder(new EmptyBorder(2,2,2,2));
  }

  /** Provides an interface through which the program can advertise changes
   *  in the simulation model.
   */
  public void selectionChanged()
  {
    NSRelation or;
    int i, oc;

    SObjectTableModel tm;
    oc = 0;
    for (i=0; i<M.getSize(); i++) {
      if (M.getObjectAt(i).isSelected()) {
        oc++;
        o = (NSObject)M.getObjectAt(i);
      }
    }
    if (oc==1) {
      nameField.setText(o.getName());
      if (o.getSnippet().isRelation) {
        or = (NSRelation)o;
        description.setText( "Relates (" + or.getFrom().getName() + ", " + or.getTo().getName() + ")" );
      } else
        description.setText( "Entity class: " + o.getSnippet().getName() + " : " + o.getSnippet().getBase() );
      // Prepare array list
      arrayIndex . removeAllItems();
      arrayIndex . addItem("No Index");
      for (i=0; i<M . getArrayCount(); i++)
        arrayIndex . addItem( M.getArray(i) );
      if (o.getArrayIndex()>=0) {
        arrayIndex . setSelectedIndex(o.getArrayIndex()+1);
      }
    }
    if (oc>1) {
      nameField . setText( "" );
      description . setText("Multiple selection.");
      arrayIndex.setSelectedIndex(-1);
      o = null;
    }
    if (oc == 0) {
      nameField . setText( "" );
      description . setText("No selection.");
      arrayIndex.setSelectedIndex(-1);
      o = null;
    }
    tm = new SObjectTableModel(o);
    attrTable.setModel( tm );
    if (oc==1) {
      attrTable.getColumnModel().getColumn(1).setCellEditor(new SObjectCellEditor( o ));
    }
  }

  /** Implements the ActionListener interface by responding to the
   *  different actions the interface elements generate when manipulated
   *  by the user.
   */
  public void actionPerformed( ActionEvent ae )
  {
    int i;

    if (ae.getSource()==defaultBtn) {
      if (o!=null) {
        o . getSnippet() . instantiateNSObject( o );
        M.updateAllViews();
        M.setDirty(true);
      }
    }
    if (ae.getSource()==applyBtn) {
      if (o!=null) {
        o . setName( nameField.getText() );
        o . setArrayIndex( arrayIndex.getSelectedIndex()-1 );
        for (i=0; i<o.getAttributeCount(); i++) {
          o . setAttribute( i, (String) attrTable.getValueAt(i,1) );
        }
        M.updateAllViews();
        M.setDirty(true);
      }
    }
    if (ae.getSource()==nameField) {
      if (o!=null) {
        o . setName( nameField.getText() );
        o . setArrayIndex( arrayIndex.getSelectedIndex()-1 );
        for (i=0; i<o.getAttributeCount(); i++) {
          o . setAttribute( i, (String) attrTable.getValueAt(i,1) );
        }
        M.updateAllViews();
        M.setDirty(true);
      }
    }
  }
//    /** Responds to changes in the selection of the list. */
//    public void itemStateChanged(ItemEvent e)
//    {
//    	int i;
//		if (e.getStateChange()==2 && o!=null) {
//			o . setName( nameField.getText() );
//			o . setArrayIndex( arrayIndex.getSelectedIndex()-1 );
//			for (i=0; i<o.getAttributeCount(); i++) {
//				o . setAttribute( i, (String) attrTable.getValueAt(i,1) );
//			}
//			M.updateAllViews();
//			M.setDirty(true);
//		}
//    }
}
