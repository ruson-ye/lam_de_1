package com.contec.jar.BC401;

import android.util.Log;






public class PrintBytes
{
  public PrintBytes() {}
  
  public static void printData(byte[] pack)
  {
    Log.i("***********************", "************************");
    String _temp = "";
    for (int i = 0; i < pack.length; i++) {
      if (i >= 3)
      {
        if ((i - 2) % 7 == 1) {
          Log.i("Data", _temp);
          _temp = "";
        } }
      _temp = _temp + " " + Integer.toHexString(pack[i]);
    }
    Log.e("Data", _temp);
  }
  




  public static void printData(byte[] pack, int count)
  {
    Log.e("***********************", "************************");
    String _temp = "";
    for (int i = 0; i < count; i++) {
      _temp = _temp + " " + Integer.toHexString(pack[i]);
    }
    
    Log.e("Data", _temp);
    Log.e("Data", "数据的长度" + count);
    Log.e("***********************", "************************");
  }
  




  public static void printDatai(byte[] pack, int count)
  {
    Log.i("***********************", "************************");
    String _temp = "";
    for (int i = 0; i < count; i++) {
      _temp = _temp + " " + Integer.toHexString(pack[i]);
    }
    
    Log.i("Data", _temp);
    Log.i("***********************", "************************");
  }
}
