import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** JFrame pour modifier quelques options du jeu... */


class GameOptions extends JFrame implements ActionListener, ChangeListener {

	private Slider startingLevel;
	private JComboBox interactionFactor;
	private Slider startingRows;
	private Slider holesInRows;
	private Slider rounds;
	private JButton ok;
	private Tetris tetris;
	private static GameOptions frame;

	// Constructeur

	public GameOptions(Tetris t) {

		// Réglages de la fenetre
		super("Options du Jeu");
		setVisible(false);
		tetris = t;
		frame = this;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new FlowLayout());
		
		// On ajoute les composants
		contentPane.add(startingLevel = new Slider(5,1,9,0,"Level de départ"));
		contentPane.add(startingRows = new Slider(5,1,15,0,"Nombre de lignes au début"));
		contentPane.add(holesInRows = new Slider(3,1,Grid.COLS-1,1,"Trous dans les nouv. lignes"));
		contentPane.add(rounds = new Slider(3,1,10,1,"Nombre de manches"));
		
		String[] data = {"0 ligne ajoutée","1 ligne ajoutée","2 ligne ajoutée","3 ligne ajoutée","4 ligne ajoutée"};
		contentPane.add(interactionFactor = new JComboBox(data));
		
		interactionFactor.setToolTipText("Facteur d'intéraction entre les joueurs : combien de lignes apparaissent chez les adversaires lorsqu'un joueur en fait une.");
		interactionFactor.setActionCommand("interactionFactor");
		interactionFactor.addActionListener(this);

		contentPane.add(ok = new JButton("OK"));
		ok.setActionCommand("OK");
		ok.addActionListener(this);
	}

	/** Gestion des événements pour les JSlider. */
	public void stateChanged(ChangeEvent event)
	{
		Slider slider = (Slider)event.getSource();
		if (!slider.getValueIsAdjusting())
		{
			slider.refreshTitle();
			if (slider == startingLevel)
				Player.setStartingLevel(startingLevel.getValue());
			else if (slider == startingRows)
				Player.setStartingRows(startingRows.getValue());
			else if (slider == holesInRows)
				Grid.setHolesInRows(holesInRows.getValue());
			else if (slider == rounds)
				Player.setRounds(rounds.getValue());
		}
	}

	/** Gestion des evenements pour les comboBox et JButton. */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == "interactionFactor")
			Player.setInteractionFactor(interactionFactor.getSelectedIndex());
		else if (command == "OK") {
			setEnable(false);
		}
	}
	
	/** Afficher ou non cette JFrame. */
	public void setEnable(boolean b) {
		if(b)
			refresh();
		setSize(500,250);
		setVisible(b);
	}
	
	/** Mis à jour des parametres actuels. */
	public void refresh() {
		startingLevel.setMyValue(Player.getStartingLevel());
		startingRows.setMyValue(Player.getStartingRows());
		holesInRows.setMyValue(Grid.getHolesInRows());
		rounds.setMyValue(Player.getRounds());
		interactionFactor.setSelectedIndex(Player.getInteractionFactor());
	}

	// Accessors
	public static GameOptions getFrame() {return frame;}


	
	/** Classe interne pour gérer les JSlider. */
	class Slider extends JSlider
	{
		String mainTitle;
		TitledBorder border;
		
		public Slider(int majorTS, int minorTS, int maximum, int minimum, String title)
		{
			setValues(majorTS,minorTS,maximum,minimum);
			setBorder(border = new TitledBorder(title));
			addChangeListener(GameOptions.getFrame());
			mainTitle = title;
		}
		
		public void setValues(int majorTS, int minorTS, int maximum, int minimum)
		{
			setMajorTickSpacing(majorTS);
			setMinorTickSpacing(minorTS);
			setMaximum(maximum);
			setMinimum(minimum);
			setPaintTicks(true);
			setPaintLabels(true);
		}
	
		public void setMyValue(int i) {
			setValue(i);
			refreshTitle();
		}

		public void refreshTitle()
		{
				border.setTitle(mainTitle+" ("+getValue()+")");
		}
	}

}