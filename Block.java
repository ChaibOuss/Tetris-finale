import java.awt.Graphics;

/**
 * Cette classe d�finit les blocs qui sont utilis�s par les pi�ces du jeu
 * et la grille du jeu se sert de sa m�thode static paint(Graphics g).
 * Les constantes X0 et Y0 sont l'abscisse et l'ordonn�e du centre du
 * rep�re dans lequel on va les afficher. Il s'agit d'un rep�re traditionnel
 * dans lequel l'axe des y croit vers le haut, et non pas vers le bas comme
 * dans la classe Graphics de l'API. Les blocs ne s'occupent pas des couleurs,
 * celles-ci sont g�r�s par les classe Piece et Grid.
 */

class Block {
	
	// Champs de classe
	/** Taille du bloc en pixel. */
	public static final int SIZE = 25;
	/** Abscisse du centre du rep�re en pixel. */
	public static final int X0 = 10;
	/** Ordonn�e du centre du rep�re en pixel. */
	public static final int Y0 = X0 + Grid.ROWS * SIZE;

	// Champs de variable
	/** Abscisse du bloc en nombre de colonnes. */
	private int x;
	/** Ordonn�e du bloc en nombre de lignes. */
	private int y;

	// Constructeur
	public Block(int i, int j) {
		x = i;
		y = j;
	}

	// M�thodes
	/** Afficher un pseudo-bloc de coordonn�es (x,y)dans le Graphics g. */
	public static void paint(Graphics g, int x, int y) {
			g.fillRect(X0+x*SIZE+2,Y0-(y+1)*SIZE+2,SIZE-2,SIZE-2);
		}

	/** Affich� un bloc dans le Graphics g. */
	public void paint(Graphics g) {paint(g,x,y);}

	/** D�placer un bloc de horizontal colonnes et de vertical lignes. */
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

