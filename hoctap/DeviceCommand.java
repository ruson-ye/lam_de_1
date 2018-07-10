package com.contec.jar.BC401;

import java.util.Calendar;

public class DeviceCommand { public DeviceCommand() {}
  
  public static byte[] doPack(byte[] pbyte) { int _size = pbyte.length;
    int _checkSum = 0;
    for (int i = 2; i < _size - 1; i++) {
      _checkSum += pbyte[i];
    }
    pbyte[(_size - 1)] = ((byte)_checkSum);
    return pbyte;
  }
  
  public static byte[] Request_AllData()
  {
    byte[] _request = { -109, -114, 4, 
      0, 9, 5, 18 };
    return _request;
  }
  
  public static byte[] Request_AllData_all()
  {
    byte[] _request = { -109, -114, 4, 
      0, 9, 21, 34 };
    return _request;
  }
  
  public static byte[] Delete_AllData()
  {
    byte[] _request = { -109, -114, 4, 
      0, 9, 6, 19 };
    return _request;
  }
  
  public static byte[] Synchronous_Time()
  {
    int mYear = Calendar.getInstance().get(1) - 2000;
    int mMonth = Calendar.getInstance().get(2) + 1;
    int mDay = Calendar.getInstance().get(5);
    
    int mHours = Calendar.getInstance().get(11);
    int mMinutes = Calendar.getInstance().get(12);
    
    byte[] _times = { -109, -114, 9, 
      0, 9, 2, (byte)mYear, (byte)mMonth, 
      (byte)mDay, (byte)mHours, (byte)mMinutes };
    return doPack(_times);
  }
  
  public static byte[] Synchronous_Time_NEW()
  {
    int mYear = Calendar.getInstance().get(1) - 2000;
    int mMonth = Calendar.getInstance().get(2) + 1;
    int mDay = Calendar.getInstance().get(5);
    
    int mHours = Calendar.getInstance().get(11);
    int mMinutes = Calendar.getInstance().get(12);
    int mSec = Calendar.getInstance().get(13);
    
    byte[] _times = { -109, -114, 10, 
      0, 9, 2, (byte)mYear, (byte)mMonth, 
      (byte)mDay, (byte)mHours, (byte)mMinutes, (byte)mSec };
    
    return doPack(_times);
  }
}
