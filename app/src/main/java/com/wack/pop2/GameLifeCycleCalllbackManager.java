package com.wack.pop2;

import android.os.Handler;
import android.os.Looper;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * All {@link BaseEntity}s are added to this class and will receive callbacks anytime a relevany game
 * lifecycle even occurs
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
         * Callback for when the current scene is created.
         * Do all the init work here like registering listeners and setting up the level
         */
        void onCreateScene();

        /**
         * Cleanup all resources here
         */
        void onDestroy();
    }

    private final Queue<BaseEntity> gameEntities = new ConcurrentLinkedQueue<>();

    private static GameLifeCycleCalllbackManager sInstance;

    private GameLifeCycleCalllbackManager() {}

    /**
     * Called when the game scene is first created
     */
    public static void init() {
        sInstance = new GameLifeCycleCalllbackManager();

    }

    public static GameLifeCycleCalllbackManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Cannot retrieve instance of GameLifeCycleCalllbackManager before init is called!");
        }
        return sInstance;
    }

    public static void destroy() {
        if (sInstance == null) {
            throw new IllegalStateException("Cannot destroy GameLifeCycleCalllbackManager if it doesn't exist. Make a new one first");
        }
        sInstance = null;

    }

    public void registerGameEntity(BaseEntity baseEntity) {
        gameEntities.add(baseEntity);
    }

    public void onCreateResources() {
        Iterator<BaseEntity> it = gameEntities.iterator();
        while (it.hasNext()) {
            it.next().onCreateResources();
        }
    }

    public void onCreateScene() {
        Iterator<BaseEntity> it = gameEntities.iterator();
        while (it.hasNext()) {
            it.next().onCreateScene();
        }
    }

    public void onDestroy() {
        Iterator<BaseEntity> it = gameEntities.iterator();
        while (it.hasNext()) {
            it.next().onDestroy();
        }
    }
}
