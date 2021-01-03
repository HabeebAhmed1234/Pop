package com.stupidfungames.pop.bubblespawn.bombbubbles;

import static com.stupidfungames.pop.GameConstants.BOMB_BUBBLE_EXPLOSION_RADIUS_DP;
import static com.stupidfungames.pop.utils.ScreenUtils.dpToPx;

import android.content.Context;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblepopper.BubblePopperEntity;
import com.stupidfungames.pop.entitymatchers.BubblesInRadiusEntityMatcher;
import com.stupidfungames.pop.fixturedefdata.BombBubbleEntityUserData;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
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
          popBubblesInRadius(sprite);
          get(GameSoundsManager.class).getSound(SoundId.EXPOSION_2).play();
          // User had diffused the bomb. make it disappear and pop all bubbles within a radius of it
          get(BombBubbleSpritePool.class).recycle(sprite);
        } else if (sprite.getColor().equals(BombBubbleSpritePool.EXPLODING_BOMB_COLOUR)) {
          get(BombBubbleExpiredListenerEntity.class).onBombBubbleExpired(sprite);
        }
        return true;
      }
      return false;
    }
  }

  @Override
  protected void createBindings(Binder binder) {
    super.createBindings(binder);
    binder.bind(BombBubbleExplosionEffectEntity.class, new BombBubbleExplosionEffectEntity(this));
  }

  private void popBubblesInRadius(Sprite sprite) {
    float explosionRadiusPx = dpToPx(BOMB_BUBBLE_EXPLOSION_RADIUS_DP, get(Context.class));
    List<IEntity> bubblesInRadius = scene.query(
        new BubblesInRadiusEntityMatcher(
            sprite.getCenterX(),
            sprite.getCenterY(),
            explosionRadiusPx,
            false,
            true));
    BubblePopperEntity bubblePopperEntity = get(BubblePopperEntity.class);
    for (IEntity bubble : bubblesInRadius) {
      bubblePopperEntity.popBubbleWithNoChildren((Sprite) bubble);
    }
    get(BombBubbleExplosionEffectEntity.class)
        .explode(sprite.getCenterX(), sprite.getCenterY(), explosionRadiusPx);
  }
}