package com.guilhermesgb.shiftableedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShiftableEditText extends RelativeLayout {

    private static final int MODE_EDIT = 0;
    private static final int MODE_MARQUEE = 1;
    private int mode;

    @Bind(R.id.togglable_edit_text_layout_label_wrapper) TextInputLayout mWrapper;
    @Bind(R.id.togglable_edit_text_layout_edit_text) EditText mEditMode;
    @Bind(R.id.togglable_edit_text_layout_marquee_text) TextView mMarqueeMode;

    public ShiftableEditText(Context context) {
        this(context, null);
    }

    public ShiftableEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShiftableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        context.setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        Iconify.with(new MaterialModule());
        LayoutInflater.from(context).inflate(R.layout.layout_shiftable_edit_text, this, true);
        ButterKnife.bind(this, this);
        init(context.obtainStyledAttributes(attrs, R.styleable.ShiftableEditText));
    }

    private void init(TypedArray styledAttributes) {
        String text = styledAttributes.getString(R.styleable.ShiftableEditText_text);
        final int textColor = styledAttributes.getColor(R.styleable.ShiftableEditText_text_color,
                getResources().getColor(android.R.color.black));
        final int textSize = styledAttributes.getColor(R.styleable.ShiftableEditText_text_size,
                (int) getResources().getDimension(R.dimen.togglable_edit_text_text_size));
        final String hint = styledAttributes.getString(R.styleable.ShiftableEditText_hint);
        final int hintColor = styledAttributes.getColor(R.styleable.ShiftableEditText_hint_color,
                getResources().getColor(android.R.color.black));
        final int accentColor = styledAttributes.getColor(R.styleable.ShiftableEditText_accent_color,
                getResources().getColor(android.R.color.black));
        final int iconColor = styledAttributes
                .getColor(R.styleable.ShiftableEditText_icon_color, accentColor);
        String iconKey = styledAttributes.getString(R.styleable.ShiftableEditText_icon_key);
        if (iconKey == null) {
            iconKey = getResources().getString(R.string.togglable_edit_text_layout_icon_key_default);
        }
        final int labelColor = styledAttributes
                .getColor(R.styleable.ShiftableEditText_label_color, accentColor);
        mode = styledAttributes.getInt(R.styleable.ShiftableEditText_mode, MODE_MARQUEE);
        final IconDrawable iconDrawable = new IconDrawable(getContext(), iconKey).color(iconColor)
                .sizeRes(R.dimen.togglable_edit_text_icon_size_big);
        final CharSequence iconCharacter = String.format(getResources()
                        .getString(R.string.togglable_edit_text_layout_icon_definition_template), iconKey,
                String.format("#%06X", (0xFFFFFF & iconColor)), "@dimen/togglable_edit_text_icon_size_small");
        mEditMode.setCompoundDrawablesWithIntrinsicBounds(null, null, iconDrawable, null);
        mEditMode.setText(text);
        mEditMode.setTextSize(textSize);
        mEditMode.setTextColor(textColor);
        mEditMode.setHint(hint);
        mEditMode.setHintTextColor(hintColor);
        mEditMode.setHighlightColor(labelColor);
        mMarqueeMode.setSelected(true);
        mMarqueeMode.setText((text == null ? "" : text) + "   " + iconCharacter);
        mMarqueeMode.setTextSize(textSize);
        mMarqueeMode.setTextColor(textColor);
        mWrapper.setVisibility(getVisibility());
        if (mode == MODE_MARQUEE) {
            disableVenueContactPropertyEditMode(iconCharacter);
        }
        else if (mode == MODE_EDIT) {
            enableVenueContactPropertyEditMode();
        }
        mMarqueeMode.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                return enableVenueContactPropertyEditMode();
            }

        });
        mEditMode.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                return disableVenueContactPropertyEditMode(iconCharacter);
            }

        });
        final View.OnFocusChangeListener existingListener = mEditMode.getOnFocusChangeListener();
        if (!(existingListener instanceof DisableEditModeOnFocusChangeListener)) {
            mEditMode.setOnFocusChangeListener(
                    new DisableEditModeOnFocusChangeListener(iconKey, existingListener)
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
                disableVenueContactPropertyEditMode(iconCharacter);
            }
        }

    }

    private boolean enableVenueContactPropertyEditMode() {
        mode = MODE_EDIT;
        mEditMode.setVisibility(View.VISIBLE);
        mEditMode.setEnabled(true);
        mMarqueeMode.setVisibility(View.INVISIBLE);
        return true;
    }

    private boolean disableVenueContactPropertyEditMode(final CharSequence iconCharacter) {
        if (mEditMode.getText().toString().trim().isEmpty()) {
            return false;
        }
        mode = MODE_MARQUEE;
        mEditMode.setVisibility(View.INVISIBLE);
        mEditMode.setEnabled(false);
        mMarqueeMode.setVisibility(View.VISIBLE);
        mMarqueeMode.setSelected(true);
        mMarqueeMode.setText(mEditMode.getText() + "   " + iconCharacter);
        return true;
    }

}
