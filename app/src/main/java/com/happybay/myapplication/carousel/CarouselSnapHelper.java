package com.happybay.myapplication.carousel;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @ProjectName: My Application
 * @ClassName: CarouselSnapHelper
 * @Description: java类作用描述
 * @Author: liangtg
 * @CreateDate: 19-12-28 上午10:54
 * @UpdateUser: 更新者
 * @UpdateDate: 19-12-28 上午10:54
 * @UpdateRemark: 更新说明
 */
public class CarouselSnapHelper extends LinearSnapHelper {
    @Override public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        CarouselLayoutManager manager = (CarouselLayoutManager) layoutManager;
        int center = manager.getCenterItemPosition();
        for (int i = 0; i < manager.getChildCount(); i++) {
            if (manager.getPosition(manager.getChildAt(i)) == center) return manager.getChildAt(i);
        }
        return null;
    }

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager,
        @NonNull View targetView) {
        int[] out = new int[2];
        CarouselLayoutManager manager = (CarouselLayoutManager) layoutManager;
        int offset = manager.getOffsetForCurrentView(targetView);
        out[0] = -offset;
        return out;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX,
        int velocityY) {
        CarouselLayoutManager manager = (CarouselLayoutManager) layoutManager;
        int position = super.findTargetSnapPosition(layoutManager, velocityX * 10, velocityY);
        if (position != RecyclerView.NO_POSITION) {
            int[] distances = calculateScrollDistance(velocityX, velocityY);
            float distancePerChild = layoutManager.getChildAt(0).getWidth();
            if (distancePerChild <= 0) {
                return 0;
            }
            int distance =
                Math.abs(distances[0]) > Math.abs(distances[1]) ? distances[0] : distances[1];
            int round = Math.round(distance / distancePerChild) + manager.getCenterItemPosition();
            final int itemCount = layoutManager.getItemCount();
            if (round < 0) {
                round = 0;
            }
            if (round >= itemCount) {
                round = itemCount - 1;
            }
            return round;
        }
        return position;
    }
}
