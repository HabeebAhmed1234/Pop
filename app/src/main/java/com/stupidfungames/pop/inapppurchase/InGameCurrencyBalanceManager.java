package com.stupidfungames.pop.inapppurchase;

import com.android.billingclient.api.SkuDetails;

/**
 * Loads and tracks the balance of in game currency. If no existing ledger is found in save game
 * data we load the ledger from google play services of unconsumed in app currency purchases.
 *
 * we then set those as "blocks" of currency in our ledger (save game data). We can then subtract
 * from them when told to do so. If a block is consumed we return that it was consumed. All of this
 * is updated in the ledger
 */
public class InGameCurrencyBalanceManager {

  private static InGameCurrencyBalanceManager sInstance;

  private InGameCurrencyBalanceManager() {
    init();
  }

  public static InGameCurrencyBalanceManager get() {
    if (sInstance == null) {
      sInstance = new InGameCurrencyBalanceManager();
    }
    return sInstance;
  }

  /**
   * Checks if a ledger already exists, else builds a ledger
   */
  private void init() {

  }

  /**
   * Builds ledger from google play services billing into save game data
   */
  private void buildLedger() {

  }

  /**
   * Returns the current balance of currency.
   * @return
   */
  public int getBalance() {
    return 0;
  }

  /**
   * Called when an in app purchase of in game currency is successful
   *
   * @param purchaseToken uniquely identifies the purchase
   * @param productId
   * @return true if adding of balance was successful
   */
  public boolean addBalance(String purchaseToken, String productId) {
    return true;
  }

  /**
   * Returns true if a block has been consumed.
   * @param numCoins
   * @return
   */
  public boolean subtractCurrency(int numCoins) {
    return false;
  }
}
