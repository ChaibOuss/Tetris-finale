import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

/** La barre de menu du jeu ! */

public class MenuBar extends JMenuBar implements ActionListener {

	protected static MenuBar menuBar;

	private Item onePlayer, twoPoints, twoBattle, stop, quit, game,
		score, help, apropos, load, save;
	private CheckBox pause, sound, music;
	private JRadioButtonMenuItem[][] radios;

	private Tetris tetris;
	
	

	// Constructeur
	public MenuBar(Tetris t) {

		tetris = t;
		menuBar = this;
		Menu menu, menu_, menu__;
		
		// MENU JEU
		add(menu = new Menu("Jeu",KeyEvent.VK_J));
		menu.add(onePlayer = new Item("Jeu Solo",KeyEvent.VK_S));
		menu.add(twoPoints = new Item("Dual Points",KeyEvent.VK_D));
		menu.add(twoBattle = new Item("Dual Battle",KeyEvent.VK_B));
		menu.add(new JSeparator());
		menu.add(pause = new CheckBox("Pause",KeyEvent.VK_P));
		menu.add(stop = new Item("Stopper la partie",KeyEvent.VK_T));
		menu.add(quit = new Item("Quitter le jeu",KeyEvent.VK_Q));
		
		// Pré-réglages indépendants de la configuration (voir refresh() pour les autres)
		pause.setSelected(false);
		pause.setEnabled(false);
		stop.setEnabled(false);

		
		// MENU OPTIONS
		// on forme des chaines de caracteres a partir des configurations clavier
		int[][] keyConf = Tetris.getKeyConf();
		String[] keyText = new String[keyConf.length];
		for(int i=0;i<keyConf.length;i++) {
			keyText[i] = " ";
			for(int j=0;j<keyConf[i].length;j++)
				keyText[i] += KeyEvent.getKeyText(keyConf[i][j])+" ";			
		}

		// on ajoute les sous-menus et on propose les configurations claviers aux joueurs
		add(menu = new Menu("Options",KeyEvent.VK_O));
		menu.add(menu_ = new Menu("Touches",KeyEvent.VK_T));
		radios = new JRadioButtonMenuItem[2][keyText.length];
		for(int k=0;k<2;k++) {
			menu_.add(menu__ = new Menu("Joueur "+(k+1),49+k));
			ButtonGroup group = new ButtonGroup();
			for(int l=0;l<keyText.length;l++) {
				group.add(radios[k][l] = new JRadioButtonMenuItem(keyText[l]));
				radios[k][l].setActionCommand("key"+k+l);
				radios[k][l].addActionListener(this);
				menu__.add(radios[k][l]);
			}
		}
		
		// le reste...
		menu.add(game = new Item("Jeu", KeyEvent.VK_J));
		menu.add(new JSeparator());
		menu.add(sound = new CheckBox("Sons",KeyEvent.VK_S));
		menu.add(music = new CheckBox("Musique",KeyEvent.VK_M));

		
		// MENU AUTRES
		add(menu = new Menu("Autres",KeyEvent.VK_A));
		menu.add(score = new Item("High Scores",KeyEvent.VK_H));
		menu.add(help = new Item("Aide",KeyEvent.VK_A));
		menu.add(apropos = new Item("A Propos...",KeyEvent.VK_P));
		menu.add(new JSeparator());
		menu.add(load = new Item("Restaurer la configuration par défaut",KeyEvent.VK_R));
		menu.add(save = new Item("Sauvegarder cette configuration",KeyEvent.VK_S));
		
		refresh();
	}

	/** Rafraichi les options du menu après un chargement de fichier configs. */
	public void refresh() {
		for(int k=0;k<radios.length;k++) {	//	Touches
			for(int l=0;l<radios[k].length;l++) {
				if(Tetris.getKeys()[k]==l)
					radios[k][l].setSelected(true);
			}
		}
		if (Sound.isEnable())	// Checkboxs
			sound.setSelected(true);
		else
			sound.setSelected(false);
		if (Music.isEnable()) {
			music.setSelected(true);
			Music.play();
		} else {
			music.setSelected(false);
			Music.stop();
		}
	}

	/** Permet d'activer ou non certaines options selon qu'une partie est en cours ou non. */
	public void isPlaying(boolean b) {
		pause.setEnabled(b);
		stop.setEnabled(b);
		onePlayer.setEnabled(!b);
		twoPoints.setEnabled(!b);
		twoBattle.setEnabled(!b);
		game.setEnabled(!b);
		save.setEnabled(!b);
		load.setEnabled(!b);
	}

	
	/** Evenements. */
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		//System.out.println("Menu : "+command);
		if (command == "item") {
			try {	// on a cliqué sur un menuItem
				Item item = (Item)e.getSource();
				if(item == onePlayer) {
					tetris.startGame(1,Player.POINTS);
					isPlaying(true);
				} else if (item == twoPoints) {
					tetris.startGame(2,Player.POINTS);
					isPlaying(true);
				} else if (item == twoBattle) {
					tetris.startGame(2,Player.BATTLE);
					isPlaying(true);
				} else if (item == stop) {
					if(0 == JOptionPane.showConfirmDialog(this,"Souhaitez-vous quitter la partie en cours ?","Demande de confirmation",JOptionPane.OK_OPTION)) {
						tetris.stopGame();
						isPlaying(false);
					}
				} else if (item == quit) {
					tetris.quit();
				} else if (item == game) {
					tetris.getGameOptions().setEnable(true);
				} else if (item == score) {
					HighScore.show(this);
				} else if (item == help) {
					Desktop desk = Desktop.getDesktop();
					try {
						desk.open(new File("Correction CI  - 2017.pdf"));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else if (item == apropos) {
					String str = "Ce jeu a été écrit par l'équipe 36-2CPI";
					JOptionPane.showMessageDialog(this,str,command,JOptionPane.PLAIN_MESSAGE);
				} else if(item == load) {
					if(0 == JOptionPane.showConfirmDialog(this,"Souhaitez-vous charger les parametres par defaut (ils sont contenus dans le fichier default.properties) ?","Demande de confirmation",JOptionPane.OK_OPTION)) {
						tetris.getProperties().load(tetris, Properties.DEFAULT_FILE);
						refresh();
					}
				} else if (item == save) {
						if(tetris.getProperties().store(tetris))
						{
							File f = new File("user.properties");
							f.setWritable(true);
							JOptionPane.showMessageDialog(this,"Vos parametres actuels ont ete ecrits dans le fichier user.properties.","Information",JOptionPane.PLAIN_MESSAGE);
							
						}	
						else
							JOptionPane.showMessageDialog(this,"Il y a eu un problème d'écriture dans le fichier user.properties.","Information",JOptionPane.PLAIN_MESSAGE);
				}
			} catch (ClassCastException exception) {}			
		} else if (command == "box") {
			try {	// on a clique sur un checkBox
				CheckBox box = (CheckBox)e.getSource();
				if (box == pause) {
					tetris.changePause();
				} else if (box == sound) {
					Sound.change();
				} else if (box == music) {
					Music.change();
				}
			} catch (ClassCastException exception) {}
		}	else	// il reste les configurations clavier
			for(int i=0;i<2;i++)
				for(int j=0;j<5;j++)
					if(command.equals("key"+i+j)) {
						Tetris.setKeys(i,j);
						save.setEnabled(true);
					}
	}


	//	Accessors
	public CheckBox getPause() {return pause;}
	
	
	/** Classe interne pour les MenuItem. */
	class Item extends JMenuItem {
		public Item(String s, String c, int i) {
			super(s,i);
			setActionCommand(c);
			addActionListener(MenuBar.menuBar);
		}
		public Item(String s, int i) {
			this(s,"item",i);
		}
	}

	/** Classe interne pour les Menus. */
	class Menu extends JMenu {
		public Menu(String s, int i) {
			super(s);
			setMnemonic(i);
		}
	}

	/** Classe interne pour les CheckBox*/
	class CheckBox extends JCheckBoxMenuItem {
		public CheckBox(String s, String c, int i) {
			super(s);
			setActionCommand(c);
			setMnemonic(i);
			addActionListener(MenuBar.menuBar);
		}
		public CheckBox(String s, int i) {
			this(s,"box",i);
		}
	}
	
}


