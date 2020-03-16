package com.wack.pop2.settingstray;

import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.tray.BaseHostTrayEntity;
import com.wack.pop2.tray.BaseTrayIconsHolderEntity;
import com.wack.pop2.tray.BaseTrayOpenCloseButtonEntity;
import com.wack.pop2.utils.ScreenUtils;

/**
 * A small expandable tray that renders on the top right of the screen.
 */
public class GameQuickSettingsHostTrayEntity extends BaseHostTrayEntity<GameQuickSettingsHostTrayEntity.IconId> {

    public enum IconId {
        SETTING_MUSIC_TOGGLE,
    }

    public GameQuickSettingsHostTrayEntity(
            GameTexturesManager textureManager,
            GameAreaTouchListenerEntity areaTouchListenerEntity,
            GameResources gameResources) {
        super(textureManager, areaTouchListenerEntity, gameResources);
    }

    @Override
    protected Spec getSpec() {
        return new Spec(
                hostActivity.getActivityContext(),
                4,
                44,
                ScreenUtils.getSreenSize().widthDp,
                0.2f);
    }

    @Override
    protected BaseTrayOpenCloseButtonEntity getOpenCloseButtonEntity(GameResources gameResources) {
        return new GameQuickSettingsTrayOpenCloseButton(this, gameResources);
    }

    @Override
    protected BaseTrayIconsHolderEntity getTrayIconsHolderEntity(GameResources gameResources) {
        return new GameQuickSettingsTrayIconsHolderEntity(this, gameResources);
    }
}
