package org.mlfreeman.dsexplorer.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.datastructures.DSType;
import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.winapi.api.Process;

import com.sun.jna.Memory;

public class Byte8Listener extends AbstractMemoryListener
{
    private static final Log log         = LogFactory.getLog(Byte8Listener.class);
    private int              overlapping = 7;
    private DSType           type        = DSType.Byte8;
    private long             value;
                             
    public Byte8Listener(Process process)
    {
        super(process);
    }
    
    @Override
    public void init(Object value)
    {
        this.value = (Long) value;
    }
    
    @Override
    public void mem(Memory outputBuffer, long address, long size)
    {
        long current;
        for (long pos = 0; pos < size - overlapping; pos = pos + 1)
        {
            current = outputBuffer.getLong(pos);
            if (value == current)
            {
                add(new Result(getResultList(), type.getInstance(), address + pos, current));
                Byte8Listener.log.debug("Found:\t" + Long.toHexString(address + pos));
            }
        }
    }
    
}
