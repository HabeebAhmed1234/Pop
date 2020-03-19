package com.wack.pop2.areatouch;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.interaction.InteractionCounter;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;

import java.util.ArrayList;
import java.util.List;

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

    private List<AreaTouchLayer> areaTouchlayers = new ArrayList<>();
    private InteractionCounter interactionCounter;

    public GameAreaTouchListenerEntity(InteractionCounter interactionCounter, GameResources gameResources) {
        super(gameResources);
        this.interactionCounter = interactionCounter;

        initTouchLayers();

        scene.setOnAreaTouchListener(this);
        scene.setOnAreaTouchTraversalFrontToBack();
    }

    private void initTouchLayers() {
        for (TouchPriority priority : TouchPriority.values()) {
            areaTouchlayers.add(new AreaTouchLayer(interactionCounter));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scene.setOnAreaTouchListener(null);
    }

    public boolean hasAreaTouchListener(Class<? extends BaseEntityUserData> userDataType, AreaTouchListener listener) {
        for (AreaTouchLayer layer : areaTouchlayers) {
            if (layer.hasAreaTouchListener(userDataType, listener)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a listener on the lowest priority layer
     */
    public GameAreaTouchListenerEntity addAreaTouchListener(Class<? extends BaseEntityUserData> userDataType, AreaTouchListener listener) {
        addAreaTouchListener(TouchPriority.LOW, userDataType, listener);
        return this;
    }

    /**
     * Adds a listener on the given priority layer
     */
    public GameAreaTouchListenerEntity addAreaTouchListener(TouchPriority priority, Class<? extends BaseEntityUserData> userDataType, AreaTouchListener listener) {
        areaTouchlayers.get(priority.ordinal()).addAreaTouchListener(userDataType, listener);
        return this;
    }

    public void removeAreaTouchListener(Class<? extends BaseEntityUserData> userDataType, AreaTouchListener listener) {
        for (AreaTouchLayer layer : areaTouchlayers) {
            if (layer.removeAreaTouchListener(userDataType, listener)) {
                return;
            }
        }
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        for (AreaTouchLayer layer : areaTouchlayers) {
            if (layer.onAreaTouched(pSceneTouchEvent, pTouchArea, pTouchAreaLocalX, pTouchAreaLocalY)) {
                return true;
            }
        }
        return false;
    }
}
