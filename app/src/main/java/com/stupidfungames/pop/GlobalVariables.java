package com.stupidfungames.pop;

import android.app.Application;

public class GlobalVariables extends Application {

	  private int Difficulty=1;
	  private String Mode="Arcade";

	  public int getDifficulty(){
	    return Difficulty;
	  }
	  
	  
	  public String getMode(){
		    return Mode;
	  }
	  public void setMode(String s){
		  Mode = s;
	  }
	  
	  
}