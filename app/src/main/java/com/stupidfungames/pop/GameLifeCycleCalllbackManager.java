package com.stupidfungames.pop;

import com.stupidfungames.pop.savegame.SaveGame;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * All {@link BaseEntity}s are added to this class and will receive callbacks anytime a relevany
 * game lifecycle even occurs
 *
 * Only one instance of this is available per game.
 */
public class GameLifeCycleCalllbackManager {

  public interface GameCallback {

    /**
     * Callback for when you are supposed to load textures and animations
     */
    void onCreateResources();

    /**
     * Callback for when the current scene is created. Do all the init work here like registering
     * listeners and setting up the level
     */
    void onCreateScene();

    /**
     * Called when its time to load an already saved game. Use the values in the given SaveGame to
     * create the game
     */
    void onLoadGame(SaveGame saveGame);

    /**
     * Called when its time to save the game. Set the correct values to each field of SaveGame
     */
    void onSaveGame(SaveGame saveGame);

    /**
     * Cleanup all resources here
     */
    void onDestroy();
  }

  private final Queue<BaseEntity> gameEntities = new LinkedList<>();

  public GameLifeCycleCalllbackManager() {
  }

  public void registerGameEntity(BaseEntity baseEntity) {
    gameEntities.add(baseEntity);
  }

  public void unRegisterGameEntity(BaseEntity baseEntity) {
    gameEntities.remove(baseEntity);
  }

  public void onCreateResources() {
    Iterator<BaseEntity> it = new ArrayList(gameEntities).iterator();
    while (it.hasNext()) {
      it.next().onCreateResources();
    }
  }

  public void onCreateScene() {
    Iterator<BaseEntity> it = new ArrayList(gameEntities).iterator();
    while (it.hasNext()) {
      it.next().onCreateScene();
    }
  }

  public void onLoadGame(SaveGame saveGame) {
    Iterator<BaseEntity> it = new ArrayList(gameEntities).iterator();
    while (it.hasNext()) {
      it.next().onLoadGame(saveGame);
    }
  }


  public void onSaveGame(SaveGame saveGame) {
    Iterator<BaseEntity> it = new ArrayList(gameEntities).iterator();
    while (it.hasNext()) {
      it.next().onSaveGame(saveGame);
    }
  }

  public void onDestroy() {
    Iterator<BaseEntity> it = new ArrayList(gameEntities).iterator();
    while (it.hasNext()) {
      it.next().onDestroy();
    }
  }
}
