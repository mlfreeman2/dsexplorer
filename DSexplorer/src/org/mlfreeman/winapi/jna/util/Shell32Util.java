package org.mlfreeman.winapi.jna.util;

import org.mlfreeman.winapi.jna.Shell32;

import com.sun.jna.Pointer;

public abstract class Shell32Util
{
    public static Pointer ExtractSmallIcon(String lpszFile, int nIconIndex)
    {
        Pointer[] hIcons = new Pointer[1];
        Shell32.INSTANCE.ExtractIconEx(lpszFile, 0, null, hIcons, nIconIndex);
        return hIcons[0];
    }
    
}
