package com.stupidfungames.pop;

import android.app.Application;
import com.stupidfungames.pop.ads.GameAdsManager;

public class GameApplication extends Application {


  @Override
  public void onCreate() {
    super.onCreate();
    GameAdsManager.get().initialize(this);
  }
}
