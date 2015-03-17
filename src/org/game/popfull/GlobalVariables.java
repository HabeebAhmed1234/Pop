package org.game.popfull;

import android.app.Application;

public class GlobalVariables extends Application {

	  private int Difficulty=1;
	  private String Mode="Arcade";

	  public int getDifficulty(){
	    return Difficulty;
	  }
	  public void setDifficulty(int s){
		  Difficulty = s;
	  }
	  
	  
	  public String getMode(){
		    return Mode;
	  }
	  public void setMode(String s){
		  Mode = s;
	  }
	  
	  
}