package org.mlfreeman.dsexplorer.datastructures.simple;

import org.mlfreeman.dsexplorer.datastructures.DSType;

import com.sun.jna.Memory;

public class Ascii extends DefaultDatastructure
{
    
    public Ascii()
    {
        super();
        name = getClass().getSimpleName();
        byteCount = 32;
        byteCountFix = false;
    };
    
    @Override
    public Object eval(Memory buffer)
    {
        return new String(buffer.getByteArray(0, (int) buffer.size()));
    }
    
    @Override
    public DSType getType()
    {
        return DSType.Ascii;
    }
}
