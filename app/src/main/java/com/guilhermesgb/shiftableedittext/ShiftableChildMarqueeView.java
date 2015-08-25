package com.guilhermesgb.shiftableedittext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.joanzapata.iconify.widget.IconTextView;

public class ShiftableChildMarqueeView extends IconTextView {

    public ShiftableChildMarqueeView(Context context) {
        this(context, null);
    }

    public ShiftableChildMarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShiftableChildMarqueeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        this.setLayoutParams(new FrameLayout.LayoutParams(parentWidth, parentHeight));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
