package com.wack.pop2.backgroundmusic;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameSettingChangedEventPayload;
import com.wack.pop2.gamesettings.GamePreferences;
import com.wack.pop2.gamesettings.Setting;
import com.wack.pop2.resources.music.GameMusicResourceManagerEntity;
import com.wack.pop2.resources.music.MusicId;

import org.andengine.audio.music.Music;

public class BackgroundMusicEntity extends BaseEntity implements EventBus.Subscriber {

    private final GameMusicResourceManagerEntity musicManager;
    private final GamePreferences preferencesEntity;

    private Music currentMusic;

    public BackgroundMusicEntity(GameMusicResourceManagerEntity musicManager, GamePreferences preferencesEntity, GameResources gameResources) {
        super(gameResources);
        this.musicManager = musicManager;
        this.preferencesEntity = preferencesEntity;
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
        if (preferencesEntity.getBoolean(Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN)) {
            return;
        }
        stopCurrentMusic();
        currentMusic = musicManager.getMusic(soundId);
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
            boolean isMusicEnabled = !preferencesEntity.getBoolean(payload.settingKey);
            if (isMusicEnabled) {
                resume();
            } else {
                pause();
            }
        }
    }
}
