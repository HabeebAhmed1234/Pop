package com.stupidfungames.pop.notifications;

import static android.app.AlarmManager.INTERVAL_DAY;
import static com.stupidfungames.pop.analytics.Events.USER_NUDGE_NOTIF_FAILED;
import static com.stupidfungames.pop.analytics.Events.USER_NUDGE_NOTIF_SHOWN;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.stupidfungames.pop.MainMenuActivity;
import com.stupidfungames.pop.R;
import com.stupidfungames.pop.analytics.Logger;
import com.stupidfungames.pop.globalpoppedbubbles.GlobalPoppedBubbleManager;
import java.util.Random;

/**
 * Schedules NUM_NUDGES notifications to be delivered to the user after some interval in between
 * that tell the user to open the app.
 */
public class UserNudgeNotificationManager extends BroadcastReceiver {

  public static final String EXTRA_FROM_NOTIFICATION = "extra_from_notification";

  private static final String CHANNEL_ID = "pop_notifications_channel";
  private static final String EXTRA_NOTIFICATION_ID = "notification_id";
  private static final int STARTING_NOTIFICATION_ID = 1234;
  private static final int NUM_NUDGES = 3;
  private static final long INTERVAL_MILLIS = INTERVAL_DAY;
  private static final float GLOBAL_BUBBLES_POPPED_NOTIFICATION_PROBABILITY = 0.5f;

  public void scheduleNudgeNotifications(Context context) {
    createNotificationChannel(context);
    cancelPendingNotifications(context);
    int scheduledAlarms = 0;
    for (int i = STARTING_NOTIFICATION_ID; i < STARTING_NOTIFICATION_ID + NUM_NUDGES; i++) {
      Intent notificationIntent = new Intent(context, UserNudgeNotificationManager.class);
      notificationIntent.putExtra(EXTRA_NOTIFICATION_ID, i);
      PendingIntent contentIntent = PendingIntent
          .getBroadcast(context, i, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

      AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      am.cancel(contentIntent);
      long notificationTime = System.currentTimeMillis() + INTERVAL_MILLIS * (scheduledAlarms + 1);
      scheduledAlarms++;
      am.set(AlarmManager.RTC_WAKEUP, notificationTime, contentIntent);
    }
  }

  private void cancelPendingNotifications(Context context) {
    // Remove all existing notifications if any exist when the user launches the app
    NotificationManagerCompat.from(context).cancelAll();
  }

  private void createNotificationChannel(Context context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = context.getString(R.string.notification_channel_name);
      String description = context.getString(R.string.notification_channel_description);
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }

  @Override
  public void onReceive(final Context context, final Intent intent) {
    String notificationContent = getRandomNotificationString(context);
    if (!TextUtils.isEmpty(notificationContent)) {
      showNotification(context, intent, notificationContent);
    } else {
      Logger.logSelect(context, USER_NUDGE_NOTIF_FAILED);
    }
  }

  private void showNotification(Context context, Intent intent, String contentString) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(getNotificationIcon())
        .setContentTitle(context.getString(R.string.nudge_notification_title))
        .setContentText(contentString)
        .setContentIntent(getAppLaunchIntent(context))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true);

    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

    int notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0);
    notificationManager.notify(notificationId, builder.build());
    Logger.logSelect(context, USER_NUDGE_NOTIF_SHOWN);
  }

  private String getRandomNotificationString(final Context context) {
    Random random = new Random();
    if (random.nextFloat() < GLOBAL_BUBBLES_POPPED_NOTIFICATION_PROBABILITY) {
      @Nullable String globalPoppedNotif = getGlobalBubblesPoppedNotificationString(context);
      if (!TextUtils.isEmpty(globalPoppedNotif)) {
        return globalPoppedNotif;
      }
    }
    return getRandomFallbackNotif(context);
  }

  private String getRandomFallbackNotif(Context context) {
    Random random = new Random();
    @StringRes int stringRes = R.string.nudge_notification_content_1;
    switch (random.nextInt(5)) {
      case 0:
        stringRes = R.string.nudge_notification_content_1;
      case 1:
        stringRes = R.string.nudge_notification_content_2;
      case 2:
        stringRes = R.string.nudge_notification_content_3;
      case 3:
        stringRes = R.string.nudge_notification_content_4;
      case 4:
        stringRes = R.string.nudge_notification_content_5;
    }
    return context.getString(stringRes);
  }

  private String getGlobalBubblesPoppedNotificationString(final Context context) {
    long globalPoppedBubbles = GlobalPoppedBubbleManager.getInstance()
        .getGlobalBubblesPoppedCached(context);
    if (globalPoppedBubbles >= 0) {
      return context
          .getString(R.string.nudge_notification_content_global_bubbles, globalPoppedBubbles);
    }
    return null;
  }

  private int getNotificationIcon() {
    boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT
        >= android.os.Build.VERSION_CODES.LOLLIPOP);
    return useWhiteIcon ? R.drawable.ic_silhouette : R.drawable.ic_launcher;
  }

  private PendingIntent getAppLaunchIntent(Context context) {
    // Create an explicit intent for an Activity in your app
    Intent intent = new Intent(context, MainMenuActivity.class);
    intent.putExtra(EXTRA_FROM_NOTIFICATION, true);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    return PendingIntent.getActivity(context, 0, intent, 0);
  }
}
