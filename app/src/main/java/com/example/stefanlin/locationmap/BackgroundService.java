package com.example.stefanlin.locationmap;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by stefanlin on 7/31/16.
 */
public class BackgroundService extends Service implements
  LocationListener, ConnectionCallbacks, OnConnectionFailedListener {

  private static final String _TAG = "[Background Service]";
  private GoogleApiClient _google_api_client = null;
  private LocationRequest _location_request = null;
  private boolean _processing_loc = false;

  private void _start_tracking() {
    Log.d(_TAG, "start tracking");
    if (GoogleApiAvailability.getInstance()
      .isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
      this._google_api_client = new GoogleApiClient.Builder(
        this
      ).addApi(LocationServices.API)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this).build();
      /* VALIDATE GOOGLE API CLIENT INSTANCE AND CONNECT */
      if (!this._google_api_client.isConnected() &&
        !this._google_api_client.isConnecting()) {
        this._google_api_client.connect();
      }
    } // END IF STATEMENT
    else {
      Log.d(_TAG, "unable to establish Google API Service");
    }
  } // END METHOD _start_tracking

  private void _stop_tracking() {
    if (this._google_api_client != null &&
      this._google_api_client.isConnected()) {
      this._google_api_client.disconnect();
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    /* CHECK IF CURRENTLY ALREADY STARTED PROCESSING LOCATION */
    if (!this._processing_loc) {
      this._processing_loc = true;
      this._start_tracking();
    }
    return START_NOT_STICKY;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    Log.d(_TAG, "onConnected");

    this._location_request = LocationRequest.create();
    this._location_request.setInterval(2000); // update every two seconds
    this._location_request.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
    this._location_request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    if (ActivityCompat.checkSelfPermission(
        this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
          this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
          PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    LocationServices.FusedLocationApi.requestLocationUpdates(
      this._google_api_client, this._location_request, this
    );
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.e(_TAG, "GoogleApiClient connection has been suspend");
  }

  @Override
  public void onLocationChanged(Location location) {
    if (location != null) {
      String lat = String.valueOf(location.getLatitude());
      String lng = String.valueOf(location.getLongitude());
      String alt = String.valueOf(location.getAltitude());
      Log.e(
        _TAG,
        "position: " + lat + ", " +
                       lng + ", " +
                       alt + " accuracy: " +
                       location.getAccuracy()
      );

      String arr[] = {lat, lng, alt};
      Intent intent = new Intent();
      intent.setAction("test");
      intent.putExtra("star", arr);
      sendBroadcast(intent);

      /* ABORT IF THE ACCURACY IS TOO LOW */
      //if (location.getAccuracy() < 500.0f) {
      //  this._stop_tracking();
      //}
    }
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.e(_TAG, "onConnectionFailed");
    this._stop_tracking();
    stopSelf();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
