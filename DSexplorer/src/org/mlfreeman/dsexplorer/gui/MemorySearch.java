package org.mlfreeman.dsexplorer.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import org.mlfreeman.dsexplorer.datastructures.DSType;
import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.dsexplorer.datastructures.ResultList;
import org.mlfreeman.dsexplorer.gui.listener.MemorySearchListener;
import org.mlfreeman.dsexplorer.search.AbstractMemoryListener;
import org.mlfreeman.dsexplorer.search.AsciiListener;
import org.mlfreeman.dsexplorer.search.Byte1Listener;
import org.mlfreeman.dsexplorer.search.Byte2Listener;
import org.mlfreeman.dsexplorer.search.Byte4Listener;
import org.mlfreeman.dsexplorer.search.Byte8Listener;
import org.mlfreeman.dsexplorer.search.ByteArrayListener;
import org.mlfreeman.dsexplorer.search.DoubleListener;
import org.mlfreeman.dsexplorer.search.FloatListener;
import org.mlfreeman.dsexplorer.search.UnicodeListener;
import org.mlfreeman.winapi.api.Process;

public class MemorySearch extends javax.swing.JPanel
{
    private enum Action
    {
        Add, FirstSeach, NextSearch
    }
    
    private static final long serialVersionUID = 2361607017186276542L;
    
    /**
     * Auto-generated main method to display this JPanel inside a new JFrame.
     */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new MemorySearch());
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    private JButton     btnAdd;
    private JButton     btnAddAll;
    private JButton     btnFirst;
    private JButton     btnNext;
    private JComboBox   cbType;
    private JComboBox   cbValue;
    private JLabel      jLabel1;
    private JLabel      jLabel2;
    private JScrollPane jScrollPane1;
    private JLabel      lblFrom;
    private JLabel      lblTo;
    private JLabel      lblValue;
    private Process     process;
    private ResultTable tblResults;
    private JTextField  txtFrom;
                        
    private JTextField  txtSearch;
                        
    private JTextField  txtTo;
                        
    public MemorySearch()
    {
        super();
        initGUI();
    }
    
    public void addListener(MemorySearchListener l)
    {
        listenerList.add(MemorySearchListener.class, l);
    }
    
    private void btnFirstActionPerformed()
    {
        try
        {
            int from = Integer.parseInt(txtFrom.getText(), 16);
            int to = Integer.parseInt(txtTo.getText(), 16);
            DSType type = (DSType) cbValue.getSelectedItem();
            AbstractMemoryListener listener;
            Object search;
            String text = txtSearch.getText();
            switch (type)
            {
                case Byte1:
                    listener = new Byte1Listener(process);
                    search = Byte.parseByte(text);
                    break;
                case Byte2:
                    listener = new Byte2Listener(process);
                    search = Short.parseShort(text);
                    break;
                case Byte4:
                    listener = new Byte4Listener(process);
                    search = Integer.parseInt(text);
                    break;
                case Byte8:
                    listener = new Byte8Listener(process);
                    search = Long.parseLong(text);
                    break;
                case Float:
                    listener = new FloatListener(process);
                    search = Float.parseFloat(text);
                    break;
                case Double:
                    listener = new DoubleListener(process);
                    search = Double.parseDouble(text);
                    break;
                case ByteArray:
                    listener = new ByteArrayListener(process);
                    search = text;
                    break;
                case Ascii:
                    listener = new AsciiListener(process);
                    search = text;
                    break;
                case Unicode:
                    listener = new UnicodeListener(process);
                    search = text;
                    break;
                default:
                    listener = null;
                    search = null;
            }
            process.search(from, to, search, listener);
            ResultList results = (ResultList) listener.getResults();
            tblResults.setResults(results);
        }
        catch (NumberFormatException e)
        {
        
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void btnNextActionPerformed()
    {
    
    }
    
    ///////////////////////////////////////////////////////////
    
    protected void fireActionPerformed(Action action, Object o)
    {
        Object[] listeners = listenerList.getListenerList(); // Guaranteed to return a non-null array
        // Process the listeners last to first, notifying those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == MemorySearchListener.class)
            {
                switch (action)
                {
                    case FirstSeach:
                        try
                        {
                            ((MemorySearchListener) listeners[i + 1]).FirstSearchPerformed(Integer.parseInt(txtFrom.getText()), Integer.parseInt(txtTo.getText()));
                        }
                        catch (NumberFormatException e)
                        {
                        }
                        break;
                    case NextSearch:
                        ((MemorySearchListener) listeners[i + 1]).NextSearchPerformed();
                        break;
                    case Add:
                        ((MemorySearchListener) listeners[i + 1]).AddPerformed((List<Result>) o);
                        break;
                }
            }
        }
    }
    
    public MemorySearchListener[] getListeners()
    {
        return listenerList.getListeners(MemorySearchListener.class);
    }
    
    private void initGUI()
    {
        try
        {
            GroupLayout thisLayout = new GroupLayout(this);
            setLayout(thisLayout);
            setPreferredSize(new Dimension(400, 300));
            {
                txtSearch = new JTextField();
            }
            {
                btnFirst = new JButton();
                btnFirst.setText("First Scan");
                btnFirst.setMargin(new java.awt.Insets(2, 2, 2, 2));
                btnFirst.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        btnFirstActionPerformed();
                    }
                });
            }
            {
                ComboBoxModel cbTypeModel = new DefaultComboBoxModel(new String[] {"Exact Search", "Pattern Search"});
                cbType = new JComboBox();
                cbType.setModel(cbTypeModel);
            }
            {
                btnNext = new JButton();
                btnNext.setText("Next Scan");
                btnNext.setMargin(new java.awt.Insets(2, 2, 2, 2));
                btnNext.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        btnNextActionPerformed();
                    }
                });
            }
            {
                jScrollPane1 = new JScrollPane();
                {
                    tblResults = new ResultTable();
                    jScrollPane1.setViewportView(tblResults);
                    tblResults.addMouseListener(new MouseAdapter()
                    {
                        @Override
                        public void mouseClicked(MouseEvent evt)
                        {
                            if (evt.getClickCount() > 1)
                            {
                                fireActionPerformed(Action.Add, tblResults.getSelectedResults());
                            }
                        }
                    });
                }
            }
            {
                btnAdd = new JButton();
                btnAdd.setText("<- Add");
                btnAdd.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        fireActionPerformed(Action.Add, tblResults.getSelectedResults());
                    }
                });
            }
            {
                btnAddAll = new JButton();
                btnAddAll.setText("<- Add All");
                btnAddAll.setMargin(new java.awt.Insets(2, 2, 2, 2));
                btnAddAll.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        fireActionPerformed(Action.Add, tblResults.getResults());
                    }
                });
            }
            {
                lblFrom = new JLabel();
                lblFrom.setText("From");
            }
            {
                txtFrom = new JTextField();
                txtFrom.setText("00400000");
            }
            {
                lblTo = new JLabel();
                lblTo.setText("To");
            }
            {
                txtTo = new JTextField();
                txtTo.setText("7FFF0000"); // 7FFFFFFF
            }
            {
                lblValue = new JLabel();
                lblValue.setText("Value");
            }
            {
                jLabel1 = new JLabel();
                jLabel1.setText("Scan Tye");
            }
            {
                
                ComboBoxModel cbValueModel = new DefaultComboBoxModel(DSType.values());
                cbValue = new JComboBox();
                cbValue.setModel(cbValueModel);
                cbValue.setSelectedIndex(2);
            }
            {
                jLabel2 = new JLabel();
                jLabel2.setText("Value Type");
            }
            thisLayout.setVerticalGroup(thisLayout.createSequentialGroup().addContainerGap().addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(btnNext, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(btnFirst, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(txtSearch, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblValue, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(cbType, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(jLabel1, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(cbValue, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(jLabel2, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(txtFrom, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblFrom, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblTo, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(txtTo, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane1, 0, 112, Short.MAX_VALUE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(btnAdd, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(btnAddAll, GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)).addContainerGap());
            thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup().addContainerGap().addGroup(thisLayout.createParallelGroup().addGroup(thisLayout.createSequentialGroup().addGroup(thisLayout.createParallelGroup().addComponent(lblFrom, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE).addComponent(jLabel2, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE).addComponent(jLabel1, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE).addComponent(lblValue, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE).addComponent(btnFirst, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup().addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup().addGroup(thisLayout.createParallelGroup().addComponent(txtFrom, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE).addComponent(cbValue, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE).addComponent(cbType, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE).addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup().addComponent(btnNext, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE).addGap(40))).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(lblTo, GroupLayout.PREFERRED_SIZE, 13, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(txtTo, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE).addGap(0, 86, Short.MAX_VALUE)).addComponent(txtSearch, GroupLayout.Alignment.LEADING, 0, 311, Short.MAX_VALUE))).addComponent(jScrollPane1, GroupLayout.Alignment.LEADING, 0, 380, Short.MAX_VALUE).addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup().addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(btnAddAll, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE).addGap(0, 224, Short.MAX_VALUE))).addContainerGap());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void setProcess(Process p)
    {
        process = p;
    }
    
}
