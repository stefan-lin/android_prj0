package com.example.stefanlin.locationmap.util;

/**
 * Created by stefanlin on 8/3/16.
 */

public class KalmanFilter {
  static private double _prev_lat_est = 100.0;
  static private double _prev_lng_est = 100.0;
  static private double _prev_alt_est = 100.0;
  static private double _prev_lat_err = 0.5;
  static private double _prev_lng_err = 0.5;
  static private double _prev_alt_err = 0.5;

  //public KalmanFilter(){
  //  _prev_lat_est = 100.0;
  //  _prev_lng_est = 100.0;
  //  _prev_alt_est = 100.0;
  //  _prev_lat_err = 0.5;
  //  _prev_lng_err = 0.5;
  //  _prev_alt_err = 0.5;
  //}

  static private double get_kalman_gain(double estimate_err, double measure_err){
    return estimate_err / (estimate_err + measure_err);
  }

  static private double get_current_estimate(double kalman_gain, double prev_est, double measure){
    return prev_est + kalman_gain * (measure - prev_est);
  }

  static private double get_current_estimate_err(double kalman_gain, double prev_estimate_err){
    return (1 - kalman_gain) * prev_estimate_err;
  }

  static public double get_estimate_latitude(double measure_latitude, double measure_err){
    double kalman_gain = get_kalman_gain(_prev_lat_err, measure_err);
    double curr_estimate = get_current_estimate(kalman_gain, _prev_lat_est, measure_latitude);
    double curr_err_estimate = get_current_estimate_err(kalman_gain, _prev_lat_err);
    _prev_lat_est = curr_estimate;
    _prev_lat_err = curr_err_estimate;
    return curr_estimate;
  }

  static public double get_estimate_longitude(double measure_longitude, double measure_err){
    double kalman_gain = get_kalman_gain(_prev_lat_err, measure_err);
    double curr_estimate = get_current_estimate(kalman_gain, _prev_lat_est, measure_longitude);
    double curr_err_estimate = get_current_estimate_err(kalman_gain, _prev_lat_err);
    _prev_lat_est = curr_estimate;
    _prev_lat_err = curr_err_estimate;
    return curr_estimate;
  }

  static public double get_estimate_altitude(double measure_altitude, double measure_err){
    double kalman_gain = get_kalman_gain(_prev_lat_err, measure_err);
    double curr_estimate = get_current_estimate(kalman_gain, _prev_lat_est, measure_altitude);
    double curr_err_estimate = get_current_estimate_err(kalman_gain, _prev_lat_err);
    _prev_lat_est = curr_estimate;
    _prev_lat_err = curr_err_estimate;
    return curr_estimate;
  }
}
