package org.smartregister.chw.hf.activity;

import static org.smartregister.chw.hf.utils.Constants.JsonForm.HIV_REGISTRATION;
import static org.smartregister.chw.hf.utils.JsonFormUtils.getAutoPopulatedJsonEditFormString;
import static org.smartregister.util.Utils.getName;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.ViewPager;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.core.activity.CoreAllClientsMemberProfileActivity;
import org.smartregister.chw.core.activity.CoreFamilyProfileActivity;
import org.smartregister.chw.core.contract.CoreAllClientsMemberContract;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.form_data.NativeFormsDataBinder;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;
import org.smartregister.chw.gbv.dao.GbvDao;
import org.smartregister.chw.hf.BuildConfig;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.custom_view.FamilyMemberFloatingMenu;
import org.smartregister.chw.hf.dataloader.FamilyMemberDataLoader;
import org.smartregister.chw.hf.fragment.FamilyOtherMemberProfileFragment;
import org.smartregister.chw.hf.presenter.FamilyOtherMemberActivityPresenter;
import org.smartregister.chw.hf.presenter.HfAllClientsMemberPresenter;
import org.smartregister.chw.hf.utils.AllClientsUtils;
import org.smartregister.chw.hf.utils.Constants;
import org.smartregister.chw.hf.utils.LFTUFormUtils;
import org.smartregister.chw.hivst.dao.HivstDao;
import org.smartregister.chw.kvp.dao.KvpDao;
import org.smartregister.chw.ld.dao.LDDao;
import org.smartregister.chw.malaria.dao.MalariaDao;
import org.smartregister.chw.sbc.dao.SbcDao;
import org.smartregister.chw.vmmc.dao.VmmcDao;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.adapter.ViewPagerAdapter;
import org.smartregister.family.fragment.BaseFamilyOtherMemberProfileFragment;
import org.smartregister.family.model.BaseFamilyOtherMemberProfileActivityModel;
import org.smartregister.family.util.DBConstants;
import org.smartregister.view.contract.BaseProfileContract;

import timber.log.Timber;

public class AllClientsMemberProfileActivity extends CoreAllClientsMemberProfileActivity {

    private FamilyMemberFloatingMenu familyFloatingMenu;
    private CoreAllClientsMemberContract.Presenter allClientsMemberPresenter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        CommonRepository commonRepository = org.smartregister.family.util.Utils.context().commonrepository(org.smartregister.family.util.Utils.metadata().familyMemberRegister.tableName);

        // show profile view
        CommonPersonObject personObject = commonRepository.findByBaseEntityId(baseEntityId);
        commonPersonObject = new CommonPersonObjectClient(personObject.getCaseId(), personObject.getDetails(), "");
        commonPersonObject.setColumnmaps(personObject.getColumnmaps());

        String gender = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);
        menu.findItem(R.id.action_location_info).setVisible(true);
        menu.findItem(R.id.action_pregnancy_out_come).setVisible(false);
        menu.findItem(R.id.action_remove_member).setVisible(true);
        if (BuildConfig.BUILD_FOR_BORESHA_AFYA_SOUTH) {
            AllClientsUtils.updateHivMenuItems(baseEntityId, menu);
            // AllClientsUtils.updateTbMenuItems(baseEntityId, menu);

        }
        if (isOfReproductiveAge(commonPersonObject, gender) && gender.equalsIgnoreCase("female") && !AncDao.isANCMember(baseEntityId)) {
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
            menu.findItem(R.id.action_ld_registration).setVisible(isOfReproductiveAge(commonPersonObject, gender) && gender.equalsIgnoreCase("female") && !LDDao.isRegisteredForLD(baseEntityId));
        }

        menu.findItem(R.id.action_sick_child_follow_up).setVisible(false);
        if (HealthFacilityApplication.getApplicationFlavor().hasMalaria())
            menu.findItem(R.id.action_malaria_diagnosis).setVisible(!MalariaDao.isRegisteredForMalaria(baseEntityId));

        if (gender.equalsIgnoreCase("male") && HealthFacilityApplication.getApplicationFlavor().hasVmmc())
            menu.findItem(R.id.action_vmmc_registration).setVisible(!VmmcDao.isRegisteredForVmmc(baseEntityId));

        if (HealthFacilityApplication.getApplicationFlavor().hasHivst()) {
            String dob = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
            int age = Utils.getAgeFromDate(dob);
            menu.findItem(R.id.action_hivst_registration).setVisible(!HivstDao.isRegisteredForHivst(baseEntityId) && age >= 15);
        }
        if (HealthFacilityApplication.getApplicationFlavor().hasKvpPrEP()) {
            String dob = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
            int age = Utils.getAgeFromDate(dob);
            menu.findItem(R.id.action_kvp_registration).setVisible(!KvpDao.isRegisteredForKvp(baseEntityId) && age >= 15);
        }
        if (HealthFacilityApplication.getApplicationFlavor().hasSbc()) {
            String dob = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
            int age = Utils.getAgeFromDate(dob);
            menu.findItem(R.id.action_sbc_registration).setVisible(!SbcDao.isRegisteredForSbc(baseEntityId) && age >= 10);
        }
        if (HealthFacilityApplication.getApplicationFlavor().hasGbv()) {
            menu.findItem(R.id.action_gbv_registration).setVisible(!GbvDao.isRegisteredForGbv(baseEntityId));
        }
        return true;
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        TextView toolbarTitleView = findViewById(org.smartregister.chw.core.R.id.toolbar_title);
        String toolbarTitle = getIntent().getStringExtra(CoreConstants.INTENT_KEY.TOOLBAR_TITLE);
        if (StringUtils.isNotBlank(toolbarTitle)) {
            toolbarTitleView.setText(toolbarTitle);
        } else {
            toolbarTitleView.setText(getString(org.smartregister.chw.core.R.string.return_to_all_client));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == org.smartregister.chw.core.R.id.action_pregnancy_confirmation) {
            startPregnancyConfirmation();
            return true;
        }
        if (itemId == org.smartregister.chw.core.R.id.action_pmtct_register) {
            startPmtctRegisration();
            return true;
        }
        if (itemId == org.smartregister.chw.core.R.id.action_anc_registration) {
            startAncTransferInRegistration();
            return true;
        }
        if (itemId == org.smartregister.chw.core.R.id.action_location_info) {
            JSONObject preFilledForm = getAutoPopulatedJsonEditFormString(CoreConstants.JSON_FORM.getFamilyDetailsRegister(), this, getFamilyRegistrationDetails(), Utils.metadata().familyRegister.updateEventType);
            if (preFilledForm != null) startFormActivity(preFilledForm);
            return true;
        } else if (itemId == org.smartregister.chw.core.R.id.action_malaria_diagnosis) {
            startHfMalariaFollowupForm();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public FamilyOtherMemberActivityPresenter presenter() {
        return (FamilyOtherMemberActivityPresenter) presenter;
    }

    @Override
    protected void startAncRegister() {
        AncRegisterActivity.startAncRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId, PhoneNumber, CoreConstants.JSON_FORM.getAncRegistration(), null, familyBaseEntityId, familyName);
    }

    @Override
    protected void startPncRegister() {
        PncRegisterActivity.startPncRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId, PhoneNumber, CoreConstants.JSON_FORM.getPregnancyOutcome(), null, familyBaseEntityId, familyName, null, false);
    }

    @Override
    protected void startMalariaRegister() {
        MalariaRegisterActivity.startMalariaRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId);

    }

    @Override
    protected void startVmmcRegister() {

        VmmcRegisterActivity.startVmmcRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId);
    }

    @Override
    protected void startIntegratedCommunityCaseManagementEnrollment() {

    }

    @Override
    protected void startHivRegister() {
        try {
            HivRegisterActivity.startHIVFormActivity(AllClientsMemberProfileActivity.this, baseEntityId, HIV_REGISTRATION, (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, HIV_REGISTRATION).toString());
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    protected void startTbRegister() {
        try {
            TbRegisterActivity.startTbFormActivity(AllClientsMemberProfileActivity.this, baseEntityId, CoreConstants.JSON_FORM.getTbRegistration(), (new FormUtils()).getFormJsonFromRepositoryOrAssets(this, CoreConstants.JSON_FORM.getTbRegistration()).toString());
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    protected void startFpRegister() {
        String gender = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);

        FpRegisterActivity.startFpRegistrationActivity(this, baseEntityId, CoreConstants.JSON_FORM.getFpRegistrationForm(gender));
    }

    @Override
    protected void startFpEcpScreening() {
        FpRegisterActivity.startFpRegistrationActivity(this, baseEntityId, Constants.JsonForm.getFPEcpScreening());
    }


    @Override
    protected void removeIndividualProfile() {
        IndividualProfileRemoveActivity.startIndividualProfileActivity(AllClientsMemberProfileActivity.this, commonPersonObject, familyBaseEntityId, familyHead, primaryCaregiver, AllClientsRegisterActivity.class.getCanonicalName());
    }

    @Override
    protected void startEditMemberJsonForm(Integer title_resource, CommonPersonObjectClient client) {
        String titleString = title_resource != null ? getResources().getString(title_resource) : null;
        CommonPersonObjectClient commonPersonObjectClient = getFamilyRegistrationDetails();
        String uniqueID = commonPersonObjectClient.getColumnmaps().get(DBConstants.KEY.UNIQUE_ID);
        boolean isPrimaryCareGiver = commonPersonObject.getCaseId().equalsIgnoreCase(primaryCaregiver);

        NativeFormsDataBinder binder = new NativeFormsDataBinder(getContext(), commonPersonObject.getCaseId());
        binder.setDataLoader(new FamilyMemberDataLoader(familyName, isPrimaryCareGiver, titleString, Utils.metadata().familyMemberRegister.updateEventType, uniqueID));
        JSONObject jsonObject = binder.getPrePopulatedForm(CoreConstants.JSON_FORM.getAllClientUpdateRegistrationInfoForm());

        try {
            startFormActivity(jsonObject);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected BaseProfileContract.Presenter getFamilyOtherMemberActivityPresenter(String familyBaseEntityId, String baseEntityId, String familyHead, String primaryCaregiver, String villageTown, String familyName) {
        return new FamilyOtherMemberActivityPresenter(this, new BaseFamilyOtherMemberProfileActivityModel(), null, familyBaseEntityId, baseEntityId, familyHead, primaryCaregiver, villageTown, familyName);
    }

    @Override
    protected FamilyMemberFloatingMenu getFamilyMemberFloatingMenu() {
        if (familyFloatingMenu == null) {
            familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        }
        return familyFloatingMenu;
    }

    @Override
    protected Context getFamilyOtherMemberProfileActivity() {
        return AllClientsMemberProfileActivity.this;
    }

    @Override
    protected Class<? extends CoreFamilyProfileActivity> getFamilyProfileActivity() {
        return FamilyProfileActivity.class;
    }

    @Override
    protected void initializePresenter() {
        super.initializePresenter();
        onClickFloatingMenu = this;
        allClientsMemberPresenter = new HfAllClientsMemberPresenter(this, baseEntityId);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        BaseFamilyOtherMemberProfileFragment profileOtherMemberFragment = FamilyOtherMemberProfileFragment.newInstance(this.getIntent().getExtras());
        adapter.addFragment(profileOtherMemberFragment, "");

        viewPager.setAdapter(adapter);

        return viewPager;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @Override
    protected BaseFamilyOtherMemberProfileFragment getFamilyOtherMemberProfileFragment() {
        return FamilyOtherMemberProfileFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected void startMalariaFollowUpVisit() {
        //Do nothing - not required for HF
    }

    @Override
    protected void startHfMalariaFollowupForm() {
        MalariaFollowUpVisitActivityHelper.startMalariaFollowUpActivity(this, baseEntityId);
    }

    @Override
    protected void startPmtctRegisration() {
        PncRegisterActivity.startPncRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId, PhoneNumber, Constants.JsonForm.getPmtctRegistrationForClientsPostPnc(), null, familyBaseEntityId, familyName, null, false);
    }

    @Override
    protected void startLDRegistration() {
        String firstName = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String middleName = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.MIDDLE_NAME, true);
        String lastName = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);

        String dob = org.smartregister.family.util.Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, true);
        int age = StringUtils.isNotBlank(dob) ? org.smartregister.family.util.Utils.getAgeFromDate(dob) : 0;

        try {
            LDRegistrationFormActivity.startMe(this, baseEntityId, false, getName(getName(firstName, middleName), lastName), String.valueOf(age));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    protected void startHivstRegistration() {
        String gender = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);
        String dob = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
        int age = Utils.getAgeFromDate(dob);
        HivstRegisterActivity.startHivstRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId, gender, age);
    }

    @Override
    protected void startKvpPrEPRegistration() {
        //do nothing--> this is for chw
    }

    @Override
    protected void startKvpRegistration() {
        String gender = AllClientsUtils.getClientGender(baseEntityId);
        String dob = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
        int age = Utils.getAgeFromDate(dob);
        if (gender.equalsIgnoreCase(Constants.GENDER.MALE)) {
            KvpRegisterActivity.startKvpScreeningMale(AllClientsMemberProfileActivity.this, baseEntityId, gender, age);
        }
        if (gender.equalsIgnoreCase(Constants.GENDER.FEMALE)) {
            KvpRegisterActivity.startKvpScreeningFemale(AllClientsMemberProfileActivity.this, baseEntityId, gender, age);
        }
    }

    @Override
    protected void startPrEPRegistration() {
        String gender = AllClientsUtils.getClientGender(baseEntityId);
        String dob = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
        int age = Utils.getAgeFromDate(dob);
        PrEPRegisterActivity.startMe(this, baseEntityId, gender, age);
    }

    @Override
    protected void startAgywScreening() {
        //do nothing
    }

    @Override
    protected void startSbcRegistration() {
        SbcRegisterActivity.startRegistration(AllClientsMemberProfileActivity.this, baseEntityId);
    }

    @Override
    protected void startGbvRegistration() {
        GbvRegisterActivity.startRegistration(AllClientsMemberProfileActivity.this, baseEntityId);
    }

    @Override
    protected void setIndependentClient(boolean isIndependentClient) {
        super.isIndependent = isIndependentClient;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClickMenu(int viewId) {
        if (viewId == R.id.call_layout) {
            FamilyCallDialogFragment.launchDialog(this, familyBaseEntityId);
        }
        if (viewId == R.id.refer_to_facility_layout) {
            String gender = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);
            String dob = Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.DOB, false);
            LFTUFormUtils.startLTFUReferral(this, baseEntityId, gender, Utils.getAgeFromDate(dob));
        }
    }

    @Override
    public CoreAllClientsMemberContract.Presenter getAllClientsMemberPresenter() {
        return allClientsMemberPresenter;
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

    protected void startPregnancyConfirmation() {
        AncRegisterActivity.startAncRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId, PhoneNumber, CoreConstants.JSON_FORM.ANC_PREGNANCY_CONFIRMATION, null, familyBaseEntityId, familyName);
    }

    protected void startAncTransferInRegistration() {
        AncRegisterActivity.startAncRegistrationActivity(AllClientsMemberProfileActivity.this, baseEntityId, PhoneNumber, Constants.JSON_FORM.ANC_TRANSFER_IN_REGISTRATION, null, familyBaseEntityId, familyName);
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        delayInvalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        delayInvalidateOptionsMenu();
    }

    private void delayInvalidateOptionsMenu() {
        try {
            new Handler(Looper.getMainLooper()).postDelayed(this::invalidateOptionsMenu, 2000);
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
