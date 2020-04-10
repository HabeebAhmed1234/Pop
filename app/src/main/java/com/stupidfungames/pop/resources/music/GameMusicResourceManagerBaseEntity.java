package com.stupidfungames.pop.resources.music;

import android.content.Context;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.music.MusicManager;
import org.andengine.util.debug.Debug;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameMusicResourceManagerBaseEntity extends BaseEntity {

    private final Map<MusicId, Music> mMusic = new HashMap();

    public GameMusicResourceManagerBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateResources() {
        MusicManager musicManager = get(MusicManager.class);
        Context context = get(Context.class);

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
