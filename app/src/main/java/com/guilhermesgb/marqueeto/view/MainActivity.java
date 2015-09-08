package com.guilhermesgb.marqueeto.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;

import com.guilhermesgb.marqueeto.LabelledMarqueeEditText;
import com.guilhermesgb.marqueeto.R;

public class MainActivity extends Activity {

    LabelledMarqueeEditText inputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputField = (LabelledMarqueeEditText) findViewById(R.id.website_edit_text);
        inputField.setCustomStyle(R.style.LabelledMarqueeEditTextCustom2);
        inputField.setText("");
        inputField.setHint("Endereço Eletrônico");
        inputField.setIcon("md-explore");
        inputField.setInputType(InputType.TYPE_CLASS_TEXT);
        inputField.setTextColor(android.R.color.holo_orange_dark);
        inputField.setMode(LabelledMarqueeEditText.MODE_MARQUEE);
    }

}
