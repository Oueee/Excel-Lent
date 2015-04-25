package gui;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;

public class BackgroundContainer extends Container {
	 private static final long serialVersionUID = 1;

	    private final Image img;
	    private final int width;
	    private final int height;

	    public BackgroundContainer(Image background)
	    {
	        img = background;
	        width = img.getWidth(this);
	        height = img.getHeight(this);
	    }

	    @Override
	    public void paint(Graphics g)
	    {
	        super.paint(g);
	        g.drawImage(img, 0, 0, width, height, this);
	        super.paintComponents(g);
	    }
}
