package Main;

import pds.trafficlight.CardinalDirection;
import pds.trafficlight.Colour;
import pds.trafficlight.Reporter;

public class TrafficLight extends Thread {

	CardinalDirection cd;
	private static volatile CardinalDirection dir;//x
	Colour color;

	private static volatile boolean mainAxis;//andere Bezeichnung z.B. mainaxis

	static volatile boolean stopped = false;

	public TrafficLight(CardinalDirection cd, CardinalDirection dir) {
		this.color = Colour.RED;
		this.cd = cd;
		TrafficLight.dir = dir;
	}

	@Override
	public void run() {
		// Aufgabe a

		/*
		 * while (true) { Reporter.show(cd, color); color = Colour.next(color); }
		 */

		// Aufgabe b
		Reporter.show(cd, color);

		while (!stopped) {
			synchronized (this) { // mutual exclusion, xxxxxxxx
				if (cd == dir) {
					color = Colour.next(color);
					Reporter.show(cd, color);

					if (color == Colour.RED) {
						// System.out.println(cd + ": " + isRed);
						if (!mainAxis) {
							dir = CardinalDirection.opposite(cd);
							mainAxis = true;
						} else {
							dir = CardinalDirection.next(cd);//xxxxxxxxxx
							mainAxis = false;
						}
					} else {
						dir = CardinalDirection.opposite(cd);
					}
				}
			}
		}
	}

	// Aufgabe c
	public void halt() {//xxxxxxxxxx
		stopped = true;
	}
}
