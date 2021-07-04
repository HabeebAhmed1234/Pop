package com.stupidfungames.pop.difficulty;

import static com.stupidfungames.pop.GameConstants.TIME_TO_MAX_DIFFICULTY_SECONDS;

import android.util.Log;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameDifficultyEventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.savegame.SaveGame;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

/**
 * Controls the game difficulty
 */
public class GameDifficultyEntity extends BaseEntity {

  public static final float GAME_PROGRESS_UPDATE_INTERVAL_SECONDS = 1;

  /**
   * Value that ranges between 0 and 1 which defines the amount of time between 0 and
   * TIME_TO_MAX_DIFFICULTY_SECONDS the user had played.
   */
  private float currentGameProgress = 0f;


  private TimerHandler updateIntervalUpdater =
      new TimerHandler(GAME_PROGRESS_UPDATE_INTERVAL_SECONDS, true, new ITimerCallback() {
        @Override
        public void onTimePassed(TimerHandler pTimerHandler) {
          updateInterval();
        }
      });

  public GameDifficultyEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    initUpdateInterval();
    engine.registerUpdateHandler(updateIntervalUpdater);
  }

  @Override
  public void onSaveGame(SaveGame saveGame) {
    super.onSaveGame(saveGame);
    saveGame.gameProgress = currentGameProgress;
  }

  @Override
  public void onLoadGame(SaveGame saveGame) {
    super.onLoadGame(saveGame);
    setGameProgress(saveGame.gameProgress);
    onGameProgressUpdated();
  }

  public float getGameProgress() {
    return currentGameProgress;
  }

  private void initUpdateInterval() {
    EventBus.get().sendEvent(GameEvent.GAME_DIFFICULTY_CHANGED, new GameDifficultyEventPayload(0));
  }

  private void updateInterval() {
    // increment the game progress
    float gameProgressIncrement =
        1f / (TIME_TO_MAX_DIFFICULTY_SECONDS / GAME_PROGRESS_UPDATE_INTERVAL_SECONDS);

    setGameProgress(currentGameProgress + gameProgressIncrement);
  }

  private void setGameProgress(float gameProgress) {
    if (gameProgress < 0) {
      gameProgress = 0;
    } else if (gameProgress > 1) {
      gameProgress = 1;
    }
    currentGameProgress = gameProgress;
    onGameProgressUpdated();
  }

  private void onGameProgressUpdated() {
    // Calculate the current difficulty and broadcast.
    float gameDifficulty = GameDifficultyFunction.getDifficultyFromSetPoints(currentGameProgress);
    EventBus.get().sendEvent(GameEvent.GAME_DIFFICULTY_CHANGED,
        new GameDifficultyEventPayload(gameDifficulty));
  }
}
