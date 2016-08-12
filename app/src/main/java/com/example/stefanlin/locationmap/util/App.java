package com.example.stefanlin.locationmap.util;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

/**
 * Created by stefanlin on 8/3/16.
 */

public class App extends MultiDexApplication {
  private final String _TAG          = MultiDexApplication.class.getSimpleName();
  private final String _ACCOUNT_FILE = "usr_account.json";
  private BroadcastReceiver _br = null;
  private Account           _usr_account = null;

  private ArrayList<Double> _lats = new ArrayList<>();
  private ArrayList<Double> _lngs = new ArrayList<>();
  private double _total_dis = 0.0;


  @Override
  protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(newBase);
    MultiDex.install(this);
  }

  @Override
  public void onCreate(){
    super.onCreate();

    init_account();

    this._reg_broadcast_receiver();
  }

  public boolean write_internal_storage(String file_name, String json_data){
    try {
      FileOutputStream file_out = getApplicationContext().openFileOutput(
        file_name, MODE_PRIVATE
      );
      if(file_out == null){
        return false;
      }
      OutputStreamWriter outputWriter = new OutputStreamWriter(file_out);
      if(outputWriter == null){
        return false;
      }
      outputWriter.write(json_data);
      outputWriter.close();
      //display file saved message
      Toast.makeText(getBaseContext(), "File saved successfully!",
        Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
      Log.e(this._TAG, "file read failed");
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public String read_internal_storage(String file_name){
    String ret = "";
    try{
      InputStreamReader isr = new InputStreamReader(
        getApplicationContext().openFileInput(file_name)
      );
      BufferedReader buffered_reader = new BufferedReader(isr);
      String temp;
      StringBuilder string_builder = new StringBuilder();
      while((temp = buffered_reader.readLine()) != null){
        string_builder.append(temp);
      }
      isr.close();  // CLOSE INPUT STREAM READER
      if(string_builder.length() > 0) {
        //display file saved message
        Toast.makeText(getBaseContext(), "File read successfully!",
          Toast.LENGTH_SHORT).show();
        ret = string_builder.toString();
      }
    }
    catch (FileNotFoundException e) {
      Log.e(this._TAG, "InputStreamReader error");
      e.printStackTrace();
    } catch (IOException e) {
      Log.e(this._TAG, "buffered_reader.readLine error");
      e.printStackTrace();
    }
    return (ret == "")? null: ret;
  }

  private void init_account(){
    // LOAD ACCOUNT FILE IF IT EXISTS
    if(does_file_exist(this._ACCOUNT_FILE)){
      Log.e(this._TAG, "usr_account.json exists");
      Gson gson = new GsonBuilder().create();
      String json_str = this.read_internal_storage(this._ACCOUNT_FILE);
      this._usr_account = gson.fromJson(json_str, Account.class);
      Log.e(this._TAG, this._usr_account.toString());
    }
    else{
      Log.e(this._TAG, "usr_account.json does not exist");
      this._usr_account = new Account();
      Log.e(this._TAG, "new account");
      //this._usr_account.set_name("Tom", "Jones");
      //this._usr_account.add_record(new Record());
      //this._usr_account.add_record(new Record());
      //Log.e(this._TAG, this._usr_account.toString());
      String account_str = (new Gson()).toJson(this._usr_account);
      this.write_internal_storage(this._ACCOUNT_FILE, account_str);
      //Log.e(this._TAG, account_str);
      //Account temp = (new GsonBuilder().create()).fromJson(account_str, Account.class);
      //Log.e(this._TAG, temp.toString());
    }
  }

  /**
   * PRIVATE METHOD - does_file_exist
   *
   * VALIDATE IF THE EXISTENCE OF THE INTERNAL FILE
   *
   * @param file_name: NAME OF THE TARGET FILE
   * @return TRUE: EXISTS, FALSE: NOT EXIST
   */
  private boolean does_file_exist(String file_name){
    File file = getBaseContext().getFileStreamPath(file_name);
    return file.exists();
  }

  private void _reg_broadcast_receiver(){
    this._br = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if(intent != null){
          //String[] arr = intent.getStringArrayExtra("star");
          //for(String s: arr){
          //  Log.e("[Application Class]", s);
          //} // END FOR LOOP
          double[] dbls = intent.getDoubleArrayExtra("star");
          double distance = _get_distance(dbls[0], dbls[1]);
          BigDecimal bd = new BigDecimal(distance, MathContext.DECIMAL32);
          double speed = Calculator.get_speed(distance);
          _lats.add(dbls[0]);
          _lngs.add(dbls[1]);
          _total_dis += distance;
          String arr[] = {
            String.valueOf(dbls[0]),
            String.valueOf(dbls[1]),
            String.valueOf(dbls[2]),
            String.valueOf(speed),
            String.valueOf(_total_dis),
            bd.toString()
          };
          _send_data_to_ui(arr);
        }
        else{
          Log.e("[Application Class]", "intent is null");
        }
      } // END METHOD onReceive
    };
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("test");
    registerReceiver(this._br, intentFilter);
  } // END METHOD _reg_broadcast_receiver

  private void _send_data_to_ui(String[] strings){
    Intent intent = new Intent();
    intent.setAction("location data");
    intent.putExtra("str arr", strings);
    sendBroadcast(intent);
  } // END METHOD _send_data_to_ui

  private double _get_distance(double lat, double lng){
    if(!this._lats.isEmpty() && !this._lngs.isEmpty()){
      double ret_dis = Calculator.get_distance(
        this._lats.get(this._lats.size() -1),
        this._lngs.get(this._lngs.size() - 1),
        lat,
        lng
      );
      return ret_dis;
    }
    return 0.0;
  }
}
