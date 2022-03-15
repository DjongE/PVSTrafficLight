/*
 * CardinalDirection.java
 *
 * Copyright (c) 2021-2022 HS Emden-Leer
 * All Rights Reserved.
 *
 * @version 2.00 - 25 Feb 2022 - GJV - prepare for 22SS
 * @version 1.10 - 19 Mar 2021 - GJV - cardinality added
 * @version 1.00 - 17 Mar 2021 - GJV - initial version
 */

package pds.trafficlight;

/**
 * Representation of the four cardinal directions.
 * <br><code><b>[PVS]</b></code>
 *
 * @author Gert Veltink
 * @version 2.00 - 25 Feb 2022
 */
public enum CardinalDirection {
  /** the cardinal direction: north. */
  NORTH,
  /** the cardinal direction: east. */
  EAST,
  /** the cardinal direction: south. */
  SOUTH,
  /** the cardinal direction: west. */
  WEST;

  /**
   * Exposes the number of elements of the enumeration.
   * The number of elements is determined once and provided statically thereafter.
   */
  public static final int cardinality;
  static {
    cardinality = values().length;
  }

  /**
   * Returns the opposite cardinal direction.
   *
   * @param cd a cardinal direction
   * @return the opposite cardinal direction
   */
  public static CardinalDirection opposite(final CardinalDirection cd) {
    switch (cd) {
      case NORTH:
        return SOUTH;
      case EAST:
        return WEST;
      case SOUTH:
        return NORTH;
      default:
        return EAST;                        // catch all
    }
  }


  /**
   * Returns the next cardinal direction. Order: north - east - south - west - north - ...
   *
   * @param cd a cardinal direction
   * @return the next cardinal direction
   */
  public static CardinalDirection next(final CardinalDirection cd) {
    switch (cd) {
      case NORTH:
        return EAST;
      case EAST:
        return SOUTH;
      case SOUTH:
        return WEST;
      default:
        return NORTH;                          // catch all
    }
  }
}
