package org.mlfreeman.winapi.jna.util;

import java.awt.image.BufferedImage;

import org.mlfreeman.winapi.jna.User32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HICON;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFOHEADER;
import com.sun.jna.platform.win32.WinGDI.ICONINFO;
import com.sun.jna.platform.win32.WinUser;

public class User32Util
{
    
    public static BufferedImage getIcon(HICON hIcon)
    {
        int width = 16;
        int height = 16;
        short depth = 24;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        Memory lpBitsColor = new Memory(width * height * depth / 8);
        Memory lpBitsMask = new Memory(width * height * depth / 8);
        BITMAPINFO info = new BITMAPINFO();
        BITMAPINFOHEADER hdr = new BITMAPINFOHEADER();
        info.bmiHeader = hdr;
        hdr.biWidth = width;
        hdr.biHeight = height;
        hdr.biPlanes = 1;
        hdr.biBitCount = depth;
        hdr.biCompression = WinGDI.BI_RGB;
        
        HDC hDC = User32.INSTANCE.GetDC(null);
        ICONINFO piconinfo = new ICONINFO();
        User32.INSTANCE.GetIconInfo(hIcon, piconinfo);
        
        GDI32.INSTANCE.GetDIBits(hDC, piconinfo.hbmColor, 0, height, lpBitsColor, info, WinGDI.DIB_RGB_COLORS);
        GDI32.INSTANCE.GetDIBits(hDC, piconinfo.hbmMask, 0, height, lpBitsMask, info, WinGDI.DIB_RGB_COLORS);
        
        int r, g, b, a, argb;
        int x = 0, y = height - 1;
        for (int i = 0; i < lpBitsColor.size(); i = i + 3)
        {
            b = lpBitsColor.getByte(i) & 0xFF;
            g = lpBitsColor.getByte(i + 1) & 0xFF;
            r = lpBitsColor.getByte(i + 2) & 0xFF;
            a = 0xFF - lpBitsMask.getByte(i) & 0xFF;
            
            argb = a << 24 | r << 16 | g << 8 | b;
            image.setRGB(x, y, argb);
            x = (x + 1) % width;
            if (x == 0)
            {
                y--;
            }
        }
        
        User32.INSTANCE.ReleaseDC(null, hDC);
        GDI32.INSTANCE.DeleteObject(piconinfo.hbmColor);
        GDI32.INSTANCE.DeleteObject(piconinfo.hbmMask);
        
        return image;
    }
    
    private static long GetClassLong(HWND hWnd, int nIndex) throws Exception
    {
        long ret = User32.INSTANCE.GetClassLong(hWnd, nIndex);
        if (ret == 0)
        {
            throw new Win32Exception(Native.getLastError());
        }
        return ret;
    }
    
    public static HICON getHIcon(HWND hWnd)
    {
        try
        {
            Pointer icon = SendMessageTimeoutA(hWnd, WinUser.WM_GETICON, WinUser.ICON_SMALL, 0, WinUser.SMTO_NORMAL, 20);
            if (Pointer.nativeValue(icon) != 0)
            {
                return User32.INSTANCE.CopyIcon(new HICON(icon));
            }
        }
        catch (Exception e)
        {
        }
        
        try
        {
            Pointer icon = SendMessageTimeoutA(hWnd, WinUser.WM_GETICON, WinUser.ICON_BIG, 0, WinUser.SMTO_NORMAL, 20);
            if (Pointer.nativeValue(icon) != 0)
            {
                return User32.INSTANCE.CopyIcon(new HICON(icon));
            }
        }
        catch (Exception e)
        {
        }
        
        try
        {
            Pointer icon = SendMessageTimeoutA(hWnd, WinUser.WM_GETICON, WinUser.ICON_SMALL2, 0, WinUser.SMTO_NORMAL, 20);
            if (Pointer.nativeValue(icon) != 0)
            {
                return User32.INSTANCE.CopyIcon(new HICON(icon));
            }
        }
        catch (Exception e)
        {
        }
        
        try
        {
            long hiconSM = GetClassLong(hWnd, User32.GCL_HICONSM);
            if (hiconSM != 0)
            {
                return User32.INSTANCE.CopyIcon(new HICON(Pointer.createConstant(hiconSM)));
            }
        }
        catch (Exception e)
        {
        }
        
        try
        {
            long hicon = GetClassLong(hWnd, User32.GCL_HICON);
            if (hicon != 0)
            {
                return User32.INSTANCE.CopyIcon(new HICON(Pointer.createConstant(hicon)));
            }
        }
        catch (Exception e)
        {
        }
        
        return null;
    }
    
    private static Pointer SendMessageTimeoutA(HWND hWnd, int msg, int wParam, int lParam, int fuFlags, int uTimeout) throws Exception
    {
        DWORDByReference lpdwResult = new DWORDByReference();
        long ret = User32.INSTANCE.SendMessageTimeout(hWnd, msg, wParam, lParam, fuFlags, uTimeout, lpdwResult);
        if (ret == 0)
        {
            throw new Win32Exception(Native.getLastError());
        }
        return Pointer.createConstant(lpdwResult.getValue().longValue());
    }
    
}
