public class SearchAndRescueEnv extends DefaultEnvironment {

	double rubble_x = 5;
	double rubble_y = 5;

	boolean robot_rubble = false;

	public Unifier executeAction(String agName, Action act) throws AILexception {
		Unifier u = new Unifier();

		if (act.getFunctor().equals("move_to")) {
			double x = ((NumberTerm) act.getTerm(0)).solve();
			double y = ((NumberTerm) act.getTerm(1)).solve();

			Predicate at = new Predicate("at");
			at.addTerm(new NumberTermImpl(x));
			at.addTerm(new NumberTermImpl(y));

			Predicate old_pos = new Predicate("at");
			old_pos.addTerm(new NumberTermImpl(robot_x));
			old_pos.addTerm(new NumberTermImpl(robot_y));

			removePercept(agName, old_pos);
			addPercept(agName, at);

			robot_x = x;
			robot_y = y;

			if (robot_y == rubble_y && robot_x == rubble_x && !robot_rubble) {
				Predicate rubble = new Predicate("rubble");
				rubble.addTerm(new NumberTermImpl(rubble_x));
				rubble.addTerm(new NumberTermImpl(rubble_y));

				addPercept(agName, rubble);
			}
		}
		else if (act.getFunctor().equals("lift_rubble")) {
			if (robot_x == rubble_x) {
				if (robot_y == rubble_y && !robot_rubble1) {
					Predicate rubble = new Predicate("rubble");
					rubble.addTerm(new NumberTermImpl(rubble_x));
					rubble.addTerm(new NumberTermImpl(rubble_y));

					removePercept(agName, rubble);

					Predicate holding = new Predicate("holding");
					holding.addTerm(new Predicate("rubble"));
					addPercept(agName, holding);
					robot_rubble = true;
				}
			}
		}
		super.executeAction(agName, act);
	}

}
