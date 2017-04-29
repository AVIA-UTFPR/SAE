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
import ail.syntax.Term;
import ail.syntax.Unifier;
import ail.syntax.VarTerm;
import ail.util.AILexception;
import ajpf.util.choice.Choice;
import main.Client;
import util.Coordinate;
import util.GridCell;
import util.Passenger;
import util.Util;

public class AutonomousCarEnv extends DefaultEnvironmentwRandomness {
	

	Choice<Boolean> accident_chance;
	

	// Information about the car
	// Initial position of the car
	private Coordinate car = new Coordinate(0, 0);
	private Coordinate depotLocation = new Coordinate(0, 0);
	private String currentDirection = "north";


	// Information about the grid

	// Total of Static Obstacles
	private int nObstacles = 50;

	// Grid is always a square. Lowest coordinate is (minGridSize, minGridSize)
	// and Highest coordinate (maxGridSize, maxGridSize)
	private int minGridSize = 0;
	private int maxGridSize = 15;

	// Information about each position
	Map<String, GridCell> environmentGrid;

	// Information about passengers
	private ArrayList<Passenger> passengers = new ArrayList<Passenger>();
	private int maxInitPassengers = 5;
	private Passenger currentPassenger = null;

	
	// Client to send messages to the Simulator
	Client client = new Client();
	private boolean simulate = true;

	@Override
	public void initialise() {

	}

	@Override
	public void setMAS(MAS m) {
		super.setMAS(m);


		accident_chance = new Choice<Boolean>(m.getController());
		accident_chance.addChoice(0.9, false);
		accident_chance.addChoice(0.1, true);
		
		this.environmentGrid = initGridInformation();
		initPassengerList();
		initObstacles();
		
		client.sendMessage( client.convertArray2String( new String[] {"clear", String.valueOf(maxGridSize)} ) );
		client.sendMessage( client.convertArray2String( new String[] {"gridSize", String.valueOf(maxGridSize)} ) );
		try {
			TimeUnit.MILLISECONDS.sleep(100);
		} catch(Exception e) {
			System.err.println(e);
		}
		/*
		passengers.add(new Passenger("" , 4, 1, 7, 1) );
		passengers.add(new Passenger("" , 4, 1, 7, 1) );
		passengers.add(new Passenger("" , 4, 1, 7, 1) );
		environmentGrid.get(GridCell.getIndex(5, 0)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(5, 1)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(5, 2)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(6, 2)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(7, 2)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(8, 2)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(9, 3)).setHasObstacle(true);
		
		*/
		/*

		passengers.add(new Passenger("" , 0, 9, 0, 9) );
		passengers.add(new Passenger("" , 0, 0, 0, 9) );
		passengers.add(new Passenger("Simple Belief East to North" , 0, 9, 3, 11) );
		passengers.add(new Passenger("Simple Belief West to North" , 3, 9, 0, 11) );
		
		passengers.add(new Passenger("Simple Belief East to South" , 0, 11, 3, 9) );
		passengers.add(new Passenger("Simple Belief West to South" , 3, 11, 0, 9) );

		passengers.add(new Passenger("Reroute East to North", 0, 10, 3, 10) );
		passengers.add(new Passenger("Reroute West to North", 3, 10, 0, 10) );

		passengers.add(new Passenger("Reroute East to South", 0, 15, 3, 15) );
		passengers.add(new Passenger("Reroute West to South", 3, 15, 0, 15) );
		
		passengers.add(new Passenger("Simple Belief North to East" , 0, 0, 1, 3) );
		passengers.add(new Passenger("Simple Belief South to East" , 1, 3, 2, 0) );

		passengers.add(new Passenger("Simple Belief North to West" , 6, 0, 3, 3) );
		passengers.add(new Passenger("Simple Belief South to West" , 3, 3, 2, 0) );
		
		passengers.add(new Passenger("Reroute North to East" , 0, 0, 0, 3) );
		passengers.add(new Passenger("Reroute South to East" , 0, 3, 0, 0) );

		passengers.add(new Passenger("Reroute North to West" , 6, 0, 6, 3) );
		passengers.add(new Passenger("Reroute South to West" , 6, 3, 6, 0) );

		passengers.add(new Passenger("" , 3, 0, 1, 2) );
		passengers.add(new Passenger("Reroute North to West - Simple Belief East" , 1, 2, 2, 7) );
		passengers.add(new Passenger("Reroute South to West - Simple Belief East" , 2, 7, 3, 2) );

		passengers.add(new Passenger("Reroute North to East - Simple Belief West" , 3, 2, 2, 7) );
		passengers.add(new Passenger("Reroute South to East - Simple Belief West" , 6, 7, 5, 3) );
		

		
		 

		passengers.add(new Passenger("" , 3, 3, 8, 3) );
		
		
		passengers.add(new Passenger("Return North - South - East" , 12, 0, 10, 1) );
		//passengers.add(new Passenger("Return North - South - East" , 12, 0, 10, 1) );
		//passengers.add(new Passenger("Return North - South - East" , 12, 0, 10, 2) );
		//passengers.add(new Passenger("Return North - South - East" , 12, 0, 10, 3) );
		
		
		passengers.add(new Passenger("Return North - South - East" , 9, 1, 13, 1) );
		passengers.add(new Passenger("Return North - South - West" , 9, 1, 13, 1) );
		//passengers.add(new Passenger("Return North - South - East" , 9, 1, 13, 1) );
		//passengers.add(new Passenger("Return North - South - West" , 9, 1, 13, 1) );
		

		passengers.add(new Passenger("Return North - South - EW" , 10, 0, 13, 1) );
		//passengers.add(new Passenger("Return North - South - EW" , 10, 0, 13, 1) );
		//passengers.add(new Passenger("Return North - South - EW" , 10, 0, 13, 2) );
		//passengers.add(new Passenger("Return North - South - EW" , 10, 0, 13, 3) );
		passengers.add(new Passenger("Return North - South - EW" , 10, 1, 13, 1) );
		//passengers.add(new Passenger("Return North - South - EW" , 10, 1, 13, 1) );
		//passengers.add(new Passenger("Return North - South - EW" , 10, 1, 13, 2) );
		//passengers.add(new Passenger("Return North - South - EW" , 10, 1, 13, 3) );
		passengers.add(new Passenger("Return North - South - EW" , 10, 2, 13, 1) );
		//passengers.add(new Passenger("Return North - South - EW" , 10, 2, 13, 1) );
		//passengers.add(new Passenger("Return North - South - EW" , 10, 2, 13, 2) );
		//passengers.add(new Passenger("Return North - South - EW" , 10, 2, 13, 3) );
		
		 
		

		passengers.add(new Passenger("Return South to North - East" , 10, 9, 12, 9) );
		passengers.add(new Passenger("Return South to North - East" , 10, 9, 12, 9) );
		//passengers.add(new Passenger("Return South to North - East" , 10, 9, 12, 9) );
		
		
		passengers.add(new Passenger("Return North - South - EW" , 10, 0, 13, 1) );
		passengers.add(new Passenger("Return North - South - EW" , 10, 0, 13, 1) );
		passengers.add(new Passenger("Return North - South - EW" , 10, 0, 13, 1) );
		
		
		environmentGrid.get(GridCell.getIndex(9, 10)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(10, 10)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(11, 10)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(12, 10)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(13, 10)).setHasObstacle(true);
		
		environmentGrid.get(GridCell.getIndex(11, 6)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(11, 7)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(11, 8)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(11, 9)).setHasObstacle(true);
		
		environmentGrid.get(GridCell.getIndex(9, 6)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(10, 6)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(11, 6)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(12, 6)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(13, 6)).setHasObstacle(true);
		
		
		
		//environmentGrid.get(GridCell.getIndex(11, 0)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(11, 1)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(11, 2)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(11, 3)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(11, 4)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(10, 4)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(12, 4)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(9, 4)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(13, 4)).setHasObstacle(true);
		
		
		environmentGrid.get(GridCell.getIndex(5, 6)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(2, 9)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(2, 10)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(2, 11)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(6, 5)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(2, 4)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(1, 5)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(2, 5)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(3, 6)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(7, 0)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(7, 2)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(4, 1)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(5, 1)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(6, 1)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(0, 1)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(1, 1)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(2, 1)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(2, 15)).setHasObstacle(true);
		environmentGrid.get(GridCell.getIndex(2, 14)).setHasObstacle(true);
		
		*/

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

	private void initPassengerList() {
		for (int i = 0; i < maxInitPassengers; i++) {

			int pickUpX = random_ints.nextInt(maxGridSize) + 1;
			int pickUpY = random_ints.nextInt(maxGridSize) + 1;

			int dropOffX = random_ints.nextInt(maxGridSize) + 1;
			int dropOffY = random_ints.nextInt(maxGridSize) + 1;

			passengers.add(new Passenger("" + i, pickUpX, pickUpY, dropOffX, dropOffY));
		}
	}

	private void initObstacles() {
		int x, y;
		for(int i = 0; i < this.nObstacles; i++) {
			x = random_ints.nextInt(maxGridSize) + 1;
			y = random_ints.nextInt(maxGridSize) + 1;
			environmentGrid.get(GridCell.getIndex(x, y)).setHasObstacle(true);
		}
	}

	// Identifies agents' actions
	public Unifier executeAction(String agName, Action act) throws AILexception {

		String actionName = act.getFunctor();

		if (actionName.equals("drive")) {
			int fromX = Util.getIntTerm(act.getTerm(0));
			int fromY = Util.getIntTerm(act.getTerm(1));
			String direction = act.getTerm(2).getFunctor();
			int destinationX = Util.getIntTerm(act.getTerm(3));
			int destinationY = Util.getIntTerm(act.getTerm(4));
			drive(agName, fromX, fromY, direction, destinationX, destinationY);

		} else if (actionName.equals("compass")) {

			int x = Util.getIntTerm(act.getTerm(0));
			int y = Util.getIntTerm(act.getTerm(1));

			compass(agName, x, y);

		} else if (actionName.equals("localize")) {
			
			

			localize(agName);

		} else if (actionName.equals("get_ride")) {

			getRide(agName);

		} else if (actionName.equals("refuse_ride")) {
			String refuseType = act.getTerm(0).getFunctor();

			refuseRide(agName, refuseType);
		} else if (actionName.equals("park")) {

			String parkType = act.getTerm(0).getFunctor();

			park(agName, parkType);

		} else if (actionName.equals("a_m")) {

			String am = act.getTerm(0).getFunctor();
			
			int x = Util.getIntTerm(act.getTerm(1));
			int y = Util.getIntTerm(act.getTerm(2));

			String direction = act.getTerm(3).getFunctor();

			int dx = Util.getIntTerm(act.getTerm(4));
			int dy = Util.getIntTerm(act.getTerm(5));
			
			System.err.println(am + " - "+ direction +": ("+x+","+y+") to ("+dx+","+dy+")");


		} else if (actionName.equals("no_further_from")) {
			int fromX = Util.getIntTerm(act.getTerm(0));
			int fromY = Util.getIntTerm(act.getTerm(1));
			int x = Util.getIntTerm(act.getTerm(2));
			int y = Util.getIntTerm(act.getTerm(3));
			int destinationX = Util.getIntTerm(act.getTerm(4));
			int destinationY = Util.getIntTerm(act.getTerm(5));

			noFurtherFrom(agName, fromX, fromY, x, y, destinationX, destinationY);

		} else if (actionName.equals("call_emergency")) {

			int x = Util.getIntTerm(act.getTerm(0));
			int y = Util.getIntTerm(act.getTerm(1));

			callEmergency(agName, x, y);
		} else if (actionName.equals("message")) {

			System.err.println(String.format("%s says %s", agName, act.getTerm(0)));

		} else if (actionName.equals("honk")) {

			System.err.println("HONK");
		}

		return super.executeAction(agName, act);

	}
	
	private void addFromTo(Predicate predicate, int fromX, int fromY, int x, int y, String direction, int destinationX, int destinationY) {
		predicate.addTerm(new NumberTermImpl(fromX));
		predicate.addTerm(new NumberTermImpl(fromY));
		predicate.addTerm(new NumberTermImpl(x));
		predicate.addTerm(new NumberTermImpl(y));
		predicate.addTerm(new Predicate(direction));
		predicate.addTerm(new NumberTermImpl(destinationX));
		predicate.addTerm(new NumberTermImpl(destinationY));
		

	}

	private void noFurtherFrom(String agName, int fromX, int fromY, int x, int y, int destinationX, int destinationY) {
			
		System.err.println("Can't come here: at("+x+","+y+") to get to ("+destinationX+","+destinationY+")");
		
		//	north
		Predicate adaptFromTo = new Predicate("adapt_from_to");
		Predicate movedFromTo = new Predicate("moved_from_to");
		addFromTo(adaptFromTo, fromX, fromY, x, y-1, "north", destinationX, destinationY);
		addFromTo(movedFromTo, fromX, fromY, x, y-1, "south", destinationX, destinationY);
		addPercept(agName, adaptFromTo);
		addPercept(agName, movedFromTo);

		// south
		adaptFromTo = new Predicate("adapt_from_to");
		movedFromTo = new Predicate("moved_from_to");
		addFromTo(adaptFromTo, fromX, fromY, x, y+1, "south", destinationX, destinationY);
		addFromTo(movedFromTo, fromX, fromY, x, y+1, "north", destinationX, destinationY);
		addPercept(agName, adaptFromTo);
		addPercept(agName, movedFromTo);

		// east
		adaptFromTo = new Predicate("adapt_from_to");
		movedFromTo = new Predicate("moved_from_to");
		addFromTo(adaptFromTo, fromX, fromY, x-1, y, "east", destinationX, destinationY);
		addFromTo(movedFromTo, fromX, fromY, x-1, y, "west", destinationX, destinationY);
		addPercept(agName, adaptFromTo);
		addPercept(agName, movedFromTo);
		
		// west
		adaptFromTo = new Predicate("adapt_from_to");
		movedFromTo = new Predicate("moved_from_to");
		addFromTo(adaptFromTo, fromX, fromY, x+1, y, "west", destinationX, destinationY);
		addFromTo(movedFromTo, fromX, fromY, x+1, y, "east", destinationX, destinationY);
		addPercept(agName, adaptFromTo);
		addPercept(agName, movedFromTo);
		
		Predicate noFurther = new Predicate("no_further");
		noFurther.addTerm(new NumberTermImpl(fromX));
		noFurther.addTerm(new NumberTermImpl(fromY));
		noFurther.addTerm(new NumberTermImpl(x));
		noFurther.addTerm(new NumberTermImpl(y));
		noFurther.addTerm(new NumberTermImpl(destinationX));
		noFurther.addTerm(new NumberTermImpl(destinationY));
		addPercept(agName, noFurther);
		

	}

	private void compass(String agName, int x, int y) {
		// System.err.println( String.format("Verifying direction for (%d,%d)", x,y) );

		removeDirection(agName, "north");
		removeDirection(agName, "south");
		removeDirection(agName, "east");
		removeDirection(agName, "west");

		if (y > car.getY())
			addDirection(agName, "north");
		if (y < car.getY())
			addDirection(agName, "south");
		if (x > car.getX())
			addDirection(agName, "east");
		if (x < car.getX())
			addDirection(agName, "west");

		Predicate receiveDirection = new Predicate("receive_direction");
		addPercept(agName, receiveDirection);

	}

	private void addDirection(String agName, String direction) {

		// System.err.println( String.format("go %s", direction) );

		Predicate go = new Predicate(direction);
		addPercept(agName, go);

	}

	private void removeDirection(String agName, String direction) {

		// System.err.println( String.format("go %s", direction) );

		Predicate go = new Predicate(direction);
		removePercept(agName, go);

	}

	private void refuseRide(String agName, String refuseType) {

		switch (refuseType) {
		case "pick_up":

			break;
		case "drop_off":

			break;
		default:
			break;
		}

		System.err.println(String.format("%s can't %s passanger %s", agName, refuseType, currentPassenger.getName()));

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

		updateLocation(agName, minGridSize, minGridSize, car.getX(), car.getY());

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

	private void drive(String agName, int fromX, int fromY, String direction, int destinationX, int destinationY) {
		
		this.currentDirection = direction;

		// Don't move if direction is invalid
		int newX = car.getX();
		int newY = car.getY();

		switch (direction) {
		case "north": // Move up in the Y axis
			newY++;
			break;
		case "south": // Move down in the Y axis
			newY--;
			break;
		case "east": // Move right in the X axis
			newX++;
			break;
		case "west": // Move left in the X axis
			newX--;
			break;
		default:
		}
		



		Predicate movedFromTo = new Predicate("moved_from_to");
		Predicate adaptedFromTo = new Predicate("adapt_from_to");
		

		addFromTo(adaptedFromTo, fromX, fromY, car.getX(), car.getY(), direction, destinationX, destinationY);
		addFromTo(movedFromTo, fromX, fromY, newX, newY, direction, destinationX, destinationY);

		addPercept(agName, adaptedFromTo); // inform where the car came from
		addPercept(agName, movedFromTo); // inform where the car came from
		
		System.err.println(String.format("Moving %s from (%d,%d) to (%d,%d)", direction, car.getX(), car.getY(), newX, newY));

		updateLocation(agName, car.getX(), car.getY(), newX, newY);

	}

	private void updateLocation(String agName, int x, int y, int newX, int newY) {
		

/*
 * 	try {
				TimeUnit.MILLISECONDS.sleep(0);
			}
			catch (Exception e) {
				System.err.println(e);
			}
 */
		client.sendMessage( client.convertArray2String( new String[] {"carLocation", String.valueOf(newX), String.valueOf(newY), this.currentDirection} ) );

		Predicate oldLocation = new Predicate("at");
		oldLocation.addTerm(new NumberTermImpl(x));
		oldLocation.addTerm(new NumberTermImpl(y));

		Predicate at = new Predicate("at");
		at.addTerm(new NumberTermImpl(newX));
		at.addTerm(new NumberTermImpl(newY));

		// Update position of the agent in the environment
		car.setX(newX);
		car.setY(newY);

		scanSurroundings(agName, newX, newY);

		removePercept(agName, oldLocation); // remove old location of the agent
		addPercept(agName, at); // inform new location of the agent

	}

	private void scanSurroundings(String agName, int x, int y) {

		verifyObstacle(agName, "north", x, y + 1, x, y);
		verifyObstacle(agName, "south", x, y - 1, x, y);
		verifyObstacle(agName, "east", x + 1, y, x, y);
		verifyObstacle(agName, "west", x - 1, y, x, y);

		// verifyObstacle(agName, "center", x, y, x, y); // Current Location

	}

	private void verifyObstacle(String agName, String direction, int directionX, int directionY, int currentX,
			int currentY) {
		// Areas that the agent can't reach are marked as obstacles
		
		

		if (directionX < minGridSize || directionX > maxGridSize || directionY < minGridSize
				|| directionY > maxGridSize) {
			// System.err.println( String.format("Can't reach in (%d,%d)", x, y)
			// );
			addObstacle(agName, direction, currentX, currentY);
			addObstacle(agName, "center", directionX, directionY);
		} else {

			GridCell currentGridCell = environmentGrid.get(GridCell.getIndex(directionX, directionY));
			if (currentGridCell.hasObstacle()) {
				if(simulate) {
					client.sendMessage( client.convertArray2String( new String[] {"obstacle", String.valueOf(directionX), String.valueOf(directionY)} ) );
				}
				
				if(direction.equals(this.currentDirection)) {
					boolean accident = accident_chance.get_choice();
					if (accident)
					{
						client.sendMessage( client.convertArray2String( new String[] {"cant_avoid_obstacle", String.valueOf(directionX), String.valueOf(directionY)} ) );
						Predicate cantAvoidObstacle = new Predicate("cant_avoid_obstacle");
						cantAvoidObstacle.addTerm(new Predicate(direction));
						cantAvoidObstacle.addTerm(new NumberTermImpl(currentX));
						cantAvoidObstacle.addTerm(new NumberTermImpl(currentY));
						addPercept(agName, cantAvoidObstacle);
					}
				}
				addObstacle(agName, direction, currentX, currentY);
				addObstacle(agName, "center", directionX, directionY);
			}
		}
	}

	private void addObstacle(String agName, String direction, int x, int y) {
		Predicate obstacle = new Predicate("obstacle");
		obstacle.addTerm(new Predicate(direction));
		obstacle.addTerm(new NumberTermImpl(x));
		obstacle.addTerm(new NumberTermImpl(y));

		addPercept(agName, obstacle);
	}

	private void getRide(String agName) {

		Predicate pickUpAll = new Predicate("pick_up");
		pickUpAll.addTerm(new VarTerm("X"));
		pickUpAll.addTerm(new VarTerm("Y"));

		Predicate dropOffAll = new Predicate("drop_off");
		dropOffAll.addTerm(new VarTerm("X"));
		dropOffAll.addTerm(new VarTerm("Y"));

		removeUnifiesPercept(agName, pickUpAll);
		removeUnifiesPercept(agName, dropOffAll);

		if (passengers.isEmpty()) {

			Predicate noPossibleRide = new Predicate("no_possible_new_ride");
			addPercept(agName, noPossibleRide);

			System.err.println("No more availble rides!");
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

	private void callEmergency(String agName, int x, int y) {

		System.err.println(String.format("%s crashed in (%d,%d). Calling Emergency.", agName, x, y));

		Predicate help = new Predicate("help");
		help.addTerm(new NumberTermImpl(x));
		help.addTerm(new NumberTermImpl(y));

		addPercept(agName, help);

	}

	private void showKnownGrid() {

	}

}
