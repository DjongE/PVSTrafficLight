/*
 * Colour.java
 *
 * Copyright (c) 2021-2022 HS Emden-Leer
 * All Rights Reserved.
 *
 * @version 2.00 - 25 Feb 2022 - GJV - prepare for 22SS
 * @version 1.00 - 17 Mar 2021 - GJV - initial version
 */

package pds.trafficlight;

/**
 * Representation of the colours of a traffic light.
 * <br><code><b>[PVS]</b></code>
 *
 * @author Gert Veltink
 * @version 2.00 - 25 Feb 2022
 */
public enum Colour {
  /** the colour red. */
  RED,
  /** the colour green. */
  GREEN,
  /** the colour yellow. */
  YELLOW;


  /**
   * Returns the colour of the next phase of a traffic light.
   * Order: red - green - yellow - red - ...
   *
   * @param c   a colour
   * @return    the colour of the next phase
   */
  public static Colour next(Colour c) {
    switch (c) {
      case RED:
        return GREEN;
      case GREEN:
        return YELLOW;
      default:
        return RED; // catch all
    }
  }
}
