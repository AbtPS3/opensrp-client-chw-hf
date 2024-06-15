package org.smartregister.chw.hf.activity;

import static org.smartregister.chw.hf.utils.JsonFormUtils.ENCOUNTER_TYPE;
import static org.smartregister.util.JsonFormUtils.FIELDS;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.chw.core.activity.CoreGbvRegisterActivity;
import org.smartregister.chw.gbv.util.Constants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.fragment.GbvRegisterFragment;
import org.smartregister.chw.hf.utils.VmmcReferralFormUtils;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Calendar;

import timber.log.Timber;

public class GbvRegisterActivity extends CoreGbvRegisterActivity {

    public static void startRegistration(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, GbvRegisterActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.ACTION, Constants.ACTIVITY_PAYLOAD_TYPE.REGISTRATION);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.GBV_FORM_NAME, Constants.FORMS.GBV_ENROLLMENT);

        activity.startActivity(intent);
    }

    @Override
    public Form getFormConfig() {
        Form form = new Form();
        form.setActionBarBackground(org.smartregister.chw.core.R.color.family_actionbar);
        form.setWizard(true);
        form.setName(getString(R.string.gbv_registration));
        form.setNavigationBackground(org.smartregister.chw.core.R.color.family_navigation);
        form.setNextLabel(this.getResources().getString(org.smartregister.chw.core.R.string.next));
        form.setPreviousLabel(this.getResources().getString(org.smartregister.chw.core.R.string.back));
        form.setSaveLabel(this.getResources().getString(org.smartregister.chw.core.R.string.save));
        return form;
    }

    @Override
    protected BaseRegisterFragment getRegisterFragment() {
        return new GbvRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        return new Fragment[]{};
    }

    @Override
    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, Utils.metadata().familyMemberFormActivity);

        try {
            if (jsonForm.getString(ENCOUNTER_TYPE).equals("GBV Registration")) {

                JSONObject registrationNumberJsonObject = JsonFormUtils.getFieldJSONObject(jsonForm.getJSONObject(STEP1).getJSONArray(FIELDS), "registration_number");
                if (registrationNumberJsonObject != null) {
                    registrationNumberJsonObject.put("mask", VmmcReferralFormUtils.getHfrCode() + "-##-###");
                }

            }
        } catch (Exception e) {
            Timber.e(e);
        }

        intent.putExtra(org.smartregister.chw.vmmc.util.Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        if (getFormConfig() != null) {
            intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, getFormConfig());
        }
        startActivityForResult(intent, org.smartregister.chw.vmmc.util.Constants.REQUEST_CODE_GET_JSON);
    }

}
