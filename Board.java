

import java.awt.Graphics;
import java.util.ArrayList;
import java.awt.Color;

/**
 * Cette classe est très importante pour le jeu, c'est elle qui met en relation
 * la grille et la pièce. Avant de déplacer une pièce, elle va vérifier si la
 * nouvelle position est permise, lorsqu'une pièce touche le sol elle va ordonnée
 * la suppression des éventuelles lignes pleines...
 */

class Board {

	// Champs de classe
	/** La pièce est entrain de se poser sur le sol ou bien la grille. */
	public static final int LANDING = 0;
	/** Mauvaise position, mais rien de grave (on ne peut perdre qu'après un LANDING). */
	public static final int BAD = -1;
	/** Position OK. */
	public static final int GOOD = -2;
	
	// Champs de variable
	/** Grille du jeu. */
	private Grid grid;
	/** Pièce qui est entrain de jouer. */
	private Piece piece;
	/** Prochaine pièce à jouer. */
	private Piece nextPiece;
	/** Vaut true si la pièce est en chute libre. */
	private boolean dropping;
	
	// Constructeur	
	public Board() {
		grid = new Grid();
		changePieces();
		dropping = false;
	}
	
	// Méthodes
	/** Changement de la prochaine pièce. */
	private void changeNextPiece() {
		nextPiece = new Piece(Grid.COLS+4,Grid.ROWS-2);
	}
	/** Changement des pièces. */
	public void changePieces() {
		if(nextPiece==null)
			changeNextPiece();
		piece = nextPiece;
		piece.go(Grid.CENTER,Grid.ROWS+1);
		changeNextPiece();
	}
	
	/**
	 * Vérifie si le mouvement move est permis en appelant la méthode testPosition().
	 * Selon la réponse de cette dernière, la pièce sera déplacée, inchangée ou bien
	 * "manger" par la grille. Retourne un tableau de deux entiers, le premier étant
	 * le nombre lignes remplies/effacées et le deuxième étant la hauteur du drop.
	 * Il est interessant de remarque pourquoi nous avons choisi d'utiliser un ArrayList
	 * pour gérer la grille de jeu (classe Grid) : en utilisant un tableau 2D classique,
	 * on ne peut pas ajouter de pièces au haut de la grille (ArrayOutOfBounds Exception).
	 * Imaginons que notre grille soit presque rempli et que nous soyons sur le point de perdre,
	 * une pièce arrive et se pose sur la grille. Elle dépasse la hauteur de cette dernière.
	 * On a perdu ? Eh bien non ! Pas forcément, car il se peut que même en dépassant à priori,
	 * elle permette de remplir un ou deux lignes et ainsi de baisser le niveau de la grille.
	 * Même si on va surement bientot perdre, ce ne sera pas maintenant car elle arrive à se caser
	 * dans la grille grace à ces lignes qui viennent d'être enlevées (et la hauteur de la grille
	 * de diminuer d'autant de lignes). C'est important de gérer ce genre de cas et c'est pourquoi
	 * nous avons choisi d'utiliser un ArrayList à la place d'un tableau classique : il n'y a pas
	 * la contrainte du nombre maximum d'éléments à prendre en compte. Voici donc comment fonctionne
	 * l'algorithme de cette méthode : on vérifie la position, on l'autorise ou pas, en se posant
	 * éventuellement, on enleve les eventuels lignes remplies et on retourne leur nombre à
	 * Player.testAndGo() qui va se charger de vérifier si la taille de notre ArrayList dépasse
	 * Grid.ROWS. @return Un tableau contenant le nombre de lignes completées et la hauteur du drop. */
	public int[] testAndGo(int move) {
		int[] result = {0,0};
		if(move == Piece.DROP) {
			dropping=true;
			while(dropping) {
				result[0] = testAndGo(Piece.DOWN)[0];
				result[1]++;	// récursivité jusqu'a ce qu'on atteindre un LANDING
			}
			return result;
		}
		piece.move(move);
		switch(result[0] = testPosition(move)) { // vérifie si la position est permise
			case BAD:
				piece.move(-move);	// on rétablit les anciennes coordonnées
				break;
			case LANDING:
				dropping = false;
				piece.move(-move);	// on rétablit les anciennes coordonnées
				Block[] blocks = piece.getBlocks();
				int[] tab = new int[blocks.length];
				for(int i=0;i<blocks.length;i++) {
					int y = blocks[i].getY();
					grid.add(blocks[i].getX(),y,piece.getColor()); // on ajoute les blocs de la pièce à la grille
					tab[i]=y;
				}
				result[0] = grid.removeFilledRows(tab); // combien de lignes sont pleines
				changePieces();	// on charge une nouvelle pièce
				break;
	  }
	  return result;
	}

	/** Vérifie si une position est permise. @return GOOD, BAD ou LANDING. */
	private int testPosition(int movement) {
		ArrayList tab = grid.getRows();
		for(int i=0;i<piece.getBlocks().length;i++) {
			int x = piece.getBlocks()[i].getX();
			int y = piece.getBlocks()[i].getY();
			if (x < 0 || x >= Grid.COLS)	// il dépasse les limites horizontales
				return(BAD);
			if (y < 0)// Il dépasse le fond de la grille
				return(LANDING);
			else if (y < tab.size() && ((Color[])tab.get(y))[x] != null) // Il cogne un bloc de la grille
				if (movement == Piece.DOWN)
					return(LANDING);	// on touche le sol
				else					
					return(BAD);
		}
		return(GOOD);// bonne position
	}

	/** Affichage de la Board, c'est à dire de la grille et des pièces. */
	public void paint(Graphics g) {
		nextPiece.paint(g,true);
		piece.paint(g);
		grid.paint(g);
	}
	

	// Accessors
	public Grid getGrid() {return grid;}
		
}