package com.stupidfungames.pop;

import static com.stupidfungames.pop.analytics.Events.APP_START;
import static com.stupidfungames.pop.analytics.Events.APP_START_FROM_NOTIFICATION;
import static com.stupidfungames.pop.analytics.Events.OPEN_PURCHASES;
import static com.stupidfungames.pop.analytics.Events.OPEN_STORE;
import static com.stupidfungames.pop.notifications.UserNudgeNotificationManager.EXTRA_FROM_NOTIFICATION;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.androidui.BlinkAnimator;
import com.stupidfungames.pop.androidui.GameMenuButton;
import com.stupidfungames.pop.androidui.LoadingSpinner;
import com.stupidfungames.pop.appreviews.AppReviewUtil;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.globalpoppedbubbles.GlobalPoppedBubbleManager;
import com.stupidfungames.pop.inapppurchase.EquipActivity;
import com.stupidfungames.pop.inapppurchase.StoreActivity;
import com.stupidfungames.pop.notifications.UserNudgeNotificationManager;
import com.stupidfungames.pop.savegame.SaveGameManager;
import com.stupidfungames.pop.share.ShareHostActivity;
import com.stupidfungames.pop.share.SocialsBtnView;

public class MainMenuActivity extends AppCompatActivity implements ShareHostActivity {

  public static Intent newIntent(Context context) {
    return new Intent(context, MainMenuActivity.class);
  }

  private GooglePlayServicesAuthManager authManager;
  private SaveGameManager saveGameManager;

  private GoogleAuthPopupView popupView;
  private PlayerProfileView playerProfileView;
  private NewGameBtnView newGameBtnView;
  private LoadGameBtnView loadGameBtnView;
  private SocialsBtnView socialsBtnView;

  private BlinkAnimator blinkAnimator = new BlinkAnimator();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    logAppStart();
    AppReviewUtil.maybeShowAppReviewDialog(this);
    scheduleNudgeNotifications();
    MobileAds.initialize(this);

    authManager = new GooglePlayServicesAuthManager(this);
    saveGameManager = new SaveGameManager(this, this);

    setContentView(R.layout.main_menu_layout);

    findViewById(R.id.open_store_btn).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        openStore();
      }
    });

    findViewById(R.id.open_purchases_btn).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        openPurchses();
      }
    });

    findViewById(R.id.info_btn).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(AppInfoAppDialog.newIntent(MainMenuActivity.this));
      }
    });

    popupView = new GoogleAuthPopupView(findViewById(R.id.root_view), this);
    playerProfileView =
        new PlayerProfileView(
            (ViewGroup) findViewById(R.id.player_profile_view), this);
    newGameBtnView = new NewGameBtnView(findViewById(R.id.new_game_btn), this);
    loadGameBtnView = new LoadGameBtnView(
        saveGameManager,
        (LoadingSpinner) findViewById(R.id.loading_spinner),
        (GameMenuButton) findViewById(R.id.load_game_btn),
        this);

    socialsBtnView = new SocialsBtnView(
        findViewById(R.id.share_btn_android),
        findViewById(R.id.share_btn_fb),
        findViewById(R.id.review_btn),
        this);

    animate();
    authManager.maybeLoginOnAppStart(this);

    ((AdView) findViewById(R.id.adView)).loadAd(((new AdRequest.Builder()).build()));

    GlobalPoppedBubbleManager.getInstance()
        .populateTotalPoppedBubblesTextView(
            (TextView) findViewById(R.id.total_bubbles_popped_count));
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (authManager.isLoggedIn()) {
      authManager.initiateLogin(this);
    }
  }

  private void scheduleNudgeNotifications() {
    new UserNudgeNotificationManager().scheduleNudgeNotifications(this);
  }

  private void animate() {
    blinkAnimator.animate(findViewById(R.id.logo));
  }

  private void openStore() {
    Logger.logSelect(this, OPEN_STORE);
    startActivity(StoreActivity.getIntent(this));
  }

  private void openPurchses() {
    Logger.logSelect(this, OPEN_PURCHASES);
    startActivity(EquipActivity.getIntent(this));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    blinkAnimator.onDestroy();
  }

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public GooglePlayServicesAuthManager getAuthManager() {
    return authManager;
  }

  @Override
  public Activity getActivity() {
    return this;
  }

  private void logAppStart() {
    Intent launchIntent = getIntent();
    Bundle extras = launchIntent.getExtras();
    boolean fromNotification =
        (extras != null && extras.containsKey(EXTRA_FROM_NOTIFICATION))
            ? extras.getBoolean(EXTRA_FROM_NOTIFICATION) : false;
    Logger.logSelect(this, fromNotification ? APP_START_FROM_NOTIFICATION : APP_START);
  }
}