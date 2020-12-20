package com.stupidfungames.pop.upgrades;

import static com.stupidfungames.pop.eventbus.GameEvent.UPGRADE_ACQUIRED;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.resources.sounds.GameSoundsManager;
import com.stupidfungames.pop.resources.sounds.SoundId;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

/**
 * This class enables the user to pop bubbles that they tap on the screen
 */
public class UpgradeTouchFactoryEntity extends BaseEntity {

  public UpgradeTouchFactoryEntity(BinderEnity parent) {
    super(parent);
  }

  public UpgradeTouchListener getNewTouchListener() {
    return new UpgradeTouchListener();
  }

  public class UpgradeTouchListener implements IOnAreaTouchListener {

    private UpgradeTouchListener() {
    }

    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea,
        float pTouchAreaLocalX, float pTouchAreaLocalY) {
      if (pSceneTouchEvent.isActionDown()) {
        final Sprite entity = (Sprite) pTouchArea;
        if (entity.getUserData() == null) {
          return false;
        }
        onUpgradeTouched(entity);
        return true;
      }
      return false;
    }
  }

  private void onUpgradeTouched(IShape upgradeSprite) {
    EventBus.get().sendEvent(UPGRADE_ACQUIRED);
    removeFromScene(upgradeSprite);
    // Play the upgrade sound
    get(GameSoundsManager.class).getSound(SoundId.UPGRADE_POP).play();
  }
}
