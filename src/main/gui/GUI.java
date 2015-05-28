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
import javax.swing.Box;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;
import java.awt.Component;

import core.ExcelLent;

public class GUI  extends JFrame{

	private Explorer explore;
	private static ImageIcon background;

	private JCheckBox v_cb;
	private JCheckBox p_cb;
	private JCheckBox e_cb;

	private JRadioButton fine;
	private JRadioButton massive;

	public GUI()
	{
		super("Excel-lent");
		 this.background = new ImageIcon("img/wallpaperadn.PNG");
	     this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JMenuBar menuBar = new JMenuBar();
			GraphicsEnvironment env =
	        GraphicsEnvironment.getLocalGraphicsEnvironment();
	    	//this.setExtendedState(this.getExtendedState() | Frame.MAXIMIZED_BOTH);

	        JLabel titre = new JLabel("Excel-Lent",JLabel.CENTER);
	        titre.setBorder(BorderFactory.createEmptyBorder(10, 10, 50,10));
	        Font font = new Font("Roboto", Font.BOLD, 28);
	        titre.setAlignmentX(CENTER_ALIGNMENT);
	        titre.setBackground(Color.white);
	        titre.setFont(font);
	        JLabel label = new JLabel("Select your choice");
	        label.setAlignmentX(CENTER_ALIGNMENT);
	        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 30,10));
	        label.setFont(new Font("Roboto", Font.BOLD, 15));
	        Global.lbl = label;
	        final JProgressBar progressBar = new JProgressBar(0, 100);
	        final JLabel progressText = new JLabel("Species done: 0/0");
	        
	        ProgressBarListener pbl = new ProgressBarListener(progressBar, progressText);
	        final ExcelLent excellent = new ExcelLent(pbl);
					FileTree tree = new FileTree(ExcelLent.tree_root);
					JButton button = new JButton();
					JButton b;

	        ImagePanel panel = new ImagePanel(this.background.getImage());
	        panel.repaint();
	        panel.setLayout(new BorderLayout());


	        button.setVerticalTextPosition(AbstractButton.CENTER);
	        button.setHorizontalTextPosition(AbstractButton.LEADING); //aka LEFT, for left-to-right locales
	        button.setMnemonic(KeyEvent.VK_ENTER);
	        button.setSize(100, 50);
	        button.setActionCommand("run");
	        button.setAlignmentX(LEFT_ALIGNMENT);
	        Global.btn_run = button;
	        GridLayout grid = new GridLayout(0, 1, 10, 10);

	        ImagePanel panelGrid = new ImagePanel(this.background.getImage());
	        BoxLayout boxl =  new BoxLayout(panelGrid, BoxLayout.PAGE_AXIS);

	        //panelGrid.setLayout(grid);
	        panelGrid.setLayout(boxl);
	        panelGrid.add(titre);
	        panelGrid.add(label);
	        panelGrid.add(button);
	        //panel.add(progressBar,BorderLayout.SOUTH);
	        //panel.add(panelGrid, BorderLayout.CENTER);

					explore =  new Explorer(ExcelLent.tree_root.listFiles());


					Box main = Box.createVerticalBox();
					Box choice = Box.createHorizontalBox();
					choice.setAlignmentX(Component.LEFT_ALIGNMENT);
						v_cb = new JCheckBox("Viruses");
						v_cb.setSelected(true);
						choice.add(v_cb);

						e_cb = new JCheckBox("Eukaryotes");
						e_cb.setSelected(true);
						choice.add(e_cb);

						p_cb = new JCheckBox("Prokaryotes");
						p_cb.setSelected(true);
						choice.add(p_cb);

						ButtonGroup group = new ButtonGroup();
							fine = new JRadioButton("fine");
							group.add(fine);

							massive = new JRadioButton("massive");
							fine.setSelected(true);
							group.add(massive);

						choice.add(Box.createHorizontalStrut(25));
						choice.add(fine);
						choice.add(massive);
						choice.add(Box.createHorizontalStrut(25));

					b = new JButton(new AbstractAction("Run") {
						public void actionPerformed(ActionEvent e) {
							excellent.setToDo(v_cb.isSelected(),
																e_cb.isSelected(),
																p_cb.isSelected(),
																fine.isSelected());
							new Thread(excellent).start();
							JButton button = (JButton)e.getSource();
							button.setEnabled(false);
							}
					});
					choice.add(b);
					choice.add(Box.createHorizontalStrut(10));
					b = new JButton(new AbstractAction("Refresh tree") {
						public void actionPerformed(ActionEvent e) {
							tree.refresh();
							}
					});
					choice.add(b);

					main.add(tree);
					main.add(choice);
					main.add(progressText);
					main.add(progressBar);

	        explore.setAutoscrolls(true);
	        //panel.add(explore, BorderLayout.WEST);
					this.getContentPane().add(main);
	       // this.setContentPane(main);
	        //this.setSize(1000,500);
	        //Display the window.
	        //this.pack();
	        //frame.setSize(500, 300);
					this.setSize(700, 500);
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

        }
        });


    }

}
