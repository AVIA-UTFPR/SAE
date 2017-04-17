package _005_dont_crash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import util.Coordinate;
import util.GridCell;
import util.Passenger;
import util.Util;

public class AutonomousCarEnv extends DefaultEnvironmentwRandomness{


	// Information about the car
	// Initial position of the car
	
	private int scanRadius = 1;
	

	// Information about the grid
	
	// Total of Static Obstacles
	private int nObstacles = 1000;
	
	// Grid is always a square. Lowest coordinate is (minGridSize, minGridSize) and Highest coordinate (maxGridSize, maxGridSize)
	private int minGridSize = 0;
	private int maxGridSize = 10;
	
	//private Coordinate minGrid = new Coordinate(0,0);
	//private Coordinate maxGrid = new Coordinate(maxGridSize,maxGridSize);
	
	// Information about each position
	Map <String, GridCell> gridCells = new HashMap<String, GridCell>();

	// Information about passengers
	private ArrayList<Passenger> passengers = new ArrayList<Passenger>();
	private int maxInitPassengers = 0;
	private Passenger currentPassenger = null;
	

	private Coordinate car = new Coordinate(0,0);
	private Coordinate depotLocation = car;
	
	@Override
	public void initialise() {
		
	}
	
	@Override
	public void setMAS(MAS m) {
		super.setMAS(m);

		initGridInformation();
		initPassengerList();
		initObstacles();
		
		passengers.add(new Passenger("1" , 1, 0, 1, 3) );
		gridCells.get( GridCell.getIndex(1, 2) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(0, 1) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(2, 1) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(2, 3) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(1, 4) ).setHasObstacle(true);
		

		
		//passengers.add(new Passenger("1" , 5, 9, 9, 9) );
		//passengers.add(new Passenger("2" , 9, 9, 5, 9) );
		//gridCells.get( GridCell.getIndex(7, 9) ).setHasObstacle(true);
		//gridCells.get( GridCell.getIndex(8, 10) ).setHasObstacle(true);
		/*
		
		First Scenario: Going north or south and found an obstacle, but believes it can go east or west.
		passengers.add(new Passenger("1" , 0, 1, 1, 3) );
		passengers.add(new Passenger("2" , 3, 3, 4, 1) );
		passengers.add(new Passenger("3" , 6, 1, 5, 4) );
		passengers.add(new Passenger("4" , 8, 4, 7, 1) );

		gridCells.get( GridCell.getIndex(0, 2) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(3, 2) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(6, 3) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(8, 2) ).setHasObstacle(true);
		*/

		/*
		Second Scenario: Going north or south
		passengers.add(new Passenger("1" , 0, 1, 0, 4) );
		passengers.add(new Passenger("2" , 0, 4, 0, 1) );
		gridCells.get( GridCell.getIndex(0, 3) ).setHasObstacle(true);
		
		passengers.add(new Passenger("1" , 0, 1, 3, 4) );
		passengers.add(new Passenger("1" , 3, 4, 3, 1) );
		passengers.add(new Passenger("1" , 3, 1, 3, 4) );
		gridCells.get( GridCell.getIndex(3, 2) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(4, 3) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(4, 1) ).setHasObstacle(true);
		*/
		
		
		//gridCells.get( GridCell.getIndex(2, 3) ).setHasObstacle(true);

		/*passengers.add(new Passenger("pk x" , -1, 0, 0, 0) );
		passengers.add(new Passenger("pk y" , 0, -1, 0, 0) );
		
		passengers.add(new Passenger("dp x" , 0, 0, -1, 0) );
		passengers.add(new Passenger("dp y" , 0, 0, 0, -1) );
		

		passengers.add(new Passenger("X1" , -1, -1, 0, 0) );
		passengers.add(new Passenger("X2", 0, 0, -1, -1) ); */

		//
		//gridCells.get( GridCell.getIndex(0, 2) ).setHasObstacle(true);
		//gridCells.get( GridCell.getIndex(2, 2) ).setHasObstacle(true);
		/*gridCells.get( GridCell.getIndex(0, 3) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(0, 5) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(0, 7) ).setHasObstacle(true);

		gridCells.get( GridCell.getIndex(1, 0) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(5, 0) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(3, 0) ).setHasObstacle(true);
		gridCells.get( GridCell.getIndex(7, 0) ).setHasObstacle(true);*/
		
		showAllPassengerList();
		
	}
	
	private void showAllPassengerList() {
		
		for (Passenger passenger : passengers) {
			System.err.println( 
					String.format("Passenger %s: PickUp(%d,%d) - Drop Off (%d,%d)", 
							passenger.getName(), 
							passenger.getPickUpX(), passenger.getPickUpY(), 
							passenger.getDropOffX(), passenger.getDropOffY())
					);
		}
		
	}

	private void initGridInformation() {
		
		for(int x = minGridSize; x <= maxGridSize; x++) {
			for(int y = minGridSize; y <= maxGridSize; y++) {	
				
				String cellName = GridCell.getIndex(x, y);
				gridCells.put(cellName, new GridCell(x, y, false) );
				
			}
		}
		
	}
	

	private void initPassengerList() {
		for(int i = 0; i < maxInitPassengers; i++) {
			
			int pickUpX = random_ints.nextInt(maxGridSize)  + 1;
			int pickUpY = random_ints.nextInt(maxGridSize)  + 1;

			int dropOffX = random_ints.nextInt(maxGridSize) + 1;
			int dropOffY = random_ints.nextInt(maxGridSize) + 1;
			
			passengers.add(new Passenger( "" + i , pickUpX, pickUpY, dropOffX, dropOffY) );
		}
	}

	private void initObstacles() {
		
	}

	// Identifies agents' actions
	public Unifier executeAction(String agName, Action act) throws AILexception {	
		
		String actionName = act.getFunctor();

		if(actionName.equals("drive")) {
			String direction = act.getTerm(0).getFunctor();
			//int x = Util.getIntTerm(act.getTerm(1));
			//int y = Util.getIntTerm(act.getTerm(2));
			
			
			drive(agName, direction);
		
		}
		else if(actionName.equals("compass")) {
			
			int x = Util.getIntTerm(act.getTerm(0));
			int y = Util.getIntTerm(act.getTerm(1));
			
			compass(agName, x, y);
				
			
			
		}
		else if(actionName.equals("localize")) {
			
			localize(agName);
			
		}
		else if(actionName.equals("get_ride")) {
			
			getRide(agName);
			
		}
		else if(actionName.equals("refuse_ride")) {
			String refuseType = act.getTerm(0).getFunctor();
			
			refuseRide(agName, refuseType);
		}
		else if(actionName.equals("park")) {
			
			String parkType = act.getTerm(0).getFunctor();
			
			park(agName, parkType);

		}
		else if(actionName.equals("call_emergency")) {
			
			int x = Util.getIntTerm( act.getTerm(0) );
			int y = Util.getIntTerm( act.getTerm(1) );
			
			callEmergency(agName, x, y);
		}
		else if(actionName.equals("message")) {

			System.err.println( String.format("%s says %s", agName, act.getTerm(0)) );

		}
		else if(actionName.equals("honk")) {

			System.err.println("HONK");
		}

		return super.executeAction(agName, act);

	}

	private void compass(String agName, int x, int y) {
		System.err.println( String.format("Verifying direction for (%d,%d)", x,y) );
		
		removeDirection(agName, "north");
		removeDirection(agName, "south");
		removeDirection(agName, "east");
		removeDirection(agName, "west");

		if( y > car.getY() )
			addDirection(agName, "north");
		if( y < car.getY() )
			addDirection(agName, "south");
		if( x > car.getX() )
			addDirection(agName, "east");
		if( x < car.getX() )
			addDirection(agName, "west");
		
		Predicate receiveDirection = new Predicate("receive_direction");
		addPercept(agName, receiveDirection);
		
		//System.err.println(receiveDirection);
		
	}
	
	private void addDirection(String agName, String direction) {
		
		System.err.println( String.format("go %s", direction) );
		
		Predicate go = new Predicate(direction);
		addPercept(agName, go);
		
	}
	
	private void removeDirection(String agName, String direction) {
		
		//System.err.println( String.format("go %s", direction) );
		
		Predicate go = new Predicate(direction);
		removePercept(agName, go);
		
	}

	private void refuseRide(String agName, String refuseType) {

		switch(refuseType) {
			case "pick_up": 
				
				break;
			case "drop_off":
				
				break;
			default: break;
	
		}
		
		System.err.println( String.format("%s can't %s passanger %s", agName, refuseType, currentPassenger.getName()) );
		
	}

	private void park(String agName, String parkType) {
		
		Predicate passenger = new Predicate("passenger");
		
		switch(parkType) {
			case "pick_up": 
				System.err.println( String.format("Pick Up Passanger %s in (%d,%d)", currentPassenger.getName(), car.getX(), car.getY()) );
				break;
			case "drop_off":
				System.err.println( String.format("Drop Off Passanger %s in (%d,%d)\n", currentPassenger.getName(), car.getX(), car.getY()) );
				break;
			case "depot":
				System.err.println( String.format("%s is back to the depot.", agName) );
				break;
			default: break;
		
		}
		
	}

	private void localize(String agName) {
		
		System.err.println("Initializing GPS");
		System.err.println( String.format("Agent %s is at (%d,%d)", agName, car.getX(), car.getY()) );
		
		addGridMaxSize(agName);
		addDepot(agName);
	
		updateLocation(agName, minGridSize, minGridSize, car.getX(), car.getY());
		
	}
	
	private void addDepot(String agName) {
		
		Predicate depot = new Predicate("depot");
		depot.addTerm(new NumberTermImpl( depotLocation.getX() ));
		depot.addTerm(new NumberTermImpl( depotLocation.getY() ));

		addPercept(agName, depot);
		
	}

	private void addGridMaxSize(String agName) {
		
		Predicate max = new Predicate("max");
		max.addTerm(new NumberTermImpl( maxGridSize ));
		max.addTerm(new NumberTermImpl( maxGridSize ));

		addPercept(agName, max);
		
	}

	private void drive(String agName, String direction){

		// Don't move if direction is invalid
		int newX = car.getX();
		int newY = car.getY();

		switch(direction) {
			case "north": // Move up in the Y axis
				newY++; break;
			case "south": // Move down in the Y axis
				newY--; break;
			case "east": // Move right in the X axis
				newX++; break;
			case "west": // Move left in the X axis
				newX--; break;
			default:
		}

		System.err.println( String.format("Moving %s from (%d,%d) to (%d,%d)", direction, car.getX(), car.getY(), newX, newY) );
		
		updateLocation(agName, car.getX(), car.getY(), newX, newY);

	}
	
	private void updateLocation(String agName, int x, int y, int newX, int newY) {

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
		
		verifyObstacle(agName, "north", x, y+1, x, y);
		verifyObstacle(agName, "south", x, y-1, x, y);
		verifyObstacle(agName, "east", x+1, y, x, y);
		verifyObstacle(agName, "west", x-1, y, x, y);
		
		//verifyObstacle(agName, "center", x, y, x, y); // Current Location
		
	}
	
	private void verifyObstacle(String agName, String direction, int directionX, int directionY, int currentX, int currentY) {
		// Areas that the agent can't reach are marked as obstacles
		
		if ( directionX <  minGridSize || directionX > maxGridSize || directionY < minGridSize || directionY > maxGridSize ) {
			//System.err.println( String.format("Can't reach in (%d,%d)", x, y) );
			addObstacle(agName, direction, currentX, currentY);
			addObstacle(agName, "center", directionX, directionY);
		}
		else {
			
			GridCell currentGridCell = gridCells.get( GridCell.getIndex(directionX, directionY) );
			if (currentGridCell.hasObstacle()) {
				addObstacle(agName, direction, currentX, currentY);
				addObstacle(agName, "center", directionX, directionY);
			}
		}
	}
	
	private void addObstacle(String agName, String direction, int x, int y){
		Predicate obstacle = new Predicate("obstacle");
		obstacle.addTerm( new Predicate( direction ) );
		obstacle.addTerm( new NumberTermImpl(x) );
		obstacle.addTerm( new NumberTermImpl(y) );
		
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
		
		if(passengers.isEmpty()) {
			
			Predicate noPossibleRide = new Predicate("no_possible_new_ride");
			addPercept(agName, noPossibleRide);
			
			System.err.println("No more availble rides!");
		}
		else {
			currentPassenger = passengers.get(0);
			passengers.remove(0);

			int pickUpX = currentPassenger.getPickUpX();
			int pickUpY = currentPassenger.getPickUpY();

			int dropOffX = currentPassenger.getDropOffX();
			int dropOffY = currentPassenger.getDropOffY();

			System.err.println( String.format("\n%s going to pick up  %s in (%d,%d)", agName, currentPassenger.getName(), pickUpX, pickUpY) );
			System.err.println( String.format("%s going to drop off %s in (%d,%d)", agName, currentPassenger.getName(), dropOffX, dropOffY) );

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
		
		System.err.println( String.format("%s crashed in (%d,%d). Calling Emergency.", agName, x, y) );
		
		Predicate help = new Predicate("help");
		help.addTerm(new NumberTermImpl( x ));
		help.addTerm(new NumberTermImpl( y ));
		
		addPercept(agName, help);
		
	}
	

}
