import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;

/** Cette classe s'occupe de garder à jour un fichier contenant les meilleurs scores. */

public class HighScore {
	
	/** Nombre de scores à conserver. */
	public static final int NBMAX = 10;
	/** Sert de séparateur dans le fichier des High Scores. */
	public static final String SEPARATOR = "\t";
	/** Son substitut si un utilisateur utilisait cette chaine/caractere dans son nom. */
	public static final String SUBSTITUTE = "_";
	private static final String FILE = "high.scores";
	private static ArrayList scores;

	/** Copier FILE dans l'ArrayList scores */
	public static void read() {
		scores = new ArrayList();
		try {
	   BufferedReader in = new BufferedReader(new FileReader(FILE));
	   String s;
	   int i=0;
	   while ((s = in.readLine()) != null && i++ < NBMAX)
		   scores.add(s.split(SEPARATOR));
	   in.close();
   } catch (IOException e) {}
	}


	/** Copier l'ArrayList scores dans FILE. */
	private static void write() {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(FILE));
			String[] s;
			out.flush();
			for(int j=0;j<NBMAX && j<scores.size();j++) {
				s=(String [])scores.get(j);
				out.println(s[0]+SEPARATOR+s[1]);
			}
			out.close();
		} catch (IOException e) {}
	}

	/** Affiche un JOptionPane listant les High Scores. */
	public static void show(Component c) {
		read();
		StringBuffer str = new StringBuffer("");
		for(int i=0;i<scores.size();i++) {
			String[] s = (String[]) scores.get(i);
			str.append((i+1)+". "+s[0]+" a fait "+s[1]+" points.\n"); 
		}
		JOptionPane.showMessageDialog(c,str,"High Scores",JOptionPane.PLAIN_MESSAGE);
	}
	
	
	/** Vérifie si l'entier score est dans le top et l'insère si il faut @param number Le numéro du joueur.
	 * @return true si FILE a été mis à jour et false si c'était une fausse alerte. */
	public static boolean checkItOut(Tetris tetris, int number, int score) {
		if (score <= 0)
			return false;
		read();
		String[] s;
		int size = scores.size();
		int i = 0;
		while(i < NBMAX && i < size && score < Integer.parseInt((s=(String [])scores.get(i))[1]))
			i++; // on se décale jusqu'à la position de ce score (si il est trop faible on dépasse NBMAX
		if(i<NBMAX || size==0) { // si le joueur a gagné sa place
			s = new String[2];
			do {
				if (tetris.getPlayers() != null)
					tetris.setPause(true);
				s[0] = JOptionPane.showInputDialog(tetris,"bravo! vous faites partie des meilleurs.\nVeuillez entrer votre nom :");
				if (s[0] == null) // on lui demande d'entrer son nom
					return false;
			} while (s[0].length() == 0);
			s[0] = s[0].replaceAll(SEPARATOR,SUBSTITUTE);
			s[1] = ""+score;
			scores.add(i,s);
			write(); // on ecrit ca dans le fichier
			return true;
		}
		return false;
	}

}