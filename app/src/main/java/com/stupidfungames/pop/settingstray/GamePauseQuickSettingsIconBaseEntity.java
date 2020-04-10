package com.stupidfungames.pop.settingstray;

import com.stupidfungames.pop.GamePauser;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.stupidfungames.pop.resources.textures.TextureId.PAUSE_BTN;

public class GamePauseQuickSettingsIconBaseEntity extends QuickSettingsIconBaseEntity {

    private final ButtonUpTouchListener touchListener = new ButtonUpTouchListener() {
        @Override
        protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
            get(GamePauser.class).pauseGameWithPauseScreen();
            return true;
        }
    };

    public GamePauseQuickSettingsIconBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected TextureId getIconTextureId() {
        return PAUSE_BTN;
    }

    @Override
    protected GameQuickSettingsHostTrayBaseEntity.IconId getIconId() {
        return GameQuickSettingsHostTrayBaseEntity.IconId.GAME_PAUSE_BUTTON;
    }

    @Override
    protected AndengineColor getInitialIconColor() {
        return AndengineColor.RED;
    }

    @Override
    protected IOnAreaTouchListener getTouchListener() {
        return touchListener;
    }
}
