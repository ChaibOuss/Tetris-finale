import java.io.FileInputStream;
import java.io.IOException;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/** Cette classe sert simplement à jouer des sons au format *.au. */

public class Sound {
	private static boolean enable;
	
	public static final String PATH = "audio/";
	public static final String EXT = ".au";
	public static boolean isEnable() {return enable;}
	public static void setEnable(boolean b) {enable = b;}
	public static void change() {enable = !enable;}

	/** Jouer un son. */
	public static void play(String name) {
		if(enable)
			try{
				AudioPlayer.player.start(new AudioStream(new FileInputStream(PATH+name+EXT)));
			} catch(IOException e){}		
	}

}
