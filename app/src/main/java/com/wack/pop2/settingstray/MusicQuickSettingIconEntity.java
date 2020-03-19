package com.wack.pop2.settingstray;

import com.wack.pop2.areatouch.GameAreaTouchListenerEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameSettingChangedEventPayload;
import com.wack.pop2.fixturedefdata.BaseEntityUserData;
import com.wack.pop2.fixturedefdata.MusicIconEntityUserData;
import com.wack.pop2.gamesettings.GamePreferencesEntity;
import com.wack.pop2.gamesettings.Setting;
import com.wack.pop2.resources.textures.GameTexturesManager;
import com.wack.pop2.resources.textures.TextureId;

import org.andengine.entity.scene.ITouchArea;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.color.AndengineColor;

import static com.wack.pop2.eventbus.GameEvent.SETTING_CHANGED;

public class MusicQuickSettingIconEntity extends BaseQuickSettingsIconEntity implements EventBus.Subscriber {

    GamePreferencesEntity preferencesEntity;

    public MusicQuickSettingIconEntity(
            GamePreferencesEntity preferencesEntity,
            GameQuickSettingsHostTrayEntity quickSettingsTrayEntity,
            GameTexturesManager gameTexturesManager,
            GameAreaTouchListenerEntity touchListenerEntity,
            GameResources gameResources) {
        super(quickSettingsTrayEntity, gameTexturesManager, touchListenerEntity, gameResources);
        this.preferencesEntity = preferencesEntity;
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
            if (settingChangedPayload.settingKey.equals(Setting.IS_MUSIC_ENABLED_SETTING_BOOLEAN)) {
                setIconColor(getSettingIconColor());
            }
        }
    }

    @Override
    protected TextureId getIconTextureId() {
        return TextureId.MUSIC_QUICK_SETTING_ICON;
    }

    @Override
    protected Class<? extends BaseEntityUserData> getIconUserDataType() {
        return MusicIconEntityUserData.class;
    }

    @Override
    protected BaseEntityUserData getUserData() {
        return new MusicIconEntityUserData();
    }

    @Override
    protected GameQuickSettingsHostTrayEntity.IconId getIconId() {
        return GameQuickSettingsHostTrayEntity.IconId.SETTING_MUSIC_TOGGLE;
    }

    @Override
    protected AndengineColor getInitialIconColor() {
        return getSettingIconColor();
    }

    @Override
    public boolean onTouch(TouchEvent touchEvent, ITouchArea pTouchArea, float pTouchAreaLocalX, float pTouchAreaLocalY) {
        if (touchEvent.isActionUp()) {
            toggleMusicSetting();
            return true;
        }
        return false;
    }

    private void toggleMusicSetting() {
        preferencesEntity.set(Setting.IS_MUSIC_ENABLED_SETTING_BOOLEAN, !preferencesEntity.getBoolean(Setting.IS_MUSIC_ENABLED_SETTING_BOOLEAN));
    }

    private AndengineColor getSettingIconColor() {
        return preferencesEntity.getBoolean(Setting.IS_MUSIC_ENABLED_SETTING_BOOLEAN) ? AndengineColor.GREEN : AndengineColor.RED;
    }
}
