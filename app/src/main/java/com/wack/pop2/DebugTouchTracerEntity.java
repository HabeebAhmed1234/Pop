package com.wack.pop2;

import android.util.Log;

import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;

public class DebugTouchTracerEntity extends BaseEntity implements GameSceneTouchListenerEntity.SceneTouchListener {

    private static final String TAG = "DebugTouchTracerEntity";
    private GameSceneTouchListenerEntity touchListenerEntity;

    public DebugTouchTracerEntity(GameSceneTouchListenerEntity touchListenerEntity, GameResources gameResources) {
        super(gameResources);
        this.touchListenerEntity = touchListenerEntity;
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        touchListenerEntity.addSceneTouchListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        touchListenerEntity.removeSceneTouchListener(this);
    }

    @Override
    public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
        Log.d(TAG, "TouchEvent: ("+touchEvent.getX() + "," + touchEvent.getY() + ")");
        return false;
    }
}
