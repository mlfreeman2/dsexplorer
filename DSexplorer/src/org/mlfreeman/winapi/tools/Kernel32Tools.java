package org.mlfreeman.winapi.tools;

import java.util.LinkedList;
import java.util.List;

import org.mlfreeman.winapi.api.Process;
import org.mlfreeman.winapi.api.ProcessList;
import org.mlfreeman.winapi.constants.DwDesiredAccess;
import org.mlfreeman.winapi.jna.User32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.BaseTSD.SIZE_T;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.Tlhelp32.PROCESSENTRY32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.MEMORY_BASIC_INFORMATION;
import com.sun.jna.ptr.IntByReference;

public abstract class Kernel32Tools
{
    public static int MEM_COMMIT             = 0x01000;
                                             
    public static int MEM_FREE               = 0x10000;
                                             
    public static int MEM_RESERVE            = 0x02000;
                                             
    public static int PAGE_EXECUTE           = 0x0010; // 000 0001 0000
                                             
    public static int PAGE_EXECUTE_READ      = 0x0020; // 000 0010 0000
    public static int PAGE_EXECUTE_READWRITE = 0x0040; // 000 0100 0000
    public static int PAGE_EXECUTE_WRITECOPY = 0x0080; // 000 1000 0000
                                             
    public static int PAGE_GUARD             = 0x0100; // 001 0000 0000
    public static int PAGE_NOACCESS          = 0x0001; // 000 0000 0001
    public static int PAGE_NOCACHE           = 0x0200; // 010 0000 0000
    public static int PAGE_READONLY          = 0x0002; // 000 0000 0010
    public static int PAGE_READWRITE         = 0x0004; // 000 0000 0100
    public static int PAGE_WRITECOMBINE      = 0x0400; // 100 0000 0000
    public static int PAGE_WRITECOPY         = 0x0008; // 000 0000 1000
                                             
    public static ProcessList getProcessList()
    {
        ProcessList plist = new ProcessList();
        
        List<PROCESSENTRY32> list = new LinkedList<PROCESSENTRY32>();
        
        HANDLE hProcessSnap = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new DWORD(0));
        
        PROCESSENTRY32 pe32 = new PROCESSENTRY32();
        boolean success = Kernel32.INSTANCE.Process32First(hProcessSnap, pe32);
        if (!success)
        {
            throw new Win32Exception(Native.getLastError());
        }
        
        do
        {
            if (pe32.th32ProcessID.intValue() != 0)
            {
                list.add(pe32);
            }
            pe32 = new PROCESSENTRY32();
        } while (Kernel32.INSTANCE.Process32Next(hProcessSnap, pe32));
        
        for (PROCESSENTRY32 pe : list)
        {
            plist.add(new Process(pe));
        }
        
        List<DesktopWindow> windows = WindowUtils.getAllWindows(false);
        IntByReference lpdwProcessId = new IntByReference();
        int pid = 0;
        for (DesktopWindow window : windows)
        {
            User32.INSTANCE.GetWindowThreadProcessId(window.getHWND(), lpdwProcessId);
            pid = lpdwProcessId.getValue();
            plist.add(pid, window.getHWND());
        }
        
        return plist;
    }
    
    public static HANDLE OpenProcess(DwDesiredAccess dwDesiredAccess, boolean bInheritHandle, int dwProcessId)
    {
        HANDLE process = Kernel32.INSTANCE.OpenProcess(dwDesiredAccess.getFlags(), false, dwProcessId);
        if (process == null)
        {
            throw new Win32Exception(Native.getLastError());
        }
        return process;
    }
    
    public static void ReadProcessMemory(HANDLE hProcess, Pointer pAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead)
    {
        boolean success = Kernel32.INSTANCE.ReadProcessMemory(hProcess, pAddress, outputBuffer, nSize, outNumberOfBytesRead);
        if (!success)
        {
            throw new Win32Exception(Native.getLastError());
        }
    }
    
    public static MEMORY_BASIC_INFORMATION VirtualQueryEx(HANDLE hProcess, Pointer lpAddress)
    {
        MEMORY_BASIC_INFORMATION lpBuffer = new MEMORY_BASIC_INFORMATION();
        SIZE_T ret = Kernel32.INSTANCE.VirtualQueryEx(hProcess, lpAddress, lpBuffer, new SIZE_T(lpBuffer.size()));
        if (ret.intValue() == 0)
        {
            throw new Win32Exception(Native.getLastError());
        }
        return lpBuffer;
    }
    
    public static void WriteProcessMemory(HANDLE hProcess, Pointer pAddress, Pointer inputBuffer, int nSize, IntByReference outNumberOfBytesWritten)
    {
        boolean success = Kernel32.INSTANCE.WriteProcessMemory(hProcess, pAddress, inputBuffer, nSize, outNumberOfBytesWritten);
        if (!success)
        {
            throw new Win32Exception(Native.getLastError());
        }
    }
}
