package com.wack.pop2;

import com.wack.pop2.fixturedefdata.BubbleEntityUserData;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.jbox2d.common.Vec2;

/**
 * This class enables the user to pop bubbles that they tap on the screen
 */
public class TouchPopperEntity extends BaseEntity implements IOnAreaTouchListener {

    private BubblePopperEntity bubblePopperEntity;

    public TouchPopperEntity(BubblePopperEntity bubblePopperEntity, GameResources gameResources) {
        super(gameResources);
        this.bubblePopperEntity = bubblePopperEntity;
    }

    @Override
    public void onCreateScene() {
        scene.setOnAreaTouchListener(this);
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if(pSceneTouchEvent.isActionDown()) {
            final Sprite entity =  (Sprite) pTouchArea;
            if (entity.getUserData() == null) {
                return false;
            }
            final Object userData = entity.getUserData();

            if(userData instanceof BubbleEntityUserData) {
                BubbleEntityUserData bubbleEntityUserData = (BubbleEntityUserData) userData;
                if (bubbleEntityUserData.isGameOverWhenPopped) {
                    bubblePopperEntity.triggerGameOverExplosion(entity);
                    return true;
                } else {
                    bubblePopperEntity.popBubble(entity, bubbleEntityUserData.size, new Vec2(entity.getX(), entity.getY()), bubbleEntityUserData.bubbleType);
                }
            }

            return true;
        }
        return false;
    }
}
