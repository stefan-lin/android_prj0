package com.example.stefanlin.locationmap.util;

/**
 * Created by stefanlin on 8/3/16.
 */
public class Calculator {
  final static private Double _EARTH_RADIUS_MILE  = 3958.75;
  final static private Double _EARTH_RADIUS_METER = 6371.00;
  final static private String _DISTANCE_FOR_INVALID_PATH = "0.00";

  public static double get_distance(
      double lat0, double lng0,
      double lat1, double lng1){
    //TODO: making this able to change the units of calculation (meter vs mile)
    double lat_diff = Math.toRadians(lat0 - lat1);
    double lng_diff = Math.toRadians(lng0 - lng1);
    double a = Math.sin(lat_diff /2) * Math.sin(lat_diff /2) +
        Math.cos(Math.toRadians(lat0)) *
        Math.cos(Math.toRadians(lat1)) *
        Math.sin(lng_diff /2) * Math.sin(lng_diff /2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return _EARTH_RADIUS_MILE * c;
  } // END METHOD get_distance

  public static double get_speed(double distance){
    return distance / ( 2 / 3600.00 );
  }
}
