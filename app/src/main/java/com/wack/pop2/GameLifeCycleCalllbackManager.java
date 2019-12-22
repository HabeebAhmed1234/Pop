package com.wack.pop2;

import android.os.Handler;
import android.os.Looper;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

        /**
         * Called when the entity gets created on a posted thread only if it is safe to initialize
         * the entity (load and add sprites)
         */
        void onLazyInit();
    }

    private final Set<BaseEntity> gameEntities = new HashSet<>();

    private static GameLifeCycleCalllbackManager sInstance;

    /**
     * True in between the scene being created and onDestroy being called
     */
    private boolean isGameSceneAlive = false;

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
        maybeLazyInit(baseEntity);
    }

    public void onCreateResources() {
        Iterator<BaseEntity> it = gameEntities.iterator();
        while (it.hasNext()) {
            it.next().onCreateResources();
        }
    }

    public void onCreateScene() {
        isGameSceneAlive = true;
        Iterator<BaseEntity> it = gameEntities.iterator();
        while (it.hasNext()) {
            BaseEntity baseEntity = it.next();
            baseEntity.onCreateScene();
            maybeLazyInit(baseEntity);
        }
    }

    public void onDestroy() {
        isGameSceneAlive = false;
        Iterator<BaseEntity> it = gameEntities.iterator();
        while (it.hasNext()) {
            it.next().onDestroy();
        }
    }

    private void maybeLazyInit(final BaseEntity baseEntity) {
        if (isGameSceneAlive) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    baseEntity.onLazyInit();
                }
            });
        }
    }
}
