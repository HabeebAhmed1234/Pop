package com.wack.pop2.bubblepopper;

import com.wack.pop2.BaseEntity;
import com.wack.pop2.binder.Binder;
import com.wack.pop2.binder.BinderEnity;
import com.wack.pop2.bubblespawn.BubbleSpawnerEntity;
import com.wack.pop2.physics.PhysicsWorld;

import org.andengine.entity.shape.IShape;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A wrapper around {@link BubblePopperEntity} which limits the number of bubbles popped per frame
 */
public class BufferedBubblePopperBaseEntity extends BaseEntity implements PhysicsWorld.OnUpdateListener, BubblePopper {

    private static final int MAX_BUBBLES_POPPED_PER_FRAME = 1;
    private Set<BubblePopData> bubblesToPop = new HashSet<>();

    private static class BubblePopData {
        public final IShape previousBubble;
        public final BubbleSpawnerEntity.BubbleSize oldBubbleSize;
        public final BubbleSpawnerEntity.BubbleType bubbleType;

        BubblePopData(IShape previousBubble,
                      BubbleSpawnerEntity.BubbleSize oldBubbleSize,
                      BubbleSpawnerEntity.BubbleType bubbleType) {
            this.previousBubble = previousBubble;
            this.oldBubbleSize = oldBubbleSize;
            this.bubbleType = bubbleType;
        }
    }

    public BufferedBubblePopperBaseEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        physicsWorld.addOnUpdateListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        physicsWorld.removeOnUpdateListener(this);
    }

    @Override
    public void onUpdateCompleted() {
        if (!bubblesToPop.isEmpty()) {
            Iterator<BubblePopData> dataIterator = bubblesToPop.iterator();
            int numBubblesToPop = MAX_BUBBLES_POPPED_PER_FRAME;

            while (numBubblesToPop > 0 && dataIterator.hasNext()) {
                numBubblesToPop--;
                BubblePopData data = dataIterator.next();
                dataIterator.remove();
                get(BubblePopperEntity.class).popBubble(data.previousBubble, data.oldBubbleSize, data.bubbleType);
            }
        }
    }

    @Override
    public void popBubble(IShape previousBubble, BubbleSpawnerEntity.BubbleSize oldBubbleSize, BubbleSpawnerEntity.BubbleType bubbleType) {
        bubblesToPop.add(new BubblePopData(previousBubble, oldBubbleSize, bubbleType));
    }
}
