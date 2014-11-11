package us.malfeasant.modem;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Player extends Thread {
	private static final AudioFormat format = new AudioFormat(48000, 8, 1, true, true);
	private static final DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
	private static final double mStep = Math.PI * 2 * 127 / 4800;
	private static final double sStep = Math.PI * 2 * 107 / 4800;
	
	private final byte[] buffer;
	private final SourceDataLine line;
	
	public Player(byte[] buffer) {
		this.buffer = buffer;
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open();
		} catch (LineUnavailableException e) {
			throw new IllegalStateException(e);	// TODO - this is bad, at least make a custom exception...
		}
	}
	
	@Override
	public void run() {
		line.start();
		idle();
		for (byte b : buffer) {
			play(b);
		}
		idle();
	}
	private void play(byte b) {
		play(false);	// start bit
		for (int i = 0; i < 8; ++i) {
			play((b & 1) != 0);
			b >>= 1;
		}
		play(true);	// stop bit
	}
	private static final int samplesPerSymbol = 48000 / 300;
	private byte[] samples = new byte[samplesPerSymbol];
	private double waveformPos;
	private void play(boolean mark) {
		for (int i = 0; i < samplesPerSymbol; i++) {
			samples[i] = (byte) (Math.sin(waveformPos) * 127);
			waveformPos += mark ? mStep : sStep;
		}
		line.write(samples, 0, samplesPerSymbol);
//		System.out.println("Wrote " + samplesPerSymbol + " samples.");
	}
	private void idle() {
		for (int i = 0; i < 100; i++) play(true);	// some padding before and after
	}
}
