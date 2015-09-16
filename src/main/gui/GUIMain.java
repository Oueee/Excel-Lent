package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import core.ExcelLent;

/**
 * Cette classe démarre le programme et son interface graphique.
 * <br>Il n'est donc pas étonnant d'y retrouver de nombreux éléments servant uniquement à l'interface graphique.
 * <br>J'essaie néanmoins de regrouper tout l'aspect graphique vers le bas du fichier autant que possible.
 * @author Alexandre Florentin
 */
@SuppressWarnings("serial")
public class GUIMain extends JFrame{
	public static void main(String[] args){
		new GUIMain().initialize();
	}
	
	/**
	 * Création de toute l'interface graphique.
	 * <br>On la laisse tout en bas, car elle est assez énorme.
	 */
	void initialize(){
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} 
		catch (Exception e) {
			// Si on arrive pas à charger le thème, au pire c'est pas grave.
			e.printStackTrace();
		}
		this.setTitle("BioInfo");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 650, 500);
		
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		this.setContentPane(contentPane);
		
		JPopupMenu menuContextuel = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("ACTION 1" );
		menuContextuel.add(menuItem);
		
		//DEBUT BARRE DU HAUT
		JMenuBar menuBar = new JMenuBar();
		contentPane.add(menuBar, BorderLayout.NORTH);
		
		JMenu mnFichier = new JMenu("Fichier");
		menuBar.add(mnFichier);
		
		JMenuItem mntmFermer = new JMenuItem("Fermer");
		mnFichier.add(mntmFermer);
		mntmFermer.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					System.exit(0);}});
		
		JMenu parametres = new JMenu("Paramètres");
		menuBar.add(parametres);
		
		JMenu mnArbre = new JMenu("Royaumes concernés");
		parametres.add(mnArbre);
		
		//Ajout des CheckBox
		final StayOpenCBItem e_cb = new StayOpenCBItem("Eukaryotes");
		e_cb.setSelected(true);
		mnArbre.add(e_cb);
		
		final StayOpenCBItem p_cb = new StayOpenCBItem("Prokaryotes");
		p_cb.setSelected(true);
		mnArbre.add(p_cb);
		
		final StayOpenCBItem v_cb = new StayOpenCBItem("Virus");
		v_cb.setSelected(true);
		mnArbre.add(v_cb);
		
		JMenu stats = new JMenu("Statistiques");
		final JRadioButton fine = new JRadioButton("Statistique fine");
		fine.setSelected(true);
		final JRadioButton massive = new JRadioButton("Statistique massive");

		ButtonGroup btnGrp = new ButtonGroup();
		btnGrp.add(fine);
		btnGrp.add(massive);
		parametres.addSeparator();
		
		stats.add(fine);
		stats.add(massive);
		parametres.add(stats);
		
		//FIN BARRE DU HAUT
		
		
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.CENTER);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		final FileTree tree = new FileTree(ExcelLent.tree_root);
		panel_2.add(tree, BorderLayout.CENTER);
		
		final JProgressBar progressBar = new JProgressBar(0, 100);
        final JLabel progressText = new JLabel("Species done: 0/0");
        
        ProgressBarListener pbl = new ProgressBarListener(progressBar, progressText);
        final ExcelLent excellent = new ExcelLent(pbl);
		
		JPanel panel = new JPanel();
		panel_2.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblRecherche = new JLabel("Recherche :");
		panel.add(lblRecherche, BorderLayout.WEST);
		
		final JTextField champRecherche = new JTextField();
		panel.add(champRecherche, BorderLayout.CENTER);
		champRecherche.setColumns(10);
		champRecherche.getDocument().addDocumentListener(new DocumentListener() {
		    private void updateData() {
		    	if(champRecherche.getText().length() > 4){
		    		//recherche
		    	}
		    }
		    @Override
		    public void changedUpdate(DocumentEvent e) {}
		 
		    @Override
		    public void insertUpdate(DocumentEvent e) {
		    	updateData();
		    }
		    @Override
		    public void removeUpdate(DocumentEvent e)  {
		        updateData();
		    }
		});
		
		JPanel panel_1 = new JPanel();
		panel_2.add(panel_1, BorderLayout.SOUTH);
		panel_1.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		panel_1.add(progressText);
		
		final JFrame frame = this;
		final JButton btnRun = new JButton("Run");
		final JButton btnRefresh = new JButton("Refresh Tree");
		
		panel_1.add(btnRun);
		btnRun.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				btnRun.setEnabled(false);
				btnRefresh.setEnabled(false);
				frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				excellent.setToDo(v_cb.isSelected(), e_cb.isSelected(),
						p_cb.isSelected(), fine.isSelected());
				new Thread(excellent).start();
				
				frame.setCursor(Cursor.getDefaultCursor());
				btnRun.setEnabled(true);
				btnRefresh.setEnabled(true);
			}});
		panel_1.add(btnRefresh);
		btnRefresh.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				//tree.refresh();
				tree.threadedRefresh();
			}});

		JButton btnQuitter = new JButton("Exit");
		panel_1.add(btnQuitter);
		btnQuitter.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}});
		
		contentPane.add(progressBar, BorderLayout.SOUTH);
		
		this.setVisible(true);
		//tree.refresh();
	}
}

/**
 * Cette classe, dont la source figure ci-dessous, sert é évider que le menu se ferme quand on coche une case.
 * <br>Par soucis d'honnéteté elle est laissée intacte et telle qu'elle est trouvée sur le site d'origine.
 * @author florentin
 * @see <a href="http://stackoverflow.com/questions/3759379/how-to-prevent-jpopupmenu-disappearing-when-checking-checkboxes-in-it">Origine du code</a>
 */
@SuppressWarnings("serial")
class StayOpenCBItem extends JCheckBoxMenuItem {
    private static MenuElement[] path;

    {
        getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (getModel().isArmed() && isShowing()) {
                    path = MenuSelectionManager.defaultManager().getSelectedPath();
                }
            }
        });
    }

    /**
     * StayOpenCBItem est une CheckBox qui ne ferme pas le menu lorsque l'on clique dessus.
     * @param text Label de la CheckBox
     */
    public StayOpenCBItem(String text) {
        super(text);
    }

    @Override
    public void doClick(int pressTime) {
        super.doClick(pressTime);
        MenuSelectionManager.defaultManager().setSelectedPath(path);
    }
}