package com.wack.pop2.gameiconstray;

import com.wack.pop2.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.tray.BaseHostTrayEntity;
import com.wack.pop2.tray.BaseTrayIconsHolderEntity;
import com.wack.pop2.tray.BaseTrayOpenCloseButtonEntity;
import com.wack.pop2.utils.ScreenUtils;

/**
 * Single entity used to manage the icons for different tools in the game.
 */
public class GameIconsHostTrayEntity extends BaseHostTrayEntity<GameIconsHostTrayEntity.IconId> {

    public enum IconId {
        BALL_AND_CHAIN_ICON,
        TURRETS_ICON,
        WALLS_ICON,
        NUKE_ICON,
    }

    public GameIconsHostTrayEntity(
            GameTexturesManager textureManager,
            GameAreaTouchListenerEntity areaTouchListenerEntity,
            GameResources gameResources) {
        super(textureManager, areaTouchListenerEntity, gameResources);
    }

    @Override
    protected TraySpec getTraySpec() {
        return new TraySpec(
                hostActivity.getActivityContext(),
                8,
                ScreenUtils.getSreenSize().heightDp / 2,
                ScreenUtils.getSreenSize().widthDp,
                0.2f);
    }

    @Override
    protected BaseTrayOpenCloseButtonEntity getOpenCloseButtonEntity(GameResources gameResources) {
        return new GameIconsTrayOpenCloseButton(this, gameResources);
    }

    @Override
    protected BaseTrayIconsHolderEntity getTrayIconsHolderEntity(GameResources gameResources) {
        return new GameTrayIconsHolderEntity(this, gameResources);
    }
}
