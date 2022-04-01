package main;

import pds.trafficlight.CardinalDirection;

/**
* A traffic light circuit with a total of 4 traffic lights,
 * each traffic light is marked by a cardinal direction.
* <br><code><b>[PVS]</b></code>
*
* @author Gruppe C1
* @version 1.00
*/
public class Intersection {

  /**
  * This is the main method. The traffic lights are constructed and activated.
  *
  * @param args - not used
  */
  public static void main(String[] args) {

    CardinalDirection startDir = CardinalDirection.NORTH;
    TrafficLight lightEast = new TrafficLight(CardinalDirection.EAST, startDir);
    lightEast.start();
    
    TrafficLight lightNorth = new TrafficLight(CardinalDirection.NORTH, startDir);
    lightNorth.start();
    
    TrafficLight lightSouth = new TrafficLight(CardinalDirection.SOUTH, startDir);
    lightSouth.start();
    
    TrafficLight lightWest = new TrafficLight(CardinalDirection.WEST, startDir);
    lightWest.start();

    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    lightEast.halt();
  }
}
