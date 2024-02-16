package org.smartregister.chw.hf.activity;

import static org.smartregister.chw.core.utils.CoreReferralUtils.getCommonRepository;
import static org.smartregister.chw.hf.utils.Constants.JsonForm.HIV_REGISTRATION;
import static org.smartregister.chw.hf.utils.JsonFormUtils.getAutoPopulatedJsonEditFormString;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.form_data.NativeFormsDataBinder;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreJsonFormUtils;
import org.smartregister.chw.core.utils.UpdateDetailsUtil;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.gbv.GbvLibrary;
import org.smartregister.chw.gbv.activity.BaseGbvProfileActivity;
import org.smartregister.chw.gbv.dao.GbvDao;
import org.smartregister.chw.gbv.domain.MemberObject;
import org.smartregister.chw.gbv.domain.Visit;
import org.smartregister.chw.gbv.util.Constants;
import org.smartregister.chw.hf.BuildConfig;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.custom_view.GbvFloatingMenu;
import org.smartregister.chw.hf.dataloader.FamilyMemberDataLoader;
import org.smartregister.chw.hf.utils.AllClientsUtils;
import org.smartregister.chw.hf.utils.GbvVisitUtils;
import org.smartregister.chw.hivst.dao.HivstDao;
import org.smartregister.chw.kvp.dao.KvpDao;
import org.smartregister.chw.ld.dao.LDDao;
import org.smartregister.chw.malaria.dao.MalariaDao;
import org.smartregister.chw.sbc.dao.SbcDao;
import org.smartregister.chw.vmmc.dao.VmmcDao;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.opd.activity.BaseOpdFormActivity;
import org.smartregister.opd.utils.OpdConstants;

import javax.annotation.Nullable;

import timber.log.Timber;

public class GbvMemberProfileActivity extends BaseGbvProfileActivity {
    protected CommonPersonObjectClient commonPersonObject;

    public static void startMe(Activity activity, String baseEntityID) {
        Intent intent = new Intent(activity, GbvMemberProfileActivity.class);
        intent.putExtra(Constants.ACTIVITY_PAYLOAD.BASE_ENTITY_ID, baseEntityID);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_GET_JSON);
    }

    @Override
    public void recordGbv(MemberObject memberObject) {
        GbvVisitActivity.startMe(this, memberObject.getBaseEntityId(), false);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        try {
            GbvVisitUtils.processVisits();
        } catch (Exception e) {
            Timber.e(e);
        }

        Visit lastFollowupVisit = getVisit(Constants.EVENT_TYPE.GBV_FOLLOW_UP_VISIT);

        if (lastFollowupVisit != null && !lastFollowupVisit.getProcessed()) {
            if (GbvVisitUtils.isVisitComplete(lastFollowupVisit)) {
                manualProcessVisit.setVisibility(View.VISIBLE);
                manualProcessVisit.setOnClickListener(view -> {
                    try {
                        GbvVisitUtils.manualProcessVisit(lastFollowupVisit);
                        onResume();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } else {
                manualProcessVisit.setVisibility(View.GONE);
            }
            showVisitInProgress(org.smartregister.chw.hf.utils.Constants.Visits.GBV_VISIT);
            setUpEditButton();
        } else {
            manualProcessVisit.setVisibility(View.GONE);
            textViewVisitDoneEdit.setVisibility(View.GONE);
            visitDone.setVisibility(View.GONE);

            textViewRecordGbv.setVisibility(View.VISIBLE);
        }
    }

    private void showVisitInProgress(String typeOfVisit) {
        if (typeOfVisit.equalsIgnoreCase(org.smartregister.chw.hf.utils.Constants.Visits.GBV_VISIT)) {
            textViewRecordGbv.setVisibility(View.GONE);
        }
        textViewVisitDoneEdit.setVisibility(View.VISIBLE);
        visitDone.setVisibility(View.VISIBLE);
        textViewVisitDone.setText(getString(R.string.visit_in_progress, typeOfVisit));
        textViewVisitDone.setTextColor(getResources().getColor(R.color.black_text_color));
        imageViewCross.setImageResource(org.smartregister.chw.core.R.drawable.activityrow_notvisited);
    }

    private void setUpEditButton() {
        textViewVisitDoneEdit.setOnClickListener(v -> {
            GbvVisitActivity.startMe(GbvMemberProfileActivity.this, memberObject.getBaseEntityId(), true);
        });
    }

    @Override
    public void openMedicalHistory() {
        GbvMedicalHistoryActivity.startMe(this, memberObject);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupViews();
        fetchProfileData();
        profilePresenter.refreshProfileBottom();
    }

    @Override
    public void initializeFloatingMenu() {
        baseGbvFloatingMenu = new GbvFloatingMenu(this, memberObject);
        baseGbvFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(baseGbvFloatingMenu, linearLayoutParams);


        OnClickFloatingMenu onClickFloatingMenu = viewId -> {
            switch (viewId) {
                case R.id.gbv_fab:
                    checkPhoneNumberProvided();
                    ((GbvFloatingMenu) baseGbvFloatingMenu).animateFAB();
                    break;
                case R.id.gbv_call_layout:
                    ((GbvFloatingMenu) baseGbvFloatingMenu).launchCallWidget();
                    ((GbvFloatingMenu) baseGbvFloatingMenu).animateFAB();
                    break;
                default:
                    Timber.d("Unknown fab action");
                    break;
            }

        };

        ((GbvFloatingMenu) baseGbvFloatingMenu).setFloatMenuClickListener(onClickFloatingMenu);
    }

    private void checkPhoneNumberProvided() {
        boolean phoneNumberAvailable = (StringUtils.isNotBlank(memberObject.getPhoneNumber()));
        ((GbvFloatingMenu) baseGbvFloatingMenu).redraw(phoneNumberAvailable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem addMember = menu.findItem(org.smartregister.chw.core.R.id.add_member);
        if (addMember != null) {
            addMember.setVisible(false);
        }

        getMenuInflater().inflate(org.smartregister.chw.core.R.menu.other_member_menu, menu);

        CommonRepository commonRepository = org.smartregister.family.util.Utils.context().commonrepository(org.smartregister.family.util.Utils.metadata().familyMemberRegister.tableName);

        // show profile view
        CommonPersonObject personObject = commonRepository.findByBaseEntityId(memberObject.getBaseEntityId());
        commonPersonObject = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        commonPersonObject.setColumnmaps(personObject.getColumnmaps());

        String gender = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);
        menu.findItem(R.id.action_location_info).setVisible(true);
        menu.findItem(R.id.action_pregnancy_out_come).setVisible(false);
        menu.findItem(R.id.action_remove_member).setVisible(true);
        if (BuildConfig.BUILD_FOR_BORESHA_AFYA_SOUTH) {
            AllClientsUtils.updateHivMenuItems(memberObject.getBaseEntityId(), menu);
            // AllClientsUtils.updateTbMenuItems(memberObject.getBaseEntityId(), menu);

        }
        if (isOfReproductiveAge(commonPersonObject, gender) && gender.equalsIgnoreCase("female") && !AncDao.isANCMember(memberObject.getBaseEntityId())) {
            menu.findItem(R.id.action_pregnancy_confirmation).setVisible(true);
            menu.findItem(R.id.action_anc_registration).setVisible(true);
            menu.findItem(R.id.action_pregnancy_out_come).setVisible(true);
            menu.findItem(R.id.action_pmtct_register).setVisible(true);
        } else {
            menu.findItem(R.id.action_anc_registration).setVisible(false);
            menu.findItem(R.id.action_pregnancy_confirmation).setVisible(false);
            menu.findItem(R.id.action_anc_registration).setVisible(false);
            menu.findItem(R.id.action_pregnancy_out_come).setVisible(false);
            menu.findItem(R.id.action_pmtct_register).setVisible(false);
        }

        if (isOfReproductiveAge(commonPersonObject, gender))
            menu.findItem(R.id.action_fp_initiation).setVisible(HealthFacilityApplication.getApplicationFlavor().hasFp());

        if (isOfReproductiveAge(commonPersonObject, gender) && gender.equalsIgnoreCase("female"))
            menu.findItem(R.id.action_fp_ecp_provision).setVisible(HealthFacilityApplication.getApplicationFlavor().hasFp());


        if (HealthFacilityApplication.getApplicationFlavor().hasLD()) {
            menu.findItem(R.id.action_ld_registration).setVisible(isOfReproductiveAge(commonPersonObject, gender) && gender.equalsIgnoreCase("female") && !LDDao.isRegisteredForLD(memberObject.getBaseEntityId()));
        }

        menu.findItem(R.id.action_sick_child_follow_up).setVisible(false);
        if (HealthFacilityApplication.getApplicationFlavor().hasMalaria())
            menu.findItem(R.id.action_malaria_diagnosis).setVisible(!MalariaDao.isRegisteredForMalaria(memberObject.getBaseEntityId()));

        if (gender.equalsIgnoreCase("male") && HealthFacilityApplication.getApplicationFlavor().hasVmmc())
            menu.findItem(R.id.action_vmmc_registration).setVisible(!VmmcDao.isRegisteredForVmmc(memberObject.getBaseEntityId()));

        if (HealthFacilityApplication.getApplicationFlavor().hasHivst()) {
            String dob = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
            int age = Utils.getAgeFromDate(dob);
            menu.findItem(R.id.action_hivst_registration).setVisible(!HivstDao.isRegisteredForHivst(memberObject.getBaseEntityId()) && age >= 15);
        }
        if (HealthFacilityApplication.getApplicationFlavor().hasKvpPrEP()) {
            String dob = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
            int age = Utils.getAgeFromDate(dob);
            menu.findItem(R.id.action_kvp_registration).setVisible(!KvpDao.isRegisteredForKvp(memberObject.getBaseEntityId()) && age >= 15);
        }
        if (HealthFacilityApplication.getApplicationFlavor().hasSbc()) {
            String dob = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
            int age = Utils.getAgeFromDate(dob);
            menu.findItem(R.id.action_sbc_registration).setVisible(!SbcDao.isRegisteredForSbc(memberObject.getBaseEntityId()) && age >= 10);
        }
        if (HealthFacilityApplication.getApplicationFlavor().hasGbv()) {
            menu.findItem(R.id.action_gbv_registration).setVisible(!GbvDao.isRegisteredForGbv(memberObject.getBaseEntityId()));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_anc_registration) {
            startAncRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_pregnancy_out_come) {
            startPncRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_fp_initiation) {
            startFpRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_fp_ecp_provision) {
            startFpEcpScreening();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_malaria_registration) {
            startMalariaRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_iccm_registration) {
            startIntegratedCommunityCaseManagementEnrollment();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_vmmc_registration) {
            startVmmcRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_registration) {
            if (UpdateDetailsUtil.isIndependentClient(memberObject.getBaseEntityId())) {
                startFormForEdit(org.smartregister.chw.core.R.string.registration_info,
                        CoreConstants.JSON_FORM.getAllClientUpdateRegistrationInfoForm());
            } else {
                startFormForEdit(org.smartregister.chw.core.R.string.edit_member_form_title,
                        CoreConstants.JSON_FORM.getFamilyMemberRegister());
            }
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_hiv_registration) {
            startHivRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_cbhs_registration) {
            startHivRegister();
            return true;
        } else if (i == org.smartregister.chw.core.R.id.action_tb_registration) {
            startTbRegister();
        }
        if (i == org.smartregister.chw.core.R.id.action_pregnancy_confirmation) {
            startPregnancyConfirmation();
            return true;
        }
        if (i == org.smartregister.chw.core.R.id.action_pmtct_register) {
            startPmtctRegisration();
            return true;
        }
        if (i == org.smartregister.chw.core.R.id.action_anc_registration) {
            startAncTransferInRegistration();
            return true;
        }
        if (i == org.smartregister.chw.core.R.id.action_location_info) {
            JSONObject preFilledForm = getAutoPopulatedJsonEditFormString(CoreConstants.JSON_FORM.getFamilyDetailsRegister(), this, getFamilyRegistrationDetails(), Utils.metadata().familyRegister.updateEventType);
            if (preFilledForm != null) startFormActivity(preFilledForm);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected CommonPersonObjectClient getFamilyRegistrationDetails() {
        //Update common person client object with all details from family register table
        final CommonPersonObject personObject = getCommonRepository(Utils.metadata().familyRegister.tableName)
                .findByBaseEntityId(memberObject.getFamilyBaseEntityId());
        CommonPersonObjectClient commonPersonObjectClient = new CommonPersonObjectClient(personObject.getCaseId(),
                personObject.getDetails(), "");
        commonPersonObjectClient.setColumnmaps(personObject.getColumnmaps());
        commonPersonObjectClient.setDetails(personObject.getDetails());
        return commonPersonObjectClient;
    }

    private boolean isOfReproductiveAge(CommonPersonObjectClient commonPersonObject, String gender) {
        if (gender.equalsIgnoreCase("Female")) {
            return Utils.isMemberOfReproductiveAge(commonPersonObject, 10, 55);
        } else if (gender.equalsIgnoreCase("Male")) {
            return Utils.isMemberOfReproductiveAge(commonPersonObject, 15, 49);
        } else {
            return false;
        }
    }

    protected void startAncRegister() {
        AncRegisterActivity.startAncRegistrationActivity(GbvMemberProfileActivity.this, memberObject.getBaseEntityId(), memberObject.getPhoneNumber(), CoreConstants.JSON_FORM.getAncRegistration(), null, memberObject.getFamilyBaseEntityId(), memberObject.getFamilyName());
    }

    protected void startPncRegister() {
        PncRegisterActivity.startPncRegistrationActivity(GbvMemberProfileActivity.this, memberObject.getBaseEntityId(), memberObject.getPhoneNumber(), CoreConstants.JSON_FORM.getPregnancyOutcome(), null, memberObject.getFamilyBaseEntityId(), memberObject.getFamilyName(), null, false);
    }

    protected void startMalariaRegister() {
        MalariaRegisterActivity.startMalariaRegistrationActivity(GbvMemberProfileActivity.this, memberObject.getBaseEntityId());

    }

    protected void startVmmcRegister() {

        VmmcRegisterActivity.startVmmcRegistrationActivity(GbvMemberProfileActivity.this, memberObject.getBaseEntityId());
    }


    protected void startIntegratedCommunityCaseManagementEnrollment() {

    }

    protected void startHivRegister() {
        try {
            HivRegisterActivity.startHIVFormActivity(GbvMemberProfileActivity.this, memberObject.getBaseEntityId(), HIV_REGISTRATION, (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, HIV_REGISTRATION).toString());
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    protected void startTbRegister() {
        try {
            TbRegisterActivity.startTbFormActivity(GbvMemberProfileActivity.this, memberObject.getBaseEntityId(), CoreConstants.JSON_FORM.getTbRegistration(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, CoreConstants.JSON_FORM.getTbRegistration()).toString());
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    protected void startFpRegister() {
        String gender = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);

        FpRegisterActivity.startFpRegistrationActivity(this, memberObject.getBaseEntityId(), CoreConstants.JSON_FORM.getFpRegistrationForm(gender));
    }

    protected void startFpEcpScreening() {
        FpRegisterActivity.startFpRegistrationActivity(this, memberObject.getBaseEntityId(), org.smartregister.chw.hf.utils.Constants.JsonForm.getFPEcpScreening());
    }

    protected void startPregnancyConfirmation() {
        AncRegisterActivity.startAncRegistrationActivity(GbvMemberProfileActivity.this, memberObject.getBaseEntityId(), memberObject.getPhoneNumber(), CoreConstants.JSON_FORM.ANC_PREGNANCY_CONFIRMATION, null, memberObject.getFamilyBaseEntityId(), memberObject.getFamilyName());
    }


    public void startFormForEdit(Integer title_resource, String formName) {
        try {
            JSONObject form = null;
            boolean isPrimaryCareGiver = memberObject.getPrimaryCareGiver().equals(memberObject.getBaseEntityId());
            String titleString = title_resource != null ? getResources().getString(title_resource) : null;
            if (formName.equals(CoreConstants.JSON_FORM.getFamilyMemberRegister())) {

                String eventName = Utils.metadata().familyMemberRegister.updateEventType;

                NativeFormsDataBinder binder = new NativeFormsDataBinder(this, memberObject.getBaseEntityId());
                binder.setDataLoader(new FamilyMemberDataLoader(memberObject.getFamilyName(), isPrimaryCareGiver, titleString, eventName, memberObject.getUniqueId()));

                form = binder.getPrePopulatedForm(CoreConstants.JSON_FORM.getFamilyMemberRegister());
            } else if (formName.equals(CoreConstants.JSON_FORM.getAllClientUpdateRegistrationInfoForm())) {
                String eventName = Utils.metadata().familyMemberRegister.updateEventType;

                NativeFormsDataBinder binder = new NativeFormsDataBinder(this, memberObject.getBaseEntityId());
                binder.setDataLoader(new FamilyMemberDataLoader(memberObject.getFamilyName(), isPrimaryCareGiver, titleString, eventName, memberObject.getUniqueId()));

                form = binder.getPrePopulatedForm(CoreConstants.JSON_FORM.getAllClientUpdateRegistrationInfoForm());
            }

            startActivityForResult(CoreJsonFormUtils.getAncPncStartFormIntent(form, this), JsonFormUtils.REQUEST_CODE_GET_JSON);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    protected void startPmtctRegisration() {
        PncRegisterActivity.startPncRegistrationActivity(GbvMemberProfileActivity.this, memberObject.getBaseEntityId(), memberObject.getPhoneNumber(), org.smartregister.chw.hf.utils.Constants.JsonForm.getPmtctRegistrationForClientsPostPnc(), null, memberObject.getFamilyBaseEntityId(), memberObject.getFamilyName(), null, false);
    }

    protected void startAncTransferInRegistration() {
        AncRegisterActivity.startAncRegistrationActivity(GbvMemberProfileActivity.this, memberObject.getBaseEntityId(), memberObject.getPhoneNumber(), org.smartregister.chw.hf.utils.Constants.JSON_FORM.ANC_TRANSFER_IN_REGISTRATION, null, memberObject.getFamilyBaseEntityId(), memberObject.getFamilyName());
    }


    public void startFormActivity(JSONObject jsonForm) {
        Intent intent = new Intent(this, BaseOpdFormActivity.class);
        intent.putExtra(OpdConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());
        Form form = new Form();
        form.setName(getString(org.smartregister.chw.core.R.string.update_client_registration));
        form.setActionBarBackground(org.smartregister.chw.core.R.color.family_actionbar);
        form.setNavigationBackground(org.smartregister.chw.core.R.color.family_navigation);
        form.setHomeAsUpIndicator(org.smartregister.chw.core.R.mipmap.ic_cross_white);
        form.setPreviousLabel(getResources().getString(org.smartregister.chw.core.R.string.back));
        form.setWizard(false);
        intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, form);
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    public @Nullable
    Visit getVisit(String eventType) {
        return GbvLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), eventType);
    }

}
