package org.mlfreeman.dsexplorer.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.dsexplorer.datastructures.ResultList;
import org.mlfreeman.winapi.api.Process;

public class ProcessTree extends JTree
{
    private static class CellRenderer extends DefaultTreeCellRenderer
    {
        private static final long serialVersionUID = 1997910950484074510L;
        
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
        {
            Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            
            // Mark static variables
            if (value instanceof Result)
            {
                Result r = (Result) value;
                if (r.getStatic() != null)
                {
                    c.setForeground(Color.BLUE);
                }
                
            }
            return c;
        }
        
    }
    
    private static final Log   log              = LogFactory.getLog(ProcessTree.class);
    private static final long  serialVersionUID = 8889377903469038055L;
    private ResultList         rl;
                               
    private TreeSelectionModel sm               = getSelectionModel();
                                                
    public ProcessTree()
    {
        setCellRenderer(new CellRenderer());
        setModel(null);
        setFont(new Font("Lucida Console", Font.PLAIN, 11));
        addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
            }
            
            @Override
            public void keyReleased(KeyEvent e)
            {
            }
            
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (e.getKeyChar() == 127)
                {
                    deleteSelected();
                }
            }
        });
    }
    
    public void addResult(Result result)
    {
        Result r = result.clone(); // create clones to avoid any border effects
        rl.add(r);
    }
    
    public void addResults(List<Result> results)
    {
        Result r;
        int begin = rl.getChildCount();
        int[] indexes = new int[results.size()];
        
        for (int i = 0; i < indexes.length; i++)
        {
            r = results.get(i).clone();// create clones to avoid any border effects
            rl.add(r);
            indexes[i] = begin + i;
        }
    }
    
    public void deleteSelected()
    {
        TreePath[] paths = getSelectionPaths();
        if (paths != null)
        {
            for (TreePath treePath : paths)
            {
                Object o = treePath.getLastPathComponent();
                if (o instanceof Result)
                {
                    ProcessTree.log.info("delete");
                    Result r = (Result) o;
                    r.delete();
                }
            }
        }
    }
    
    public void reset()
    {
        if (rl != null)
        {
            rl.removeAllChildren();
        }
    }
    
    public void saveToFile(File file)
    {
        try
        {
            rl.saveToFile(file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void setProcess(Process p)
    {
        rl.setProcess(p);
        sm.setSelectionPath(new TreePath(rl));
    }
    
    public void setResultList(ResultList list)
    {
        if (rl != null)
        {
            list.setProcess(rl.getProcess()); // take over previous Process
        }
        rl = list;
        setModel(rl);
        // sm.setSelectionPath(new TreePath(rl));
        
    }
    
}
