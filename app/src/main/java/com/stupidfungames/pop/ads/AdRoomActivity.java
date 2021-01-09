package com.stupidfungames.pop.ads;

import static com.stupidfungames.pop.analytics.Events.AD_WATCH_ERROR;
import static com.stupidfungames.pop.analytics.Events.AD_WATCH_LOADED;
import static com.stupidfungames.pop.analytics.Events.AD_WATCH_START;
import static com.stupidfungames.pop.analytics.Events.CONTINUE_GAME_WITH_AD;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.analytics.Events;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.androidui.LoadingSpinner;
import com.stupidfungames.pop.androidui.music.MusicPlayer;

/**
 * Activity used to display a full screen interstitial ad or chain of ads.
 */
public class AdRoomActivity extends AppCompatActivity {

  public static final int RESULT_AD_WATCHED = 1;
  public static final int RESULT_AD_NOT_WATCHED = 2;

  private boolean isRewardEarned = false;

  private LoadingSpinner loadingSpinner;
  private RewardedAd rewardedAd;
  RewardedAdCallback adCallback = new RewardedAdCallback() {
    @Override
    public void onRewardedAdOpened() {
      if (loadingSpinner != null) {
        loadingSpinner.stopLoadingAnimation();
      }
    }

    @Override
    public void onRewardedAdClosed() {
      if (isRewardEarned) {
        onAdWatched();
      } else {
        Toast.makeText(AdRoomActivity.this, R.string.ad_reward_failed_message, Toast.LENGTH_SHORT).show();
        onAdNotWatched();
      }
    }

    @Override
    public void onUserEarnedReward(@NonNull RewardItem reward) {
      isRewardEarned = true;
      Toast.makeText(AdRoomActivity.this, R.string.ad_reward_message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedAdFailedToShow(AdError adError) {
      isRewardEarned = true;
      onAdWatchError();
    }
  };

  public static Intent createIntent(Context context) {
    return new Intent(context, AdRoomActivity.class);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MusicPlayer.get().pausePlaying();
    setContentView(R.layout.ad_room_activity);
    loadingSpinner = findViewById(R.id.loading_spinner);
    loadingSpinner.startLoadingAnimation();

    rewardedAd = new RewardedAd(this, getString(R.string.test_rewarded_ad_unit_id));
    Logger.logSelect(this, AD_WATCH_START);
    RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
      @Override
      public void onRewardedAdLoaded() {
        // Ad successfully loaded.
        if (rewardedAd.isLoaded()) {
          Logger.logSelect(AdRoomActivity.this, AD_WATCH_LOADED);
          rewardedAd.show(AdRoomActivity.this, adCallback);
        } else {
          onAdWatchError();
        }
      }

      @Override
      public void onRewardedAdFailedToLoad(LoadAdError adError) {
        onAdWatchError();
      }
    };
    rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
  }

  private void onAdWatchError() {
    Toast.makeText(AdRoomActivity.this, R.string.ad_load_error, Toast.LENGTH_SHORT).show();
    onAdWatched();
    Logger.logSelect(this, AD_WATCH_ERROR);
  }

  private void onAdWatched() {
    setResult(RESULT_AD_WATCHED);
    finishAdWatch();
    Logger.logSelect(this, CONTINUE_GAME_WITH_AD);
  }

  private void onAdNotWatched() {
    setResult(RESULT_AD_NOT_WATCHED);
    finishAdWatch();
  }

  void finishAdWatch() {
    MusicPlayer.get().resumePlaying();
    finish();
  }
}
