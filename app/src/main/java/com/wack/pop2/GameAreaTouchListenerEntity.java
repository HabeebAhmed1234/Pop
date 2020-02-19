package com.wack.pop2;

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

public class GameAreaTouchListenerEntity extends BaseEntity implements IOnAreaTouchListener {

    private static final String TAG = "AreaTouchListenerEntity";

    public interface AreaTouchListener {

        /**
         * Returns true if the touch event was handled
         * @param pSceneTouchEvent
         * @param pTouchArea
         * @param pTouchAreaLocalX
         * @param pTouchAreaLocalY
         * @return
         */
        boolean onTouch(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY);
    }

    private Map<Class<? extends BaseEntityUserData>, Set<AreaTouchListener>> areaTouchlisteners = new HashMap<>();
    private InteractionCounter interactionCounter;

    public GameAreaTouchListenerEntity(InteractionCounter interactionCounter, GameResources gameResources) {
        super(gameResources);
        this.interactionCounter = interactionCounter;
        scene.setOnAreaTouchListener(this);
    }

    public boolean hasAreaTouchListener(Class<? extends BaseEntityUserData> userDataType, AreaTouchListener listener) {
        return areaTouchlisteners.containsKey(userDataType) && areaTouchlisteners.get(userDataType).contains(listener);
    }

    public GameAreaTouchListenerEntity addAreaTouchListener(Class<? extends BaseEntityUserData> userDataType, AreaTouchListener listener) {
        if (!areaTouchlisteners.containsKey(userDataType)) {
            areaTouchlisteners.put(userDataType, new HashSet<AreaTouchListener>());
        }

        Set<AreaTouchListener> listeners = areaTouchlisteners.get(userDataType);
        if (listeners.contains(listener)) {
            Log.w(TAG, "Listener " + listener + " is already listening to " + userDataType + " touch event");
        }
        listeners.add(listener);
        return this;
    }

    public void removeAreaTouchListener(Class<? extends BaseEntityUserData> userDataType, AreaTouchListener listener) {
        if (!areaTouchlisteners.containsKey(userDataType)) {
            Log.w(TAG, "No listeners for " + userDataType);
        }
        Set<AreaTouchListener> listeners = areaTouchlisteners.get(userDataType);
        listeners.remove(listener);
        if (listeners.isEmpty()) {
            areaTouchlisteners.remove(userDataType);
        }
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
        Set<AreaTouchListener> listeners = areaTouchlisteners.get(userDataType);
        boolean handled = false;
        for (AreaTouchListener listener : listeners) {
            if (listener.onTouch(pSceneTouchEvent, pTouchArea, pTouchAreaLocalX, pTouchAreaLocalY)){
                handled = true;
            }
        }
        return handled;
    }
}
