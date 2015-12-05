package org.mlfreeman.winapi.constants;

import java.util.BitSet;

/*
 * http://msdn.microsoft.com/en-us/library/aa374905(v=VS.85%).aspx http://www.pinvoke.net/default.aspx/advapi32/OpenProcessToken.html
 */
public class TokenFlags
{
    
    private BitSet flags = new BitSet(20);
    
    public void setTOKEN_ASSIGN_PRIMARY(boolean b)
    {
        flags.set(0, b);
    } // 0x00000001
    
    public void setTOKEN_DUPLICATE(boolean b)
    {
        flags.set(1, b);
    } // 0x00000002
    
    public void setTOKEN_IMPERSONATE(boolean b)
    {
        flags.set(2, b);
    } // 0x00000004
    
    public void setTOKEN_QUERY(boolean b)
    {
        flags.set(3, b);
    } // 0x00000008
    
    public void setTOKEN_QUERY_SOURCE(boolean b)
    {
        flags.set(4, b);
    } // 0x00000010
    
    public void setTOKEN_ADJUST_PRIVILEGES(boolean b)
    {
        flags.set(5, b);
    } // 0x00000020
    
    public void setTOKEN_ADJUST_GROUPS(boolean b)
    {
        flags.set(6, b);
    } // 0x00000040
    
    public void setTOKEN_ADJUST_DEFAULT(boolean b)
    {
        flags.set(7, b);
    } // 0x00000080
    
    public void setTOKEN_ADJUST_SESSIONID(boolean b)
    {
        flags.set(8, b);
    } // 0x00000100
    
    public void setSTANDARD_RIGHTS_DELETE(boolean b)
    {
        flags.set(16, b);
    } // 0x00010000
    
    public void setSTANDARD_RIGHTS_READ_CONTROL(boolean b)
    {
        flags.set(17, b);
    } // 0x00020000
    
    public void setSTANDARD_RIGHTS_WRITE_DAC(boolean b)
    {
        flags.set(18, b);
    } // 0x00040000
    
    public void setSTANDARD_RIGHTS_WRITE_OWNER(boolean b)
    {
        flags.set(19, b);
    } // 0x00080000
    
    public int getFlags()
    {
        int value = 0;
        int max = flags.length();
        for (int i = 0; i < max; i++)
        {
            if (flags.get(i))
                value |= (1 << i);
        }
        return value;
    }
}