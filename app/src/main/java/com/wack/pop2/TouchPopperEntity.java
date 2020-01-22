package com.wack.pop2;

import com.wack.pop2.bubblepopper.BubblePopperEntity;
import com.wack.pop2.fixturedefdata.BubbleEntityUserData;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.jbox2d.common.Vec2;

/**
 * This class enables the user to pop bubbles that they tap on the screen
 */
public class TouchPopperEntity extends BaseEntity implements GameAreaTouchListenerEntity.AreaTouchListener {

    private BubblePopperEntity bubblePopperEntity;
    private GameAreaTouchListenerEntity touchListenerEntity;

    public TouchPopperEntity(GameAreaTouchListenerEntity touchListenerEntity,
                             BubblePopperEntity bubblePopperEntity,
                             GameResources gameResources) {
        super(gameResources);
        this.touchListenerEntity = touchListenerEntity;
        this.bubblePopperEntity = bubblePopperEntity;
    }

    @Override
    public void onCreateScene() {
        touchListenerEntity.addAreaTouchListener(BubbleEntityUserData.class, this);
    }

    @Override
    public void onDestroy() {
        touchListenerEntity.removeAreaTouchListener(BubbleEntityUserData.class, this);
    }

    @Override
    public boolean onTouch(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if(pSceneTouchEvent.isActionDown()) {
            final Sprite entity =  (Sprite) pTouchArea;
            if (entity.getUserData() == null) {
                return false;
            }
            final BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) entity.getUserData();
            if (bubbleEntityUserData.isGameOverWhenPopped) {
                bubblePopperEntity.triggerGameOverExplosion(entity);
            } else {
                bubblePopperEntity.popBubble(entity, bubbleEntityUserData.size, bubbleEntityUserData.bubbleType);
            }
            return true;
        }
        return false;
    }
}
