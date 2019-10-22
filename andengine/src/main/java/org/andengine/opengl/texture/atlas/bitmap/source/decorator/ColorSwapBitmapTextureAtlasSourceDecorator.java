package org.andengine.opengl.texture.atlas.bitmap.source.decorator;

import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.bitmap.source.decorator.shape.IBitmapTextureAtlasSourceDecoratorShape;
import org.andengine.util.color.AndengineColor;

import android.graphics.AvoidXfermode;
import android.graphics.AvoidXfermode.Mode;

/**
 * (c) 2010 Nicolas Gramlich 
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 19:41:39 - 07.06.2011
 */
public class ColorSwapBitmapTextureAtlasSourceDecorator extends BaseShapeBitmapTextureAtlasSourceDecorator {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int TOLERANCE_DEFAULT = 0;

	// ===========================================================
	// Fields
	// ===========================================================

	protected final int mColorKeyColorARGBPackedInt;
	protected final int mTolerance;
	protected final int mColorSwapColorARGBPackedInt;

	// ===========================================================
	// Constructors
	// ===========================================================

	public ColorSwapBitmapTextureAtlasSourceDecorator(final IBitmapTextureAtlasSource pBitmapTextureAtlasSource, final IBitmapTextureAtlasSourceDecoratorShape pBitmapTextureAtlasSourceDecoratorShape, final AndengineColor pColorKeyAndengineColor, final AndengineColor pColorSwapAndengineColor) {
		this(pBitmapTextureAtlasSource, pBitmapTextureAtlasSourceDecoratorShape, pColorKeyAndengineColor.getARGBPackedInt(), pColorSwapAndengineColor.getARGBPackedInt());
	}

	public ColorSwapBitmapTextureAtlasSourceDecorator(final IBitmapTextureAtlasSource pBitmapTextureAtlasSource, final IBitmapTextureAtlasSourceDecoratorShape pBitmapTextureAtlasSourceDecoratorShape, final int pColorKeyColorARGBPackedInt, final int pColorSwapColorARGBPackedInt) {
		this(pBitmapTextureAtlasSource, pBitmapTextureAtlasSourceDecoratorShape, pColorKeyColorARGBPackedInt, TOLERANCE_DEFAULT, pColorSwapColorARGBPackedInt, null);
	}

	public ColorSwapBitmapTextureAtlasSourceDecorator(final IBitmapTextureAtlasSource pBitmapTextureAtlasSource, final IBitmapTextureAtlasSourceDecoratorShape pBitmapTextureAtlasSourceDecoratorShape, final AndengineColor pColorKeyAndengineColor, final AndengineColor pColorSwapAndengineColor, final TextureAtlasSourceDecoratorOptions pTextureAtlasSourceDecoratorOptions) {
		this(pBitmapTextureAtlasSource, pBitmapTextureAtlasSourceDecoratorShape, pColorKeyAndengineColor.getARGBPackedInt(), pColorSwapAndengineColor.getARGBPackedInt(), pTextureAtlasSourceDecoratorOptions);
	}

	public ColorSwapBitmapTextureAtlasSourceDecorator(final IBitmapTextureAtlasSource pBitmapTextureAtlasSource, final IBitmapTextureAtlasSourceDecoratorShape pBitmapTextureAtlasSourceDecoratorShape, final int pColorKeyColorARGBPackedInt, final int pColorSwapColorARGBPackedInt, final TextureAtlasSourceDecoratorOptions pTextureAtlasSourceDecoratorOptions) {
		this(pBitmapTextureAtlasSource, pBitmapTextureAtlasSourceDecoratorShape, pColorKeyColorARGBPackedInt, TOLERANCE_DEFAULT, pColorSwapColorARGBPackedInt, pTextureAtlasSourceDecoratorOptions);
	}

	public ColorSwapBitmapTextureAtlasSourceDecorator(final IBitmapTextureAtlasSource pBitmapTextureAtlasSource, final IBitmapTextureAtlasSourceDecoratorShape pBitmapTextureAtlasSourceDecoratorShape, final AndengineColor pColorKeyAndengineColor, final int pTolerance, final AndengineColor pColorSwapAndengineColor) {
		this(pBitmapTextureAtlasSource, pBitmapTextureAtlasSourceDecoratorShape, pColorKeyAndengineColor.getARGBPackedInt(), pTolerance, pColorSwapAndengineColor.getARGBPackedInt());
	}

	public ColorSwapBitmapTextureAtlasSourceDecorator(final IBitmapTextureAtlasSource pBitmapTextureAtlasSource, final IBitmapTextureAtlasSourceDecoratorShape pBitmapTextureAtlasSourceDecoratorShape, final int pColorKeyColorARGBPackedInt, final int pTolerance, final int pColorSwapColorARGBPackedInt) {
		this(pBitmapTextureAtlasSource, pBitmapTextureAtlasSourceDecoratorShape, pColorKeyColorARGBPackedInt, pTolerance, pColorSwapColorARGBPackedInt, null);
	}

	public ColorSwapBitmapTextureAtlasSourceDecorator(final IBitmapTextureAtlasSource pBitmapTextureAtlasSource, final IBitmapTextureAtlasSourceDecoratorShape pBitmapTextureAtlasSourceDecoratorShape, final AndengineColor pColorKeyAndengineColor, final int pTolerance, final AndengineColor pColorSwapAndengineColor, final TextureAtlasSourceDecoratorOptions pTextureAtlasSourceDecoratorOptions) {
		this(pBitmapTextureAtlasSource, pBitmapTextureAtlasSourceDecoratorShape, pColorKeyAndengineColor.getARGBPackedInt(), pTolerance, pColorSwapAndengineColor.getARGBPackedInt(), pTextureAtlasSourceDecoratorOptions);
	}

	public ColorSwapBitmapTextureAtlasSourceDecorator(final IBitmapTextureAtlasSource pBitmapTextureAtlasSource, final IBitmapTextureAtlasSourceDecoratorShape pBitmapTextureAtlasSourceDecoratorShape, final int pColorKeyColorARGBPackedInt, final int pTolerance, final int pColorSwapColorARGBPackedInt, final TextureAtlasSourceDecoratorOptions pTextureAtlasSourceDecoratorOptions) {
		super(pBitmapTextureAtlasSource, pBitmapTextureAtlasSourceDecoratorShape, pTextureAtlasSourceDecoratorOptions);

		this.mColorKeyColorARGBPackedInt = pColorKeyColorARGBPackedInt;
		this.mTolerance = pTolerance;
		this.mColorSwapColorARGBPackedInt = pColorSwapColorARGBPackedInt;
		this.mPaint.setXfermode(new AvoidXfermode(pColorKeyColorARGBPackedInt, pTolerance, Mode.TARGET));
		this.mPaint.setColor(pColorSwapColorARGBPackedInt);
	}

	@Override
	public ColorSwapBitmapTextureAtlasSourceDecorator deepCopy() {
		return new ColorSwapBitmapTextureAtlasSourceDecorator(this.mBitmapTextureAtlasSource, this.mBitmapTextureAtlasSourceDecoratorShape, this.mColorKeyColorARGBPackedInt, this.mTolerance, this.mColorSwapColorARGBPackedInt, this.mTextureAtlasSourceDecoratorOptions);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
