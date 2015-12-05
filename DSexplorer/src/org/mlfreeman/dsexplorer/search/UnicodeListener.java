package org.mlfreeman.dsexplorer.search;

import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.datastructures.DSType;
import org.mlfreeman.dsexplorer.datastructures.Datastructure;
import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.winapi.api.Process;

import com.sun.jna.Memory;

public class UnicodeListener extends AbstractMemoryListener
{
    private static final Log log   = LogFactory.getLog(UnicodeListener.class);
    private int              targetSize;
    private DSType           type  = DSType.Unicode;
    private Charset          utf16 = Charset.forName("UTF-16");
    private String           value;
                             
    public UnicodeListener(Process process)
    {
        super(process);
    }
    
    @Override
    public void init(Object value)
    {
        this.value = ((String) value).toLowerCase();
        overlapping = this.value.length() * 2; // Assume utf16 = 2 bytes
        targetSize = this.value.length() * 2;
    }
    
    @Override
    public void mem(Memory outputBuffer, long address, long size)
    {
        String current;
        for (long pos = 0; pos < size - overlapping; pos = pos + 1)
        {
            current = outputBuffer.getString(pos, true);
            if (current.toLowerCase().startsWith(value))
            {
                Datastructure ds = type.getInstance();
                ds.setByteCount(targetSize);
                add(new Result(getResultList(), ds, address + pos, current));
                UnicodeListener.log.debug("Found:\t" + Long.toHexString(address + pos));
            }
        }
    }
    
}
