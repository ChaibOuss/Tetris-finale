import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
/**
 * Cette classe gère un joueur de Tetris. Elle utilise une Board
 * et lui envoie des commandes, selon les résultats elles modifient
 * le score, le level etc... Remarquons le fait qu'elle possède un
 * Timer et c'est donc elle qui va gérer la vitesse du jeu.
 */

class Player extends JLabel implements ActionListener {
	
	// Champs de classe
	/**
	 * Le mode POINTS est le classique Tetris : on joue jusqu'a ce qu'on en ai marre.
	 * Tout seul comme à deux joueurs, le nombre de "rounds" est illimité et le but est
	 * de faire le maximum de points. */
	public static final int POINTS = 0;	// mode de jeu
	/**
	 * Il s'agit d'un mode qui ne fonctionne qu'à deux joueurs, il oppose deux adversaires
	 * dans le temps et ici, on ne s'occupe plus des points ou des lignes mais on essaie
	 * de durer plus longtemps que l'autre... et c'est tout ! Lorsqu'un joueur perd, l'autre
	 * remporte immédiatement le round (il n'a pas le temps de finir sa partie). La partie
	 * est remporté lorsqu'un joueur a gagné un certains nombre de rounds. @see rounds */
	public static final int BATTLE = 1; // mode de jeu

	/** Etat : une partie est en cours mais on attend que le joueur appuie sur drop pour lancer le round. */
	public static final int READY = 0;
	/** Etat : on est entrain de jouer. */
	public static final int PLAYING = 1;
	/** Etat : on a gagné le mode BATTLE. */
	public static final int WINNER = 2;
	/** Etat : on a perdu le mode BATTLE. */
	public static final int LOOSER = 3;
	
	/** Abscisse du point supérieur gauche du cadre "Prochaine Pièce". */
	public static final int NEXTX0 = 4 * Block.X0 + Grid.WIDTH ;
	/** Ordonnée du point supérieur gauche du cadre "Prochaine Pièce". */	
	public static final int NEXTY0 = Block.X0;
	/** Largeur de la zone "Prochaine Pièce". */
	public static final int NEXTWIDTH = 6 * Block.SIZE;
	/** Hauteur de la zone "Prochaine Pièce". */
	public static final int NEXTHEIGHT = 4 * Block.SIZE;

	/** Abscisse du point supérieur gauche du cadre "Infos". */
	public static final int INFOX0 = NEXTX0 - Block.X0;
	/** Ordonnée du point supérieur gauche du cadre "Infos". */	
	public static final int INFOY0 = Block.X0 + NEXTHEIGHT + Block.SIZE;
	/** Largeur de la zone "Infos". */
	public static final int INFOWIDTH = NEXTWIDTH;
	/** Hauteur de la zone "Infos". */
	public static final int INFOHEIGHT = 20;

	/** Largeur du JLabel. */
	public static final int WIDTH = Grid.WIDTH + INFOWIDTH + 5 * Block.X0;
	/** Hauteur du JLabel. */
	public static final int HEIGHT = Grid.HEIGHT + 2 * Block.X0;
	/** Espace entre chaque Players. */
	public static final int SPACING = 30;

	
	/**
	 * Tableau de 5 entiers. L'élément scores[0] contient le nombre de lignes à traverser
	 * pour faire 1 point lors d'un drop, et scores[i] pour i != 0 contient le nombre de
	 * points que l'on gagne à faire i lignes d'un seul coup. */
	private static int[] scores = new int[5];
	/** Couleur d'arrière-plan. */
	private static Color backGround;
	/** Couleur des informations standardes (avant-plan). */
	private static Color foreGround;
	/** Couleur des messages spéciaux comme "APPUYER SUR DROP" ou bien "PAUSE". */
	private static Color specialGround;
	/** Nombre de rounds pour gagner le mode BATTLE. */
	private static int rounds;
	/** Level de départ. */
	private static int startingLevel;
	/** Nombre de lignes avec lesquelles on commence un round. */
	private static int startingRows;
	/** Facteur d'interaction entre les joueurs. Chaque fois qu'un joueur fera une ligne,
	 * son adversaire (aussi bien en mode BATTLE qu'en mode POINTS) se verra ajouter d'autant
	 * de lignes multipliées par interactionFactor. Attention : ca devient vite très dur ! */
	private static int interactionFactor;

	// Champs de variable
	/** On se servira de cette référence pour appeler des méthode non statique de Tetris. */
	Tetris tetris;	
	/** La Board du Player. */
	private Board board;
	/** Etat du jeu. */
	private int state;
	/** Mode de jeu. */
	private int mode;
	/** Score cumulé sur tous les rounds. */
	private Info totalScore;
	/** Lignes cumulées sur tous les rounds. */
	private Info totalLines;
	/** Nombre de rounds déjà gagnés (BATTLE) ou déjà joués (POINTS). */
	private Info round;
	/** Score du round actuel. */
	private Info score;
	/** Lignes du round actuel. */
	private Info lines;
	/** Lignes qu'on va se manger à la prochaine pièce qui tombe sans faire de lignes. */
	private Info waitingRows;
	/** Level actuel. */
	private Info level;
	/** Message spéciaux à afficher. */
	private Message message;
	/** Timer. */
	private Timer timer;
	
	private Font police= new Font("Berlin Sans FB Demi",Font.ITALIC,15 );
	// Constructeur
	public Player(int m, int n, Tetris t) {
		tetris = t;
		mode = m;
		setBounds(n*WIDTH+(n+1)*SPACING,SPACING,WIDTH,HEIGHT);
		add(level = new Info("Niveau",1));
		level.setFont(police);
		level.setForeground(Color.WHITE);
		add(score = new Info("Score",3));
		score.setFont(police);
		score.setForeground(Color.WHITE);
		add(lines = new Info("Lignes",5));
		lines.setFont(police);
		lines.setForeground(Color.WHITE);
		add(waitingRows = new Info("A venir",7));
		waitingRows.setFont(police);
		waitingRows.setForeground(Color.WHITE);
		add(round = new Info((mode==BATTLE)?"Victoire(s)":"Parties Perdues",9));
		round.setFont(police);
		round.setForeground(Color.WHITE);
		
		add(totalScore = new Info("Total Score",11));
		totalScore.setFont(police);
		totalScore.setForeground(Color.WHITE);
		add(totalLines = new Info("Total Lines",13));
		totalLines.setFont(police);
		totalLines.setForeground(Color.WHITE);
		if(mode==POINTS)
			waitingRows.setVisible(false);
		round.set(0);
		totalScore.set(0);
		totalLines.set(0);
		add(message = new Message());
		message.setFont(police);
		message.setForeground(Color.WHITE);
		setState(READY);
		timer = new Timer(1000,this);
	}
	
	/** Règle le délai du Timer en fonction du level courant. */
	public void setDelay() {
		if (level.value<10)
			timer.setDelay(1000-level.value*100);
		else if (level.value<20)
			timer.setDelay(100-level.value*10);
	}
	
	/** Teste un mouvement et réagit en conséquence (rafraichissement des informations,
	 * signalement de la fin du round... Cette méthode se contente d'interfacer l'éponyme
	 * de la classe Board avec ses propres champs. @see Board.testAndGo(int move */
	public void testAndGo(int movement) {
		if(!tetris.isPaused()) {
			if (state == PLAYING) {
				int[] result = board.testAndGo(movement);
				refreshInfos(result); // rafraichit les infos
				if (result[0] == Board.LANDING && waitingRows.value>0) {
					board.getGrid().addRows(waitingRows.value);
					waitingRows.set(0);	// ajoute les lignes en attente
				}
				if (board.getGrid().getRows().size() > Grid.ROWS) {
					Sound.play("gameover");
					stopRound(); // round over si on dépasse la hauteur de la grille
				}
			} else if(state == READY && movement == Piece.DROP)
				startRound();
			repaint();
		}
	}

	/** Nouveau round. */
	public void startRound() {
		if (state == READY) {
			board = new Board();
			score.set(0);
			lines.set(0);
			waitingRows.set(startingRows);
			level.set(startingLevel);
			setState(PLAYING);
			setDelay();
			timer.start();
			if (mode==BATTLE && getOther().getState() == READY)
				getOther().startRound();
	   }
   }
	
	/** Stop round. */
	public void stopRound() {
		timer.stop(); // Très important d'arreter le timer !
		board = null;
		if(state == PLAYING) {
			if(mode == POINTS || getOther().getState()==READY)
				round.add(1);
			if(mode == BATTLE)
				if (round.value >= rounds) {
					setState(WINNER);
					getOther().setState(LOOSER);
				} else {
					setState(READY);
					if (getOther().getState() == PLAYING)
						getOther().stopRound();
				}
			else
				setState(READY);
			if(HighScore.checkItOut(tetris,getNumber(),score.value))
				HighScore.show(tetris); // Vérification High Scores et éventuelle mise en pause du jeu (pour ne pas gener l'autre joueur)
			tetris.setPause(false); // Enleve la pause mise au cas ou on etait bon pour le High Scores
		}
	}
	
	/** Refresh infos*/
	public void refreshInfos(int[] result) {
		int s=0;
		if(result[0] > 0) {
			Sound.play(""+result[0]);
			lines.add(result[0]);
			totalLines.add(result[0]);
			s += scores[result[0]];
			if(tetris.getPlayers().length > 1)
				getOther().waitingRows.add(result[0]*interactionFactor);
		}
		s += result[1] / scores[0];
		score.add(s);
		totalScore.add(s);
		while (score.value > (level.value+1)*1000) {
			level.add(1);
			setDelay();
		}			
	}
	
	/** Modifier l'état de la partie et afficher en conséquence. */
	public void setState(int i) {
		state = i;
		switch(i) {
			case PLAYING :
				message.setText("");
				break;
			case READY :
				message.setForeground(Color.RED);
				message.setText("APPUYEZ SUR DROP");
				break;
			case WINNER :
				message.setText("WINNER");
				break;
			case LOOSER :
				message.setText("LOOSER");
		}
	}

	/** Pas besoin d'effacer ou de calculer ce qui est à rafraichir, swing le fait tout seul !? */
	public void paintComponent(Graphics g) {
		g.setColor(Color.black);
		
		
		g.fillRoundRect(NEXTX0-Block.X0,NEXTY0-Block.X0,NEXTWIDTH+2*Block.X0,NEXTHEIGHT+2*Block.X0,Block.X0,Block.X0);
		g.fillRoundRect(0,0,Grid.WIDTH+2*Block.X0,Grid.HEIGHT+2*Block.X0,Block.X0,Block.X0);	
		if(board!=null)
			board.paint(g);
		for(int i=1;i<10;i++)
		{
			g.setColor(Color.DARK_GRAY);
			g.drawLine(9+i*25, 0,9+i*25 ,1000);
			g.drawLine(10+i*25, 0,10+i*25 ,1000);
			g.drawLine(11+i*25, 0,11+i*25 ,1000);	
		}
		for(int i=1;i<22;i++)
		{
			g.setColor(Color.DARK_GRAY);
			g.drawLine(0, 9+i*25,266 ,9+i*25);
			g.drawLine(0, 10+i*25,266 ,10+i*25);
			g.drawLine(0, 11+i*25,266 ,11+i*25);			
		}
		g.setColor(Color.LIGHT_GRAY);
		for(int i=0;i<12;i++)
			g.drawLine(i, 0,i ,1000);
		for(int i=0;i<12;i++)
			g.drawLine(i+260, 0,i+260 ,1000);
		for(int i=0;i<12;i++)
			g.drawLine(0, i,266 ,i);
		for(int i=0;i<12;i++)
			g.drawLine(0, i+560,266 ,i+560);
	}


	/** Lorsque le Timer frappe on descend vers le bas. */
	public void actionPerformed(ActionEvent arg0) {
		testAndGo(Piece.DOWN);
	}


	// Accessors
	
	public int getState() {return state;}
	public static Color getForeGround() {return foreGround;}
	public static Color getSpecialGround() {return specialGround;}
	public static Color getBackGround() {return backGround;}
	public static int getStartingLevel() {return startingLevel;}
	public static int getInteractionFactor() {return interactionFactor;}
	public static int getStartingRows() {return startingRows;}
	public int getMode() {return mode;}
	public static int getRounds() {return rounds;}
	public static int getScores(int i) {return scores[i];}
	private int getNumber() {return (tetris.getPlayers()[0] == this)?0:1;}
	/** Retourne l'autre instance de Player si on joue à deux. */
	private Player getOther() {
		Player[] players = tetris.getPlayers();
		if (players.length > 1)
			return (this == players[0])?players[1]:players[0];
		else
			return this;
	}
	
	// Mutators
	
	public static void setScores(int i, int j) {scores[i] = j;}
	public static void setBackGround(Color color) {backGround = color;}
	public static void setForeGround(Color color) {foreGround = color;}
	public static void setSpecialGround(Color color) {specialGround = color;}
	public static void setRounds(int i) {rounds = i;}
	public static void setStartingLevel(int i) {startingLevel = i;}
	public static void setStartingRows(int i) {startingRows = i;}
	public static void setInteractionFactor(int i) {interactionFactor = i;}
	public void setWaitingLines(Info info) {waitingRows = info;}
	


	/** Classe interne permettant de manipuler facilement les informations du jeu. */
	class Info extends JLabel {
		private String text;
		private int value;
		public Info(String s, int a, int b, int c, int d) {
			setForeground(foreGround);
			setBounds(a,b,c,d);
			text = s;
			set(0);
		}
		public Info(String s, int i) {this(s,INFOX0,INFOY0+i*Block.SIZE,NEXTWIDTH,20);}
		public void set(int j) {
			value = j;
			setText(text+" : "+value);
		}
		public void add(int j) {set(value+j);}
	}
	
	/** Classe interne permettant d'afficher des messages spéciaux. */
	class Message extends JLabel {
		public Message(int a, int b, int c, int d) {
			setForeground(specialGround);
			setHorizontalAlignment(CENTER);
			setBounds(a,b,c,d);
		}
		public Message() {this(0,0,Grid.WIDTH,Grid.HEIGHT);}
	}


}