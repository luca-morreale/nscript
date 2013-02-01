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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

/**
 * Implements the main application object for nscript. Interface element objects
 * are created, library modules are loaded, and the instance of the model is
 * created.
 */
public class NScript extends JFrame {

    static final long serialVersionUID = 42L;
    /**
     * Library manager for the application. Contains the opened libraries.
     */
    private TclLibraryManager libManager;
    /**
     * An object representing the ns simulation environment, present in every
     * simulation script.
     */
    private TclSnippet env;
    /**
     * A view containing the libraries and their available objects.
     */
    private SToolBar toolBox;
    /**
     * A view that imlements the edition of object attributes.
     */
    private SObjectBrowser objectBrowser;
    /**
     * The main editing view, where the topology, transportation, and
     * application components can be edited.
     */
    private DMView mainView;
    /**
     * The world view, which is a list of the available objects in the
     * simulation.
     */
    private NSWorldView worldView;
    /**
     * The model that stores the simulation objects.
     */
    private NSModel model;
    /**
     * The text view that shows the TclScript that results from the simulation
     * script.
     */
    private JTextArea tclView;
    /**
     * Last path explored file File Chooser
     */
    private String lastpath;

    /**
     * Main constructor. Its responsibilities include creating the title dialog
     * box, the menu bar, toolbar, a library manager to store opened libraries,
     * its graphic representation (SToolBar), create the model, and other
     * relevant views, like the objects browser.
     */
    public NScript() {
        super(Messages.tr("nscript"));
        SAboutDialog ad = new SAboutDialog(null, false);
        Container c = this.getContentPane();

        // Create a new library manager
        libManager = new TclLibraryManager();
        toolBox = new SToolBar(this, libManager);

        // Prepare toolbar and menu (needs an initialized toolBox!)
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        JToolBar toolBar = new JToolBar();
        prepareToolBarAndMenu(toolBar, menuBar);
        toolBar.setOrientation(SwingConstants.HORIZONTAL);

        // Allows for "exit confirm"
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Set initial path
        this.lastpath = "examples";

        // ------------- CREATE MODEL AND MAIN VIEW ---------------
        ad.setMessage(Messages.tr("reading_env"));
        model = new NSModel(readEnvironment("settings/environment"), toolBox);
        ad.setMessage(Messages.tr("reading_libs"));
        addDefaultLibraries("settings/deflibs");
        mainView = new DMView(model);

        ad.setMessage(Messages.tr("creating_gui"));
        objectBrowser = new SObjectBrowser(model);

        worldView = new NSWorldView(model);
        JTabbedPane wv_tp = new JTabbedPane(SwingConstants.BOTTOM);
        wv_tp.addTab(Messages.tr("object_browser"), objectBrowser);
        wv_tp.addTab(Messages.tr("world_view"), worldView);

        tclView = new JTextArea(model.toTcl());
        Font tclFont = new Font("Monospaced", Font.PLAIN, 14);
        tclView.setFont(tclFont);
        JScrollPane sp_tcl = new JScrollPane(tclView);

        model.setViews(mainView, tclView, objectBrowser, worldView);
        JScrollPane sp_edit = new JScrollPane(mainView);
        mainView.setPreferredSize(new Dimension(612, 792));

        JSplitPane helpers = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toolBox, wv_tp);
        helpers.setOneTouchExpandable(true);
        helpers.setDividerSize(2);
        helpers.setDividerLocation(200);

        // Script view as another tab
        JTabbedPane tp = new JTabbedPane(SwingConstants.BOTTOM);
        tp.addTab(Messages.tr("visual"), sp_edit);
        tp.addTab(Messages.tr("tcl_script"), sp_tcl);

        // Simultaneous view of the scripts
        // JSplitPane tp = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, sp_edit, sp_tcl );
        // tp.setDividerSize(4);
        // tp.setDividerLocation(400);

        JSplitPane tabbedView = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tp, helpers);
        tabbedView.setOneTouchExpandable(true);
        tabbedView.setDividerSize(4);
        tabbedView.setDividerLocation(612);

        // Add components to frame
        Container g = new Container();
        g.setLayout(new BorderLayout());
        g.add(menuBar, BorderLayout.NORTH);
        g.add(toolBar, BorderLayout.CENTER);
        c.add(g, BorderLayout.NORTH);
        // c.add(toolBar,BorderLayout.WEST);
        c.add(tabbedView, BorderLayout.CENTER);

        addWindowListener(new WindowEventHandler());
        // getToolkit().getScreenSize();
        setSize(800, 600);
        setLocation(0, 0);
        ad.setMessage(Messages.tr("done"));
        setVisible(true);
        ad.dispose();
    }

    /**
     * Reads the default values for the simulation environmen (the ns object),
     * from a file.
     */
    private NSObject readEnvironment(String envFileName) {
        BufferedReader br = null;
        Reader reader;
        NSObject o;
        String newLine;
        StringBuilder s = new StringBuilder(20); //FIXME magic number

        try {
            reader = new InputStreamReader(
                    new FileInputStream(envFileName), "utf-8");
            br = new BufferedReader(reader);
            do {
                newLine = br.readLine();
                s.append(newLine);
            } while (newLine.indexOf("end") < 0);
        } catch (FileNotFoundException fnfe) {
            LOG.log(Level.WARNING, "{0}{1}", new Object[]{
                        Messages.tr("env_default_not_found"),
                        fnfe.toString()
                    });
            return null;
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "{0}{1}", new Object[]{
                        Messages.tr("env_reading_error"),
                        ioe.toString()
                    });
            return null;
        } finally {
            close(br);
        }

        env = new TclSnippet(s.toString());
        o = new NSEntity(env, "ns", 0.01, 0.01);
        env.instantiateNSObject(o);
        return o;
    }

    /**
     * Prepares the toolbar and menu items, and relates them to the handling
     * routines.
     */
    private void prepareToolBarAndMenu(JToolBar toolbar, JMenuBar menuBar) {
        Icon newIcon = new ImageIcon(NScript.class.getResource("/pixmaps/new.png"));
        Icon openIcon = new ImageIcon(NScript.class.getResource("/pixmaps/open.png"));
        Icon saveIcon = new ImageIcon(NScript.class.getResource("/pixmaps/save.png"));
        Icon openLibIcon = new ImageIcon(NScript.class.getResource("/pixmaps/openlib.png"));
        Icon indexIcon = new ImageIcon(NScript.class.getResource("/pixmaps/index.png"));
        Icon exportIcon = new ImageIcon(NScript.class.getResource("/pixmaps/nsexport.png"));
        Icon runIcon = new ImageIcon(NScript.class.getResource("/pixmaps/run.png"));
        //Icon selectIcon = new ImageIcon(SToolBar.class.getResource("/pixmaps/select.png"));

        NewAction newA = new NewAction(Messages.tr("new"), newIcon);
        OpenAction openA = new OpenAction(Messages.tr("open"), openIcon);
        OpenLibAction openLibA = new OpenLibAction(Messages.tr("open_lib"), openLibIcon);
        SaveAction saveA = new SaveAction(Messages.tr("save"), saveIcon);
        EditArrayAction editArrayA = new EditArrayAction(Messages.tr("arrays_edit"), indexIcon);
        ExportAction exportA = new ExportAction(Messages.tr("ns_export"), exportIcon);
        RunAction runA = new RunAction(Messages.tr("ns_run"), runIcon);

        // Add things to the toolbar and menubar
        JButton b = toolbar.add(newA);
        b.setToolTipText(Messages.tr("script_new"));
        b = toolbar.add(openA);
        b.setToolTipText(Messages.tr("script_open"));
        b = toolbar.add(openLibA);
        b.setToolTipText(Messages.tr("open_lib"));
        b = toolbar.add(saveA);
        b.setToolTipText(Messages.tr("script_save"));
        toolbar.addSeparator();
        b = toolbar.add(editArrayA);
        b.setToolTipText(Messages.tr("arrays_edit"));
        toolbar.addSeparator();
        b = toolbar.add(exportA);
        b.setToolTipText(Messages.tr("ns_export"));
        b = toolbar.add(runA);
        b.setToolTipText(Messages.tr("ns_run"));

        // Now the menu bar (easy)

        JMenu fileMenu = new JMenu(Messages.tr("file"));
        fileMenu.add(newA);
        fileMenu.add(openA);
        fileMenu.add(openLibA);
        fileMenu.add(saveA);
        fileMenu.addSeparator();
        fileMenu.add(new QuitAction(Messages.tr("quit"), null));

        // Edit menu
        JMenu editMenu = new JMenu(Messages.tr("edit"));
        editMenu.add(new ClearAction(Messages.tr("clear"), null));
        editMenu.addSeparator();
        editMenu.add(editArrayA);

        // NS menu
        JMenu scriptMenu = new JMenu(Messages.tr("script"));
        scriptMenu.add(exportA);
        scriptMenu.addSeparator();
        scriptMenu.add(runA);

        // Help menu
        JMenu helpMenu = new JMenu(Messages.tr("help"));
        helpMenu.add(new AboutAction(Messages.tr("about"), null));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(scriptMenu);
        menuBar.add(helpMenu);
    }

    /**
     * Reads the list of default libraries from a file, and opens the
     * corresponding libraries, which are assumed to be stored in the 'libs'
     * subdirectory of the 'bin' directory.
     */
    private void addDefaultLibraries(String defFileName) {
        Reader reader;
        BufferedReader br = null;
        String line = "";

        try {
            reader = new InputStreamReader(
                    new FileInputStream(defFileName), "utf-8");
            br = new BufferedReader(reader);

            for (line = br.readLine(); line != null; line = br.readLine()) {
                if (libManager.addLibrary(line)) {
                    toolBox.addLibraryPane(
                            libManager.getLibrary(libManager.getLibraryCount() - 1));
                }
            }
        } catch (FileNotFoundException fnfe) {
            LOG.log(Level.WARNING, "{0}{1}, {2}", new Object[]{
                        Messages.tr("lib_not_found"),
                        line, fnfe.toString()
                    });
        } catch (IOException ioe) {
            LOG.log(Level.SEVERE, "{0}{1}", new Object[]{
                        Messages.tr("lib_read_error"),
                        ioe.toString()
                    });
        } finally {
            close(br);
        }
    }

    /**
     * Handles the 'Open Library' item of the 'File' menu by opening an open
     * file dialog, to allow the user to select a library to open.
     */
    public void openLibraryAction() {
        File f;
        JFileChooser fch = new JFileChooser("lib");
        fch.setDialogTitle(Messages.tr("lib_select_open"));
        int selected = fch.showOpenDialog(this.getContentPane());
        if (selected == JFileChooser.APPROVE_OPTION) {
            f = fch.getSelectedFile();
            if (libManager.addLibrary(f.getPath())) {
                toolBox.addLibraryPane(libManager.getLibrary(libManager.getLibraryCount() - 1));
            }
        }
    }

    /**
     * Handles the save script option, that allows the user to export the
     * current script as a Tcl script, runnable script.
     *
     * @return
     */
    public String saveScript() {
        Writer writer = null;

        JFileChooser fch = new JFileChooser(this.lastpath);
        fch.setDialogTitle(Messages.tr("script_select_name"));
        // fch.setFileFilter(ff);
        fch.setSelectedFile(new File("Untitled.tcl"));
        int selected = fch.showSaveDialog(this.getContentPane());
        if (selected == JFileChooser.APPROVE_OPTION) {
            try {
                this.updateLastPath(fch.getSelectedFile());
                writer = new OutputStreamWriter(
                        new FileOutputStream(fch.getSelectedFile()), "utf-8");
                writer.write(model.toTcl());
                return fch.getSelectedFile().getAbsolutePath();
            } catch (FileNotFoundException e) {
                LOG.log(Level.SEVERE, "{0} {1}", new Object[]{
                            Messages.tr("file_open_error"), e.toString()});
                return null;
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, "{0} {1}", new Object[]{
                            Messages.tr("file_write_error"), ioe.toString()});
                return null;
            } finally {
                close(writer);
            }
        }
        return null;
    }

    /**
     * Handles the 'Save' item of the 'File' menu, by allowing the user to
     * select a place to store the current script in a propiertary format. This
     * will soon be replaced by an XML format.
     *
     * @return
     */
    public String saveFileAction() {
        Writer writer = null;

        JFileChooser fch = new JFileChooser(this.lastpath);
        fch.setDialogTitle(Messages.tr("save_as"));
        // fch.setFileFilter(ff);
        fch.setSelectedFile(new File("Untitled.nss"));
        int selected = fch.showSaveDialog(this.getContentPane());
        if (selected == JFileChooser.APPROVE_OPTION) {
            try {
                this.updateLastPath(fch.getSelectedFile());
                writer = new OutputStreamWriter(
                        new FileOutputStream(fch.getSelectedFile()), "utf-8");
                writer.write(model.toString());
                writer.close();
                model.setDirty(false);
                return fch.getSelectedFile().getAbsolutePath();
            } catch (FileNotFoundException e) {
                LOG.log(Level.SEVERE, "{0} {1}", new Object[]{
                            Messages.tr("file_open_error"), e.toString()});
                return null;
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, "{0} {1}", new Object[]{
                            Messages.tr("file_write_error"), ioe.toString()});
                return null;
            } finally {
                close(writer);
            }
        }
        return null;
    }

    /**
     * Handles closing of a model / application when the current simulation
     * script has suffered modifications.
     *
     * @return
     */
    public boolean reallyClose() {
        int selectedValue = JOptionPane.showConfirmDialog(
                getContentPane(),
                Messages.tr("close_confirm"),
                Messages.tr("close_confirm_title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return (selectedValue == 0);
    }

    /**
     * Set directory in which next File Chooser will open
     */
    void updateLastPath(File chosenfile) {
        this.lastpath = chosenfile.getPath();
    }

    /**
     * Implements the "Open" option of the "File" menu. Open a dialog box to let
     * the user select a file, and opens it.
     */
    public void openFileAction() {
        int nA, nO;
        int i, selected;
        String sName;
        NSObject o;
        NSRelation or;
        TclSnippet s;

        JFileChooser fch = new JFileChooser(this.lastpath);
        fch.setDialogTitle(Messages.tr("script_open"));
        selected = fch.showOpenDialog(this.getContentPane());
        if (selected == JFileChooser.APPROVE_OPTION) {
            this.updateLastPath(fch.getSelectedFile());
            model.newModel();
            try {
                Reader reader = new InputStreamReader(
                        new FileInputStream(fch.getSelectedFile()), "utf-8");
                BufferedReader br = new BufferedReader(reader);

                // Read arrays
                nA = Integer.parseInt(br.readLine());
                for (i = 0; i < nA; i++) {
                    model.addArray(br.readLine(), Integer.parseInt(br.readLine()));
                }

                // Read objects (gulp)
                nO = Integer.parseInt(br.readLine());
                br.readLine(); // Skip snippet and name information
                ((NSObject) model.getObjectAt(0)).setName(br.readLine());
                ((NSEntity) model.getObjectAt(0)).fromString(br);
                for (i = 1; i < nO; i++) {
                    sName = br.readLine();
                    s = libManager.getSnippet(sName);
                    if (s == null) {
                        s = searchLibAction(sName);
                    }
                    if (s == null) {
                        model.newModel();
                        model.updateAllViews();
                        model.setDirty(false);
                        return;
                    }
                    if (s.isRelation()) {
                        or = new NSRelation(s, br.readLine(), null, null);
                        s.instantiateNSObject(or);
                        or.fromString(br, model);
                        model.addObject(or);
                    } else {
                        o = new NSEntity(s, br.readLine(), 0.0, 0.0);
                        s.instantiateNSObject(o);
                        o.fromString(br);
                        model.addObject(o);
                    }
                }
                model.updateAllViews();
                model.setDirty(false);
            } catch (FileNotFoundException e) {
                LOG.log(Level.SEVERE, "{0} {1}", new Object[]{
                            Messages.tr("file_open_error"), e.toString()});
            } catch (IOException ioe) {
                LOG.log(Level.SEVERE, "{0} {1}", new Object[]{
                            Messages.tr("file_write_error"), ioe.toString()});
            }
        }
    }

    /**
     * Open a 'Open Lib' dialog box whenever a model is open that contains an
     * object that is not part of the currently opened library.
     *
     * @param snippetName
     * @return
     */
    public TclSnippet searchLibAction(String snippetName) {
        TclSnippet s;
        File f;
        do {
            JFileChooser fch = new JFileChooser("lib");
            fch.setDialogTitle(Messages.tr("lib_locate") + " " + snippetName);
            int selected = fch.showOpenDialog(this.getContentPane());
            if (selected == JFileChooser.APPROVE_OPTION) {
                f = fch.getSelectedFile();
                if (libManager.addLibrary(f.getPath())) {
                    toolBox.addLibraryPane(libManager.getLibrary(libManager.getLibraryCount() - 1));
                }
            } else {
                return null;
            }
            s = libManager.getSnippet(snippetName);
        } while (s == null);
        return s;
    }

    /**
     * Safely close the passed in resource or log error.
     *
     * @param stream the stream to be closed
     */
    private void close(Closeable stream) {
        if (stream == null) {
            return;
        }

        try {
            stream.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Fatal I/O error: {0}", e.toString());
        }
    }

    /**
     * Implements the handling of events at frame window level.
     */
    private class WindowEventHandler extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent evt) {
            if (!model.dirty() || reallyClose()) {
                dispose();
            }
        }
    }

    /**
     * Action definitions for the 'New' action.
     */
    private class NewAction extends AbstractAction {

        static final long serialVersionUID = 42L;

        NewAction(String label, Icon icon) {
            super(label, icon);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (model.dirty()) {
                if (!reallyClose()) {
                    return;
                }
            }

            model.newModel();
        }
    }

    /**
     * Action implementation of the 'Open' option.
     */
    private class OpenAction extends AbstractAction {

        static final long serialVersionUID = 42L;

        OpenAction(String label, Icon icon) {
            super(label, icon);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (model.dirty()) {
                if (!reallyClose()) {
                    return;
                }
            }

            openFileAction();
        }
    }

    /**
     * Action implementation of the 'Open Library' option.
     */
    private class OpenLibAction extends AbstractAction {

        static final long serialVersionUID = 42L;

        OpenLibAction(String label, Icon icon) {
            super(label, icon);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            openLibraryAction();
        }
    }

    /**
     * Action implementation of the 'Save' option.
     */
    private class SaveAction extends AbstractAction {

        static final long serialVersionUID = 42L;

        SaveAction(String label, Icon icon) {
            super(label, icon);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            saveFileAction();
        }
    }

    /**
     * Action implementation of the 'Quit' option.
     */
    private class QuitAction extends AbstractAction {

        static final long serialVersionUID = 42L;

        QuitAction(String label, Icon icon) {
            super(label, icon);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (!model.dirty() || reallyClose()) {
                dispose();
            }
        }
    }

    /**
     * Action implementation of the 'Clear' option.
     */
    private class ClearAction extends AbstractAction {

        static final long serialVersionUID = 42L;

        ClearAction(String label, Icon icon) {
            super(label, icon);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            model.removeSelected();
        }
    }

    /**
     * Action implementation of the 'About...' option.
     */
    private static class AboutAction extends AbstractAction {

        static final long serialVersionUID = 42L;

        AboutAction(String label, Icon icon) {
            super(label, icon);
        }

        @Override
        @SuppressWarnings("ResultOfObjectAllocationIgnored")
        public void actionPerformed(ActionEvent ae) {
            new SAboutDialog(null, true);
        }
    }

    /**
     * Action implementation of the 'Edit Array...' menu option.
     */
    private class EditArrayAction extends AbstractAction {

        static final long serialVersionUID = 42L;

        EditArrayAction(String label, Icon icon) {
            super(label, icon);
        }

        @Override
        @SuppressWarnings("ResultOfObjectAllocationIgnored")
        public void actionPerformed(ActionEvent ae) {
            new SArrayDialog(model, null);
        }
    }

    /**
     * Action implementation of the 'Export...' menu option.
     */
    private class ExportAction extends AbstractAction {

        static final long serialVersionUID = 42L;

        ExportAction(String label, Icon icon) {
            super(label, icon);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            saveScript();
        }
    }

    /**
     * Action implementation of the 'Run...' menu option.
     */
    private class RunAction extends AbstractAction {

        static final long serialVersionUID = 42L;

        RunAction(String label, Icon icon) {
            super(label, icon);
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            String fName = saveScript();

            if (fName != null) {
                try {
                    Runtime.getRuntime().exec("ns " + fName);
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(
                            getContentPane(),
                            ioe.toString(),
                            Messages.tr("ns_exec_error"),
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * A filter of '.nss' files to be used in an 'Open File' dialog box.
     */
    private static class LibFilter implements FileFilter {

        LibFilter() {
            super();
        }

        @Override
        public boolean accept(File f) {
            return (f.getName().indexOf(".lib") == f.getName().length() - 4);
        }

        public String getDescription() {
            return Messages.tr("lib_description");
        }
    }
    private static final Logger LOG = Logger.getLogger(NScript.class.getName());
}
