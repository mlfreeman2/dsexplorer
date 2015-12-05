package org.mlfreeman.dsexplorer.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mlfreeman.dsexplorer.gui.listener.ProcessDialogListener;
import org.mlfreeman.winapi.api.Process;

public class ProcessDialog extends javax.swing.JDialog
{
    private enum Action
    {
        CANCEL, OK
    }
    
    private static final long   serialVersionUID = -9170956341779668601L;
                                                 
    private JButton             btnCancel;
    private JButton             btnOpen;
    private JButton             btnRefresh;
    private JScrollPane         jScrollPane1;
                                
    protected EventListenerList listenerList     = new EventListenerList();
                                                 
    private ProcessTable        tblProcess;
                                
    public ProcessDialog(JFrame frame)
    {
        super(frame);
        initGUI();
    }
    
    public void addListener(ProcessDialogListener l)
    {
        listenerList.add(ProcessDialogListener.class, l);
    }
    
    protected void fireActionPerformed(Action action, Object o)
    {
        Object[] listeners = listenerList.getListenerList(); // Guaranteed to return a non-null array
        // Process the listeners last to first, notifying those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == ProcessDialogListener.class)
            {
                switch (action)
                {
                    case OK:
                        ((ProcessDialogListener) listeners[i + 1]).okPerformed((Process) o);
                        break;
                    case CANCEL:
                        ((ProcessDialogListener) listeners[i + 1]).cancelPerformed();
                        break;
                }
            }
        }
    }
    
    public ProcessDialogListener[] getListeners()
    {
        return listenerList.getListeners(ProcessDialogListener.class);
    }
    
    private void initGUI()
    {
        try
        {
            GroupLayout thisLayout = new GroupLayout(getContentPane());
            getContentPane().setLayout(thisLayout);
            setTitle("Open Process");
            setModal(true);
            setMinimumSize(new Dimension(250, 300));
            {
                btnOpen = new JButton();
                btnOpen.setText("Open");
                btnOpen.setEnabled(false);
                btnOpen.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        openSelectedProcess(evt);
                    }
                });
            }
            {
                btnCancel = new JButton();
                btnCancel.setText("Cancel");
                btnCancel.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        ProcessDialog.this.dispose();
                    }
                });
            }
            {
                jScrollPane1 = new JScrollPane();
                {
                    tblProcess = new ProcessTable();
                    jScrollPane1.setViewportView(tblProcess);
                    tblProcess.addMouseListener(new MouseAdapter()
                    {
                        @Override
                        public void mouseClicked(MouseEvent evt)
                        {
                            tblProcessMouseClicked(evt);
                        }
                    });
                    tblProcess.getSelectionModel().addListSelectionListener(new ListSelectionListener()
                    {
                        @Override
                        public void valueChanged(ListSelectionEvent e)
                        {
                            btnOpen.setEnabled(true);
                        }
                    });
                }
            }
            {
                btnRefresh = new JButton();
                btnRefresh.setText("Refresh");
                btnRefresh.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        refresh();
                    }
                });
            }
            thisLayout.setVerticalGroup(thisLayout.createSequentialGroup().addContainerGap().addComponent(jScrollPane1, 0, 193, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(btnRefresh, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(btnOpen, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(btnCancel, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)).addContainerGap());
            thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup().addContainerGap().addGroup(thisLayout.createParallelGroup().addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup().addComponent(btnOpen, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE).addGap(0, 122, Short.MAX_VALUE).addComponent(btnCancel, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)).addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, 0, 272, Short.MAX_VALUE).addComponent(btnRefresh, GroupLayout.Alignment.LEADING, 0, 272, Short.MAX_VALUE)).addContainerGap());
            pack();
            this.setSize(350, 400);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    ///////////////////////////////////////////////////////////
    
    private void openSelectedProcess(ActionEvent evt)
    {
        fireActionPerformed(Action.OK, tblProcess.getSelectedProcess());
        dispose();
    }
    
    public void refresh()
    {
        tblProcess.refresh();
        btnOpen.setEnabled(false);
    }
    
    private void tblProcessMouseClicked(MouseEvent evt)
    {
        if (evt.getClickCount() >= 2)
        {
            btnOpen.doClick();
        }
    }
    
}
