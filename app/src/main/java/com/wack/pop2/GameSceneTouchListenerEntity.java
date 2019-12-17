package com.wack.pop2;

import android.util.Log;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

import java.util.HashSet;
import java.util.Set;

public class GameSceneTouchListenerEntity extends BaseEntity implements IOnSceneTouchListener {

    public interface SceneTouchListener {

        /**
         * Returns true if the touch event was handled
         * @param scene
         * @param touchEvent
         * @return
         */
        boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent);
    }

    private static final String TAG = "TouchListener";

    private Set<SceneTouchListener> sceneTouchlisteners = new HashSet<>();

    public GameSceneTouchListenerEntity(GameResources gameResources) {
        super(gameResources);
        scene.setOnSceneTouchListener(this);
    }

    public GameSceneTouchListenerEntity addSceneTouchListener(SceneTouchListener listener) {
        if (sceneTouchlisteners.contains(listener)) {
            Log.w(TAG, "SceneTouchListener has already been added to the GameSceneTouchListenerEntity");
        }
        sceneTouchlisteners.add(listener);
        return this;
    }

    public void removeSceneTouchListener(SceneTouchListener listener) {
        if (!sceneTouchlisteners.contains(listener)) {
            Log.w(TAG, "No listeners for " + listener);
        }
        sceneTouchlisteners.remove(listener);
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        boolean handled  = false;
        for (SceneTouchListener listener : sceneTouchlisteners) {
            if (listener.onSceneTouchEvent(pScene, pSceneTouchEvent)) {
                handled = true;
            }
        }
        return handled;
    }
}