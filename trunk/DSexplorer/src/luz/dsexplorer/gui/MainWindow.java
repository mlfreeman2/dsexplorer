package luz.dsexplorer.gui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import luz.dsexplorer.gui.listener.ProcessDialogListener;
import luz.dsexplorer.tools.Process;


public class MainWindow extends javax.swing.JFrame {
	private static final long serialVersionUID = 8472126583792212590L;
	private JMenuBar jMenuBar;
	private JMenu mFile;
	private JMenuItem miNew;
	private JSeparator jSeparator2;
	private JSeparator jSeparator1;
	private JMenuItem miSettings;
	private JMenuItem miOpen;	
	private JMenuItem miSave;
	private JMenuItem miExit;
	private JMenu mEdit;
	private JMenuItem miCopy;
	private JMenuItem miPaste;	
	private JMenu mProcess;
	private JMenuItem miOpenProcess;
	private JMenu mHelp;
	private JMenuItem miHelp;
	private JMenuItem miAbout;
	private JSplitPane jSplitPane1;
	private ProcessTree tree;
	private JPanel jPanel1;
	
	/**
	* Auto-generated main method to display this JFrame
	*/
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainWindow inst = new MainWindow();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public MainWindow() {
		super();
		initGUI();
	}
	
	private void initGUI() {
		try {
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("DSExplorer v0.1");
			{
				jSplitPane1 = new JSplitPane();
				getContentPane().add(jSplitPane1, BorderLayout.CENTER);
				{
					tree = new ProcessTree();
					jSplitPane1.add(tree, JSplitPane.LEFT);
					tree.setPreferredSize(new Dimension(179, 381));
					tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
						@Override
						public void valueChanged(TreeSelectionEvent evt) {
							// TODO Auto-generated method stub
							System.out.println("Tree selection: "+evt.getPath().getLastPathComponent());
						}
					});
				}
				{
					jPanel1 = new JPanel();
					jSplitPane1.add(jPanel1, JSplitPane.RIGHT);
					jPanel1.setPreferredSize(new Dimension(236, 381));
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
					}
					{
						miOpen = new JMenuItem();
						mFile.add(miOpen);
						miOpen.setText("Open...");
					}
					{
						miSave = new JMenuItem();
						mFile.add(miSave);
						miSave.setText("Save as...");
					}
					{
						jSeparator1 = new JSeparator();
						mFile.add(jSeparator1);
					}
					{
						miExit = new JMenuItem();
						mFile.add(miExit);
						miExit.setText("Exit");
						miExit.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
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
						miCopy = new JMenuItem();
						mEdit.add(miCopy);
						miCopy.setText("Copy");
					}
					{
						miPaste = new JMenuItem();
						mEdit.add(miPaste);
						miPaste.setText("Paste");
					}
					{
						jSeparator2 = new JSeparator();
						mEdit.add(jSeparator2);
					}
					{
						miSettings = new JMenuItem();
						mEdit.add(miSettings);
						miSettings.setText("Settings...");
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
						miOpenProcess.setBounds(-60, 19, 78, 19);
						miOpenProcess.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
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
						miAbout.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								JOptionPane.showMessageDialog(MainWindow.this, "DSExplorer v0.1", "About", JOptionPane.PLAIN_MESSAGE);
							}
						});
						
					}
				}
			}
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void miOpenProcessActionPerformed() {
		ProcessDialog pd = new ProcessDialog(this);
		pd.setLocationRelativeTo(this);
		pd.refresh();
		pd.addListener(new ProcessDialogListener(){
			@Override
			public void okPerformed(Process p) {
				System.out.println(p.getModuleFileNameExA());
				tree.setProcess(p);
			}
			
			@Override
			public void cancelPerformed() {
				// TODO Auto-generated method stub
			}
		});
		pd.setVisible(true);
	}
	
	private void miExitActionPerformed() {
		this.dispose();
	}

}
