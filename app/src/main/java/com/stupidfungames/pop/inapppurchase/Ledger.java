package com.stupidfungames.pop.inapppurchase;

import com.google.common.collect.ImmutableList;
import java.io.Serializable;

/**
 * Model used by {@link InGameCurrencyLedger} to keep track if the ledger.
 */
public class Ledger implements Serializable {

  public static class CurrencyBlock {
    public final boolean isConsumed;
    public final String orderId;
    public final int numCoinsRemaining;

    public CurrencyBlock(
        final boolean isConsumed,
        final String orderId,
        final int numCoinsRemaining) {
      this.isConsumed = isConsumed;
      this.orderId = orderId;
      this.numCoinsRemaining = numCoinsRemaining;
    }
  }

  public final ImmutableList<CurrencyBlock> currencyBlocks;

  public Ledger(ImmutableList<CurrencyBlock> currencyBlocks) {
    this.currencyBlocks = currencyBlocks;
  }
}
