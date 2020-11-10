package com.stupidfungames.pop.ads;

import android.content.Context;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class GameAdsManager implements OnInitializationCompleteListener {

  private static GameAdsManager sGameAdsManager;

  public static GameAdsManager get() {
      if (sGameAdsManager == null) {
        sGameAdsManager = new GameAdsManager();
      }
      return sGameAdsManager;
  }

  private GameAdsManager() {}


  public void initialize(Context context) {
    MobileAds.initialize(context, this);
  }

  @Override
  public void onInitializationComplete(InitializationStatus initializationStatus) {
  }
}
