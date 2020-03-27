package com.wack.pop2.settingstray;

import com.wack.pop2.GamePauser;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.resources.textures.TextureId.PAUSE_BTN;

public class GamePauseQuickSettingsIconEntity extends BaseQuickSettingsIconEntity {

    private final ButtonUpTouchListener touchListener = new ButtonUpTouchListener() {
        @Override
        protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
            gamePauser.pauseGameWithPauseScreen();
            return true;
        }
    };

    private GamePauser gamePauser;

    public GamePauseQuickSettingsIconEntity(
            GamePauser gamePauser,
            GameQuickSettingsHostTrayEntity quickSettingsTrayEntity,
            GameTexturesManager gameTexturesManager,
            GameResources gameResources) {
        super(quickSettingsTrayEntity, gameTexturesManager, gameResources);
        this.gamePauser = gamePauser;
    }

    @Override
    protected TextureId getIconTextureId() {
        return PAUSE_BTN;
    }

    @Override
    protected GameQuickSettingsHostTrayEntity.IconId getIconId() {
        return GameQuickSettingsHostTrayEntity.IconId.GAME_PAUSE_BUTTON;
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
