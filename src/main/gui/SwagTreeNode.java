package gui;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.awt.Desktop;
import java.io.IOException;

import util.Log;

public class SwagTreeNode extends DefaultMutableTreeNode {
  private File path;

  public SwagTreeNode(File path) {
    super(path.getName());
    this.path = path;
  }

  public void click() {
    try{Desktop.getDesktop().open(path);}
    catch(IOException e) {Log.e(e);}
  }
}
