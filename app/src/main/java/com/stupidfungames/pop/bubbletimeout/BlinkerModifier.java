package com.stupidfungames.pop.bubbletimeout;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.BaseDurationModifier;

import javax.annotation.Nullable;
import javax.microedition.khronos.opengles.GL10;

/**
 * Splines between the given alpha values for the specified duration with an equal amount of time
 * between each transition.
 */
class BlinkerModifier extends BaseDurationModifier<IEntity> implements IEntityModifier {

    /**
     * Callback everytime we load a new segment. First callback is at 1 last callback is
     * at alphas.length
     */
    public interface NewSegmentCallback {
        void onNewSegment(int segment);
    }

    private float[] alphas;

    private float segmentLength = 0;
    private int nextAlphaIndex = 1;

    @Nullable private NewSegmentCallback newSegmentCallback;

    /**
     * @param alphas the alpha values to spline between
     * @param blinkingDuration the total duration we should be binking the entity for
     */
    public BlinkerModifier(float[] alphas, float blinkingDuration, NewSegmentCallback newSegmentCallback) {
        super(blinkingDuration);
        this.alphas = alphas;
        this.newSegmentCallback = newSegmentCallback;
    }

    public BlinkerModifier(BlinkerModifier other) {
        super(other.mDuration);
        this.alphas = other.alphas;
        this.newSegmentCallback = other.newSegmentCallback;
    }

    @Override
    public void reset() {
        super.reset();
        segmentLength = 0;
        nextAlphaIndex = 1;
    }

    @Override
    protected void onManagedUpdate(float secondsElapsed, IEntity item) {
        final float elapsed = getSecondsElapsed();

        float elapsedSinceLastAlpha = elapsed - (nextAlphaIndex - 1) * segmentLength;

        float percentDoneCurrentSegment = elapsedSinceLastAlpha / segmentLength;
        float lastAlpha = alphas[nextAlphaIndex - 1];
        float currentAlphaRange = nextAlphaIndex >= alphas.length ? 0 : alphas[nextAlphaIndex] - alphas[nextAlphaIndex - 1];

        float currentAlpha = lastAlpha + currentAlphaRange * percentDoneCurrentSegment;

        item.setAlpha(currentAlpha);
        if (elapsedSinceLastAlpha > segmentLength) {
            if (newSegmentCallback != null) {
                newSegmentCallback.onNewSegment(nextAlphaIndex);
            }
            nextAlphaIndex ++;
        }
    }

    @Override
    protected void onManagedInitialize(IEntity item) {
        Sprite itemSprite = (Sprite) item;
        itemSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        itemSprite.setAlpha(alphas[0]);
        segmentLength = getDuration() / alphas.length;
    }

    @Override
    protected void onModifierFinished(IEntity pItem) {
        super.onModifierFinished(pItem);
    }

    @Override
    public IEntityModifier deepCopy() throws DeepCopyNotSupportedException {
        return new BlinkerModifier(this);
    }
}
