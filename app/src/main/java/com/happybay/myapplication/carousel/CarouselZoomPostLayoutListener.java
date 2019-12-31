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
        float k = 0.875f;
        float kl = 0.5f;
        ds = ds * k;
        if (itemPositionToCenterDiff < 0) {
            ds *= kl;
            if (itemPositionToCenterDiff <= -2) {
                ds -= 100;
                //ds = (itemPositionToCenterDiff + 1 - kl) * child.getWidth() * k;
                //float sk = 0.675f;
                //ds = sk * itemPositionToCenterDiff * child.getWidth()
                //    + (sk - k * kl) * child.getWidth();
            }
        }

        float zeroScale = 0.875f;
        float scale =
            itemPositionToCenterDiff * (itemPositionToCenterDiff < 0 ? 0.16f : (1 - zeroScale))
                + zeroScale;
        if (scale > 1) scale = 1;
        child.setPivotX(child.getWidth() / 2);
        child.setPivotY(child.getHeight() / 2);

        return new ItemTransformation(scale, scale, ds, 0);
    }
}