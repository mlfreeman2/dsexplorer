package org.mlfreeman.dsexplorer.datastructures;

import org.mlfreeman.dsexplorer.datastructures.simple.Ascii;
import org.mlfreeman.dsexplorer.datastructures.simple.Byte1;
import org.mlfreeman.dsexplorer.datastructures.simple.Byte2;
import org.mlfreeman.dsexplorer.datastructures.simple.Byte4;
import org.mlfreeman.dsexplorer.datastructures.simple.Byte8;
import org.mlfreeman.dsexplorer.datastructures.simple.ByteArray;
import org.mlfreeman.dsexplorer.datastructures.simple.Double;
import org.mlfreeman.dsexplorer.datastructures.simple.Float;
import org.mlfreeman.dsexplorer.datastructures.simple.TimeUnix;
import org.mlfreeman.dsexplorer.datastructures.simple.TimeW32;
import org.mlfreeman.dsexplorer.datastructures.simple.Unicode;

public enum DSType
{
    Ascii(Ascii.class), // class
    Byte1(Byte1.class), Byte2(Byte2.class), Byte4(Byte4.class), Byte8(Byte8.class), ByteArray(ByteArray.class), Container(ContainerImpl.class), Double(Double.class), Float(Float.class), TimeUnix(TimeUnix.class), TimeW32(TimeW32.class), Unicode(Unicode.class);
    private Class<? extends Datastructure> clazz;
    
    DSType(Class<? extends Datastructure> clazz)
    {
        this.clazz = clazz;
    }
    
    public Datastructure getInstance()
    {
        Datastructure instance = null;
        try
        {
            instance = clazz.newInstance();
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        return instance;
    }
    
}