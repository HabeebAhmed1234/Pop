package com.stupidfungames.pop.savegame;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.dialog.GameNeonDialogActivity;
import java.util.Arrays;
import java.util.List;

public class UpdateGameDialogActivity extends GameNeonDialogActivity {

  public static Intent newIntent(Context context) {
    return new Intent(context, UpdateGameDialogActivity.class);
  }

  @Override
  protected int getTitleResId() {
    return R.string.game_update_text;
  }

  @Override
  protected List<ButtonModel> getButtonModels() {
    return Arrays.asList(
        new ButtonModel(R.string.update_btn_text, new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            launchPlayStore();
            finish();
          }
        }),new ButtonModel(R.string.dismiss, new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            finish();
          }
        }));
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
