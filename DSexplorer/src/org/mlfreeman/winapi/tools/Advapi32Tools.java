package org.mlfreeman.winapi.tools;

import org.mlfreeman.winapi.constants.TokenFlags;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.sun.jna.platform.win32.WinNT.LUID;
import com.sun.jna.platform.win32.WinNT.TOKEN_PRIVILEGES;

public abstract class Advapi32Tools
{
    public static final String SE_DEBUG_NAME        = "SeDebugPrivilege";
                                                    
    public static final DWORD  SE_PRIVILEGE_ENABLED = new DWORD(2);
                                                    
    public static void enableDebugPrivilege(HANDLE hProcess) throws Exception
    {
        HANDLEByReference hToken = new HANDLEByReference();
        
        TokenFlags tokenFlags = new TokenFlags();
        tokenFlags.setTOKEN_QUERY(true);
        tokenFlags.setTOKEN_ADJUST_PRIVILEGES(true);
        
        boolean success = Advapi32.INSTANCE.OpenProcessToken(hProcess, tokenFlags.getFlags(), hToken);
        if (!success)
        {
            int err = Native.getLastError();
            throw new Exception("OpenProcessToken failed. Error: " + err);
        }
        
        LUID luid = new LUID();
        success = Advapi32.INSTANCE.LookupPrivilegeValue(null, SE_DEBUG_NAME, luid);
        if (!success)
        {
            throw new Win32Exception(Native.getLastError());
        }
        
        TOKEN_PRIVILEGES tkp = new TOKEN_PRIVILEGES(1);
        tkp.Privileges[0].Luid = luid;
        tkp.Privileges[0].Attributes = SE_PRIVILEGE_ENABLED;
        success = Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.getValue(), false, tkp, 0, null, null);
        if (!success)
        {
            throw new Win32Exception(Native.getLastError());
        }
        
        Kernel32.INSTANCE.CloseHandle(hToken.getValue());
    }
    
}
