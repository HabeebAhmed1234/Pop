package com.wack.pop2.gamesettings;

import android.content.Context;
import android.content.SharedPreferences;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.eventbus.EventBus;
import com.wack.pop2.eventbus.GameEvent;
import com.wack.pop2.eventbus.GameSettingChangedEventPayload;

import static android.content.Context.MODE_PRIVATE;

public class GamePreferencesEntity extends BaseEntity {

    private interface Mutation {
        void mutate(SharedPreferences.Editor editor);
    }

    private static final String SHARED_PREFERENCES_NAME = "POP_SHARED_PREFERENCES";

    public GamePreferencesEntity(BinderEnity parent) {
        super(parent);
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
        return getSharedPreferences().getBoolean(key, false);
    }

    private void runMutation(Mutation mutation) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        mutation.mutate(editor);
        editor.apply();
    }

    private SharedPreferences getSharedPreferences() {
        return get(Context.class).getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }
}
