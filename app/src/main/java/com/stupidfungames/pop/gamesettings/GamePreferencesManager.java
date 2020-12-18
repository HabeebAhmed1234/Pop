package com.stupidfungames.pop.gamesettings;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.stupidfungames.pop.eventbus.EventBus;
import com.stupidfungames.pop.eventbus.GameEvent;
import com.stupidfungames.pop.eventbus.GameSettingChangedEventPayload;

public class GamePreferencesManager {

  private interface Mutation {

    void mutate(SharedPreferences.Editor editor);
  }

  private static final String SHARED_PREFERENCES_NAME = "POP_SHARED_PREFERENCES";

  public static void clear(Context context, final String key) {
    runMutation(context, new Mutation() {
      @Override
      public void mutate(SharedPreferences.Editor editor) {
        editor.remove(key);
      }
    });
    notifyChanged(key);
  }

  public static void set(Context context, final String key, final int value) {
    runMutation(context, new Mutation() {
      @Override
      public void mutate(SharedPreferences.Editor editor) {
        editor.putInt(key, value);
      }
    });
    notifyChanged(key);

  }

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

  public static void remove(Context context, final String key) {
    runMutation(context, new Mutation() {
      @Override
      public void mutate(Editor editor) {
        editor.remove(key);
      }
    });
  }

  private static void notifyChanged(final String key) {
    EventBus.get().sendEvent(GameEvent.SETTING_CHANGED, new GameSettingChangedEventPayload(key));
  }

  public static String getString(Context context, final String key) {
    return getSharedPreferences(context).getString(key, "");
  }

  public static boolean getBoolean(Context context, final String key) {
    return getBoolean(context, key, false);
  }

  public static boolean getBoolean(Context context, final String key, boolean defaultValue) {
    return getSharedPreferences(context).getBoolean(key, defaultValue);
  }

  public static int getInt(Context context, final String key) {
    return getSharedPreferences(context).getInt(key, -1);
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
