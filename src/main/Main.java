package main;

import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Main {
	public static String pathJar = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	public static String path = pathJar.replace("WavConcat.jar", "");
	public static File mainFolder = new File(path);
	public static File[] listOfFolders = mainFolder.listFiles();

	public static void main(String[] args) throws IOException, UnsupportedAudioFileException {
		System.out.println(path);
		for (File folder : listOfFolders) {
			if (folder.isDirectory()) {
				String destinationFileName = folder.getName();
				List<String> sourceFilesList = new ArrayList<String>();
				File[] files = folder.listFiles();
				for (File file : files) {
					if (file.isFile()) {
						sourceFilesList.add(file.getName());
					}
				}
				AudioInputStream audioInputStream = null;
				List<AudioInputStream> audioInputStreamList = null;
				AudioFormat audioFormat = null;
				Long frameLength = null;
				// loop through our files first and load them up
				for (String sourceFile : sourceFilesList) {
					File input = new File(destinationFileName + "/" + sourceFile);
					System.out.println(destinationFileName + "/" + sourceFile);
					File silenceFile = new File(path + "silence.wav");
					AudioInputStream silence = AudioSystem.getAudioInputStream(silenceFile);
					audioInputStream = AudioSystem.getAudioInputStream(input);

					// get the format of first file
					if (audioFormat == null) {
						audioFormat = audioInputStream.getFormat();
					}

					// add it to our stream list
					if (audioInputStreamList == null) {
						audioInputStreamList = new ArrayList<AudioInputStream>();
					}
					audioInputStreamList.add(audioInputStream);
					audioInputStreamList.add(silence);

					// keep calculating frame length
					if (frameLength == null) {
						frameLength = audioInputStream.getFrameLength();
					} else {
						frameLength += audioInputStream.getFrameLength();
						frameLength += silence.getFrameLength();
					}
				}

				// now write our concatenated file
				File output = new File(destinationFileName + ".wav");
				AudioSystem.write(new AudioInputStream(new SequenceInputStream(
						Collections.enumeration(audioInputStreamList)),
						audioFormat, frameLength), AudioFileFormat.Type.WAVE,
						output);

				if (audioInputStream != null) {
					audioInputStream.close();
				}
				if (audioInputStreamList != null) {
					audioInputStreamList = null;
				}
			}
		}
	}
}