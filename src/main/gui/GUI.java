package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.concurrent.FutureTask;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UnsupportedLookAndFeelException;

import core.ExcelLent;

public class GUI  extends JFrame{
	
	private Explorer explore;
	private static ImageIcon background;
	public GUI()
	{
		super("Excel-lent");
		 this.background = new ImageIcon("img/wallpaperadn.PNG");
	     this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     JMenuBar menuBar = new JMenuBar();
			GraphicsEnvironment env =
	        GraphicsEnvironment.getLocalGraphicsEnvironment();
	    	this.setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);
	        
	        JLabel titre = new JLabel("Excel-Lent",JLabel.CENTER);
	        titre.setBorder(BorderFactory.createEmptyBorder(10, 10, 50,10));
	        Font font = new Font("Roboto", Font.BOLD, 28);
	        titre.setAlignmentX(CENTER_ALIGNMENT);
	        titre.setBackground(Color.white);
	        titre.setFont(font);
	        JLabel label = new JLabel("Select your choise");
	        label.setAlignmentX(CENTER_ALIGNMENT);
	        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 30,10));
	        label.setFont(new Font("Roboto", Font.BOLD, 15));
	        Global.lbl = label;
	        final JProgressBar progressBar = new JProgressBar(0, 100);
	        final ExcelLent excellent = new ExcelLent(new ProgressBarListener(progressBar));
	        
	        
	        
	        
	        
	        ImagePanel panel = new ImagePanel(this.background.getImage());
	        panel.repaint();
	        panel.setLayout(new BorderLayout());
			JButton button = new JButton(new AbstractAction("Run") {
				public void actionPerformed(ActionEvent e) {
					new Thread(excellent).start();
					System.out.println("Executing excellent");
			    }
			});
			
	        button.setVerticalTextPosition(AbstractButton.CENTER);
	        button.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
	        button.setMnemonic(KeyEvent.VK_ENTER);
	        button.setSize(100, 50);;
	        button.setActionCommand("run");
	        button.setAlignmentX(LEFT_ALIGNMENT);
	        button.setEnabled(false);
	        Global.btn_run = button;
	        GridLayout grid = new GridLayout(0, 1, 10, 10);
	        
	        ImagePanel panelGrid = new ImagePanel(this.background.getImage());
	        BoxLayout boxl =  new BoxLayout(panelGrid, BoxLayout.PAGE_AXIS);
	    
	        //panelGrid.setLayout(grid);
	        panelGrid.setLayout(boxl);
	        panelGrid.add(titre);
	        panelGrid.add(label);
	        panelGrid.add(button);
	        panel.add(progressBar,BorderLayout.SOUTH);
	        panel.add(panelGrid, BorderLayout.CENTER);
	        
	        try
	        {
	        	File rep = new File(System.getProperty("user.dir")+"\tree");
	        	explore =  new Explorer(rep.listFiles());
	        }
	        catch (NullPointerException e)
	        {
	        	explore = new Explorer();
	        }
	        
	        
	        
	        explore.setAutoscrolls(true);
	        panel.add(explore, BorderLayout.WEST);
	        this.setContentPane(panel);
	        this.setSize(1000,500);
	        //Display the window.
	        this.pack();
	        //frame.setSize(500, 300);
	        this.setLocationRelativeTo(null);
	        this.setVisible(true);
	      
	}
	
	public void refresh(String path)
	{
		this.explore.refreshTree(path);
		
	}
   
 
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    	
    	
    	
    	javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
          Global.gui = new GUI();
          Global.gui.refresh(System.getProperty("user.dir"));
        }
        });
        
        
    }

}
