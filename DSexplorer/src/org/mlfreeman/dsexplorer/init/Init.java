package org.mlfreeman.dsexplorer.init;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mlfreeman.dsexplorer.gui.MainWindow;
import org.mlfreeman.winapi.tools.Advapi32Tools;

import com.sun.jna.platform.win32.Kernel32;

public class Init
{
    private static final Log log = LogFactory.getLog(Init.class);
    
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException e)
        {
            Init.log.warn("getSystemLookAndFeelClassName", e);
        }
        catch (InstantiationException e)
        {
            Init.log.warn("getSystemLookAndFeelClassName", e);
        }
        catch (IllegalAccessException e)
        {
            Init.log.warn("getSystemLookAndFeelClassName", e);
        }
        catch (UnsupportedLookAndFeelException e)
        {
            Init.log.warn("getSystemLookAndFeelClassName", e);
        }
        
        try
        {
            Advapi32Tools.enableDebugPrivilege(Kernel32.INSTANCE.GetCurrentProcess());
        }
        catch (Exception e)
        {
            Init.log.warn("enableDebugPrivilege", e);
        }
        
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                MainWindow inst = new MainWindow();
                inst.setLocationRelativeTo(null);
                inst.setVisible(true);
            }
        });
        
    }
    
}
