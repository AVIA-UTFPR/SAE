package _002_environment;

import ail.mas.DefaultEnvironment;
import ail.syntax.Action;
import ail.syntax.NumberTermImpl;
import ail.syntax.Predicate;
import ail.syntax.Unifier;
import ail.util.AILexception;

public class AutonomousCarEnvSimple extends DefaultEnvironment{
	
	
	// Initial position of the car
	private int car_x = 0;
	private int car_y = 0;
	
	// Identifies agents' actions
	public Unifier executeAction(String agName, Action act) throws AILexception {
		
		Unifier u = new Unifier();
		
		if(act.getFunctor().equals("north")) {
			
			Predicate old_position = new Predicate("at");
			old_position.addTerm(new NumberTermImpl(car_x));
			old_position.addTerm(new NumberTermImpl(car_y));
			
			// car_x is not altered
			car_y++; // increment one in the Y axis
			
			Predicate at = new Predicate("at");
			at.addTerm(new NumberTermImpl(car_x));
			at.addTerm(new NumberTermImpl(car_y));
			
			System.err.println("MOVING " + car_x + " " + car_y);
			
			
			removePercept(agName, old_position); //remove old position
			addPercept(agName, at); //inform new position to the agent
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
