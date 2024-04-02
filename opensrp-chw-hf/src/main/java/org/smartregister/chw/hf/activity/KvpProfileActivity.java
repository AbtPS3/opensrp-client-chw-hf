package org.smartregister.chw.hf.activity;

import static org.smartregister.chw.hf.utils.JsonFormUtils.SYNC_LOCATION_ID;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.chw.core.activity.CoreKvpProfileActivity;
import org.smartregister.chw.core.model.CoreAllClientsMemberModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.UpdateDetailsUtil;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.dao.HfKvpDao;
import org.smartregister.chw.hf.interactor.KvpProfileInteractor;
import org.smartregister.chw.hf.model.HfAllClientsRegisterModel;
import org.smartregister.chw.hf.presenter.KvpProfilePresenter;
import org.smartregister.chw.hivst.dao.HivstDao;
import org.smartregister.chw.kvp.KvpLibrary;
import org.smartregister.chw.kvp.domain.Visit;
import org.smartregister.chw.kvp.util.Constants;
import org.smartregister.chw.kvp.util.DBConstants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.interactor.FamilyProfileInteractor;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.opd.utils.OpdConstants;
import org.smartregister.opd.utils.OpdJsonFormUtils;
import org.smartregister.opd.utils.OpdUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class KvpProfileActivity extends CoreKvpProfileActivity {

    public static void startProfile(Activity activity, String baseEntityId) {
        Intent intent = new Intent(activity, KvpProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.PROFILE_TYPE, Constants.PROFILE_TYPES.KVP_PROFILE);
        activity.startActivity(intent);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        if (HfKvpDao.wereSelfTestingKitsDistributed(memberObject.getBaseEntityId())) {
            if (HivstDao.isRegisteredForHivst(memberObject.getBaseEntityId())) {
                boolean shouldIssueHivSelfTestingKits = false;
                String lastSelfTestingFollowupDateString = HivstDao.clientLastFollowup(memberObject.getBaseEntityId());
                if (lastSelfTestingFollowupDateString == null) {
                    shouldIssueHivSelfTestingKits = true;
                } else {
                    try {
                        Date lastSelfTestingFollowupDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(lastSelfTestingFollowupDateString);
                        Visit lastVisit = getVisit(org.smartregister.chw.kvp.util.Constants.EVENT_TYPE.KVP_BIO_MEDICAL_SERVICE_VISIT);
                        if (truncateTimeFromDate(lastSelfTestingFollowupDate).before(truncateTimeFromDate(lastVisit.getDate())) && lastVisit.getProcessed()) {
                            shouldIssueHivSelfTestingKits = true;
                        }
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }

                if (shouldIssueHivSelfTestingKits) {
                    textViewRecordKvp.setVisibility(View.GONE);
                    visitDone.setVisibility(View.VISIBLE);
                    textViewVisitDoneEdit.setText(R.string.issue_selft_testing_kits);
                    textViewVisitDone.setText(getContext().getString(R.string.pending_hivst_followup));
                    textViewVisitDone.setVisibility(View.VISIBLE);
                    textViewVisitDoneEdit.setOnClickListener(view -> HivstProfileActivity.startProfile(KvpProfileActivity.this, memberObject.getBaseEntityId(), true));
                    imageViewCross.setImageResource(org.smartregister.chw.core.R.drawable.activityrow_notvisited);
                } else {
                    textViewRecordKvp.setVisibility(View.VISIBLE);
                    visitDone.setVisibility(View.GONE);
                    textViewVisitDone.setVisibility(View.GONE);
                    if (isVisitOnProgress(profileType)) {
                        textViewRecordKvp.setVisibility(View.GONE);
                        visitInProgress.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                textViewRecordKvp.setVisibility(View.GONE);
                visitDone.setVisibility(View.VISIBLE);
                textViewVisitDoneEdit.setText(R.string.register_client);
                textViewVisitDone.setText(getContext().getString(R.string.pending_hivst_registration));
                textViewVisitDone.setVisibility(View.VISIBLE);
                textViewVisitDoneEdit.setOnClickListener(v -> startHivstRegistration());
                imageViewCross.setImageResource(org.smartregister.chw.core.R.drawable.activityrow_notvisited);
            }
        }
    }

    private Date truncateTimeFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Override
    public void openFollowupVisit() {
        KvpServiceActivity.startMe(this, memberObject.getBaseEntityId());
    }

    @Override
    protected void startPrEPRegistration() {
        PrEPRegisterActivity.startMe(this, memberObject.getBaseEntityId(), memberObject.getGender(), memberObject.getAge());
    }


    @Override
    public void refreshMedicalHistory(boolean hasHistory) {
        Visit kvpBehavioralServices = getVisit(org.smartregister.chw.kvp.util.Constants.EVENT_TYPE.KVP_BEHAVIORAL_SERVICE_VISIT);
        Visit kvpBioMedicalServices = getVisit(org.smartregister.chw.kvp.util.Constants.EVENT_TYPE.KVP_BIO_MEDICAL_SERVICE_VISIT);
        Visit kvpStructuralServices = getVisit(org.smartregister.chw.kvp.util.Constants.EVENT_TYPE.KVP_STRUCTURAL_SERVICE_VISIT);
        Visit kvpOtherServicesVisit = getVisit(org.smartregister.chw.kvp.util.Constants.EVENT_TYPE.KVP_OTHER_SERVICE_VISIT);


        if (kvpBehavioralServices != null || kvpBioMedicalServices != null || kvpOtherServicesVisit != null || kvpStructuralServices != null) {
            rlLastVisit.setVisibility(View.VISIBLE);
            findViewById(R.id.view_notification_and_referral_row).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.vViewHistory)).setText(R.string.visits_history);
            ((TextView) findViewById(R.id.ivViewHistoryArrow)).setText(getString(R.string.view_visits_history));
        } else {
            rlLastVisit.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.kvp_profile_menu, menu);
        menu.findItem(R.id.action_location_info).setVisible(UpdateDetailsUtil.isIndependentClient(memberObject.getBaseEntityId()));

        if (HfKvpDao.getDominantKVPGroup(memberObject.getBaseEntityId()).equalsIgnoreCase("fsw")) {
            menu.findItem(R.id.action_index_client_elicitation).setVisible(true);
        }
        if (HealthFacilityApplication.getApplicationFlavor().hasHivst()) {
            int age = memberObject.getAge();
            menu.findItem(R.id.action_hivst_registration).setVisible(!HivstDao.isRegisteredForHivst(memberObject.getBaseEntityId()) && age >= 15);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_index_client_elicitation) {
            startHivIndexClientsRegistration();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startHivstRegistration() {
        CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(memberObject.getBaseEntityId());
        final CommonPersonObjectClient client =
                new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());
        String gender = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);
        HivstRegisterActivity.startHivstRegistrationActivity(this, memberObject.getBaseEntityId(), gender);
    }

    public void startHivIndexClientsRegistration() {
        try {
            String locationId = org.smartregister.family.util.Utils.context().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);
            ((KvpProfilePresenter) profilePresenter).startIndexContactRegistrationForm(locationId);
        } catch (Exception e) {
            Timber.e(e);
            displayToast(org.smartregister.family.R.string.error_unable_to_start_form);
        }
    }

    public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {
        JSONObject jsonForm = new HfAllClientsRegisterModel(getContext()).getFormAsJson(formName, entityId, currentLocationId);
        if (formName.equalsIgnoreCase(CoreConstants.JSON_FORM.getHivIndexClientsContactsRegistrationForm())) {
            JSONObject global = jsonForm.getJSONObject("global");
            global.put("index_client_age", memberObject.getAge());
        }
//        startFormActivity(jsonForm);


        Intent intent = new Intent(this, org.smartregister.family.util.Utils.metadata().familyMemberFormActivity);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

        Form form = new Form();
        form.setName(this.getString(R.string.register_hiv_index_clients_contacts));
        form.setActionBarBackground(org.smartregister.chw.core.R.color.family_actionbar);
        form.setNavigationBackground(org.smartregister.chw.core.R.color.family_navigation);
        form.setHomeAsUpIndicator(org.smartregister.chw.core.R.mipmap.ic_cross_white);
        form.setPreviousLabel(getResources().getString(org.smartregister.chw.core.R.string.back));
        form.setWizard(true);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

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
                ((KvpProfilePresenter) profilePresenter).saveForm(jsonString, registerParam);
            }
            if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(org.smartregister.chw.core.utils.Utils.metadata().familyRegister.updateEventType)) {
                FamilyEventClient familyEventClient = new CoreAllClientsMemberModel().processJsonForm(jsonString, memberObject.getFamilyBaseEntityId());
                JSONObject syncLocationField = CoreJsonFormUtils.getJsonField(new JSONObject(jsonString), STEP1, SYNC_LOCATION_ID);
                familyEventClient.getEvent().setLocationId(CoreJsonFormUtils.getSyncLocationUUIDFromDropdown(syncLocationField));
                familyEventClient.getEvent().setEntityType(CoreConstants.TABLE_NAME.INDEPENDENT_CLIENT);
                new FamilyProfileInteractor().saveRegistration(familyEventClient, jsonString, true, (FamilyProfileContract.InteractorCallBack) profilePresenter);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void openMedicalHistory() {
        KvpMedicalHistoryActivity.startMe(this, memberObject);
    }

    private Visit getVisit(String eventType) {
        return KvpLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), eventType);
    }

    @Override
    protected void initializePresenter() {
        showProgressBar(true);
        profilePresenter = new KvpProfilePresenter(this, new KvpProfileInteractor(), memberObject);
        fetchProfileData();
        profilePresenter.refreshProfileBottom();
    }

}
