import java.awt.Color;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Font;

/**
 * Voici la classe principale du jeu. C'est une JFrame auquel on ajoute un MenuBar
 * et on lui met deux JLabel, un pour afficher le jeu (et qui contient aussi l'image
 * de fond) et un pour afficher des messages divers et informatifs. Avant toutes chose,
 * le constructeur de cette classe va lire le fichier user.properties et charger ses
 * parametres en memoire. Nous avons ainsi le moyen de controler facilement, en plus
 * des quelques options deja rendues possibles par le menu, l'ensemble des parametres
 * generaux de jeu par le moyen d'un fichier editable "à la main".
 */

public class Tetris extends JFrame implements KeyListener, WindowListener {

	// Champs de classe
	/** Configurations pré-établis des touches, qui seront chargées par le fichier Properties. */
	private static int[][] keyConf = new int[5][5];
	/** keys[0] est l'indice de la configuration choisie par le joueur 1, key[1] pour le joueur 2... */
		private static int[] keys = new int[2];
		
	// Champs de variable
	/** Propriétés configurables. */
	private Properties properties;
	/** Barre de menu; */
	private MenuBar menuBar;
	/** Un JFrame qui sera masqué dès le début et affichable par le menu entre deux parties. */
	private GameOptions gameOptions;
	private Container contentPane = getContentPane();
	private JLabel messageLabel = new JLabel();
	private JLabel gameLabel = new JLabel();
	/** Touche pause. */
	private int pauseKey;
	private boolean pause;
	/** Ce tableau sera différent de null pendant une partie, et contiendra autant d'éléments que de joueurs (1 ou 2). */
	private Player[] players;
   
	Font police = new Font("Berlin Sans FB Demi",Font.HANGING_BASELINE,25 );
	Font polic= new Font("Berlin Sans FB Demi",Font.ITALIC,20 );
	// Constructeur
	public Tetris() {
				
		// chargement du fichier de configuration
		properties = new Properties(this);

		gameOptions = new GameOptions(this);

		// Reglages de la fenetre
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("   Tetris");
		setResizable(false);
		contentPane.setLayout(null);

		// ajout de la barre de menu
		setJMenuBar(menuBar = new MenuBar(this));
		
		// Ajoute le panel de message
		contentPane.add(messageLabel);
		showIntro();
		
		// Ajoute le panel du jeu
		contentPane.add(gameLabel);
		
		// Affiche l'image en arriere plan
		gameLabel.setIcon(new ImageIcon("image.jpg"));

		addKeyListener(this);
		requestFocus();
		resize();
		Sound.play("welcome");
	}

	/** Lancer une partie. @param n Nombre de joueurs @param mode Mode de jeu. @see Player.POINTS @seePlayer.BATTLE */
	public void startGame(int n, int mode) {
		stopGame();
		Sound.play("startgame");
		Piece.setModels();	// ne pas oublier ceci pour savoir quelles pieces on va utilser
		players = new Player[n];
		for(int i=0;i<n;i++)
			gameLabel.add(players[i] = new Player(mode,i,this));
		setPause(false);
		resize(); // qui a été rédéfini et s'occupe de tout redimensionner
	}

	/** Arrete une partie lancée. */
	public void stopGame() {
		if(players != null) {
			for(int i=0;i<players.length;i++)
				players[i].stopRound();
			gameLabel.removeAll();
			players = null;
			Sound.play("goodbye");
		}
		showIntro();
		resize();
	}
	
	/** Message de bienvenue. */
	public void showIntro()  {
		messageLabel.setVerticalAlignment(JLabel.CENTER);
		messageLabel.setHorizontalAlignment(JLabel.CENTER);
		messageLabel.setForeground(Color.WHITE);
		messageLabel.setFont(police);
		messageLabel.setText("Utilser le menu pour lancer une partie");
	}
	
	/** Gestion du clavier, ces méthodes qui va recevoir toutes les frappes
	 * et les transmettre s'il le faut au Players. */
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (players != null)
			if(keyCode == pauseKey)
				changePause();
			else
				if(!pause)
					for(int i=0;i<players.length;i++)
						for(int j=0;j<keyConf[keys[i]].length;j++)
							if(keyCode == keyConf[keys[i]][j]) 
								players[i].testAndGo(j+1);
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}

	// Le main qui n'a pour seul but que de lancer une instance de la classe
	public static void main(String[] args){Tetris tetris = new Tetris();}
	
	public void windowActivated(WindowEvent arg0) {}
	public void windowClosing(WindowEvent arg0) {quit();}
	public void windowDeactivated(WindowEvent arg0) {}
	public void windowDeiconified(WindowEvent arg0) {}
	public void windowIconified(WindowEvent arg0) {setPause(true);}
	public void windowOpened(WindowEvent arg0) {}
	public void windowClosed(WindowEvent arg0) {}
	public void quit() {
		if(0 == JOptionPane.showConfirmDialog(this,"Souhaitez-vous quitter le jeu ?","Demande de confirmation",JOptionPane.OK_OPTION)) {
			dispose();
			System.exit(0);
		}
	}

	public void resize() {
		int width, height, n;
		n = (players == null)?1:players.length;
		width = Player.WIDTH*n + (n+1)*Player.SPACING;
		height = Player.HEIGHT + 2*Player.SPACING;
		setSize(width,height+Player.SPACING);
		messageLabel.setBounds(0,0,width,height);
		gameLabel.setBounds(0,0,width,height);
		setVisible(true);
		//pack();
	}

	// Accessors
	public static int[][] getKeyConf() {return keyConf;}
	public static int[] getKeys() {return keys;}
	public static int getKeys(int i) {return keys[i];}
	public GameOptions getGameOptions() {return gameOptions;}
	public int getPauseKey() {return pauseKey;}
	public Properties getProperties() {return properties;}
	public Player[] getPlayers() {return players;}
	public boolean isPaused() {return pause;}

	// Mutators
	public static void setKeyConf(int i, int j, int k) {keyConf[i][j] = k;}
	public static void setKeys(int[] i) {keys = i;}
	public static void setKeys(int i, int j) {keys[i] = j;}
	public  void setPauseKey(int i) {pauseKey = i;}
	/** Met le jeu en pause ou non selon la valeur du boolean b. */
	public void setPause(boolean b) {
		if(players != null) {
			pause = b;
			menuBar.getPause().setSelected(b);
			messageLabel.setHorizontalAlignment(JLabel.CENTER);
			messageLabel.setVerticalAlignment(JLabel.TOP);
			messageLabel.setForeground((pause)?Player.getSpecialGround():Player.getForeGround());
			messageLabel.setFont(polic);
			messageLabel.setForeground(Color.WHITE);
			messageLabel.setText((pause)?"PAUSE":(players[0].getMode()==Player.POINTS)?"Autaunt de rounds pour un maximum de points":"Un vrai match en "+Player.getRounds()+" Manches gagnantes");
		}
	}
	public void changePause() {setPause(!pause);	}

}
