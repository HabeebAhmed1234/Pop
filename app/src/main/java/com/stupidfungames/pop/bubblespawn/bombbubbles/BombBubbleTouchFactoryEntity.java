package com.stupidfungames.pop.bubblespawn.bombbubbles;

import static com.stupidfungames.pop.GameConstants.BOMB_BUBBLE_EXPLOSION_RADIUS_DP;
import static com.stupidfungames.pop.utils.ScreenUtils.dpToPx;

import android.content.Context;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.entitymatchers.BubblesInRadiusEntityMatcher;
import com.stupidfungames.pop.fixturedefdata.BombBubbleEntityUserData;
import java.util.List;
import org.andengine.entity.IEntity;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

/**
 * This class enables the user to pop bomb bubbles or maybe trigger them
 */
public class BombBubbleTouchFactoryEntity extends BaseEntity {

  public BombBubbleTouchFactoryEntity(BinderEnity parent) {
    super(parent);
  }

  public BombBubbleTouchedListener getBombBubbleTouchListener() {
    return new BombBubbleTouchedListener();
  }

  public class BombBubbleTouchedListener implements IOnAreaTouchListener {

    private BombBubbleTouchedListener() {
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea,
        float pTouchAreaLocalX, float pTouchAreaLocalY) {
      if (pSceneTouchEvent.isActionDown()) {
        final Sprite sprite = (Sprite) pTouchArea;
        if (sprite.getUserData() == null && !(sprite
            .getUserData() instanceof BombBubbleEntityUserData)) {
          return false;
        }
        if (sprite.getColor().equals(BombBubbleSpritePool.DIFFUSE_BOMB_COLOUR)) {
          // User had diffused the bomb. make it disappear and pop all bubbles within a radius of it
          get(BombBubbleSpritePool.class).recycle(sprite);
          popBubblesInRadius(sprite);
        } else {
          get(BombBubbleExpiredListenerEntity.class).onBombBubbleExpired(sprite);
        }
        return true;
      }
      return false;
    }
  }

  private void popBubblesInRadius(Sprite sprite) {
    List<IEntity> bubblesInRadius = scene.query(
        new BubblesInRadiusEntityMatcher(
            sprite.getCenter(),
            dpToPx(BOMB_BUBBLE_EXPLOSION_RADIUS_DP, get(Context.class)),
            false,
            true));
    BubblePopperEntity bubblePopperEntity = get(BubblePopperEntity.class);
    for (IEntity bubble : bubblesInRadius) {
      bubblePopperEntity.popBubble((Sprite) bubble);
    }
  }
}