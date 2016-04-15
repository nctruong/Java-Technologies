package tutorialspoint.dateTime;

import java.util.Calendar;
import java.util.Formatter;

public class CurrentDateTime {
	public static void main(String args[]) {
		Formatter fmt = new Formatter();
		Calendar cal = Calendar.getInstance();
		fmt = new Formatter();
		fmt.format("%tc", cal);
		System.out.println(fmt);
	}
}
