package com.example.stefanlin.locationmap.util;

import java.util.ArrayList;

/**
 * Created by stefanlin on 8/4/16.
 */

public class Account {
  private String _first_name;
  private String _last_name;
  private ArrayList<Record> _records = null;

  public Account(){
    this._records = new ArrayList<>();
  } // END CONSTRUCTOR

  public void add_record(Record r){
    this._records.add(r);
  }

  public void display(){
    for(Record r: this._records){
      //TODO: display record here (individually)
    }
  } // END METHOD display

  public void set_name(String f, String l){
    this._first_name = f;
    this._last_name = l;
  }

  public String toString(){
    String ret = "";
    ret += "FIRST NAME = " + this._first_name + "\n";
    ret += "LAST  NAME = " + this._last_name  + "\n";
    for(Record r: this._records){
      ret += r.toString() + "\n";
    }
    return ret;
  }
}
