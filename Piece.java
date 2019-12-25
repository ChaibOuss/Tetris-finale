import java.awt.Graphics;
import java.awt.Color;

/**
 * Une pièce du jeu est composée d'un certain nombre de blocs, et d'une couleur.
 * Le positionnement de chacun des blocs par rapport aux autres est aussi très
 * important. Il existe 7 pieces dans le jeu original. Chacune est composée de
 * quatre blocs. Voici ces 7 pièces, le bloc central autour duquel les autres
 * tournent est représenté par un X majuscule. Le numéro de la pièce, tel qu'il
 * est utilisée plus bas, est indiqué.
 *	<pre>
 *  x            x          x x    x x
 *  x X x    x X x        x X        X x
 *    0        1            2        3
 *
 *    x      x x
 *  x X x    X x          x X x x
 *
 *    4        5            6
 * </pre>
 */

class Piece {
	
	// Champs de classe
	public static final int ROTATE = 1;
	public static final int LEFT = 2;
	public static final int DOWN = 3;
	public static final int RIGHT = 4;
	public static final int DROP = 5;
	
	/**
	 * Tableau tri-dimensionnelle d'entiers, définissant les différentes pièces
	 * du jeu. Le premier indice est le numéro d'identification de la pièce, le
	 * deuxième indice est le numéro du bloc et le troisième est le numéro de la
	 * donnée (0 pour abscisse et 1 pour ordonnée). Les pièces peuvent avoir un
	 * nombre de blocs différents les unes par rapport aux autres. */
	private static final int[][][] MODELS = {
		{ {0, 0},{-1, -1},{-1, 0},{1, 0} },
		{ {0, 0},{-1, 0},{1, 0},{1, -1} },
		{ {0, 0},{-1, 0},{0, -1},{1, -1} },
		{ {0, 0},{-1, -1},{0, -1},{1, 0} },
		{ {0, 0},{-1, 0},{0, -1},{1, 0} },
		{ {0, 0},{0, -1},{1, 0},{1, -1} },
		{ {0, 0},{0, -2},{0, -1},{0, 1} },
		// Ci-dessous, les pièces ajoutées au jeu original
		
	};
	/** True si on joue en mode "monochrome". */
	private static boolean monochrome;
	/** Couleur de toutes les pièces si on joue en monochrome. @see Tetris.monochrome */
	private static Color uniColor;
	/** Couleurs des pièces lorsque le jeu n'est pas monochrome. @see Tetris.monochrome */
	private static Color[] colors = new Color[MODELS.length];
	/**
	 * Liste des pièces utilisés dans le jeu. Les éléments du tableau sont des indices
	 * de MODELS. Ainsi, models[i] = i pour i = 0...6 signifie qu'on va jouer avec les
	 * sept premières pièces de MODELS. @see Piece.MODELS */
	private static int[] models;
	
	// Champs de variable
	/** Couleur de la pièce. */
	private Color color;
	/** Tableau des blocs composant la pièce. */
	private Block[] blocks;
	private int car;

	// Constructeurs
	/** Crée une pièce selon MODELS[n] puis la déplace de x colonnes et de y lignes. */
	public Piece(int n, int x, int y) {
		color = colors[n];
		car = n;
		color = (monochrome)?uniColor:colors[n];
		blocks = new Block[MODELS[n].length];
		for(int i=0;i<blocks.length;i++) {
			blocks[i] = new Block(MODELS[n][i][0] + x, MODELS[n][i][1] + y);
		}
	}
	public Piece(int x, int y){this(getRandomModelNumber(),x,y);}
	public Piece(){this(getRandomModelNumber(),0,0);}


	// Méthodes
	/** Déplace la pièce en fixant son bloc central sur (horizontal, vertical). */
	public void go(int horizontal, int vertical) {
			int x = blocks[0].getX();
			int y = blocks[0].getY();
			for(int i=0;i<blocks.length;i++)
				blocks[i].move(horizontal-x, vertical-y);
	}
	/** Déplace une pièce de horizontal colonnes et de vertical lignes. */
	public void move(int horizontal, int vertical) {
		for(int i=0;i<blocks.length;i++)
	  	blocks[i].move(horizontal, vertical);
	}
	/** Déplace une pièce en se servant des constantes ROTATE, LEFT, DOWN et RIGHT. */
	public void move(int movement) {
		switch(movement) {
			case ROTATE :
				rotate(true);
				break;
			case -ROTATE :
				rotate(false);
				break;
			case DOWN :
				move(0,-1);
				break;
			case -DOWN :
				move(0,1);
				break;
			case LEFT :	case -RIGHT :
				move(-1,0);
				break;
			case RIGHT : case -LEFT :
				move(1,0);
				break;
		}
	}
	
	/**
	 * Faire tourner une piece autour du bloc central :
	 * si ça parait un peu compliqué au début, un petit
	 * algorithme qui marche bien revient à exprimer les
	 * coordonnées de chaque bloc relativement à celles
	 * du bloc central, puis d'échanger les valeurs des
	 * abscisses et des ordonnées en multipliant l'une
	 * des deux par (-1). Ensuite il suffit de repasser
	 * les coordonnées en "absolue" en ajoutant celles
	 * du bloc central. Mathématiquement :
	 * <pre>
	 * Avec :
	 *
	 * (x0,y0)	le bloc central
	 * (x,y)		le bloc à déplacer
	 *
	 * On fait :
	 *
	 * x = (y-y0)+x0
	 * y = -(x-x0)+y0		pour tourner dans le sens des aiguilles d'une montre
	 * 
 	 * x = -(y-y0)+x0
	 * y = (x-x0)+y0		pour tourner dans le sens contraire
	 *
	 * </pre>
	 * Attention : ça n'apparait pas ci-dessus par souci
	 * de clarté mais on a besoin de la valeur "initiale"
	 * de x pour calculer la nouvelle valeur de y.
	 *
	 * @param clockwise Mettre true si la rotation doit se fait dans
	 * le sens des aiguilles d'une montre.
	 */
	public void rotate(boolean clockwise) {
		int temp0x = blocks[0].getX();
	  int temp0y = blocks[0].getY();
	if (car != 5){
	  for(int i=1;i<blocks.length;i++) {
		  int tempix = blocks[i].getX();
			int tempiy = blocks[i].getY();
		  if (clockwise == true)	{	//	tourner dans le sens des aiguilles
				blocks[i].setX(tempiy - temp0y + temp0x);
				blocks[i].setY(- tempix + temp0x + temp0y);
		  } else {									//	tourner dans l'autre sens
				blocks[i].setX(- tempiy + temp0y + temp0x);
				blocks[i].setY(tempix - temp0x + temp0y);
		  }
	  }
	 } 
	}
	
	/**
	 * Afficher la pièce dans le Graphics g avec la couleur c.
	 * On met outPainting à false si on veut n'afficher que les blocs
	 * dont l'ordonnée ne dépasse Grid.rows. En pratique, on met false
	 * pour afficher la pièce qui est entrain de jouer et true pour
	 * afficher la pièce à venir. @see Grid.ROWS */  
  public void paint(Graphics g, Color c, boolean outPainting) {
		g.setColor(c);
		for(int i=0;i<blocks.length;i++)
		  if (blocks[i].getY() < Grid.ROWS || outPainting)
			  blocks[i].paint(g);
	}
	/** Afficher la pièce dans sa couleur. */
	public void paint(Graphics g, boolean b) {
		paint(g,color,b);
	}
	/** Idem que ci-dessus en n'affichant que le bloc dont l'ordonnée est inférieure à Grid.ROWS. */
	public void paint(Graphics g) {
	  paint(g,color,false);
  }


	// Accessors
	/**
	 * Il ne s'agit pas d'un véritable "accessor" mais il est pratique de le considérer comme tel.
	 * Retourne un élément de models tiré aléatoirement. @return Un entier entre 0 et models.length. */
	private static int getRandomModelNumber() {return models[(int)(Math.random()*models.length)];}
	/**
	 * Il ne s'agit pas vraiment d'un "accessor" mais il est pratique de considérer comme tel.
	 * Retourne aléatoirement une couleur parmi celles des pièces utilisées par le jeu.
	 * @return La couleur d'une des pièces utilisées dans le jeu. */
	public static Color getRandomUsedColor() {return (monochrome)?uniColor:colors[getRandomModelNumber()];}
	public static Color getUniColor() {return uniColor;}
	public static Color[] getColors() {return colors;}
	public static boolean isMonochrome() {return monochrome;}
	public Block[] getBlocks() {return blocks;}
	public Color getColor() {return color;}

			
	// Mutators
	public static void setMonochrome(boolean b) {monochrome = b;}
	public static void setUniColor(Color c) {uniColor = c;}
	public static void setColors(int i, Color c) {colors[i] = c;}
	/**
	 * Cette méthode static, il faut la lancer en début de partie.
	 * Elle rempli models avec les indices des MODELS correspondant aux
	 * pièces que l'on souhaite utilisé. Pour ce faire, elle utilise colors en
	 * considérant que pièce d'indice i est non désirée si colors[i] = null. */
	public static void setModels() {
		int i=0,j=0;
		for(;i<colors.length;i++)
			if(colors[i] != null)
				j++;
		models = new int[j];
		for(i=0,j=0;i<models.length;i++,j++) {
			while(colors[j] == null)
				j++;
			models[i] = j;
		}
	}


}