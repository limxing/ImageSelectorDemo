package me.leefeng.imageselector;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by limxing on 2016/12/14.
 */

public class SqureImageView extends ImageView {
    public SqureImageView(Context context) {
        super(context);
    }

    public SqureImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SqureImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
