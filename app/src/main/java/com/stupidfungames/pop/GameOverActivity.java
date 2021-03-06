package com.stupidfungames.pop;

import static com.stupidfungames.pop.analytics.Events.GAME_OVER_SCORE;
import static com.stupidfungames.pop.analytics.Events.NEW_GAME_STARTED_GAME_OVER;

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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.androidui.GameMenuButton;
import com.stupidfungames.pop.androidui.LoadingSpinner;
import com.stupidfungames.pop.auth.GooglePlayServicesAuthManager;
import com.stupidfungames.pop.continuegame.ContinueGameBtnView;
import com.stupidfungames.pop.continuegame.ContinueGameBtnView.ContinueGameBtnViewHostActivity;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.savegame.SaveGameManager;

public class GameOverActivity extends AppCompatActivity implements ContinueGameBtnViewHostActivity {

  public static final String SCORE_EXTRA = "score_extra";
  public static final String GLOBAL_TOTAL_BUBBLES_EXTRA = "global_total_bubbles";
  public static final String BUBBLES_POPPED = "bubbles_popped";
  public static final String CONTINUE_SAVE_GAME_EXTRA = "continue_save_game";
  private ValueAnimator logoAnimator;
  private GooglePlayServicesAuthManager authManager;
  private ContinueGameBtnView continueGameBtnView;
  private GameOverBubblesPoppedCounterView gameOverBubblesPoppedCounterView;
  private SaveGameManager saveGameManager;

  public static Intent newIntent(Context context, int score, long globalTotalBubbles,
      long bubblesPopped,
      SaveGame continueGame) {
    Intent intent = new Intent(context, GameOverActivity.class);
    Bundle b = new Bundle();
    b.putInt(GameOverActivity.SCORE_EXTRA, score);
    b.putLong(GameOverActivity.GLOBAL_TOTAL_BUBBLES_EXTRA, globalTotalBubbles);
    b.putLong(GameOverActivity.BUBBLES_POPPED, bubblesPopped);
    b.putString(CONTINUE_SAVE_GAME_EXTRA, continueGame.toJson());
    intent.putExtras(b);
    return intent;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.game_over_layout);

    authManager = new GooglePlayServicesAuthManager(this);

    continueGameBtnView = new ContinueGameBtnView(
        (LoadingSpinner) findViewById(R.id.loading_spinner),
        (GameMenuButton) findViewById(R.id.continue_game_btn),
        this);

    gameOverBubblesPoppedCounterView = new GameOverBubblesPoppedCounterView(
        (TextView) findViewById(R.id.global_bubbles_popped),
        (TextView) findViewById(R.id.incremental_bubbles),
        (TextView) findViewById(R.id.slash));

    gameOverBubblesPoppedCounterView
        .startAnimation(getGlobalBubblesPoppedCount(), getIncrementalBubblesPoppedCount());

    findViewById(R.id.new_game_btn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startNewGame();
      }
    });

    initScoreText();

    animateGameOver();

    saveGameManager = new SaveGameManager(this, this);
    saveGameManager.deleteSaveGame();

    ((AdView) findViewById(R.id.adView)).loadAd(((new AdRequest.Builder()).build()));
  }

  private void initScoreText() {
    int score = getGameOverScore();
    ((TextView) findViewById(R.id.score_text))
        .setText(Integer.toString(score));
    Logger.logSelect(this, GAME_OVER_SCORE, score);
  }

  private int getGameOverScore() {
    return getIntent().getIntExtra(SCORE_EXTRA, 0);
  }

  private long getGlobalBubblesPoppedCount() {
    return getIntent().getLongExtra(GLOBAL_TOTAL_BUBBLES_EXTRA, 0);
  }

  private long getIncrementalBubblesPoppedCount() {
    return getIntent().getLongExtra(BUBBLES_POPPED, 0);
  }

  private void startNewGame() {
    startActivity(GameActivity.newIntent(this));
    finish();
    Logger.logSelect(this, NEW_GAME_STARTED_GAME_OVER);
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
    return authManager;
  }
}