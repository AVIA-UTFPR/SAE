package _005_dont_crash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//import ail.mas.DefaultEnvironment;
import ail.mas.DefaultEnvironmentwRandomness;
import ail.mas.MAS;
import ail.syntax.Action;
import ail.syntax.NumberTermImpl;
import ail.syntax.Predicate;
import ail.syntax.Unifier;
import ail.util.AILexception;
import ajpf.util.choice.Choice;
import main.Client;
import util.Coordinate;
import util.GridCell;
import util.Passenger;
import util.Util;

public class AutonomousCarEnv extends DefaultEnvironmentwRandomness { //DefaultEnvironmentwRandomness
	
	// Variável utilizada para o ambiente decidir qual é a chance de um acidente acontecer 
	private Choice<Boolean> accidentChance;
	
	// Variável utilizada para o ambiente decidir definir o nível de dano causado em uma colisão.
	private Choice<String> damageLevel;
	

	// Informações sobre o agente Carro
	private Coordinate car = new Coordinate(1, 1); // Posição Inicial do agente
	private Coordinate depotLocation = new Coordinate(0, 0); // Localização do Depósito
	private String currentDirection = "north"; // Direção default do carro

	// Informações sobre a Grid do ambiente
	private int nObstacles = 10; // Número total de obstáculos estáticos 
	private int minGridSize = 0; // Utilizado somente para a programação. Indica que o grid inicia nas posições (0,0)
	private int maxGridSize = 10; // Tamanho máximo da grid.

	private Map<String, GridCell> environmentGrid; // Mapeamento de todas as posições do ambiente
	 
	// Armazena informações sobre os predicadores relacionados com um obstáculo que não pode ser desviado.
	private ArrayList<Predicate> obstacleDamageRelated= new ArrayList<>();

	// Informações sobre os passageiros
	private ArrayList<Passenger> passengers = new ArrayList<Passenger>(); // Lista de passageiros
	private int maxInitPassengers = 5; // Número de passageiros gerados randômicamente
	private Passenger currentPassenger = new Passenger(0, 0, 0, 0); 

	
	// Informações sobre o simulador gráfico do ambiente.
	Client client = new Client(); // Classe responsável pela conexão com o simulador
	private boolean simulate = true; // Informa esta classe se deve mandar informações para o simulador
	private int waitTime = 700; 
	// Tempo de espera padrão para que o ambiente atualize novamente. 
	//Têm o propósito de parar o processamente do ambiente e do agente para que possa ser possível acompanhar a execução pelo simulador.


	// Configurações Iniciais do Ambiente
	@Override
	public void setMAS(MAS m) {
		super.setMAS(m);

		
		// Inicializa a variável accidentChance com a probabilidade de um acidente ocorrer
		// Nota: Há pré-condições para que a possiblidade de um acidente seja considerada
		/*
		 * A probabilidade de um acidente acontecer no cenário determinado é de 10%.
		 */
		accidentChance = new Choice<Boolean>(m.getController());
		accidentChance.addChoice(0.0, false); // Probabilidade negativa
		accidentChance.addChoice(0.1, true);  // Probalidade de um acidente ocorrer.
		
		// Inicializa a variável damageLevel com as probalidades do dano causado por um acidente.
		/*
		 * Nível de dano:
		 * 1 - Colisão Grave (high) - Chances de ocorrência 10%: Grande estrago no veículo e ferimentos graves dos passageiros do veículo.
		 * 2 - Colisão Média (moderate) - Chances de ocorrência 25%: Estrago no veículo,e possivelmente causa ferimentos leves nos passageiros do veículo.
		 * 3 - Colisão Leve (low) - Chances de ocorrência 65%: Causa apenas arranhões no veículo, nenhum passageiro se fere.
		 */
		damageLevel = new Choice<String>(m.getController());
		damageLevel.addChoice(0.125, "high");
		damageLevel.addChoice(0.25, "moderate");
		damageLevel.addChoice(0.0625, "low");
		
		// Define as informações inicias sobre o  ambiente. Mapemento das posições, lista de passageiros e lista de obstáculos.
		this.environmentGrid = initGridInformation();
		initPassengerList();
		initObstacles();
		
		/*
		 * FAR_1
		this.car.setX(0);
		this.car.setX(0);
		 */

		/*
		 * FAR_6
		this.car.setX(1);
		this.car.setY(0);
		passengers.add(new Passenger("FAR_6", 1, 1, 1, 2));
		passengers.add(new Passenger("FAR_6", 1, 3, 1, 4));
		 */

		/*
		 * FAR_7
		this.car.setX(1);
		this.car.setY(0);
		passengers.add(new Passenger("FAR_7", 1, 1, 2, 1));
		passengers.add(new Passenger("FAR_7", 1, 2, 2, 1));
		passengers.add(new Passenger("FAR_7", 1, 1, 1, 2));
		environmentGrid.get(GridCell.getIndex(1,2)).setHasObstacle(true);
		 */
		
		/*
		 * FAR_8_1_8_2
		 * FAR_9_1_9_2
		this.car.setX(1);
		this.car.setY(0);
		passengers.add(new Passenger("FAR_8_1_8_2", 1, 1, 1, 2));
		 */
		
		/*
		 * FAR_8_1_8_3
		this.car.setX(1);
		this.car.setY(0);
		passengers.add(new Passenger("FAR_8_1_8_3", 1, 2, 1, 3));
		environmentGrid.get(GridCell.getIndex(1,2)).setHasObstacle(true);
		 */
		
		/*
		 * ADD_OBSTACLE_1
		 * ADD_OBSTACLE_2
		this.car.setX(1);
		this.car.setY(0);
		passengers.add(new Passenger("ADD_OBSTACLE", 1, 1, 2, 1));
		environmentGrid.get(GridCell.getIndex(1,2)).setHasObstacle(true);
		 */
		
		
		/*
		 * Cenário XX - Ocorrência de um acidente - Todos obstáculos são mapeados pelo ambiente
		 * */
		
		// Envia informações para o simulador
		if(simulate) {
			client.sendMessage( client.convertArray2String( new String[] {"clear", String.valueOf(maxGridSize)} ) );
			try {
				TimeUnit.MILLISECONDS.sleep(100);
			} catch(Exception e) {
				System.err.println(e);
			}
		}


		// Garante que a posição inicial do agente e o depósito não possuam um obstáculo.
		environmentGrid.get(GridCell.getIndex(car.getX(), car.getY())).setHasObstacle(false);
		environmentGrid.get(GridCell.getIndex(depotLocation.getX(), depotLocation.getY())).setHasObstacle(false);

		showAllPassengerList();

	}

	private void showAllPassengerList() {
		for (Passenger passenger : passengers) {
			System.err.println(String.format("Passenger %s: PickUp(%d,%d) - Drop Off (%d,%d)", passenger.getName(),
					passenger.getPickUpX(), passenger.getPickUpY(), passenger.getDropOffX(), passenger.getDropOffY()));
		}
	}

	// Inicializa o mapeamento das posições do ambiente
	private Map<String, GridCell> initGridInformation() {

		Map<String, GridCell> grid = new HashMap<String, GridCell>();
		
		for (int x = minGridSize; x <= maxGridSize; x++) {
			for (int y = minGridSize; y <= maxGridSize; y++) {

				String cellName = GridCell.getIndex(x, y);
				grid.put(cellName, new GridCell(x, y, false));

			}
		}

		return grid;
	}

	// Inicializa a lista de passageiros do ambiente
	private void initPassengerList() {

			for (int i = 0; i < maxInitPassengers; i++) {
				int pickUpX = (int) (Math.random() * (maxGridSize+1));
				int pickUpY = (int) (Math.random() * (maxGridSize+1));
				int dropOffX = (int) (Math.random() * (maxGridSize+1));
				int dropOffY = (int) (Math.random() * (maxGridSize+1));
				passengers.add(new Passenger("" + i, pickUpX, pickUpY, dropOffX, dropOffY));
				
			}
	}

	// Inicializa a localização de obstáculos randômicos no ambiente. 
	private void initObstacles() {
		
		int x, y;
		for(int i = 0; i < this.nObstacles; i++) {
			x = (int) (Math.random() * (maxGridSize+1));
			y = (int) (Math.random() * (maxGridSize+1));
			environmentGrid.get(GridCell.getIndex(x, y)).setHasObstacle(true);
		}
		
	}

	
	public Unifier executeAction(String agName, Action act) throws AILexception {

		String actionName = act.getFunctor();
		
		/*
		 * Aqui são identificas as ações realizadas pelo agente.
		 * Para cada ação, os seus argumentos do predicado são 
		 * atribuidos a variáveis e enviados para uma função, 
		 * que realiza as operações necessárias.
		 * */
		

		int fromX, fromY, x, y, destinationX, destinationY;
		//Coordinate from, currentPosition, destination;
		String direction;
		
		switch(actionName) {
		case "drive": 
			fromX = Util.getIntTerm(act.getTerm(0));
			fromY = Util.getIntTerm(act.getTerm(1));
			direction = act.getTerm(2).getFunctor();
			destinationX = Util.getIntTerm(act.getTerm(3));
			destinationY = Util.getIntTerm(act.getTerm(4));
			
			drive(agName, new Coordinate(fromX, fromY), direction, new Coordinate(destinationX, destinationY));
			break;
		case "compass": 
			x = Util.getIntTerm(act.getTerm(0));
			y = Util.getIntTerm(act.getTerm(1));
			compass(agName, new Coordinate(x,y));
			break;
		case "localize": 
			localize(agName);
			break;
		case "get_ride": 
			getRide(agName);
			break;
		case "refuse_ride": 
			String refuseType = act.getTerm(0).getFunctor();
			
			refuseRide(agName, refuseType);
			break;
		case "park": 
			String parkType = act.getTerm(0).getFunctor();
			
			park(agName, parkType);
			break;
		case "no_further_from": 
			fromX = Util.getIntTerm(act.getTerm(0));
			fromY = Util.getIntTerm(act.getTerm(1));
			x = Util.getIntTerm(act.getTerm(2));
			y = Util.getIntTerm(act.getTerm(3));
			destinationX = Util.getIntTerm(act.getTerm(4));
			destinationY = Util.getIntTerm(act.getTerm(5));
			
			noFurtherFrom(agName, new Coordinate(fromX, fromY), new Coordinate(x, y), new Coordinate(destinationX, destinationY));
			break;
		case "call_emergency": 
			x = Util.getIntTerm(act.getTerm(0));
			y = Util.getIntTerm(act.getTerm(1));
			callEmergency(agName, new Coordinate(x, y));
			break;
		case "get_assistance": 
			x = Util.getIntTerm(act.getTerm(0));
			y = Util.getIntTerm(act.getTerm(1));
			getAssistance(agName, new Coordinate(x, y));
			break;
		case "a_m": 
			// Exibe mensagens
			String am = act.getTerm(0).getFunctor();
			x = Util.getIntTerm(act.getTerm(1));
			y = Util.getIntTerm(act.getTerm(2));
			direction = act.getTerm(3).getFunctor();
			destinationX = Util.getIntTerm(act.getTerm(4));
			destinationY = Util.getIntTerm(act.getTerm(5));
			
			System.err.println(am + " - "+ direction +": ("+x+","+y+") to ("+destinationX+","+destinationY+")");
			break;
		case "message": 
			System.err.println(String.format("%s says %s", agName, act.getTerm(0)));
			break;
		case "honk":
			System.err.println("HONK"); 
			break;
			default:
		}

		return super.executeAction(agName, act);

	}

	/*
	 * A função drive recebe os seguintes valores:
	 * 	String agName: Nome do agente
	 *  Coordinate from: Coordenada do ponto de partida do agente
	 *  String direction: Direção para qual o agente deseja se mover.
	 *  Coordinate destination: Coordenada do destino do agente
	 *  
	 *  Baseado na direção que o agente quer se mover, o ambiente calcula as novas coordenadas e envia novas percepções para o agente.
	 *  Na sequência, o ambiente cria uma percepção 'adapt_from_to' baseado na posição atual 
	 *  e uma percepção 'moved_from_to' na nova posição.
	 *  
	 *  Por fim, a função drive invoca o método updateLocation para atualizar as perpeções da posição do agente.
	 * */
	private void drive(String agName, Coordinate from, String direction, Coordinate destination) {
		
		this.currentDirection = direction;
		
		Coordinate newPosition = getDirectionCoordinate(direction, car);

		Predicate adaptedFromTo = new Predicate("adapt_from_to");
		Predicate movedFromTo = new Predicate("moved_from_to");
		
		addFromTo(agName, adaptedFromTo, from, car, direction, destination);
		addFromTo(agName, movedFromTo, from, newPosition, direction, destination);
		
		System.err.println(String.format("Moving %s from (%d,%d) to (%d,%d)", direction, car.getX(), car.getY(), newPosition.getX(), newPosition.getY()));

		updateLocation(agName, car, newPosition);
	}
	
	/*
	 * A função updateLocation possui os seguintes argumentos:
	 * String agName: Nome do agente
	 * Coordinate currentPosition: Coordenadas da posição atual do agente
	 * Coordinate newPosition: Coordenadas da nova posição do agente
	 * 
	 * Remove a percepção sobre a posição antiga do agente,
	 * atualiza a variável interna do ambiente que armazena a posição do agente, e adiciona a nova posição do agente.
	 * Invoca o método  scanSurroundings com a nova posição do agente para verificar se há obstásculos ao redor do agente.
	 * 
	 */
	private void updateLocation(String agName, Coordinate currentPosition, Coordinate newPosition) {	
		if(simulate) {
			client.sendMessage( client.convertArray2String( new String[] 
					{"carLocation", String.valueOf(newPosition.getX()), String.valueOf(newPosition.getY()), this.currentDirection} ) );
		}
		
		Predicate oldLocation = new Predicate("at");
		oldLocation.addTerm(new NumberTermImpl(currentPosition.getX()));
		oldLocation.addTerm(new NumberTermImpl(currentPosition.getY()));

		Predicate at = new Predicate("at");
		at.addTerm(new NumberTermImpl(newPosition.getX()));
		at.addTerm(new NumberTermImpl(newPosition.getY()));

		car.setX(newPosition.getX());
		car.setY(newPosition.getY());

		scanSurroundings(agName, newPosition);

		removePercept(agName, oldLocation); 
		addPercept(agName, at);
	}

	/*
	 * O método scanSurroundings possui os seguintes argumentos: 
	 * String agName: Nome do agente
	 * Coordinate currentPosition: Coordenadas com a posição que será verifica seus arredores.
	 * 
	 * scanSurroundings têm como função principal verificar a existência de obstáculos em qualquer direção que o agente possa vir a se movimentar.
	 * Como o ambiente é estático, o agente mantêm suas crenças sobre obstáculos conhecidos.
	 * 
	 * Aqui é verificado se o cenário em que o veículo agente se encontra é possível que ocorra um acidente.
	 * Caso uma colisão seja inevitável, sé verificado os níveis de dano causado pela colisão com cada obstáculo.
	 * 
	 * A verificação do cenário é feita da seguinte forma:
	 * Se houver no mínimo três obstáculos ao redor do agente 
	 * (Nota: Neste caso, três resultados da função verifyObstacle que não sejam o caracter 'N'), 
	 * é gerado uma possibilidade randômicamente para que um acidente ocorra.
	 * Neste cenário, o agente não pode direcionar-se para qualquer posição sem que haja uma colisão. 
	 * Isto é, não pode 'dar ré' e voltar pelo caminho onde este veio.
	 * Na ocorrência de um acidente (accidentChance é verdadeiro), o ambiente classifica os obstáculos por níveis de destruição.
	 * E por fim, adiciona uma percepção que não é possível desviar dos obstáculos a partir da posição atual. 
	 * 
	 * Para controle interno do ambiente, o predicado unavoidableCollision é inserido em um ArrayList obstacleDamageRelated 
	 * para que possa ser removido futuramente quando necessário.
	 * 
	 * Simulador: Caso o simulador esteja ativo, o ambiente é forçado a dormir (sleep) por 2 segundos, 
	 * para que seja possível a visualização  gráfica da classificação dos níveis de dano de cada obstáculo.
	 * 
	 * */
	private void scanSurroundings(String agName, Coordinate currentPosition) {
		
		char north = verifyObstacle(agName, "north", currentPosition);
		char south = verifyObstacle(agName, "south", currentPosition);
		char east =  verifyObstacle(agName, "east",  currentPosition);
		char west =  verifyObstacle(agName, "west",  currentPosition);
		
		int obstacleAround = 0;

		if(north != 'N') 
			obstacleAround++;
		if(south != 'N') 
			obstacleAround++;
		if(east != 'N') 
			obstacleAround++;
		if(west != 'N') 
			obstacleAround++;
		
		if(obstacleAround >= 3 && simulate) {
			
			if(accidentChance.get_choice()) {

				addObstacleDamage(agName, north, "north", currentPosition);
				addObstacleDamage(agName, south, "south", currentPosition);
				addObstacleDamage(agName, east, "east", currentPosition);
				addObstacleDamage(agName, west, "west", currentPosition);
				
				Predicate unavoidableCollision = new Predicate("unavoidable_collision");
				unavoidableCollision.addTerm(new NumberTermImpl(currentPosition.getX()));
				unavoidableCollision.addTerm(new NumberTermImpl(currentPosition.getY()));

				obstacleDamageRelated.add(unavoidableCollision);
				
				addPercept(agName, unavoidableCollision);
				
				if(simulate) {
					try {
						TimeUnit.MILLISECONDS.sleep(waitTime);
					} catch(Exception e) {
						System.err.println(e);
					}
				}
				
			}
		}
	}
	
	
	/* 
	 * O método addObstacleDamage recebe os seguintes argumentos:
	 * char typeObstacle: Que identifica o tipo do obstáculo
	 * 		'N' - Não há nenhum obstáculo nas coordenadaas currentPosition.
	 * 		'O' - Há um obstáculo em currentPosition.
	 * 		'F' - Há um obstáculo em currentPosition, mas este não é em uma posição esta mapeada pelo ambiente.
	 * String direction: Direção em relação a currentPosition que o obstáculo está localizado.
	 * Coordinate currentPosition: Coordenadas da posição que está sendo verificada seus arredores.
	 * 
	 * A função  addObstacleDamage tem como função inserir percepções sobre o nível de dano causado por possíveis colisões do veículo.
	 * O único caso em que não é inserido informações sobre o dano do veículo
	 * é quando o obstáculo é uma das fronteiras do mapeamento do ambiente.
	 * É inserido o predicado obstacle_damage com os argumentos sobre a posição atual, a direção em que o obstáculo se encontra, 
	 * e o nível de dano causado.
	 * 
	 * É também adiciona perpepções de obstáculo para as coordenadas que não possuiam um obstáculo previamente. 
	 * De forma que se o agente mover-se para aquelas coordenadas, o mesmo irá processar aquilo como uma colisão. 
	 * Além disso, é adicionado uma percepção informando para o agente que tal obstáculo é temporário.
	 * 
	 * Para controle interno do ambiente, o predicado obstacleDamage é inserido em um ArrayList obstacleDamageLevel 
	 * para que possa ser removido futuramente se necessário.
	 * 
	 * É enviado mensagem para o simulador a partir desta função depende se o obstáculo é fronteira ou não (i.e., typeObstacle != 'F').
	 * São enviados os seguintes dados: Coordenadas da posição atual do agente, 
	 * direção do obstáculo em relação a posição atual e nível de dano causado em uma possível colisão.
	 * A interpretação dos dados enviados é feita pelo simulador.
	 * 
	 */
	private void addObstacleDamage(String agName, char typeObstacle, String direction, Coordinate currentPosition) {
		String currentDamageLevel = " ";
		
		if(typeObstacle != 'F') {
			currentDamageLevel = this.damageLevel.get_choice();
			
			Predicate obstacleDamage = new Predicate("obstacle_damage");
			obstacleDamage.addTerm(new NumberTermImpl(currentPosition.getX()));
			obstacleDamage.addTerm(new NumberTermImpl(currentPosition.getY()));
			obstacleDamage.addTerm(new Predicate(direction));
			
			if (typeObstacle == 'N') {
				Coordinate directionPosition = getDirectionCoordinate(direction, currentPosition);
				
				Predicate newObstacle = addObstacle("center", directionPosition);
				addPercept(agName, newObstacle);
				
				Predicate tempObstacle = new Predicate("temp_obstacle");
				tempObstacle.addTerm(new NumberTermImpl(directionPosition.getX()));
				tempObstacle.addTerm(new NumberTermImpl(directionPosition.getY()));
				addPercept(agName, tempObstacle);

				obstacleDamageRelated.add(newObstacle);
				obstacleDamageRelated.add(tempObstacle);
				
			}
			/*
			if(direction.equals("south")) 
				currentDamageLevel = "low";
			else
				currentDamageLevel = "high";
			*/

			obstacleDamage.addTerm(new Predicate( currentDamageLevel ));
			
			System.err.println("Damage obstacle: " + obstacleDamage);

			if(simulate) {
				Coordinate directionPosition = getDirectionCoordinate(direction, currentPosition) ;
				
				client.sendMessage( client.convertArray2String( new String[] {"obstacleDamage", 
						String.valueOf(directionPosition.getX()),
						String.valueOf(directionPosition.getY()),
						currentDamageLevel
				} ) );
			}
			obstacleDamageRelated.add(obstacleDamage);
			addPercept(agName, obstacleDamage);
		}
		
	}
	
	
	/*
	 * O método verifyObstacle possui os seguintes argumentos: 
	 * String agName: Nome do agente
	 * String direction: Direção que deseja ser verificada
	 * Coordinate currentPosition: Posição atual do agente
	 * 
	 * A função verifica se há um obstáculo em qualquer direção (norte, sul, leste e oeste) em relação a currentPosition.
	 * Caso sim, é adicionado novas perpeções no agente referentes aos obstáculos 
	 * e em qual direção (north, south, east, west) está localizado em relação a posição atual do agente.
	 * Nas coordenadas do próprio obstáculo, é adicionado uma percepção informando que naquela coordenada existe um obstáculo (center).
	 * 
	 * As fronteiras do ambiente, ou seja, coordenadas que não são mapeadas pelo ambiente, são consideradas como obstáculos. 
	 * 
	 * O retorna da função é um char:
	 * 'N' - Não há nenhum obstáculo nas coordenadaas surrondingPosition.
	 * 'O' - Há um obstáculo em surrondingPosition.
	 * 'F' - Há um obstáculo em surrondingPosition, mas este não é em uma posição esta mapeada pelo ambiente.
	 * */
	private char verifyObstacle(String agName, String direction, Coordinate currentPosition) {
		
		Coordinate surroundingPosition = getDirectionCoordinate(direction, currentPosition);
		
		char typeObstacle = 'N';
		boolean hasObstacle = false;
		

		if (	surroundingPosition.getX() < minGridSize || surroundingPosition.getX() > maxGridSize || 
				surroundingPosition.getY() < minGridSize || surroundingPosition.getY() > maxGridSize) {
			hasObstacle = true;
			typeObstacle = 'F';
		} else {
			if (environmentGrid.get(GridCell.getIndex(surroundingPosition.getX(), surroundingPosition.getY())).hasObstacle()) {
				hasObstacle = true;
				typeObstacle = 'O';
				
				if(simulate) {
					client.sendMessage( client.convertArray2String( new String[] {"obstacle", 
							String.valueOf(surroundingPosition.getX()), String.valueOf(surroundingPosition.getY())} ) );
				}
			}
		}
		
		if(hasObstacle) {
			addPercept(agName, addObstacle(direction, currentPosition));
			addPercept(agName, addObstacle("center", surroundingPosition));
		}
		
		return typeObstacle;
	}

	/*
	 * O método addObstacle possui os seguintes argumentos:
	 * String direction: Em qual direção o obstáculo está localizado
	 * Coordinate coordinate: Coordenada do obstáculo
	 * 
	 * Esta função cria o predicado obstacle com os argumentos de direção, eixo X e eixo Y (representados por um objeto Coordinate) do obstáculo,
	 * e adiciona uma perpeção ao agente.
	 * 
	 * */
	private Predicate addObstacle(String direction, Coordinate coordinate) {
		Predicate obstacle = new Predicate("obstacle");
		obstacle.addTerm(new Predicate(direction));
		obstacle.addTerm(new NumberTermImpl(coordinate.getX()));
		obstacle.addTerm(new NumberTermImpl(coordinate.getY()));
		
		return obstacle;
	}

	
	/*
	 * O método addFromTo possui os seguintes argumentos:
	 * String agName: Nome do agente
	 * Predicate predicate: Nome do predicado que será criado. (adapt_from_to ou moved_from_to)
	 * Coordinate from: Coordenadas do ponto de partida do agente na rota atual.
	 * Coordinate current: Coordenadas posição atual do agente.
	 * String direction: Direção em qual o agente deseja se mover.
	 * Coordinate destination: Coordenadas do destino da rota atual.
	 * 
	 * Como os predicados adapt_from_to e moved_from_to possuem argumentos semelhantes, 
	 * esta função têm como objetivo inserir ambos os tipos de percepções no agente.
	 * 
	 * */
	private void addFromTo(String agName, Predicate predicate, Coordinate from, Coordinate current, String direction, Coordinate destination) {
		predicate.addTerm(new NumberTermImpl(from.getX()));
		predicate.addTerm(new NumberTermImpl(from.getY()));
		predicate.addTerm(new NumberTermImpl(current.getX()));
		predicate.addTerm(new NumberTermImpl(current.getY()));
		predicate.addTerm(new Predicate(direction));
		predicate.addTerm(new NumberTermImpl(destination.getX()));
		predicate.addTerm(new NumberTermImpl(destination.getY()));
		
		addPercept(agName, predicate);
	}

	
	private void addAdaptAndMovedFromTo(
			String agName, Coordinate from, Coordinate currentPosition, 
			Coordinate destination, String moved, String adapt) {
		
		Predicate adaptFromTo = new Predicate("adapt_from_to");
		Predicate movedFromTo = new Predicate("moved_from_to");
		
		addFromTo(agName, adaptFromTo, from, getDirectionCoordinate(moved, currentPosition), adapt, destination);
		addFromTo(agName, movedFromTo, from, getDirectionCoordinate(moved, currentPosition), moved, destination);
		
		
	}
	
	private void noFurtherFrom(String agName, Coordinate from, Coordinate currentPosition, Coordinate destination) {
			
		
		System.err.println(
				"Can't come here: at("+currentPosition.getX()+","+currentPosition.getY()+") "
				+ "to get to ("+destination.getX()+","+destination.getY()+")");
				

		addAdaptAndMovedFromTo(agName, from, currentPosition, destination, "north", "south");
		addAdaptAndMovedFromTo(agName, from, currentPosition, destination, "south", "north");
		addAdaptAndMovedFromTo(agName, from, currentPosition, destination, "east", "west");
		addAdaptAndMovedFromTo(agName, from, currentPosition, destination, "west", "east");
		
		
		Predicate noFurther = new Predicate("no_further");
		noFurther.addTerm(new NumberTermImpl(from.getX()));
		noFurther.addTerm(new NumberTermImpl(from.getY()));
		noFurther.addTerm(new NumberTermImpl(currentPosition.getX()));
		noFurther.addTerm(new NumberTermImpl(currentPosition.getY()));
		noFurther.addTerm(new NumberTermImpl(destination.getX()));
		noFurther.addTerm(new NumberTermImpl(destination.getY()));
		addPercept(agName, noFurther);

	}
	
	/*
	 * O método compass recebe os seguintes argumentos:
	 * String agName: Nome do agente.
	 * Coordinate coordinate: Coordenadas do destino do trajeto atual, para as quais deseja se verificar as direções.
	 * 
	 * Todas as crenças referentes a um direção são removidas do agente, 
	 * e verifica-se quais direções o agente deve-se mover para atingir o destino atual.
	 * Após a verificação, novas percepções sobre as direções são adicionadas ao agente.
	 */

	private void compass(String agName, Coordinate coordinate) {
		//System.err.println( String.format("Verifying direction for (%d,%d)", x,y) );

		removeDirection(agName, "north");
		removeDirection(agName, "south");
		removeDirection(agName, "east");
		removeDirection(agName, "west");

		if (coordinate.getY() > car.getY())
			addDirection(agName, "north");
		if (coordinate.getY() < car.getY())
			addDirection(agName, "south");
		if (coordinate.getX() > car.getX())
			addDirection(agName, "east");
		if (coordinate.getX() < car.getX())
			addDirection(agName, "west");

		Predicate receiveDirection = new Predicate("receive_direction");
		addPercept(agName, receiveDirection);

	}

	private void addDirection(String agName, String direction) {

		//System.err.println( String.format("go %s", direction) );

		Predicate go = new Predicate(direction);
		addPercept(agName, go);

	}

	private void removeDirection(String agName, String direction) {

		//System.err.println( String.format("go %s", direction) );

		Predicate go = new Predicate(direction);
		removePercept(agName, go);

	}

	/*
	 * O método refuseRide recebe os seguintes argumentos:
	 * String agName: Nome do agente.
	 * String refuseType: O motivo pelo qual o agente se recusa a realizar a corrida.
	 * 
	 * A única função do método é exibir um gráficamente a recusa do agente agName a terminar sua corrida atual.
	 * Os tipos de recusa são:
	 * pick_up: Agente não possui um passageiro e também não pode pega-lo.
	 * drop_off: Agente possui um passageiro mas não conseguirá atingir o destino final.
	 * car_unavailable: O veículo controlado pelo agente sofreu perca total e não pode mover-se.
	 * 
	 * 
	 */
	private void refuseRide(String agName, String refuseType) {

		String type = "";
		
		switch (refuseType) {
		case "pick_up":
			type = "Pick up";
			break;
		case "drop_off":
			type = "Drop off";
			break;
		case "car_unavailable":
			type = "Car Unavailable - Total Loss";
			break;
		default:
			break;
		}

		if(simulate)
		{
			client.sendMessage( client.convertArray2String( 
					new String[] {"refuseRide", String.valueOf(car.getX()), String.valueOf(car.getY()), refuseType} ) );
			try {
				TimeUnit.MILLISECONDS.sleep(this.waitTime);
			} catch(Exception e) {
				System.err.println(e);
			}
		}
		
		System.err.println(String.format("%s: %s can't finish ride for %s", type, agName, currentPassenger.getName()));

	}

	private void park(String agName, String parkType) {


		switch (parkType) {
		case "pick_up":
			System.err.println(String.format("Pick Up Passanger %s in (%d,%d)", currentPassenger.getName(), car.getX(),
					car.getY()));
			break;
		case "drop_off":
			System.err.println(String.format("Drop Off Passanger %s in (%d,%d)\n", currentPassenger.getName(),
					car.getX(), car.getY()));
			break;
		case "depot":
			if(car.getX() == depotLocation.getX() && car.getY() == depotLocation.getY())
				System.err.println(String.format("%s is back to the depot.", agName));
			break;
		default:
			break;

		}

	}

	private void localize(String agName) {

		System.err.println("Initializing GPS");
		System.err.println(String.format("Agent %s is at (%d,%d)", agName, car.getX(), car.getY()));

		addGridMaxSize(agName);
		addDepot(agName);

		updateLocation(agName, new Coordinate(minGridSize, minGridSize), car);

	}

	private void addDepot(String agName) {
		
		if(simulate)
		{
			client.sendMessage( client.convertArray2String( 
					new String[] {"depot", String.valueOf(depotLocation.getX()), String.valueOf(depotLocation.getY())} 
					) );
			
		}

		Predicate depot = new Predicate("depot");
		depot.addTerm(new NumberTermImpl(depotLocation.getX()));
		depot.addTerm(new NumberTermImpl(depotLocation.getY()));

		addPercept(agName, depot);

	}

	private void addGridMaxSize(String agName) {

		Predicate max = new Predicate("max");
		max.addTerm(new NumberTermImpl(maxGridSize));
		max.addTerm(new NumberTermImpl(maxGridSize));

		addPercept(agName, max);

	}


	/*
	 * O método getRide recebe o seguintes argumento:
	 * String agName: O nome do agente.
	 * 
	 * A função remove as percepções referentes ao ponto de pegada, pick_up, e o ponto de destino, drop_off,do último passageiro.
	 * Na sequência, é verificado se ainda há algum passageiro disponível, onde tal informação é armazenada na array interna do ambiente, passengers.
	 * 
	 * Caso não haja nenhum outro passageiro, o ambiente adiciona uma percepção para o agente 
	 * informando que não há mais nenhuma corrida disponível, no_possible_new_ride.
	 * Caso exista alguma corrida disponível, o ambiente insere percepções referentes a tal corrida no agente agName, 
	 * e atualiza a variável de controle da lista de passageiros.
	 * 
	 * Em ambos os casos, o ambiente informa ao agente por meio da percepção ride_info que os dados sobre uma possível corrida foram atualizados. 
	 * 
	 */
	private void getRide(String agName) {
		
		Predicate pickUpLast = new Predicate("pick_up");
		pickUpLast.addTerm(new NumberTermImpl(currentPassenger.getPickUpX()));
		pickUpLast.addTerm(new NumberTermImpl(currentPassenger.getPickUpY()));

		Predicate dropOffLast = new Predicate("drop_off");
		dropOffLast.addTerm(new NumberTermImpl(currentPassenger.getDropOffX()));
		dropOffLast.addTerm(new NumberTermImpl(currentPassenger.getDropOffY()));

		removePercept(agName, pickUpLast);
		removePercept(agName, dropOffLast);

		if (passengers.isEmpty()) {

			Predicate noPossibleRide = new Predicate("no_possible_new_ride");
			addPercept(agName, noPossibleRide);

			System.err.println("No more available rides!");
		} else {
			currentPassenger = passengers.get(0);
			passengers.remove(0);

			int pickUpX = currentPassenger.getPickUpX();
			int pickUpY = currentPassenger.getPickUpY();

			int dropOffX = currentPassenger.getDropOffX();
			int dropOffY = currentPassenger.getDropOffY();
			
			if(simulate)
			{
				client.sendMessage( client.convertArray2String( new String[] {"pickUp", String.valueOf(pickUpX), String.valueOf(pickUpY)} ) );
				client.sendMessage( client.convertArray2String( new String[] {"dropOff", String.valueOf(dropOffX), String.valueOf(dropOffY)} ) );
			}

			
			System.err.println(String.format("\n%s going to pick up  %s in (%d,%d)", agName, currentPassenger.getName(),
					pickUpX, pickUpY));
			System.err.println(String.format("%s going to drop off %s in (%d,%d)", agName, currentPassenger.getName(),
					dropOffX, dropOffY));

			Predicate pickUp = new Predicate("pick_up");
			pickUp.addTerm(new NumberTermImpl(pickUpX));
			pickUp.addTerm(new NumberTermImpl(pickUpY));

			Predicate dropOff = new Predicate("drop_off");
			dropOff.addTerm(new NumberTermImpl(dropOffX));
			dropOff.addTerm(new NumberTermImpl(dropOffY));

			addPercept(agName, pickUp);
			addPercept(agName, dropOff);
		}

		Predicate rideInfo = new Predicate("ride_info");
		addPercept(agName, rideInfo); 

	}
	
	
	/*
	 * O método callEmergency recebe os seguintes argumentos:
	 * String agName:
	 * Coordinate currentPosition: Coordenadas da posição de onde o agente está requisitando ajuda da emergência.
	 * 
	 * A funcão adiciona uma percepção para o agente de que a emergência das coordenadas currentPosition está sendo atendida.
	 * 
	 * Para controle interno do ambiente, o predicado emergency é inserido em um ArrayList obstacleDamageRelated 
	 * para que possa ser removido futuramente quando necessário.
	 */
	private void callEmergency(String agName, Coordinate currentPosition) {

		System.err.println(String.format("%s crashed in (%d,%d). Calling Emergency.", agName, currentPosition.getX(), currentPosition.getY()));

		Predicate emergency = new Predicate("emergency");
		emergency.addTerm(new NumberTermImpl(currentPosition.getX()));
		emergency.addTerm(new NumberTermImpl(currentPosition.getY()));

		this.obstacleDamageRelated.add(emergency);
		
		addPercept(agName, emergency);
		

	}
	

	/*
	 * A função getAssistance recebe os seguintes argumentos:
	 * String agName: Nome do agente
	 * Coordinate currentPosition: Coordenadas onde o agente colidiu.
	 * 
	 * O objetivo deste método é remover os dados referentes aos níveis de 
	 * dano dos obstáculos que o agente colidiu e ou poderia ter colidido. Sendo estes os predicados contidos em obstacleDamageRelated.
	 * E finalmente, adicionar uma percepção, assisted, referente ao fato de que o 
	 * agente está pronto para continuar com seu trajeto.
	 * 
	 * O ambiente também informa o simulador para remover os dados sobre os níveis de dano de cada obstáculo.
	 * 
	 */
	private void getAssistance(String agName, Coordinate currentPosition) {
		
		for(Predicate obstacleDamage : this.obstacleDamageRelated) {
			removePercept(agName, obstacleDamage);
		}
		this.obstacleDamageRelated.clear();
		
		if(simulate) {
			try {
				TimeUnit.MILLISECONDS.sleep(this.waitTime);
			} catch(Exception e) {
				System.err.println(e);
			}
		}
		
		if(simulate) {
			client.sendMessage( client.convertArray2String( new String[] {"removeObstacleDamage"} ) );
		}
		
		Predicate assisted = new Predicate("assisted");
		assisted.addTerm(new NumberTermImpl(currentPosition.getX()));
		assisted.addTerm(new NumberTermImpl(currentPosition.getY()));
		addPercept(agName, assisted);
	}

	
	/*
	 * Recebe como argumentos uma direção e uma coordenada.
	 * Calcula os valores se movimentar naquela direção a partir daquela coordenada.
	 * */
	private Coordinate getDirectionCoordinate(String direction, Coordinate coordinate) {
		Coordinate newCoordinate;
		
		switch (direction) {
			case "north": 
				newCoordinate = new Coordinate(coordinate.getX(), coordinate.getY()+1);
				break;
			case "south":
				newCoordinate = new Coordinate(coordinate.getX(), coordinate.getY()-1);
				break;
			case "east":
				newCoordinate = new Coordinate(coordinate.getX()+1, coordinate.getY());
				break;
			case "west":
				newCoordinate = new Coordinate(coordinate.getX()-1, coordinate.getY());
				break;
			default:
				newCoordinate = new Coordinate(coordinate.getX(), coordinate.getY());
				break;
		}
		
		return newCoordinate;
	}
}
