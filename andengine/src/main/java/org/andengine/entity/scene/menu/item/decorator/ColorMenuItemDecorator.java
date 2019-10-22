package org.andengine.entity.scene.menu.item.decorator;

import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.util.color.AndengineColor;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 14:25:35 - 07.07.2010
 */
public class ColorMenuItemDecorator extends BaseMenuItemDecorator {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	private final AndengineColor mSelectedAndengineColor;
	private final AndengineColor mUnselectedAndengineColor;

	// ===========================================================
	// Constructors
	// ===========================================================

	public ColorMenuItemDecorator(final IMenuItem pMenuItem, final AndengineColor pSelectedAndengineColor, final AndengineColor pUnselectedAndengineColor) {
		super(pMenuItem);

		this.mSelectedAndengineColor = pSelectedAndengineColor;
		this.mUnselectedAndengineColor = pUnselectedAndengineColor;

		pMenuItem.setColor(pUnselectedAndengineColor);
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
		pMenuItem.setColor(this.mSelectedAndengineColor);
	}

	@Override
	public void onMenuItemUnselected(final IMenuItem pMenuItem) {
		pMenuItem.setColor(this.mUnselectedAndengineColor);
	}

	@Override
	public void onMenuItemReset(final IMenuItem pMenuItem) {
		pMenuItem.setColor(this.mUnselectedAndengineColor);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
