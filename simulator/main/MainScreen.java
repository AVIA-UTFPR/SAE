package main;

import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;

import javax.swing.JFrame;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class MainScreen extends JFrame{

	public MainScreen() throws HeadlessException {
		setSize(600,400);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setContentPane(new DrawArea());
		
		setVisible(true);
	}
	
	public static void main (String[] args) {
		new MainScreen();
	}
	
}

@SuppressWarnings("serial")
class DrawArea extends JPanel {
	Point A = null;
	public DrawArea() {
		A = new Point(100,200);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawRect(A.x, A.y, 50, 50);
	}
	
}
