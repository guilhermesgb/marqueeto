package com.github.guilhermesgb.marqueeto.sample;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.activeandroid.query.Select;
import com.github.guilhermesgb.marqueeto.LabelledMarqueeEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class LicensesViewPagerAdapter extends PagerAdapter {

    private List<DriversLicense> driversLicenses = new ArrayList<>();

    @Bind(R.id.name_edit_text) LabelledMarqueeEditText nameEditText;
    @Bind(R.id.identity_number_edit_text) LabelledMarqueeEditText identityNumberEditText;
    @Bind(R.id.issuing_org_edit_text) LabelledMarqueeEditText issuingOrgEditText;
    @Bind(R.id.cpf_number_edit_text) LabelledMarqueeEditText cpfNumberEditText;
    @Bind(R.id.birth_date_edit_text) LabelledMarqueeEditText birthDateEditText;
    @Bind(R.id.filiation_edit_text) LabelledMarqueeEditText filiationEditText;
    @Bind(R.id.good_thru_edit_text) LabelledMarqueeEditText goodThruEditText;
    @Bind(R.id.issuing_date_edit_text) LabelledMarqueeEditText issuingDateEditText;
    @Bind(R.id.location_edit_text) LabelledMarqueeEditText locationEditText;
    @Bind(R.id.save_button) Button saveButton;

    public LicensesViewPagerAdapter() {
        super();
        driversLicenses = fetchAllStoredLicenses();
    }

    private List<DriversLicense> fetchAllStoredLicenses() {
        return new Select().all().from(DriversLicense.class).execute();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        Context context = container.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.drivers_license_form, container, false);
        ButterKnife.bind(this, view);
        container.addView(view);
        if (position != 0) {
            DriversLicense license = driversLicenses.get(position - 1);
            nameEditText.setText(license.getName());
            identityNumberEditText.setText(license.getIdentityNumber());
            issuingOrgEditText.setText(license.getIssuingOrg());
            cpfNumberEditText.setText(license.getCpfNumber());
            birthDateEditText.setText(license.getBirthDate());
            filiationEditText.setText(license.getFiliation());
            goodThruEditText.setText(license.getGoodThru());
            issuingDateEditText.setText(license.getIssuingDate());
            locationEditText.setText(license.getLocation());

        }
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (validateAllFieldValues()) {
                    final DriversLicense license;
                    if (position != 0) {
                        license = driversLicenses.get(position - 1);
                    }
                    else {
                        license = new DriversLicense();
                    }
                    license.setName(nameEditText.getText());
                    license.setIdentityNumber(identityNumberEditText.getText());
                    license.setIssuingOrg(issuingOrgEditText.getText());
                    license.setCpfNumber(cpfNumberEditText.getText());
                    license.setBirthDate(birthDateEditText.getText());
                    license.setFiliation(filiationEditText.getText());
                    license.setGoodThru(goodThruEditText.getText());
                    license.setIssuingDate(issuingDateEditText.getText());
                    license.setLocation(locationEditText.getText());
                    license.save();
                    Snackbar.make(saveButton, "Salvo com sucesso.", Snackbar.LENGTH_SHORT).show();
                    if (position == 0) {
                        EventBus.getDefault().post(new NewLicenseEvent());
                    }
                }
                else {
                    Snackbar.make(saveButton, "Não foi possível salvar, alguns campos estão incorretos.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

        });
        return view;
    }

    private boolean validateAllFieldValues() {
        boolean veredict = validateFieldValue(nameEditText);
        veredict &= validateFieldValue(identityNumberEditText);
        veredict &= validateFieldValue(issuingOrgEditText);
        veredict &= validateFieldValue(cpfNumberEditText);
        veredict &= validateFieldValue(birthDateEditText);
        veredict &= validateFieldValue(filiationEditText);
        veredict &= validateFieldValue(goodThruEditText);
        veredict &= validateFieldValue(issuingDateEditText);
        veredict &= validateFieldValue(locationEditText);
        return veredict;
    }

    private boolean validateFieldValue(LabelledMarqueeEditText editTextField) {
        String value = editTextField.getText();
        if (value == null || value.trim().isEmpty()) {
            editTextField.setErrorEnabled(true);
            editTextField.setError("Não pode ser vazio!");
            return false;
        }
        editTextField.setErrorEnabled(false);
        return true;
    }

    @Override
    public int getCount() {
        return driversLicenses.size() + 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return position == 0 ? "Novo" : driversLicenses.get(position - 1).getCpfNumber();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
