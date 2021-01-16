package com.stupidfungames.pop.hudentities;

import static com.stupidfungames.pop.GameConstants.STREAK_EXPIRE_THRESHOLD_SECONDS;

import android.text.TextUtils;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.DecrementScoreEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.IncrementScoreEventPayload;
import com.stupidfungames.pop.savegame.SaveGame;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.util.color.AndengineColor;

/**
 * Entity that contains score hud panel
 */
public class ScoreHudEntity extends HudTextBaseEntity implements EventBus.Subscriber {

  private static final String SCORE_TEXT_PREFIX = "Score: ";
  private static final AndengineColor NEGATIVE_SCORE_COLOR = AndengineColor.RED;
  private static final AndengineColor POSITIVE_SCORE_COLOR = AndengineColor.GREEN;

  private int scoreValue = 0;
  private final ScoreStreakModel streakModel = new ScoreStreakModel();
  private final TimerHandler streakExpiryHandler = new TimerHandler(STREAK_EXPIRE_THRESHOLD_SECONDS,
      new ITimerCallback() {
        @Override
        public void onTimePassed(TimerHandler pTimerHandler) {
          streakModel.cancelCurrentStreak();
          updateScoreText();
        }
      });

  public ScoreHudEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    EventBus.get()
        .subscribe(GameEvent.INCREMENT_SCORE, this)
        .subscribe(GameEvent.DECREMENT_SCORE, this);
  }

  @Override
  public void onSaveGame(SaveGame saveGame) {
    super.onSaveGame(saveGame);
    saveGame.score = scoreValue;
  }

  @Override
  public void onLoadGame(SaveGame saveGame) {
    super.onLoadGame(saveGame);
    setScoreOnLoadGame(saveGame.score);
  }

  @Override
  String getInitialText() {
    return SCORE_TEXT_PREFIX + "-------";
  }

  @Override
  int[] getTextPositionPx(int[] textDimensPx) {
    return new int[]{20, ScreenUtils.getSreenSize().heightPx - textDimensPx[1]};
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get().unSubscribe(GameEvent.INCREMENT_SCORE, this)
        .unSubscribe(GameEvent.DECREMENT_SCORE, this);
  }

  @Override
  int getMaxStringLength() {
    return 30;
  }

  @Override
  AndengineColor getInitialTextColor() {
    return AndengineColor.GREEN;
  }

  public int getScore() {
    return scoreValue;
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    switch (event) {
      case DECREMENT_SCORE:
        decrementScore((DecrementScoreEventPayload) payload);
        break;
      case INCREMENT_SCORE:
        incrementScore((IncrementScoreEventPayload) payload);
        break;
    }
  }

  private void setScoreOnLoadGame(int loadedScore) {
    scoreValue = loadedScore;
    updateScoreText();
  }

  private void incrementScore(IncrementScoreEventPayload payload) {
    if (payload.isPoppedByTouch) {
      updateStreak(payload);
    }
    scoreValue += (payload.incrementAmount * (payload.isPoppedByTouch ? streakModel
        .getScoreMultiplier() : 1));
    updateScoreText();
  }

  private void decrementScore(DecrementScoreEventPayload payload) {
    scoreValue -= payload.decrementAmmount;
    updateScoreText();
  }

  private void updateStreak(IncrementScoreEventPayload payload) {
    boolean shouldScheduleNewStreakExpiry = false;
    if (streakModel.shouldStartNewStreak(payload.poppedBubbleType)) {
      // load tracking a new streak
      streakModel.startNewStreak(payload.poppedBubbleType);
      shouldScheduleNewStreakExpiry = true;
    } else if (streakModel.shouldContinueStreak(payload.poppedBubbleType)) {
      streakModel.continueStreak();
      shouldScheduleNewStreakExpiry = true;
    }
    if (shouldScheduleNewStreakExpiry) {
      if (engine.containsUpdateHandler(streakExpiryHandler)) {
        streakExpiryHandler.reset();
      } else {
        engine.registerUpdateHandler(streakExpiryHandler);
      }
    }
  }

  private void updateScoreText() {
    updateText(SCORE_TEXT_PREFIX + scoreValue);

    if (scoreValue < 0 && !currentTextColor().equals(NEGATIVE_SCORE_COLOR)) {
      updateColor(NEGATIVE_SCORE_COLOR);
    } else if (scoreValue >= 0 && !currentTextColor().equals(POSITIVE_SCORE_COLOR)) {
      updateColor(POSITIVE_SCORE_COLOR);
    }

    String scoreMultString = streakModel.getScoreMultiplierString();
    if (!TextUtils.isEmpty(scoreMultString)) {
      get(StreakHudEntity.class).updateText(scoreMultString);
      get(StreakHudEntity.class).updateColor(streakModel.getStreakColor());
    } else {
      get(StreakHudEntity.class).updateColor(AndengineColor.TRANSPARENT);
    }
  }
}