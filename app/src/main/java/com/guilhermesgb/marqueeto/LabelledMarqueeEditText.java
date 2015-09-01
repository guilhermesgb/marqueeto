package com.guilhermesgb.marqueeto;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.joanzapata.iconify.widget.IconTextView;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LabelledMarqueeEditText extends FrameLayout {

    private static final String TAG = LabelledMarqueeEditText.class.getSimpleName();
    public static final int MODE_EDIT = 0;
    public static final int MODE_MARQUEE = 1;

    private AttributeSet mAttrs;

    static {
        try {
            Iconify.with(new MaterialModule());
        }
        catch (IllegalArgumentException exception) {
            Log.d(TAG, "Iconify modules already set.");
        }
    }

    final class EditView {

        @Bind(R.id.labelled_marquee_edit_text_layout_label_wrapper) TextInputLayout textInputLayout;
        @Bind(R.id.labelled_marquee_edit_text_layout_edit_text) AppCompatEditText editText;

        public EditView(View source) {
            ButterKnife.bind(this, source);
        }

    }
    private EditView mEditView;

    final class MarqueeView {

        @Bind(R.id.labelled_marquee_edit_text_layout_marquee_text) IconTextView textView;

        public MarqueeView(View source) {
            ButterKnife.bind(this, source);
        }

    }
    private MarqueeView mMarqueeView;

    private String mText;
    private int mTextColor;
    private float mTextSize;
    private String mHint;
    private IconDrawable mIconDrawable;
    private CharSequence mIconCharacter;
    private int mBaseColor;
    private int mHighlightColor;
    private int mIconColor;
    private String mIconKey;
    private int mLabelColor;
    private int mMode;
    private int mInputType;

    public LabelledMarqueeEditText(Context context) {
        this(context, null);
    }

    public LabelledMarqueeEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelledMarqueeEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAttrs = attrs;
        context.setTheme(R.style.LabelledMarqueeEditTextTheme);
        TypedArray customAttributes = context.obtainStyledAttributes(mAttrs,
                R.styleable.LabelledMarqueeEditText);
        int customLabelledMarqueeEditTextStyle = customAttributes
                .getResourceId(R.styleable.LabelledMarqueeEditText_labelledMarqueeEditTextStyle, -1);
        Resources.Theme theme = overrideThemeWithCustomStyle(context, customLabelledMarqueeEditTextStyle);
        final TypedArray themeAttributes = theme.obtainStyledAttributes(mAttrs, new int[]{
                R.attr.baseColor, R.attr.highlightColor, R.attr.iconColor, R.attr.labelColor
        }, R.attr.colorPrimary, 0);
        retrieveAttributesValues(customAttributes, themeAttributes);
        buildEditAndMarqueeViews(context);
        initEditAndMarqueeViews();
    }

    private Resources.Theme overrideThemeWithCustomStyle(Context context, int customAttributesStyle) {
        final Resources.Theme theme = context.getTheme();
        if (customAttributesStyle != -1) {
            theme.applyStyle(customAttributesStyle, true);
        }
        return theme;
    }

    private void retrieveAttributesValues(TypedArray customAttributes, TypedArray themeAttributes) {
        retrieveThemeAttributeValues(themeAttributes);
        retrieveCustomAttributeValues(customAttributes);
    }

    private void retrieveThemeAttributeValues(TypedArray themeAttributes) {
        TypedValue typedValue = new TypedValue();
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
        if (themeAttributes.getValue(themeAttributes.getIndex(3), typedValue)) {
            mLabelColor = typedValue.data;
        }
        else {
            mLabelColor = mHighlightColor;
        }
        themeAttributes.recycle();
    }

    private void retrieveCustomAttributeValues(TypedArray customAttributes) {
        mText = customAttributes.getString(R.styleable.LabelledMarqueeEditText_android_text);
        mTextColor = customAttributes.getColor(R.styleable.LabelledMarqueeEditText_android_textColor,
                getResources().getColor(android.R.color.black));
        float maxSize = getResources().getDimension(R.dimen.labelled_marquee_edit_text_max_text_size);
        float maxSizeConverted = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, maxSize,
                getResources().getDisplayMetrics());
        mTextSize = customAttributes.getDimension(R.styleable.LabelledMarqueeEditText_android_textSize,
                getResources().getDimension(R.dimen.labelled_marquee_edit_text_default_text_size));
        float textSizeConverted = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTextSize,
                getResources().getDisplayMetrics());
        if (textSizeConverted > maxSizeConverted) {
            mTextSize = maxSize;
        }
        mHint = customAttributes.getString(R.styleable.LabelledMarqueeEditText_android_hint);
        mIconKey = customAttributes.getString(R.styleable.LabelledMarqueeEditText_iconKey);
        if (mIconKey == null) {
            mIconDrawable = null;
            mIconCharacter = "";
        }
        else {
            mIconDrawable = new IconDrawable(getContext(), mIconKey)
                    .color(mIconColor).sizeRes(R.dimen.labelled_marquee_edit_text_default_icon_size_big);
            mIconCharacter = "   " + String.format(getResources()
                            .getString(R.string.labelled_marquee_edit_text_layout_icon_definition_template),
                    mIconKey, String.format("#%06X", (0xFFFFFF & mIconColor)),
                    "@dimen/labelled_marquee_edit_text_default_icon_size_small");
        }
        mMode = customAttributes.getInt(R.styleable.LabelledMarqueeEditText_mode, MODE_MARQUEE);
        mInputType = customAttributes.getInt(R.styleable.LabelledMarqueeEditText_android_inputType,
                EditorInfo.TYPE_CLASS_TEXT);
        customAttributes.recycle();
    }

    private void buildEditAndMarqueeViews(Context context) {
        View editViewSource = LayoutInflater.from(context).inflate(R.layout.layout_edit, this, false);
        mEditView = new EditView(editViewSource);
        addView(mEditView.textInputLayout);
        View marqueeViewSource = LayoutInflater.from(context).inflate(R.layout.layout_marquee, this, false);
        mMarqueeView = new MarqueeView(marqueeViewSource);
        addView(mMarqueeView.textView, new ViewGroup
                .LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
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
                    DisableEditModeOnFocusChangeListener.newInstance(this, existingListener)
            );
        }
    }

    private static final class DisableEditModeOnFocusChangeListener implements View.OnFocusChangeListener {

        private final WeakReference<LabelledMarqueeEditText> labelledMarqueeEditTextWeakReference;
        private final View.OnFocusChangeListener previousOnFocusChangeListener;

        public static DisableEditModeOnFocusChangeListener newInstance(final LabelledMarqueeEditText target,
                                                                       final View.OnFocusChangeListener listener) {
            WeakReference<LabelledMarqueeEditText> reference = new WeakReference<>(target);
            return new DisableEditModeOnFocusChangeListener(reference, listener);
        }

        private DisableEditModeOnFocusChangeListener(final WeakReference<LabelledMarqueeEditText> reference,
                                                     final View.OnFocusChangeListener listener) {
            labelledMarqueeEditTextWeakReference = reference;
            previousOnFocusChangeListener = listener;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            previousOnFocusChangeListener.onFocusChange(view, hasFocus);
            LabelledMarqueeEditText labelledMarqueeEditText = labelledMarqueeEditTextWeakReference.get();
            if (labelledMarqueeEditText != null) {
                if (!hasFocus) {
                    labelledMarqueeEditText.tintIconWithIconColor();
                    labelledMarqueeEditText.enableMarqueeMode(labelledMarqueeEditText.getIconCharacter());
                    labelledMarqueeEditText.invalidate();
                    labelledMarqueeEditText.requestLayout();
                }
                else {
                    labelledMarqueeEditText.tintIconWithHighlightColorIfApplicable();
                }
            }
        }

    }

    private void tintIconWithIconColor() {
        setIconColorTemporarily(mIconColor);
    }

    private void tintIconWithHighlightColorIfApplicable() {
        if (mIconColor != mHighlightColor) {
            setIconColorTemporarily(mHighlightColor);
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
        mText = mEditView.editText.getText().toString();
        mMarqueeView.textView.setText(mText + iconCharacter);
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
        reloadEditAndMarqueeViews();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int color) {
        mTextColor = getResources().getColor(color);
        reloadEditAndMarqueeViews();
    }

    public String getHint() {
        return mHint;
    }

    public void setHint(String hint) {
        mHint = hint;
        reloadEditAndMarqueeViews();
    }

    public int getBaseColor() {
        return mBaseColor;
    }

    public int getHighlightColor() {
        return mHighlightColor;
    }

    public int getIconColor() {
        return mIconColor;
    }

    private void setIconColorTemporarily(int color) {
        final int oldIconColor = mIconColor;
        mIconColor = color;
        setIcon(mIconKey, false);
        mEditView.editText.setCompoundDrawablesWithIntrinsicBounds(null, null, mIconDrawable, null);
        mIconColor = oldIconColor;
    }

    public String getIcon() {
        return mIconKey;
    }

    public IconDrawable getIconDrawable() {
        return mIconDrawable;
    }

    public CharSequence getIconCharacter() {
        return mIconCharacter;
    }

    public void setIcon(String iconKey) {
        setIcon(iconKey, true);
    }

    private void setIcon(String iconKey, boolean shouldReload) {
        mIconKey = iconKey;
        if (mIconKey == null) {
            mIconDrawable = null;
            mIconCharacter = "";
        } else {
            mIconDrawable = new IconDrawable(getContext(), mIconKey)
                    .color(mIconColor).sizeRes(R.dimen.labelled_marquee_edit_text_default_icon_size_big);
            mIconCharacter = "   " + String.format(getResources()
                            .getString(R.string.labelled_marquee_edit_text_layout_icon_definition_template),
                    mIconKey, String.format("#%06X", (0xFFFFFF & mIconColor)),
                    "@dimen/labelled_marquee_edit_text_default_icon_size_small");
        }
        if (shouldReload) {
            reloadEditAndMarqueeViews();
        }
    }

    public int getLabelColor() {
        return mLabelColor;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) throws IllegalArgumentException {
        switch (mode) {
            case MODE_EDIT:
                mMode = MODE_EDIT;
                break;
            case MODE_MARQUEE:
                mMode = MODE_MARQUEE;
                break;
            default:
                throw new IllegalArgumentException(String
                        .format("LabelledMarqueeEditText doesn't support this mode (%d).", mode));
        }
        reloadEditAndMarqueeViews();
    }

    public int getInputType() {
        return mInputType;
    }

    public void setInputType(int inputType) {
        mInputType = inputType;
        reloadEditAndMarqueeViews();
    }

    public void setCustomStyle(int customStyle) {
        Context context = getContext();
        Resources.Theme theme = overrideThemeWithCustomStyle(context, customStyle);
        final TypedArray themeAttributes = theme.obtainStyledAttributes(mAttrs, new int[]{
                R.attr.baseColor, R.attr.highlightColor, R.attr.iconColor, R.attr.labelColor
        }, R.attr.colorPrimary, 0);
        retrieveThemeAttributeValues(themeAttributes);
        removeAllViews();
        buildEditAndMarqueeViews(context);
        reloadEditAndMarqueeViews();
    }

    public void reloadEditAndMarqueeViews() {
        initEditAndMarqueeViews();
        invalidate();
        requestLayout();
    }

}
