package org.andengine.entity.scene.menu.item.decorator;

import org.andengine.entity.OnDetachedListener;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierMatcher;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.util.modifier.IModifier;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 15:04:29 - 18.11.2010
 */
public class ScaleMenuItemDecorator extends BaseMenuItemDecorator {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final float mSelectedScale;
	private final float mUnselectedScale;

	// ===========================================================
	// Constructors
	// ===========================================================

	public ScaleMenuItemDecorator(final IMenuItem pMenuItem, final float pSelectedScale, final float pUnselectedScale) {
		super(pMenuItem);

		this.mSelectedScale = pSelectedScale;
		this.mUnselectedScale = pUnselectedScale;

		pMenuItem.setScale(pUnselectedScale);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onMenuItemSelected(final IMenuItem pMenuItem) {
		this.setScale(this.mSelectedScale);
	}

	@Override
	public void onMenuItemUnselected(final IMenuItem pMenuItem) {
		this.setScale(this.mUnselectedScale);
	}

	@Override
	public void onMenuItemReset(final IMenuItem pMenuItem) {
		this.setScale(this.mUnselectedScale);
	}

	@Override
	public boolean isTouchEnabled() {
		return true;
	}

	@Override
	public void setTouchEnabled(boolean pTouchEnabled) {

	}

	@Override
	public boolean isAttached() {
		return false;
	}

	@Override
	public void addOnDetachedListener(OnDetachedListener listener) {

	}

	@Override
	public void removeOnDetachedListener(OnDetachedListener listener) {

	}

	@Override
	public void clearOnDetachedListeners() {

	}

	@Override
	public IModifier getEntityModifier(IEntityModifierMatcher pEntityModifierMatcher) {
		return null;
	}

	@Override
	public void setOnAreaTouchListener(IOnAreaTouchListener listener) {

	}

	@Override
	public void removeOnAreaTouchListener() {

	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
