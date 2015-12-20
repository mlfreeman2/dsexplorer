package org.mlfreeman.dsexplorer.datastructures.simple;

import org.mlfreeman.dsexplorer.datastructures.DSType;

import com.sun.jna.Memory;

public class Unicode extends DefaultDatastructure
{
    
    public Unicode()
    {
        super();
        name = getClass().getSimpleName();
        byteCount = 32;
        byteCountFix = false;
    };
    
    @Override
    public Object eval(Memory buffer)
    {
        return buffer.getWideString(0).substring(0, byteCount / 2);
    }
    
    @Override
    public DSType getType()
    {
        return DSType.Unicode;
    }
    
}
