package com.stupidfungames.pop.inapppurchase;

import android.content.Context;
import android.text.TextUtils;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.stupidfungames.pop.gamesettings.GamePreferencesManager;

/**
 * Loads, saves, and clears the ledger in shared preferences.
 */
class LocalLedgerManager {

  private static final String LOCAL_LEDGER_KEY = "local_ledger";

  public static void clear(Context context) {
    GamePreferencesManager.set(context, LOCAL_LEDGER_KEY, null);
  }

  public static ListenableFuture<Ledger> load(Context context) {
    String ledgerJson = GamePreferencesManager.getString(context, LOCAL_LEDGER_KEY);
    if (TextUtils.isEmpty(ledgerJson)) {
      return Futures.immediateFailedFuture(new IllegalStateException("No ledger stored in local storage"));
    }
    Ledger ledger = new Gson().fromJson(ledgerJson, Ledger.class);
    return Futures.immediateFuture(ledger);
  }

  public static void saveGame(Context context, Ledger ledger) {
    String json = new Gson().toJson(ledger);
    GamePreferencesManager.set(context, LOCAL_LEDGER_KEY, json);
  }
}
