package org.mlfreeman.winapi.tools;

import java.util.LinkedList;
import java.util.List;

import org.mlfreeman.winapi.jna.Psapi;
import org.mlfreeman.winapi.jna.Psapi.LPMODULEINFO;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;

public abstract class PsapiTools
{
    public static List<HMODULE> EnumProcessModules(HANDLE hProcess) throws Exception
    {
        List<HMODULE> list = new LinkedList<HMODULE>();
        
        HMODULE[] lphModule = new HMODULE[100 * 4];
        IntByReference lpcbNeededs = new IntByReference();
        boolean success = Psapi.INSTANCE.EnumProcessModules(hProcess, lphModule, lphModule.length, lpcbNeededs);
        if (!success)
        {
            throw new Win32Exception(Native.getLastError());
        }
        for (int i = 0; i < lpcbNeededs.getValue() / 4; i++)
        {
            list.add(lphModule[i]);
        }
        
        return list;
    }
    
    public static String GetModuleFileNameEx(HANDLE hProcess, HANDLE hModule)
    {
        Memory lpImageFileName = new Memory(512);
        Psapi.INSTANCE.GetModuleFileNameEx(hProcess, hModule, lpImageFileName, 256);
        return Native.toString(lpImageFileName.getCharArray(0, 256));
    }
    
    public static String GetProcessImageFileName(HANDLE hProcess)
    {
        byte[] lpImageFileName = new byte[256];
        Psapi.INSTANCE.GetProcessImageFileName(hProcess, lpImageFileName, 256);
        return Native.toString(lpImageFileName);
    }
    
    public static LPMODULEINFO GetModuleInformation(HANDLE hProcess, HMODULE hModule) throws Exception
    {
        LPMODULEINFO lpmodinfo = new LPMODULEINFO();
        
        boolean success = Psapi.INSTANCE.GetModuleInformation(hProcess, hModule, lpmodinfo, lpmodinfo.size());
        if (!success)
        {
            throw new Win32Exception(Native.getLastError());
        }
        return lpmodinfo;
    }
    
}
