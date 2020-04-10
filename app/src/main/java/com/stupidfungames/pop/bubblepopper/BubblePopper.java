package com.stupidfungames.pop.bubblepopper;

import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity;

import org.andengine.entity.shape.IShape;

public interface BubblePopper {

    void popBubble(IShape previousBubble, BubbleSpawnerEntity.BubbleSize oldBubbleSize, BubbleSpawnerEntity.BubbleType bubbleType);
}
