package com.quollwriter.exporter;

import java.awt.event.*;

import java.io.*;

import java.text.*;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;

import com.gentlyweb.utils.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

import com.quollwriter.*;

import com.quollwriter.data.*;
import com.quollwriter.data.comparators.*;

import com.quollwriter.ui.*;
import com.quollwriter.ui.renderers.*;

import org.docx4j.convert.out.pdf.viaXSLFO.*;

import org.docx4j.jaxb.*;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.*;

import org.docx4j.wml.*;


public class PDFDocumentExporter extends MSWordDocXDocumentExporter
{

    protected void save (WordprocessingMLPackage wordMLPackage,
                         String                  name)
                  throws Exception
    {

        name = name.replace ('/',
                             '_');

        name = name.replace ('\\',
                             '_');

        Conversion c = new Conversion (wordMLPackage);

        FileOutputStream out = new FileOutputStream (new File (this.settings.outputDirectory.getPath () + "/" + name + Constants.PDF_FILE_EXTENSION));

        c.output (out);

        out.flush ();
        out.close ();

    }

}
