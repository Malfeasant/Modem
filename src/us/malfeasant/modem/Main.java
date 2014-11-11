package us.malfeasant.modem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {
	private static final int MAX_SIZE = 0x1000;
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new Main());
	}
	
	private final JFrame frame;
	private final JLabel filename;
	private final JFileChooser chooser;
	private byte[] buffer;
	
	private Main() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		filename = new JLabel();
		chooser = new JFileChooser();
		
		JButton browse = new JButton("Browse...");
		browse.addActionListener(e -> choose());
		
		JButton play = new JButton("Play");
		play.addActionListener(e -> play());
		
		JPanel panel = new JPanel();
		panel.add(filename);
		panel.add(browse);
		panel.add(play);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	private void choose() {
		if (chooser.showOpenDialog(filename) == JFileChooser.APPROVE_OPTION) {
			Path path = chooser.getSelectedFile().toPath();
			try {
				if (Files.size(path) < MAX_SIZE) {
					buffer = Files.readAllBytes(path);
					filename.setText(path.getFileName().toString());
					frame.pack();
				} else {
					JOptionPane.showMessageDialog(filename, "File " + path.getFileName() + " is too large.",
							"Problem", JOptionPane.ERROR_MESSAGE);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(filename, "Can't open file " + path.getFileName(), "Problem", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
	
	private void play() {
		if (buffer == null || buffer.length == 0)
			JOptionPane.showMessageDialog(filename, "Buffer is empty, try opening a file.", "Problem", JOptionPane.ERROR_MESSAGE);
		else {
			new Player(buffer).start();
		}
	}
}
