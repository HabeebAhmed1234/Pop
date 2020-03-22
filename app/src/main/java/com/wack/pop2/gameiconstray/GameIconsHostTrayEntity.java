package com.wack.pop2.gameiconstray;

import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
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

    private final EventBus.Subscriber openGameIconsTrayEventSubscriber = new EventBus.Subscriber() {
        @Override
        public void onEvent(GameEvent event, EventPayload payload) {
            if (event == GameEvent.OPEN_GAME_ICONS_TRAY) {
                openTray();
            }
        }
    };

    public GameIconsHostTrayEntity(
            GameTexturesManager textureManager,
            GameSoundsManager soundsManager,
            GameResources gameResources) {
        super(textureManager, soundsManager, gameResources);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        EventBus.get().subscribe(GameEvent.OPEN_GAME_ICONS_TRAY, openGameIconsTrayEventSubscriber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.get().unSubscribe(GameEvent.OPEN_GAME_ICONS_TRAY, openGameIconsTrayEventSubscriber);
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
    protected boolean shouldExpandWhenIconAdded() {
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

    @Override
    protected GameEvent getTrayOpenEvent() {
        return GameEvent.GAME_ICONS_TRAY_OPENED;
    }

    @Override
    protected GameEvent getTrayCloseEvent() {
        return GameEvent.GAME_ICONS_TRAY_CLOSED;
    }
}
