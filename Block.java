import java.awt.Graphics;

/**
 * Cette classe définit les blocs qui sont utilisés par les pièces du jeu
 * et la grille du jeu se sert de sa méthode static paint(Graphics g).
 * Les constantes X0 et Y0 sont l'abscisse et l'ordonnée du centre du
 * repère dans lequel on va les afficher. Il s'agit d'un repère traditionnel
 * dans lequel l'axe des y croit vers le haut, et non pas vers le bas comme
 * dans la classe Graphics de l'API. Les blocs ne s'occupent pas des couleurs,
 * celles-ci sont gérés par les classe Piece et Grid.
 */

class Block {
	
	// Champs de classe
	/** Taille du bloc en pixel. */
	public static final int SIZE = 25;
	/** Abscisse du centre du repère en pixel. */
	public static final int X0 = 10;
	/** Ordonnée du centre du repère en pixel. */
	public static final int Y0 = X0 + Grid.ROWS * SIZE;

	// Champs de variable
	/** Abscisse du bloc en nombre de colonnes. */
	private int x;
	/** Ordonnée du bloc en nombre de lignes. */
	private int y;

	// Constructeur
	public Block(int i, int j) {
		x = i;
		y = j;
	}

	// Méthodes
	/** Afficher un pseudo-bloc de coordonnées (x,y)dans le Graphics g. */
	public static void paint(Graphics g, int x, int y) {
			g.fillRect(X0+x*SIZE+2,Y0-(y+1)*SIZE+2,SIZE-2,SIZE-2);
		}

	/** Affiché un bloc dans le Graphics g. */
	public void paint(Graphics g) {paint(g,x,y);}

	/** Déplacer un bloc de horizontal colonnes et de vertical lignes. */
	public void move(int horizontal, int vertical) {
		x += horizontal;
		y += vertical;
	}

	
	// Accesors
	public int getX() {return x;}
	public int getY() {return y;}

	// Mutators
	public void setX(int i) {x = i;}
	public void setY(int i) {y = i;}
	
}

