package org.mlfreeman.dsexplorer.gui;

import java.awt.Component;
import java.awt.Font;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.dsexplorer.datastructures.ResultList;

public class ResultTable extends JTable
{
    private static class MyCellRenderer extends DefaultTableCellRenderer
    {
        private static final long serialVersionUID = -7760559280746695139L;
        Font                      font             = new Font("Lucida Console", Font.PLAIN, 11);
                                                   
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            l.setFont(font);
            return l;
        };
    }
    
    private static class MyTableModel extends AbstractTableModel
    {
        private static final long serialVersionUID = 7780019195084742274L;
        @SuppressWarnings("unchecked")
        private Class[]           classes          = {String.class, Integer.class};
        private String[]          columnNames      = {"Address", "Value"};
        private ResultList        list;
                                  
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
        
        public Result getResultAt(int row)
        {
            return (Result) list.getChildAt(row);
        }
        
        @Override
        public int getRowCount()
        {
            if (list == null)
            {
                return 0;
            }
            else
            {
                return list.getChildCount();
            }
        }
        
        @Override
        public Object getValueAt(int row, int col)
        {
            try
            {
                switch (col)
                {
                    case 0:
                        return ((Result) list.getChildAt(row)).getAddressString();
                    case 1:
                        return ((Result) list.getChildAt(row)).getValueString();
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
            fireTableDataChanged();
        }
        
        public void setResults(ResultList results)
        {
            list = results;
            fireTableDataChanged();
        }
    }
    
    private static final long  serialVersionUID = -5848750370811800958L;
                                                
    private final MyTableModel model;
                               
    public ResultTable()
    {
        super();
        model = new MyTableModel();
        setModel(model);
        setAutoCreateRowSorter(true);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        getColumnModel().getColumn(0).setCellRenderer(new MyCellRenderer()); // TODO suboptimal font change
    }
    
    public List<Result> getResults()
    {
        List<Result> list = new LinkedList<Result>();
        for (int row = 0; row < model.getRowCount(); row++)
        {
            list.add(model.getResultAt(row));
        }
        
        return list;
    }
    
    public List<Result> getSelectedResults()
    {
        int[] rows = getSelectedRows();
        if (rows.length == 0)
        {
            return null;
        }
        
        List<Result> list = new LinkedList<Result>();
        for (int row : rows)
        {
            list.add(model.getResultAt(row));
        }
        
        return list;
    }
    
    public void refresh()
    {
        model.refresh();
    }
    
    public void setResults(ResultList results)
    {
        model.setResults(results);
    }
    
}
