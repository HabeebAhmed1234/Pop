package com.stupidfungames.pop.settingstray;

import static com.stupidfungames.pop.resources.textures.TextureId.SAVE_BTN;
import static com.stupidfungames.pop.settingstray.GameQuickSettingsHostTrayBaseEntity.IconId.GAME_SAVE_BUTTON;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.settingstray.GameQuickSettingsHostTrayBaseEntity.IconId;
import com.stupidfungames.pop.touchlisteners.ButtonUpTouchListener;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

public class SaveGameQuickSettingsIconEntity extends QuickSettingsIconBaseEntity {

  public interface SaveGameButtonCallback {
    void saveGamePressed();
  }

  private final ButtonUpTouchListener touchListener = new ButtonUpTouchListener() {

    @Override
    protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea,
        float pTouchAreaLocalX, float pTouchAreaLocalY) {
      get(SaveGameButtonCallback.class).saveGamePressed();
      return true;
    }
  };

  public SaveGameQuickSettingsIconEntity(BinderEnity parent) {
    super(parent);
  }

  @Override
  protected TextureId getIconTextureId() {
    return SAVE_BTN;
  }

  @Override
  protected IconId getIconId() {
    return GAME_SAVE_BUTTON;
  }

  @Override
  protected AndengineColor getInitialIconColor() {
    return AndengineColor.PINK;
  }

  @Override
  protected IOnAreaTouchListener getTouchListener() {
    return touchListener;
  }
}
