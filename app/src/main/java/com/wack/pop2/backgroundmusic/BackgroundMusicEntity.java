package com.wack.pop2.backgroundmusic;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.EventPayload;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.resources.music.GameMusicManagerEntity;
import com.wack.pop2.resources.music.MusicId;

import org.andengine.audio.music.Music;

public class BackgroundMusicEntity extends BaseEntity implements EventBus.Subscriber {

    private final GameMusicManagerEntity musicManager;

    private Music currentMusic;

    public BackgroundMusicEntity(GameMusicManagerEntity musicManager, GameResources gameResources) {
        super(gameResources);
        this.musicManager = musicManager;
    }

    @Override
    public void onCreateScene() {
        EventBus.get()
                .subscribe(GameEvent.PLAY_SOFT_MUSIC, this)
                .subscribe(GameEvent.PLAY_MEDIUM_MUSIC, this)
                .subscribe(GameEvent.PLAY_HARD_MUSIC, this);
        playStartignMusic();
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
        }
    }

    private void stopCurrentMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    private void playMusic(MusicId soundId) {
        stopCurrentMusic();
        currentMusic = musicManager.getMusic(soundId);
        if (currentMusic != null) {
            currentMusic.play();
        }
    }

    private void playStartignMusic() {
        playMusic(MusicId.BACKGROUND_MUSIC_SOFT);
    }
}
