package org.mlfreeman.winapi.api;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.search.AbstractMemoryListener;
import org.mlfreeman.winapi.constants.DwDesiredAccess;
import org.mlfreeman.winapi.constants.GAFlags;
import org.mlfreeman.winapi.jna.User32;
import org.mlfreeman.winapi.tools.Kernel32Tools;
import org.mlfreeman.winapi.tools.PsapiTools;
import org.mlfreeman.winapi.tools.Shell32Tools;
import org.mlfreeman.winapi.tools.User32Tools;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Tlhelp32.PROCESSENTRY32;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.MEMORY_BASIC_INFORMATION;
import com.sun.jna.ptr.IntByReference;

public class Process
{
    private static final Log       log         = LogFactory.getLog(Process.class);
                                               
    private final int              cntThreads;
                                   
    private HANDLE                 handleCache = null;
                                               
    private List<HWND>             hWnds       = new LinkedList<HWND>();
                                               
    private ImageIcon              iconCache   = null;
                                               
    private AbstractMemoryListener listener;
                                   
    private Module                 moduleCache = null;
                                               
    private final int              pcPriClassBase;
                                   
    private final int              pid;
                                   
    private final String           szExeFile;
                                   
    private final int              th32ParentProcessID;
                                   
    public Process(PROCESSENTRY32 pe32)
    {
        this.pid = pe32.th32ProcessID.intValue();
        this.szExeFile = Native.toString(pe32.szExeFile);
        this.cntThreads = pe32.cntThreads.intValue();
        this.pcPriClassBase = pe32.pcPriClassBase.intValue();
        this.th32ParentProcessID = pe32.th32ParentProcessID.intValue();
    }
    
    public void addHwnd(HWND hWnd)
    {
        hWnds.add(hWnd);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
            
        if (!(obj instanceof Process))
            return false;
            
        if (this.getPid() != ((Process) obj).getPid())
            return false;
            
        return true;
    }
    
    public Pointer getBase()
    {
        Module module = getModule();
        if (module != null)
            return module.getLpBaseOfDll();
        else
            return Pointer.NULL;
    }
    
    public int getCntThreads()
    {
        return cntThreads;
    }
    
    public HANDLE getHandle()
    {
        if (handleCache != null)
            return handleCache;
        DwDesiredAccess dwDesiredAccess = new DwDesiredAccess();
        dwDesiredAccess.setPROCESS_ALL_ACCESS();
        handleCache = Kernel32Tools.OpenProcess(dwDesiredAccess, false, this.pid);
        return handleCache;
    }
    
    public List<HWND> getHwnds()
    {
        return hWnds;
    }
    
    public ImageIcon getIcon()
    {
        if (iconCache != null)
            return iconCache;
            
        HICON hIcon = null;
        
        Pointer attempt_1 = Shell32Tools.ExtractSmallIcon(this.getModuleFileNameExA(), 1);
        if (attempt_1 != null)
        {
            hIcon = new HICON(attempt_1);
        }
        
        if (hIcon == null)
        {
            Pointer attempt_2 = Shell32Tools.ExtractSmallIcon(szExeFile, 1);
            if (attempt_2 != null)
            {
                hIcon = new HICON(attempt_2);
            }
        }
        
        if (hIcon == null)
        {
            if (hWnds.size() > 0)
            {
                hIcon = User32Tools.getHIcon(User32.INSTANCE.GetAncestor(hWnds.get(0), GAFlags.GA_ROOTOWNER.getValue()));
            }
            
        }
        
        if (hIcon != null)
            iconCache = new ImageIcon(User32Tools.getIcon(hIcon));
        else
            iconCache = new ImageIcon();
        return iconCache;
    }
    
    public Module getModule()
    {
        if (moduleCache != null)
            return moduleCache;
            
        List<Module> modules = getModules();
        if (modules != null && modules.size() > 0)
            moduleCache = modules.get(0);
            
        return moduleCache;
    }
    
    public String getModuleFileNameExA()
    {
        try
        {
            return PsapiTools.GetModuleFileNameEx(getHandle(), null);
        }
        catch (Exception e)
        {
            return "";
        }
    }
    
    public List<Module> getModules()
    {
        // TODO add modules cache?
        try
        {
            List<HMODULE> pointers = PsapiTools.EnumProcessModules(getHandle());
            List<Module> modules = new LinkedList<Module>();
            for (HMODULE hModule : pointers)
                modules.add(new Module(getHandle(), hModule));
            return modules;
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    public int getPcPriClassBase()
    {
        return pcPriClassBase;
    }
    
    public int getPid()
    {
        return pid;
    }
    
    public String getProcessImageFileName()
    {
        try
        {
            return PsapiTools.GetProcessImageFileName(getHandle());
        }
        catch (Exception e)
        {
            return "";
        }
    }
    
    public int getSize()
    {
        Module module = getModule();
        if (module != null)
            return module.getSizeOfImage();
        else
            return 0;
    }
    
    public String getStatic(Long address)
    {
        if (address == null)
            return null;
        List<Module> modules = getModules();
        long begin, end;
        for (Module module : modules)
        {
            begin = Pointer.nativeValue(module.getLpBaseOfDll());
            end = begin + module.getSizeOfImage();
            // log.trace("module "+begin+" "+end+" "+module.getFileName());
            if (begin <= address && address <= end)
            {
                File f = new File(module.getFileName());
                return f.getName() + "+" + String.format("%08X", address - begin);
            }
        }
        return null;
    }
    
    public String getSzExeFile()
    {
        return szExeFile;
    }
    
    public int getTh32ParentProcessID()
    {
        return th32ParentProcessID;
    }
    
    public void ReadProcessMemory(Pointer pAddress, Pointer outputBuffer, int nSize, IntByReference outNumberOfBytesRead)
    {
        Kernel32Tools.ReadProcessMemory(getHandle(), pAddress, outputBuffer, nSize, outNumberOfBytesRead);
    }
    
    private void search(long from, long to)
    {
        int partSize = 512 * 1024;
        int bufferSize = partSize + listener.getOverlapping();
        int readSize;
        long regionEnd;
        MEMORY_BASIC_INFORMATION info;
        Memory outputBuffer = new Memory(bufferSize);
        long maxRegionSize = 0;
        
        for (long regionBegin = from; regionBegin < to;)
        {
            info = VirtualQueryEx(Pointer.createConstant(regionBegin));
            maxRegionSize = Math.max(maxRegionSize, info.regionSize.intValue());
            regionEnd = regionBegin + info.regionSize.intValue();
            
            if (info.state.intValue() == Kernel32Tools.MEM_COMMIT && (info.protect.intValue() & Kernel32Tools.PAGE_NOACCESS) == 0 && (info.protect.intValue() & Kernel32Tools.PAGE_GUARD) == 0 && (info.protect.intValue() & Kernel32Tools.PAGE_EXECUTE_READ) == 0 && (info.protect.intValue() & Kernel32Tools.PAGE_READONLY) == 0)
            {
                log.trace("Region:\t" + Long.toHexString(regionBegin) + " - " + Long.toHexString(regionBegin + info.regionSize.intValue()));
                
                for (long regionPart = regionBegin; regionPart < regionEnd; regionPart = regionPart + partSize)
                {
                    if ((regionPart + bufferSize) < regionEnd)
                        readSize = bufferSize;
                    else
                        readSize = (int) (regionEnd - regionPart);
                        
                    log.trace("Read:\t\t" + Long.toHexString(regionPart) + " - " + Long.toHexString(regionPart + readSize) + "\t" + Integer.toHexString(info.type.intValue()));
                    try
                    {
                        ReadProcessMemory(Pointer.createConstant(regionPart), outputBuffer, readSize, null);
                        listener.mem(outputBuffer, regionPart, readSize);
                    }
                    catch (Exception e)
                    { // FIXME
                        log.warn("Cannot search mem\t" + Long.toHexString(regionPart) + "\t" + Integer.toHexString(info.type.intValue()), e);
                    }
                }
            }
            regionBegin += info.regionSize.intValue();
        }
        log.debug("maxRegionSize " + (maxRegionSize / 1024) + " kB");
    }
    
    public synchronized void search(long from, long to, final Object value, AbstractMemoryListener listener)
    {
        log.debug("search from " + Long.toHexString(from) + " to " + Long.toHexString(to) + " value " + value + " listener " + listener);
        this.listener = listener;
        long timer = System.currentTimeMillis();
        
        this.listener.init(value);
        search(from, to);
        
        log.debug("timer " + (System.currentTimeMillis() - timer));
    }
    
    public MEMORY_BASIC_INFORMATION VirtualQueryEx(Pointer lpAddress)
    {
        return Kernel32Tools.VirtualQueryEx(getHandle(), lpAddress);
    }
    
    public void WriteProcessMemory(Pointer pAddress, Pointer inputBuffer, int nSize, IntByReference outNumberOfBytesWritten)
    {
        Kernel32Tools.WriteProcessMemory(getHandle(), pAddress, inputBuffer, nSize, outNumberOfBytesWritten);
    }
    
    // TODO stop search function
    // TODO search function progess
    
}
