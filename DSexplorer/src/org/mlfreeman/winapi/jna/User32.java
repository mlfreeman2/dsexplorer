package org.mlfreeman.winapi.jna;

import com.sun.jna.Native;

public interface User32 extends com.sun.jna.platform.win32.User32
{
    
    User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms648058(S.85).aspx
     */
    HICON CopyIcon(HICON hIcon);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms633502(VS.85).aspx
     */
    HWND GetAncestor(HWND hwnd, int gaFlags);
    
    /*
     * http://msdn.microsoft.com/en-us/library/ms633580(VS.85).aspx
     */
    int GetClassLong(HWND hWnd, int nIndex);
    
}
