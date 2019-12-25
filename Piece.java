import java.awt.Graphics;
import java.awt.Color;

/**
 * Une pi�ce du jeu est compos�e d'un certain nombre de blocs, et d'une couleur.
 * Le positionnement de chacun des blocs par rapport aux autres est aussi tr�s
 * important. Il existe 7 pieces dans le jeu original. Chacune est compos�e de
 * quatre blocs. Voici ces 7 pi�ces, le bloc central autour duquel les autres
 * tournent est repr�sent� par un X majuscule. Le num�ro de la pi�ce, tel qu'il
 * est utilis�e plus bas, est indiqu�.
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
	 * Tableau tri-dimensionnelle d'entiers, d�finissant les diff�rentes pi�ces
	 * du jeu. Le premier indice est le num�ro d'identification de la pi�ce, le
	 * deuxi�me indice est le num�ro du bloc et le troisi�me est le num�ro de la
	 * donn�e (0 pour abscisse et 1 pour ordonn�e). Les pi�ces peuvent avoir un
	 * nombre de blocs diff�rents les unes par rapport aux autres. */
	private static final int[][][] MODELS = {
		{ {0, 0},{-1, -1},{-1, 0},{1, 0} },
		{ {0, 0},{-1, 0},{1, 0},{1, -1} },
		{ {0, 0},{-1, 0},{0, -1},{1, -1} },
		{ {0, 0},{-1, -1},{0, -1},{1, 0} },
		{ {0, 0},{-1, 0},{0, -1},{1, 0} },
		{ {0, 0},{0, -1},{1, 0},{1, -1} },
		{ {0, 0},{0, -2},{0, -1},{0, 1} },
		// Ci-dessous, les pi�ces ajout�es au jeu original
		
	};
	/** True si on joue en mode "monochrome". */
	private static boolean monochrome;
	/** Couleur de toutes les pi�ces si on joue en monochrome. @see Tetris.monochrome */
	private static Color uniColor;
	/** Couleurs des pi�ces lorsque le jeu n'est pas monochrome. @see Tetris.monochrome */
	private static Color[] colors = new Color[MODELS.length];
	/**
	 * Liste des pi�ces utilis�s dans le jeu. Les �l�ments du tableau sont des indices
	 * de MODELS. Ainsi, models[i] = i pour i = 0...6 signifie qu'on va jouer avec les
	 * sept premi�res pi�ces de MODELS. @see Piece.MODELS */
	private static int[] models;
	
	// Champs de variable
	/** Couleur de la pi�ce. */
	private Color color;
	/** Tableau des blocs composant la pi�ce. */
	private Block[] blocks;
	private int car;

	// Constructeurs
	/** Cr�e une pi�ce selon MODELS[n] puis la d�place de x colonnes et de y lignes. */
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


	// M�thodes
	/** D�place la pi�ce en fixant son bloc central sur (horizontal, vertical). */
	public void go(int horizontal, int vertical) {
			int x = blocks[0].getX();
			int y = blocks[0].getY();
			for(int i=0;i<blocks.length;i++)
				blocks[i].move(horizontal-x, vertical-y);
	}
	/** D�place une pi�ce de horizontal colonnes et de vertical lignes. */
	public void move(int horizontal, int vertical) {
		for(int i=0;i<blocks.length;i++)
	  	blocks[i].move(horizontal, vertical);
	}
	/** D�place une pi�ce en se servant des constantes ROTATE, LEFT, DOWN et RIGHT. */
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
	 * si �a parait un peu compliqu� au d�but, un petit
	 * algorithme qui marche bien revient � exprimer les
	 * coordonn�es de chaque bloc relativement � celles
	 * du bloc central, puis d'�changer les valeurs des
	 * abscisses et des ordonn�es en multipliant l'une
	 * des deux par (-1). Ensuite il suffit de repasser
	 * les coordonn�es en "absolue" en ajoutant celles
	 * du bloc central. Math�matiquement :
	 * <pre>
	 * Avec :
	 *
	 * (x0,y0)	le bloc central
	 * (x,y)		le bloc � d�placer
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
	 * Attention : �a n'apparait pas ci-dessus par souci
	 * de clart� mais on a besoin de la valeur "initiale"
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
	 * Afficher la pi�ce dans le Graphics g avec la couleur c.
	 * On met outPainting � false si on veut n'afficher que les blocs
	 * dont l'ordonn�e ne d�passe Grid.rows. En pratique, on met false
	 * pour afficher la pi�ce qui est entrain de jouer et true pour
	 * afficher la pi�ce � venir. @see Grid.ROWS */  
  public void paint(Graphics g, Color c, boolean outPainting) {
		g.setColor(c);
		for(int i=0;i<blocks.length;i++)
		  if (blocks[i].getY() < Grid.ROWS || outPainting)
			  blocks[i].paint(g);
	}
	/** Afficher la pi�ce dans sa couleur. */
	public void paint(Graphics g, boolean b) {
		paint(g,color,b);
	}
	/** Idem que ci-dessus en n'affichant que le bloc dont l'ordonn�e est inf�rieure � Grid.ROWS. */
	public void paint(Graphics g) {
	  paint(g,color,false);
  }


	// Accessors
	/**
	 * Il ne s'agit pas d'un v�ritable "accessor" mais il est pratique de le consid�rer comme tel.
	 * Retourne un �l�ment de models tir� al�atoirement. @return Un entier entre 0 et models.length. */
	private static int getRandomModelNumber() {return models[(int)(Math.random()*models.length)];}
	/**
	 * Il ne s'agit pas vraiment d'un "accessor" mais il est pratique de consid�rer comme tel.
	 * Retourne al�atoirement une couleur parmi celles des pi�ces utilis�es par le jeu.
	 * @return La couleur d'une des pi�ces utilis�es dans le jeu. */
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
	 * Cette m�thode static, il faut la lancer en d�but de partie.
	 * Elle rempli models avec les indices des MODELS correspondant aux
	 * pi�ces que l'on souhaite utilis�. Pour ce faire, elle utilise colors en
	 * consid�rant que pi�ce d'indice i est non d�sir�e si colors[i] = null. */
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