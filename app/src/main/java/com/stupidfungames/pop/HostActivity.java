package com.stupidfungames.pop;

import android.content.Context;
import android.content.Intent;
import androidx.activity.result.ActivityResultCaller;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;

public interface HostActivity extends ActivityResultCaller {
  Context getContext();
  void startActivity(Intent intent);
  void finish();
  GooglePlayServicesAuthManager getAuthManager();
}
