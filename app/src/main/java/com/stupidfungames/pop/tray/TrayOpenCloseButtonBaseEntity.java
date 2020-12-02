package com.stupidfungames.pop.tray;

import android.content.Context;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.GameFixtureDefs;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.collision.CollisionFilters;
import com.stupidfungames.pop.physics.PhysicsFactory;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.statemachine.BaseStateMachine;
import com.stupidfungames.pop.touchlisteners.ButtonUpTouchListener;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

public abstract class TrayOpenCloseButtonBaseEntity extends BaseEntity implements
    BaseStateMachine.Listener<TrayStateMachine.State> {

  public static class ButtonSpec {

    public final int iconSizePx;
    public final int iconRightMarginPx;

    public ButtonSpec(Context context,
        int iconSizeDp,
        int iconRightMarginDp) {
      iconSizePx = ScreenUtils.dpToPx(iconSizeDp, context);
      iconRightMarginPx = ScreenUtils.dpToPx(iconRightMarginDp, context);
    }
  }

  private ButtonSpec buttonSpec;

  private Sprite iconSpriteOpen;
  private Sprite iconSpriteClose;
  private HostTrayCallback hostTrayCallback;

  private IOnAreaTouchListener toggleBtnTouchListener =
      new ButtonUpTouchListener() {
        @Override
        protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea,
            float pTouchAreaLocalX, float pTouchAreaLocalY) {
          if (hostTrayCallback.getStateMachine().canOpen()) {
            onOpenBtnTouch();
            return true;
          } else if (hostTrayCallback.getStateMachine().canClose()) {
            onCloseBtnTouch();
            return true;
          }
          return false;
        }
      };

  public TrayOpenCloseButtonBaseEntity(
      HostTrayCallback hostTrayCallback,
      BinderEnity parent) {
    super(parent);
    this.hostTrayCallback = hostTrayCallback;
  }

  protected abstract ButtonSpec getButtonSpec();

  protected abstract TextureId getOpenButtonTextureId();

  protected abstract TextureId getCloseButtonTextureId();

  private ButtonSpec getButtonSpecInternal() {
    if (buttonSpec == null) {
      buttonSpec = getButtonSpec();
    }
    return buttonSpec;
  }

  @Override
  public void onCreateScene() {
    hostTrayCallback.getStateMachine().addAllStateTransitionListener(this);
  }

  @Override
  public void onDestroy() {
    hostTrayCallback.getStateMachine().removeAllStateTransitionListener(this);

    iconSpriteOpen.removeOnAreaTouchListener();
    iconSpriteClose.removeOnAreaTouchListener();
  }

  @Override
  public void onEnterState(TrayStateMachine.State newState) {
    if (isIconCreated()) {
      updateIconTexture(newState);
    }
  }

  public void onIconsTrayInitialized() {
    /*if (!(isIconCreated())) {
      createIconSprite();
    }

    // we only need one of the icons to be touchable
    addToSceneWithTouch(hostTrayCallback.getTrayIconsHolderRectangle(), iconSpriteOpen,
        toggleBtnTouchListener);
    addToScene(hostTrayCallback.getTrayIconsHolderRectangle(), iconSpriteClose);

    refreshDimensions();*/
  }

  public void onIconsTrayDimensionsChanged() {
    refreshDimensions();
  }

  public void refreshDimensions() {
    if (iconSpriteOpen == null || iconSpriteClose == null) {
      return;
    }

    int iconX = -getButtonSpecInternal().iconSizePx - getButtonSpecInternal().iconRightMarginPx;
    int iconY = (int) (hostTrayCallback.getTrayIconsHolderRectangle().getHeightScaled() / 2
        - getButtonSpecInternal().iconSizePx / 2);
    setIconPosition(iconX, iconY);
  }

  private boolean isIconCreated() {
    return iconSpriteOpen != null && iconSpriteClose != null;
  }

  private void createIconSprite() {
    iconSpriteOpen = new Sprite(
        0,
        0,
        hostTrayCallback.getTextureManager().getTextureRegion(getOpenButtonTextureId()),
        vertexBufferObjectManager);
    iconSpriteOpen.setWidth(getButtonSpecInternal().iconSizePx);
    iconSpriteOpen.setHeight(getButtonSpecInternal().iconSizePx);
    iconSpriteOpen.setVisible(false);

    iconSpriteClose = new Sprite(
        0,
        0,
        hostTrayCallback.getTextureManager().getTextureRegion(getCloseButtonTextureId()),
        vertexBufferObjectManager);
    iconSpriteClose.setWidth(getButtonSpecInternal().iconSizePx);
    iconSpriteClose.setHeight(getButtonSpecInternal().iconSizePx);
    iconSpriteClose.setVisible(false);

    //setUpPhysics();
  }

  private void setIconPosition(float x, float y) {
    iconSpriteOpen.setX(x);
    iconSpriteClose.setX(x);

    iconSpriteOpen.setY(y);
    iconSpriteClose.setY(y);
  }

  private void updateIconTexture(TrayStateMachine.State newState) {
    if (newState == TrayStateMachine.State.EMPTY) {
      iconSpriteOpen.setVisible(false);
      iconSpriteClose.setVisible(false);
    } else if (newState == TrayStateMachine.State.EXPANDED
        || newState == TrayStateMachine.State.EXPANDING) {
      iconSpriteOpen.setVisible(false);
      iconSpriteClose.setVisible(true);
    } else if (newState == TrayStateMachine.State.CLOSED
        || newState == TrayStateMachine.State.CLOSING) {
      iconSpriteOpen.setVisible(true);
      iconSpriteClose.setVisible(false);

    }
  }

  private void setUpPhysics() {
    final FixtureDef iconFixtureDef = GameFixtureDefs.ICON_BOX_FIXTURE_DEF;
    iconFixtureDef.setFilter(CollisionFilters.BUBBLE_FILTER);
    final Body openBody = PhysicsFactory
        .createBoxBody(physicsWorld, iconSpriteOpen, BodyType.STATIC, iconFixtureDef);
    final Body closeBody = PhysicsFactory
        .createBoxBody(physicsWorld, iconSpriteClose, BodyType.STATIC, iconFixtureDef);
    linkReversePhysics(iconSpriteOpen, openBody);
    linkReversePhysics(iconSpriteClose, closeBody);
  }

  private void onOpenBtnTouch() {
    hostTrayCallback.openTray();
  }

  private void onCloseBtnTouch() {
    hostTrayCallback.closeTray();
  }
}
