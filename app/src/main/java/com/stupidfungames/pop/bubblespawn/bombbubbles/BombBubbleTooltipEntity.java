package com.stupidfungames.pop.bubblespawn.bombbubbles;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.tooltips.GameTooltipsEntity;
import com.stupidfungames.pop.tooltips.TooltipId;

public class BombBubbleTooltipEntity extends BaseEntity {

  public BombBubbleTooltipEntity(BinderEnity parent) {
    super(parent);
  }

  public void maybeShowBombBubbleTooltip() {
    get(GameTooltipsEntity.class).maybeShowTooltip(TooltipId.BOMB_BUBBLE_TOOLTIP);
  }
}
