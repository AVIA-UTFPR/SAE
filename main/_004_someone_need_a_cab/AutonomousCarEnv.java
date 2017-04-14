package _004_someone_need_a_cab;

import ail.mas.DefaultEnvironment;
import ail.mas.MAS;
import ail.syntax.Action;
import ail.syntax.NumberTermImpl;
import ail.syntax.Predicate;
import ail.syntax.Term;
import ail.syntax.Unifier;
import ail.util.AILexception;
import ajpf.util.choice.ProbBoolChoice;
import ajpf.util.choice.UniformIntChoice;

public class AutonomousCarEnv extends DefaultEnvironment{
	
	
	// Initial position of the car
	private int car_x = 5;
	private int car_y = 4;
	
	@Override
	public void setMAS(MAS m) {
		super.setMAS(m);
		
		//Predicate at = new Predicate("at");
		//at.addTerm(new NumberTermImpl(car_x));
		//at.addTerm(new NumberTermImpl(car_y));
		//addPercept(at);

	}
	
	private void moveCar(String agName, Action act, String direction) throws AILexception{
		
		// Don't move if direction is invalid
		int new_car_x = car_x;
		int new_car_y = car_y;
		
		switch(direction) {

			case "north": 
				// Move up in the Y axis
				new_car_y++; 
				break;
			case "south": 
				// Move down in the Y axis
				new_car_y--;
				break;
			case "east": 
				// Move right in the X axis
				new_car_x++;
				break;
			case "west": 
				// Move left in the X axis
				new_car_x--;
				break;
			default: 
				System.err.println("DON'T MOVE");
		
		}
		
		Predicate old_position = new Predicate("at");
		old_position.addTerm(new NumberTermImpl(car_x));
		old_position.addTerm(new NumberTermImpl(car_y));
		
		Predicate at = new Predicate("at");
		at.addTerm(new NumberTermImpl(new_car_x));
		at.addTerm(new NumberTermImpl(new_car_y));
		
		System.err.println("Moving " + direction +  ": From (" + car_x + "," + car_y + ")" + 
							" To (" + new_car_x + "," + new_car_y + ")");
		
		// Update position of the agent in the enviroment
		car_x = new_car_x;
		car_y = new_car_y;
		
		removePercept(agName, old_position); //remove old position
		addPercept(agName, at); //inform new position to the agent
	}
	
	// Identifies agents' actions
	public Unifier executeAction(String agName, Action act) throws AILexception {
		
		Unifier u = new Unifier();
		
		if(act.getFunctor().equals("drive")) {
			
			Term direction = act.getTerm(0);
			
			moveCar(agName, act, direction.getFunctor());
			
		}
		else if(act.getFunctor().equals("get_ride")) {
			

			int pick_up_x = 10;
			int pick_up_y = 10;
			
			int drop_off_x = 5;
			int drop_off_y = 5;
			
			
			System.err.println("Pick up is in (" + pick_up_x + "," + pick_up_y + ")");
			System.err.println("Drop off is in (" + drop_off_x + "," + drop_off_y + ")");
			
			Predicate pick_up = new Predicate("pick_up");
			pick_up.addTerm(new NumberTermImpl(pick_up_x));
			pick_up.addTerm(new NumberTermImpl(pick_up_y));
			
			Predicate drop_off = new Predicate("drop_off");
			drop_off.addTerm(new NumberTermImpl(drop_off_x));
			drop_off.addTerm(new NumberTermImpl(drop_off_y));
		
			addPercept(agName, pick_up); 
			addPercept(agName, drop_off); 
		}
		else if(act.getFunctor().equals("get_drop_off_location")) {
			
			int x = 1, y = 1;
			
			System.err.println("Drop off is in (" + x + "," + y + ")");
			
			Predicate destination = new Predicate("destination");
			destination.addTerm(new NumberTermImpl(x));
			destination.addTerm(new NumberTermImpl(y));
			
			//System.err.println(destination);
			
			addPercept(agName, destination); 
		}
		else if(act.getFunctor().equals("init_gps")) {

			car_x = 10;
			car_y = 10;
			
			Predicate at = new Predicate("at");
			at.addTerm(new NumberTermImpl(car_x));
			at.addTerm(new NumberTermImpl(car_y));

			System.err.println("Initializing GPS");
			System.err.println("Agent " + agName + " is " + at);

			addPercept(agName, at); 
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
