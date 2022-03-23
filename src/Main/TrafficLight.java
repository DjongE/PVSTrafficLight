package Main;

import pds.trafficlight.CardinalDirection;
import pds.trafficlight.Colour;
import pds.trafficlight.Reporter;

public class TrafficLight extends Thread {

	private CardinalDirection cd; // Aktueller Standort der Ampel
	private static volatile CardinalDirection dir;// Shared Memory, welche Ampel Richtung schalten darf
	
	private Colour color; // Farbe der aktuellen Ampel
	private static volatile Colour mainColor; // Farbe der "Hauptampel" (die zuerst in die mutual exclusion rein gegangen ist)
	private static volatile Colour oppColor; // Farbe der gegenüberliegenden Ampel z.B. North --> South
	private static volatile Colour nextColor; // Die nächste Farbe, auf die die Ampeln geschaltet werden sollen

	private static volatile boolean stopped = false; // Solange wie auf false gesetzt ist, laufen die Ampeln
	
	private static final Object lock = new Object(); // Mutual Exclusion lock Object

	public TrafficLight(CardinalDirection cd, CardinalDirection dir) {
		color = Colour.RED;
		oppColor = Colour.RED;
		nextColor = Colour.GREEN;
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
			synchronized (lock) { // mutual exclusion, xxxxxxxx	
				if (cd == dir) {
					
						if(color != nextColor) { // Wenn aktuelle Farbe ungleich nächste (erwartete) Farbe
							color = Colour.next(color); // Schalte aktuelle Ampel auf die nächste Farbe
							Reporter.show(cd, color);
							mainColor = color; // Die Farbe der Hauptampel der aktuellen Axis
						}
						
						if(oppColor == color) { // Wenn gegenüberliegende Farbe gleich ist, wie die aktuelle Ampel
							nextColor = Colour.next(nextColor); // Nächste Ampel Farbe bestimmen
							
							if(nextColor == Colour.GREEN) { // Wenn die nächste erwartete Farbe GREEN ist, wird die Axis geschaltet
								dir = CardinalDirection.next(cd);
							}
						}
					
				}else if(cd == CardinalDirection.opposite(dir)) {
					
					if(color != nextColor) {
						color = Colour.next(color);
						Reporter.show(cd, color);
						oppColor = color;
					}
					
					if(mainColor == color) {
						nextColor = Colour.next(nextColor);
						
						if(nextColor == Colour.GREEN) {
							dir = CardinalDirection.next(cd);
						}
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
