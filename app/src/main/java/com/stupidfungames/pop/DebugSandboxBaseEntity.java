package com.stupidfungames.pop;

import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;

public class DebugSandboxBaseEntity extends BaseEntity {

  private static final String TAG = "DebugTouchTracerEntity";

  public DebugSandboxBaseEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {

  }

  @Override
  public void onCreateScene() {
    super.onCreateScene();

    final Sprite parent = new Sprite(
        0,
        ScreenUtils.getSreenSize().heightPx / 2 - 300,
        get(GameTexturesManager.class).getTextureRegion(TextureId.BALL),
        vertexBufferObjectManager);

    parent.setScale(3f);

    parent.setPosition(ScreenUtils.getSreenSize().widthPx / 2 - parent.getWidth() / 2,
        ScreenUtils.getSreenSize().heightPx / 2 - parent.getHeight() / 2);

    final Sprite child = new Sprite(
        0,
        50,
        get(GameTexturesManager.class).getTextureRegion(TextureId.BULLET),
        vertexBufferObjectManager);

    centerInHorizontal(parent, child);

    parent.attachChild(child);
    addToScene(parent);

  }

  public static void centerInHorizontal(IAreaShape parent, Sprite child) {
    child.setX(parent.getWidth() / 2 - child.getWidth() / 2);
  }
}
