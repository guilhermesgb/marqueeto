package com.github.guilhermesgb.marqueeto;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.joanzapata.iconify.widget.IconTextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

public class LabelledMarqueeEditText extends FrameLayout {

    private static final String TAG = LabelledMarqueeEditText.class.getSimpleName();
    public static final int MODE_EDIT = 0;
    public static final int MODE_MARQUEE = 1;

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private AttributeSet mAttrs;

    static {
        try {
            Iconify.with(new MaterialModule());
        }
        catch (IllegalArgumentException exception) {
            Log.d(TAG, "Iconify modules already set.");
        }
    }

    private Resources.Theme mContextTheme;

    private TextInputLayout mTextInputLayout;
    private AppCompatEditText mEditText;
    private IconTextView mTextView;
    private RippleView mRippleView;

    private String mText;
    private int mTextColor;
    private float mTextSize;
    private String mHint;
    private int mBaseColor;
    private int mHighlightColor;
    private int mIconColor;
    private String mIconKey;
    private IconDrawable mIconDrawable;
    private CharSequence mIconCharacter;
    private int mErrorColor;
    private String mError;
    private boolean mErrorEnabled;
    private String mErrorCached;
    private int mPreferredMode;
    private int mCurrentMode;
    private int mInputType;
    private int mCurrentCustomStyle;

    private boolean mTextChanged = true;
    private boolean mTextColorChanged = true;
    private boolean mHintChanged = true;
    private boolean mIconChanged = true;
    private boolean mErrorChanged = true;
    private boolean mInputTypeChanged = true;
    private boolean mStyleColorsChanged = true;

    public LabelledMarqueeEditText(Context context) {
        this(context, null);
    }

    public LabelledMarqueeEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LabelledMarqueeEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mAttrs = attrs;
        TypedArray customAttributes = context.obtainStyledAttributes(mAttrs,
                R.styleable.LabelledMarqueeEditText);
        mCurrentCustomStyle = customAttributes
                .getResourceId(R.styleable.LabelledMarqueeEditText_labelledMarqueeEditTextStyle, -1);
        Resources.Theme theme = overrideThemeWithCustomStyle(context, mCurrentCustomStyle);
        final TypedArray themeAttributes = theme.obtainStyledAttributes(mAttrs, new int[]{
                R.attr.baseColor, R.attr.highlightColor, R.attr.iconColor, R.attr.errorColor
        }, 0, 0);
        retrieveAttributesValues(customAttributes, themeAttributes);
        buildEditAndMarqueeViews(context);
        initEditAndMarqueeViews(true);
        resetContextTheme(theme);
    }

    private void resetContextTheme(Resources.Theme theme) {
        theme.setTo(getResources().newTheme());
        theme.setTo(mContextTheme);
        mContextTheme = null;
    }

    private Resources.Theme overrideThemeWithCustomStyle(Context context, int customAttributesStyle) {
        final Resources.Theme theme = context.getTheme();
        mContextTheme = getResources().newTheme();
        mContextTheme.setTo(theme);
        theme.applyStyle(R.style.LabelledMarqueeEditTextTheme, true);
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
            mErrorColor = typedValue.data;
        }
        else {
            mErrorColor = mHighlightColor;
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
        mErrorEnabled = customAttributes.getBoolean(R.styleable.LabelledMarqueeEditText_errorEnabled, true);
        String error = customAttributes.getString(R.styleable.LabelledMarqueeEditText_error);
        if (error == null || error.trim().isEmpty()) {
            mErrorEnabled = false;
        }
        if (mErrorEnabled) {
            mError = error;
        }
        else {
            mError = "";
            mErrorCached = error;
        }
        mPreferredMode = customAttributes.getInt(R.styleable.LabelledMarqueeEditText_mode, MODE_MARQUEE);
        mInputType = customAttributes.getInt(R.styleable.LabelledMarqueeEditText_android_inputType,
                EditorInfo.TYPE_CLASS_TEXT);
        customAttributes.recycle();
    }

    private void buildEditAndMarqueeViews(Context context) {
        View rippleViewSource = LayoutInflater.from(context).inflate(R.layout.layout_ripple, this, false);
        mRippleView = (RippleView) rippleViewSource.findViewById(R.id.labelled_marquee_edit_text_layout_ripple);
        mRippleView.setId(doGenerateViewId());
        addView(mRippleView);
        View editViewSource = LayoutInflater.from(context).inflate(R.layout.layout_edit, this, false);
        mTextInputLayout = (TextInputLayout) editViewSource.findViewById(R.id.labelled_marquee_edit_text_layout_label_wrapper);
        mTextInputLayout.setId(doGenerateViewId());
        mEditText = (AppCompatEditText) editViewSource.findViewById(R.id.labelled_marquee_edit_text_layout_edit_text);
        mEditText.setId(doGenerateViewId());
        addView(mTextInputLayout);
        View marqueeViewSource = LayoutInflater.from(context).inflate(R.layout.layout_marquee, this, false);
        mTextView = (IconTextView) marqueeViewSource.findViewById(R.id.labelled_marquee_edit_text_layout_marquee_text);
        mTextView.setId(doGenerateViewId());
        addView(mTextView, new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    private static synchronized int doGenerateViewId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            while (true) {
                final int result = sNextGeneratedId.get();
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1;
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        }
        else {
            return View.generateViewId();
        }
    }

    private void initEditAndMarqueeViews(boolean firstLoad) {
        if (mTextChanged || mIconChanged || mStyleColorsChanged) {
            mEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mIconDrawable, null);
            setText();
            setTextSize();
            setLabelColor();
            setCursorDrawableColor();
            tintIconWithIconColor();
            mTextChanged = false;
            mIconChanged = false;
            mStyleColorsChanged = false;
        }
        if (mTextColorChanged) {
            setTextColor();
            mTextColorChanged = false;
        }
        if (mHintChanged) {
            setHint();
            mHintChanged = false;
        }
        if (mErrorChanged) {
            setErrorEnabled();
            setError();
        }
        if (mInputTypeChanged) {
            setInputType();
            mInputTypeChanged = false;
        }
        mTextView.setSelected(true);
        mTextInputLayout.setVisibility(getVisibility());
        if (mPreferredMode == MODE_MARQUEE) {
            enableMarqueeMode(mIconCharacter, firstLoad);
        }
        else if (mPreferredMode == MODE_EDIT) {
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
        mTextView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return detector.onTouchEvent(event);
            }

        });
        final View.OnFocusChangeListener existingListener = mEditText.getOnFocusChangeListener();
        if (!(existingListener instanceof DisableEditModeOnFocusChangeListener)) {
            mEditText.setOnFocusChangeListener(
                    DisableEditModeOnFocusChangeListener.newInstance(this, existingListener)
            );
        }
    }

    private void setText() {
        mEditText.setText(mText);
        mTextView.setText((mText == null ? "" : mText) + mIconCharacter);
    }

    private void setTextSize() {
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
    }

    private void setTextColor() {
        mEditText.setTextColor(mTextColor);
        mTextView.setTextColor(mTextColor);
    }

    private void setHint() {
        mTextInputLayout.setHint(mHint);
    }

    private void setLabelColor() {
        mEditText.setHighlightColor(mHighlightColor);
    }

    private void setError() {
        mTextInputLayout.setError(mError);
    }

    private void setErrorEnabled() {
        mTextInputLayout.setErrorEnabled(mErrorEnabled);
    }

    private void setCursorDrawableColor() {
        try {
            Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            fCursorDrawableRes.setAccessible(true);
            int mCursorDrawableRes = fCursorDrawableRes.getInt(mEditText);
            final Drawable[] drawables = new Drawable[2];
            drawables[0] = ContextCompat.getDrawable(mEditText.getContext(), mCursorDrawableRes);
            drawables[1] = ContextCompat.getDrawable(mEditText.getContext(), mCursorDrawableRes);
            drawables[0].setColorFilter(mHighlightColor, PorterDuff.Mode.SRC_IN);
            drawables[1].setColorFilter(mHighlightColor, PorterDuff.Mode.SRC_IN);
            try {
                Field fEditor = TextView.class.getDeclaredField("mEditor");
                fEditor.setAccessible(true);
                Object editor = fEditor.get(mEditText);
                Field fCursorDrawable = editor.getClass().getDeclaredField("mCursorDrawable");
                fCursorDrawable.setAccessible(true);
                fCursorDrawable.set(editor, drawables);
            }
            catch (Throwable ignored) {
                Field fCursorDrawable = TextView.class.getDeclaredField("mCursorDrawable");
                fCursorDrawable.setAccessible(true);
                fCursorDrawable.set(mEditText, drawables);
            }
        }
        catch (Throwable ignored) {
            Log.d(TAG, "Could not override EditText's cursor drawable color via reflection: "
                    + ignored.getMessage());
        }
    }

    private void setInputType() {
        mEditText.setInputType(mInputType);
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
                    labelledMarqueeEditText.enableMarqueeMode(labelledMarqueeEditText.getIconCharacter(), false);
                    labelledMarqueeEditText.invalidate();
                    labelledMarqueeEditText.requestLayout();
                    labelledMarqueeEditText.mRippleView
                            .setRippleColor(labelledMarqueeEditText.mBaseColor);
                }
                else {
                    labelledMarqueeEditText.tintIconWithHighlightColorIfApplicable();
                    labelledMarqueeEditText.tintSelectorDrawableWithHighlightColor();
                    labelledMarqueeEditText.mRippleView.clearAnimation();
                    labelledMarqueeEditText.mRippleView
                            .setRippleColor(labelledMarqueeEditText.mHighlightColor);
                    int x = labelledMarqueeEditText.mRippleView.getWidth() -
                            (labelledMarqueeEditText.mEditText.getCompoundPaddingRight() / 2);
                    int y = labelledMarqueeEditText.mRippleView.getHeight() -
                            (labelledMarqueeEditText.mEditText.getBaseline() +
                            (labelledMarqueeEditText.mEditText
                                    .getCompoundDrawables()[2].getIntrinsicHeight() / 2));
                    labelledMarqueeEditText.mRippleView.animateRipple(x, y);
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

    private void setIconColorTemporarily(int color) {
        final int oldIconColor = mIconColor;
        mIconColor = color;
        setIcon(mIconKey, false);
        mEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, mIconDrawable, null);
        mIconColor = oldIconColor;
    }

    private void tintSelectorDrawableWithHighlightColor() {
        setSelectorDrawableColorTemporarily(mHighlightColor);
    }

    private void setSelectorDrawableColorTemporarily(int color) {
        Drawable middleHandle = ContextCompat.getDrawable(mEditText.getContext(),
                R.drawable.text_select_handle_middle_material);
        Drawable middleHandleWrapper = DrawableCompat.wrap(middleHandle);
        DrawableCompat.setTint(middleHandleWrapper, color);

        Drawable leftHandle = ContextCompat.getDrawable(mEditText.getContext(),
                R.drawable.text_select_handle_left_material);
        Drawable leftHandleWrapper = DrawableCompat.wrap(leftHandle);
        DrawableCompat.setTint(leftHandleWrapper, color);

        Drawable rightHandle = ContextCompat.getDrawable(mEditText.getContext(),
                R.drawable.text_select_handle_right_material);
        Drawable rightHandleWrapper = DrawableCompat.wrap(rightHandle);
        DrawableCompat.setTint(rightHandleWrapper, color);
    }

    private void enableEditMode() {
        mCurrentMode = MODE_EDIT;
        mEditText.setVisibility(View.VISIBLE);
        mEditText.setEnabled(true);
        mTextView.setVisibility(View.INVISIBLE);
        mRippleView.clearAnimation();
        int x = mRippleView.getWidth() - (mEditText.getCompoundPaddingRight() / 2);
        int y = mRippleView.getHeight() - (mEditText.getBaseline() +
                (mEditText.getCompoundDrawables()[2].getIntrinsicHeight() / 2));
        mRippleView.animateRipple(x, y);
    }

    private void enableMarqueeMode(final CharSequence iconCharacter, boolean firstLoad) {
        if (mEditText.getText().toString().trim().isEmpty()) {
            if (mPreferredMode == MODE_MARQUEE) {
                enableEditMode();
            }
            return;
        }
        if (firstLoad) {
            fadeLabelInIfTextNotEmpty();
        }
        mCurrentMode = MODE_MARQUEE;
        mEditText.setVisibility(View.INVISIBLE);
        mEditText.setEnabled(false);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setSelected(true);
        mText = mEditText.getText().toString();
        mTextView.setText(mText + iconCharacter);
    }

    private void fadeLabelInIfTextNotEmpty() {
        if (!isEmpty(true)) {
            mTextInputLayout.setVisibility(View.INVISIBLE);
            int duration = 500;
            final AnimationSet fadeIn = new AnimationSet(true);
            {
                fadeIn.setDuration(duration);
                fadeIn.setInterpolator(new AccelerateInterpolator());
                fadeIn.addAnimation(new AlphaAnimation(0, 1));
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTextInputLayout.setVisibility(View.VISIBLE);
                    mTextInputLayout.startAnimation(fadeIn);
                }
            }, duration);
        }
    }

    public boolean isEmpty(boolean trim) {
        return (trim ? mText.trim().isEmpty() : mText.isEmpty());
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
        mTextChanged = true;
        reloadEditAndMarqueeViews();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int color) {
        mTextColor = getResources().getColor(color);
        mTextColorChanged = true;
        reloadEditAndMarqueeViews();
    }

    public String getHint() {
        return mHint;
    }

    public void setHint(String hint) {
        mHint = hint;
        mHintChanged = true;
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
        mIconChanged = true;
        if (shouldReload) {
            reloadEditAndMarqueeViews();
        }
    }

    public int getErrorColor() {
        return mErrorColor;
    }

    public String getError() {
        if (mErrorEnabled) {
            return mError;
        }
        return mErrorCached;
    }

    public void setError(String error) {
        if (mErrorEnabled) {
            mError = error;
        }
        else {
            mError = "";
            mErrorCached = error;
        }
        mErrorChanged = true;
        reloadEditAndMarqueeViews();
    }

    public boolean getErrorEnabled() {
        return mErrorEnabled;
    }

    public void setErrorEnabled(boolean errorEnabled) {
        mErrorEnabled = errorEnabled;
        if (!errorEnabled) {
            mErrorCached = mError;
            mError = "";
        }
        else {
            mError = mErrorCached;
        }
        if (mErrorEnabled) {
            setCustomStyle(mCurrentCustomStyle);
        }
        else {
            mErrorChanged = true;
            reloadEditAndMarqueeViews();
        }
    }

    public int getPreferredMode() {
        return mPreferredMode;
    }

    public void setMode(int mode) throws IllegalArgumentException {
        switch (mode) {
            case MODE_EDIT:
                mPreferredMode = MODE_EDIT;
                break;
            case MODE_MARQUEE:
                mPreferredMode = MODE_MARQUEE;
                break;
            default:
                throw new IllegalArgumentException(String
                        .format("LabelledMarqueeEditText doesn't support this mode (%d).", mode));
        }
        reloadEditAndMarqueeViews();
    }

    public int getCurrentMode() {
        return mCurrentMode;
    }

    public int getInputType() {
        return mInputType;
    }

    public void setInputType(int inputType) {
        mInputType = inputType;
        mInputTypeChanged = true;
        reloadEditAndMarqueeViews();
    }

    public void setCustomStyle(int customStyle) {
        mCurrentCustomStyle = customStyle;
        Context context = getContext();
        Resources.Theme theme = overrideThemeWithCustomStyle(context, mCurrentCustomStyle);
        final TypedArray themeAttributes = theme.obtainStyledAttributes(mAttrs, new int[]{
                R.attr.baseColor, R.attr.highlightColor, R.attr.iconColor, R.attr.errorColor
        }, 0, 0);
        retrieveThemeAttributeValues(themeAttributes);
        removeAllViews();
        buildEditAndMarqueeViews(context);
        mTextChanged = true;
        mTextColorChanged = true;
        mHintChanged = true;
        mIconChanged = true;
        mErrorChanged = true;
        mInputTypeChanged = true;
        mStyleColorsChanged = true;
        reloadEditAndMarqueeViews();
        resetContextTheme(theme);
    }

    public void reloadEditAndMarqueeViews() {
        initEditAndMarqueeViews(false);
        invalidate();
        requestLayout();
    }

}
