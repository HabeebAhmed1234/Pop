package com.wack.pop2.bubblepopper;

import com.wack.pop2.bubblespawn.BubbleSpawnerEntity;

import org.andengine.entity.shape.IShape;

public interface BubblePopper {

    void popBubble(IShape previousBubble, BubbleSpawnerEntity.BubbleSize oldBubbleSize, BubbleSpawnerEntity.BubbleType bubbleType);
}
