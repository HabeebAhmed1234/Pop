package com.stupidfungames.pop.backgroundmusic;

import android.content.Context;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.EventPayload;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameProgressEventPayload;
import com.stupidfungames.pop.eventbus.GameSettingChangedEventPayload;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;
import com.stupidfungames.pop.gamesettings.Setting;
import com.stupidfungames.pop.resources.music.GameMusicResourceManagerBaseEntity;
import com.stupidfungames.pop.resources.music.MusicId;

import org.andengine.audio.music.Music;

public class BackgroundMusicBaseEntity extends BaseEntity implements EventBus.Subscriber {

    private static final float EASY_MUSIC_PROGRESS_THRESHOLD = 0;
    private static final float MEDIUM_MUSIC_PROGRESS_THRESHOLD = 0.33f;
    private static final float HARD_MUSIC_PROGRESS_THRESHOLD = 0.66f;

    private MusicId currentMusicId;
    private Music currentMusic;

    public BackgroundMusicBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        EventBus.get()
                .subscribe(GameEvent.GAME_PROGRESS_CHANGED, this)
                .subscribe(GameEvent.SETTING_CHANGED, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onEvent(GameEvent event, EventPayload payload) {
        switch (event) {
            case GAME_PROGRESS_CHANGED:
                onGameProgressChanged((GameProgressEventPayload) payload);
                break;
            case SETTING_CHANGED:
                onSettingChanged((GameSettingChangedEventPayload) payload);
                break;
        }
    }

    private void onGameProgressChanged(GameProgressEventPayload payload) {
        float percentProgress = payload.percentProgress;
        if (percentProgress > HARD_MUSIC_PROGRESS_THRESHOLD) {
            playMusic(MusicId.BACKGROUND_MUSIC_HARD);
        } else if (percentProgress > MEDIUM_MUSIC_PROGRESS_THRESHOLD) {
            playMusic(MusicId.BACKGROUND_MUSIC_MEDIUM);
        } else if (percentProgress > EASY_MUSIC_PROGRESS_THRESHOLD) {
            playMusic(MusicId.BACKGROUND_MUSIC_SOFT);
        }
    }

    private void stopCurrentMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    private void playMusic(MusicId soundId) {
        if (currentMusic != null && currentMusicId == soundId) {
            // If we are trying to play the same music again don't do anything
            return;
        }
        if (GamePreferencesManager.getBoolean(get(Context.class), Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN)) {
            return;
        }
        stopCurrentMusic();
        currentMusicId = soundId;
        currentMusic = get(GameMusicResourceManagerBaseEntity.class).getMusic(soundId);
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    private void resume() {
        if (currentMusic != null) {
            currentMusic.resume();
        }
    }

    private void pause() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    private void onSettingChanged(GameSettingChangedEventPayload payload) {
        String key = payload.settingKey;
        if (key.equals(Setting.IS_MUSIC_DISABLED_SETTING_BOOLEAN)) {
            boolean isMusicEnabled = !GamePreferencesManager.getBoolean(get(Context.class), payload.settingKey);
            if (isMusicEnabled) {
                resume();
            } else {
                pause();
            }
        }
    }
}
