package Main;

import pds.trafficlight.CardinalDirection;

public class Intersection {

	public static void main(String[] args) {

		// Aufgabe a
		/*
		 * TrafficLight light_east = new TrafficLight(CardinalDirection.EAST, null);
		 * TrafficLight light_north = new TrafficLight(CardinalDirection.NORTH, null);
		 * TrafficLight light_south = new TrafficLight(CardinalDirection.SOUTH, null);
		 * TrafficLight light_west = new TrafficLight(CardinalDirection.WEST, null);
		 * 
		 * light_east.start(); light_north.start(); light_south.start();
		 * light_west.start();
		 */

		// Aufgabe b
		CardinalDirection startDir = CardinalDirection.NORTH;
		TrafficLight light_east = new TrafficLight(CardinalDirection.EAST, startDir);
		TrafficLight light_north = new TrafficLight(CardinalDirection.NORTH, startDir);
		TrafficLight light_south = new TrafficLight(CardinalDirection.SOUTH, startDir);
		TrafficLight light_west = new TrafficLight(CardinalDirection.WEST, startDir);

		light_east.start();
		light_north.start();
		light_south.start();
		light_west.start();

		// Aufgabe c
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		light_east.halt();
		//light_north.halt();
		//light_south.halt();
		//light_west.halt();
	}
}
