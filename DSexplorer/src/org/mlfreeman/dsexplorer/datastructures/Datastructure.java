package org.mlfreeman.dsexplorer.datastructures;

import com.sun.jna.Memory;

public interface Datastructure
{
    
    public void addListener(DSListener listener);
    
    public Object eval(Memory buffer);
    
    public int getByteCount();
    
    public Datastructure getContainer();
    
    public String getName();
    
    public DSType getType();
    
    public boolean isByteCountFix();
    
    public boolean isContainer();
    
    public void removeListener(DSListener listener);
    
    public void setByteCount(int byteCount);
    
    public void setContainer(Datastructure container);
    
    public void setName(String name);
    
    public String valueToString(Object value);
    
}
