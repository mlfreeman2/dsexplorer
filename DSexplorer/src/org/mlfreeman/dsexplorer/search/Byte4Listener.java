package org.mlfreeman.dsexplorer.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.datastructures.DSType;
import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.winapi.api.Process;

import com.sun.jna.Memory;

public class Byte4Listener extends AbstractMemoryListener
{
    private static final Log log         = LogFactory.getLog(Byte4Listener.class);
    private int              overlapping = 3;
    private DSType           type        = DSType.Byte4;
    private int              value;
                             
    public Byte4Listener(Process process)
    {
        super(process);
    }
    
    @Override
    public void init(Object value)
    {
        this.value = (Integer) value;
    }
    
    @Override
    public void mem(Memory outputBuffer, long address, long size)
    {
        for (long pos = 0; pos < size - overlapping; pos = pos + 1)
        {
            int current = outputBuffer.getInt(pos);
            
            if (value == current)
            {
                add(new Result(getResultList(), type.getInstance(), address + pos, current));
                Byte4Listener.log.debug("Found:\t" + Long.toHexString(address + pos));
            }
        }
    }
    
}
