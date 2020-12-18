package com.stupidfungames.pop.settingstray;

import android.content.Context;

import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameSettingChangedEventPayload;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;
import com.stupidfungames.pop.gamesettings.Setting;
import com.stupidfungames.pop.resources.textures.TextureId;
import com.stupidfungames.pop.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.stupidfungames.pop.eventbus.GameEvent.SETTING_CHANGED;

public class MusicQuickSettingIconEntity extends QuickSettingsIconBaseEntity implements EventBus.Subscriber {

    private final ButtonUpTouchListener touchListener = new ButtonUpTouchListener() {
        @Override
        protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
            toggleMusicSetting();
            return true;
        }
    };

    public MusicQuickSettingIconEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        EventBus.get().subscribe(SETTING_CHANGED, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.get().unSubscribe(SETTING_CHANGED, this);
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        if (event == SETTING_CHANGED) {
            GameSettingChangedEventPayload settingChangedPayload = (GameSettingChangedEventPayload) payload;
            if (settingChangedPayload.settingKey.equals(Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN)) {
                setIconColor(getSettingIconColor());
            }
        }
    }

    @Override
    protected TextureId getIconTextureId() {
        return TextureId.MUSIC_QUICK_SETTING_ICON;
    }

    @Override
    protected GameQuickSettingsHostTrayBaseEntity.IconId getIconId() {
        return GameQuickSettingsHostTrayBaseEntity.IconId.SETTING_MUSIC_TOGGLE;
    }

    @Override
    protected AndengineColor getInitialIconColor() {
        return getSettingIconColor();
    }

    @Override
    protected IOnAreaTouchListener getTouchListener() {
        return touchListener;
    }

    private void toggleMusicSetting() {
        GamePreferencesManager.set(
                get(Context.class),
                Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN,
                !GamePreferencesManager.getBoolean(
                        get(Context.class),
                        Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN));
    }

    private AndengineColor getSettingIconColor() {
        return GamePreferencesManager.getBoolean(
                get(Context.class),
                Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN) ?
                AndengineColor.RED : AndengineColor.GREEN;
    }
}
