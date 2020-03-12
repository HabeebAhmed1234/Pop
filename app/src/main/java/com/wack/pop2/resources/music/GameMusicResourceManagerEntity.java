package com.wack.pop2.resources.music;

import android.content.Context;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.music.MusicManager;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameMusicResourceManagerEntity extends BaseEntity {

    private final Context context;
    private final MusicManager musicManager;
    private final Map<MusicId, Music> mMusic = new HashMap();

    public GameMusicResourceManagerEntity(Context context, MusicManager musicManager, GameResources gameResources) {
        super(gameResources);
        this.context = context;
        this.musicManager = musicManager;
    }

    @Override
    public void onCreateResources() {

        MusicFactory.setAssetBasePath("mfx/");
        try {
            mMusic.put(
                    MusicId.BACKGROUND_MUSIC_SOFT,
                    MusicFactory.createMusicFromAsset(musicManager, context, "bgm_soft.mp3"));
            mMusic.put(
                    MusicId.BACKGROUND_MUSIC_MEDIUM,
                    MusicFactory.createMusicFromAsset(musicManager, context, "bgm_medium.mp3"));
            mMusic.put(
                    MusicId.BACKGROUND_MUSIC_HARD,
                    MusicFactory.createMusicFromAsset(musicManager, context, "bgm_hard.mp3"));
        } catch (final IOException e) {
            Debug.e(e);
        }
    }

    public Music getMusic(MusicId musicId) {
        return mMusic.get(musicId);
    }
}
