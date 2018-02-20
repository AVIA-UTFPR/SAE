package util;

import ail.syntax.NumberTerm;
import ail.syntax.Term;

public class Util {

	public static int getIntTerm(Term term) 
	{
		return (int) ( ( NumberTerm ) term ).solve();
	}
	
	public static String getStringTerm(Term term) 
	{
		return term.getFunctor();
	}
	
}
