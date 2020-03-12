package com.wack.pop2.gamesettings;

import android.content.Context;
import android.content.SharedPreferences;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.GameResources;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameSettingChangedEventPayload;

import static android.content.Context.MODE_PRIVATE;

public class GamePreferencesEntity extends BaseEntity{

    private interface Mutation {
        void mutate(SharedPreferences.Editor editor);
    }

    private static final String SHARED_PREFERENCES_NAME = "POP_SHARED_PREFERENCES";

    private final Context context;

    public GamePreferencesEntity(Context context, GameResources gameResources) {
        super(gameResources);
        this.context =context;
    }

    public void set(final String key, final boolean value) {
        runMutation(new Mutation() {
            @Override
            public void mutate(SharedPreferences.Editor editor) {
                editor.putBoolean( key, value);
            }
        });
        EventBus.get().sendEvent(GameEvent.SETTING_CHANGED, new GameSettingChangedEventPayload(key));
    }

    public boolean getBoolean(final String key) {
        return getSharedPreferences().getBoolean(key, true);
    }

    private void runMutation(Mutation mutation) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        mutation.mutate(editor);
        editor.commit();
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }
}
