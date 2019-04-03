package com.quollwriter.ui.fx;

import java.io.*;

import java.util.*;

import javafx.beans.property.*;
import javafx.scene.input.*;
import javafx.event.*;
import javafx.scene.layout.*;

import com.quollwriter.*;
import com.quollwriter.data.*;
import com.quollwriter.ui.fx.components.*;
import com.quollwriter.ui.fx.viewers.*;

import static com.quollwriter.uistrings.UILanguageStringsManager.getUILanguageStringProperty;
import static com.quollwriter.LanguageStrings.*;

public class NewProjectPanel extends VBox
{

    private QuollTextField     name = null;
    private QuollFileField     saveDir = null;
    private QuollPasswordField passwords = null;
    private QuollCheckBox      encrypt = null;
    private Project        project = null;
    private Form               form = null;
    private AbstractViewer viewer = null;

    public NewProjectPanel (AbstractViewer viewer,
                            StringProperty desc)
    {

        final NewProjectPanel _this = this;

        this.viewer = viewer;

        this.setFillWidth (true);

        this.name = QuollTextField.builder ()
            .styleClassName (StyleClassNames.NAME)
            .build ();

        File defDir = Environment.getDefaultSaveProjectDirPath ().toFile ();

        this.saveDir = QuollFileField.builder ()
            .styleClassName (StyleClassNames.SAVE)
            .initialFile (defDir)
            .limitTo (QuollFileField.Type.directory)
            .chooserTitle (newprojectpanel,find,title)
            .withViewer (viewer)
            .build ();

        this.encrypt = QuollCheckBox.builder ()
            .selected (false)
            .label (getUILanguageStringProperty (Arrays.asList (newprojectpanel,labels,LanguageStrings.encrypt)))
            .build ();

        this.passwords = QuollPasswordField.builder ()
            .build ();

        Runnable r = () ->
        {

            _this.createProject ();

        };

        UIUtils.addDoOnReturnPressed (this.name,
                                      r);
        UIUtils.addDoOnReturnPressed (this.passwords.getPasswordField1 (),
                                      r);
        UIUtils.addDoOnReturnPressed (this.passwords.getPasswordField2 (),
                                      r);

        this.form = Form.builder ()
            .description (desc)
            .item (getUILanguageStringProperty (Arrays.asList (newprojectpanel,labels,LanguageStrings.name)),
                   this.name)
            .item (getUILanguageStringProperty (Arrays.asList (newprojectpanel,labels,savein)),
                   this.saveDir)
            .item (this.encrypt)
            .item (this.passwords)
            .confirmButton (getUILanguageStringProperty (Arrays.asList (newprojectpanel,buttons,create)))
            .cancelButton (getUILanguageStringProperty (Arrays.asList (newprojectpanel,buttons,cancel)))
            .build ();

        this.form.setOnConfirm (ev ->
        {

            r.run ();

        });

        this.passwords.getParent ().managedProperty ().bind (this.passwords.getParent ().visibleProperty ());
        this.passwords.getParent ().setVisible (false);
        this.encrypt.selectedProperty ().addListener ((v, oldv, newv) ->
        {

            _this.passwords.getParent ().setVisible (newv);

        });

        this.getChildren ().add (this.form);

    }

    public void setOnCreate (EventHandler<ActionEvent> ev)
    {
/*
        this.addEventHandler (ActionEvent.ACTION,
                              ev);
*/
    }

    public void setOnCancel (EventHandler<Form.FormEvent> ev)
    {

        this.form.setOnCancel (ev);

    }

    public void setProject (Project p)
    {

        this.project = p;

    }

    public boolean createProject ()
    {

        if (!this.checkForm ())
        {

            return false;

        }

        Project proj = this.project;

        if (proj == null)
        {

            proj = new Project (this.getName ());

        } else {

            proj.setName (this.getName ());

        }

        AbstractProjectViewer pj = null;

        try
        {

            pj = Environment.getProjectViewerForType (proj);

        } catch (Exception e)
        {

            Environment.logError ("Unable to create new project: " +
                                  proj,
                                  e);

            ComponentUtils.showErrorMessage (this.viewer,
                                             getUILanguageStringProperty (newprojectpanel,actionerror));
                                      //"Unable to create new project: " + proj.getName ());

            return false;

        }

        try
        {

            pj.newProject (this.getSaveDirectory ().toPath (),
                           proj,
                           this.getPassword ());

        } catch (Exception e)
        {

            Environment.logError ("Unable to create new project: " +
                                  proj,
                                  e);

            ComponentUtils.showErrorMessage (this.viewer,
                                             getUILanguageStringProperty (newprojectpanel,actionerror));
                                      //"Unable to create new project: " + proj.getName ());

            return false;

        }

        this.fireEvent (new ActionEvent ());

        return true;

    }

    public boolean checkForm ()
    {

        List<String> prefix = Arrays.asList (newprojectpanel,errors);

        this.form.hideError ();

        String n = this.name.getText ().trim ();

        if (n.equals (""))
        {
System.out.println ("HERE");
            this.form.showError (getUILanguageStringProperty (Utils.newList (prefix,novalue)));
            return false;

        }

        // See if the project already exists.
        File pf = new File (saveDir.getFile (), Utils.sanitizeForFilename (n));

        if (pf.exists ())
        {

            this.form.showError (getUILanguageStringProperty (Utils.newList (prefix,valueexists)));
            return false;

        }

        if (this.encrypt.isSelected ())
        {

            // Make sure a password has been provided.
            String pwd = this.passwords.getPassword1 ();

            String pwd2 = this.passwords.getPassword2 ();
System.out.println ("HERE3: " + pwd);
            if (pwd.equals (""))
            {
System.out.println ("HREE4");
                this.form.showError (getUILanguageStringProperty (Utils.newList (prefix,nopassword)));
                                       //"Please provide a password for securing the {project}.");
                return false;

            }

            if (pwd2.equals (""))
            {
System.out.println ("HREE5");
                this.form.showError (getUILanguageStringProperty (Utils.newList (prefix,confirmpassword)));
                                       //"Please confirm your password.");
                return false;

            }

            if (!pwd.equals (pwd2))
            {
System.out.println ("HERE2");
                this.form.showError (getUILanguageStringProperty (Utils.newList (prefix,nomatch)));
                                       //"The passwords do not match.");
                return false;

            }

        }

        return true;

    }

    public File getSaveDirectory ()
    {

        return this.saveDir.getFile ();

    }

    public String getPassword ()
    {

        if (!this.encrypt.isSelected ())
        {

            return null;

        }

        return this.passwords.getPassword1 ();

    }

    public void setName (String n)
    {

        this.name.setText (n);

    }

    public String getName ()
    {

        return this.name.getText ();

    }

}