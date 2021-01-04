package com.stupidfungames.pop;

import android.app.Application;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.stupidfungames.pop.ads.GameAdsManager;
import com.stupidfungames.pop.androidui.music.MusicPlayer;
import com.stupidfungames.pop.appreviews.AppOpenCounter;

public class GameApplication extends Application implements LifecycleObserver {

  @Override
  public void onCreate() {
    super.onCreate();
    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    MusicPlayer.init(this);
    AppOpenCounter.onAppOpened(this);
    GameAdsManager.get().initialize(this);
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
    MusicPlayer.get().stop();
  }


  @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
  public void onAppBackgrounded() {
    MusicPlayer.get().stop();
  }

  @OnLifecycleEvent(Lifecycle.Event.ON_START)
  public void onAppForegrounded() {
    MusicPlayer.get().onAppForegrounded();
  }
}
