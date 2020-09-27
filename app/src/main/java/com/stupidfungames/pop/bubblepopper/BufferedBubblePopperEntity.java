package com.stupidfungames.pop.bubblepopper;

import com.stupidfungames.pop.BaseEntity;
import com.stupidfungames.pop.binder.Binder;
import com.stupidfungames.pop.binder.BinderEnity;
import com.stupidfungames.pop.bubblespawn.BubbleSpawnerEntity;
import com.stupidfungames.pop.physics.PhysicsWorld;

import org.andengine.entity.shape.IShape;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A wrapper around {@link BubblePopperEntity} which limits the number of bubbles popped per frame
 */
public class BufferedBubblePopperEntity extends BaseEntity implements PhysicsWorld.OnUpdateListener {

    private static final int MAX_BUBBLES_POPPED_PER_FRAME = 10;
    private Set<BubblePopData> bubblesToPop = new HashSet<>();
    private BubblePopperEntity bubblePopperEntity;

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

    public BufferedBubblePopperEntity(BinderEnity parent) {
        super(parent);
    }

    @Override
    protected void createBindings(Binder binder) {
        super.createBindings(binder);
        binder.bind(BubblePopperEntity.class, new BubblePopperEntity(this));
    }

    @Override
    public void onCreateScene() {
        super.onCreateScene();
        bubblePopperEntity = get(BubblePopperEntity.class);
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
                bubblePopperEntity.popBubble(data.previousBubble, data.oldBubbleSize, data.bubbleType);
            }
        }
    }

    public void popBubble(IShape previousBubble, BubbleSpawnerEntity.BubbleSize oldBubbleSize, BubbleSpawnerEntity.BubbleType bubbleType) {
        bubblesToPop.add(new BubblePopData(previousBubble, oldBubbleSize, bubbleType));
    }
}
