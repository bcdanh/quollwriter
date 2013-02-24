package com.quollwriter.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.*;

import java.io.*;

import java.util.*;

import javax.swing.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

import com.quollwriter.*;

import com.quollwriter.data.*;
import com.quollwriter.data.comparators.*;

import com.quollwriter.ui.components.*;


public class NewProjectPanel
{

    private JTextField     nameField = null;
    private JTextField     saveField = null;
    private JPasswordField passwordField2 = null;
    private JPasswordField passwordField = null;
    private JCheckBox      encryptField = null;

    public NewProjectPanel()
    {

    }

    public JPanel createPanel (final Component parent)
    {

        final NewProjectPanel _this = this;

        final FormLayout fl = new FormLayout ("right:p, 6px, fill:200px:grow, 2px, p",
                                              "p, 6px, p, 6px, p, 6px, p, 6px, p");

        final PanelBuilder builder = new PanelBuilder (fl);

        final CellConstraints cc = new CellConstraints ();

        this.nameField = UIUtils.createTextField ();

        builder.addLabel ("Name",
                          cc.xy (1,
                                 1));
        builder.add (this.nameField,
                     cc.xy (3,
                            1));

        builder.addLabel ("Save In",
                          cc.xy (1,
                                 3));

        String defDir = null;

        java.util.List pss = new ArrayList ();

        try
        {

            pss.addAll (Environment.getAllProjects ());

        } catch (Exception e)
        {

            Environment.logError ("Unable to get all project stubs",
                                  e);

        }

        final java.util.List projs = pss;

        Collections.sort (projs,
                          new ProjectSorter ());

        if (projs.size () > 0)
        {

            Project p = (Project) projs.get (0);

            defDir = p.getProjectDirectory ().getParentFile ().getPath ();

        } else
        {

            File projsDir = new File (Environment.getUserQuollWriterDir ().getPath () + "/" + Constants.DEFAULT_PROJECTS_DIR_NAME);

            projsDir.mkdirs ();

            defDir = projsDir.getPath ();

        }

        this.saveField = UIUtils.createTextField ();
        this.saveField.setText (defDir);

        // this.nameField.addKeyListener (k);
        // this.saveField.addKeyListener (k);

        builder.add (this.saveField,
                     cc.xy (3,
                            3));

        JButton findBut = new JButton (Environment.getIcon ("find",
                                                            Constants.ICON_MENU));

        findBut.addActionListener (new ActionAdapter ()
            {

                public void actionPerformed (ActionEvent ev)
                {

                    JFileChooser f = new JFileChooser ();
                    f.setDialogTitle ("Select a Directory");
                    f.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
                    f.setApproveButtonText ("Select");
                    f.setCurrentDirectory (new File (_this.saveField.getText ()));

                    // Need to run: attrib -r "%USERPROFILE%\My Documents" on XP to allow a new directory
                    // to be created in My Documents.

                    if (f.showOpenDialog (parent) == JFileChooser.APPROVE_OPTION)
                    {

                        _this.saveField.setText (f.getSelectedFile ().getPath ());

                    }

                }

            });

        builder.add (findBut,
                     cc.xy (5,
                            3));

        this.encryptField = new JCheckBox ("Encrypt this project?  You will be prompted for a password.");
        this.encryptField.setBackground (Color.WHITE);

        builder.add (this.encryptField,
                     cc.xyw (3,
                             5,
                             2));

        FormLayout pfl = new FormLayout ("right:p, 6px, 100px, 6px, p, 6px, fill:100px",
                                         "6px, p, 6px");

        PanelBuilder pbuilder = new PanelBuilder (pfl);

        this.passwordField = new JPasswordField ();

        pbuilder.addLabel ("Password",
                           cc.xy (1,
                                  2));

        pbuilder.add (this.passwordField,
                      cc.xy (3,
                             2));

        this.passwordField2 = new JPasswordField ();

        pbuilder.addLabel ("Confirm",
                           cc.xy (5,
                                  2));

        pbuilder.add (this.passwordField2,
                      cc.xy (7,
                             2));

        final JPanel ppanel = pbuilder.getPanel ();

        ppanel.setVisible (false);
        ppanel.setOpaque (false);

        builder.add (ppanel,
                     cc.xyw (3,
                             7,
                             2));

        this.encryptField.addActionListener (new ActionAdapter ()
            {

                public void actionPerformed (ActionEvent ev)
                {

                    ppanel.setVisible (_this.encryptField.isSelected ());

                    parent.repaint ();
                    
                    if (parent instanceof PopupWindow)
                    {
                        
                        ((PopupWindow) parent).resize ();
                        
                    }
                    
                }

            });

        JPanel p = builder.getPanel ();
        p.setOpaque (false);

        return p;

    }

    public boolean checkForm (Component parent)
    {

        String n = this.nameField.getText ().trim ();

        if (n.equals (""))
        {

            UIUtils.showMessage (parent,
                                 "Please provide a Name for the Project.");

            return false;

        }

        // See if the project already exists.
        File pf = new File (saveField.getText () + "/" + Utils.sanitizeForFilename (n));

        if (pf.exists ())
        {

            UIUtils.showMessage (parent,
                                 "A Project with name: " +
                                 n +
                                 " already exists.");

            return false;

        }

        String pwd = null;

        if (this.encryptField.isSelected ())
        {

            // Make sure a password has been provided.
            pwd = new String (this.passwordField.getPassword ()).trim ();

            String pwd2 = new String (this.passwordField2.getPassword ()).trim ();

            if (pwd.equals (""))
            {

                UIUtils.showMessage (parent,
                                     "Please provide a password for securing the Project files.");

                return false;

            }

            if (pwd2.equals (""))
            {

                UIUtils.showMessage (parent,
                                     "Please confirm your password.");

                return false;

            }

            if (!pwd.equals (pwd2))
            {

                UIUtils.showMessage (parent,
                                     "The passwords do not match.");

                return false;

            }

        }

        return true;

    }

    public File getSaveDirectory ()
    {

        return new File (this.saveField.getText ());

    }

    public String getPassword ()
    {

        String pwd = new String (this.passwordField.getPassword ());

        if (pwd.trim ().equals (""))
        {

            pwd = null;

        }

        return pwd;

    }

    public String getName ()
    {

        return this.nameField.getText ();

    }

}
