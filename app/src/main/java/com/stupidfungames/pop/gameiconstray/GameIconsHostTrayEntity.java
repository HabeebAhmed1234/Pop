package com.stupidfungames.pop.gameiconstray;

import android.content.Context;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.tray.HostTrayBaseEntity;
import com.stupidfungames.pop.tray.TrayIconsHolderBaseEntity;
import com.stupidfungames.pop.tray.TrayOpenCloseButtonBaseEntity;
import com.stupidfungames.pop.utils.ScreenUtils;

/**
 * Single entity used to manage the icons for different tools in the game.
 */
public class GameIconsHostTrayEntity extends HostTrayBaseEntity<GameIconsHostTrayEntity.IconId> {

    public enum IconId {
        BALL_AND_CHAIN_ICON,
        TURRETS_ICON,
        WALLS_ICON,
        NUKE_ICON,
        MULTI_POP_ICON,
    }

    private final EventBus.Subscriber openGameIconsTrayEventSubscriber = new EventBus.Subscriber() {
        @Override
        public void onEvent(GameEvent event, EventPayload payload) {
            if (event == GameEvent.OPEN_GAME_ICONS_TRAY) {
                openTray();
            }
        }
    };

    public GameIconsHostTrayEntity(BinderEnity parent) {
        super(parent);
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
                get(Context.class),
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
    protected TrayOpenCloseButtonBaseEntity getOpenCloseButtonEntity(BinderEnity parent) {
        return null;
    }

    @Override
    protected TrayIconsHolderBaseEntity getTrayIconsHolderEntity(BinderEnity parent) {
        return new GameTrayIconsHolderBaseEntity(this, parent);
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
    protected GameEvent getTrayOpenedEvent() {
        return GameEvent.GAME_ICONS_TRAY_OPENED;
    }

    @Override
    protected GameEvent getTrayClosedEvent() {
        return GameEvent.GAME_ICONS_TRAY_CLOSED;
    }
}
