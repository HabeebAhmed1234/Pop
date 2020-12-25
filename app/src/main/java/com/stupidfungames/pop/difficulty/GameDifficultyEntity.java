package com.stupidfungames.pop.difficulty;

import static com.stupidfungames.pop.GameConstants.MAX_SPAWN_INTERVAL_SECONDS;
import static com.stupidfungames.pop.GameConstants.MIN_SPAWN_INTERVAL_SECONDS;
import static com.stupidfungames.pop.difficulty.DifficultyConstants.SPAWN_INTERVAL_DECREASE_SPEED_SECONDS;
import static com.stupidfungames.pop.difficulty.DifficultyConstants.SPAWN_INTERVAL_UPDATE_SECONDS;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.DifficultyChangedEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameProgressEventPayload;
import com.stupidfungames.pop.savegame.SaveGame;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;

/**
 * Controls the game difficulty
 */
public class GameDifficultyEntity extends BaseEntity {

  private float currentSpawnInterval = MAX_SPAWN_INTERVAL_SECONDS;


  private TimerHandler updateIntervalUpdater =
      new TimerHandler(SPAWN_INTERVAL_UPDATE_SECONDS, true, new ITimerCallback() {
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
    saveGame.spawnInterval = currentSpawnInterval;
  }

  @Override
  public void onLoadGame(SaveGame saveGame) {
    super.onLoadGame(saveGame);
    setCurrentSpawnInterval(saveGame.spawnInterval);
  }

  public void initUpdateInterval() {
    EventBus.get().sendEvent(GameEvent.SPAWN_INTERVAL_CHANGED,
        new DifficultyChangedEventPayload(MAX_SPAWN_INTERVAL_SECONDS));
    EventBus.get().sendEvent(GameEvent.GAME_PROGRESS_CHANGED, new GameProgressEventPayload(0));
  }

  public void updateInterval() {
    setCurrentSpawnInterval(currentSpawnInterval - SPAWN_INTERVAL_DECREASE_SPEED_SECONDS);
  }

  private void setCurrentSpawnInterval(float newInterval) {
    currentSpawnInterval = newInterval;
    if (currentSpawnInterval < MIN_SPAWN_INTERVAL_SECONDS) {
      currentSpawnInterval = MIN_SPAWN_INTERVAL_SECONDS;
    }
    EventBus.get().sendEvent(GameEvent.SPAWN_INTERVAL_CHANGED,
        new DifficultyChangedEventPayload(currentSpawnInterval));
    EventBus.get().sendEvent(GameEvent.GAME_PROGRESS_CHANGED,
        new GameProgressEventPayload(getGameProgress()));
  }

  /**
   * Returns the game progress as the currentSpawnInterval percentage between min and max spawn
   * intervals
   */
  private float getGameProgress() {
    return 1 - (currentSpawnInterval - MIN_SPAWN_INTERVAL_SECONDS) / (MAX_SPAWN_INTERVAL_SECONDS
        - MIN_SPAWN_INTERVAL_SECONDS);
  }
}
