package org.mlfreeman.dsexplorer.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.datastructures.DSType;
import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.winapi.api.Process;

import com.sun.jna.Memory;

public class Byte2Listener extends AbstractMemoryListener
{
    private static final Log log         = LogFactory.getLog(Byte2Listener.class);
    private int              overlapping = 1;
    private DSType           type        = DSType.Byte2;
    private short            value;
                             
    public Byte2Listener(Process process)
    {
        super(process);
    }
    
    @Override
    public void init(Object value)
    {
        this.value = (Short) value;
    }
    
    @Override
    public void mem(Memory outputBuffer, long address, long size)
    {
        short current;
        for (long pos = 0; pos < size - overlapping; pos = pos + 1)
        {
            current = outputBuffer.getShort(pos);
            if (value == current)
            {
                add(new Result(getResultList(), type.getInstance(), address + pos, current));
                Byte2Listener.log.debug("Found:\t" + Long.toHexString(address + pos));
            }
        }
    }
    
}
