package org.andengine.util.color;

import org.andengine.util.adt.pool.GenericPool;

/**
 * (c) Zynga 2011
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 2:25:20 - 12.08.2011
 */
public class ColorPool extends GenericPool<AndengineColor> {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	protected AndengineColor onAllocatePoolItem() {
		return new AndengineColor(AndengineColor.WHITE);
	}

	@Override
	protected void onHandleRecycleItem(final AndengineColor pAndengineColor) {
		pAndengineColor.setChecking(AndengineColor.WHITE);

		super.onHandleRecycleItem(pAndengineColor);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
