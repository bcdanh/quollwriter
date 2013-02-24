package com.quollwriter.data;

import org.jdom.*;


public class ResearchItem extends Asset
{

    public static final String OBJECT_TYPE = "researchitem";

    private String url = null;

    public ResearchItem()
    {

        super (ResearchItem.OBJECT_TYPE);

    }

    public String getUrl ()
    {

        return this.url;

    }

    public void setUrl (String url)
    {

        this.url = url;

    }

    public void getChanges (NamedObject old,
                            Element     root)
    {

        ResearchItem ri = (ResearchItem) old;

        this.addFieldChangeElement (root,
                                    "url",
                                    ((old != null) ? ri.getUrl () : null),
                                    this.url);

    }

}
