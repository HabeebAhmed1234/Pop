package com.wack.pop2.settingstray;

import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameSettingChangedEventPayload;
import com.wack.pop2.gamesettings.GamePreferencesEntity;
import com.wack.pop2.gamesettings.Setting;
import com.wack.pop2.resources.textures.TextureId;
import com.wack.pop2.touchlisteners.ButtonUpTouchListener;

import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.eventbus.GameEvent.SETTING_CHANGED;

public class MusicQuickSettingIconBaseEntity extends QuickSettingsIconBaseEntity implements EventBus.Subscriber {

    private final ButtonUpTouchListener touchListener = new ButtonUpTouchListener() {
        @Override
        protected boolean onButtonPressed(TouchEvent pSceneTouchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
            toggleMusicSetting();
            return true;
        }
    };

    public MusicQuickSettingIconBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {

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
        GamePreferencesEntity preferencesEntity = get(GamePreferencesEntity.class);
        preferencesEntity.set(Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN, !preferencesEntity.getBoolean(Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN));
    }

    private AndengineColor getSettingIconColor() {
        return get(GamePreferencesEntity.class).getBoolean(Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN) ? AndengineColor.RED : AndengineColor.GREEN;
    }
}
