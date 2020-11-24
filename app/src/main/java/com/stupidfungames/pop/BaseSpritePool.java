package com.stupidfungames.pop;

import android.util.Log;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.fixturedefdata.BaseEntityUserData;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.andengine.entity.sprite.Sprite;

/**
 * A pool of sprites.
 */
public abstract class BaseSpritePool extends BaseEntity {

  private static int num = 1;

  protected Queue<Sprite> sprites = new ConcurrentLinkedQueue<>();

  public BaseSpritePool(BinderEnity parent) {
    super(parent);
  }

  public Sprite get(float x, float y) {
    Sprite sprite = sprites.poll();
    if (sprite == null) {
      num++;
      sprite = createNewSprite(x, y);
      Log.d("asdasd", getClass().getSimpleName() + " creating sprite number " + num + " hash " + sprite.hashCode());
    } else {
      updateSprite(sprite);
      sprite.setX(x);
      sprite.setY(y);
      sprite.setVisible(true);
      sprite.setTouchEnabled(true);
    }
    return sprite;
  }


  public void recycle(Sprite sprite) {
    sprite.setVisible(false);
    sprite.setTouchEnabled(false);
    sprites.add(sprite);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    for (Sprite sprite : sprites) {
      Object userData = sprite.getUserData();
      if (userData instanceof BaseEntityUserData) {
        ((BaseEntityUserData) userData).reset();
      }
      sprite.setUserData(null);
    }
    sprites.clear();
    sprites = null;
  }

  protected void updateSprite(Sprite sprite) {
  }

  protected abstract Sprite createNewSprite(float x, float y);
}
