package gui;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class ImagePanel extends JPanel{
	   private Image img;
	   
	   public ImagePanel(Image image)
	   {
		   super();
		   this.img = image;
	   }
	   
	    public void paintComponent(Graphics g) {
	    	super.paintComponents(g);
	        g.drawImage(img, 0, 0, null);
	    }
}
