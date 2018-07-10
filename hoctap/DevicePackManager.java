package com.contec.jar.BC401;

import android.util.Log;

public class DevicePackManager {
  public DevicePackManager() {}
  
  int mDataLen = 0;
  int mTotal_Pack = 0;
  int mCurrent_Pack = 0;
  int mPack_Data_Count = 0;
  int mRec_DataCount = 0;
  public int mVersion;
  
  public BC401_Data mBc401_Data = new BC401_Data();
  public int Percent = 0;
  public int PercentAll = 0;
  
  public int PackLength(byte pOrder) {
    int _len = 0;
    switch (pOrder) {
    case -109: 
      _len = 6;
      break;
    case 5: 
      _len = 9;
    case 21: 
      _len = 9;
      break;
    case 6: 
      _len = 7;
      break;
    case 8: 
      _len = 8;
    }
    
    return _len;
  }
  
  public int PackLength(byte pOrder, byte[] pack) {
    int _len = 0;
    switch (pOrder) {
    case 2: 
      int lenght = pack[2];
      Log.e("输出数据的长度", "传统设备数据长度" + lenght);
      if (lenght == 9) {
        _len = 12;
      } else if (lenght == 10) {
        _len = 13;
      }
      else {
        _len = 14;
      }
      
      break;
    }
    
    return _len;
  }
  
  boolean bGetPackId = false;
  byte[] curPack = new byte[12];
  int k = 0;
  int len = 0;
  int i;
  byte value;
  
  public byte arrangeMessage(byte[] buf, int length) {
    PrintBytes.printData(buf, length);
    
    byte _return = 0;
    for (i = 0; i < length; i += 1)
    {
      value = buf[i];
      
      if (bGetPackId)
      {
        curPack[(k++)] = value;
        
        if (k >= len) {
          if (len == 6) {
            if (curPack[5] == 2) {
              len = PackLength(curPack[5], buf);
            } else {
              len = PackLength(curPack[5]);
            }
            byte[] _data = new byte[curPack.length];
            for (int j = 0; j < curPack.length; j++) {
              _data[j] = curPack[j];
            }
            curPack = new byte[len];
            for (int j = 0; j < _data.length; j++) {
              curPack[j] = _data[j];
            }
          } else if (len == 9) {
            byte a = buf[5];
            Log.e("输出当前的命令为多少", a);
            if (a == 5) {
              Log.e("旧版传统蓝牙返回的值", length);
              len = (9 + curPack[8] * 14 + 1);
            } else {
              Log.e("新版传统蓝牙返回的值", length);
              len = (9 + curPack[8] * 30 + 1);
            }
            byte[] _data = new byte[curPack.length];
            for (int j = 0; j < curPack.length; j++) {
              _data[j] = curPack[j];
            }
            curPack = new byte[len];
            for (int j = 0; j < _data.length; j++)
              curPack[j] = _data[j];
          } else {
            bGetPackId = false;
            _return = processData(curPack);
          }
          
        }
        
      }
      else if (PackLength(value) > 0)
      {



        bGetPackId = true;
        k = 0;
        len = PackLength(value);
        curPack = new byte[len];
        curPack[(k++)] = value;
      }
    }
    

    return _return;
  }
  
  public byte processData(byte[] pack)
  {
    byte _return = 0;
    switch (pack[5]) {
    case 2: 
      Log.e("发送对时命令操作", "对时成功,打印程序版本的命令");
      
      int lendata = pack.length;
      Log.e("发送对时命令操作", "输出数据的长度" + lendata);
      if (lendata == 14) {
        int mVerIndex = pack.length - 2;
        mVersion = (pack[mVerIndex] & 0xFF);
      } else {
        mVersion = 0;
      }
      Log.e("当前的版本程序", "程序版本" + mVersion);
      _return = 2;
      break;
    case 5: 
      Log.e("发送全部数据接收命令", "返回一组尿液分析仪的数据(适用于非定量显示、不兼容14项试纸)");
      

      mBc401_Data.Percent = (((pack[7] & 0xFF) + 1) * 100 / (pack[6] & 0xFF));
      Percent = (((pack[7] & 0xFF) + 1) * 100 / (pack[6] & 0xFF));
      if (mVersion == 1) {
        Log.e("如果返回的版本是1", "不存储这次数据处理数据");
      }
      else {
        Log.e("如果返回的版本不是1", "存储这次数据处理数据");
        dealDPack(pack);
      }
      
      if (Percent == 100)
      {
        _return = 5;
      }
      else
      {
        _return = 0;
      }
      break;
    case 21: 
      Log.e("发送全部数据接收命令", "返回一组尿液分析仪的数据(适用于定量显示、兼容14项试纸)");
      

      mBc401_Data.PercentAll = ((pack[7] & 0xFF) * 100 / (pack[6] & 0xFF));
      PercentAll = (((pack[7] & 0xFF) + 1) * 100 / (pack[6] & 0xFF));
      



      dealDPackall(pack);
      
      if (PercentAll == 100) {
        _return = 21;
      } else {
        _return = 0;
      }
      break;
    case 6: 
      Log.e("发送数据删除指令", "删除成功");
      _return = 6;
      break;
    case 8: 
      _return = 8;
    }
    
    

    return _return;
  }
  



  public void dealDPack(byte[] pPack)
  {
    int _dataCount = pPack[8] & 0xFF;
    for (int i = 0; i < _dataCount; i++) {
      byte[] _data = new byte[14];
      for (int j = 0; j < 14; j++) {
        _data[j] = pPack[(9 + j + i * 14)];
      }
      

      BC401_Struct _struct = unPack(_data);
      mBc401_Data.Structs.add(_struct);
    }
  }
  



  public void dealDPackall(byte[] pPack)
  {
    int _dataCount = pPack[8] & 0xFF;
    for (int i = 0; i < _dataCount; i++) {
      byte[] _data = new byte[30];
      for (int j = 0; j < 30; j++) {
        _data[j] = pPack[(9 + j + i * 30)];
      }
      


      BC401_Struct _struct = unPackall(_data);
      mBc401_Data.Structs.add(_struct);
    }
  }
  




  public BC401_Struct unPack(byte[] pData)
  {
    byte[] _data = pData;
    BC401_Struct _BC01 = new BC401_Struct();
    
    ID = ((_data[0] | (_data[1] & 0xFF) << 8) & 0x3FF);
    User = ((_data[1] & 0xFF) >> 2 & 0x1F);
    
    Year = (_data[2] & 0x7F);
    

    Month = (((_data[2] & 0xFF) >> 7 | (_data[3] & 0xFF) << 1) & 0xF);
    Date = ((_data[3] & 0xFF) >> 3 & 0x1F);
    
    Hour = (_data[4] & 0x1F);
    Min = (((_data[4] & 0xFF) >> 5 | _data[5] << 3) & 0x3F);
    
    Sec = (_data[6] & 0x7F);
    
    Item = ((_data[8] & 0xFF | (_data[9] & 0xFF) << 8) & 0x7FF);
    
    if ((Item & 0x400) > 0) {
      URO = ((byte)((_data[9] & 0xFF) >> 3 & 0x7));
      URO1 = 999;
      URO1_Real = 999;
      URO_Real = 0;
    } else {
      URO = 9;
      URO_Real = 9;
      URO1 = 999;
      URO1_Real = 999;
    }
    if ((Item & 0x200) > 0) {
      BLD = ((byte)(_data[10] & 0x7));
      BLD1 = 999;
      BLD1_Real = 999;
      BLD_Real = 0;
    } else {
      BLD = 9;
      BLD_Real = 9;
      BLD1 = 999;
      BLD1_Real = 999;
    }
    
    if ((Item & 0x100) > 0) {
      BIL = ((byte)((_data[10] & 0xFF) >> 3 & 0x7));
      BIL1 = 999;
      BIL1_Real = 999;
      BIL_Real = 0;
    } else {
      BIL = 9;
      BIL_Real = 9;
      BIL1 = 999;
      BIL1_Real = 999;
    }
    if ((Item & 0x80) > 0) {
      KET = ((byte)(((_data[10] & 0xFF) >> 6 | (_data[11] & 0x1) << 2) & 0x7));
      KET_Real = 0;
      KET1 = 999;
      KET1_Real = 999;
    } else {
      KET = 9;
      KET_Real = 9;
      KET1 = 999;
      KET1_Real = 999;
    }
    
    if ((Item & 0x40) > 0) {
      GLU = ((byte)((_data[11] & 0xFF) >> 1 & 0x7));
      GLU1 = 999;
      GLU1_Real = 999;
      GLU_Real = 0;
    } else {
      GLU = 9;
      GLU_Real = 9;
      GLU1 = 999;
      GLU1_Real = 999;
    }
    if ((Item & 0x20) > 0) {
      PRO = ((byte)((_data[11] & 0xFF) >> 4 & 0x7));
      PRO1 = 999;
      PRO1_Real = 999;
      PRO_Real = 0;
    } else {
      PRO = 9;
      PRO_Real = 9;
      PRO1 = 999;
      PRO1_Real = 999;
    }
    if ((Item & 0x10) > 0) {
      PH = ((byte)(_data[12] & 0x7));
      PH_Real = 0;
      PH1 = 999;
      PH1_Real = 999;
    } else {
      PH = 9;
      PH_Real = 9;
      PH1 = 999;
      PH1_Real = 999;
    }
    if ((Item & 0x8) > 0) {
      NIT = ((byte)((_data[12] & 0xFF) >> 3 & 0x7));
      NIT_Real = 0;
      NIT1 = 999;
      NIT1_Real = 999;
    } else {
      NIT = 9;
      NIT_Real = 9;
      NIT1 = 999;
      NIT1_Real = 999;
    }
    if ((Item & 0x4) > 0) {
      LEU = ((byte)(((_data[12] & 0xFF) >> 6 | _data[13] << 2) & 0x7));
      LEU_Real = 0;
      LEU1 = 999;
      LEU1_Real = 999;
    } else {
      LEU = 9;
      LEU_Real = 9;
      LEU1 = 999;
      LEU1_Real = 999;
    }
    if ((Item & 0x2) > 0) {
      SG = ((byte)((_data[13] & 0xFF) >> 1 & 0x7));
      SG_Real = 0;
      SG1 = 999;
      SG1_Real = 999;
    } else {
      SG = 9;
      SG_Real = 9;
      SG1 = 999;
      SG1_Real = 999;
    }
    if ((Item & 0x1) > 0) {
      VC = ((byte)((_data[13] & 0xFF) >> 4 & 0x7));
      VC_Real = 0;
      VC1 = 999;
      VC1_Real = 999;
    } else {
      VC = 9;
      VC_Real = 9;
      VC1 = 999;
      VC1_Real = 999;
    }
    MAL = -8;
    CR = -8;
    UCA = -8;
    return _BC01;
  }
  
  public BC401_Struct unPackall(byte[] pData)
  {
    Log.e("适用于定量显示、兼容14项试纸", "$$$$$$$$$$$");
    PrintBytes.printData(pData, pData.length);
    Log.e("适用于定量显示、兼容14项试纸", "$$$$$$$$$$$");
    byte[] _data = pData;
    BC401_Struct _BC01 = new BC401_Struct();
    
    ID = ((_data[0] | (_data[1] & 0xFF) << 8) & 0x3FF);
    User = ((_data[1] & 0xFF) >> 2 & 0x1F);
    
    Year = (_data[2] & 0x7F);
    Month = (((_data[2] & 0xFF) >> 7 & 0x7 | (_data[3] & 0x7) << 1) & 0xF);
    Date = ((_data[3] & 0xFF) >> 3 & 0x1F);
    
    Hour = (_data[4] & 0x1F);
    Min = (((_data[4] & 0xFF) >> 5 | _data[5] << 3) & 0x3F);
    
    Sec = (_data[6] & 0x7F);
    Log.e("值和时间", 
      ID + User + Year + Month + 
      Date + Hour + Min + Sec);
    
    Item = ((_data[8] & 0xFF | (_data[9] & 0xFF) << 8) & 0x3FFF);
    


    if ((Item & 0x2000) > 0) {
      URO = ((byte)(_data[10] & 0xFF & 0x7));
      URO1 = (_data[16] & 0xFF);
      URO_Real = 0;
      URO1_Real = 0;
    } else {
      URO = 9;
      URO_Real = 9;
      
      URO1 = 999;
      URO1_Real = 999;
    }
    if ((Item & 0x1000) > 0) {
      BLD = ((byte)((_data[10] & 0xFF) >> 3 & 0x7));
      BLD1 = (_data[17] & 0xFF);
      BLD_Real = 0;
      BLD1_Real = 0;
    } else {
      BLD = 9;
      BLD_Real = 9;
      BLD1 = 999;
      BLD1_Real = 999;
    }
    
    if ((Item & 0x800) > 0) {
      BIL = ((byte)(((_data[10] & 0xFF) >> 6 | (_data[11] & 0x1) << 2) & 0x7));
      BIL1 = (_data[18] & 0xFF & 0x7F);
      BIL_Real = 0;
      BIL1_Real = 0;
    } else {
      BIL = 9;
      BIL_Real = 9;
      BIL1 = 999;
      BIL1_Real = 999;
    }
    if ((Item & 0x400) > 0) {
      KET = ((byte)((_data[11] & 0xFF) >> 1 & 0x7));
      KET1 = (((_data[18] & 0xFF) >> 7 | (_data[19] & 0xFF) << 1) & 0x7F);
      KET1_Real = 0;
      KET_Real = 0;
    } else {
      KET = 9;
      KET_Real = 9;
      KET1 = 999;
      KET1_Real = 999;
    }
    if ((Item & 0x200) > 0) {
      GLU = ((byte)((_data[11] & 0xFF) >> 4 & 0x7));
      GLU1 = ((_data[20] & 0xFF | (_data[21] & 0xFF) << 8) & 0x3FF);
      GLU1_Real = 0;
      GLU_Real = 0;
    }
    else {
      GLU = 9;
      GLU_Real = 9;
      GLU1 = 999;
      GLU1_Real = 999;
    }
    if ((Item & 0x100) > 0) {
      PRO = ((byte)(_data[12] & 0xFF & 0x7));
      PRO1 = ((_data[22] & 0xFF | (_data[23] & 0xFF) << 8) & 0x1FF);
      PRO1_Real = 0;
      PRO_Real = 0;
    } else {
      PRO = 9;
      PRO_Real = 9;
      PRO1 = 999;
      PRO1_Real = 999;
    }
    if ((Item & 0x80) > 0) {
      PH = ((byte)((_data[12] & 0xFF) >> 3 & 0x7));
      PH1 = ((_data[23] & 0xFF) >> 1 & 0x3F);
      PH1_Real = 0;
      PH_Real = 0;
    } else {
      PH = 9;
      PH_Real = 9;
      PH1 = 999;
      PH1_Real = 999;
    }
    if ((Item & 0x40) > 0) {
      NIT = ((byte)(((_data[12] & 0xFF) >> 6 | (_data[13] & 0xFF) << 2) & 0x7));
      NIT1 = ((byte)(_data[24] & 0xFF & 0x1F));
      NIT1_Real = 0;
      NIT_Real = 0;
    } else {
      NIT = 9;
      NIT_Real = 9;
      NIT1 = 999;
      NIT1_Real = 999;
    }
    if ((Item & 0x20) > 0) {
      LEU = ((byte)((_data[13] & 0xFF) >> 1 & 0x7));
      LEU1 = (((_data[24] & 0xFF) >> 5 | (_data[25] & 0xFF) << 3) & 0x1FF);
      LEU1_Real = 0;
      LEU_Real = 0;
    } else {
      LEU = 9;
      LEU_Real = 9;
      LEU1 = 999;
      LEU1_Real = 999;
    }
    if ((Item & 0x10) > 0) {
      SG = ((byte)((_data[13] & 0xFF) >> 4 & 0x7));
      SG1 = (_data[26] & 0xFF & 0x1F);
      SG1_Real = 0;
      SG_Real = 0;
    } else {
      SG = 9;
      SG_Real = 9;
      SG1 = 999;
      SG1_Real = 999;
    }
    if ((Item & 0x8) > 0) {
      VC = ((byte)(_data[14] & 0xFF & 0x7));
      VC1 = (((_data[26] & 0xFF) >> 5 | (_data[27] & 0xFF) << 3) & 0x3F);
      VC1_Real = 0;
      VC_Real = 0;
    } else {
      VC = 9;
      VC_Real = 9;
      VC1 = 999;
      VC1_Real = 999;
    }
    if ((Item & 0x4) > 0) {
      MAL = ((byte)((_data[14] & 0xFF) >> 3 & 0x7));
      MAL1 = ((_data[27] & 0xFF) >> 3 & 0xF);
      MAL1_Real = 0;
      MAL_Real = 0;
    } else {
      MAL = 9;
      MAL_Real = 9;
      MAL1 = 999;
      MAL1_Real = 999;
    }
    if ((Item & 0x2) > 0) {
      CR = ((byte)(((_data[14] & 0xFF) >> 6 | (_data[15] & 0xFF) << 2) & 0x7));
      CR1 = ((_data[28] & 0xFF | (_data[29] & 0xFF) << 8) & 0x1FF);
      CR1_Real = 0;
      CR_Real = 0;
    } else {
      CR = 9;
      CR_Real = 9;
      CR1 = 999;
      CR1_Real = 999;
    }
    if ((Item & 0x1) > 0) {
      UCA = ((byte)((_data[15] & 0xFF) >> 1 & 0x7));
      UCA1 = ((_data[29] & 0xFF) >> 1 & 0x7F);
      UCA1_Real = 0;
      UCA_Real = 0;
    } else {
      UCA = 9;
      UCA_Real = 9;
      UCA1 = 999;
      UCA1_Real = 999;
    }
    return _BC01;
  }
}
