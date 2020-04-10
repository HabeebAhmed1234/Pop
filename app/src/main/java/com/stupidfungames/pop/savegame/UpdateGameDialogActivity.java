package com.stupidfungames.pop.savegame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import com.stupidfungames.pop.R;

public class UpdateGameDialogActivity extends Activity {

  public static Intent newIntent(Context context) {
    return new Intent(context, UpdateGameDialogActivity.class);
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.update_game_dialog_layout);

    findViewById(R.id.update_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        launchPlayStore();
        finish();
      }
    });

    findViewById(R.id.dismiss_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }

  private void launchPlayStore() {
    final String appPackageName = getPackageName();
    try {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
    } catch (android.content.ActivityNotFoundException anfe) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
    }
  }
}
