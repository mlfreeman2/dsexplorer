package org.mlfreeman.dsexplorer.gui.listener;

import java.util.EventListener;

import org.mlfreeman.winapi.api.Process;

public interface ProcessDialogListener extends EventListener
{
    
    public void cancelPerformed();
    
    public void okPerformed(Process p);
    
}
