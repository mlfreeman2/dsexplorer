package org.mlfreeman.dsexplorer.datastructures;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.exceptions.NoProcessException;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;

@Root
public class Result implements TreeNode, DSListener, Cloneable
{
    private static final Log log               = LogFactory.getLog(Result.class);
    
    @Attribute(required = false)
    private Long             address;
    
    private List<Result>     childs;
    
    @Element
    private Datastructure    datastructure;
                             
    private int              fieldIndex;                                         // only for fields. used to calcualte the Offset

    private TreeNode         parent;                                             // needs to be reconstructed after loading
                             
    private Long             pointerCache      = null;
    
    private boolean          pointerCacheOK    = false;
                                               
    private ResultList       resultList;                                         // needs to be reconstructed after loading
    
    private String           staticAddr        = null;
                                               
    private boolean          staticAddrCacheOK = false;
                                               
    private Object           valueCache        = null;
                                               
    private boolean          valueCacheOK      = false;
                                               
    protected Result()
    {
    }
    
    // Used to create manual Results
    public Result(Datastructure datastructure)
    {
        this.datastructure = datastructure;
        this.datastructure.addListener(this);
    }
    
    // used from Process to create results
    public Result(ResultList resultList, Datastructure datastructure, long address, Object value)
    {
        this.resultList = resultList;
        this.address = address;
        valueCache = value;
        if (value != null)
        {
            valueCacheOK = true;
        }
        this.datastructure = datastructure;
        this.datastructure.addListener(this);
    }
    
    // used from Results to create children
    public Result(ResultList resultList, Result parent, Datastructure datastructure, int fieldIndex)
    {
        this.resultList = resultList;
        this.parent = parent;
        this.fieldIndex = fieldIndex;
        this.datastructure = datastructure;
        this.datastructure.addListener(this);
    }
    
    // triggered on the parent of the added field
    @Override
    public void addedField(Datastructure field, int fieldIndex)
    {
        if (childs != null)
        {
            Result r = new Result(resultList, this, field, fieldIndex);
            childs.add(fieldIndex, r);
            for (int i = fieldIndex + 1; i < childs.size(); i++)
            {
                childs.get(i).fieldIndex++;
            }
            getResultList().nodeInserted(r);
        }
        invalidateFollowingSiblings(); // ByteCount has changed of this field (addresses of the following childs too)
    }
    
    @Override
    public Enumeration<Result> children()
    {
        return new Enumeration<Result>()
        {
            Iterator<Result> iter = childs.iterator();
            
            @Override
            public boolean hasMoreElements()
            {
                return iter.hasNext();
            }
            
            @Override
            public Result nextElement()
            {
                return iter.next();
            }
        };
    }
    
    @Override
    public Result clone()
    {
        try
        {
            Result clone = (Result) super.clone();
            // FIXME its Datastructure only notifies the original Result
            getDatastructure().removeListener(this);
            getDatastructure().addListener(clone);
            // Now its Datastructure only notifies the clone Result
            return clone;
            
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
            return null;
        }
    }
    
    private void defineChildNodes()
    {
        if (!isLeaf() && childs == null)
        {
            childs = new LinkedList<Result>();
            if (datastructure.isContainer())
            {
                List<Datastructure> fields = ((Container) datastructure).getFields();
                for (int i = 0; i < fields.size(); i++)
                {
                    Result r = new Result(resultList, this, fields.get(i), i);
                    childs.add(r);
                }
            }
        }
    }
    
    public void delete()
    {
        if (isSimpleResult())
        { // simple Result
            getResultList().remove(this);
        }
        else
        {
            // Container dsParent=(Container)datastructure.getContainer();//TODO why is there a difference (if it not a pointer)
            Container dsParent = (Container) ((Result) parent).getDatastructure();
            dsParent.removeField(fieldIndex);
        }
    }
    
    public Long getAddress()
    {
        if (isSimpleResult())
        { // simple result
            return address;
        }
        else
        {
            
            // Container dsParent=(Container)datastructure.getContainer(); //TODO why is there a difference (if it not a pointer)
            Container dsParent = (Container) ((Result) parent).getDatastructure();
            Long address;
            if (dsParent.isPointer())
            {
                address = ((Result) parent).getPointer(); // warning, recursion, down to the root
            }
            else
            {
                address = ((Result) parent).getAddress(); // warning, recursion, down to the root
            }
            if (address != null)
            {
                address += dsParent.getOffset(fieldIndex);
            }
            return address;
        }
    }
    
    public String getAddressString()
    {
        Long p = getAddress();
        return p == null ? null : String.format("%1$08X", p);
    }
    
    @Override
    public boolean getAllowsChildren()
    {
        return !datastructure.isContainer();
    }
    
    @Override
    public TreeNode getChildAt(int childIndex)
    {
        defineChildNodes();
        return childs.get(childIndex);
    }
    
    @Override
    public int getChildCount()
    {
        defineChildNodes();
        return childs.size();
    }
    
    public Datastructure getDatastructure()
    {
        return datastructure;
    }
    
    @Override
    public int getIndex(TreeNode node)
    {
        defineChildNodes();
        return childs.indexOf(node);
    }
    
    public byte[] getMemoryBytes(long low, long high)
    {
        Memory buffer = new Memory(high - low);
        byte[] value = null;
        try
        {
            getResultList().ReadProcessMemory(Pointer.createConstant(low), buffer, (int) buffer.size(), null);
            value = buffer.getByteArray(0, (int) buffer.size());
        }
        catch (NoProcessException e)
        {
        }
        catch (Exception e)
        {
        }
        return value;
    }
    
    @Override
    public TreeNode getParent()
    {
        return parent;
    }
    
    private Long getPointer()
    {
        if (datastructure.isContainer())
        {
            Container c = (Container) datastructure;
            if (c.isPointer())
            {
                
                if (pointerCacheOK)
                {
                    return pointerCache;
                }
                
                Memory buffer = new Memory(4);
                try
                {
                    Result.log.trace("Pointer: " + getAddressString());
                    getResultList().ReadProcessMemory(Pointer.createConstant(getAddress()), buffer, (int) buffer.size(), null);
                    pointerCache = (long) buffer.getInt(0);
                }
                catch (NoProcessException e)
                {
                    pointerCache = null;
                }
                catch (Exception e)
                {
                    Result.log.warn(e);
                    pointerCache = null;
                }
                pointerCacheOK = true;
                return pointerCache;
            }
        }
        return null;
    }
    
    // DSListener///////////////////////////////////////////////
    
    public String getPointerString()
    {
        Long p = getPointer();
        return p == null ? null : String.format("%1$08X", p);
    }
    
    public ResultList getResultList()
    {
        return resultList;
    }
    
    public String getStatic()
    {
        if (!staticAddrCacheOK)
        {
            Long addr = getAddress();
            if (addr != null)
            {
                staticAddr = getResultList().getStatic(addr);
            }
            staticAddrCacheOK = true;
        }
        return staticAddr;
    }
    
    public Object getValue()
    {
        if (datastructure.isContainer())
        {
            return null;
        }
        
        if (valueCacheOK)
        {
            return valueCache;
        }
        
        Memory buffer = new Memory(datastructure.getByteCount());
        try
        {
            Long address = getAddress();
            if (address != null && address != 0)
            {
                Result.log.trace("Read: " + getAddressString());
                getResultList().ReadProcessMemory(Pointer.createConstant(address), buffer, (int) buffer.size(), null);
                valueCache = datastructure.eval(buffer);
            }
            else
            {
                valueCache = null;
            }
        }
        catch (NoProcessException e)
        {
            valueCache = null;
        }
        catch (Exception e)
        {
            Result.log.warn("Cannot Read: " + getAddressString());
            valueCache = null;
        }
        valueCacheOK = true;
        return valueCache;
    }
    
    public String getValueString()
    {
        Object v = getValue();
        return v == null ? null : getDatastructure().valueToString(v);
    }
    
    @Override
    public void hasChanged()
    {
        valueCacheOK = false;
        staticAddrCacheOK = false;
        // log.trace("resultlist is "+getResultList()+" on "+this.hashCode());
        getResultList().nodeChanged(this);
    }
    
    private void invalidateFollowingChilds(int index)
    {
        if (childs != null)
        {
            for (int i = index + 1; i < childs.size(); i++)
            {
                childs.get(i).invalidateParentAndChilds();
            }
        }
    }
    
    private void invalidateFollowingSiblings()
    {
        if (parent instanceof Result)
        {
            List<Result> siblings = ((Result) parent).childs;
            if (siblings != null)
            {
                for (int i = siblings.indexOf(this) + 1; i < siblings.size(); i++)
                {
                    siblings.get(i).invalidateParentAndChilds();
                }
            }
        }
    }
    
    public void invalidateParentAndChilds()
    {
        valueCacheOK = false;
        pointerCacheOK = false;
        staticAddrCacheOK = false;
        getResultList().nodeChanged(this);
        if (childs != null)
        {
            for (Result r : childs)
            {
                r.invalidateParentAndChilds();
            }
        }
        
    }
    
    @Override
    public boolean isLeaf()
    {
        return !datastructure.isContainer();
    }
    
    public boolean isSimpleResult()
    {
        return parent.equals(resultList);
    }
    
    @Override
    public void pointerChanged(boolean pointer)
    {
        invalidateParentAndChilds();
        invalidateFollowingSiblings(); // ByteCount has changed of this field (addresses of the following siblings too)
        getResultList().nodeChanged(this);
        
    }
    
    private void removeAllChilds()
    {
        childs = null;
    }
    
    // triggered on the parent of the removed field
    @Override
    public void removedField(int fieldIndex)
    {
        if (childs != null)
        {
            Result.log.debug("field removed");
            Result child = childs.get(fieldIndex);
            if (child.fieldIndex == fieldIndex)
            {
                childs.remove(fieldIndex);
                for (int i = fieldIndex; i < childs.size(); i++)
                {
                    childs.get(i).fieldIndex--;
                }
                getResultList().nodeRemoved(child, fieldIndex);
                invalidateFollowingChilds(fieldIndex - 1);
            }
        }
    }
    
    // triggered on the parent of the removed field
    @Override
    public void replacedField(Datastructure oldField, Datastructure newField, int fieldIndex)
    {
        if (childs != null)
        {
            Result.log.debug("replaced Field");
            Result child = childs.get(fieldIndex);
            if (child.fieldIndex == fieldIndex)
            {
                oldField.removeListener(child);
                child.removeAllChilds();
                child.valueCacheOK = false;
                child.datastructure = newField;
                newField.addListener(child);
                if (!newField.isContainer())
                {
                    newField.setName(oldField.getName()); // take over old name
                }
                
                getResultList().reload(child); // if ds changed->childs may change
            }
        }
        invalidateFollowingChilds(fieldIndex); // ByteCount has changed of this field (addresses of the following siblings too)
    }
    
    public void setAddress(Long address)
    {
        if (isSimpleResult())
        { // only for simple result
            this.address = address;
            invalidateParentAndChilds();
        }
    }
    
    public void setDatastructure(Datastructure newDS)
    {
        Datastructure oldDS = datastructure;
        
        if (isSimpleResult())
        { // simple result
            Result.log.debug("change ds");
            
            oldDS.removeListener(this);
            removeAllChilds();
            valueCacheOK = false;
            datastructure = newDS;
            newDS.addListener(this);
            if (!newDS.isContainer())
            {
                newDS.setName(oldDS.getName()); // take over old name
            }
            
            getResultList().reload(this); // if ds changed->childs may change
        }
        else
        {
            Result.log.debug("change field");
            // Container dsParent = (Container)oldDS.getContainer(); //TODO why is there a difference (if it not a pointer)
            Container dsParent = (Container) ((Result) parent).getDatastructure();
            dsParent.replaceField(oldDS, newDS, fieldIndex);
        }
    }
    
    public void setParent(TreeNode parent)
    {
        this.parent = parent;
    }
    
    // Object//////////////////////////////////////
    
    public void setResultList(ResultList resultList)
    {
        Result.log.debug("set resultList " + resultList + " on " + hashCode());
        this.resultList = resultList;
    }
    
    // Clonable//////////////////////////////////////
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getAddressString()).append(' ');
        sb.append('[').append(getDatastructure().getName()).append("] ");
        if (!datastructure.isContainer())
        {
            sb.append(datastructure.valueToString(getValue()));
        }
        else
        {
            if (((Container) datastructure).isPointer())
            {
                sb.append(getPointerString());
            }
        }
        
        return sb.toString();
    }
    
}
