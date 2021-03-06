package com.stupidfungames.pop.icons;

import androidx.annotation.Nullable;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameDifficultyEventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.IconUnlockedEventPayload;
import com.stupidfungames.pop.gameiconstray.GameIconsHostTrayEntity;
import com.stupidfungames.pop.resources.textures.GameTexturesManager;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.tooltips.GameTooltipsEntity;
import com.stupidfungames.pop.tooltips.TooltipId;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
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

  private Sprite iconSprite;
  private boolean isUnlocked;
  private boolean overrideTouchListenerEnabled = false;

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
        .subscribe(GameEvent.GAME_DIFFICULTY_CHANGED, this, true);
  }

  @Override
  public void onDestroy() {
    EventBus.get()
        .unSubscribe(GameEvent.GAME_DIFFICULTY_CHANGED, this);
    iconSprite.removeOnAreaTouchListener();
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    switch (event) {
      case GAME_DIFFICULTY_CHANGED:
        GameDifficultyEventPayload progressEventPayload = (GameDifficultyEventPayload) payload;
        onGameProgressChanged(progressEventPayload.difficulty);
        break;
    }
  }

  public boolean isUnlocked() {
    return isUnlocked;
  }

  private void onGameProgressChanged(float newProgressPercentage) {
    if (newProgressPercentage >= getGameProgressPercentageUnlockThreshold() && !isUnlocked) {
      isUnlocked = true;
      setIconColor(getUnlockedIconColor());
      addIconToTray();
      onIconUnlocked();
      broadcastIconUnlock();
      showTooltip();
    }
  }

  private void showTooltip() {
    get(GameTooltipsEntity.class)
        .maybeShowTooltip(getIconTooltipId());
  }

  private void broadcastIconUnlock() {
    EventBus.get().sendEvent(GameEvent.ICON_UNLOCKED, new IconUnlockedEventPayload(getIconId()));
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
                if ((overrideTouchListenerEnabled && overrideTouchListener != null
                    && overrideTouchListener
                    .onAreaTouched(pSceneTouchEvent, pTouchArea, pTouchAreaLocalX,
                        pTouchAreaLocalY))
                    || (touchListener != null && touchListener
                    .onAreaTouched(pSceneTouchEvent, pTouchArea, pTouchAreaLocalX,
                        pTouchAreaLocalY))) {
                  return true;
                }
                return NO_OP_AREA_TOUCH_LISTENER
                    .onAreaTouched(pSceneTouchEvent, pTouchArea, pTouchAreaLocalX,
                        pTouchAreaLocalY);
              }
            });
  }

  protected void enableOverrideTouchListener(boolean overrideTouchListenerEnabled) {
    this.overrideTouchListenerEnabled = overrideTouchListenerEnabled;
  }

  protected Sprite getIconSprite() {
    return iconSprite;
  }

  protected void setIconColor(AndengineColor color) {
    iconSprite.setColor(color);
  }

  protected AndengineColor getCurrentIconSpriteColor() {
    return iconSprite.getColor();
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
