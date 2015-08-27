package com.guilhermesgb.shiftableedittext;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.joanzapata.iconify.widget.IconTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftableEditText extends FrameLayout {

    private static final String TAG = ShiftableEditText.class.getSimpleName();
    private static final int MODE_EDIT = 0;
    private static final int MODE_MARQUEE = 1;

    static {
        try {
            Iconify.with(new MaterialModule());
        }
        catch (IllegalArgumentException exception) {
            Log.d(TAG, "Iconify modules already set.");
        }
    }

    final class EditView {

        @Bind(R.id.shiftable_edit_text_layout_label_wrapper) TextInputLayout textInputLayout;
        @Bind(R.id.shiftable_edit_text_layout_edit_text) EditText editText;

        public EditView(View source) {
            ButterKnife.bind(this, source);
        }

    }
    final EditView mEditView;

    final class MarqueeView {

        @Bind(R.id.shiftable_edit_text_layout_marquee_text) IconTextView textView;

        public MarqueeView(View source) {
            ButterKnife.bind(this, source);
        }

    }
    final MarqueeView mMarqueeView;

    private String mText;
    private int mTextColor;
    private float mTextSize;
    private String mHint;
    private IconDrawable mIconDrawable;
    private CharSequence mIconCharacter;
    private int mLabelColor;
    private int mMode;
    private int mInputType;

    public ShiftableEditText(Context context) {
        this(context, null);
    }

    public ShiftableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShiftableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        overrideThemeWithCustomAttributes(context, attrs);
        View editViewSource = LayoutInflater.from(context).inflate(R.layout.layout_edit, this, false);
        mEditView = new EditView(editViewSource);
        addView(mEditView.textInputLayout);
        View marqueeViewSource = LayoutInflater.from(context).inflate(R.layout.layout_marquee, this, false);
        mMarqueeView = new MarqueeView(marqueeViewSource);
        addView(mMarqueeView.textView, new ViewGroup
                .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        initEditAndMarqueeViews();
    }

    private void overrideThemeWithCustomAttributes(Context context, AttributeSet attrs) {
        context.setTheme(R.style.ShiftableEditTextTheme);
        final Resources.Theme theme = context.getTheme();
        TypedArray customAttributes = context.obtainStyledAttributes(attrs, R.styleable.ShiftableEditText);
        int shiftableEditStyle = customAttributes
                .getResourceId(R.styleable.ShiftableEditText_shiftableEditTextStyle, -1);
        if (shiftableEditStyle != -1) {
            theme.applyStyle(shiftableEditStyle, true);
        }
        final TypedArray themeAttributes = theme.obtainStyledAttributes(attrs, new int[]{
                R.attr.baseColor, R.attr.highlightColor, R.attr.iconColor, R.attr.labelColor
        }, R.attr.colorPrimary, 0);
        TypedValue typedValue = new TypedValue();
        mText = customAttributes.getString(R.styleable.ShiftableEditText_android_text);
        mTextColor = customAttributes.getColor(R.styleable.ShiftableEditText_android_textColor,
                getResources().getColor(android.R.color.black));
        mTextSize = customAttributes.getDimension(R.styleable.ShiftableEditText_android_textSize,
                getResources().getDimension(R.dimen.shiftable_edit_text_default_text_size));
        mHint = customAttributes.getString(R.styleable.ShiftableEditText_android_hint);
        int mBaseColor, mHighlightColor, mIconColor;
        if (themeAttributes.getValue(themeAttributes.getIndex(0), typedValue)) {
            mBaseColor = typedValue.data;
        }
        else {
            mBaseColor = getResources().getColor(android.R.color.black);
        }
        if (themeAttributes.getValue(themeAttributes.getIndex(1), typedValue)) {
            mHighlightColor = typedValue.data;
        }
        else {
            mHighlightColor = getResources().getColor(android.R.color.black);
        }
        if (themeAttributes.getValue(themeAttributes.getIndex(2), typedValue)) {
            mIconColor = typedValue.data;
        }
        else {
            mIconColor = mBaseColor;
        }
        String mIconKey = customAttributes.getString(R.styleable.ShiftableEditText_iconKey);
        if (mIconKey == null) {
            mIconDrawable = null;
            mIconCharacter = "";
        }
        else {
            mIconDrawable = new IconDrawable(getContext(), mIconKey)
                    .color(mIconColor).sizeRes(R.dimen.shiftable_edit_text_default_icon_size_big);
            mIconCharacter = "   " + String.format(getResources()
                            .getString(R.string.shiftable_edit_text_layout_icon_definition_template),
                    mIconKey, String.format("#%06X", (0xFFFFFF & mIconColor)),
                    "@dimen/shiftable_edit_text_default_icon_size_small");
        }
        if (themeAttributes.getValue(themeAttributes.getIndex(3), typedValue)) {
            mLabelColor = typedValue.data;
        }
        else {
            mLabelColor = mHighlightColor;
        }
        themeAttributes.recycle();
        mMode = customAttributes.getInt(R.styleable.ShiftableEditText_mode, MODE_MARQUEE);
        mInputType = customAttributes.getInt(R.styleable.ShiftableEditText_android_inputType,
                EditorInfo.TYPE_CLASS_TEXT);
        customAttributes.recycle();
    }

    private void initEditAndMarqueeViews() {
        mEditView.editText.setCompoundDrawablesWithIntrinsicBounds(null, null, mIconDrawable, null);
        mEditView.editText.setText(mText);
        mEditView.editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mEditView.editText.setTextColor(mTextColor);
        mEditView.editText.setHighlightColor(mLabelColor);
        mEditView.editText.setInputType(mInputType);
        mMarqueeView.textView.setSelected(true);
        mMarqueeView.textView.setText((mText == null ? "" : mText) + mIconCharacter);
        mMarqueeView.textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mMarqueeView.textView.setTextColor(mTextColor);
        mEditView.textInputLayout.setHint(mHint);
        mEditView.textInputLayout.setVisibility(getVisibility());
        if (mMode == MODE_MARQUEE) {
            enableMarqueeMode(mIconCharacter);
        }
        else if (mMode == MODE_EDIT) {
            enableEditMode();
        }
        final GestureDetectorCompat detector = new GestureDetectorCompat(getContext(),
                new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {}

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                enableEditMode();
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

        });
        mMarqueeView.textView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return detector.onTouchEvent(event);
            }

        });
        final View.OnFocusChangeListener existingListener = mEditView.editText.getOnFocusChangeListener();
        if (!(existingListener instanceof DisableEditModeOnFocusChangeListener)) {
            mEditView.editText.setOnFocusChangeListener(
                    new DisableEditModeOnFocusChangeListener(mIconCharacter, existingListener)
            );
        }
    }

    class DisableEditModeOnFocusChangeListener implements View.OnFocusChangeListener {

        private final CharSequence iconCharacter;
        private final View.OnFocusChangeListener previousListener;

        public DisableEditModeOnFocusChangeListener(final CharSequence iconCharacter,
                                                    final View.OnFocusChangeListener previousListener) {
            this.previousListener = previousListener;
            this.iconCharacter = iconCharacter;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            previousListener.onFocusChange(view, hasFocus);
            if (!hasFocus) {
                enableMarqueeMode(iconCharacter);
            }
        }

    }

    private void enableEditMode() {
        mMode = MODE_EDIT;
        mEditView.editText.setVisibility(View.VISIBLE);
        mEditView.editText.setEnabled(true);
        mMarqueeView.textView.setVisibility(View.INVISIBLE);
    }

    private void enableMarqueeMode(final CharSequence iconCharacter) {
        if (mEditView.editText.getText().toString().trim().isEmpty()) {
            if (mMode == MODE_MARQUEE) {
                enableEditMode();
            }
            return;
        }
        mMode = MODE_MARQUEE;
        mEditView.editText.setVisibility(View.INVISIBLE);
        mEditView.editText.setEnabled(false);
        mMarqueeView.textView.setVisibility(View.VISIBLE);
        mMarqueeView.textView.setSelected(true);
        mMarqueeView.textView.setText(mEditView.editText.getText().toString() + iconCharacter);
    }

}
