package com.stupidfungames.pop.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.DimenRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.stupidfungames.pop.R;
import java.util.List;

public abstract class GameNeonDialogActivity extends Activity {

  protected static class ButtonModel {

    public final @StringRes int stringResid;
    public final OnClickListener onClickListener;

    public ButtonModel(@StringRes int stringResid, OnClickListener onClickListener) {
      this.stringResid = stringResid;
      this.onClickListener = onClickListener;
    }
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.neon_dialog_activity);

    TextView title = findViewById(R.id.neon_dialog_title_text);
    title.setText(getTitleResId());
    title.setTextSize(getResources().getDimension(getTitleSize()));
    ViewGroup buttonsContainer = findViewById(R.id.neon_dialog_buttons_container);
    List<ButtonModel> buttons = getButtonModels();

    for (ButtonModel model : buttons) {
      LayoutInflater inflater = getLayoutInflater();
      TextView button = (TextView) inflater.inflate(R.layout.neon_dialog_button, buttonsContainer, false);
      button.setText(model.stringResid);
      button.setOnClickListener(model.onClickListener);
      buttonsContainer.addView(button);
    }
  }

  protected @DimenRes int getTitleSize() {
    return R.dimen.neon_dialog_title_text_size;
  }

  protected abstract @StringRes int getTitleResId();
  protected abstract List<ButtonModel> getButtonModels();
}
