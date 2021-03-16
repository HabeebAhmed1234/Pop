package com.stupidfungames.pop.analytics;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Logger {

  private static final String VALUE_PREFIX = "value_";

  public static void logSelect(Context context, String event) {
    logSelect(context, event, "");
  }

  public static void logSelect(Context context, String event, Float... values) {
    String[] stringValues = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      stringValues[i] = Float.toString(values[i]);
    }
    logSelect(context, event, stringValues);

  }

  public static void logSelect(Context context, String event, Integer... values) {
    String[] stringValues = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      stringValues[i] = Integer.toString(values[i]);
    }
    logSelect(context, event, stringValues);
  }

  public static void logSelect(Context context, String event, Long... values) {
    String[] stringValues = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      stringValues[i] = Long.toString((values[i]));
    }
    logSelect(context, event, stringValues);
  }

  public static void logSelect(Context context, String event, String... values) {
    Bundle bundle = new Bundle();
    if (values != null) {
      for (int i = 0; i < values.length; i++) {
        if (!TextUtils.isEmpty(values[i])) {
          bundle.putString(VALUE_PREFIX + i, values[i]);
        }
      }
    }
    FirebaseAnalytics.getInstance(context).logEvent(event, bundle);
  }
}
