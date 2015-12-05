package org.mlfreeman.dsexplorer.search;

import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.dsexplorer.datastructures.ResultList;
import org.mlfreeman.dsexplorer.datastructures.ResultList;
import org.mlfreeman.winapi.api.Process;

import com.sun.jna.Memory;

public abstract class AbstractMemoryListener
{
    protected int        overlapping;
    protected ResultList results;
                         
    public AbstractMemoryListener(Process process)
    {
        results = new ResultList(process);
    }
    
    public void add(Result result)
    {
        results.add(result);
    }
    
    /**
     * To optimize memory reading, the reading algorithm divides large memory areas into smaller ones. This value forces the reading algorithm to overlap these memory areas by the given value. This is usefull if the seach pattern is longer than one Byte. In this case, the MemoryListener has only to worry about one area at each time. Values at the end of an area will repeat at the beginning of the next area. In general this value should be 'size of the search pattern -1'.
     *
     * @param overlapping
     */
    public int getOverlapping()
    {
        return overlapping;
    }
    
    public ResultList getResultList()
    {
        return results;
    }
    
    public ResultList getResults()
    {
        return results;
    }
    
    public abstract void init(Object value);
    
    public abstract void mem(Memory outputBuffer, long address, long size);
}
