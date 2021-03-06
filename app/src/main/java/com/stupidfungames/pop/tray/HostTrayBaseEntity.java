package com.stupidfungames.pop.tray;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.tray.TrayStateMachine.State;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.sprite.Sprite;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public abstract class HostTrayBaseEntity<IconIdType> extends BaseEntity implements
    HostTrayCallback {

  /**
   * Contains the specifications of the tray's dimensions and layout
   */
  public static class Spec {

    public final float marginRightPx;
    public final float verticalAnchorPx;
    public final float horizontalAnchorPx;
    public final float openCloseAnimationDuration;

    public Spec(
        float marginRightPx,
        float verticalAnchorPx,
        float horizontalAnchorPx,
        float openCloseAnimationDuration) {
      this.marginRightPx = marginRightPx;
      this.verticalAnchorPx = verticalAnchorPx;
      this.horizontalAnchorPx = horizontalAnchorPx;
      this.openCloseAnimationDuration = openCloseAnimationDuration;
    }
  }

  private Spec spec;

  public HostTrayBaseEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected void createBindings(Binder binder) {
    binder.bind(HostTrayCallback.class, this);
    binder.bind(TrayStateMachine.class,
        new TrayStateMachine(shouldStartWithExpandedTray() ? State.EXPANDED : State.EMPTY));
    @Nullable TrayOpenCloseButtonBaseEntity trayOpenCloseButtonBaseEntity = getOpenCloseButtonEntity(
        this);
    if (trayOpenCloseButtonBaseEntity != null) {
      binder.bind(TrayOpenCloseButtonBaseEntity.class, trayOpenCloseButtonBaseEntity);
    }
    binder.bind(TrayIconsHolderBaseEntity.class, getTrayIconsHolderEntity(this));
    binder.bind(TrayAnimationManager.class,
        new TrayAnimationManager(getSpecInternal().openCloseAnimationDuration, this));
  }

  protected abstract Spec getSpec();

  protected abstract boolean shouldExpandWhenIconAdded();

  protected abstract boolean shouldStartWithExpandedTray();

  protected abstract TrayOpenCloseButtonBaseEntity getOpenCloseButtonEntity(BinderEnity parent);

  protected abstract TrayIconsHolderBaseEntity getTrayIconsHolderEntity(BinderEnity parent);

  protected abstract SoundId getOpenSound();

  protected abstract SoundId getCloseSound();

  protected abstract GameEvent getTrayOpenedEvent();

  protected abstract GameEvent getTrayClosedEvent();

  public void addIcon(IconIdType iconId, Sprite iconSprite,
      IOnAreaTouchListener areaTouchListener) {
    get(TrayIconsHolderBaseEntity.class).addIcon(iconId, iconSprite, areaTouchListener);

    TrayStateMachine stateMachine = get(TrayStateMachine.class);
    if (stateMachine.getCurrentState() == TrayStateMachine.State.EMPTY) {
      stateMachine.transitionState(TrayStateMachine.State.CLOSED);
    }
    if (shouldExpandWhenIconAdded()) {
      openTray();
    } else if (stateMachine.getCurrentState() == TrayStateMachine.State.EXPANDED) {
      EventBus.get().sendEvent(getTrayOpenedEvent());
    }
  }

  @Nullable
  public Sprite getIcon(IconIdType id) {
    return get(TrayIconsHolderBaseEntity.class).getIcon(id);
  }

  @Override
  public void openTray() {
    TrayAnimationManager trayAnimationManager = get(TrayAnimationManager.class);
    if (trayAnimationManager != null) {
      Futures.addCallback(
          trayAnimationManager.openTray(getOpenSound()),
          new FutureCallback() {
            @Override
            public void onSuccess(@NullableDecl Object result) {
              EventBus.get().sendEvent(getTrayOpenedEvent());
            }

            @Override
            public void onFailure(Throwable t) {
              Log.e("HostTrayBaseEntity", "Error opening tray ", t);
            }
          },
          ContextCompat.getMainExecutor(get(Context.class)));
    }
  }

  @Override
  public void closeTray() {
    TrayAnimationManager trayAnimationManager = get(TrayAnimationManager.class);
    if (trayAnimationManager != null) {
      Futures.addCallback(
          trayAnimationManager.closeTray(getCloseSound()),
          new FutureCallback() {
            @Override
            public void onSuccess(@NullableDecl Object result) {
              EventBus.get().sendEvent(getTrayClosedEvent());
            }

            @Override
            public void onFailure(Throwable t) {
              Log.e("HostTrayBaseEntity", "Error closing tray ", t);
            }
          },
          ContextCompat.getMainExecutor(get(Context.class)));
    }
  }

  @Override
  public TrayStateMachine getStateMachine() {
    return get(TrayStateMachine.class);
  }

  @Override
  public GameTexturesManager getTextureManager() {
    return get(GameTexturesManager.class);
  }

  private Spec getSpecInternal() {
    if (spec == null) {
      spec = getSpec();
    }
    return spec;
  }

  @Override
  public Rectangle getTrayIconsHolderRectangle() {
    return get(TrayIconsHolderBaseEntity.class).getTrayIconsHolderRectangle();
  }

  @Override
  public int[] getOpenPosition() {
    return get(TrayIconsHolderBaseEntity.class).getOpenPosition();
  }

  @Override
  public int[] getClosedPosition() {
    return get(TrayIconsHolderBaseEntity.class).getClosedPosition();
  }

  @Override
  public void onIconsTrayInitialized() {
    TrayOpenCloseButtonBaseEntity trayOpenCloseButtonBaseEntity = get(
        TrayOpenCloseButtonBaseEntity.class);
    if (trayOpenCloseButtonBaseEntity != null) {
      trayOpenCloseButtonBaseEntity.onIconsTrayInitialized();
    }
  }

  @Override
  public void onIconsTrayDimensionsChanged() {
    TrayOpenCloseButtonBaseEntity trayOpenCloseButtonBaseEntity = get(
        TrayOpenCloseButtonBaseEntity.class);
    if (trayOpenCloseButtonBaseEntity != null) {
      trayOpenCloseButtonBaseEntity.onIconsTrayDimensionsChanged();
    }
  }

  @Override
  public int[] getAnchorPx() {
    return new int[]{
        (int) (getSpecInternal().horizontalAnchorPx - getMarginRightPx()),
        (int) (getSpecInternal().verticalAnchorPx)};
  }

  private int getMarginRightPx() {
    return (int) getSpecInternal().marginRightPx;
  }
}
