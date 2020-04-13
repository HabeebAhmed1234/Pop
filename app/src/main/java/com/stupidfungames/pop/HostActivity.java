package com.stupidfungames.pop;

import android.content.Intent;

public interface HostActivity {
  void startActivityForResult(Intent intent, int rc);
  void finish();
  void startActivity(Intent intent);
}
