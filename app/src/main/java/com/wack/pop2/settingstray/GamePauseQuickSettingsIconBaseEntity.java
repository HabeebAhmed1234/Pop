package com.wack.pop2.settingstray;

import com.wack.pop2.GamePauser;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.resources.textures.TextureId.PAUSE_BTN;

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
