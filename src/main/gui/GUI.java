package gui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.FutureTask;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.UnsupportedLookAndFeelException;

import core.ExcelLent;

public class GUI {
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Excel-Lent");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        JLabel label = new JLabel("DO NOT press the button below", JLabel.CENTER);
        
        final JProgressBar progressBar = new JProgressBar(0, 100);
        final ExcelLent excellent = new ExcelLent(new ProgressBarListener(progressBar));

        
		JButton button = new JButton(new AbstractAction("Run") {
			public void actionPerformed(ActionEvent e) {
				new Thread(excellent).start();
				System.out.println("Executing excellent");
		    }
		});
		
        button.setVerticalTextPosition(AbstractButton.CENTER);
        button.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
        button.setMnemonic(KeyEvent.VK_ENTER);
        button.setActionCommand("run");
  
        GridLayout grid = new GridLayout(0, 1, 10, 10);
        
        Container c = frame.getContentPane();

        c.setLayout(grid);
        
        c.add(label);
        c.add(button);
        c.add(progressBar);
        
        
        //Display the window.
        frame.pack();
        //frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
 
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    	
    	
    	
    	javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
