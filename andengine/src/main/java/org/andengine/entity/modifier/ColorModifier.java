package org.andengine.entity.modifier;

import org.andengine.entity.IEntity;
import org.andengine.util.color.AndengineColor;
import org.andengine.util.modifier.ease.EaseLinear;
import org.andengine.util.modifier.ease.IEaseFunction;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 15:39:50 - 29.06.2010
 */
public class ColorModifier extends TripleValueSpanEntityModifier {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	public ColorModifier(final float pDuration, final AndengineColor pFromAndengineColor, final AndengineColor pToAndengineColor) {
		this(pDuration, pFromAndengineColor.getRed(), pToAndengineColor.getRed(), pFromAndengineColor.getGreen(), pToAndengineColor.getGreen(), pFromAndengineColor.getBlue(), pToAndengineColor.getBlue(), null, EaseLinear.getInstance());
	}

	public ColorModifier(final float pDuration, final float pFromRed, final float pToRed, final float pFromGreen, final float pToGreen, final float pFromBlue, final float pToBlue) {
		this(pDuration, pFromRed, pToRed, pFromGreen, pToGreen, pFromBlue, pToBlue, null, EaseLinear.getInstance());
	}

	public ColorModifier(final float pDuration, final AndengineColor pFromAndengineColor, final AndengineColor pToAndengineColor, final IEaseFunction pEaseFunction) {
		this(pDuration, pFromAndengineColor.getRed(), pToAndengineColor.getRed(), pFromAndengineColor.getGreen(), pToAndengineColor.getGreen(), pFromAndengineColor.getBlue(), pToAndengineColor.getBlue(), null, pEaseFunction);
	}

	public ColorModifier(final float pDuration, final float pFromRed, final float pToRed, final float pFromGreen, final float pToGreen, final float pFromBlue, final float pToBlue, final IEaseFunction pEaseFunction) {
		this(pDuration, pFromRed, pToRed, pFromGreen, pToGreen, pFromBlue, pToBlue, null, pEaseFunction);
	}

	public ColorModifier(final float pDuration, final AndengineColor pFromAndengineColor, final AndengineColor pToAndengineColor, final IEntityModifierListener pEntityModifierListener) {
		super(pDuration, pFromAndengineColor.getRed(), pToAndengineColor.getRed(), pFromAndengineColor.getGreen(), pToAndengineColor.getGreen(), pFromAndengineColor.getBlue(), pToAndengineColor.getBlue(), pEntityModifierListener, EaseLinear.getInstance());
	}

	public ColorModifier(final float pDuration, final float pFromRed, final float pToRed, final float pFromGreen, final float pToGreen, final float pFromBlue, final float pToBlue, final IEntityModifierListener pEntityModifierListener) {
		super(pDuration, pFromRed, pToRed, pFromGreen, pToGreen, pFromBlue, pToBlue, pEntityModifierListener, EaseLinear.getInstance());
	}

	public ColorModifier(final float pDuration, final AndengineColor pFromAndengineColor, final AndengineColor pToAndengineColor, final IEntityModifierListener pEntityModifierListener, final IEaseFunction pEaseFunction) {
		super(pDuration, pFromAndengineColor.getRed(), pToAndengineColor.getRed(), pFromAndengineColor.getGreen(), pToAndengineColor.getGreen(), pFromAndengineColor.getBlue(), pToAndengineColor.getBlue(), pEntityModifierListener, pEaseFunction);
	}

	public ColorModifier(final float pDuration, final float pFromRed, final float pToRed, final float pFromGreen, final float pToGreen, final float pFromBlue, final float pToBlue, final IEntityModifierListener pEntityModifierListener, final IEaseFunction pEaseFunction) {
		super(pDuration, pFromRed, pToRed, pFromGreen, pToGreen, pFromBlue, pToBlue, pEntityModifierListener, pEaseFunction);
	}

	protected ColorModifier(final ColorModifier pColorModifier) {
		super(pColorModifier);
	}

	@Override
	public ColorModifier deepCopy(){
		return new ColorModifier(this);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected void onSetInitialValues(final IEntity pEntity, final float pRed, final float pGreen, final float pBlue) {
		pEntity.setColor(pRed, pGreen, pBlue);
	}

	@Override
	protected void onSetValues(final IEntity pEntity, final float pPerctentageDone, final float pRed, final float pGreen, final float pBlue) {
		pEntity.setColor(pRed, pGreen, pBlue);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
