package main;

import pds.trafficlight.CardinalDirection;
import pds.trafficlight.Colour;
import pds.trafficlight.Reporter;

/**
 * The traffic light circuit provides safe signalling.
 * The traffic lights communicate via the shared memory,
 * each traffic light is represented by its own thread.
 * <br><code><b>[PVS]</b></code>
 *
 * @author Gruppe C1
 * @version 1.00
 */
public class TrafficLight extends Thread {
  private CardinalDirection cd;
  private static volatile CardinalDirection dir;

  private Colour color; // Colour of the traffic light
  private static volatile Colour mainColor; // Colour of the "main traffic light"
  private static volatile Colour oppositeColor; // Colour of the opposite traffic light
  private static volatile Colour nextColor; // Next (expected) traffic light colour

  private static volatile boolean stopped = false;

  private static final Object lock = new Object();

  /**
   * Constructor of the traffic light.
   *
   * @param cd  this is an cardinal direction of the location of this traffic light
   * @param dir this direction of the sky indicates which direction of the
    *            traffic light is allowed to start and the opposite traffic
    *            light is also allowed to start.
   */
  public TrafficLight(CardinalDirection cd, CardinalDirection dir) {
    color = Colour.RED;
    oppositeColor = Colour.RED;
    nextColor = Colour.GREEN;
    this.cd = cd;
    TrafficLight.dir = dir;
  }

  /**
   * This is the loop that switches the traffic lights.
   */
  @Override
  public void run() {

    Reporter.show(cd, color);

    while (!stopped) {
      synchronized (lock) { // mutual exclusion (critical area)
        if (cd == dir || cd == CardinalDirection.opposite(dir)) {

          if (color != nextColor) { // If current colour is not equal to next (expected) colour
            color = Colour.next(color); // Switch current traffic light to the next colour
            Reporter.show(cd, color);
            if (cd == dir) {
              mainColor = color; // The colour of the current traffic light
            } else {
              oppositeColor = color; // The colour of the traffic light opposite
            }
          }

          if (oppositeColor == mainColor) { // opposite color is the same as the main color
            nextColor = Colour.next(nextColor); // Next (expected) traffic light colour

            if (nextColor == Colour.GREEN) { // If expected colour is green, the Axis will switch
              dir = CardinalDirection.next(cd);
            }
          }
        }
      }
    }
  }

  /**
  * The traffic light is called to stop.
  * Here, only one of the 4 traffic lights needs to receive the information,
   * as the information is stored in a shared variable.
  * This will turn off all the traffic lights.
  */
  public void halt() {
    stopped = true;
  }
}
