package com.happybay.myapplication.carousel;

import android.view.View;
import androidx.annotation.NonNull;

/**
 * Implementation of {@link CarouselLayoutManager.PostLayoutListener} that makes interesting scaling
 * of items. <br />
 * We are trying to make items scaling quicker for closer items for center and slower for when they
 * are far away.<br />
 * Tis implementation uses atan function for this purpose.
 */
public class CarouselZoomPostLayoutListener implements CarouselLayoutManager.PostLayoutListener {

    @Override
    public ItemTransformation transformChild(@NonNull final View child,
        final float itemPositionToCenterDiff, final int orientation) {

        float ds = itemPositionToCenterDiff * child.getWidth();
        ds = ds * 0.75f;
        if (ds < 0) ds *= 0.5f;
        float scale = itemPositionToCenterDiff * 0.25f + 0.75f;
        if (scale > 1) scale = 1;
        child.setPivotX(child.getWidth() / 2);
        child.setPivotY(child.getHeight() / 2);

        return new ItemTransformation(scale, scale, ds, 0);
    }
}