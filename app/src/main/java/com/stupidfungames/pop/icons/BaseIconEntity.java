package com.stupidfungames.pop.icons;

import android.content.Context;
import androidx.annotation.Nullable;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameProgressEventPayload;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tooltips.GameTooltipsEntity;
import com.stupidfungames.pop.tooltips.TooltipId;
import com.stupidfungames.pop.utils.ScreenUtils;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.transformation.Transformation;
import org.andengine.util.color.AndengineColor;

/**
 * The base class for all unlockable {@link GameIconsHostTrayEntity} icons in the game.
 */
public abstract class BaseIconEntity extends BaseEntity implements EventBus.Subscriber {

  private static final IOnAreaTouchListener NO_OP_AREA_TOUCH_LISTENER = new IOnAreaTouchListener() {
    @Override
    public boolean onAreaTouched(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea,
        float pTouchAreaLocalX, float pTouchAreaLocalY) {
      return false;
    }
  };

  private static final int TOOLTIP_LEFT_PADDING_DP = 8;

  private Sprite iconSprite;
  private boolean isUnlocked;

  public BaseIconEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  public void onCreateScene() {
    iconSprite = new Sprite(
        -1000,
        0,
        get(GameTexturesManager.class).getTextureRegion(getIconTextureId()),
        vertexBufferObjectManager);

    setIconColor(AndengineColor.TRANSPARENT);

    EventBus.get()
        .subscribe(GameEvent.GAME_PROGRESS_CHANGED, this, true)
        .subscribe(GameEvent.GAME_ICONS_TRAY_OPENED, this, true);
  }

  @Override
  public void onDestroy() {
    EventBus.get()
        .unSubscribe(GameEvent.GAME_PROGRESS_CHANGED, this)
        .unSubscribe(GameEvent.GAME_ICONS_TRAY_OPENED, this);
    iconSprite.removeOnAreaTouchListener();
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    switch (event) {
      case GAME_PROGRESS_CHANGED:
        GameProgressEventPayload progressEventPayload = (GameProgressEventPayload) payload;
        onGameProgressChanged(progressEventPayload.percentProgress);
        break;
      case GAME_ICONS_TRAY_OPENED:
        if (isUnlocked()) {
          float[] tooltipAnchor = getIconTooltipAnchor();
          get(GameTooltipsEntity.class)
              .maybeShowTooltip(getIconTooltipId(), tooltipAnchor[0], tooltipAnchor[1]);
        }
        break;
    }
  }

  private void onGameProgressChanged(float newProgressPercentage) {
    if (newProgressPercentage >= getGameProgressPercentageUnlockThreshold() && !isUnlocked) {
      isUnlocked = true;
      setIconColor(getUnlockedIconColor());
      addIconToTray();
      onIconUnlocked();
    }
  }

  private float[] getIconTooltipAnchor() {
    float[] anchor = new float[2];
    Transformation transformation = iconSprite.getLocalToSceneTransformation();
    anchor[0] = -ScreenUtils.dpToPx(TOOLTIP_LEFT_PADDING_DP, get(Context.class));
    anchor[1] = iconSprite.getHeightScaled() / 2;
    transformation.transform(anchor);
    return anchor;
  }

  protected void addIconToTray() {
    @Nullable final IOnAreaTouchListener overrideTouchListener = getOverrideTouchListener();
    @Nullable final IOnAreaTouchListener touchListener = getTouchListener();
    get(GameIconsHostTrayEntity.class).addIcon(getIconId(), iconSprite,
        overrideTouchListener == null && touchListener == null ? NO_OP_AREA_TOUCH_LISTENER
            : new IOnAreaTouchListener() {
              @Override
              public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
                  ITouchArea pTouchArea, float pTouchAreaLocalX,
                  float pTouchAreaLocalY) {
                if ((overrideTouchListener != null && overrideTouchListener.onAreaTouched(pSceneTouchEvent, pTouchArea, pTouchAreaLocalX, pTouchAreaLocalY))
                    || (touchListener != null && touchListener.onAreaTouched(pSceneTouchEvent, pTouchArea, pTouchAreaLocalX, pTouchAreaLocalY))) {
                  return true;
                }
                return NO_OP_AREA_TOUCH_LISTENER
                    .onAreaTouched(pSceneTouchEvent, pTouchArea, pTouchAreaLocalX,
                        pTouchAreaLocalY);
              }
            });
  }

  protected Sprite getIconSprite() {
    return iconSprite;
  }

  protected void setIconColor(AndengineColor color) {
    iconSprite.setColor(color);
  }

  protected boolean isUnlocked() {
    return isUnlocked;
  }

  /**
   * Called before the {@link IOnAreaTouchListener} returned by getTouchListener. If touch is
   * handled in this listener then the touch even will not propagate to the getTouchListener
   */
  protected IOnAreaTouchListener getOverrideTouchListener() {
    return null;
  }

  protected abstract TextureId getIconTextureId();

  protected abstract GameIconsHostTrayEntity.IconId getIconId();

  protected abstract float getGameProgressPercentageUnlockThreshold();

  protected abstract void onIconUnlocked();

  protected abstract AndengineColor getUnlockedIconColor();

  @Nullable
  protected abstract IOnAreaTouchListener getTouchListener();

  protected abstract TooltipId getIconTooltipId();
}
