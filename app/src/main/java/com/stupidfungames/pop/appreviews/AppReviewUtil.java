package com.stupidfungames.pop.appreviews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;

public class AppReviewUtil {

  // Number of times the app must be opened before we show the review prompt
  public static int SHOW_REVIEW_PROMPT_APP_OPENED_THRESHOLD = 3;

  public static String DONT_SHOW_AGAIN_PREF = "dont_show_again";

  public static void maybeShowAppReviewDialog(Context context) {
    if (shouldShowReviewDialog(context)) {
      context.startActivity(ReviewAppDialogActivity.newIntent(context));
    }
  }

  public static void userLeftReviewOrFeedback(Context context) {
    dontShowAgain(context);
  }

  public static void dontShowAgain(Context context) {
    GamePreferencesManager.set(context, DONT_SHOW_AGAIN_PREF, true);
  }

  public static void sendUserToPlayStore(Context context) {
    final String appPackageName = context.getPackageName();
    try {
      context.startActivity(
          new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
    } catch (android.content.ActivityNotFoundException anfe) {
      context.startActivity(new Intent(Intent.ACTION_VIEW,
          Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
    }
  }

  private static boolean shouldShowReviewDialog(Context context) {
    int numTimesAppOpened = AppOpenCounter.getNumTimesAppOpened(context);
    return numTimesAppOpened >= SHOW_REVIEW_PROMPT_APP_OPENED_THRESHOLD
        && !GamePreferencesManager.getBoolean(context, DONT_SHOW_AGAIN_PREF);
  }
}
