package com.wack.pop2.areatouch;

import android.util.Log;

import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.interaction.InteractionCounter;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Holds all of the touch listeners for a given touch priority
 */
class AreaTouchLayer implements IOnAreaTouchListener {

    private static final String TAG = "AreaTouchLayer";

    private final InteractionCounter interactionCounter;
    private final Map<Class<? extends BaseEntityUserData>, Set<GameAreaTouchListenerEntity.AreaTouchListener>> areaTouchlisteners = new HashMap<>();

    public AreaTouchLayer(InteractionCounter interactionCounter) {
        this.interactionCounter = interactionCounter;
    }

    public void addAreaTouchListener(Class<? extends BaseEntityUserData> userDataType, GameAreaTouchListenerEntity.AreaTouchListener listener) {
        if (!areaTouchlisteners.containsKey(userDataType)) {
            areaTouchlisteners.put(userDataType, new HashSet<GameAreaTouchListenerEntity.AreaTouchListener>());
        }

        Set<GameAreaTouchListenerEntity.AreaTouchListener> listeners = areaTouchlisteners.get(userDataType);
        if (listeners.contains(listener)) {
            Log.w(TAG, "Listener " + listener + " is already listening to " + userDataType + " touch event");
        }
        listeners.add(listener);
    }

    public boolean removeAreaTouchListener(Class<? extends BaseEntityUserData> userDataType, GameAreaTouchListenerEntity.AreaTouchListener listener) {
        if (!areaTouchlisteners.containsKey(userDataType)) {
            Log.w(TAG, "No listeners for " + userDataType);
            return false;
        }
        Set<GameAreaTouchListenerEntity.AreaTouchListener> listeners = areaTouchlisteners.get(userDataType);
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            areaTouchlisteners.remove(userDataType);
        }
        return true;
    }

    public boolean hasAreaTouchListener(Class<? extends BaseEntityUserData> userDataType, GameAreaTouchListenerEntity.AreaTouchListener listener) {
        return areaTouchlisteners.containsKey(userDataType) && areaTouchlisteners.get(userDataType).contains(listener);
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if (!(pTouchArea instanceof Sprite)) {
            return false;
        }
        interactionCounter.onInteraction(InteractionCounter.TOUCH_INSTANCE_INTERACTION_WEIGHT);

        final Sprite entity =  (Sprite) pTouchArea;
        Object userData = entity.getUserData();
        if (userData == null) {
            return false;
        }
        Class userDataType = userData.getClass();
        if (!areaTouchlisteners.containsKey(userDataType)) {
            Log.e(TAG, "No areaTouchlisteners for userDataType = " + userDataType);
            return false;
        }
        Set<GameAreaTouchListenerEntity.AreaTouchListener> listeners = areaTouchlisteners.get(userDataType);
        for (GameAreaTouchListenerEntity.AreaTouchListener listener : listeners) {
            if (listener.onTouch(pSceneTouchEvent, pTouchArea, pTouchAreaLocalX, pTouchAreaLocalY)){
                return true;
            }
        }
        return false;
    }
}
