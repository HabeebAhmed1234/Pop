package com.wack.pop2.backgroundmusic;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameSettingChangedEventPayload;
import com.wack.pop2.gamesettings.GamePreferencesEntity;
import com.wack.pop2.gamesettings.Setting;
import com.wack.pop2.resources.music.GameMusicResourceManagerBaseEntity;
import com.wack.pop2.resources.music.MusicId;

import org.andengine.audio.music.Music;

public class BackgroundMusicBaseEntity extends BaseEntity implements EventBus.Subscriber {

    private Music currentMusic;

    public BackgroundMusicBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        EventBus.get()
                .subscribe(GameEvent.PLAY_SOFT_MUSIC, this)
                .subscribe(GameEvent.PLAY_MEDIUM_MUSIC, this)
                .subscribe(GameEvent.PLAY_HARD_MUSIC, this)
                .subscribe(GameEvent.SETTING_CHANGED, this);
        playStartingMusic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        switch (event) {
            case PLAY_SOFT_MUSIC:
                playMusic(MusicId.BACKGROUND_MUSIC_SOFT);
                break;
            case PLAY_MEDIUM_MUSIC:
                playMusic(MusicId.BACKGROUND_MUSIC_MEDIUM);
                break;
            case PLAY_HARD_MUSIC:
                playMusic(MusicId.BACKGROUND_MUSIC_HARD);
                break;
            case SETTING_CHANGED:
                onSettingChanged((GameSettingChangedEventPayload) payload);
                break;
        }
    }

    private void stopCurrentMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    private void playMusic(MusicId soundId) {
        if (get(GamePreferencesEntity.class).getBoolean(Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN)) {
            return;
        }
        stopCurrentMusic();
        currentMusic = get(GameMusicResourceManagerBaseEntity.class).getMusic(soundId);
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    private void resume() {
        if (currentMusic == null) {
            playStartingMusic();
        } else {
            currentMusic.resume();
        }
    }

    private void pause() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    private void playStartingMusic() {
        playMusic(MusicId.BACKGROUND_MUSIC_SOFT);
    }

    private void onSettingChanged(GameSettingChangedEventPayload payload) {
        String key = payload.settingKey;
        if (key.equals(Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN)) {
            boolean isMusicEnabled = !get(GamePreferencesEntity.class).getBoolean(payload.settingKey);
            if (isMusicEnabled) {
                resume();
            } else {
                pause();
            }
        }
    }
}
