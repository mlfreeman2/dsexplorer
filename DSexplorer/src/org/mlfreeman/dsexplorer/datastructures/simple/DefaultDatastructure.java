package org.mlfreeman.dsexplorer.datastructures.simple;

import java.util.LinkedList;
import java.util.List;

import org.mlfreeman.dsexplorer.datastructures.DSListener;
import org.mlfreeman.dsexplorer.datastructures.DSType;
import org.mlfreeman.dsexplorer.datastructures.Datastructure;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import com.sun.jna.Memory;

@Root
public abstract class DefaultDatastructure implements Datastructure
{
    @Attribute
    protected int            byteCount    = 0;
    protected boolean        byteCountFix = true;
    private List<DSListener> listeners    = new LinkedList<DSListener>();
    @Attribute
    protected String         name         = "";
    private Datastructure    parent;                                     // needs to be reconstructed after loading
                             
    protected DefaultDatastructure()
    {
    }
    
    @Override
    public void addListener(DSListener listener)
    {
        listeners.add(listener);
    }
    
    @Override
    public abstract Object eval(Memory buffer);
    
    @Override
    public int getByteCount()
    {
        return byteCount;
    }
    
    @Override
    public Datastructure getContainer()
    {
        return parent;
    }
    
    @Override
    public String getName()
    {
        return name;
    }
    
    @Override
    public abstract DSType getType();
    
    @Override
    public boolean isByteCountFix()
    {
        return byteCountFix;
    }
    
    @Override
    public boolean isContainer()
    {
        return false;
    }
    
    @Override
    public void removeListener(DSListener listener)
    {
        listeners.remove(listener);
    }
    
    @Override
    public void setByteCount(int byteCount)
    {
        if (!isByteCountFix())
        {
            this.byteCount = byteCount;
            for (int i = listeners.size() - 1; i >= 0; i--)
            {
                listeners.get(i).hasChanged();
            }
        }
    }
    
    @Override
    public void setContainer(Datastructure parent)
    {
        this.parent = parent;
    }
    
    @Override
    public void setName(String name)
    {
        this.name = name;
        for (int i = listeners.size() - 1; i >= 0; i--)
        {
            listeners.get(i).hasChanged();
        }
    }
    
    @Override
    public String valueToString(Object value)
    {
        return value == null ? null : value.toString();
    }
}
