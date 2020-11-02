package com.stupidfungames.pop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.stupidfungames.pop.androidui.GameMenuButton;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.continuegame.ContinueGameBtnView;
import com.stupidfungames.pop.continuegame.ContinueGameBtnView.ContinueGameBtnViewHostActivity;
import com.stupidfungames.pop.savegame.SaveGame;

public class GameOverActivity extends AppCompatActivity implements ContinueGameBtnViewHostActivity {

  public static final String SCORE_EXTRA = "score_extra";
  public static final String CONTINUE_SAVE_GAME_EXTRA = "continue_save_game";
  private ValueAnimator logoAnimator;

  private ContinueGameBtnView continueGameBtnView;

  public static Intent newIntent(Context context, int score, SaveGame continueGame) {
    Intent intent = new Intent(context, GameOverActivity.class);
    Bundle b = new Bundle();
    b.putInt(GameOverActivity.SCORE_EXTRA, score);
    b.putString(CONTINUE_SAVE_GAME_EXTRA, continueGame.toJson());
    intent.putExtras(b);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.game_over_layout);

    continueGameBtnView = new ContinueGameBtnView(
        (GameMenuButton) findViewById(R.id.continue_game_btn), this);

    findViewById(R.id.new_game_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startNewGame();
      }
    });

    findViewById(R.id.quit_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        quitGame();
      }
    });

    ((TextView) findViewById(R.id.score_text))
        .setText(Integer.toString(getGameOverScore()));

    animateGameOver();
  }

  private int getGameOverScore() {
    return getIntent().getIntExtra(SCORE_EXTRA, 0);
  }

  private void startNewGame() {
    startActivity(GameActivity.newIntent(this));
    finish();
  }

  @Override
  public void continueGame() {
    startActivity(GameActivity.newIntent(getSaveGameFromIntent(), this));
    finish();
  }

  private SaveGame getSaveGameFromIntent() {
    String saveGameJson = getIntent().getStringExtra(CONTINUE_SAVE_GAME_EXTRA);
    SaveGame saveGame = SaveGame.fromJson(saveGameJson);
    return saveGame;
  }

  private void quitGame() {
    this.finish();
  }

  private void animateGameOver() {
    final View logo = findViewById(R.id.game_over_text);
    logoAnimator = ObjectAnimator.ofFloat(1, 0.5f, 1, 1, 1, 1, 1, 1, 1, 1, 0.7f, 1);
    logoAnimator.setDuration(2000);
    logoAnimator.start();
    logoAnimator.setRepeatMode(ValueAnimator.RESTART);

    logoAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        logo.setAlpha((float) animation.getAnimatedValue());
      }
    });
    logoAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        animation.start();
      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    logoAnimator.end();
  }

  @Override
  public Context getContext() {
    return this;
  }

  @Override
  public GooglePlayServicesAuthManager getAuthManager() {
    return null;
  }

}