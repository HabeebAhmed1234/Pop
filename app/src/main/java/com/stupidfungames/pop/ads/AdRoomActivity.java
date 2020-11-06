package com.stupidfungames.pop.ads;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity used to display a full screen interstitial ad or chain of ads.
 */
public class AdRoomActivity extends AppCompatActivity {

  public static final int RESULT_AD_WATCHED = 1;

  public static Intent createIntent(Context context) {
    return new Intent(context, AdRoomActivity.class);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setResult(RESULT_AD_WATCHED);
    finish();
  }
}
