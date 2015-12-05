package org.mlfreeman.dsexplorer.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import org.fife.ui.hex.swing.HexEditor;
import org.mlfreeman.dsexplorer.datastructures.Container;
import org.mlfreeman.dsexplorer.datastructures.ContainerImpl;
import org.mlfreeman.dsexplorer.datastructures.DSList;
import org.mlfreeman.dsexplorer.datastructures.DSType;
import org.mlfreeman.dsexplorer.datastructures.Datastructure;
import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.dsexplorer.datastructures.simple.Byte4;

public class DSEditor extends javax.swing.JPanel
{
    private static final long serialVersionUID = -6928391243482994782L;
    private JButton           btnAddDS;
    private JButton           btnAddField;
    private JButton           btnRefresh;
    private JComboBox         cbDSselector;
    private JComboBox         cbValue;
    private JCheckBox         chbPointer;
    private DSList            dsList;
    private Font              font             = new Font("Lucida Console", Font.PLAIN, 11);
    private HexEditor         hexEditor;
    private JSeparator        jSeparator1;
    private JLabel            lblAddress;
    private JLabel            lblDSselector;
    private JLabel            lblName;
    private JLabel            lblSize;
    private JLabel            lblStatic;
    private JLabel            lblType;
    private JLabel            lblValue;
    private Result            result;
    private JTextField        txtAddress;
    private JTextField        txtName;
    private JTextField        txtPointer;
    private JTextField        txtSize;
    private JTextField        txtStatic;
    private JTextField        txtValue;
                              
    public DSEditor()
    {
        super();
        initGUI();
    }
    
    private void btnAddDSActionPerformed()
    {
        dsList.addElement(new ContainerImpl());
    }
    
    private void btnAddFieldActionPerformed()
    {
        Datastructure ds = result.getDatastructure();
        ((Container) ds).addField(new Byte4());
        txtSize.setText("" + ds.getByteCount());
        hexEditor.setHighlightHexSize(result.getDatastructure().getByteCount());
    }
    
    private void btnRefreshActionPerformed(ActionEvent evt)
    {
        result.invalidateParentAndChilds();
        setResult(result);
    }
    
    private void cbDSselectorActionPerformed()
    {
        Datastructure ds = (Datastructure) cbDSselector.getSelectedItem();
        if (!result.getDatastructure().equals(ds))
        { // Avoid unecessairy changes
            result.setDatastructure(ds);
            
            hexEditor.setHighlightHexSize(ds.getByteCount());
            txtSize.setText("" + ds.getByteCount());
            txtName.setText(ds.getName());
            boolean container = ds.isContainer();
            if (container)
            {
                Container c = (Container) ds;
                chbPointer.setSelected(c.isPointer());
            }
        }
    }
    
    private void cbValueActionPerformed()
    {
        DSType newType = (DSType) cbValue.getSelectedItem();
        Datastructure ds = result.getDatastructure();
        
        if (!newType.equals(ds.getType()))
        { // Avoid unecessairy changes
            
            if (newType.equals(DSType.Container))
            {
                Datastructure item = (Datastructure) cbDSselector.getItemAt(0);
                if (item == null)
                {
                    dsList.addElement(new ContainerImpl());
                }
                
                result.setDatastructure(item);
                cbDSselector.setSelectedItem(item);
            }
            else
            {
                result.setDatastructure(newType.getInstance());
            }
            
            ds = result.getDatastructure();
            boolean container = ds.isContainer();
            if (container)
            {
                Container c = (Container) ds;
                chbPointer.setSelected(c.isPointer());
            }
            
            hexEditor.setHighlightHexSize(ds.getByteCount());
            txtSize.setText("" + ds.getByteCount());
            txtSize.setEditable(!ds.isByteCountFix());
            txtName.setText(ds.getName());
            txtValue.setText(result.getValueString());
            txtValue.setEditable(!container);
            chbPointer.setVisible(container);
            lblDSselector.setVisible(container);
            cbDSselector.setVisible(container);
            btnAddField.setVisible(container);
            btnAddDS.setVisible(container);
            txtPointer.setVisible(container);
        }
    }
    
    private void chbPointerActionPerformed()
    {
        boolean isPointer = chbPointer.isSelected();
        Container c = (Container) result.getDatastructure();
        if (c.isPointer() != isPointer)
        { // Avoid unecessairy changes
            c.setPointer(chbPointer.isSelected());
            txtPointer.setText(isPointer ? result.getPointerString() : null);
            txtSize.setText("" + c.getByteCount());
            hexEditor.setHighlightHexSize(result.getDatastructure().getByteCount());
        }
    }
    
    private void initGUI()
    {
        try
        {
            GroupLayout thisLayout = new GroupLayout(this);
            setLayout(thisLayout);
            setPreferredSize(new Dimension(532, 440));
            {
                txtValue = new JTextField();
                txtValue.setFont(font);
            }
            {
                ComboBoxModel cbValueModel = new DefaultComboBoxModel(DSType.values());
                cbValue = new JComboBox();
                cbValue.setModel(cbValueModel);
                cbValue.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        cbValueActionPerformed();
                    }
                });
            }
            {
                lblValue = new JLabel();
                lblValue.setText("Value");
            }
            {
                lblAddress = new JLabel();
                lblAddress.setText("Address");
            }
            {
                btnAddField = new JButton();
                btnAddField.setText("Add Field");
                btnAddField.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        btnAddFieldActionPerformed();
                    }
                });
            }
            {
                hexEditor = new HexEditor();
            }
            {
                btnRefresh = new JButton();
                btnRefresh.setText("Refresh");
                btnRefresh.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        btnRefreshActionPerformed(evt);
                    }
                });
            }
            {
                txtStatic = new JTextField();
                txtStatic.setFont(font);
                txtStatic.setEditable(false);
            }
            {
                lblStatic = new JLabel();
                lblStatic.setText("is Static");
            }
            {
                txtPointer = new JTextField();
                txtPointer.setFont(font);
                txtPointer.setEditable(false);
            }
            {
                chbPointer = new JCheckBox();
                chbPointer.setText("is Pointer");
                chbPointer.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        chbPointerActionPerformed();
                    }
                });
            }
            {
                cbDSselector = new JComboBox();
                cbDSselector.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        cbDSselectorActionPerformed();
                    }
                });
            }
            {
                lblDSselector = new JLabel();
                lblDSselector.setText("Datastructure");
            }
            {
                btnAddDS = new JButton();
                btnAddDS.setText("Add new");
                btnAddDS.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        btnAddDSActionPerformed();
                    }
                });
            }
            {
                jSeparator1 = new JSeparator();
            }
            {
                txtName = new JTextField();
                txtName.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        txtNameActionPerformed();
                    }
                });
            }
            {
                lblSize = new JLabel();
                lblSize.setText("Size");
            }
            {
                txtSize = new JTextField();
                txtSize.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        txtSizeActionPerformed();
                    }
                });
            }
            {
                txtAddress = new JTextField();
                txtAddress.setFont(font);
                txtAddress.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent evt)
                    {
                        txtAddressActionPerformed();
                    }
                });
            }
            {
                lblName = new JLabel();
                lblName.setText("Name");
            }
            {
                lblType = new JLabel();
                lblType.setText("Value Type");
            }
            thisLayout.setVerticalGroup(thisLayout.createSequentialGroup().addContainerGap().addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(txtName, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(lblName, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(btnRefresh, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(chbPointer, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(txtPointer, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(txtAddress, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(lblAddress, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(lblStatic, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(txtStatic, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(cbDSselector, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblDSselector, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(cbValue, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(lblType, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(btnAddDS, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(txtSize, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(lblSize, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(btnAddField, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(lblValue, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(txtValue, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 3, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(hexEditor, 0, 236, Short.MAX_VALUE).addContainerGap());
            thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup().addContainerGap().addGroup(thisLayout.createParallelGroup().addGroup(thisLayout.createSequentialGroup().addGroup(thisLayout.createParallelGroup().addComponent(lblValue, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE).addComponent(lblSize, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE).addComponent(lblType, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE).addComponent(lblStatic, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE).addComponent(lblAddress, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE).addComponent(lblName, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup().addComponent(txtValue, GroupLayout.Alignment.LEADING, 0, 438, Short.MAX_VALUE).addGroup(thisLayout.createSequentialGroup().addGroup(thisLayout.createParallelGroup().addComponent(txtSize, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE).addComponent(cbValue, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE).addComponent(txtStatic, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE).addComponent(txtAddress, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE).addComponent(txtName, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup().addGap(74).addComponent(lblDSselector, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE).addComponent(chbPointer, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addGroup(thisLayout.createParallelGroup().addComponent(btnAddField, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE).addComponent(btnAddDS, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE).addComponent(cbDSselector, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE).addComponent(txtPointer, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE).addComponent(btnRefresh, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE))))).addComponent(jSeparator1, GroupLayout.Alignment.LEADING, 0, 512, Short.MAX_VALUE).addComponent(hexEditor, GroupLayout.Alignment.LEADING, 0, 512, Short.MAX_VALUE)).addContainerGap());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void setDataStructures(DSList dsList)
    {
        this.dsList = dsList;
        cbDSselector.setModel(this.dsList);
    }
    
    private void setHexeditor(Result result)
    {
        Long address = result.getAddress();
        if (address != null && address >= 0)
        {
            long low = Math.max(address - 512, 0);
            long high = address + 512;
            hexEditor.open(result.getMemoryBytes(low, high), low);
            hexEditor.setHighlightHexPos((int) ((high - low) / 2));
            hexEditor.setHighlightHexSize(result.getDatastructure().getByteCount());
        }
    }
    
    // $hide>>$
    public void setResult(Result result)
    {
        this.result = result;
        Datastructure ds = result.getDatastructure();
        
        setHexeditor(result);
        
        txtAddress.setText(result.getAddressString());
        txtAddress.setEditable(result.isSimpleResult());
        String isStatic = result.getStatic();
        txtStatic.setText(isStatic == null ? "No" : isStatic);
        cbValue.setSelectedItem(ds.getType());
        
        boolean container = ds.isContainer();
        if (container)
        {
            Container c = (Container) ds;
            cbDSselector.setSelectedItem(ds);
            txtPointer.setText(result.getPointerString());
            chbPointer.setSelected(c.isPointer());
        }
        
        txtSize.setText("" + ds.getByteCount());
        txtSize.setEditable(!ds.isByteCountFix());
        txtName.setText(ds.getName());
        txtValue.setText(result.getValueString());
        txtValue.setEditable(!container);
        
        chbPointer.setVisible(container);
        lblDSselector.setVisible(container);
        cbDSselector.setVisible(container);
        btnAddField.setVisible(container);
        btnAddDS.setVisible(container);
        chbPointer.setVisible(container);
        txtPointer.setVisible(container);
    }
    
    private void txtAddressActionPerformed()
    {
        try
        {
            long address = Long.parseLong(txtAddress.getText(), 16);
            result.setAddress(address);
            txtValue.setText(result.getValueString());
            String isStatic = result.getStatic();
            txtStatic.setText(isStatic == null ? "No" : isStatic);
            setHexeditor(result);
            txtPointer.setText(result.getPointerString());
            
        }
        catch (NumberFormatException e)
        {
        }
        ;
    }
    
    private void txtNameActionPerformed()
    {
        result.getDatastructure().setName(txtName.getText());
    }
    
    private void txtSizeActionPerformed()
    {
        try
        {
            result.getDatastructure().setByteCount(Integer.parseInt(txtSize.getText()));
            txtValue.setText(result.getValueString());
            hexEditor.setHighlightHexSize(result.getDatastructure().getByteCount());
        }
        catch (NumberFormatException e)
        {
        }
        ;
    }
    
    // $hide<<$
}
