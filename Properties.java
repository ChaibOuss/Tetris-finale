import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Color;

/**
 * On peut sauvegarder ses préférences dans un fichier Properties.
 * Mais plus encore, celui-ci permet d'accéder à des options qui ne figurent pas
 * dans le menu, comme changer la couleur des pièces et choisir quelles pièce
 * utiliser dans le jeu, changer les configurations claviers préexistantes...
 * Le fichier default.properties ne devrait normalement pas être édité à la main,
 * il ne sert qu'à rétablir les parametres d'origine du jeu. Le fichier user.properties
 * est le votre et vous pouvez en faire ce que vous voulez, mais une mauvaise édition risque
 * de rendre le jeu inutilisable. Vous etes alors inviter à copier default.properties dans
 * user.properties afin de rétablir les paramètres d'origine "manuellement".
 */

public class Properties extends java.util.Properties {
	
	public static final String DEFAULT_FILE = "default.properties";
	public static final String USER_FILE = "user.properties";
	
	// Constructeurs
	public Properties(Tetris tetris, String file) {
		super();
		load(tetris, file);
	}
	public Properties(Tetris tetris) {this(tetris, USER_FILE);}


	/** Charger les parametres depuis un fichier. */
	public void load(Tetris tetris, String file) {
		try {
			load(new FileInputStream(file));
		} catch (IOException e) {}
		Player.setStartingLevel(Integer.parseInt(getProperty("startingLevel")));
		Player.setInteractionFactor(Integer.parseInt(getProperty("interactionFactor")));
		Player.setStartingRows(Integer.parseInt(getProperty("startingRows")));
		Grid.setHolesInRows(Integer.parseInt(getProperty("holesInRows")));
		Player.setRounds(Integer.parseInt(getProperty("rounds")));
		Sound.setEnable(Boolean.valueOf(getProperty("sound")).booleanValue());
		Music.setEnable(Boolean.valueOf(getProperty("music")).booleanValue());
		for(int i=0;i<5;i++)
			Player.setScores(i,Integer.parseInt(getProperty("scores").split(":")[i]));
		Player.setBackGround(new Color(Integer.parseInt(getProperty("background"),16)));
		Player.setForeGround(new Color(Integer.parseInt(getProperty("foreground"),16)));
		Player.setSpecialGround(new Color(Integer.parseInt(getProperty("message"),16)));
		Piece.setMonochrome(Boolean.valueOf(getProperty("monochrome")).booleanValue());
		for(int i=0;i<Piece.getColors().length;i++) {
			String str = getProperty("piece"+i);
			if (str.length()>0)
				Piece.setColors(i,new Color(Integer.parseInt(str,16)));
			else 
				Piece.setColors(i,null);
		}
		Piece.setUniColor(new Color(Integer.parseInt(getProperty("piece"),16)));
		tetris.setPauseKey(Integer.parseInt(getProperty("pauseKey")));
		for(int i=0;i<5;i++) {
			String[] s = getProperty("keys"+i).split(":");
			for(int j=0;j<5;j++)
				Tetris.setKeyConf(i,j,Integer.parseInt(s[j]));
		String[] keys = getProperty("keys").split(":");
		Tetris.setKeys(0,Integer.parseInt(keys[0]));
		Tetris.setKeys(1,Integer.parseInt(keys[1]));
		}
	}
	
	public void load(Tetris tetris) {load(tetris,USER_FILE);}

	
	/** Enregistrer la configuration actuelle dans USER_FILE */
	public boolean store(Tetris tetris) {
		setProperty("startingLevel",String.valueOf(Player.getStartingLevel()));
		setProperty("interactionFactor",String.valueOf(Player.getInteractionFactor()));
		setProperty("startingRows",String.valueOf(Player.getStartingRows()));
		setProperty("holesInRows",String.valueOf(Grid.getHolesInRows()));
		setProperty("rounds",String.valueOf(Player.getRounds()));
		setProperty("sound",String.valueOf(Sound.isEnable()));
		setProperty("music",String.valueOf(Music.isEnable()));
		String scores = "";
		for(int i=0;i<4;i++)
			scores += Player.getScores(i)+":";
		scores += Player.getScores(4);
		setProperty("scores",scores);
		setProperty("background",color2String(Player.getBackGround()));
		setProperty("foreground",color2String(Player.getForeGround()));
		setProperty("message",color2String(Player.getSpecialGround()));
		setProperty("monochrome",String.valueOf(Piece.isMonochrome()));
		setProperty("piece",color2String(Piece.getUniColor()));
		for(int i=0;i<Piece.getColors().length;i++)
				setProperty("piece"+i,(Piece.getColors()[i] == null)?"":color2String(Piece.getColors()[i]));
		setProperty("pauseKey",String.valueOf(tetris.getPauseKey()));
		setProperty("keys",Tetris.getKeys()[0]+":"+Tetris.getKeys()[1]);
		try {
			store(new FileOutputStream(USER_FILE), null);
			return true;
		} catch (IOException e) { return false;}
	}

	/** Méthode servant à convertir une couleur en String représentant sa valeur hexadecimale. */
	private String color2String(Color c) {
		String[] s = new String[3];
		for(int i=0;i<3;i++) {
			s[0] = Integer.toHexString(c.getRed());
			s[1] = Integer.toHexString(c.getGreen());
			s[2] = Integer.toHexString(c.getBlue());
		}
		for (int j=0;j<3;j++)
			if(s[j].length() < 2)
				s[j] = '0'+s[j];
		return (s[0]+s[1]+s[2]);
	}
	
}
