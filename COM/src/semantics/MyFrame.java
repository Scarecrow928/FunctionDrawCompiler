package semantics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import parsing.*;
import lexicalAnalyze.*;
import javax.sound.sampled.Line;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class MyFrame extends JFrame {
	private ArrayList<Dimension> line;
	public ArrayList<Dimension> getLine() {
		return line;
	}

	public void setLine(ArrayList<Dimension> line) {
		this.line = line;
	}

	private String filename;
	private int w;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		if (args.length == 0) {
			System.out.println("Input options and filename.");
		} else if (args[0].equals("-p")) {
			if(args[1] == null) {
				System.out.println("-p Lost filename.");
				return;
			}
			try {
				Parsing parsing = new Parsing(args[1]);
				parsing.parsingToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (args[0].equals("-l")) {
			if(args[1] == null) {
				System.out.println("-l Lost filename.");
				return;
			}
			try {
				Lexical lexical = new Lexical(args[1]);
				lexical.lexicalAnalyze();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						MyFrame frame = new MyFrame();
						Semantics semantics = new Semantics(args[0]);
						try {
							ArrayList<Dimension> ar = semantics.transform();
							frame.setLine(ar);
						} catch (IOException e) {
							e.printStackTrace();
						} 
						frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		
	}

	/**
	 * Create the frame.
	 */
	public MyFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, (int) getToolkit().getScreenSize().getWidth(), (int) getToolkit().getScreenSize().getHeight());
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		w = 2;
		line = new ArrayList<>();
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D graphics2d = (Graphics2D) g;
		for(Dimension d: line) {
			graphics2d.fillRect(d.width, d.height, w, w);
		}
	}
	
	public void loadGraphics() {
		
	}

}
