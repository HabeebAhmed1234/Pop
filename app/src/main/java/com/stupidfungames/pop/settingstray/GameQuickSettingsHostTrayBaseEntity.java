package com.stupidfungames.pop.settingstray;

import android.content.Context;

import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.resources.sounds.SoundId;
import com.stupidfungames.pop.tray.HostTrayBaseEntity;
import com.stupidfungames.pop.tray.TrayIconsHolderBaseEntity;
import com.stupidfungames.pop.tray.TrayOpenCloseButtonBaseEntity;
import com.stupidfungames.pop.utils.ScreenUtils;

/**
 * A small expandable tray that renders on the top right of the screen.
 */
public class GameQuickSettingsHostTrayBaseEntity extends HostTrayBaseEntity<GameQuickSettingsHostTrayBaseEntity.IconId> {

    public enum IconId {
        SETTING_MUSIC_TOGGLE,
        GAME_PAUSE_BUTTON,
        GAME_SAVE_BUTTON,
    }

    public GameQuickSettingsHostTrayBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected Spec getSpec() {
        return new Spec(
                get(Context.class),
                4,
                44,
                ScreenUtils.getSreenSize().widthDp,
                0.2f);
    }

    @Override
    protected boolean shouldExpandWhenIconAdded() {
        return false;
    }

    @Override
    protected TrayOpenCloseButtonBaseEntity getOpenCloseButtonEntity(BinderEnity parent) {
        return new GameQuickSettingsTrayOpenCloseButton(this, parent);
    }

    @Override
    protected TrayIconsHolderBaseEntity getTrayIconsHolderEntity(BinderEnity parent) {
        return new GameQuickSettingsTrayIconsHolderBaseEntity(this, parent);
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
        return GameEvent.QUICK_SETTINGS_TRAY_OPEN;
    }

    @Override
    protected GameEvent getTrayClosedEvent() {
        return GameEvent.QUICK_SETTINGS_TRAY_CLOSE;
    }
}
