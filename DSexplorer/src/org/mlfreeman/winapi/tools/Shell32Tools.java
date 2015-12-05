package org.mlfreeman.winapi.tools;

import org.mlfreeman.winapi.jna.Shell32;

import com.sun.jna.Pointer;

public abstract class Shell32Tools
{
    public static Pointer ExtractSmallIcon(String lpszFile, int nIconIndex)
    {
        Pointer[] hIcons = new Pointer[1];
        Shell32.INSTANCE.ExtractIconExA(lpszFile, 0, null, hIcons, nIconIndex);
        return hIcons[0];
    }
    
}
