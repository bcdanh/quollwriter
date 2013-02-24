package com.quollwriter.ui;

import java.awt.*;
import java.awt.event.*;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.gentlyweb.utils.*;

import com.jgoodies.forms.factories.*;

import com.quollwriter.*;

import com.quollwriter.ui.components.*;


public abstract class PopupWizard extends PopupWindow
{

    private Box                     contentBox = null;
    private WizardStep              current = null;
    private String                  currentStage = null;
    private Box                     contentPanel = null;
    private Map<String, WizardStep> stages = new LinkedHashMap ();
    private JButton                 cancelBut = null;
    private JButton                 nextBut = null;
    private JButton                 prevBut = null;
    private Header                  header = null;

    public PopupWizard(AbstractProjectViewer pv)
    {

        super (pv);

    }

    public void init ()
    {

        this.contentBox = new Box (BoxLayout.Y_AXIS);

        this.contentPanel = new Box (BoxLayout.X_AXIS);
        this.contentPanel.setAlignmentX (JComponent.LEFT_ALIGNMENT);
        this.contentPanel.setOpaque (false);
        this.contentPanel.setPreferredSize (new Dimension (300,
                                                           this.getMaximumContentHeight ()));
        this.contentPanel.setBorder (new EmptyBorder (0,
                                                      10,
                                                      10,
                                                      10));

        this.header = UIUtils.createBoldSubHeader ("",
                                                   null);
        this.header.setBorder (new CompoundBorder (new MatteBorder (0,
                                                                    0,
                                                                    1,
                                                                    0,
                                                                    Environment.getBorderColor ()),
                                                   new EmptyBorder (0,
                                                                    0,
                                                                    2,
                                                                    0)));
        
        this.header.setAlignmentX (JComponent.LEFT_ALIGNMENT);
        this.contentBox.add (this.header);
        this.contentBox.add (Box.createVerticalStrut (10));

        this.contentBox.add (this.contentPanel);

        final PopupWizard _this = this;

        this.prevBut = new JButton ();
        this.prevBut.setText ("< Back");
        this.prevBut.setEnabled (false);

        this.prevBut.addActionListener (new ActionAdapter ()
            {

                public void actionPerformed (ActionEvent ev)
                {

                    WizardStep ws = null;

                    String prev = _this.getPreviousStage (_this.currentStage);

                    if (prev != null)
                    {

                        ws = _this.stages.get (prev);

                        if (ws == null)
                        {

                            try
                            {

                                ws = _this.getStage (prev);

                            } catch (Exception e)
                            {

                                Environment.logError ("Unable to get stage for: " +
                                                      prev,
                                                      e);

                            }

                            _this.stages.put (prev,
                                              ws);

                        }

                        if (!_this.handleStageChange (_this.currentStage,
                                                      prev))
                        {

                            return;

                        }

                        _this.contentPanel.remove (_this.current.panel);

                        _this.current = ws;
                        _this.currentStage = prev;

                        _this.initUI ();

                    }

                    _this.enableButtons ();

                    /*
                            _this.setSize (new Dimension (_this.getSize ().width,
                                                          _this.getPreferredSize ().height));
                     */
                    _this.repaint ();

                }

            });

        this.nextBut = new JButton ();
        this.nextBut.setText ("Next >");

        this.nextBut.addActionListener (new ActionAdapter ()
            {

                public void actionPerformed (ActionEvent ev)
                {

                    WizardStep ws = null;

                    String next = _this.getNextStage (_this.currentStage);

                    if (next != null)
                    {

                        ws = _this.stages.get (next);

                        if (ws == null)
                        {

                            try
                            {

                                ws = _this.getStage (next);

                            } catch (Exception e)
                            {

                                Environment.logError ("Unable to get stage: " +
                                                      next,
                                                      e);

                            }

                            if (ws != null)
                            {

                                _this.stages.put (next,
                                                  ws);

                            }

                        }

                        if (!_this.handleStageChange (_this.currentStage,
                                                      next))
                        {

                            return;

                        }

                        _this.contentPanel.remove (_this.current.panel);

                        _this.current = ws;
                        _this.currentStage = next;

                        _this.initUI ();

                    } else
                    {

                        if (_this.handleFinish ())
                        {

                            _this.setVisible (false);
                            _this.dispose ();

                            return;

                        }

                    }

                    _this.enableButtons ();
/*
                _this.setSize (new Dimension (_this.getSize ().width,
                                              _this.getPreferredSize ().height));
*/
                    _this.repaint ();

                }

            });

        this.cancelBut = new JButton ();
        this.cancelBut.setText ("Cancel");

        final ActionAdapter cancel = new ActionAdapter ()
        {

            public void actionPerformed (ActionEvent ev)
            {

                _this.handleCancel ();

                _this.setVisible (false);
                _this.dispose ();

            }

        };

        this.addWindowListener (new WindowAdapter ()
        {
           
            public void windowClosing (WindowEvent ev)
            {
                
                cancel.actionPerformed (null);
                
            }
            
        });

        this.cancelBut.addActionListener (cancel);

        super.init ();

        String startStage = this.getStartStage ();

        // Get the stage.
        WizardStep ws = this.getStage (startStage);
        this.stages.put (startStage,
                         ws);

        this.current = ws;
        this.currentStage = startStage;

        this.initUI ();

        this.enableButtons ();

        this.handleStageChange (null,
                                this.currentStage);

        SwingUtilities.invokeLater (new Runnable ()
        {

            public void run ()
            {

                try
                {

                    Thread.sleep (500);
                    
                } catch (Exception e) {
                    
                    // Ignore.
                    
                }

                _this.toFront ();
                
            }
            
        });

    }

    public JComponent getContentPanel ()
    {

        return this.contentBox;

    }

    public JButton[] getButtons ()
    {

        JButton[] buts = { this.prevBut, this.nextBut, this.cancelBut };

        return buts;

    }

    public void enableButton (String  name,
                              boolean enable)
    {

        if ((name.equals ("next")) ||
            (name.equals ("finish")))
        {

            this.nextBut.setEnabled (enable);

        }

        if (name.equals ("previous"))
        {

            this.prevBut.setEnabled (enable);

        }

    }

    private void initUI ()
    {

        this.header.setTitle (this.current.title);

        if (this.current.helpText == null)
        {

            this.setHelpText (this.getHelpText ());

        } else
        {

            this.setHelpText (this.current.helpText);

        }

        this.current.panel.setOpaque (false);
        this.contentPanel.add (this.current.panel);

    }

    public abstract int getMaximumContentHeight ();

    public abstract String getNextStage (String currStage);

    public abstract String getPreviousStage (String currStage);

    public abstract String getStartStage ();

    public abstract boolean handleStageChange (String oldStage,
                                               String newStage);

    public abstract boolean handleFinish ();

    public abstract void handleCancel ();

    public abstract WizardStep getStage (String stage);

    private void enableButtons ()
    {

        String prev = this.getPreviousStage (this.currentStage);
        String next = this.getNextStage (this.currentStage);

        if (this.current != null)
        {

            this.prevBut.setEnabled (prev != null);
            this.nextBut.setEnabled (next != null);

        }

        if (next == null)
        {

            this.nextBut.setEnabled (true);
            this.nextBut.setText ("Finish");

        } else
        {

            this.nextBut.setText ("Next >");

        }

    }

}
