package org.mlfreeman.dsexplorer.gui.listener;

import java.util.EventListener;
import java.util.List;

import org.mlfreeman.dsexplorer.datastructures.Result;

public interface MemorySearchListener extends EventListener
{
    
    public void AddPerformed(List<Result> results);
    
    public void FirstSearchPerformed(int from, int to);
    
    public void NextSearchPerformed();
    
}
