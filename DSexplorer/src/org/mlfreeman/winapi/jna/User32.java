package org.mlfreeman.winapi.jna;

import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;

public interface User32 extends com.sun.jna.platform.win32.User32
{
    
    /**
     * Retrieves the owned root window by walking the chain of parent and owner windows returned by GetParent.
     * 
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms633502(v=vs.85).aspx">MSDN</a>
     */
    // belongs in WinUser.h
    int    GA_ROOTOWNER = 3;
                        
    /**
     * Retrieves a handle to the icon associated with the class.
     * 
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms633588(v=vs.85).aspx">MSDN</a>
     */
    // belongs in WinUser.h
    int    GCL_HICON    = -14;
                        
    /**
     * Retrieves a handle to the small icon associated with the class.
     * 
     * @see <a href="https://msdn.microsoft.com/en-us/library/windows/desktop/ms633588(v=vs.85).aspx">MSDN</a>
     */
    // belongs in WinUser.h
    int    GCL_HICONSM  = -34;
                        
    User32 INSTANCE     = (User32) Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);
                        
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
