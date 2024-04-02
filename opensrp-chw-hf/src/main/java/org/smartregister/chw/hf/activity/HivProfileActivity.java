package org.smartregister.chw.hf.activity;

import static org.smartregister.chw.hf.utils.Constants.JsonForm.HIV_REGISTRATION;
import static org.smartregister.chw.hf.utils.JsonFormUtils.SYNC_LOCATION_ID;
import static org.smartregister.chw.hf.utils.JsonFormUtils.getAutoPopulatedJsonEditFormString;
import static org.smartregister.chw.hiv.util.Constants.ActivityPayload.HIV_MEMBER_OBJECT;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.chw.core.activity.CoreHivProfileActivity;
import org.smartregister.chw.core.activity.CoreHivUpcomingServicesActivity;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.model.CoreAllClientsMemberModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.UpdateDetailsUtil;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.adapter.HivAndTbReferralCardViewAdapter;
import org.smartregister.chw.hf.contract.HivProfileContract;
import org.smartregister.chw.hf.custom_view.HivFloatingMenu;
import org.smartregister.chw.hf.interactor.HfHivProfileInteractor;
import org.smartregister.chw.hf.model.HivTbReferralTasksAndFollowupFeedbackModel;
import org.smartregister.chw.hf.presenter.HivProfilePresenter;
import org.smartregister.chw.hf.utils.LFTUFormUtils;
import org.smartregister.chw.hiv.activity.BaseHivFormsActivity;
import org.smartregister.chw.hiv.dao.HivIndexDao;
import org.smartregister.chw.hiv.domain.HivIndexContactObject;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.chw.hiv.util.HivUtil;
import org.smartregister.chw.hivst.dao.HivstDao;
import org.smartregister.chw.malaria.dao.MalariaDao;
import org.smartregister.chw.tb.util.Constants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.interactor.FamilyProfileInteractor;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class HivProfileActivity extends CoreHivProfileActivity implements HivProfileContract.View {
    private static final String TITLE = "title";
    private CommonPersonObjectClient commonPersonObjectClient;

    public static void startHivProfileActivity(Activity activity, HivMemberObject memberObject) {
        Intent intent = new Intent(activity, HivProfileActivity.class);
        intent.putExtra(HIV_MEMBER_OBJECT, memberObject);
        activity.startActivity(intent);
    }

    public static void startHivProfileActivity(Activity activity, HivMemberObject memberObject, int title) {
        Intent intent = new Intent(activity, HivProfileActivity.class);
        intent.putExtra(HIV_MEMBER_OBJECT, memberObject);
        intent.putExtra(TITLE, title);
        activity.startActivity(intent);
    }

    public static void startHivFollowupActivity(Activity activity, String baseEntityID) throws JSONException {
        Intent intent = new Intent(activity, BaseHivFormsActivity.class);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, HIV_REGISTRATION).toString());
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.ACTION, Constants.ActivityPayloadType.FOLLOW_UP_VISIT);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.USE_DEFAULT_NEAT_FORM_LAYOUT, false);

        activity.startActivityForResult(intent, org.smartregister.chw.anc.util.Constants.REQUEST_CODE_HOME_VISIT);
    }

    public void setReferralTasksAndFollowupFeedback(List<HivTbReferralTasksAndFollowupFeedbackModel> tasksAndFollowupFeedbackModels) {
        if (notificationAndReferralRecyclerView != null && tasksAndFollowupFeedbackModels.size() > 0) {
            RecyclerView.Adapter mAdapter = new HivAndTbReferralCardViewAdapter(tasksAndFollowupFeedbackModels, this, getCommonPersonObjectClient(), CoreConstants.REGISTERED_ACTIVITIES.HIV_REGISTER_ACTIVITY);
            notificationAndReferralRecyclerView.setAdapter(mAdapter);
            notificationAndReferralLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.view_notification_and_referral_row).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setupViews() {
        super.setupViews();
        int titleStringResource = getIntent().getIntExtra(TITLE, -1);
        if (titleStringResource != -1) {
            ((TextView) findViewById(R.id.toolbar_title)).setText(titleStringResource);
        }
        new SetIndexClientsTask(getHivMemberObject()).execute();
    }

    public static void startUpdateCtcNumber(Activity activity, String baseEntityID) throws JSONException {
        Intent intent = new Intent(activity, HivFormsActivity.class);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);
        JSONObject form = (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, org.smartregister.chw.hf.utils.Constants.JsonForm.getHivClientUpdateCtcNumber());
        if (form != null) {
            intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, form.toString());
            intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.ACTION, Constants.ActivityPayloadType.FOLLOW_UP_VISIT);
            intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.USE_DEFAULT_NEAT_FORM_LAYOUT, false);
        }

        activity.startActivityForResult(intent, org.smartregister.chw.anc.util.Constants.REQUEST_CODE_HOME_VISIT);
    }


    @Override
    public void setProfileViewDetails(@androidx.annotation.Nullable HivMemberObject hivMemberObject) {
        super.setProfileViewDetails(hivMemberObject);
        hideFollowUpVisitButton();
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        setCommonPersonObjectClient(getClientDetailsByBaseEntityID(getHivMemberObject().getBaseEntityId()));

        TextView tvRecordCtcNumber = findViewById(R.id.textview_record_index_contact_visit);
        if (getHivMemberObject().getCtcNumber() == null || getHivMemberObject().getCtcNumber().equals("") && getHivMemberObject().getClientHivStatusAfterTesting().equalsIgnoreCase("positive")) {
            getRecordIndexContactLayout().setVisibility(View.VISIBLE);
            tvRecordCtcNumber.setText(R.string.record_ctc_number);
            tvRecordCtcNumber.setOnClickListener(v -> {
                try {
                    startUpdateCtcNumber(HivProfileActivity.this, getHivMemberObject().getBaseEntityId());
                } catch (JSONException e) {
                    Timber.e(e);
                }
            });
        } else if (!getHivMemberObject().getCtcNumber().isEmpty()) {
            getRecordIndexContactLayout().setVisibility(View.VISIBLE);
        } else {
            getRecordIndexContactLayout().setVisibility(View.VISIBLE);
            tvRecordCtcNumber.setText(getString(R.string.hiv_testing_outcome));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((HivProfileContract.Presenter) getHivProfilePresenter()).fetchReferralTasks();
        try {
            new Handler(Looper.getMainLooper()).postDelayed(this::delayNotifyDataSetChanged, 2000);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void delayNotifyDataSetChanged() {
        if (notificationAndReferralRecyclerView != null && notificationAndReferralRecyclerView.getAdapter() != null) {
            notificationAndReferralRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public CommonPersonObjectClient getCommonPersonObjectClient() {
        return commonPersonObjectClient;
    }

    public void setCommonPersonObjectClient(CommonPersonObjectClient commonPersonObjectClient) {
        this.commonPersonObjectClient = commonPersonObjectClient;
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        setHivProfilePresenter(new HivProfilePresenter(this, new HfHivProfileInteractor(this), getHivMemberObject()));
        fetchProfileData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        try {
            if (itemId == R.id.action_hiv_outcome) {
                HivRegisterActivity.startHIVFormActivity(this, getHivMemberObject().getBaseEntityId(), CoreConstants.JSON_FORM.getHivOutcome(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, CoreConstants.JSON_FORM.getHivOutcome()).toString());
                return true;
            } else if (itemId == R.id.action_pregnancy_confirmation) {
                startPregnancyConfirmation(Objects.requireNonNull(getHivMemberObject()));
                return true;
            } else if (itemId == R.id.action_pregnancy_out_come) {
                startPregnancyOutcome(Objects.requireNonNull(getHivMemberObject()));
                return true;
            } else if (itemId == R.id.action_location_info) {
                //use this method in hf to get the chw_location instead of encounter_location for chw
                JSONObject preFilledForm = getAutoPopulatedJsonEditFormString(
                        CoreConstants.JSON_FORM.getFamilyDetailsRegister(), this,
                        UpdateDetailsUtil.getFamilyRegistrationDetails(getHivMemberObject().getFamilyBaseEntityId()), Utils.metadata().familyRegister.updateEventType);
                if (preFilledForm != null)
                    UpdateDetailsUtil.startUpdateClientDetailsActivity(preFilledForm, this);
                return true;
            } else if (itemId == R.id.action_hivst_registration) {
                startHivstRegistration();
                return true;
            } else if (itemId == org.smartregister.chw.core.R.id.action_malaria_diagnosis) {
                startHfMalariaFollowupForm();
                return true;
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void startHfMalariaFollowupForm() {
        MalariaFollowUpVisitActivityHelper.startMalariaFollowUpActivity(this, getHivMemberObject().getFamilyBaseEntityId());
    }

    private void startHivstRegistration() {
        CommonRepository commonRepository = org.smartregister.family.util.Utils.context().commonrepository(org.smartregister.family.util.Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(getHivMemberObject().getBaseEntityId());
        final CommonPersonObjectClient client = new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());
        String gender = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.GENDER, false);

        HivstRegisterActivity.startHivstRegistrationActivity(this, getHivMemberObject().getBaseEntityId(), gender);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(org.smartregister.chw.core.R.menu.hiv_profile_menu, menu);
        menu.findItem(org.smartregister.chw.core.R.id.action_location_info).setVisible(UpdateDetailsUtil.isIndependentClient(getHivMemberObject().getBaseEntityId()));
        //Only showing the hiv outcome menu for positive HIV clients
        if (getHivMemberObject().getCtcNumber().isEmpty()) {
            menu.findItem(R.id.action_hiv_outcome).setVisible(true);
        }
        CommonPersonObjectClient commonPersonObject = getCommonPersonObjectClient();
        String gender = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);
        if (isOfReproductiveAge(commonPersonObject, gender) && gender.equalsIgnoreCase("female") && !AncDao.isANCMember(getHivMemberObject().getBaseEntityId())) {
            menu.findItem(R.id.action_pregnancy_confirmation).setVisible(true);
            menu.findItem(R.id.action_pregnancy_out_come).setVisible(true);
        }
        if (HealthFacilityApplication.getApplicationFlavor().hasHivst()) {
            String dob = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
            int age = Utils.getAgeFromDate(dob);
            menu.findItem(R.id.action_hivst_registration).setVisible(!HivstDao.isRegisteredForHivst(getHivMemberObject().getBaseEntityId()) && age >= 15);
        }
        menu.findItem(R.id.action_malaria_diagnosis).setVisible(!MalariaDao.isRegisteredForMalaria(getHivMemberObject().getBaseEntityId()));
        return true;
    }


    @Override
    public void updateLastVisitRow(Date lastVisitDate) {
        //overriding showing of last visit row
    }

    @Override
    public void setupFollowupVisitEditViews(boolean isWithin24Hours) {
        //overriding setupFollowupVisitEditViews row
    }


    @Override
    public void openHivRegistrationForm() {
        try {
            HivRegisterActivity.startHIVFormActivity(this, getHivMemberObject().getBaseEntityId(), HIV_REGISTRATION, (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, HIV_REGISTRATION).toString());
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public void openUpcomingServices() {
        CoreHivUpcomingServicesActivity.startMe(this, HivUtil.toMember(getHivMemberObject()));
    }

    @Override
    public void openIndexClientsList(HivMemberObject hivMemberObject) {
        IndexContactsListActivity.startHivClientIndexListActivity(this, Objects.requireNonNull(hivMemberObject));
    }

    @Override
    public void openFamilyDueServices() {
        Intent intent = new Intent(this, FamilyProfileActivity.class);

        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, getHivMemberObject().getFamilyBaseEntityId());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_HEAD, getHivMemberObject().getFamilyHead());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.PRIMARY_CAREGIVER, getHivMemberObject().getPrimaryCareGiver());
        intent.putExtra(org.smartregister.family.util.Constants.INTENT_KEY.FAMILY_NAME, getHivMemberObject().getFamilyName());

        intent.putExtra(CoreConstants.INTENT_KEY.SERVICE_DUE, true);
        startActivity(intent);
    }

    @Override
    public void openFollowUpVisitForm(boolean isEdit) {
        if (!isEdit) {
            try {
                startHivFollowupActivity(this, getHivMemberObject().getBaseEntityId());
            } catch (JSONException e) {
                Timber.e(e);
            }
        }
    }

    @Override
    protected void removeMember() {
        // Not required for HF (as seen in other profile activities)?
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.record_hiv_followup_visit) {
            openFollowUpVisitForm(false);
        } else if (id == R.id.textview_record_index_contact_visit && getHivMemberObject().getCtcNumber().isEmpty() && getHivMemberObject().getClientHivStatusAfterTesting().equalsIgnoreCase("positive")) {
            try {
                startUpdateCtcNumber(HivProfileActivity.this, getHivMemberObject().getBaseEntityId());
            } catch (JSONException e) {
                Timber.e(e);
            }
        } else if (id == R.id.textview_record_index_contact_visit && getHivMemberObject().getCtcNumber().isEmpty() && StringUtils.isBlank(getHivMemberObject().getClientHivStatusAfterTesting())) {
            try {
                HivRegisterActivity.startHIVFormActivity(this, getHivMemberObject().getBaseEntityId(), CoreConstants.JSON_FORM.getHivOutcome(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, CoreConstants.JSON_FORM.getHivOutcome()).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            super.onClick(view);
        }
    }

    @Override
    public void initializeCallFAB() {
        setHivFloatingMenu(new HivFloatingMenu(this, getHivMemberObject()));

        int age = Utils.getAgeFromDate(getHivMemberObject().getAge());

        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            switch (viewId) {
                case R.id.hiv_fab:
                    checkPhoneNumberProvided();
                    ((HivFloatingMenu) getHivFloatingMenu()).animateFAB();
                    break;
                case R.id.call_layout:
                    ((HivFloatingMenu) getHivFloatingMenu()).launchCallWidget();
                    ((HivFloatingMenu) getHivFloatingMenu()).animateFAB();
                    break;
                case R.id.refer_to_facility_layout:
                    LFTUFormUtils.startLTFUReferral(this, getHivMemberObject().getBaseEntityId(), getHivMemberObject().getGender(), age);
                    break;
                default:
                    Timber.d("Unknown fab action");
                    break;
            }
        };

        ((HivFloatingMenu) getHivFloatingMenu()).setFloatMenuClickListener(onClickFloatingMenu);
        getHivFloatingMenu().setGravity(Gravity.BOTTOM | Gravity.END);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(getHivFloatingMenu(), linearLayoutParams);
    }

    private void checkPhoneNumberProvided() {
        boolean phoneNumberAvailable = (StringUtils.isNotBlank(getHivMemberObject().getPhoneNumber())
                || StringUtils.isNotBlank(getHivMemberObject().getPrimaryCareGiverPhoneNumber()));

        ((HivFloatingMenu) getHivFloatingMenu()).redraw(phoneNumberAvailable);
    }

    @Override
    public Context getContext() {
        return HivProfileActivity.this;
    }

    @Override
    public void verifyHasPhone() {
        // TODO -> Implement for HF
    }

    @Override
    public void notifyHasPhone(boolean b) {
        // TODO -> Implement for HF
    }

    @Override
    public void openMedicalHistory() {
        //TODO implement
    }

    @Override
    public void openIndexContactRegistration() {
        startHivIndexClientsRegistration();
    }

    private void startHivIndexClientsRegistration() {
        try {
            String locationId = org.smartregister.family.util.Utils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);

            ((HivProfilePresenter) getHivProfilePresenter()).startForm(CoreConstants.JSON_FORM.getHivIndexClientsContactsRegistrationForm(), null, null, locationId);
        } catch (Exception e) {
            Timber.e(e);
            displayToast(org.smartregister.family.R.string.error_unable_to_start_form);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        try {
            String jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
            Timber.d("JSONResult : %s", jsonString);

            if (jsonString == null)
                finish();

            JSONObject form = new JSONObject(jsonString);
            String encounterType = form.getString(OpdJsonFormUtils.ENCOUNTER_TYPE);
            if (encounterType.equals(CoreConstants.EventType.FAMILY_REGISTRATION)) {
                RegisterParams registerParam = new RegisterParams();
                registerParam.setEditMode(false);
                registerParam.setFormTag(OpdJsonFormUtils.formTag(OpdUtils.context().allSharedPreferences()));
                showProgressDialog(org.smartregister.chw.core.R.string.saving_dialog_title);
                ((HivProfilePresenter) getHivProfilePresenter()).saveForm(jsonString, registerParam);
            }
            if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.updateEventType)) {
                FamilyEventClient familyEventClient = new CoreAllClientsMemberModel().processJsonForm(jsonString, getHivMemberObject().getFamilyBaseEntityId());
                JSONObject syncLocationField = CoreJsonFormUtils.getJsonField(new JSONObject(jsonString), STEP1, SYNC_LOCATION_ID);
                familyEventClient.getEvent().setLocationId(CoreJsonFormUtils.getSyncLocationUUIDFromDropdown(syncLocationField));
                familyEventClient.getEvent().setEntityType(CoreConstants.TABLE_NAME.INDEPENDENT_CLIENT);
                new FamilyProfileInteractor().saveRegistration(familyEventClient, jsonString, true, (FamilyProfileContract.InteractorCallBack) getHivProfilePresenter());
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    protected void startPregnancyConfirmation(HivMemberObject hivMemberObject) {

        AncRegisterActivity.startAncRegistrationActivity(HivProfileActivity.this, hivMemberObject.getBaseEntityId(), hivMemberObject.getPhoneNumber(),
                CoreConstants.JSON_FORM.ANC_PREGNANCY_CONFIRMATION, null, hivMemberObject.getFamilyBaseEntityId(), hivMemberObject.getFamilyName());
    }

    protected void startPregnancyOutcome(HivMemberObject hivMemberObject) {

        PncRegisterActivity.startPncRegistrationActivity(HivProfileActivity.this, hivMemberObject.getBaseEntityId(), hivMemberObject.getPhoneNumber(),
                CoreConstants.JSON_FORM.getPregnancyOutcome(), null, hivMemberObject.getFamilyBaseEntityId(), hivMemberObject.getFamilyName(), null, true);
    }

    private boolean isOfReproductiveAge(CommonPersonObjectClient commonPersonObject, String gender) {
        if (gender.equalsIgnoreCase("Female")) {
            return Utils.isMemberOfReproductiveAge(commonPersonObject, 10, 49);
        } else if (gender.equalsIgnoreCase("Male")) {
            return Utils.isMemberOfReproductiveAge(commonPersonObject, 15, 49);
        } else {
            return false;
        }
    }

    private class SetIndexClientsTask extends AsyncTask<Void, Void, Integer> {
        private HivMemberObject hivMemberObject;

        public SetIndexClientsTask(HivMemberObject hivMemberObject) {
            this.hivMemberObject = hivMemberObject;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            List<HivIndexContactObject> indexContactObjectList = HivIndexDao.getIndexContacts(hivMemberObject.getBaseEntityId());
            if (indexContactObjectList != null)
                return indexContactObjectList.size();
            else
                return 0;
        }

        @Override
        protected void onPostExecute(Integer param) {
            setIndexClientsStatus(param > 0);
        }
    }
}

