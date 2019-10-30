package com.wack.pop2;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.util.modifier.IModifier;

/**
 * Provides an API that can be used to apply {@link IEntityModifier} to
 * entities in the scene. Automatically cleans up the animation and optionally the entity after
 * the animation.
 */
public class GameAnimationManager extends BaseEntity {

    public interface AnimationListener {
        void onFinished();
    }

    public GameAnimationManager(GameResources gameResources) {
        super(gameResources);
    }

    public void startModifier(final IEntity entity, final IEntityModifier modifier) {
        startModifier(entity, modifier, new AnimationListener() {
            @Override
            public void onFinished() { /** NOOP **/ }});
    }

    public void startModifier(final IEntity entity, final IEntityModifier modifier, AnimationListener listener) {
        startModifier(entity, modifier, listener, true);
    }

    /**
     * Starts a modifier on the given entity.
     *
     * @param entity the entity that we want to apply the modifier to
     * @param modifier the modifier to apply
     * @param removeAfterFinished true if we want to remove the entity after the modifier has completed
     */
    public void startModifier(
            final IEntity entity,
            final IEntityModifier modifier,
            final AnimationListener animationListener,
            final boolean removeAfterFinished) {
        final SequenceEntityModifier sequenceEntityModifier = new SequenceEntityModifier(modifier);
        sequenceEntityModifier.addModifierListener(new IModifier.IModifierListener<IEntity>() {
            @Override
            public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {}
            @Override
            public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
                entity.unregisterEntityModifier(sequenceEntityModifier);
                if (removeAfterFinished) {
                    removeFromScene(entity);
                }
                animationListener.onFinished();
            }
        });
        entity.registerEntityModifier(sequenceEntityModifier);
    }
}
