package com.stupidfungames.pop;

import android.content.Intent;
import androidx.activity.result.ActivityResultCaller;

public interface HostActivity extends ActivityResultCaller {
  void startActivity(Intent intent);
  void finish();
}
