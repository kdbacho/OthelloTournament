package sound;

import java.io.*;
import java.net.URL;

import javax.sound.sampled.*;
import javax.swing.JFileChooser;

public enum Sound
{
	MASTER("mc.wav"), SUPER("super.wav"), KING("king.wav"), AWESOME(
			"awesome.wav"), ULTRA("ultra.wav");
	private Clip clip;


	Sound(String soundFileName)
	{
		try
		{
			// Use URL (instead of File) to read from disk and JAR.
			File url = new File(soundFileName);
			;
			AudioInputStream audioInputStream = AudioSystem
					.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
		}
		catch(UnsupportedAudioFileException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}


	public void play()
	{
		if(clip.isRunning()) clip.stop();

		clip.setFramePosition(0);
		clip.start();
	}


	public static void handleCombos(int i)
	{
		if(i == 4)
			SUPER.play();
		else if(i == 5)
			MASTER.play();
		else if(i == 6)
			AWESOME.play();
		else if(i == 7)
			KING.play();
		else if(i > 7) ULTRA.play();
	}


	public static void init()
	{
		values();
	}
}
