package com.stupidfungames.pop;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import com.stupidfungames.pop.dialog.GameNeonDialogActivity;
import java.util.Arrays;
import java.util.List;

public class GamePauseActivity extends GameNeonDialogActivity {

    public static final int RESULT_QUIT = 1;

    public static Intent newIntent(Context context) {
        return new Intent(context, GamePauseActivity.class);
    }

    @Override
    protected int getTitleResId() {
        return R.string.game_paused_text;
    }

    @Override
    protected List<ButtonModel> getButtonModels() {
        return Arrays.asList(
            new ButtonModel(R.string.resume_btn_text, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }),
            new ButtonModel(R.string.quit_btn_text, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESULT_QUIT);
                    finish();
                }
            }));
    }
}