package com.wack.pop2.bubbletimeout;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.BaseDurationModifier;

import javax.microedition.khronos.opengles.GL10;

import androidx.core.util.Preconditions;

/**
 * Blinks the given entity on and off (alpha 1 or 0) for a specified duration.
 */
class BlinkerModifier extends BaseDurationModifier<IEntity> implements IEntityModifier{

    private float blinkInterval;
    private float blinkSpan;

    private float lastOffEventTime = 0;

    /**
     * @param blinkInterval the ammount of time between the start of each blink
     * @param blinkSpan the duration of each blink
     * @param blinkingDuration the total duration we should be binking the entity for
     */
    public BlinkerModifier(float blinkInterval, float blinkSpan, float blinkingDuration) {
        super(blinkingDuration);
        Preconditions.checkArgument(blinkInterval > blinkSpan);
        this.blinkInterval = blinkInterval;
        this.blinkSpan = blinkSpan;
    }

    public BlinkerModifier(BlinkerModifier other) {
        super(other.mDuration);
        this.blinkInterval = other.blinkInterval;
        this.blinkSpan = other.blinkSpan;
    }

    @Override
    protected void onManagedUpdate(float secondsElapsed, IEntity item) {
        final float elapsed = getSecondsElapsed();
        float timeSinceLastOffEvent = elapsed - lastOffEventTime;
        if (timeSinceLastOffEvent > blinkSpan && item.getAlpha() == 0) {
            item.setAlpha(1);
        } else  if (timeSinceLastOffEvent > blinkInterval && item.getAlpha() == 1) {
            item.setAlpha(0);
            lastOffEventTime = elapsed;
        }
    }

    @Override
    protected void onManagedInitialize(IEntity item) {
        Sprite itemSprite = (Sprite) item;
        itemSprite.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        itemSprite.setAlpha(1);
    }

    @Override
    public IEntityModifier deepCopy() throws DeepCopyNotSupportedException {
        return new BlinkerModifier(this);
    }
}
