

import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;

/**
 * Cette classe est tr�s importante pour le jeu, c'est elle qui met en relation
 * la grille et la pi�ce. Avant de d�placer une pi�ce, elle va v�rifier si la
 * nouvelle position est permise, lorsqu'une pi�ce touche le sol elle va ordonn�e
 * la suppression des �ventuelles lignes pleines...
 */

class Board {

	// Champs de classe
	/** La pi�ce est entrain de se poser sur le sol ou bien la grille. */
	public static final int LANDING = 0;
	/** Mauvaise position, mais rien de grave (on ne peut perdre qu'apr�s un LANDING). */
	public static final int BAD = -1;
	/** Position OK. */
	public static final int GOOD = -2;
	
	// Champs de variable
	/** Grille du jeu. */
	private Grid grid;
	/** Pi�ce qui est entrain de jouer. */
	private Piece piece;
	/** Prochaine pi�ce � jouer. */
	private Piece nextPiece;
	/** Vaut true si la pi�ce est en chute libre. */
	private boolean dropping;
	
	// Constructeur	
	public Board() {
		grid = new Grid();
		changePieces();
		dropping = false;
	}
	
	// M�thodes
	/** Changement de la prochaine pi�ce. */
	private void changeNextPiece() {
		nextPiece = new Piece(Grid.COLS+4,Grid.ROWS-2);
	}
	/** Changement des pi�ces. */
	public void changePieces() {
		if(nextPiece==null)
			changeNextPiece();
		piece = nextPiece;
		piece.go(Grid.CENTER,Grid.ROWS+1);
		changeNextPiece();
	}
	
	/**
	 * V�rifie si le mouvement move est permis en appelant la m�thode testPosition().
	 * Selon la r�ponse de cette derni�re, la pi�ce sera d�plac�e, inchang�e ou bien
	 * "manger" par la grille. Retourne un tableau de deux entiers, le premier �tant
	 * le nombre lignes remplies/effac�es et le deuxi�me �tant la hauteur du drop.
	 * Il est interessant de remarque pourquoi nous avons choisi d'utiliser un ArrayList
	 * pour g�rer la grille de jeu (classe Grid) : en utilisant un tableau 2D classique,
	 * on ne peut pas ajouter de pi�ces au haut de la grille (ArrayOutOfBounds Exception).
	 * Imaginons que notre grille soit presque rempli et que nous soyons sur le point de perdre,
	 * une pi�ce arrive et se pose sur la grille. Elle d�passe la hauteur de cette derni�re.
	 * On a perdu ? Eh bien non ! Pas forc�ment, car il se peut que m�me en d�passant � priori,
	 * elle permette de remplir un ou deux lignes et ainsi de baisser le niveau de la grille.
	 * M�me si on va surement bientot perdre, ce ne sera pas maintenant car elle arrive � se caser
	 * dans la grille grace � ces lignes qui viennent d'�tre enlev�es (et la hauteur de la grille
	 * de diminuer d'autant de lignes). C'est important de g�rer ce genre de cas et c'est pourquoi
	 * nous avons choisi d'utiliser un ArrayList � la place d'un tableau classique : il n'y a pas
	 * la contrainte du nombre maximum d'�l�ments � prendre en compte. Voici donc comment fonctionne
	 * l'algorithme de cette m�thode : on v�rifie la position, on l'autorise ou pas, en se posant
	 * �ventuellement, on enleve les eventuels lignes remplies et on retourne leur nombre �
	 * Player.testAndGo() qui va se charger de v�rifier si la taille de notre ArrayList d�passe
	 * Grid.ROWS. @return Un tableau contenant le nombre de lignes complet�es et la hauteur du drop. */
	public int[] testAndGo(int move) {
		int[] result = {0,0};
		if(move == Piece.DROP) {
			dropping=true;
			while(dropping) {
				result[0] = testAndGo(Piece.DOWN)[0];
				result[1]++;	// r�cursivit� jusqu'a ce qu'on atteindre un LANDING
			}
			return result;
		}
		piece.move(move);
		switch(result[0] = testPosition(move)) { // v�rifie si la position est permise
			case BAD:
				piece.move(-move);	// on r�tablit les anciennes coordonn�es
				break;
			case LANDING:
				dropping = false;
				piece.move(-move);	// on r�tablit les anciennes coordonn�es
				Block[] blocks = piece.getBlocks();
				int[] tab = new int[blocks.length];
				for(int i=0;i<blocks.length;i++) {
					int y = blocks[i].getY();
					grid.add(blocks[i].getX(),y,piece.getColor()); // on ajoute les blocs de la pi�ce � la grille
					tab[i]=y;
				}
				result[0] = grid.removeFilledRows(tab); // combien de lignes sont pleines
				changePieces();	// on charge une nouvelle pi�ce
				break;
	  }
	  return result;
	}

	/** V�rifie si une position est permise. @return GOOD, BAD ou LANDING. */
	private int testPosition(int movement) {
		ArrayList tab = grid.getRows();
		for(int i=0;i<piece.getBlocks().length;i++) {
			int x = piece.getBlocks()[i].getX();
			int y = piece.getBlocks()[i].getY();
			if (x < 0 || x >= Grid.COLS)	// il d�passe les limites horizontales
				return(BAD);
			if (y < 0)// Il d�passe le fond de la grille
				return(LANDING);
			else if (y < tab.size() && ((Color[])tab.get(y))[x] != null) // Il cogne un bloc de la grille
				if (movement == Piece.DOWN)
					return(LANDING);	// on touche le sol
				else					
					return(BAD);
		}
		return(GOOD);// bonne position
	}

	/** Affichage de la Board, c'est � dire de la grille et des pi�ces. */
	public void paint(Graphics g) {
		nextPiece.paint(g,true);
		piece.paint(g);
		grid.paint(g);
	}
	

	// Accessors
	public Grid getGrid() {return grid;}
		
}