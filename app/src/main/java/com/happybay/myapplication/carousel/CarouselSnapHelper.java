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
    private boolean scrollToHead = false;
    private int headCount = 2;

    public void setScrollToHead(boolean scroll) {
        scrollToHead = scroll;
    }

    @Override public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.getChildCount() <= headCount) return null;
        CarouselLayoutManager manager = (CarouselLayoutManager) layoutManager;
        int center = manager.getCenterItemPosition();
        if (center < headCount && !scrollToHead) center = headCount;
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
        if (layoutManager.getChildCount() < headCount) return RecyclerView.NO_POSITION;
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
            if (round < headCount) {
                round = headCount;
            }
            if (round >= itemCount) {
                round = itemCount - 1;
            }
            return round;
        }
        return position;
    }
}
