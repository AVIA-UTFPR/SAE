// ----------------------------------------------------------------------------
// Copyright (C) 2013 Louise A. Dennis, Michael Fisher
//
// This file is part of the Engineering Autonomous Space Software (EASS) Library.
// 
// The EASS Library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
// 
// The EASS Library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
// 
// You should have received a copy of the GNU Lesser General Public
// License along with the EASS Library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// 
// To contact the authors:
// http://www.csc.liv.ac.uk/~lad
//
//----------------------------------------------------------------------------

package eass.cruise_control;

import ail.syntax.Action;
import ail.syntax.Unifier;
import ail.util.AILexception;


public class ThreeLaneMotorWay extends MotorWayEnv {
	String logname = "eass.cruise_control.ThreeLaneMotorWay";
	
	public Car car1 = new Car(0, 0, 1, 1, 0, -32, 14);
	
	public Exit exit1 = new Exit(1000, 2000, 1);

	public ThreeLaneMotorWay() {
		super();
		cars.add(car1);
		exits.add(exit1);
		set_up_cars();
	}
		
}
