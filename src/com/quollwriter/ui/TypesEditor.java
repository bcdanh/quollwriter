package com.quollwriter.ui;

import java.awt.*;
import java.awt.event.*;

import java.io.File;

import java.net.*;

import java.text.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;
import javax.swing.tree.*;

import com.gentlyweb.utils.*;

import com.gentlyweb.xml.*;

import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;

import com.quollwriter.*;

import com.quollwriter.data.*;
import com.quollwriter.data.comparators.*;

import com.quollwriter.exporter.*;

import com.quollwriter.ui.components.*;
import com.quollwriter.ui.renderers.*;


public abstract class TypesEditor extends PopupWindow
{

    private Project     proj = null;
    private TypesHandler handler = null;
    private DefaultTableModel typeModel = null;

    public TypesEditor (ProjectViewer pv,
                        TypesHandler  handler)
    {

        super (pv);

        this.handler = handler;
        this.proj = pv.getProject ();

    }
    
    public abstract String getTypesName ();
    
    public abstract String getNewTypeHelp ();

    public String getHelpText ()
    {

        return null;

    }

    public JComponent getContentPanel ()
    {

        final TypesEditor _this = this;

        Box b = new Box (BoxLayout.Y_AXIS);

        b.setAlignmentX (Component.LEFT_ALIGNMENT);
        b.setOpaque (true);
        b.setBackground (null);

        b.add (UIUtils.createBoldSubHeader ("New Types",
                                            null));

        JTextPane tp = UIUtils.createHelpTextPane ("Enter the new types to add below, separate the types with commas or semi-colons.");

        tp.setBorder (new EmptyBorder (5,
                                       5,
                                       0,
                                       5));

        b.add (tp);

        final JTextField newTypes = UIUtils.createTextField ();
        newTypes.setAlignmentX (Component.LEFT_ALIGNMENT);

        final JTable typeTable = new JTable ();

        typeTable.setModel (new DefaultTableModel ()
        {

            public boolean isCellEditable (int row,
                                           int col)
            {

                return true;

            }

        });

        this.typeModel = (DefaultTableModel) typeTable.getModel ();

        this.reloadTypes ();

        Box fb = new Box (BoxLayout.X_AXIS);
        fb.setAlignmentX (Component.LEFT_ALIGNMENT);
        fb.add (newTypes);

        fb.setBorder (new EmptyBorder (5,
                                       5,
                                       5,
                                       5));
        b.add (fb);

        Box buts = new Box (BoxLayout.X_AXIS);

        final JButton add = new JButton ("Add");

        buts.add (add);

        final ActionAdapter aa = new ActionAdapter ()
        {

            public void actionPerformed (ActionEvent ev)
            {

                String n = newTypes.getText ();

                StringTokenizer t = new StringTokenizer (n,
                                                         ",;");

                while (t.hasMoreTokens ())
                {

                    String w = t.nextToken ().trim ();

                    DefaultTableModel m = (DefaultTableModel) typeTable.getModel ();

                    Vector r = new Vector ();
                    r.add (w);
                    m.insertRow (0,
                                 r);

                    _this.handler.addType (w,
                                           true);

                }

                newTypes.setText ("");

            }

        };

        add.addActionListener (aa);

        newTypes.addKeyListener (new KeyAdapter ()
            {

                public void keyPressed (KeyEvent ev)
                {

                    if (ev.getKeyCode () == KeyEvent.VK_ENTER)
                    {

                        aa.actionPerformed (null);

                    }

                }

            });

        buts.setAlignmentX (Component.LEFT_ALIGNMENT);
        buts.setBorder (new EmptyBorder (0,
                                         5,
                                         20,
                                         5));

        b.add (buts);

        b.add (UIUtils.createBoldSubHeader (Environment.replaceObjectNames (this.getTypesName ()),
                                            null));

        tp = UIUtils.createHelpTextPane (Environment.replaceObjectNames (this.getNewTypeHelp ()));

        tp.setBorder (new EmptyBorder (5,
                                       5,
                                       0,
                                       5));

        b.add (tp);

        typeTable.setAlignmentX (Component.LEFT_ALIGNMENT);
        typeTable.setOpaque (false);
        typeTable.setFillsViewportHeight (true);
        typeTable.setBorder (null);
        typeTable.setTableHeader (null);

        final JScrollPane ppsp = new JScrollPane (typeTable);

        // ppsp.setBorder (null);
        ppsp.setOpaque (false);
        ppsp.setAlignmentX (Component.LEFT_ALIGNMENT);
        ppsp.setBorder (new CompoundBorder (new EmptyBorder (5,
                                                             5,
                                                             5,
                                                             5),
                                            ppsp.getBorder ()));

        ppsp.setPreferredSize (new Dimension (500,
                                              200));
        ppsp.getViewport ().setOpaque (false);

        b.add (ppsp);

        buts = new Box (BoxLayout.X_AXIS);

        final JButton remove = new JButton ("Remove Selected");

        buts.add (remove);

        remove.addActionListener (new ActionAdapter ()
        {

            public void actionPerformed (ActionEvent ev)
            {

                DefaultTableModel m = (DefaultTableModel) typeTable.getModel ();

                int[] selection = typeTable.getSelectedRows ();

                for (int i = selection.length - 1; i > -1; i--)
                {

                    String s = (String) m.getValueAt (selection[i],
                                                      0);

                    // Remove the row.
                    m.removeRow (selection[i]);
                    
                    _this.handler.removeType (s,
                                              true);

                }

                ((DefaultListSelectionModel) typeTable.getSelectionModel ()).clearSelection ();

            }

        });

        buts.setAlignmentX (Component.LEFT_ALIGNMENT);
        buts.setBorder (new EmptyBorder (0,
                                         5,
                                         20,
                                         5));

        b.add (buts);

        return b;

    }

    public void reloadTypes ()
    {

        Set<String> types = this.handler.getTypes ();

        Vector typeData = new Vector ();

        for (String i : types)
        {
            
            Vector d = new Vector ();
            d.add (i);
                        
            typeData.add (d);
            
        }

        Vector<String> cols = new Vector ();
        cols.add ("Type");

        this.typeModel.setDataVector (typeData,
                                      cols);
        
    }

    public JButton[] getButtons ()
    {

        final TypesEditor _this = this;

        JButton b = new JButton ("Finish");

        b.addActionListener (new ActionAdapter ()
            {

                public void actionPerformed (ActionEvent ev)
                {

                    _this.close ();

                }

            });

        JButton[] buts = new JButton[1];
        buts[0] = b;

        return buts;

    }

}
