package com.stupidfungames.pop.gamesettings;

import android.content.Context;
import android.content.SharedPreferences;

import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameSettingChangedEventPayload;

import static android.content.Context.MODE_PRIVATE;

public class GamePreferencesManager {

    private interface Mutation {
        void mutate(SharedPreferences.Editor editor);
    }

    private static final String SHARED_PREFERENCES_NAME = "POP_SHARED_PREFERENCES";

    public static void set(Context context, final String key, final String value) {
        runMutation(context, new Mutation() {
            @Override
            public void mutate(SharedPreferences.Editor editor) {
                editor.putString(key, value);
            }
        });
        notifyChanged(key);
    }

    public static void set(Context context, final String key, final boolean value) {
        runMutation(context, new Mutation() {
            @Override
            public void mutate(SharedPreferences.Editor editor) {
                editor.putBoolean(key, value);
            }
        });
        notifyChanged(key);
    }

    private static void notifyChanged(final String key) {
        EventBus.get().sendEvent(GameEvent.SETTING_CHANGED, new GameSettingChangedEventPayload(key));
    }

    public static String getString(Context context, final String key) {
        return getSharedPreferences(context).getString(key, "");
    }

    public static boolean getBoolean(Context context, final String key) {
        return getSharedPreferences(context).getBoolean(key, false);
    }

    private static void runMutation(Context context, Mutation mutation) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        mutation.mutate(editor);
        editor.apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }
}
