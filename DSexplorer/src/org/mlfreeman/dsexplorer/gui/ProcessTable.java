package org.mlfreeman.dsexplorer.gui;

import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import org.mlfreeman.winapi.api.Process;
import org.mlfreeman.winapi.api.ProcessList;
import org.mlfreeman.winapi.tools.Kernel32Tools;

public class ProcessTable extends JTable
{
    private static class MyTableModel extends AbstractTableModel
    {
        private static final long serialVersionUID = 7780019195084742274L;
                                                   
        private Class[]           classes          = {Integer.class, ImageIcon.class, String.class, String.class};
                                                   
        private String[]          columnNames      = {"Pid", "Icon", "Name", "Path"};
                                                   
        private ProcessList       list             = new ProcessList();
                                                   
        public MyTableModel()
        {
        
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public Class getColumnClass(int c)
        {
            return classes[c];
        }
        
        @Override
        public int getColumnCount()
        {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int col)
        {
            return columnNames[col];
        }
        
        public Process getProcessAt(int row)
        {
            return list.get(row);
        }
        
        @Override
        public int getRowCount()
        {
            return list.size();
        }
        
        @Override
        public Object getValueAt(int row, int col)
        {
            try
            {
                switch (col)
                {
                    case 0:
                        return list.get(row).getPid();
                    case 1:
                        return list.get(row).getIcon();
                    case 2:
                        return list.get(row).getSzExeFile();
                    case 3:
                        return list.get(row).getModuleFileNameExA();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
        
        public void refresh()
        {
            list = Kernel32Tools.getProcessList();
            fireTableDataChanged();
        }
    }
    
    private static final long  serialVersionUID = -5848750370811800958L;
                                                
    private final MyTableModel model;
                               
    public ProcessTable()
    {
        super();
        model = new MyTableModel();
        
        setAutoCreateRowSorter(true);
        
        setModel(model);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        getColumnModel().getColumn(0).setMinWidth(50);
        getColumnModel().getColumn(0).setMaxWidth(50);
        getColumnModel().getColumn(1).setMinWidth(30);
        getColumnModel().getColumn(1).setMaxWidth(30);
    }
    
    public Process getSelectedProcess()
    {
        int rowSorted = getSelectedRow();
        if (rowSorted != -1)
        {
            int rowReal = getRowSorter().convertRowIndexToModel(rowSorted);
            return model.getProcessAt(rowReal);
        }
        else
        {
            return null;
        }
    }
    
    public void refresh()
    {
        model.refresh();
    }
}
