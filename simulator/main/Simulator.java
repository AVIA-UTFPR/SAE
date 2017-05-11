package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import java.net.*;
import java.util.HashMap;
import java.util.Map;

import util.Coordinate;
import util.GridCell;





public class Simulator extends JComponent{

	private static final long serialVersionUID = 1L;
	
	private JFrame window = new JFrame();
	
	private int width = 800;
	private int height = 800;
	
	private int gridSize = 5;
	private int gridAmplifier = 50;

	private Coordinate car = new Coordinate(0, 0); // Coordenada onde o agente está localizado.
	private String direction = "north"; // Direção em qual o agente está se movendo.
	private boolean ableToMove = true; // Indica se o veículo é capaz de se mover.
	
	
	private Coordinate depot = new Coordinate(0, 0);
	private Map<String, GridCell> environmentGrid;


	private Coordinate pickUp = new Coordinate(gridSize-1, gridSize-1); // Indica a coordenada que é o ponto de pegada do passageiro na corrida atual.
	private boolean canPickUp = true; // Informa se é possível atingir na coordenada pickUp.
	private Coordinate dropOff = new Coordinate(gridSize, gridSize); // Indica a coordenada que é o destino da corrida atual.
	private boolean canDropOff = true; // Informa se é possível chegar na coordenada dropOff.
	
	// Definição das cores para cada nível de dano que um obstáculo pode causar.

	private Color high = new Color(255,0,0);
	private Color moderate = new Color(255,102,102);
	private Color low = new Color(255,204,204);
	
	
	private static DatagramSocket server;
	
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

		car = new Coordinate(0, 0);
		depot = new Coordinate(0, 0);

		environmentGrid = new HashMap<String, GridCell>();
		
		for (int x = 0; x <= gridSize; x++) {
			for (int y = 0; y <= gridSize; y++) {

				String cellName = GridCell.getIndex(x, y);
				environmentGrid.put(cellName, new GridCell(x, y, false, false));

			}
		}
	}
	
	protected void drawLineGrid(Graphics g) {
		// Desenha as linhas do grid
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
	
	@Override
	protected void paintComponent(Graphics g) {
		
		if (!this.ableToMove) {
			g.setColor(Color.RED);
			for (int x = 0; x <= this.gridSize; x++) {
				for (int y = 0; y <= this.gridSize; y++) {
					g.fillRect( getGridX( x ), getGridY( y ), this.gridAmplifier, this.gridAmplifier);

				}
			}
			g.setColor(Color.WHITE);
			g.fillRect( getGridX( car.getX() ), getGridY( car.getY() ), this.gridAmplifier, this.gridAmplifier);
			
			drawLineGrid(g);
			return;
		}
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, this.width, this.height);
		
		boolean isVisible, hasObstacle, hasDamageLevel;
		
		for (int x = 0; x <= this.gridSize; x++) {
			for (int y = 0; y <= this.gridSize; y++) {
				
				isVisible = this.environmentGrid.get(GridCell.getIndex(x, y)).isVisible();
				hasObstacle = this.environmentGrid.get(GridCell.getIndex(x, y)).hasObstacle();
				hasDamageLevel = !this.environmentGrid.get(GridCell.getIndex(x, y)).getDamageLevel().equals("none");
				
				if(hasObstacle) {
					g.setColor(Color.orange);
					g.fillRect( getGridX( x ), getGridY( y ), this.gridAmplifier, this.gridAmplifier);
				}
				
				if( hasDamageLevel ){
					String damageLevel = this.environmentGrid.get(GridCell.getIndex(x, y)).getDamageLevel();
					switch(damageLevel) {
					case "high":
						g.setColor(high);
						break;
					case "moderate":
						g.setColor(moderate);
						break;
					case "low":
						g.setColor(low);
						break;
					}
					g.fillRect( getGridX( x ), getGridY( y ), this.gridAmplifier, this.gridAmplifier);
				}
				

				if( !isVisible ) {
					g.setColor(Color.BLACK);
					g.fillRect( getGridX( x ), getGridY( y ), this.gridAmplifier, this.gridAmplifier);
				}

			}
		}

		// Draw Depot
		g.setColor(Color.green);
		g.fillRect(getGridX(depot.getX()), getGridY(depot.getY()), this.gridAmplifier, this.gridAmplifier);

		// Draw pick up
		g.setColor(Color.CYAN);
		g.fillOval(getXReduced(pickUp.getX(), 80), getYReduced(pickUp.getY(), 80), reducedSize(80), reducedSize(80));
		
		// Draw drop off
		g.setColor(Color.yellow);
		g.fillOval(getXReduced(dropOff.getX(), 70), getYReduced(dropOff.getY(), 70), reducedSize(70), reducedSize(70));
		

		g.setColor(Color.RED);
		if( !this.canPickUp ) {
			g.drawLine(getGridX(pickUp.getX()), getGridY(pickUp.getY()), getGridX(pickUp.getX()) + this.gridAmplifier, getGridY(pickUp.getY()) + this.gridAmplifier);
			g.drawLine(getGridX(pickUp.getX()), getGridY(pickUp.getY()) + this.gridAmplifier, getGridX(pickUp.getX()) + this.gridAmplifier, getGridY(pickUp.getY()));
		}
		if( !this.canDropOff ) {
			g.drawLine(getGridX(dropOff.getX()), getGridY(dropOff.getY()), getGridX(dropOff.getX()) + this.gridAmplifier, getGridY(dropOff.getY()) + this.gridAmplifier);
			g.drawLine(getGridX(dropOff.getX()), getGridY(dropOff.getY()) + this.gridAmplifier, getGridX(dropOff.getX()) + this.gridAmplifier, getGridY(dropOff.getY()));
		}

		
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
		
		// Desenha as linhas do grid
		drawLineGrid(g);
		
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
	
	
	/*
	 * O método readReceivedMessage realiza o processamente da mensagem.
	 * Por meio da interpretação da mensagem, é atualizado as variáveis do simulador para sua exibição gráfica.
	 * 
	 * A mensagem recebida é do tipo String, onde cada argumento da mensagem é separado por ponto-e-vírgula (;).
	 * O processamento da mensagem é feito da seguinte forma: 
	 * A mensagem é separada e tranformada em um array de String, messageArray, 
	 * onde a primeira posição identifica o tipo da mensagem, messageArray[0]. 
	 * Para cada tipo de mensagem, uma atualização diferente é realizada. A identificação de cada mensagem é feito por meio de um switch().
	 */
	private void readReceivedMessage(String message) {
		
		String[] messageArray = message.split(";");
		String switchMessage = messageArray[0];
		
		int x = 0,  y = 0;
		String d;
		
		if( !(switchMessage.equals("clear") || switchMessage.equals("removeObstacleDamage")) ) {
			x = Integer.parseInt( messageArray[1] );
			y = Integer.parseInt( messageArray[2] );
			this.environmentGrid.get( GridCell.getIndex(x, y) ).setIsVisible(true);
		}
		
		switch(switchMessage) {
			case	"clear": 
				this.gridSize = Integer.parseInt( messageArray[1] );
				this.ableToMove = true;
				initGridInformation();
				break;
			case	"depot":
				depot.setX( Integer.parseInt( messageArray[1] ) );
				depot.setY( Integer.parseInt( messageArray[2] ) );
				break;
			case	"carLocation":
				d = messageArray[3];
				car.setX( x );
				car.setY( y );
				this.direction = d;
				if(y < this.gridSize)
					this.environmentGrid.get( GridCell.getIndex(x, y+1) ).setIsVisible(true);
				if(y > 0)
					this.environmentGrid.get( GridCell.getIndex(x, y-1) ).setIsVisible(true);
				if(x < this.gridSize)
					this.environmentGrid.get( GridCell.getIndex(x+1, y) ).setIsVisible(true);
				if(x > 0)
					this.environmentGrid.get( GridCell.getIndex(x-1, y) ).setIsVisible(true);
				break;
			case	"obstacle":
				this.environmentGrid.get( GridCell.getIndex(x, y) ).setHasObstacle(true);
				break;
			case	"pickUp":
				/*
				 * Atualiza o ponto de pegada do passeiro.
				 */
				this.pickUp.setX( x );
				this.pickUp.setY( y ); 
				this.canPickUp = true;
				
				break;
			case	"dropOff":
				/*
				 * Atualiza o ponto de destino do passeiro.
				 */
				this.dropOff.setX( x );
				this.dropOff.setY( y ); 
				this.canDropOff = true;
				break;
				
			case "obstacleDamage":
				/*
				 * Com base nas coordenadas x e y informadas, a posição (x,y) do grid interno (environmentGrid) 
				 * é atualizado com a informações sobre o nível de dado definido pelo ambiente do agente.
				 * */ 
				this.environmentGrid.get( GridCell.getIndex(x, y) ).setDamageLevel(messageArray[3]);
				break;
			case "removeObstacleDamage":
				/*
				 * Define o valor "none" para todas as coordenadas do do grid interno (environmentGrid). 
				 * */ 
				for (int X = 0; X <= this.gridSize; X++) {
					for (int Y = 0; Y <= this.gridSize; Y++) {
						this.environmentGrid.get( GridCell.getIndex(X, Y)).setDamageLevel("none");
					}
				}
				break;
			case "refuseRide":
				/*
				 * Quando o agente recusa terminar uma corrida.
				 * Caso pick_up: Ambas coordenadas de pickUp e dropOff exibem um 'X' indicando que não é possível chegar naquela localidade 
				 * 				(são atualizas as variáveis canPickUp e canDropOff).
				 * Caso drop_off: A coordenada dropOff exibe um 'X' indicando que não é possível chegar naquela localidade
				 * 				(é atualizada a variável canDropOff).
				 * Caso car_unavailable: Todas as coordendas do grid são preenchidas com a cor vermelha. 
				 * 				
				 */
				String type = messageArray[3];
				switch(type) {
				case "pick_up":
					this.canPickUp = false;
					this.canDropOff = false;
					break;
				case "drop_off":
					this.canDropOff = false;
					break;
				case "car_unavailable":
					this.ableToMove = false;
					break;
				}
				break;
			default:
				System.out.println("Erro");
				System.out.println(messageArray[0]);
		}
		
		repaint();
	}
	
	
	public static void main(String args[]){
		
		Simulator sim = new Simulator();
		
		try {

			server = new DatagramSocket(9999);
			byte[] receive = new byte[1024];
			
			while(true)
			{
				DatagramPacket receivePacket = new DatagramPacket(receive, receive.length); // Pacote Recebido
				server.receive(receivePacket);
				
				String sentence = new String( receivePacket.getData() );
				sim.readReceivedMessage(sentence);
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	 }
}


