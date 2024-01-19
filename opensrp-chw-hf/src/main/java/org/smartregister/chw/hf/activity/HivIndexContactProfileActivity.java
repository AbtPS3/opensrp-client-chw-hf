package org.smartregister.chw.hf.activity;

import static org.smartregister.AllConstants.LocationConstants.SPECIAL_TAG_FOR_OPENMRS_TEAM_MEMBERS;
import static org.smartregister.chw.hf.utils.JsonFormUtils.SYNC_LOCATION_ID;
import static org.smartregister.chw.hf.utils.JsonFormUtils.getAutoPopulatedJsonEditFormString;
import static org.smartregister.chw.hiv.util.Constants.ActivityPayload.HIV_MEMBER_OBJECT;
import static org.smartregister.util.JsonFormUtils.STEP1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.activity.CoreHivIndexContactProfileActivity;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.model.CoreAllClientsMemberModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.UpdateDetailsUtil;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.adapter.HivIndexFollowupCardViewAdapter;
import org.smartregister.chw.hf.contract.HivIndexContactProfileContract;
import org.smartregister.chw.hf.custom_view.HivIndexContactFloatingMenu;
import org.smartregister.chw.hf.interactor.HfHivIndexContactProfileInteractor;
import org.smartregister.chw.hf.model.HivIndexFollowupFeedbackDetailsModel;
import org.smartregister.chw.hf.presenter.HivIndexContactProfilePresenter;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.hiv.dao.HivIndexDao;
import org.smartregister.chw.hiv.domain.HivIndexContactObject;
import org.smartregister.chw.hiv.util.DBConstants;
import org.smartregister.chw.hivst.dao.HivstDao;
import org.smartregister.chw.tb.util.Constants;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.dao.LocationsDao;
import org.smartregister.domain.Location;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.interactor.FamilyProfileInteractor;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.opd.utils.OpdConstants;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class HivIndexContactProfileActivity extends CoreHivIndexContactProfileActivity implements HivIndexContactProfileContract.View {

    public final static String REGISTERED_TO_HIV_REGISTRY = "registered_to_hiv_registry";
    public static final String NAME = "name";
    public static final String PROPERTIES = "properties";
    public static final String TEXT = "text";
    public static final String SELECTION = "selection";
    private CommonPersonObjectClient commonPersonObjectClient;

    public static void startHivIndexContactProfileActivity(Activity activity, HivIndexContactObject hivIndexContactObject) {
        Intent intent = new Intent(activity, HivIndexContactProfileActivity.class);
        intent.putExtra(HIV_MEMBER_OBJECT, hivIndexContactObject);
        activity.startActivity(intent);
    }

    public static void startHivIndexContactFollowupActivity(Activity activity, String baseEntityID) throws JSONException {

        Intent intent = new Intent(activity, HivFormsActivity.class);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);

        HivIndexContactObject hivIndexContactObject = HivIndexDao.getMember(baseEntityID);

        if (hivIndexContactObject.getRelationship().equals("sexual_partner")) { //Changing the rule file to the rule file for index contacts who are sex partners
            JSONObject form = (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, CoreConstants.JSON_FORM.getHivIndexContactFollowupVisit());
            if (form != null)
                form.put("rules_file", "rule/hiv_index_contact_followup_for_sex_partner_rules.yml");
            intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, form.toString());
        } else { //Leaving the default rule files for non sex partners index contacts
            intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, CoreConstants.JSON_FORM.getHivIndexContactFollowupVisit()).toString());
        }

        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.ACTION, Constants.ActivityPayloadType.FOLLOW_UP_VISIT);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.USE_DEFAULT_NEAT_FORM_LAYOUT, false);

        activity.startActivityForResult(intent, org.smartregister.chw.anc.util.Constants.REQUEST_CODE_HOME_VISIT);
    }

    public void setReferralAndFollowupFeedback(List<HivIndexFollowupFeedbackDetailsModel> followupFeedbackDetailsModel) {
        if (notificationAndReferralRecyclerView != null && followupFeedbackDetailsModel.size() > 0) {
            RecyclerView.Adapter mAdapter = new HivIndexFollowupCardViewAdapter(followupFeedbackDetailsModel, this, getCommonPersonObjectClient(), CoreConstants.REGISTERED_ACTIVITIES.HIV_INDEX_REGISTER_ACTIVITY);
            notificationAndReferralRecyclerView.setAdapter(mAdapter);
            notificationAndReferralLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.view_notification_and_referral_row).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreation() {
        super.onCreation();
        setCommonPersonObjectClient(getClientDetailsByBaseEntityID(getHivIndexContactObject().getBaseEntityId()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((HivIndexContactProfilePresenter) getHivContactProfilePresenter()).fetchReferralTasks();
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
        setHivContactProfilePresenter(new HivIndexContactProfilePresenter(this, new HfHivIndexContactProfileInteractor(this), getHivIndexContactObject()));
        fetchProfileData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        try {
            if (itemId == R.id.action_issue_hiv_community_followup_referral) {
                JSONObject formJsonObject = (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, CoreConstants.JSON_FORM.getHivIndexContactCommunityFollowupReferral());
                initializeHealthFacilitiesList(formJsonObject);
                HivIndexContactsContactsRegisterActivity.startHIVFormActivity(this, getHivIndexContactObject().getBaseEntityId(), CoreConstants.JSON_FORM.getHivIndexContactCommunityFollowupReferral(), formJsonObject.toString());
                return true;
            } else if (itemId == R.id.action_location_info) {
                //use this method in hf to get the chw_location instead of encounter_location for chw
                JSONObject preFilledForm = getAutoPopulatedJsonEditFormString(
                        CoreConstants.JSON_FORM.getFamilyDetailsRegister(), this,
                        UpdateDetailsUtil.getFamilyRegistrationDetails(getHivIndexContactObject().getFamilyBaseEntityId()), Utils.metadata().familyRegister.updateEventType);
                if (preFilledForm != null)
                    UpdateDetailsUtil.startUpdateClientDetailsActivity(preFilledForm, this);
                return true;
            } else if (itemId == R.id.action_hivst_registration){
                startHivstRegistration();
                return true;
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void initializeHealthFacilitiesList(JSONObject form) {
        //overrides and loads the list of chw under that facility
        JSONArray steps;
        List<Location> locationList = LocationsDao.getLocationsByTags(Collections.singleton(SPECIAL_TAG_FOR_OPENMRS_TEAM_MEMBERS));
        try {
            JSONArray options = new JSONArray();
            for (Location location : locationList) {
                JSONObject option = new JSONObject();
                option.put("name", StringUtils.capitalize(location.getProperties().getName()));
                option.put("text", StringUtils.capitalize(location.getProperties().getName()));
                JSONObject metaData = new JSONObject();
                metaData.put("openmrs_entity", "location_uuid");
                metaData.put("openmrs_entity_id", location.getProperties().getUid());
                option.put("meta_data", metaData);

                options.put(option);
            }

            steps = form.getJSONArray("steps");
            JSONObject step = steps.getJSONObject(0);
            JSONArray fields = step.getJSONArray("fields");
            int i = 0;
            int j = 0;
            int fieldCount = fields.length();
            int optionCount = options.length();
            while (i < fieldCount) {
                JSONObject field = fields.getJSONObject(i);
                if (field.getString("name").equals("chw_referral_hf")) {
                    JSONArray optionsArr = field.getJSONArray("options");
                    while (j < optionCount) {
                        optionsArr.put(options.get(j));
                        j++;
                    }
                    break;
                }
                i++;
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    private void startHivstRegistration(){
        CommonRepository commonRepository = org.smartregister.family.util.Utils.context().commonrepository(org.smartregister.family.util.Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(getHivIndexContactObject().getBaseEntityId());
        final CommonPersonObjectClient client = new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());
        String gender = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.GENDER, false);

        HivstRegisterActivity.startHivstRegistrationActivity(this, getHivIndexContactObject().getBaseEntityId(), gender);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(org.smartregister.chw.core.R.menu.hiv_profile_menu, menu);
        menu.findItem(R.id.action_issue_hiv_community_followup_referral).setVisible(true);
        CommonPersonObjectClient commonPersonObject = getCommonPersonObjectClient();
        if(HealthFacilityApplication.getApplicationFlavor().hasHivst()) {
            String dob = Utils.getValue(commonPersonObject.getColumnmaps(), org.smartregister.family.util.DBConstants.KEY.DOB, false);
            int age = Utils.getAgeFromDate(dob);
            menu.findItem(R.id.action_hivst_registration).setVisible(HivstDao.isRegisteredForHivst(getHivIndexContactObject().getBaseEntityId()) && age >= 15);
        }
        menu.findItem(org.smartregister.chw.core.R.id.action_location_info).setVisible(UpdateDetailsUtil.isIndependentClient(getHivIndexContactObject().getBaseEntityId()));
        return true;
    }

    @Override
    public void openFollowUpVisitForm(boolean isEdit) {
        if (!isEdit) {
            try {
                startHivIndexContactFollowupActivity(this, getHivIndexContactObject().getBaseEntityId());
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
        super.onClick(view);
        int id = view.getId();
        if (id == R.id.record_hiv_followup_visit) {
            openFollowUpVisitForm(false);
        }
    }

    @Override
    public void initializeCallFAB() {
        setHivFloatingMenu(new HivIndexContactFloatingMenu(this, getHivIndexContactObject()));

        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            switch (viewId) {
                case R.id.hiv_fab:
                    checkPhoneNumberProvided();
                    ((HivIndexContactFloatingMenu) getHivFloatingMenu()).animateFAB();
                    break;
                case R.id.call_layout:
                    ((HivIndexContactFloatingMenu) getHivFloatingMenu()).launchCallWidget();
                    ((HivIndexContactFloatingMenu) getHivFloatingMenu()).animateFAB();
                    break;
                default:
                    Timber.d("Unknown fab action");
                    break;
            }
        };

        ((HivIndexContactFloatingMenu) getHivFloatingMenu()).setFloatMenuClickListener(onClickFloatingMenu);
        getHivFloatingMenu().setGravity(Gravity.BOTTOM | Gravity.END);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(getHivFloatingMenu(), linearLayoutParams);
    }

    private void checkPhoneNumberProvided() {
        boolean phoneNumberAvailable = (StringUtils.isNotBlank(getHivIndexContactObject().getPhoneNumber()));
        ((HivIndexContactFloatingMenu) getHivFloatingMenu()).redraw(phoneNumberAvailable);
    }

    @Override
    public Context getContext() {
        return HivIndexContactProfileActivity.this;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        try {
            String jsonString = data.getStringExtra(OpdConstants.JSON_FORM_EXTRA.JSON);
            Timber.d("JSONResult : %s", jsonString);

            if (jsonString == null)
                finish();
            JSONObject form = new JSONObject(jsonString);
            if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Utils.metadata().familyRegister.updateEventType)) {
                FamilyEventClient familyEventClient = new CoreAllClientsMemberModel().processJsonForm(jsonString, getHivIndexContactObject().getFamilyBaseEntityId());

//                JSONObject syncLocationField = CoreJsonFormUtils.getJsonField(new JSONObject(jsonString), STEP1, SYNC_LOCATION_ID);
//                familyEventClient.getEvent().setLocationId(CoreJsonFormUtils.getSyncLocationUUIDFromDropdown(syncLocationField));

                familyEventClient.getEvent().setEntityType(CoreConstants.TABLE_NAME.INDEPENDENT_CLIENT);
                new FamilyProfileInteractor().saveRegistration(familyEventClient, jsonString, true, (FamilyProfileContract.InteractorCallBack) getHivContactProfilePresenter());
            } else {
                boolean savedToHivRegistry = data.getBooleanExtra(REGISTERED_TO_HIV_REGISTRY, false);
                if (savedToHivRegistry) {
                    HivProfileActivity.startHivProfileActivity(this, Objects.requireNonNull(HivDao.getMember(getHivIndexContactObject().getBaseEntityId())));
                    finish();
                } else {
                    setHivIndexContactObject(HivIndexDao.getMember(getHivIndexContactObject().getBaseEntityId()));
                    initializePresenter();
                    fetchProfileData();
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void setFollowUpButtonDue() {
        super.setFollowUpButtonDue();
        showFollowUpVisitButton(!getHivIndexContactObject().getContactClientNotificationMethod().equals("na"));
    }

    @Override
    public void setupViews() {
        super.setupViews();
        TextView tvRecordHivFollowup = findViewById(R.id.textview_record_reccuring_visit);
        if (!(getHivIndexContactObject().getEnrolledToClinic() || getHivIndexContactObject().getHasTheContactClientBeenTested().equals("")) && getHivIndexContactObject().getTestResults().equalsIgnoreCase("Positive")) {
            tvRecordHivFollowup.setText(R.string.record_ctc_number);
            tvRecordHivFollowup.setOnClickListener(v -> {
                try {
                    startUpdateFollowup(HivIndexContactProfileActivity.this, getHivIndexContactObject().getBaseEntityId());
                } catch (JSONException e) {
                    Timber.e(e);
                }
            });
        }
    }

    protected void startUpdateFollowup(Activity activity, String baseEntityID) throws JSONException {
        Intent intent = new Intent(activity, HivFormsActivity.class);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);
        HivIndexContactObject hivIndexContactObject = getHivIndexContactObject();
        JSONObject form = (new FormUtils()).getFormJsonFromRepositoryOrAssets(activity, org.smartregister.chw.hf.utils.Constants.JsonForm.getHivIndexContactCtcEnrollment());
        if (form != null) {
            JSONArray fields = form.getJSONArray("steps").getJSONObject(0).getJSONArray("fields");
            for (int i = 0; i < fields.length(); i++) {
                JSONObject field = fields.getJSONObject(i);
                if (field.getString(NAME).equals(DBConstants.Key.PLACE_WHERE_TEST_WAS_CONDUCTED)) {
                    field.getJSONObject(PROPERTIES).put(TEXT, hivIndexContactObject.getPlaceWhereTestWasConducted());
                } else if (field.getString(NAME).equals(DBConstants.Key.TEST_RESULTS)) {
                    field.getJSONObject(PROPERTIES).put(TEXT, hivIndexContactObject.getTestResults());
                }

            }
            intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, form.toString());
            intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.ACTION, Constants.ActivityPayloadType.FOLLOW_UP_VISIT);
            intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.USE_DEFAULT_NEAT_FORM_LAYOUT, false);
        }

        activity.startActivityForResult(intent, org.smartregister.chw.anc.util.Constants.REQUEST_CODE_HOME_VISIT);
    }
}

