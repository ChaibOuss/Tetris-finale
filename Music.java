import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;

/** Cette classe sert à jouer un midi. */

public class Music {

	private static final String PATH = "audio/";
	private static final String EXT = ".mid";
	private static Sequencer sequencer;
	private static String file = "playback";
	private static boolean enable;

	// Jouer la musique
	public static void play() {
		if(enable) {
			try {
				sequencer = MidiSystem.getSequencer();
				sequencer.open();
				sequencer.setSequence(MidiSystem.getSequence(new File(PATH+file+EXT)));
				sequencer.start();
			} catch (IOException e) {
			} catch (MidiUnavailableException e) {
			} catch (InvalidMidiDataException e) {
			}
		}
	}

	// Arreter de jouer
	public static void stop() {
		if(sequencer != null)
			sequencer.stop();
	}

	
	//	Accessors
	public static boolean isEnable() {return enable;}
	
	//	Mutators
	public static void setFile(String s) {file = s;}
	public static void setEnable(boolean b) {enable = b;}
	public static void change() {
		enable = !enable;
		if(enable)
			play();
		else
			stop();
	}

}
