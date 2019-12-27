package com.happybay.myapplication;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * @ProjectName: My Application
 * @ClassName: MatrixAnimation
 * @Description: java类作用描述
 * @Author: liangtg
 * @CreateDate: 19-12-27 下午1:18
 * @UpdateUser: 更新者
 * @UpdateDate: 19-12-27 下午1:18
 * @UpdateRemark: 更新说明
 */
public class MatrixAnimation extends Animation {
    private Matrix matrix;

    public MatrixAnimation(Matrix matrix) {
        this.matrix = new Matrix(matrix);
        setFillAfter(true);
    }

    @Override protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        t.getMatrix().set(matrix);
    }
}
