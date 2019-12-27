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
        ds = itemPositionToCenterDiff < 0 ? ds / 6 : ds/1.2f;
        float scale = (itemPositionToCenterDiff + 5) / 7;
        child.setPivotX(child.getWidth() / 2);
        child.setPivotY(child.getHeight() / 2);
        float alpha = (itemPositionToCenterDiff + 5) / 5;
        child.setAlpha(alpha > 1 ? (Math.max(0, 1.2f - alpha)) / 0.2f *0.6f+0.4f : alpha);

        return new ItemTransformation(scale, scale, ds, 0);
    }
}