package com.example.stefanlin.locationmap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

public class MapsActivity extends Activity {
  private TextView _txt_lng = null;
  private TextView _txt_lat = null;
  private TextView _txt_alt = null;
  private TextView _txt_clk = null;
  private TextView _txt_spd = null;
  private TextView _txt_dis = null;  // total distance
  private TextView _txt_dst = null;  // short distance
  private Button   _se_bttn = null;
  private BroadcastReceiver _br = null;
  private boolean  _onoff   = false;

  private Handler  _handler = null;
  private Runnable _clk_job = null;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    this._txt_lat = (TextView) findViewById(R.id.txtLng);
    this._txt_lng = (TextView) findViewById(R.id.txtLat);
    this._txt_alt = (TextView) findViewById(R.id.txtAlt);
    this._txt_clk = (TextView) findViewById(R.id.txtClock);
    this._txt_spd = (TextView) findViewById(R.id.txtSpeed);
    this._txt_dis = (TextView) findViewById(R.id.txtTotalDis);
    this._txt_dst = (TextView) findViewById(R.id.txtDis);
    this._se_bttn = (Button)   findViewById(R.id.button0);

    /* INIT CLOCK UPDATING THREAD */
    this._handler = new Handler();
    this._clk_job = new Runnable() {
      @Override
      public void run() {
        Calendar c = Calendar.getInstance();
        String t = (new StringBuilder()
          .append(c.get(Calendar.HOUR_OF_DAY)).append(":")
          .append(c.get(Calendar.MINUTE)).append(":")
          .append(c.get(Calendar.SECOND)).append(" ")).toString();
        _txt_clk.setText(t);

        /* and here comes the "trick" */
        _handler.postDelayed(this, 100);
      }
    };
    _handler.postDelayed(_clk_job, 100); // start clock


    this._se_bttn.setOnClickListener(new Button.OnClickListener(){
      @Override
      public void onClick(View v) {
        if(!_onoff){ // start tracking
          Intent intent = new Intent(MapsActivity.this, BackgroundService.class);
          startService(intent);
          _onoff = true;
        }
        else{ // stop tracking
          stopService(new Intent(MapsActivity.this, BackgroundService.class));
          _onoff = false;
        }
      }
    });
  }


  @Override
  protected void onStart(){
    super.onStart();
    Log.e("main", "onstart method");
    this._br = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String[] arr = intent.getStringArrayExtra("str arr");
        _txt_lat.setText("Lat = " + arr[0]);
        _txt_lng.setText("Lng = " + arr[1]);
        _txt_alt.setText("Alt = " + arr[2]);
        _txt_spd.setText("Spd = " + arr[3]);
        _txt_dis.setText("T.Dis = " + arr[4]);
        _txt_dst.setText("Dis = " + arr[5]);
      }
    };
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("location data");
    registerReceiver(this._br, intentFilter);
    Log.e("main", "starting service");
    //Start our own service
    //Intent intent = new Intent(MapsActivity.this, BackgroundService.class);
    //startService(intent);
  }

  @Override
  protected void onStop(){
    unregisterReceiver(this._br);
    super.onStop();
  }
}


/*
public class MapsActivity extends Activity implements OnConnectionFailedListener, ConnectionCallbacks {
  private TextView _txt_lng = null;
  private TextView _txt_lat = null;
  private TextView _txt_alt = null;
  private GoogleApiClientSingleton _gacs = null;
  private BroadcastReceiver _br = null;
  private boolean _br_registration = false;
  private GoogleApiClient _gac = null;
  private Handler _hand = null;
  private GoogleApiClient client;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    this._txt_lat = (TextView) findViewById(R.id.txtLong);
    this._txt_lng = (TextView) findViewById(R.id.txtLat);
    this._txt_alt = (TextView) findViewById(R.id.txtAlt);

    this._gac = new Builder(this)
      .addApi(LocationServices.API)
      .addConnectionCallbacks(this)
      .addOnConnectionFailedListener(this)
      .build();

    // set up handler
    this._hand = new Handler(Looper.getMainLooper()) {
      @Override
      public void handleMessage(Message message) {
        if (message != null) {
          Bundle bdl = message.getData();
          ArrayList<String> arr = bdl.getStringArrayList("storage");
          _txt_lat.setText(arr.get(0));
          _txt_lng.setText(arr.get(1));
          _txt_alt.setText(arr.get(2));
        }
      }
    };
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client = new Builder(this).addApi(AppIndex.API).build();
  }

  @Override
  protected void onStart() {
    super.onStart();
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client.connect();
    this._gac.connect();
    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    Action viewAction = Action.newAction(
      Action.TYPE_VIEW, // TODO: choose an action type.
      "Maps Page", // TODO: Define a title for the content shown.
      // TODO: If you have web page content that matches this app activity's content,
      // make sure this auto-generated web page URL is correct.
      // Otherwise, set the URL to null.
      Uri.parse("http://host/path"),
      // TODO: Make sure this auto-generated app URL is correct.
      Uri.parse("android-app://com.example.stefanlin.locationmap/http/host/path")
    );
    AppIndex.AppIndexApi.start(client, viewAction);
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    (new Thread(new LocService(this, this._gac, this._hand))).start();
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.i("error", "GoogleApiClient connection has been suspend");
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.i("error", "GoogleApiClient connection has failed");
  }

  @Override
  public void onStop() {
    super.onStop();

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    Action viewAction = Action.newAction(
      Action.TYPE_VIEW, // TODO: choose an action type.
      "Maps Page", // TODO: Define a title for the content shown.
      // TODO: If you have web page content that matches this app activity's content,
      // make sure this auto-generated web page URL is correct.
      // Otherwise, set the URL to null.
      Uri.parse("http://host/path"),
      // TODO: Make sure this auto-generated app URL is correct.
      Uri.parse("android-app://com.example.stefanlin.locationmap/http/host/path")
    );
    AppIndex.AppIndexApi.end(client, viewAction);
    client.disconnect();
  }*/
  /*
  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    // 1. set content view
    setContentView(R.layout.activity_maps);

    // 2. get reference to TextView
    this._txt_lat = (TextView)findViewById(R.id.txtLong);
    this._txt_lng = (TextView)findViewById(R.id.txtLat);
    this._txt_alt = (TextView)findViewById(R.id.txtAlt);

    //
    this._gacs = GoogleApiClientSingleton.get(this);
    this._gacs.connect();

    //
    this._br = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(LocationService.LOCATION_FILTER)){
          String result[] = intent.getExtras().getStringArray("LocationInfoString");
          _txt_lat.setText(result[0]);
          _txt_lng.setText(result[1]);
          _txt_alt.setText(result[2]);
        }
      }
    };

    //
    IntentFilter intentFilter = new IntentFilter(LocationService.LOCATION_FILTER);
    registerReceiver(this._br, intentFilter);
    this._br_registration = true;
  } // END onCreate

  @Override
  protected void onStart() {
    super.onStart();
    // 1. connect the client.
    //this._gacs.connect();
    new LocationService(
      new ContextWrapper(this.getApplicationContext()),
      this._gacs.get_google_api_client()
    );
  }

  @Override
  protected void onStop() {
    super.onStop();
    // 1. disconnecting the client invalidates it.
    this._gacs.disconnect();
    unregisterReceiver(this._br);
    this._br_registration = false;
  }

  @Override
  protected void onResume(){
    super.onResume();
    if(!this._br_registration){
      registerReceiver(this._br, new IntentFilter(
        LocationService.LOCATION_FILTER
      ));
      this._br_registration = true;
    }
  }

  @Override
  protected void onPause(){
    super.onPause();
    if(this._br_registration){
      unregisterReceiver(this._br);
      this._br_registration = false;
    }
  }
}*/

/*
public class MapsActivity extends Activity implements
  GoogleApiClient.ConnectionCallbacks,
  GoogleApiClient.OnConnectionFailedListener,
  LocationListener {

  private GoogleApiClient _gac = null;
  private LocationRequest _loc_req = null;
  private TextView _txt_lng = null;
  private TextView _txt_lat = null;
  private Location _curr_loc = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // 1. set content view
    setContentView(R.layout.activity_maps);

    // 2. get reference to TextView
    this._txt_lng = (TextView) findViewById(R.id.txtLong);
    this._txt_lat = (TextView) findViewById(R.id.txtLat);

    // 3. create google api client
    this._gac = new GoogleApiClient.Builder(this)
      .addApi(LocationServices.API)
      .addConnectionCallbacks(this)
      .addOnConnectionFailedListener(this)
      .build();
  }

  @Override
  protected void onStart() {
    super.onStart();
    // 1. connect the client.
    this._gac.connect();
  }

  @Override
  protected void onStop() {
    super.onStop();
    // 1. disconnecting the client invalidates it.
    this._gac.disconnect();
  }

  @Override
  public void onConnected(Bundle bundle) {

    this._loc_req = LocationRequest.create();
    this._loc_req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    this._loc_req.setInterval(1000); // Update location every second

    if (ActivityCompat.checkSelfPermission(
      this,
      android.Manifest.permission.ACCESS_FINE_LOCATION) !=
      PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
        this,
      android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
      PackageManager.PERMISSION_GRANTED
      ) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      this._txt_lat.setText("Error");
      this._txt_lng.setText("insufficient permission");
      return;
    }
    LocationServices.FusedLocationApi.requestLocationUpdates(
      this._gac,
      this._loc_req,
      this
    );
  }

  @Override
  public void onConnectionSuspended(int i) {
    Log.i("error", "GoogleApiClient connection has been suspend");
  }

  @Override
  public void onLocationChanged(Location location) {
    this._txt_lat.setText(String.valueOf(location.getLatitude()));
    this._txt_lng.setText(String.valueOf(location.getLongitude()));
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    Log.i("error", "GoogleApiClient connection has failed");
  }
}*/
