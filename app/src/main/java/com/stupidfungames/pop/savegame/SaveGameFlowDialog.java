package com.stupidfungames.pop.savegame;

import android.view.View;
import android.view.View.OnClickListener;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.dialog.GameNeonDialogActivity;
import java.util.Arrays;
import java.util.List;

public class SaveGameFlowDialog extends GameNeonDialogActivity {

  public static final int RESULT_DECLINE = 1;

  private final OnClickListener signInClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {

    }
  };

  private final OnClickListener declineClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      setResult(RESULT_DECLINE);
      finish();
    }
  };

  @Override
  protected int getTitleResId() {
    return R.string.save_game_flow_title;
  }

  @Override
  protected List<ButtonModel> getButtonModels() {
    return Arrays.asList(
        new ButtonModel(R.string.sign_in, signInClickListener),
        new ButtonModel(R.string.no, declineClickListener));
  }
}
