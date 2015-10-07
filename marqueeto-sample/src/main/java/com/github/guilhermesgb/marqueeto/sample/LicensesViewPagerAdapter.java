package com.github.guilhermesgb.marqueeto.sample;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.github.guilhermesgb.marqueeto.LabelledMarqueeEditText;
import com.github.guilhermesgb.marqueeto.sample.event.DeleteLicenseEvent;
import com.github.guilhermesgb.marqueeto.sample.event.NewLicenseEvent;
import com.github.guilhermesgb.marqueeto.sample.event.UpdateLicenseEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

public class LicensesViewPagerAdapter extends PagerAdapter {

    class DriversLicenseForm {

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
        @Bind(R.id.delete_button) Button deleteButton;

    }

    private List<DriversLicense> driversLicenses = new ArrayList<>();

    public LicensesViewPagerAdapter() {
        super();
        driversLicenses = fetchAllStoredLicenses();
    }

    private List<DriversLicense> fetchAllStoredLicenses() {
        return new Select().all().from(DriversLicense.class).execute();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final Context context = container.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.drivers_license_form, container, false);
        final DriversLicenseForm form = new DriversLicenseForm();
        ButterKnife.bind(form, view);
        container.addView(view);
        final DriversLicense license;
        if (position != 0) {
            license = driversLicenses.get(position - 1);
        }
        else {
            license = new DriversLicense();
        }
        form.nameEditText.setText(license.getName());
        form.identityNumberEditText.setText(license.getIdentityNumber());
        form.issuingOrgEditText.setText(license.getIssuingOrg());
        form.cpfNumberEditText.setText(license.getCpfNumber());
        form.birthDateEditText.setText(license.getBirthDate());
        form.filiationEditText.setText(license.getFiliation());
        form.goodThruEditText.setText(license.getGoodThru());
        form.issuingDateEditText.setText(license.getIssuingDate());
        form.locationEditText.setText(license.getLocation());
        form.saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (validateAllFieldValues(form)) {
                    license.setName(form.nameEditText.getText());
                    license.setIdentityNumber(form.identityNumberEditText.getText());
                    license.setIssuingOrg(form.issuingOrgEditText.getText());
                    license.setCpfNumber(form.cpfNumberEditText.getText());
                    license.setBirthDate(form.birthDateEditText.getText());
                    license.setFiliation(form.filiationEditText.getText());
                    license.setGoodThru(form.goodThruEditText.getText());
                    license.setIssuingDate(form.issuingDateEditText.getText());
                    license.setLocation(form.locationEditText.getText());
                    license.save();
                    Snackbar.make(form.saveButton, "Salvo com sucesso.", Snackbar.LENGTH_SHORT).show();
                    if (position == 0) {
                        EventBus.getDefault().post(new NewLicenseEvent());
                    }
                    else {
                        EventBus.getDefault().post(new UpdateLicenseEvent());
                    }
                } else {
                    Snackbar.make(form.saveButton, "Não foi possível salvar, alguns campos estão incorretos.",
                            Snackbar.LENGTH_SHORT).show();
                }
            }

        });
        form.deleteButton.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        form.deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Tem certeza?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                license.delete();
                                Snackbar.make(form.deleteButton, "Removido com sucesso.", Snackbar.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new DeleteLicenseEvent());
                            }

                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {}

                        }
                ).show();
            }

        });
        return view;
    }

    private boolean validateAllFieldValues(DriversLicenseForm form) {
        boolean veredict = validateFieldValue(form.nameEditText);
        veredict &= validateFieldValue(form.identityNumberEditText);
        veredict &= validateFieldValue(form.issuingOrgEditText);
        veredict &= validateFieldValue(form.cpfNumberEditText);
        veredict &= validateFieldValue(form.birthDateEditText);
        veredict &= validateFieldValue(form.filiationEditText);
        veredict &= validateFieldValue(form.goodThruEditText);
        veredict &= validateFieldValue(form.issuingDateEditText);
        veredict &= validateFieldValue(form.locationEditText);
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
