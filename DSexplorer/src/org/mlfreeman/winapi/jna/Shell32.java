package org.mlfreeman.winapi.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.W32APIOptions;

public interface Shell32 extends com.sun.jna.platform.win32.Shell32
{
    Shell32 INSTANCE = (Shell32) Native.loadLibrary("shell32", Shell32.class, W32APIOptions.DEFAULT_OPTIONS);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms648069(VS.85).aspx
     */
    public int ExtractIconEx(String lpszFile, int nIconIndex, Pointer[] phiconLarge, Pointer[] phiconSmall, int nIcons);
}