package org.mlfreeman.dsexplorer.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.datastructures.DSType;
import org.mlfreeman.dsexplorer.datastructures.Datastructure;
import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.winapi.api.Process;

import com.sun.jna.Memory;

public class ByteArrayListener extends AbstractMemoryListener
{
    private static final Log log  = LogFactory.getLog(ByteArrayListener.class);
    private byte[]           target;
    private int              targetSize;
    private DSType           type = DSType.ByteArray;
                                  
    public ByteArrayListener(Process process)
    {
        super(process);
    }
    
    @Override
    public void init(Object v)
    {
        String value = ((String) v).trim();
        overlapping = (value.length() + 1) / 2;
        
        target = new byte[value.length() / 2];
        for (int i = 0; i < target.length; i++)
        {
            target[i] = (byte) Integer.parseInt(value.substring(2 * i, 2 * i + 2), 16);
        }
        targetSize = target.length;
    }
    
    @Override
    public void mem(Memory outputBuffer, long address, long size)
    {
        byte[] current;
        boolean equal;
        for (long pos = 0; pos < size - overlapping; pos = pos + 1)
        {
            current = outputBuffer.getByteArray(pos, targetSize);
            equal = true;
            for (int i = 0; i < targetSize; i++)
            {
                if (current[i] != target[i])
                {
                    equal = false;
                }
            }
            if (equal)
            {
                Datastructure ds = type.getInstance();
                ds.setByteCount(targetSize);
                add(new Result(getResultList(), ds, address + pos, current));
                ByteArrayListener.log.debug("Found:\t" + Long.toHexString(address + pos));
            }
        }
    }
    
}
