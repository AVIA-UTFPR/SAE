package _004_someone_need_a_cab;

import java.util.ArrayList;

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
import util.Passenger;

public class AutonomousCarEnv extends DefaultEnvironmentwRandomness{


	// Initial position of the car
	private int carX = 0;
	private int carY = 0;

	// Information about the grid
	private int maxX = 100;
	private int maxY = 100;

	// Information about passengers
	private ArrayList<Passenger> passengers = new ArrayList<Passenger>();
	private int maxInitPassengers = 5;
	private Passenger currentPassenger = null;

	@Override
	public void setMAS(MAS m) {
		super.setMAS(m);

		initPassengerList();

	}

	private void initPassengerList() {
		/*
		passengers.add(new Passenger("north" , 0, 0, 0, 5) );
		passengers.add(new Passenger("south" , 0, 5, 0, 0) );
		passengers.add(new Passenger("east" , 0, 0, 5, 0) );
		passengers.add(new Passenger("west" , 5, 0, 0, 0) );

		passengers.add(new Passenger("north and east" , 0, 0, 5, 5) );
		passengers.add(new Passenger("north and west" , 5, 5, 0, 10) );
		
		passengers.add(new Passenger("south and east" , 5, 5, 0, 10) );
		passengers.add(new Passenger("south and west" , 5, 5, 0, 0) );
		*/
		
		
		for(int i = 0; i < maxInitPassengers; i++) {

			int pickUpX = random_ints.nextInt(maxX);
			int pickUpY = random_ints.nextInt(maxY);

			int dropOffX = random_ints.nextInt(maxX);
			int dropOffY = random_ints.nextInt(maxY);
			
			passengers.add(new Passenger( "" + i , pickUpX, pickUpY, dropOffX, dropOffY) );

		}
		
		for (Passenger passenger : passengers) {
			System.err.println( 
					String.format("Passenger %s: PickUp(%d,%d) - Drop Off (%d,%d)", 
							passenger.getName(), 
							passenger.getPickUpX(), passenger.getPickUpY(), 
							passenger.getDropOffX(), passenger.getDropOffY())
					);
		}

		
	}

	private void moveCar(String agName, Action act, String direction) throws AILexception{

		// Don't move if direction is invalid
		int newCarX = carX;
		int newCarY = carY;

		switch(direction) {

			case "north":
				// Move up in the Y axis
				newCarY++;
				break;
			case "south":
				// Move down in the Y axis
				newCarY--;
				break;
			case "east":
				// Move right in the X axis
				newCarX++;
				break;
			case "west":
				// Move left in the X axis
				newCarX--;
				break;
			default:
				System.err.println("DON'T MOVE");

		}

		Predicate oldPosition = new Predicate("at");
		oldPosition.addTerm(new NumberTermImpl(carX));
		oldPosition.addTerm(new NumberTermImpl(carY));

		Predicate at = new Predicate("at");
		at.addTerm(new NumberTermImpl(newCarX));
		at.addTerm(new NumberTermImpl(newCarY));

		//System.err.println( String.format("Moving %s from (%d,%d) to (%d,%d)", direction, carX, carY, newCarX, newCarY) );

		// Update position of the agent in the environment
		carX = newCarX;
		carY = newCarY;

		removePercept(agName, oldPosition); //remove old position
		addPercept(agName, at); //inform new position to the agent
	}

	// Identifies agents' actions
	public Unifier executeAction(String agName, Action act) throws AILexception {

		Unifier u = new Unifier();

		if(act.getFunctor().equals("drive")) {

			Term direction = act.getTerm(0);

			moveCar(agName, act, direction.getFunctor());

		}
		else if(act.getFunctor().equals("init_gps")) {

			Predicate at = new Predicate("at");
			at.addTerm(new NumberTermImpl(carX));
			at.addTerm(new NumberTermImpl(carY));

			System.err.println("Initializing GPS");
			System.err.println("Agent " + agName + " is " + at);

			addPercept(agName, at);
		}
		else if(act.getFunctor().equals("get_ride")) {
			
			Predicate pickUpAll = new Predicate("pick_up");
			pickUpAll.addTerm(new VarTerm("X"));
			pickUpAll.addTerm(new VarTerm("Y"));

			Predicate dropOffAll = new Predicate("drop_off");
			dropOffAll.addTerm(new VarTerm("X"));
			dropOffAll.addTerm(new VarTerm("Y"));

			removeUnifiesPercept(agName, pickUpAll);
			removeUnifiesPercept(agName, dropOffAll);
			
			if(passengers.isEmpty())
			{
				Predicate noPossibleRide = new Predicate("no_possible_new_ride");
				addPercept(agName, noPossibleRide);
				
				//Predicate possibleNewRide = new Predicate("possible_new_ride");
				//removePercept(agName, possibleNewRide);
				
				System.err.println("No more rides!");
			}
			else {
				//System.err.println("new ride!");
				currentPassenger = passengers.get(0);
				passengers.remove(0);
	
				int pickUpX = currentPassenger.getPickUpX();
				int pickUpY = currentPassenger.getPickUpY();
	
				int dropOffX = currentPassenger.getDropOffX();
				int dropOffY = currentPassenger.getDropOffY();
	
				System.err.println("Pick up is in (" + pickUpX + "," + pickUpY + ")");
				System.err.println("Drop off is in (" + dropOffX + "," + dropOffY + ")");
	
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
		else if(act.getFunctor().equals("park")) {
			
			if( carX == currentPassenger.getPickUpX() && carY == currentPassenger.getPickUpY() )
			{
				System.err.println( String.format("Pick Up Passanger %s in (%d,%d)", currentPassenger.getName(), carX, carY) );
			}
			else if( carX == currentPassenger.getDropOffX() && carY == currentPassenger.getDropOffY() )
			{
				System.err.println( String.format("Drop Off Passanger %s in (%d,%d)", currentPassenger.getName(), carX, carY) );
			}

			

		}
		else if(act.getFunctor().equals("honk")) {

			System.err.println("HONK");

			Predicate noisy = new Predicate("noisy");

			addPercept(agName, noisy);
		}

		super.executeAction(agName, act);

		return u;

	}

}
