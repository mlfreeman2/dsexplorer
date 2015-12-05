package org.mlfreeman.dsexplorer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;

import org.mlfreeman.dsexplorer.datastructures.Result;
import org.mlfreeman.dsexplorer.datastructures.ResultList;
import org.mlfreeman.dsexplorer.datastructures.simple.Byte4;
import org.mlfreeman.dsexplorer.gui.listener.MemorySearchListener;
import org.mlfreeman.dsexplorer.gui.listener.ProcessDialogListener;
import org.mlfreeman.winapi.api.Process;

public class MainWindow extends javax.swing.JFrame
{
    private static final long serialVersionUID = 8472126583792212590L;
    
    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args)
    {
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
    
    private DSEditor       dse;
    private JFileChooser   fc;
    private JMenuBar       jMenuBar;
    private JSeparator     jSeparator1;
    private JSeparator     jSeparator3;
    private JMenu          mEdit;
    private JMenu          mFile;
    private JMenu          mHelp;
    private JMenuItem      miAbout;
    private JMenuItem      miAdd;
    private JMenuItem      miCopy;
    private JMenuItem      miDelete;
    private JMenuItem      miExit;
    private JMenuItem      miHelp;
    private JMenuItem      miNew;
    private JMenuItem      miOpen;
    private JMenuItem      miOpenProcess;
    private JMenuItem      miPaste;
    private JMenuItem      miSave;
    private JMenu          mProcess;
    private MemorySearch   ms;
    private ProcessDialog  pd;
    private JPanel         rightPanel;
    private ResultList rl      = new ResultList();
    private JScrollPane    scrTree;
    private JSplitPane     splMain;
    private ProcessTree    tree;
                           
    private String         version = "0.6";
                                   
    public MainWindow()
    {
        super();
        initGUI();
        
        pd = new ProcessDialog(this);
        pd.setLocationRelativeTo(this);
        pd.addListener(new ProcessDialogListener()
        {
            @Override
            public void cancelPerformed()
            {
            
            }
            
            @Override
            public void okPerformed(Process p)
            {
                tree.setProcess(p);
                ms.setProcess(p);
            }
        });
        
        ms = new MemorySearch();
        ms.addListener(new MemorySearchListener()
        {
            @Override
            public void AddPerformed(List<Result> results)
            {
                tree.addResults(results);
            }
            
            @Override
            public void FirstSearchPerformed(int from, int to)
            {
            
            }
            
            @Override
            public void NextSearchPerformed()
            {
            
            }
        });
        
        dse = new DSEditor();
        dse.setDataStructures(rl.getDatastructures());
        
        fc = new JFileChooser();
        fc.setFileFilter(new FileFilter()
        {
            @Override
            public boolean accept(File f)
            {
                if (f != null && (f.isDirectory() || f.getName().toLowerCase().endsWith(".xml")))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            
            @Override
            public String getDescription()
            {
                return "xml DS Explorer savefile";
            }
        });
    }
    
    private void initGUI()
    {
        try
        {
            BorderLayout thisLayout = new BorderLayout();
            getContentPane().setLayout(thisLayout);
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setTitle("DSExplorer v" + version);
            {
                splMain = new JSplitPane();
                splMain.setResizeWeight(1);
                getContentPane().add(splMain, BorderLayout.CENTER);
                {
                    scrTree = new JScrollPane();
                    splMain.add(scrTree, JSplitPane.LEFT);
                    scrTree.setPreferredSize(new Dimension(250, 550));
                    scrTree.setMinimumSize(new Dimension(100, 350));
                    {
                        tree = new ProcessTree();
                        scrTree.setViewportView(tree);
                        tree.setResultList(rl);
                        tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener()
                        {
                            @Override
                            public void valueChanged(TreeSelectionEvent evt)
                            {
                                treeSelectionEvent(evt);
                            }
                        });
                    }
                }
                {
                    rightPanel = new JPanel();
                    splMain.add(rightPanel, JSplitPane.RIGHT);
                    BorderLayout jPanel1Layout = new BorderLayout();
                    rightPanel.setLayout(jPanel1Layout);
                    rightPanel.setPreferredSize(new Dimension(531, 550));
                    rightPanel.setMinimumSize(new Dimension(531, 350));
                }
            }
            {
                jMenuBar = new JMenuBar();
                setJMenuBar(jMenuBar);
                {
                    mFile = new JMenu();
                    jMenuBar.add(mFile);
                    mFile.setText("File");
                    {
                        miNew = new JMenuItem();
                        mFile.add(miNew);
                        miNew.setText("New");
                        miNew.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent evt)
                            {
                                miNewActionPerformed(evt);
                            }
                        });
                    }
                    {
                        miOpen = new JMenuItem();
                        mFile.add(miOpen);
                        miOpen.setText("Open...");
                        miOpen.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent evt)
                            {
                                miOpenActionPerformed(evt);
                            }
                        });
                    }
                    {
                        miSave = new JMenuItem();
                        mFile.add(miSave);
                        miSave.setText("Save as...");
                        miSave.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent evt)
                            {
                                miSaveActionPerformed(evt);
                            }
                        });
                    }
                    {
                        jSeparator1 = new JSeparator();
                        mFile.add(jSeparator1);
                    }
                    {
                        miExit = new JMenuItem();
                        mFile.add(miExit);
                        miExit.setText("Exit");
                        miExit.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent evt)
                            {
                                miExitActionPerformed();
                            }
                        });
                    }
                }
                {
                    mEdit = new JMenu();
                    jMenuBar.add(mEdit);
                    mEdit.setText("Edit");
                    {
                        miAdd = new JMenuItem();
                        mEdit.add(miAdd);
                        miAdd.setText("Add");
                        miAdd.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent evt)
                            {
                                miAddActionPerformed(evt);
                            }
                        });
                    }
                    {
                        miDelete = new JMenuItem();
                        mEdit.add(miDelete);
                        miDelete.setText("Delete");
                        miDelete.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent evt)
                            {
                                miDeleteActionPerformed(evt);
                            }
                        });
                    }
                    {
                        jSeparator3 = new JSeparator();
                        mEdit.add(jSeparator3);
                    }
                    {
                        miCopy = new JMenuItem();
                        mEdit.add(miCopy);
                        miCopy.setText("Copy");
                    }
                    {
                        miPaste = new JMenuItem();
                        mEdit.add(miPaste);
                        miPaste.setText("Paste");
                    }
                }
                {
                    mProcess = new JMenu();
                    jMenuBar.add(mProcess);
                    mProcess.setText("Process");
                    {
                        miOpenProcess = new JMenuItem();
                        mProcess.add(miOpenProcess);
                        miOpenProcess.setText("Open Process...");
                        miOpenProcess.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent evt)
                            {
                                miOpenProcessActionPerformed();
                            }
                        });
                    }
                }
                {
                    mHelp = new JMenu();
                    jMenuBar.add(mHelp);
                    mHelp.setText("Help");
                    {
                        miHelp = new JMenuItem();
                        mHelp.add(miHelp);
                        miHelp.setText("Help");
                    }
                    {
                        miAbout = new JMenuItem();
                        mHelp.add(miAbout);
                        miAbout.setText("About");
                        miAbout.addActionListener(new ActionListener()
                        {
                            @Override
                            public void actionPerformed(ActionEvent evt)
                            {
                                JOptionPane.showMessageDialog(MainWindow.this, "DSExplorer v" + version, "About", JOptionPane.PLAIN_MESSAGE);
                            }
                        });
                        
                    }
                }
            }
            pack();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    // $hide>>$
    
    private void miAddActionPerformed(ActionEvent evt)
    {
        Result r = new Result(new Byte4());
        tree.addResult(r);
        // TODO context relative addField or addResult
    }
    
    private void miDeleteActionPerformed(ActionEvent evt)
    {
        tree.deleteSelected();
    }
    
    private void miExitActionPerformed()
    {
        dispose();
    }
    
    private void miNewActionPerformed(ActionEvent evt)
    {
        tree.reset();
        
    }
    
    private void miOpenActionPerformed(ActionEvent evt)
    {
        File f = null;
        while (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            f = fc.getSelectedFile();
            if (f.exists())
            {
                break;
            }
        }
        if (f != null)
        {
            ResultList rl;
            try
            {
                rl = ResultList.openFromFile(f);
                tree.setResultList(rl);
                dse.setDataStructures(rl.getDatastructures());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private void miOpenProcessActionPerformed()
    {
        pd.refresh();
        pd.setVisible(true);
    }
    
    private void miSaveActionPerformed(ActionEvent evt)
    {
        File f = null;
        while (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            f = fc.getSelectedFile();
            String path = f.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".xml"))
            {
                f = new File(path + ".xml");
            }
            if (f.exists())
            {
                if (JOptionPane.showConfirmDialog(this, "Overwrite " + f.getName() + " ?", "Overwrite", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION)
                {
                    break;
                }
            }
            else
            {
                break;
            }
        }
        if (f != null)
        {
            tree.saveToFile(f);
        }
    }
    
    private void treeSelectionEvent(TreeSelectionEvent evt)
    {
        Object node = evt.getPath().getLastPathComponent();
        if (node instanceof ResultList)
        {
            rightPanel.removeAll();
            rightPanel.add(ms, BorderLayout.CENTER);
            rightPanel.repaint();
            rightPanel.validate();
        }
        if (node instanceof Result)
        {
            rightPanel.removeAll();
            dse.setResult((Result) node);
            rightPanel.add(dse, BorderLayout.CENTER);
            rightPanel.repaint();
            rightPanel.validate();
        }
    }
    
    // $hide<<$
}
