package com.stupidfungames.pop;

import android.content.Intent;
import androidx.activity.result.ActivityResultCaller;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;

public interface HostActivity extends ActivityResultCaller {
  void startActivity(Intent intent);
  void finish();
  GooglePlayServicesAuthManager getAuthManager();
}
