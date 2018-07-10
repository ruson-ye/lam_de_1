package com.contec.oem_bc01_jar;

import android.view.Menu;

public class MainActivity extends android.app.Activity
{
  public MainActivity() {}
  
  protected void onCreate(android.os.Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }
  

  public boolean onCreateOptionsMenu(Menu menu)
  {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }
}
