package com.stupidfungames.pop;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.BubbleTouchedEventPayload;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.fixturedefdata.BubbleEntityUserData;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

/**
 * This class enables the user to pop bubbles that they tap on the screen
 */
public class TouchPopperFactoryEntity extends BaseEntity {

    public TouchPopperFactoryEntity(BinderEnity parent) {
        super(parent);
    }

    public TouchBubblePopper getNewTouchBubblePopper() {
        return new TouchBubblePopper();
    }

    public static class TouchBubblePopper implements IOnAreaTouchListener {

        private TouchBubblePopper() {}

        @Override
        public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
            if (pSceneTouchEvent.isActionDown()) {
                final Sprite entity = (Sprite) pTouchArea;
                if (entity.getUserData() == null) {
                    return false;
                }
                final BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) entity.getUserData();
                EventBus.get().sendEvent(GameEvent.BUBBLE_TOUCHED, new BubbleTouchedEventPayload(entity, bubbleEntityUserData.size, bubbleEntityUserData.bubbleType));
                return true;
            }
            return false;
        }
    }
}