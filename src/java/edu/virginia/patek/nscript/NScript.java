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

import edu.virginia.patek.nscript.TclLibraryManager;
import edu.virginia.patek.nscript.TclLibrary;
import edu.virginia.patek.nscript.SObjectBrowser;
import edu.virginia.patek.nscript.SArrayDialog;
import edu.virginia.patek.nscript.SAboutDialog;
import edu.virginia.patek.nscript.SToolBar;
import edu.virginia.patek.nscript.NSIconPane;
import edu.virginia.patek.nscript.NSWorldView;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;

/** Implements the main application object for nscript. Interface element objects are
 *  created, library modules are loaded, and the instance of the model is created. */
public class NScript extends JFrame
{
    /** Library manager for the application. Contains the opened libraries. */
    TclLibraryManager libManager;
    /** An object representing the ns simulation environment, present in every simulation script. */
    TclSnippet env;
    /** A view containing the libraries and their available objects. */
    SToolBar toolBox;
    /** A view that imlements the edition of object attributes. */
    SObjectBrowser objectBrowser;
    /** The main editing view, where the topology, transportation, and application components can be edited. */
    DMView mainView;
    /** The world view, which is a list of the available objects in the simulation. */
    NSWorldView worldView;
    /** The model that stores the simulation objects. */
    NSModel model;
    /** The text view that shows the TclScript that results from the simulation script. */
    JTextArea tclView;
    
    /** Main constructor. Its responsibilities include creating the title dialog box, 
     *  the menu bar, toolbar, a library manager to store opened libraries, its graphic
     *  representation (SToolBar), create the model, and other relevant views, like 
     *  the objects browser. */
    public NScript() 
    {
	super("NScript 1.1");
	SAboutDialog ad = new SAboutDialog( null, false );
	Container c = this.getContentPane();
	
	JMenuBar menuBar = new JMenuBar();
	menuBar.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
	JToolBar toolBar = new JToolBar();
	prepareToolBarAndMenu( toolBar, menuBar);
	toolBar.setOrientation(JToolBar.HORIZONTAL);
		
	// Create a new library manager
	libManager = new TclLibraryManager();
	toolBox = new SToolBar( this, libManager );
		
	// ------------- CREATE MODEL AND MAIN VIEW ---------------
	ad . setMessage("Reading environment definitions...");
	model = new NSModel( readEnvironment("settings/environment"), toolBox );
	ad . setMessage("Adding default libraries...");
	addDefaultLibraries("settings/deflibs");
	mainView = new DMView( model );
		
	ad . setMessage("Creating GUI...");
	objectBrowser = new SObjectBrowser( model );
		
	worldView = new NSWorldView( model );
	JTabbedPane wv_tp = new JTabbedPane( JTabbedPane.BOTTOM );
	wv_tp . addTab("Object Browser",objectBrowser);
	wv_tp . addTab("World View",worldView);
		
	tclView = new JTextArea(model.toTcl());
	JScrollPane sp_tcl = new JScrollPane( tclView );
	
	model . setViews( mainView, tclView, objectBrowser, worldView );
	JScrollPane sp_edit = new JScrollPane( mainView );
	mainView . setPreferredSize( new Dimension( 612, 792 ));
	
	JSplitPane helpers = new JSplitPane(JSplitPane.VERTICAL_SPLIT,toolBox,wv_tp);
	helpers.setOneTouchExpandable(true);
	helpers.setDividerSize(2);
	helpers.setDividerLocation(200);
	
	// Script view as another tab
	JTabbedPane tp = new JTabbedPane( JTabbedPane.BOTTOM );
	tp . addTab("Edit",sp_edit);
	tp . addTab("Tcl/Tk Script",sp_tcl);
	
	// Simultaneous view of the scripts
	// JSplitPane tp = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, sp_edit, sp_tcl );	
	// tp . setDividerSize(4);
	// tp . setDividerLocation(400);
	
	JSplitPane mainView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,tp,helpers);
	mainView.setOneTouchExpandable(true);
	mainView.setDividerSize(4);
	mainView.setDividerLocation(612);
	
	// Add components to frame
	Container g = new Container();
	g . setLayout(new BorderLayout());
	g . add(menuBar,BorderLayout.NORTH);
	g . add(toolBar,BorderLayout.CENTER);
	c.add(g,BorderLayout.NORTH);
	//	c.add(toolBar,BorderLayout.WEST);
	c.add(mainView,BorderLayout.CENTER);
	
	addWindowListener(new WindowEventHandler());
	Dimension screenSize = getToolkit().getScreenSize();
	setSize(800,600);
	setLocation(0,0);
	ad . setMessage("Done.");
	setVisible(true);
	ad . dispose();
    }

    /** Reads the default values for the simulation environmen (the ns object), from a file. */
    NSObject readEnvironment( String  envFileName ) 
    {
	File f;
	FileReader fr;
	BufferedReader br;
	NSObject o;
	String s, newLine;
	
	try {
	    f = new File( envFileName );
	    fr = new FileReader( f );
	    br = new BufferedReader( fr );
	    s = "";
	    do {
		newLine = br.readLine();
		s = s+newLine;
	    } while (newLine.indexOf("end")<0);
	    br.close(); 
	    env = new TclSnippet( s );
	    o = new NSEntity( env, "ns", 0.01, 0.01 );
	    env.instantiateNSObject( o );
	    return o;
	} catch (FileNotFoundException fnfe) {
	    System.out.println( "Environment defaults file not found: " + fnfe . toString() );
	    return null;
	} catch (IOException ioe) {
	    System.out.println( "Error reading environment definition: " + ioe . toString() );
	    return null;
	}
    }

    /** Prepares the toolbar and menu items, and relates them to the handling routines. */
    void prepareToolBarAndMenu( JToolBar toolbar, JMenuBar menuBar )
    {
	Icon newIcon = new ImageIcon("pixmaps/new.png");
	Icon openIcon = new ImageIcon("pixmaps/open.png");
	Icon saveIcon = new ImageIcon("pixmaps/save.png");
	Icon openLibIcon = new ImageIcon("pixmaps/openlib.png");
	Icon indexIcon = new ImageIcon("pixmaps/index.png");
	Icon exportIcon = new ImageIcon("pixmaps/nsexport.png");
	Icon runIcon = new ImageIcon("pixmaps/run.png");
	
	NewAction newA = new NewAction( "New", newIcon );
	OpenAction openA = new OpenAction( "Open", openIcon );
	OpenLibAction openLibA = new OpenLibAction( "Open Library...", openLibIcon );
	SaveAction saveA = new SaveAction( "Save...", saveIcon );
	EditArrayAction editArrayA = new EditArrayAction( "Edit arrays...", indexIcon );
	ExportAction exportA = new ExportAction( "Export to ns...", exportIcon );
	RunAction runA = new RunAction( "Run in ns...", runIcon );
	
	// Add things to the toolbar and menubar
	JButton b = toolbar . add( newA );
	b . setToolTipText("New ns script");
	b = toolbar . add( openA );
	b . setToolTipText("Open an existing script");
	b = toolbar . add( openLibA );
	b . setToolTipText("Open a library");
	b = toolbar . add( saveA );
	b . setToolTipText("Save the script");
	toolbar . addSeparator();
	b = toolbar . add( editArrayA );
	b . setToolTipText("Edit object indexes (arrays)");
	toolbar . addSeparator();
	b = toolbar . add( exportA );
	b . setToolTipText("Export the script to ns");
	b = toolbar . add( runA );
	b . setToolTipText("Run the script in ns");
	
	// Now the menu bar (easy)
	
	JMenu fileMenu = new JMenu("File");
	fileMenu.add( newA );
	fileMenu.add( openA );
	fileMenu.add( openLibA );
	fileMenu.add( saveA );
	fileMenu.addSeparator();
	fileMenu.add( new QuitAction("Quit", null));
	
	// Edit menu
	JMenu editMenu = new JMenu("Edit");
	editMenu.add(new ClearAction("Clear",null));
	editMenu.addSeparator();
	editMenu.add( editArrayA );
	
	// NS menu
	JMenu scriptMenu = new JMenu("Script");
	scriptMenu . add( exportA );
	scriptMenu . addSeparator();
	scriptMenu . add( runA );
		
	// Help menu
	JMenu helpMenu = new JMenu("Help");
	helpMenu . add( new AboutAction("About nscript-1.0a...", null ) );
		
	menuBar.add(fileMenu);	
	menuBar.add(editMenu);
	menuBar.add(scriptMenu);
	menuBar.add(helpMenu);
    }
    
    /** Reads the list of default libraries from a file, and opens the corresponding libraries,
     *  which are assumed to be stored in the 'libs' subdirectory of the 'bin' directory. */
    public void addDefaultLibraries( String defFileName )
    {
	File f;
	FileReader fr;
	BufferedReader br;
	TclSnippet env;
	NSObject o;
	String newLine = "";
	
	try {
	    f = new File( defFileName );
	    fr = new FileReader( f );
	    br = new BufferedReader( fr );
	
	    while ((newLine = br.readLine()) != null) {
		if (libManager . addLibrary( newLine )) {
		    toolBox.addLibraryPane( libManager.getLibrary(libManager.getLibraryCount()-1) );
		}
	    }
	} catch (FileNotFoundException fnfe) {
	    System.out.println( "Library file: " + newLine + " not found: " + fnfe . toString() );
	} catch (IOException ioe) {
	    System.out.println( "Error reading library file: " + ioe . toString() );
	}
    }

    /** Handles the 'Open Library' item of the 'File' menu by opening an open file dialog, to allow
     *  the user to select a library to open. */
    public void openLibraryAction()
    {
	File f;
	FileReader fr;
	BufferedReader br;
	
	JFileChooser fch = new JFileChooser("lib");
	fch.setDialogTitle("Select a Library to Open");
	int selected = fch.showOpenDialog(this.getContentPane());
	if (selected == JFileChooser.APPROVE_OPTION) {
	    f = fch.getSelectedFile();
	    if (libManager . addLibrary( f . getPath() ))
		toolBox.addLibraryPane( libManager.getLibrary(libManager.getLibraryCount()-1) );
	}
    }

    /** Handles the save script option, that allows the user to export the current script as a
     *  Tcl script, runnable script. */
    public String saveScript()
    {
	File f;
	FileOutputStream fos;
	FileFilter ff = new LibFilter();
	String tScript;
	byte[] dataOut;
	
	JFileChooser fch = new JFileChooser("../examples");
	fch.setDialogTitle("Select a File Name for the Script.");
	//	fch.setFileFilter(ff);
	f = new File("Untitled.tcl");
	fch.setSelectedFile(f);
	int selected = fch.showSaveDialog(this.getContentPane());
	if (selected == JFileChooser.APPROVE_OPTION) {
	    try {
		fos = new FileOutputStream(fch.getSelectedFile());
		tScript = model.toTcl();
		dataOut = tScript.getBytes();
		fos . write( dataOut );
		fos . flush();
		fos . close();
		return fch.getSelectedFile().getAbsolutePath();
	    } catch (FileNotFoundException e) {
		System.out.println("Problems openning the file: "+e.toString());
		return null;
	    } catch (IOException ioe) {
		System.out.println("Problems writing into file: "+ioe.toString());
		return null;
	    }
	}
	return null;
    }

    /** Handles the 'Save' item of the 'File' menu, by allowing the user to select a place
     *  to store the current script in a propiertary format. This will soon be replaced by
     *  an XML format. */
    public String saveFileAction()
    {
	File f;
	FileOutputStream fos;
	FileFilter ff = new LibFilter();
	String tScript;
	byte[] dataOut;
	
	JFileChooser fch = new JFileChooser("../examples");
	fch . setDialogTitle("Save As");
	//	fch.setFileFilter(ff);
	f = new File("Untitled.nss");
	fch.setSelectedFile(f);
	int selected = fch.showSaveDialog(this.getContentPane());
	if (selected == JFileChooser.APPROVE_OPTION) {
	    try {
		fos = new FileOutputStream(fch.getSelectedFile());
		tScript = model.toString();
		dataOut = tScript.getBytes();
		fos . write( dataOut );
		fos . flush();
		fos . close();
		model . setDirty( false );
		return fch.getSelectedFile().getAbsolutePath();
	    } catch (FileNotFoundException e) {
		System.out.println("Problems creating the file: "+e.toString());
		return null;
	    } catch (IOException ioe) {
		System.out.println("Problems writing into file: "+ioe.toString());
		return null;
	    }
	}
	return null;
    }

    /** Handles closing of a model / application when the current simulation script has suffered
     *  modifications. */
    public boolean reallyClose()
    {
	int selectedValue = JOptionPane.showConfirmDialog(
							  getContentPane(),
							  "Changes to the model will be lost.\nClose anyway?",
							  "Please confirm",
							  JOptionPane.YES_NO_OPTION,
							  JOptionPane.QUESTION_MESSAGE);
	return (selectedValue==0);
    }
        
    /** Implements the "Open" option of the "File" menu. Open a dialog box to let the
     *  user select a file, and opens it. */
    public void openFileAction()
    {
	int nA, nO;
	int i, selected;
	String sName;
	NSObject o;
	NSRelation or;
	TclSnippet s;
	
	JFileChooser fch = new JFileChooser("../examples");
	fch . setDialogTitle("Select Script to Open");
	selected = fch.showOpenDialog(this.getContentPane());
	if (selected == JFileChooser.APPROVE_OPTION) {
	    model . newModel();
	    try {
		File f = fch.getSelectedFile();
		FileReader fr = new FileReader( f );
		BufferedReader br = new BufferedReader( fr );
				
				// Read arrays
		nA = Integer.parseInt( br.readLine() );
		for (i=0; i<nA; i++) {
		    model.addArray( br.readLine(),Integer.parseInt(br.readLine() ));
		}
				
				// Read objects (gulp)
		nO = Integer.parseInt( br.readLine() );
		br.readLine(); // Skip snippet and name information
		((NSObject)model.getObjectAt(0)) . setName(br.readLine());
		((NSEntity)model.getObjectAt(0)) . fromString( br );
		for (i=1; i<nO; i++) {
		    sName = br.readLine();
		    s = libManager.getSnippet( sName );
		    if (s==null) {
			s = searchLibAction( sName );
		    }
		    if (s==null) {
			model . newModel();
			model . updateAllViews();
			model . setDirty(false);
			return;
		    }
		    if (s.isRelation) {
			or = new NSRelation( s, br.readLine(), null, null);
			s . instantiateNSObject( or );
			or . fromString( br, model );
			model . addObject( or );
		    } else {
			o = new NSEntity( s, br.readLine(), 0.0, 0.0 );
			s . instantiateNSObject( o );
			o . fromString( br );
			model . addObject( o );
		    }
		}
		model . updateAllViews();
		model . setDirty( false );
	    } catch (FileNotFoundException e) {
		System.out.println("Library file not found:"+e.toString());
	    } catch (IOException ioe) {
		System.out.println("Error reading the file: " + ioe.toString() );
	    }
	}
    }

    /** Open a 'Open Lib' dialog box whenever a model is open that contains an object that is not
     *  part of the currently opened library. */
    public TclSnippet searchLibAction( String snippetName )
    {
	TclSnippet s;
	File f;
	FileReader fr;
	BufferedReader br;
	do {
	    JFileChooser fch = new JFileChooser("lib");
	    fch.setDialogTitle("Please locate library for " + snippetName );
	    int selected = fch.showOpenDialog(this.getContentPane());
	    if (selected == JFileChooser.APPROVE_OPTION) {
		f = fch.getSelectedFile();
		if (libManager . addLibrary( f . getPath() ))
		    toolBox.addLibraryPane( libManager.getLibrary(libManager.getLibraryCount()-1) );
	    } else {
		return null;
	    }
	    s = libManager.getSnippet(snippetName);
	} while ( s==null );
	return s;
    }

    /** Main procedure, simply creates a new NScript instance to initiate the program. */
    public static void main(String[] args)
    {
	NScript s = new NScript();
    }

    /** Implements the handling of events at frame window level. */
    class WindowEventHandler extends WindowAdapter {
	public void windowClosing(WindowEvent evt) {
	    if (model.dirty()) 
		if (!reallyClose())
		    return;
	    System.exit(0);
	}
    }

    /** Action definitions for the 'New' action. */
    class NewAction extends AbstractAction
    {
	public NewAction( String label, Icon icon )
	{
	    super( label, icon );
	}
	
	public void actionPerformed( ActionEvent ae )
	{
	    if (model.dirty())
		if (!reallyClose())
		    return;
					
	    model . newModel();
	}	
    }
    	
    /** Action implementation of the 'Open' option. */
    class OpenAction extends AbstractAction
    {
	public OpenAction( String label, Icon icon )
	{
	    super( label, icon );
	}
	
	public void actionPerformed( ActionEvent ae )
	{
	    if (model.dirty()) 
		if (!reallyClose())
		    return;

	    openFileAction();
	}
    }
    
    /** Action implementation of the 'Open Library' option. */
    class OpenLibAction extends AbstractAction
    {
	public OpenLibAction( String label, Icon icon )
	{
	    super( label, icon );
	}

	public void actionPerformed( ActionEvent ae )
	{
	    openLibraryAction();
	}
    }
    
    /** Action implementation of the 'Save' option. */
    class SaveAction extends AbstractAction
    {
	public SaveAction( String label, Icon icon )
	{
	    super( label, icon );
	}

	public void actionPerformed( ActionEvent ae )
	{
	    saveFileAction();
	}
    }

    /** Action implementation of the 'Quit' option. */
    class QuitAction extends AbstractAction
    {
	public QuitAction( String label, Icon icon )
	{
	    super( label, icon );
	}

	public void actionPerformed( ActionEvent ae )
	{
	    if (model.dirty()) 
		if (!reallyClose())
		    return;	
		
	    System.exit(0);
	}
    }

    /** Action implementation of the 'Clear' option. */
    class ClearAction extends AbstractAction
    {
	public ClearAction( String label, Icon icon )
	{
	    super( label, icon );
	}

	public void actionPerformed( ActionEvent ae )
	{
	    model . removeSelected();
	}
    }

    /** Action implementation of the 'About...' option. */
    class AboutAction extends AbstractAction
    {
	public AboutAction( String label, Icon icon )
	{
	    super( label, icon );
	}
	
	public void actionPerformed( ActionEvent ae )
	{
	    SAboutDialog d = new SAboutDialog( null, true );
	}
    }

    /** Action implementation of the 'Edit Array...' menu option. */
    class EditArrayAction extends AbstractAction
    {
	public EditArrayAction( String label, Icon icon )
	{
	    super( label, icon );
	}

	public void actionPerformed( ActionEvent ae )
	{
	    SArrayDialog arrayDialog = new SArrayDialog( model, null );
	}
    }

    /** Action implementation of the 'Export...' menu option. */
    class ExportAction extends AbstractAction
    {
	public ExportAction( String label, Icon icon )
	{
	    super( label, icon );
	}

	public void actionPerformed( ActionEvent ae )
	{
	    saveScript();
	}
    }

    /** Action implementation of the 'Run...' menu option. */
    class RunAction extends AbstractAction
    {
	public RunAction( String label, Icon icon )
	{
	    super( label, icon );
	}

	public void actionPerformed( ActionEvent ae )
	{
	    String fName = saveScript();
	
	    if (fName != null) {
		try {
		    Runtime.getRuntime().exec("ns "+fName);
		} catch( IOException ioe ) {
		    JOptionPane.showConfirmDialog(
						  getContentPane(),
						  "Error executing ns"+ioe.toString(),
						  "Warning",
						  JOptionPane.INFORMATION_MESSAGE);
		}
	    }
	}
    }

    /** A filter of '.nss' files to be used in an 'Open File' dialog box. */
    class LibFilter implements FileFilter
    {
	public LibFilter()
	{
	    super();
	}

	public boolean accept( File f )
	{
	    if (f.getName().indexOf( ".lib" ) == f.getName().length()-4)
		return true;
	    else
		return false;
	}

	public String getDescription()
	{
	    return "Library Files (*.lib)";
	}
    }
}

/* End of file */






