/*
 * Copyright (c) Ian F. Darwin, http://www.darwinsys.com/, 1996-2002.
 * All rights reserved. Software written by Ian F. Darwin and others.
 * $Id: LICENSE,v 1.8 2004/02/09 03:33:38 ian Exp $
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Java, the Duke mascot, and all variants of Sun's Java "steaming coffee
 * cup" logo are trademarks of Sun Microsystems. Sun's, and James Gosling's,
 * pioneering role in inventing and promulgating (and standardizing) the Java
 * language and environment is gratefully acknowledged.
 *
 * The pioneering role of Dennis Ritchie and Bjarne Stroustrup, of AT&T, for
 * inventing predecessor languages C and C++ is also gratefully acknowledged.
 */

package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * Display a file system in a JTree view
 */
@SuppressWarnings("serial")
public class FileTree extends JPanel implements Runnable {
  private JScrollPane scrollpane;
  private File root;
  private JTree tree;
  private String OS;
  
  public FileTree(File dir) {
	OS = System.getProperty("os.name").toLowerCase();
    root = dir;
    setLayout(new BorderLayout());
    scrollpane = new JScrollPane();
    add(scrollpane);
    //refresh();
    
    ExecutorService exec = Executors.newSingleThreadExecutor();//.newFixedThreadPool(1);
    exec.execute(this);
  }
  
  public void refresh() {
    tree = new JTree(addNodes(null, root));

    // Add a listener
    tree.addMouseListener(new MouseListener(){
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1){
				if(e.getClickCount() == 2){
					if(tree.getLastSelectedPathComponent() != null){
						SwagTreeNode node = (SwagTreeNode) tree.getLastSelectedPathComponent();
					    node.click();
					}
				}
			}
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
    });
    
    scrollpane.getViewport().removeAll();
    scrollpane.getViewport().add(tree);
  }
  
  /** Add nodes from under "dir" into curTop. Highly recursive. */
  SwagTreeNode addNodes(SwagTreeNode curTop, File dir) {
	  SwagTreeNode curDir = new SwagTreeNode(dir);
    if (curTop != null) // should only be null at root
      curTop.add(curDir);
    
    Vector<String> ol = new Vector<String>();
    String[] tmp = dir.list();
    for (int i = 0; i < tmp.length; i++)
      ol.addElement(tmp[i]);
    Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
    Vector<File> files = new Vector<File>();
    // Make two passes, one for Dirs and one for Files. This is #1.
    for (int i = 0; i < ol.size(); i++) {
      String thisObject = (String) ol.elementAt(i);
      File newDir = new File(dir, thisObject);

      if (newDir.isDirectory())
        addNodes(curDir, newDir);
      else
        files.addElement(newDir);
    }
    // Pass two: for files.
    for (int fnum = 0; fnum < files.size(); fnum++) {
      if(!((File)files.elementAt(fnum)).getName().contains(".json"))
        curDir.add((SwagTreeNode)new SwagTreeNode((File)files.elementAt(fnum)));
    }
    return curDir;
  }

  public Dimension getMinimumSize() {
    return new Dimension(200, 400);
  }

  public Dimension getPreferredSize() {
    return new Dimension(200, 400);
  }

  @Override
  public void run() {
	// TODO Auto-generated method stub
	refresh();
  }
}

