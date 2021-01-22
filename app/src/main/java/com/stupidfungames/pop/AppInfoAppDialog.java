package com.stupidfungames.pop;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import com.stupidfungames.pop.dialog.GameNeonDialogActivity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppInfoAppDialog extends GameNeonDialogActivity {

  public static Intent newIntent(Context context) {
    return new Intent(context, AppInfoAppDialog.class);
  }

  @Override
  protected int getTitleResId() {
    return R.string.app_info_title;
  }

  @Override
  protected int getSubTitleResId() {
    return R.string.app_info_subtitle;
  }

  @Override
  protected List<ButtonModel> getButtonModels() {
    return new ArrayList<>(
        Arrays.asList(
            new ButtonModel(R.string.dismiss, new OnClickListener() {
              @Override
              public void onClick(View v) {
                finish();
              }
            })));
  }
}
