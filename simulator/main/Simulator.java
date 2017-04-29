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
import util.GridCell;

//servidor de eco
//recebe uma linha e ecoa a linha recebida.

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




public class Simulator extends JComponent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JFrame window = new JFrame();
	
	private int width = 800;
	private int height = 800;
	
	private int gridSize = 10;
	private int gridAmplifier = 30;

	private Coordinate car = new Coordinate(5, 5);
	private String direction = "west";
	private Coordinate depot = new Coordinate(0, 0);
	private Coordinate pickUp = new Coordinate(gridSize, gridSize);
	private Coordinate dropOff = new Coordinate(gridSize, gridSize);
	Map<String, GridCell> environmentGrid;
	
	private Simulator() throws HeadlessException {
		this.window.setTitle("Autonomous Car Simulator");
		this.window.setSize(width, height);
		this.window.setLocationRelativeTo(null);
		this.window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		initGridInformation();
		this.window.add(this);
		this.window.setVisible(true);
	}
	
	private void initGridInformation() {

		environmentGrid = new HashMap<String, GridCell>();
		
		for (int x = 0; x <= gridSize; x++) {
			for (int y = 0; y <= gridSize; y++) {

				String cellName = GridCell.getIndex(x, y);
				environmentGrid.put(cellName, new GridCell(x, y, false, false));

			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.width, this.height);
		
		

		g.setColor(Color.red);
		for (int x = 0; x <= this.gridSize; x++) {
			for (int y = 0; y <= this.gridSize; y++) {

				if( this.environmentGrid.get(GridCell.getIndex(x, y)).hasAccident() )
					g.fillRect( getGridX( x ), getGridY( y ), this.gridAmplifier, this.gridAmplifier);

			}
		}
		
		g.setColor(new Color(255, 153, 51));
		for (int x = 0; x <= this.gridSize; x++) {
			for (int y = 0; y <= this.gridSize; y++) {

				if( this.environmentGrid.get(GridCell.getIndex(x, y)).hasObstacle() && 
						!this.environmentGrid.get(GridCell.getIndex(x, y)).hasAccident())
					g.fillRect( getGridX( x ), getGridY( y ), this.gridAmplifier, this.gridAmplifier);

			}
		}
		

		// Draw Depot
		g.setColor(Color.green);
		g.fillRect(getGridX(depot.getX()), getGridY(depot.getY()), this.gridAmplifier, this.gridAmplifier);
		
		// Draw pick up
		g.setColor(Color.CYAN);
		g.fillRect(getXReduced(pickUp.getX(), 85), getYReduced(pickUp.getY(), 85), reducedSize(85), reducedSize(85));
		// Draw drop off
		g.setColor(Color.ORANGE);
		g.fillRect(getXReduced(dropOff.getX(), 65), getYReduced(dropOff.getY(), 65), reducedSize(65), reducedSize(65));
		

		// Draw Car
		g.setColor(Color.BLUE);
		

		int [] xPoints = null;
		int [] yPoints = null;

		
		switch(direction) {
		case "north":
			xPoints = new int[]{ getXReduced(car.getX(), 90), (int) (getXReduced(car.getX(), 90) + reducedSize(90)/2), getXReduced(car.getX(), 90) + reducedSize(90) };
			yPoints = new int[]{ getYReduced(car.getY(), 90) + reducedSize(90), getYReduced(car.getY(), 90) , getYReduced(car.getY(), 90) + reducedSize(90)};
			break;
		case "south":
			xPoints = new int[]{ getXReduced(car.getX(), 90), (int) (getXReduced(car.getX(), 90) + reducedSize(90)/2), getXReduced(car.getX(), 90) + reducedSize(90) };
			yPoints = new int[]{ getYReduced(car.getY(), 90), getYReduced(car.getY(), 90) + reducedSize(90), getYReduced(car.getY(), 90)};
			break;
		case "east":
			xPoints = new int[]{ getXReduced(car.getX(), 90), getXReduced(car.getX(), 90) + reducedSize(90), getXReduced(car.getX(), 90)};
			yPoints = new int[]{ getYReduced(car.getY(), 90), (int) (getYReduced(car.getY(), 90) + reducedSize(90)/2), getYReduced(car.getY(), 90) + reducedSize(90)};
			break;
		case "west":
			xPoints = new int[]{ getXReduced(car.getX(), 90)+ reducedSize(90), getXReduced(car.getX(), 90) , getXReduced(car.getX(), 90)+ reducedSize(90)};
			yPoints = new int[]{ getYReduced(car.getY(), 90), (int) (getYReduced(car.getY(), 90) + reducedSize(90)/2), getYReduced(car.getY(), 90) + reducedSize(90)};
			break;
		}
		g.fillPolygon(xPoints, yPoints, 3);
		/*
		g.setColor(Color.white);
		g.fillPolygon(xPoints, yPoints, 4);
		g.fillRect(getXReduced(car.getX(), 75), getYReduced(car.getY(), 75), reducedSize(75), reducedSize(75));
		*/
		
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
		
		
		g.setColor(Color.BLACK);
		for (int x = 0; x <= this.gridSize; x++) {
			for (int y = 0; y <= this.gridSize; y++) {

				if( !this.environmentGrid.get(GridCell.getIndex(x, y)).isVisible())
					g.fillRect( getGridX( x ), getGridY( y ), this.gridAmplifier, this.gridAmplifier);

			}
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
		return x + 50;
	}
	
	private int convertY (int y) {
		return this.getHeight() - y - 50;
	}
	
	private void readReceivedMessage(String message) {
		
		String[] messageArray = message.split(";");
		
		int x,  y;
		String d;
		switch(messageArray[0]) {
			case	"clear": 
				//gridSize = 10;
				this.gridSize = Integer.parseInt( messageArray[1] );
				//System.out.println("clear");
				initGridInformation();
				car = new Coordinate(0, 0);
				depot = new Coordinate(0, 0);
				break;
			case	"gridSize": 
				//System.out.println("gridSize");
				break;
			case	"carLocation":
				//System.out.println("carLocation");
				x = Integer.parseInt( messageArray[1] );
				y = Integer.parseInt( messageArray[2] );
				d = messageArray[3];
				car.setX( x );
				car.setY( y );
				this.direction = d;
				this.environmentGrid.get( GridCell.getIndex(x, y) ).setIsVisible(true);
				if(y < this.gridSize)
					this.environmentGrid.get( GridCell.getIndex(x, y+1) ).setIsVisible(true);
				if(y > 0)
					this.environmentGrid.get( GridCell.getIndex(x, y-1) ).setIsVisible(true);
				if(x < this.gridSize)
					this.environmentGrid.get( GridCell.getIndex(x+1, y) ).setIsVisible(true);
				if(x > 0)
					this.environmentGrid.get( GridCell.getIndex(x-1, y) ).setIsVisible(true);
				break;
			case	"depot":
				//System.out.println("depot");
				depot.setX( Integer.parseInt( messageArray[1] ) );
				depot.setY( Integer.parseInt( messageArray[2] ) );
				break;
			case	"obstacle":
				//System.out.println("obstacle");
				x = Integer.parseInt( messageArray[1] );
				y = Integer.parseInt( messageArray[2] );
				this.environmentGrid.get( GridCell.getIndex(x, y) ).setHasObstacle(true);
				this.environmentGrid.get( GridCell.getIndex(x, y) ).setIsVisible(true);
				//this.obstacles.add( new Coordinate(x, y) );
				break;
			case	"cant_avoid_obstacle":
				x = Integer.parseInt( messageArray[1] );
				y = Integer.parseInt( messageArray[2] );
				this.environmentGrid.get( GridCell.getIndex(x, y) ).setAccident(true);
				break;
			case	"pickUp":
				//System.out.println("pickUp");
				x = Integer.parseInt( messageArray[1] );
				y = Integer.parseInt( messageArray[2] );
				this.pickUp.setX( x );
				this.pickUp.setY( y ); 
				this.environmentGrid.get( GridCell.getIndex(x, y) ).setIsVisible(true);
				break;
			case	"dropOff":
				//System.out.println("dropOff");
				x = Integer.parseInt( messageArray[1] );
				y = Integer.parseInt( messageArray[2] );
				this.dropOff.setX( x );
				this.dropOff.setY( y ); 
				this.environmentGrid.get( GridCell.getIndex(x, y) ).setIsVisible(true);
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
				//System.out.println("Received: " + sentence);
				
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



