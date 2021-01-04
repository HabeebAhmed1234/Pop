package com.stupidfungames.pop.backgroundmusic;

import android.content.Context;
import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.androidui.music.MusicPlayer;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameSettingChangedEventPayload;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;
import com.stupidfungames.pop.gamesettings.Setting;

public class BackgroundMusicEntity extends BaseEntity implements EventBus.Subscriber {

  private MusicPlayer musicPlayer;

  public BackgroundMusicEntity(BinderEnity parent) {
    super(parent);
    musicPlayer = get(MusicPlayer.class);
    if (GamePreferencesManager.getBoolean(
        get(Context.class),
        Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN)) {
      musicPlayer.pausePlaying();
    }
  }

  @Override
  public void onCreateScene() {
    EventBus.get()
        .subscribe(GameEvent.SETTING_CHANGED, this);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.get().unSubscribe(GameEvent.SETTING_CHANGED, this);
  }

  @Override
  public void onEvent(GameEvent event, EventPayload payload) {
    switch (event) {
      case SETTING_CHANGED:
        onSettingChanged((GameSettingChangedEventPayload) payload);
        break;
    }
  }

  public void playNextSong() {
    musicPlayer.playNextTrack();
  }

  private void onSettingChanged(GameSettingChangedEventPayload payload) {
    String key = payload.settingKey;
    if (key.equals(Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN)) {
      boolean isMusicEnabled = !GamePreferencesManager
          .getBoolean(get(Context.class), payload.settingKey);
      if (isMusicEnabled) {
        musicPlayer.resumePlaying();
      } else {
        musicPlayer.pausePlaying();
      }
    }
  }
}
