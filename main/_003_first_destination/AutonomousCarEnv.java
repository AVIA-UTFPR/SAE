package _003_first_destination;

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
	private int car_x = 0;
	private int car_y = 0;
	
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
		
		System.err.println(direction);
		
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
		
		System.err.println("MOVING " + new_car_x + " " + new_car_y);
		
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
		else if(act.getFunctor().equals("honk")) {
			
			System.err.println("HONK");
			
			Predicate noisy = new Predicate("noisy");
			
			addPercept(agName, noisy); //inform new position to the agent
		}
		
		super.executeAction(agName, act);
		
		return u;
		
	}

}
