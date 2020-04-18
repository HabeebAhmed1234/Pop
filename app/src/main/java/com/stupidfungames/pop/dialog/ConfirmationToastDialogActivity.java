package com.stupidfungames.pop.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.StringRes;
import com.stupidfungames.pop.HostActivity;
import com.stupidfungames.pop.R;
import java.util.Arrays;
import java.util.List;

public class ConfirmationToastDialogActivity extends GameNeonDialogActivity {

  public interface ConfirmationCallback {
    void onYes();
    void onNo();
  }

  private static final int RESULT_YES = 1;
  private static final int RESULT_NO = 2;

  private static String EXTRA_TITLE_RES_ID = "extra_title_resid";

  public static void start(
      @StringRes final int titleResId,
      final ConfirmationCallback callback,
      final HostActivity hostActivity,
      final Context context) {
    Intent intent = new Intent(context, ConfirmationToastDialogActivity.class);
    intent.putExtra(EXTRA_TITLE_RES_ID, titleResId);
    hostActivity.prepareCall(new StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
          @Override
          public void onActivityResult(ActivityResult result) {
            switch (result.getResultCode()) {
              case RESULT_YES:
                callback.onYes();
                break;
              case RESULT_NO:
                callback.onNo();
                break;
            }
          }
        }).launch(intent);
  }

  @Override
  protected int getTitleResId() {
    return getIntent().getIntExtra(EXTRA_TITLE_RES_ID, 0);
  }

  @Override
  protected List<ButtonModel> getButtonModels() {
    return Arrays.asList(new ButtonModel(R.string.yes, new OnClickListener() {
      @Override
      public void onClick(View v) {
        setResult(RESULT_YES);
        finish();
      }
    }),
    new ButtonModel(R.string.no, new OnClickListener() {
      @Override
      public void onClick(View v) {
        setResult(RESULT_NO);
        finish();
      }
    }));
  }

  @Override
  protected int getTitleSize() {
    return R.dimen.toast_dialog_title_size;
  }
}
