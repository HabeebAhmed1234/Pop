package com.stupidfungames.pop.analytics;

public class Events {

  public static final String APP_START = "app_start";
  public static final String APP_START_FROM_NOTIFICATION = "app_start_from_notification";

  public static final String NEW_GAME_STARTED_MAIN_MENU = "new_game_started_main_menu";
  public static final String LOGIN_START = "login_start";
  public static final String LOGOUT = "logout";
  public static final String LOGIN_SUCCESS = "login_success";
  public static final String LOGIN_FAILED = "login_failed";
  public static final String ALREADY_LOGGED_IN = "already_logged_in";

  public static final String RESUME_GAME = "resume_game";
  public static final String RESUME_GAME_INCORRECT_VERSION_NUMBER = "resume_game_incorrect_version_number";

  public static final String OPEN_STORE = "open_store";
  public static final String OPEN_PURCHASES = "open_purchases";

  public static final String QUIT_BTN = "quit_btn";

  public static final String PURCHASE_START = "purchase_start";
  public static final String PURCHASE_CANCELED = "purchase_cancelled";
  public static final String PURCHASE_FAILED = "purchase_failed";

  public static final String CONTINUE_GAME_WITH_TOKEN = "continue_game_with_token";
  public static final String CONTINUE_GAME_WITH_AD = "continue_game_with_ad";
  public static final String AD_WATCH_START = "ad_watch_start";
  public static final String AD_WATCH_LOADED = "ad_watch_loaded";
  public static final String AD_WATCH_ERROR = "ad_watch_error";
  public static final String CONTINUE_GAME_WITH_SHARE = "continue_game_with_share";
  public static final String CONTINUE_BTN_PRESSED = "continue_btn_pressed";

  public static final String NEW_GAME_STARTED_GAME_OVER = "new_game_started_game_over";

  public static final String EQUIPPED_BG = "equipped_bg";
  public static final String ERROR_EQUIPPING_BG = "error_equipping_bg";

  public static final String SHARE_TO_FB_START = "share_to_fb_start";
  public static final String SHARE_TO_FB_SUCCESS = "share_to_fb_success";
  public static final String SHARE_TO_FB_CANCELLED = "share_to_fb_cancelled";
  public static final String SHARE_TO_FB_ERROR = "share_to_fb_error";
  public static final String SHARE_TO_OTHER = "share_to_other";

  public static final String LOAD_LIST_START = "load_list_start";
  public static final String LOAD_LIST_SUCCESS = "load_list_success";
  public static final String LOAD_LIST_EMPTY_RESULT = "load_list_empty_result";
  public static final String LOAD_LIST_ERROR = "load_list_error";

  public static final String GAME_OVER_SCORE = "game_over_score";
  public static final String GAME_OVER_BUBBLES_POPPED = "game_over_bubbles_popped";
  public static final String GAME_OVER_PROGRESS = "game_over_progress";

  public static final String GAME_STOP_SCORE = "game_stop_score";
  public static final String GAME_STOP_PROGRESS = "game_stop_progress";

  public static final String USER_NUDGE_NOTIF_SHOWN = "user_nudge_notif_shown";
  public static final String USER_NUDGE_NOTIF_CLICKED = "user_nudge_notif_clicked";
}
