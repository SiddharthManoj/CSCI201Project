/*
 * MusicPlayer.java
 * James Zhang
 * January 14 2012
 * Uses the JLayer library to play mp3 files.
 */

package game;

import java.io.InputStream;
import java.util.ArrayList;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class MusicPlayer extends PlaybackListener implements Runnable {
	// a list of loops, which repeats. only one loop can be playing
	private static ArrayList<MusicPlayer> loops;

	// a list of effects, which do not repeat. multiple effects can be playing
	private static ArrayList<MusicPlayer> effects;

	// the name of the music file with the extension
	private String file;

	// the name of the music file without the extension
	private String name;

	// the player that will play the music file
	private AdvancedPlayer player;

	// whether the music is playing
	private boolean playing;

	// whether to repeat this music file
	private boolean repeat;

	// constructor
	public MusicPlayer(String file, boolean repeat) {
		try {
			// get the stream for the music file
			InputStream stream = Game.loadStream(file);
			
			// assign variables
			this.file = file;
			this.player = new AdvancedPlayer(stream);
			this.player.setPlayBackListener(this);
		} catch (Exception e) {
			// the game can still be played without music
		}

		// assign the name to the file name without the .mp3 extension
		this.name = file.substring(0, file.length() - 4);
		this.repeat = repeat;
	}
	
	// loads all of the music files used in the game
	public static void load() {
		loops = new ArrayList<MusicPlayer>();
		effects = new ArrayList<MusicPlayer>();

		// load the loops that will be used
		loops.add(new MusicPlayer("main.mp3", true));
		loops.add(new MusicPlayer("level1.mp3", true));
		loops.add(new MusicPlayer("level2.mp3", true));
		loops.add(new MusicPlayer("level3.mp3", true));
		loops.add(new MusicPlayer("level4.mp3", true));
		loops.add(new MusicPlayer("level5.mp3", true));
		loops.add(new MusicPlayer("win.mp3", true));

		// load the effects to be used in the game
		effects.add(new MusicPlayer("bullet.mp3", false));
	}
	
	// plays a loop
	public static void playLoop(String name) {
		// stop the current loop
		for (MusicPlayer loop : loops) {
			if (loop.playing) {
				// there's no need to stop the current loop and replay it
				if (name.equals(loop.name))
					return;
				else {
					try {
						loop.repeat = false;
						loop.playing = false;
						loop.player.stop();
					} catch (Exception e) {
						// the game can still be played without music
					}
				}
			}
		}

		// play the new loop
		play(name, loops);
	}

	// plays an effect once
	public static void playEffect(String name) {
		play(name, effects);
	}

	// plays a specific loop or effect
	private static void play(String name, ArrayList<MusicPlayer> list) {
		MusicPlayer player = null;

		// keep the most recent instance of a music file in the list
		for (int i = list.size() - 1; i >= 0; i--) {
			if (list.get(i).name.equals(name)) {
				// remove the old one
				player = list.get(i);
				list.remove(i);
				break;
			}
		}

		// play the music if it was found
		if (player != null) {
			// play the new one
			MusicPlayer newPlayer = new MusicPlayer(player.file, player.repeat);
			newPlayer.playing = true;
			newPlayer.play();
			
			// add the new one
			list.add(newPlayer);
		}
	}

	// plays the music
	private void play() {
		try {
			this.playing = true;
			
			// use a separate thread
			new Thread(this).start();
		} catch (Exception e) {
			// the game can still be played without music
		}
	}

	// called when a loop finishes playing
	@Override
	public void playbackFinished(PlaybackEvent evt) {
		// repeat the music if needed
		if (repeat) {
			try {
				// recreate the class in order to read it from the start
				InputStream stream = Game.loadStream(file);
				this.player = new AdvancedPlayer(stream);
				this.player.setPlayBackListener(this);
				this.playing = true;
				this.player.play();
			} catch (Exception e) {
				// the game can still be played without music
			}
		}
	}

	// plays the music in a different thread
	@Override
	public void run() {
		try {
			player.play();
		} catch (Exception e) {
			// the game can still be played without music
		}
	}
}
