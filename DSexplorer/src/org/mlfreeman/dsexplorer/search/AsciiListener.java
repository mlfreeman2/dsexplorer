package org.mlfreeman.dsexplorer.search;

import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.datastructures.DSType;
import org.mlfreeman.dsexplorer.datastructures.Datastructure;
import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.winapi.api.Process;

import com.sun.jna.Memory;

public class AsciiListener extends AbstractMemoryListener
{
    private static final Log log   = LogFactory.getLog(AsciiListener.class);
    private Charset          ascii = Charset.forName("US-ASCII");
    private int              targetSize;
    private DSType           type  = DSType.Ascii;
    private String           value;
                             
    public AsciiListener(Process process)
    {
        super(process);
    }
    
    @Override
    public void init(Object value)
    {
        this.value = (String) value;
        overlapping = this.value.length();
        targetSize = this.value.length();
    }
    
    @Override
    public void mem(Memory outputBuffer, long address, long size)
    {
        String current;
        for (long pos = 0; pos < size - overlapping; pos = pos + 1)
        {
            current = new String(outputBuffer.getByteArray(pos, targetSize), ascii);
            if (current.equalsIgnoreCase(value))
            {
                Datastructure ds = type.getInstance();
                ds.setByteCount(targetSize);
                add(new Result(getResultList(), ds, address + pos, current));
                AsciiListener.log.debug("Found:\t" + Long.toHexString(address + pos));
            }
        }
    }
    
}
