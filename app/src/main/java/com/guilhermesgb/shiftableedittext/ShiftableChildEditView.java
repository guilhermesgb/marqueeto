package com.guilhermesgb.shiftableedittext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class ShiftableChildEditView extends TextInputLayout {

    public ShiftableChildEditView(Context context) {
        this(context, null);
    }

    public ShiftableChildEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
