package org.mlfreeman.dsexplorer.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.datastructures.DSType;
import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.winapi.api.Process;

import com.sun.jna.Memory;

public class Byte1Listener extends AbstractMemoryListener
{
    private static final Log log         = LogFactory.getLog(Byte1Listener.class);
    private int              overlapping = 0;
    private DSType           type        = DSType.Byte1;
    private byte             value;
                             
    public Byte1Listener(Process process)
    {
        super(process);
    }
    
    @Override
    public void init(Object value)
    {
        this.value = (Byte) value;
    }
    
    @Override
    public void mem(Memory outputBuffer, long address, long size)
    {
        byte current;
        for (long pos = 0; pos < size - overlapping; pos = pos + 1)
        {
            current = outputBuffer.getByte(pos);
            if (value == current)
            {
                add(new Result(getResultList(), type.getInstance(), address + pos, current));
                Byte1Listener.log.debug("Found:\t" + Long.toHexString(address + pos));
            }
        }
    }
    
}
