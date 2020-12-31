package com.stupidfungames.pop.tooltips;

import java.io.Serializable;

class GameTooltipActivityParams implements Serializable {

  public final TooltipId tooltipId;
  public final int anchorX;
  public final int anchorY;

  private GameTooltipActivityParams(
      TooltipId tooltipId,
      int anchorX,
      int anchorY) {
    this.tooltipId = tooltipId;
    this.anchorX = anchorX;
    this.anchorY = anchorY;
  }

  public static GameTooltipActivityParams forAnchoredTooltip(TooltipId tooltipId, int x, int y) {
    return new GameTooltipActivityParams(tooltipId, x, y);
  }
}
