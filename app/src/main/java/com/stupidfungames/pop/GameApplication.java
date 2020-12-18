package com.stupidfungames.pop;

import android.app.Application;
import com.stupidfungames.pop.ads.GameAdsManager;
import com.stupidfungames.pop.appreviews.AppOpenCounter;

public class GameApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    AppOpenCounter.onAppOpened(this);
    GameAdsManager.get().initialize(this);
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
  }
}
