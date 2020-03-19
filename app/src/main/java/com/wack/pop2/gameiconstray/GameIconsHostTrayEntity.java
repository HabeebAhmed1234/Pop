package com.wack.pop2.gameiconstray;

import com.wack.pop2.areatouch.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.resources.sounds.GameSoundsManager;
import com.wack.pop2.resources.sounds.SoundId;
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
            GameSoundsManager soundsManager,
            GameAreaTouchListenerEntity areaTouchListenerEntity,
            GameResources gameResources) {
        super(textureManager, soundsManager, areaTouchListenerEntity, gameResources);
    }

    @Override
    protected Spec getSpec() {
        return new Spec(
                hostActivity.getActivityContext(),
                4,
                ScreenUtils.getSreenSize().heightDp / 2,
                ScreenUtils.getSreenSize().widthDp,
                0.2f);
    }

    @Override
    protected boolean getIsInitiallyExpanded() {
        return true;
    }

    @Override
    protected BaseTrayOpenCloseButtonEntity getOpenCloseButtonEntity(GameResources gameResources) {
        return new GameIconsTrayOpenCloseButton(this, gameResources);
    }

    @Override
    protected BaseTrayIconsHolderEntity getTrayIconsHolderEntity(GameResources gameResources) {
        return new GameTrayIconsHolderEntity(this, gameResources);
    }

    @Override
    protected SoundId getOpenSound() {
        return SoundId.OPEN;
    }

    @Override
    protected SoundId getCloseSound() {
        return SoundId.CLOSE;
    }
}
