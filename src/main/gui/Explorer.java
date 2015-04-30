package gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.*;
import javax.swing.tree.*;

public class Explorer extends JPanel
{
	private File[] 	_roots;
	private JTree		_dirs;

	public Explorer()
	{
		//on recupère les lecteurs
		//_roots = File.listRoots();
		//generation();
	}

	public Explorer( File[] debut)
	{
		//on recupère les lecteurs
		_roots = debut;

		generation();
	}
	public void refreshTree(String path)
	{
		File rep = new File(path);
		this._roots = rep.listFiles();
		this.removeAll();
		this.generation();
		 for (int row = 0; row < _dirs.getRowCount() ; row++) {
		      _dirs.expandRow(row);
		    }

	}
	private void generation()
	{
		// taille de la fenetre
		//setSize(500,500);


		// on définit notre premier noeud
		DefaultMutableTreeNode racine = new DefaultMutableTreeNode("Tout",true);

		// Création du jtree
		_dirs = new JTree(racine);
		//_dirs.setSize(700,200);
		_dirs.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent me) {
				// TODO Auto-generated method stub
				;
				/*
				 TreePath tp = _dirs.getPathForLocation(me.getX(), me.getY());
				System.out.println(tp.getLastPathComponent());
				Global.choix = tp.getLastPathComponent().toString();
				Global.btn_run.setEnabled(true);
				Global.lbl.setText("Click run for DL :"+tp.getLastPathComponent());
				*/
			}
		});
		// pour chaque lecteur
		for (int i = 0 ; i<_roots.length ; i++)
		{
			// on recupère son contenu grace a getSubDirs
			DefaultMutableTreeNode root = getSubDirs(_roots[i]);

			// et on l ajoute a notre premier noeud
			racine.add(root);

		}

		// on met le jtree dans un jscrollpane
		JScrollPane scroll = new JScrollPane(_dirs);
		scroll.setPreferredSize(new Dimension(600,600));
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(_dirs);
		// on ajoute notre jscrollpane
		//getContentPane().add(scroll);
	}
	/**

	  Méthode récursive permettant de récupérer

 tous les fichiers et sous dossiers d un autre

 @param root un File qui représente le lecteur ou le repertoire de départ

 @return DefaultMutableTreeNode

*/

public DefaultMutableTreeNode getSubDirs(File root)
	{
		// on créé un noeud
		DefaultMutableTreeNode racine = new DefaultMutableTreeNode(root,true);

		// on recupère la liste des fichiers et sous rep
		File[] list = root.listFiles();

		if ( list != null)
		{
			// pour chaque sous rep on appel cette methode => recursivité
			for (int j = 1 ; j<list.length ; j++)
			{
				DefaultMutableTreeNode file = null;
				if (list[j].isDirectory())
				{

					file = getSubDirs(list[j]);
					racine.add(file);
				}
			}
		}
		return racine;
	}


}
