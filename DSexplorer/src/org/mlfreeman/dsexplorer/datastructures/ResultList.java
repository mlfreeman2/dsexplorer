package org.mlfreeman.dsexplorer.datastructures;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.exceptions.NoProcessException;
import org.mlfreeman.winapi.api.Process;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

/**
 * This class groups Result objects which represent memory pointers with their interpretation. This class gives a meaning to these results because it links the Results with a process. That way, the results can receive their value. And it is easy to switch the process for every result, by simply changing the process of this class. Additonally, this class has the feature to load an save Results from files.
 *
 * @author cappella
 */

@Root(name = "Results")
public class ResultList implements TreeNode, TreeModel
{
    @Element
    private static DSList    dsList = new DSList();
    private static final Log log    = LogFactory.getLog(ResultList.class);
                                    
    public static ResultList openFromFile(File file) throws Exception
    {
        ResultList.log.info("open " + file.getAbsolutePath());
        
        Strategy strategy = new CycleStrategy("id", "ref");
        Serializer serializer = new Persister(strategy);
        ResultList newList = serializer.read(ResultList.class, file);
        
        // Repair Result links
        Result r;
        for (int i = 0; i < newList.getChildCount(); i++)
        {
            r = (Result) newList.getChildAt(i);
            r.setResultList(newList); // link list with result
            r.setParent(newList);
            r.getDatastructure().addListener(r); // link result with ds
        }
        
        // Repair Datastructure links
        DSList dsList = newList.getDatastructures();
        Container ds;
        List<Datastructure> doneList = new LinkedList<Datastructure>();
        for (int i = 0; i < dsList.getSize(); i++)
        {
            ds = dsList.getElementAt(i);
            ResultList.repairChildsOf(doneList, ds);
        }
        
        return newList;
    }
    
    private static void repairChildsOf(List<Datastructure> doneList, Container ds)
    {
        for (Datastructure field : ds.getFields())
        {
            if (!doneList.contains(field))
            { // avoid infinite loops
                doneList.add(field);
                field.setContainer(ds);
                if (field.isContainer())
                {
                    ResultList.repairChildsOf(doneList, (Container) field); // recursion root->childs
                }
            }
        }
    }
    
    protected EventListenerList listenerList = new EventListenerList();
                                             
    private Process             process;
                                
    @ElementList(inline = true, name = "results")
    private List<Result>        results      = new LinkedList<Result>();
                                             
    public ResultList()
    {
    }
    
    public ResultList(Process process)
    {
        this.process = process;
    }
    
    public void add(Result newChild)
    {
        ResultList.log.debug("add Result " + newChild.hashCode());
        newChild.setResultList(this); // link the result to this object
        newChild.setParent(this);
        results.add(newChild);
        nodeInserted(newChild);
    }
    
    @Override
    public void addTreeModelListener(TreeModelListener l)
    {
        listenerList.add(TreeModelListener.class, l);
    }
    
    @Override
    public Enumeration<Result> children()
    {
        Enumeration<Result> e = new Enumeration<Result>()
        {
            Iterator<Result> i = results.iterator();
            
            @Override
            public boolean hasMoreElements()
            {
                return i.hasNext();
            }
            
            @Override
            public Result nextElement()
            {
                return i.next();
            }
        };
        return e;
    }
    
    protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                if (e == null)
                {
                    e = new TreeModelEvent(source, path, childIndices, children);
                }
                ((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
            }
        }
    }
    
    protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                if (e == null)
                {
                    e = new TreeModelEvent(source, path, childIndices, children);
                }
                ((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
            }
        }
    }
    
    protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                if (e == null)
                {
                    e = new TreeModelEvent(source, path, childIndices, children);
                }
                ((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
            }
        }
    }
    
    protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children)
    {
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2)
        {
            if (listeners[i] == TreeModelListener.class)
            {
                if (e == null)
                {
                    e = new TreeModelEvent(source, path, childIndices, children);
                }
                ((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
            }
        }
    }
    
    @Override
    public boolean getAllowsChildren()
    {
        return true;
    }
    
    // TreeNode//////////////////////////////////////
    
    @Override
    public Object getChild(Object parent, int index)
    {
        return ((TreeNode) parent).getChildAt(index);
    }
    
    @Override
    public TreeNode getChildAt(int childIndex)
    {
        return results.get(childIndex);
    }
    
    @Override
    public int getChildCount()
    {
        return results.size();
    }
    
    @Override
    public int getChildCount(Object parent)
    {
        return ((TreeNode) parent).getChildCount();
    }
    
    public DSList getDatastructures()
    {
        return ResultList.dsList;
    }
    
    @Override
    public int getIndex(TreeNode node)
    {
        return results.indexOf(node);
    }
    
    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
        return ((TreeNode) parent).getIndex((Result) child);
    }
    
    // Object//////////////////////////////////////
    
    @Override
    public TreeNode getParent()
    {
        // Has no parent
        return null;
    }
    
    // TreeModel//////////////////////////////////////
    
    public TreeNode[] getPathToRoot(TreeNode aNode)
    {
        return getPathToRoot(aNode, 0);
    }
    
    protected TreeNode[] getPathToRoot(TreeNode aNode, int depth)
    {
        TreeNode[] retNodes;
        if (aNode == null)
        {
            if (depth == 0)
            {
                return null;
            }
            else
            {
                retNodes = new TreeNode[depth];
            }
        }
        else
        {
            depth++;
            if (aNode == this)
            {
                retNodes = new TreeNode[depth];
            }
            else
            {
                retNodes = getPathToRoot(aNode.getParent(), depth);
            }
            retNodes[retNodes.length - depth] = aNode;
        }
        return retNodes;
    }
    
    public Process getProcess()
    {
        return process;
    }
    
    @Override
    public Object getRoot()
    {
        return this;
    }
    
    public String getStatic(Long address)
    {
        if (process != null)
        {
            return process.getStatic(address);
        }
        return null;
    }
    
    @Override
    public boolean isLeaf()
    {
        return false;
    }
    
    @Override
    public boolean isLeaf(Object node)
    {
        return ((TreeNode) node).isLeaf();
    }
    
    public void nodeChanged(TreeNode node)
    {
        if (listenerList != null && node != null)
        {
            TreeNode parent = node.getParent();
            if (parent != null)
            {
                int anIndex = parent.getIndex(node);
                if (anIndex != -1)
                {
                    int[] cIndexs = new int[1];
                    cIndexs[0] = anIndex;
                    nodesChanged(parent, cIndexs);
                }
            }
            else if (node == getRoot())
            {
                nodesChanged(node, null);
            }
        }
    }
    
    public void nodeInserted(TreeNode node)
    {
        if (node != null)
        {
            TreeNode parent = node.getParent();
            int[] childIndices = new int[] {parent.getIndex(node)};
            Object[] children = new Object[] {node};
            fireTreeNodesInserted(this, getPathToRoot(parent), childIndices, children);
        }
    }
    
    // TreeModel-Utils//////////////////////////////////////
    
    public void nodeRemoved(TreeNode node, int index)
    {
        if (node != null)
        {
            TreeNode parent = node.getParent();
            int[] childIndices = new int[] {index};
            Object[] children = new Object[] {node};
            fireTreeNodesRemoved(this, getPathToRoot(parent), childIndices, children);
        }
    }
    
    public void nodesChanged(TreeNode node, int[] childIndices)
    {
        if (node != null)
        {
            if (childIndices != null)
            {
                int cCount = childIndices.length;
                if (cCount > 0)
                {
                    Object[] cChildren = new Object[cCount];
                    for (int counter = 0; counter < cCount; counter++)
                    {
                        cChildren[counter] = node.getChildAt(childIndices[counter]);
                    }
                    fireTreeNodesChanged(this, getPathToRoot(node), childIndices, cChildren);
                }
            }
            else if (node == getRoot())
            {
                fireTreeNodesChanged(this, getPathToRoot(node), null, null);
            }
        }
    }
    
    public void ReadProcessMemory(Pointer pointer, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead) throws NoProcessException, Exception
    {
        if (process != null)
        {
            process.ReadProcessMemory(pointer, outputBuffer, nSize, outNumberOfBytesRead);
        }
        else
        {
            throw new NoProcessException();
        }
    }
    
    public void reload(TreeNode node)
    {
        if (node != null)
        {
            fireTreeStructureChanged(this, getPathToRoot(node), null, null);
        }
    }
    
    public void remove(Result child)
    {
        int index = results.indexOf(child);
        results.remove(child);
        nodeRemoved(child, index);
    }
    
    public void removeAllChildren()
    {
        results.clear();
        reload(this);
    }
    
    @Override
    public void removeTreeModelListener(TreeModelListener l)
    {
        listenerList.remove(TreeModelListener.class, l);
    }
    
    public void saveToFile(File file) throws FileNotFoundException
    {
        ResultList.log.info("save " + file.getAbsolutePath());
        try
        {
            Strategy strategy = new CycleStrategy("id", "ref");
            Serializer serializer = new Persister(strategy);
            
            // //Clear empty Datastructures
            // Datastructure ds;
            // for (int i = 0; i < dsList.getSize(); i++) {
            // ds=dsList.getElementAt(i);
            // if (ds.isContainer()){
            // Container c = (Container)ds;
            // if (c.getFields()==null || c.getFields().size()==0){
            // dsList.removeElementAt(i);
            // i--;
            // }
            // }
            // }
            
            serializer.write(this, file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public void setProcess(Process process)
    {
        this.process = process;
        for (Result result : results)
        {
            result.invalidateParentAndChilds();
        }
        nodeChanged(this);
    }
    
    @Override
    public String toString()
    {
        if (process == null)
        {
            return "<no process>";
        }
        return process.getSzExeFile();
    }
    
    @Override
    public void valueForPathChanged(TreePath path, Object newValue)
    {
        // TODO what do to here?
        
    }
    
}
