package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.activity.CoreChildHomeVisitActivity;
import org.smartregister.chw.core.activity.CoreChildProfileActivity;
import org.smartregister.chw.core.activity.CoreUpcomingServicesActivity;
import org.smartregister.chw.core.custom_views.CoreFamilyMemberFloatingMenu;
import org.smartregister.chw.core.fragment.FamilyCallDialogFragment;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.core.model.CoreChildProfileModel;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.gbv.dao.GbvDao;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.adapter.ReferralCardViewAdapter;
import org.smartregister.chw.hf.custom_view.FamilyMemberFloatingMenu;
import org.smartregister.chw.hf.presenter.HfChildProfilePresenter;
import org.smartregister.chw.malaria.dao.MalariaDao;
import org.smartregister.domain.Task;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.family.util.Utils;

import java.util.Set;

import timber.log.Timber;

public class ChildProfileActivity extends CoreChildProfileActivity {
    public CoreFamilyMemberFloatingMenu familyFloatingMenu;

    @Override
    protected void onCreation() {
        super.onCreation();
        initializePresenter();
        onClickFloatingMenu = getOnClickFloatingMenu(this, (HfChildProfilePresenter) presenter);
        setupViews();
        setUpToolbar();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int i = view.getId();
        if (i == R.id.last_visit_row) {
            openMedicalHistoryScreen();
        } else if (i == R.id.most_due_overdue_row) {
            openUpcomingServicePage();
        } else if (i == R.id.textview_record_visit || i == R.id.record_visit_done_bar) {
            openVisitHomeScreen(false);
        } else if (i == R.id.textview_edit) {
            openVisitHomeScreen(true);
        }
    }

    @Override
    protected void initializePresenter() {
        String familyName = getIntent().getStringExtra(Constants.INTENT_KEY.FAMILY_NAME);
        if (familyName == null) {
            familyName = "";
        }

        presenter = new HfChildProfilePresenter(this, new CoreChildProfileModel(familyName), childBaseEntityId);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        View recordVisitPanel = findViewById(R.id.record_visit_panel);
        recordVisitPanel.setVisibility(View.GONE);
        familyFloatingMenu = new FamilyMemberFloatingMenu(this);
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        familyFloatingMenu.setGravity(Gravity.BOTTOM | Gravity.END);
        addContentView(familyFloatingMenu, linearLayoutParams);
        prepareFab();
    }

    @Override
    public void setFamilyHasNothingDue() {
        layoutFamilyHasRow.setVisibility(View.GONE);
    }

    @Override
    public void setFamilyHasServiceDue() {
        layoutFamilyHasRow.setVisibility(View.GONE);
    }

    @Override
    public void setFamilyHasServiceOverdue() {
        layoutFamilyHasRow.setVisibility(View.GONE);
    }

    @Override
    public void updateHasPhone(boolean hasPhone) {
        hideProgressBar();
        if (!hasPhone) {
            familyFloatingMenu.hideFab();
        }
    }

    @Override
    public void setClientTasks(Set<Task> taskList) {
        handler.postDelayed(() -> {
            if (notificationAndReferralRecyclerView != null && taskList.size() > 0) {
                RecyclerView.Adapter mAdapter = new ReferralCardViewAdapter(taskList, this,
                        presenter().getChildClient(), CoreConstants.REGISTERED_ACTIVITIES.CHILD_REGISTER_ACTIVITY);
                notificationAndReferralRecyclerView.setAdapter(mAdapter);
                notificationAndReferralLayout.setVisibility(View.VISIBLE);
                findViewById(R.id.view_notification_and_referral_row).setVisibility(View.VISIBLE);

            }
        }, 100);

    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sick_child_follow_up:
                if (presenter != null) {
                    ((HfChildProfilePresenter) presenter).startSickChildForm(null);
                }
                return true;
            case R.id.action_malaria_diagnosis:
                //  displayShortToast(R.string.clicked_malaria_diagnosis);
                startHfMalariaFollowupForm();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startHfMalariaFollowupForm() {
        MalariaFollowUpVisitActivityHelper.startMalariaFollowUpActivity(this, memberObject.getBaseEntityId());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.action_anc_registration).setVisible(false);
        menu.findItem(R.id.action_malaria_registration).setVisible(false);
        menu.findItem(R.id.action_malaria_followup_visit).setVisible(false);
        menu.findItem(R.id.action_remove_member).setVisible(false);
        menu.findItem(R.id.action_sick_child_follow_up).setVisible(true);

        if (HealthFacilityApplication.getApplicationFlavor().hasGbv()) {
            menu.findItem(R.id.action_gbv_registration).setVisible(!GbvDao.isRegisteredForGbv(memberObject.getBaseEntityId()));
        }

        if (MalariaDao.isRegisteredForMalaria(childBaseEntityId)) {
            menu.findItem(R.id.action_malaria_followup_visit).setTitle(R.string.hf_malaria_follow_up);
            menu.findItem(R.id.action_malaria_followup_visit).setVisible(true);
            menu.findItem(R.id.action_malaria_diagnosis).setVisible(false);
        } else {
            menu.findItem(R.id.action_malaria_diagnosis).setVisible(true);
        }
        return true;
    }

    @Override
    protected void startVacRegistration() {
        GbvRegisterActivity.startRegistration(ChildProfileActivity.this, memberObject.getBaseEntityId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void openMedicalHistoryScreen() {
        ChildMedicalHistoryActivity.startMe(this, memberObject);
    }

    private void openUpcomingServicePage() {
        MemberObject memberObject = new MemberObject(presenter().getChildClient());
        CoreUpcomingServicesActivity.startMe(this, memberObject);
    }

    //TODO Child Refactor
    private void openVisitHomeScreen(boolean isEditMode) {
        CoreChildHomeVisitActivity.startMe(this, presenter().getChildClient().getCaseId(), isEditMode);
    }

    public OnClickFloatingMenu getOnClickFloatingMenu(final Activity activity, final HfChildProfilePresenter presenter) {
        return viewId -> {
            switch (viewId) {
                case R.id.call_layout:
                    FamilyCallDialogFragment.launchDialog(activity, presenter.getFamilyId());
                    break;
                case R.id.refer_to_facility_fab:
                    Toast.makeText(activity, "Refer to facility", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        };
    }

    private void prepareFab() {
        familyFloatingMenu.fab.setOnClickListener(v -> FamilyCallDialogFragment.launchDialog(
                this, ((HfChildProfilePresenter) presenter).getFamilyId()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchProfileData();
        presenter().fetchTasks();
        if (notificationAndReferralRecyclerView.getAdapter() != null) {
            notificationAndReferralRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                JSONObject form = new JSONObject(jsonString);
                String encounterType = form.getString(JsonFormUtils.ENCOUNTER_TYPE);
                if (encounterType.equals(CoreConstants.EventType.SICK_CHILD_FOLLOW_UP)) {
                    ((HfChildProfilePresenter) presenter).createSickChildFollowUpEvent(Utils.getAllSharedPreferences(), jsonString);
                }
            } catch (Exception ex) {
                Timber.e(ex);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
