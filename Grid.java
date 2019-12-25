import java.awt.Graphics;
import java.awt.Color;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Cette classe définie la grille du jeu, celle qui contient les blocs qu'on a
 * déjà descendu (les blocs de la pièce qui est entrain de jouer sont gérés par la
 * classe Piece). Il s'agit d'un "tableau" bidimensionnel de Color, et non pas Block.
 * En effet, le numéro de lignes et de colonnes du bloc sont déjà donné par les indices
 * i et j du tableau, il ne sert à rien de les répéter dans les champs x et y d'un Block.
 */

class Grid {

	// Champs de classe
	/** Nombre de colonnes. */
	public static final int COLS = 10;
	/** Nombre de lignes. */
	public static final int ROWS = 22;
	/** Centre de la grille, non pas en pixels mais en colonnes. */
	public static final int CENTER = COLS / 2;
	/** Largeur de la grille en pixels. */
	public static final int WIDTH = COLS * Block.SIZE;
	/** Hauteur de la grille en pixels. */
	public static final int HEIGHT = ROWS * Block.SIZE;
	/** Nombre de trous dans les nouvelles lignes. */
	private static int holesInRows;	
	
	// Champs de variable
	/**
	 * Voici un ArrayList qui va contenir des tableaux mono-dimensionnels de Color,
	 * lesquels tableaux seront de longueur COLS. Autrement dit, c'est un tableau
	 * de lignes. Pourquoi avoir choisi d'utiliser un ArrayList plutot qu'un simple
	 * tableau bi-dimensionnel "traditionnel" ? Nous verrons ça dans la classe Board,
	 * sachez seulement qu'on crée rows de contenance maximale ROWS+5 afin de gérer
	 * correctement la fin du jeu. @see Board.testAndGo(int move) */
	private ArrayList rows = new ArrayList(ROWS+5);


	//	Constructeur
	public Grid() {}


	// Méthodes
	/** Ajoute un "bloc" de couleur c à la position (x,y). */
	public void add(int x, int y, Color c) {
		while(rows.size() <= y) // créer les lignes si elles n'existent pas encore
			rows.add(new Color[COLS]);
		((Color[])rows.get(y))[x] = c;
	}

	/** Enleve la ligne i. */
	void removeRow(int i) {
		try {
			rows.remove(i);
		} catch (IndexOutOfBoundsException e) {}
	}

  /** Vérifie si la ligne j est pleine de blocs. @return true si la ligne est pleine. */
  public boolean isRowFilled(int j) {
		try {
			Color[] row = (Color[])rows.get(j);
			int i=0;
			while (i<row.length) {
				if(row[i] == null)
					return(false);
				i++;
			}
			return(true);
		} catch(IndexOutOfBoundsException e) {return false;}
	}

	/**
	 * Pour chaque élément de tab, vérifie si la ligne tab[i] est remplie et l'efface si besoin est.
	 * @param tab Tableau d'entiers représentant des numéros de lignes.
	 * @return Le nombre de lignes effacées. */ 
	public int removeFilledRows(int[] tab) {
		Arrays.sort(tab);	// on trie dabord le tableau par ordre croissant
		int i = 0, length = tab.length;
		while (i<length-1) {	// on enleve les doublons et les éléments qui représentent
			if (tab[i]==tab[i+1] || !isRowFilled(tab[i])) {	// des lignes qui ne sont pas pleines
				for(int j=i;j<length-1;j++) 
					tab[j] = tab[j+1];
				length--;	// ne pas oublier de "modifier" la taille du tableau
			} else
				i++;
		}
		if (!isRowFilled(tab[length-1]))	// le dernier élément du tableau (non traité ci-dessus)
			length--;
		for(i=length-1;i>=0;i--)
			removeRow(tab[i]);	// on efface enfin les lignes pleines
		return(length);	// on retourne le nombre de lignes effacées
	}

	/** Ajoute par le bas n lignes contenant holesInRows trous. */
	public void addRows(int n) {
		int col,holes;		
		for(int i=0;i<n;i++) {
			Color[] row = new Color[COLS]; //	on crée une ligne
			for(int j=0;j<COLS;j++)	// on la remplie de couleurs
				row[j] = Piece.getRandomUsedColor();
			holes = holesInRows;
			while (holes > 0) {	// tant qu'on a pas fait nos holesInRows trous
				col = (int)(Math.random()*COLS);	// on tire au hasard une colonne
				if (row[col] != null) {	// on enleve le bloc s'il y en avait un sinon on recommence
					row[col] = null;
					holes--;
				}
			}
			rows.add(0,row);	// on ajoute enfin la ligne à la grille
		}	// fermeture du for
	}

	/** Affiche les lignes d'indices supérieurs ou égaux à start dans le Graphics g. */
	public void paint(Graphics g, int start) {
		for(int j=start;j<rows.size();j++) {
			Color[] row = (Color[]) rows.get(j);
			for(int i=0;i<COLS;i++)
				if(row[i] != null) {
					g.setColor(row[i]);
					Block.paint(g,i,j);
				}
		}				
	}

	/**	Affiche toute la grille. */
	public void paint(Graphics g) {paint(g,0);}


	// Accessors
	public ArrayList getRows() {return rows;}
	public static int getHolesInRows() {return holesInRows;}

	// Mutators
	public static void setHolesInRows(int i) {holesInRows = i;}

}