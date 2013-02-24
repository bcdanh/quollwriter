package com.quollwriter.ui;

import java.awt.event.*;

import java.net.*;

import java.util.*;

import javax.swing.*;

import com.quollwriter.*;

import com.quollwriter.data.*;

import com.quollwriter.ui.components.*;


public class ResearchItemDetailsEditPanel extends DetailsEditPanel
{

    private JTextField urlEdit = null;

    public ResearchItemDetailsEditPanel(Asset         a,
                                        ProjectViewer pv)
    {

        super (a,
               pv);

        this.urlEdit = UIUtils.createTextField ();

    }

    public String getViewDescription ()
    {
        
        String d = super.getViewDescription ();
        
        ResearchItem r = (ResearchItem) this.object;
        
        String url = r.getUrl ();
        
        if ((url != null)
            &&
            (url.trim ().length () > 0)
           )
        {
                            
            if (!url.toLowerCase ().trim ().startsWith ("http://"))
            {
                
                url = "http://" + url;
                
            }
            
            d = "<b>Web Page: </b>" + url + "<br /><br />" + d;
            
        }
        
        if (d == null)
        {
            
            d = "<i>No description.</i>";
            
        }

        return d;
        
    }    
    
    public String getEditHelpText ()
    {

        return null;

    }

    public void fillForSave ()
    {

        ResearchItem r = (ResearchItem) this.object;

        r.setUrl (this.urlEdit.getText ().trim ());

    }

    public void fillForEdit ()
    {

        ResearchItem r = (ResearchItem) this.object;

        this.urlEdit.setText (r.getUrl ());

    }

    public boolean canSave ()
    {

        return true;

    }

    public List<FormItem> getExtraEditItems ()
    {

        List<FormItem> items = new ArrayList ();
        items.add (new FormItem ("Web Page",
                                 this.urlEdit));

        return items;

    }

    public List<FormItem> getExtraViewItems ()
    {

        List<FormItem> items = new ArrayList ();

        return items;

    }

}
