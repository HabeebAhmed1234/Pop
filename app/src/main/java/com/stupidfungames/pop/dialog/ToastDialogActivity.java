package com.stupidfungames.pop.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import com.stupidfungames.pop.R;
import java.util.Arrays;
import java.util.List;

public class ToastDialogActivity extends GameNeonDialogActivity {

  private static String EXTRA_TITLE_RES_ID = "extra_title_resid";

  public static void start(@StringRes int titleResId, Context context) {
    Intent intent = new Intent(context, ToastDialogActivity.class);
    intent.putExtra(EXTRA_TITLE_RES_ID, titleResId);
    ContextCompat.startActivity(context, intent, null);
  }

  @Override
  protected int getTitleResId() {
    return getIntent().getIntExtra(EXTRA_TITLE_RES_ID, 0);
  }

  @Override
  protected List<ButtonModel> getButtonModels() {
    return Arrays.asList(new ButtonModel(R.string.ok, new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    }));
  }

  @Override
  protected int getTitleSize() {
    return R.dimen.toast_dialog_title_size;
  }
}
