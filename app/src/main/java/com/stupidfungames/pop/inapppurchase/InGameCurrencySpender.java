package com.stupidfungames.pop.inapppurchase;

/**
 * Anytime a purchase is made via the in app currency this will spend the in game currency by
 *
 * - subtracting it from the current balance
 * - checks to see if a one time purchase of in game currency unit has been consumed
 * - notifies google play services billing api that the unit has been consumed
 */
public class InGameCurrencySpender {


  /**
   * Spends the currency from {@link InGameCurrencyLedger} and notifies google play services
   * that a block is consumed if the balance manager tells us it is consumed
   * @param numCoins
   */
  public void spend(int numCoins) {

  }
}
