package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.JFrame.*;

import util.Coordinate;

//servidor de eco
//recebe uma linha e ecoa a linha recebida.

import java.io.*;
import java.net.*;
import java.util.ArrayList;




public class Simulator extends JComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JFrame window = new JFrame();
	
	private int width = 800;
	private int height = 800;
	
	private int gridSize = 10;
	private int gridAmplifier = 55;

	private Coordinate car = new Coordinate(0, 0);
	private Coordinate depot = new Coordinate(0, 0);
	private Coordinate pickUp = new Coordinate(gridSize, gridSize);
	private Coordinate dropOff = new Coordinate(gridSize, gridSize);
	private ArrayList<Coordinate> obstacles = new ArrayList<>();
	private ArrayList<Coordinate> noFurther = new ArrayList<>();
	
	private Simulator() throws HeadlessException {
		this.window.setTitle("Autonomous Car Simulator");
		this.window.setSize(width, height);
		this.window.setLocationRelativeTo(null);
		this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		this.window.add(this);
		
		this.window.setVisible(true);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.width, this.height);
		
	
		
		
		g.setColor(Color.RED);
		for (Coordinate coordinate : this.obstacles) {
			g.fillRect(getGridX( coordinate.getX() ), getGridY( coordinate.getY() ), this.gridAmplifier, this.gridAmplifier);
		}
		
		g.setColor(Color.pink);
		for (Coordinate coordinate : this.noFurther) {
			g.fillRect(getGridX( coordinate.getX() ), getGridY( coordinate.getY() ), this.gridAmplifier, this.gridAmplifier);
		}
		// Draw pick up
		g.setColor(Color.CYAN);
		g.fillRect(getXReduced(pickUp.getX(), 85), getYReduced(pickUp.getY(), 85), reducedSize(85), reducedSize(85));
		// Draw drop off
		g.setColor(Color.ORANGE);
		g.fillRect(getXReduced(dropOff.getX(), 65), getYReduced(dropOff.getY(), 65), reducedSize(65), reducedSize(65));
		
		// Draw Depot
		g.setColor(Color.green);
		g.fillRect(getGridX(depot.getX()), getGridY(depot.getY()), this.gridAmplifier, this.gridAmplifier);
		
		// Draw Car
		g.setColor(Color.BLUE);
		g.fillRect(getXReduced(car.getX(), 75), getYReduced(car.getY(), 75), reducedSize(75), reducedSize(75));
		
		// Draw Grid
		g.setColor( new Color(0, 0, 0) );
		for (int x = 0; x <= this.gridSize + 1; x++){
			g.drawLine(convertX(amplify(x)), convertY(0), convertX(amplify(x)), convertY( amplify(gridSize + 1) ));
		}
		
		for (int y = 0; y <= this.gridSize + 1; y++){
			g.drawLine(convertX(0), convertY( amplify(y) ), convertX( amplify(gridSize + 1) ), convertY( amplify(y) ));
		}
		
		for (int x = 0; x <= this.gridSize; x++){
			g.drawString(String.valueOf(x), convertX( amplify(x) + (int) (this.gridAmplifier * 0.4)), convertY( -20 ));
		}
		
		for (int y = 0; y <= this.gridSize; y++) {
			g.drawString(String.valueOf(y), convertX( -15 ), convertY( amplify(y) + (int) (this.gridAmplifier * 0.4) ));
		}
		
	}

	private int getGridX(int x)
	{
		return convertX(0) + (x * this.gridAmplifier); 
	}
	
	private int getGridY(int y)
	{
		return convertY(0) - this.gridAmplifier - (y * this.gridAmplifier); 
	}
	
	private int getXReduced(int x, int percentage) {
		return getGridX(x) + reducedSizeSpace(percentage);
	}
	
	private int getYReduced(int y, int percentage) {
		return getGridY(y) + reducedSizeSpace(percentage);
	}
	
	private int reducedSize(int percentage) {
		return (int) ( this.gridAmplifier * ( percentage/100.0 ));
	}
	
	private int reducedSizeSpace(int percentage) {
		return (int) (this.gridAmplifier * ( (1 - percentage/100.0) / 2 ));
	}
	
	private int amplify(int number) {
		return number * this.gridAmplifier;
	}
	
	private int convertX (int x) {
		return x + 100;
	}
	
	private int convertY (int y) {
		return this.getHeight() - y - 100;
	}
	
	private void readReceivedMessage(String message) {
		
		String[] messageArray = message.split(";");
		
		int x,  y;
		switch(messageArray[0]) {
			case	"clear": 
				System.out.println("clear");
				this.obstacles = new ArrayList<>();
				this.noFurther = new ArrayList<>();
				gridSize = 10;

				car = new Coordinate(0, 0);
				depot = new Coordinate(0, 0);
				break;
			case	"gridSize": 
				System.out.println("gridSize");
				this.gridSize = Integer.parseInt( messageArray[1] );
				break;
			case	"carLocation":
				System.out.println("carLocation");
				car.setX( Integer.parseInt( messageArray[1] ) );
				car.setY( Integer.parseInt( messageArray[2] ) );
				break;
			case	"depot":
				System.out.println("depot");
				depot.setX( Integer.parseInt( messageArray[1] ) );
				depot.setY( Integer.parseInt( messageArray[2] ) );
				break;
			case	"obstacle":
				System.out.println("obstacle");
				x = Integer.parseInt( messageArray[1] );
				y = Integer.parseInt( messageArray[2] );
				this.obstacles.add( new Coordinate(x, y) );
				break;
			case	"noFurther":
				System.out.println("noFurther");
				x = Integer.parseInt( messageArray[1] );
				y = Integer.parseInt( messageArray[2] );
				this.noFurther.add( new Coordinate(x, y) );
				break;
			case	"pickUp":
				System.out.println("pickUp");
				this.pickUp.setX( Integer.parseInt( messageArray[1] ) );
				this.pickUp.setY( Integer.parseInt( messageArray[2] ) ); 
				break;
			case	"dropOff":
				System.out.println("dropOff");
				this.dropOff.setX( Integer.parseInt( messageArray[1] ) );
				this.dropOff.setY( Integer.parseInt( messageArray[2] ) ); 
				break;
			
			default:
				System.out.println(messageArray[0]);
				System.out.println("Whoops");
		}
		
		repaint();
	}
	
	
	public static void main(String args[]){
		
		Simulator sim = new Simulator();
	
	    //System.out.println("Servidor carregado no IP 127.0.0.1 e na porta 9999");
		
		try {

			DatagramSocket server = new DatagramSocket(9999);
			byte[] receive = new byte[1024];
			byte[] send = new byte[1024];
			
			while(true)
			{
				DatagramPacket receivePacket = new DatagramPacket(receive, receive.length); // Pacote Recebido
				server.receive(receivePacket);
				
				// Exibir pacote recebido
				String sentence = new String( receivePacket.getData() );
				System.out.println("Received: " + sentence);
				
				sim.readReceivedMessage(sentence);
				/*
				// Pegar endereço do pacote
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				
				// Modificar String recebida
				String capitalizedSentence = sentence.toUpperCase();
				send = capitalizedSentence.getBytes();
				
				// Enviar resposta
				DatagramPacket sendPacket = new DatagramPacket(send, send.length, IPAddress, port);
				server.send(sendPacket);
				*/
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	
	     
	 }

}

class DrawArea extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Point A = null;
	public DrawArea() {
		A = new Point(100,200);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawRect(A.x, A.y, 50, 50);
	}
	
}



