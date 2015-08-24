package com.guilhermesgb.togglableedittextlayout;

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

public class TogglableEditTextLayout extends RelativeLayout {

    @Bind(R.id.togglable_edit_text_layout_label_wrapper) TextInputLayout mLabelWrapper;
    @Bind(R.id.togglable_edit_text_layout_edit_text) EditText mEditText;
    @Bind(R.id.togglable_edit_text_layout_marquee_text) TextView mMarqueeText;

    public TogglableEditTextLayout(Context context) {
        this(context, null);
    }

    public TogglableEditTextLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TogglableEditTextLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Iconify.with(new MaterialModule());
        LayoutInflater.from(context).inflate(R.layout.layout_togglable_edit_text, this, true);
        ButterKnife.bind(this, this);
        init(context, context.obtainStyledAttributes(attrs, R.styleable.TogglableEditTextLayout));
    }

    private void init(Context context, TypedArray styledAttributes) {

        String text = styledAttributes.getString(R.styleable.TogglableEditTextLayout_text);
        int textColor = styledAttributes.getColor(R.styleable.TogglableEditTextLayout_text_color,
                context.getResources().getColor(android.R.color.black));
        float textSize = styledAttributes.getColor(R.styleable.TogglableEditTextLayout_text_size, getResources().getDimension(R.dimen.text_size));
        String hint = styledAttributes.getString(R.styleable.TogglableEditTextLayout_hint);
        int accentColor = styledAttributes.getColor(R.styleable.TogglableEditTextLayout_accent_color,
                context.getResources().getColor(android.R.color.black));
        int iconColor = styledAttributes
                .getColor(R.styleable.TogglableEditTextLayout_icon_color, accentColor);
        String iconKey = styledAttributes.getString(R.styleable.TogglableEditTextLayout_icon_key);
        if (iconKey == null) {
            iconKey = context.getString(R.string.togglable_edit_text_layout_icon_key_default);
        }
        int labelColor = styledAttributes
                .getColor(R.styleable.TogglableEditTextLayout_label_color, accentColor);

        IconDrawable iconDrawable = new IconDrawable(getContext(), iconKey).colorRes(iconColor);
        mEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, iconDrawable, null);
        mMarqueeText.setSelected(true);


        if (text != null) {
            mEditText.setText(text);
            mMarqueeText.setText(text + "   " + );
        }

    }


    private void renderVenueContactProperty(final EditText contactProperty, final TextView contactPropertyMarquee,
                                            final TextInputLayout contactPropertyWrapper, final IconDrawable contactPropertyIcon,
                                            final int contactPropertyIconRes, final String contactPropertyValue) {
        if (contactPropertyValue == null || contactPropertyValue.isEmpty()) {
            contactPropertyWrapper.setVisibility(View.GONE);
            contactPropertyMarquee.setVisibility(View.GONE);
            return;
        }
        contactProperty.setCompoundDrawablesWithIntrinsicBounds(null, null, contactPropertyIcon, null);
        contactPropertyWrapper.setVisibility(View.VISIBLE);
        contactPropertyMarquee.setVisibility(View.VISIBLE);
        contactPropertyMarquee.setSelected(true);
        contactPropertyMarquee.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                return enableVenueContactPropertyEditMode(contactProperty, contactPropertyMarquee);
            }

        });
        contactProperty.setText(contactPropertyValue);
        contactProperty.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                return disableVenueContactPropertyEditMode(contactProperty,
                        contactPropertyMarquee, contactPropertyIconRes);
            }

        });
        final View.OnFocusChangeListener existingListener = contactProperty.getOnFocusChangeListener();
        if (!(existingListener instanceof DisableEditModeOnFocusChangeListener)) {
            contactProperty.setOnFocusChangeListener(new DisableEditModeOnFocusChangeListener(contactProperty,
                    contactPropertyMarquee, contactPropertyIconRes, existingListener));
        }
        disableVenueContactPropertyEditMode(contactProperty,
                contactPropertyMarquee, contactPropertyIconRes);
    }

    class DisableEditModeOnFocusChangeListener implements View.OnFocusChangeListener {

        private final EditText contactProperty;
        private final TextView contactPropertyMarquee;
        private final int contactPropertyIconRes;
        private final View.OnFocusChangeListener previousListener;

        public DisableEditModeOnFocusChangeListener(final EditText contactProperty,
                                                    final TextView contactPropertyMarquee, final int contactPropertyIconRes,
                                                    final View.OnFocusChangeListener previousListener) {
            this.previousListener = previousListener;
            this.contactProperty = contactProperty;
            this.contactPropertyMarquee = contactPropertyMarquee;
            this.contactPropertyIconRes = contactPropertyIconRes;
        }

        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            previousListener.onFocusChange(view, hasFocus);
            if (!hasFocus) {
                disableVenueContactPropertyEditMode(contactProperty,
                        contactPropertyMarquee, contactPropertyIconRes);
            }
        }

    }

    private boolean enableVenueContactPropertyEditMode(final EditText contactProperty,
                                                       final TextView contactPropertyMarquee) {
        contactProperty.setVisibility(View.VISIBLE);
        contactProperty.setEnabled(true);
        contactPropertyMarquee.setVisibility(View.INVISIBLE);
        return true;
    }

    private boolean disableVenueContactPropertyEditMode(final EditText contactProperty,
                                                        final TextView contactPropertyMarquee, final int contactPropertyIconRes) {
        if (contactProperty.getText().toString().trim().isEmpty()) {
            return false;
        }
        contactProperty.setVisibility(View.INVISIBLE);
        contactProperty.setEnabled(false);
        contactPropertyMarquee.setVisibility(View.VISIBLE);
        contactPropertyMarquee.setSelected(true);
        contactPropertyMarquee.setText(contactProperty.getText() + "   " + getContext()
                .getString(contactPropertyIconRes));
        return true;
    }

}
