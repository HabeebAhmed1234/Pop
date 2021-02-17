package com.stupidfungames.pop;

import static org.andengine.input.touch.TouchEvent.ACTION_CANCEL;
import static org.andengine.input.touch.TouchEvent.ACTION_DOWN;
import static org.andengine.input.touch.TouchEvent.ACTION_MOVE;
import static org.andengine.input.touch.TouchEvent.ACTION_OUTSIDE;
import static org.andengine.input.touch.TouchEvent.ACTION_UP;

import com.stupidfungames.pop.GameSceneTouchListenerEntity.SceneTouchListener;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;

/**
 * Shows a pointer on the screen anytime the user presses on it.
 */
public class PointerEntity extends BaseEntity implements SceneTouchListener {

  private Sprite pointerSprite;

  public PointerEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateResources() {
    super.onCreateResources();
    pointerSprite = new Sprite(
        -1000,
        -1000,
        get(GameTexturesManager.class).getTextureRegion(TextureId.POINTER),
        vertexBufferObjectManager);
  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();
    scene.attachChild(pointerSprite);
    get(GameSceneTouchListenerEntity.class).addSceneTouchListener(this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    get(GameSceneTouchListenerEntity.class).removeSceneTouchListener(this);
  }

  @Override
  public boolean onSceneTouchEvent(Scene scene, TouchEvent touchEvent) {
    boolean handled = false;
    switch (touchEvent.getAction()) {
      case ACTION_DOWN:
      case ACTION_MOVE:
      case ACTION_UP:
        pointToCoord(touchEvent);
        handled = true;
        break;
      case ACTION_OUTSIDE:
      case ACTION_CANCEL:
        break;
    }
    return handled;
  }

  private void pointToCoord(TouchEvent touchEvent) {
    pointerSprite.setScaledX(touchEvent.getX() - pointerSprite.getWidthScaled() / 2);
    pointerSprite.setScaledY(touchEvent.getY() - pointerSprite.getHeightScaled() * 0.1f);
  }
}
