package org.smartregister.chw.hf.activity;

import static org.smartregister.chw.hf.utils.Constants.REQUEST_FILTERS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.core.activity.CoreHivRegisterActivity;
import org.smartregister.chw.hf.fragment.HivRegisterFragment;
import org.smartregister.chw.hiv.fragment.BaseHivCommunityFollowupRegisterFragment;
import org.smartregister.chw.hiv.fragment.BaseHivRegisterFragment;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.listener.BottomNavigationListener;

/**
 * HIV Register.
 * This is the register for HIV Positive Clients.
 * HIV Positive clients will elicit their contact clients for HIV Testing
 */
public class HivRegisterActivity extends CoreHivRegisterActivity {

    public static void startHIVFormActivity(Activity activity, String baseEntityID, String formName, String payloadType) {
        Intent intent = new Intent(activity, HivRegisterActivity.class);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.BASE_ENTITY_ID, baseEntityID);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.ACTION, payloadType);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.HIV_REGISTRATION_FORM_NAME, formName);
        activity.startActivity(intent);
    }

    @NotNull
    @Override
    protected BaseHivRegisterFragment getRegisterFragment() {
        return new HivRegisterFragment();
    }

    @NotNull
    @Override
    protected BaseHivCommunityFollowupRegisterFragment[] getOtherFragments() {
        return new BaseHivCommunityFollowupRegisterFragment[0];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);

        if (bottomNavigationView != null) {
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_clients);
            bottomNavigationView.getMenu().removeItem(org.smartregister.chw.hiv.R.id.action_register);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_search);
            bottomNavigationView.getMenu().removeItem(org.smartregister.R.id.action_library);
            bottomNavigationView.getMenu().removeItem(org.smartregister.chw.hiv.R.id.action_received_referrals);

            bottomNavigationView.inflateMenu(getMenuResource());
            bottomNavigationView.getMenu().removeItem(org.smartregister.chw.hiv.R.id.action_received_referrals);
            bottomNavigationHelper.disableShiftMode(bottomNavigationView);

            BottomNavigationListener hivBottomNavigationListener = getBottomNavigation(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(hivBottomNavigationListener);

        }
    }

    @Override
    public void startFormActivity(@Nullable String formName, @Nullable String entityId, @Nullable String metaData) {
        Intent intent = new Intent(this, HivFormsActivity.class);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.BASE_ENTITY_ID, entityId);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.JSON_FORM, metaData);
        intent.putExtra(org.smartregister.chw.hiv.util.Constants.ActivityPayload.USE_DEFAULT_NEAT_FORM_LAYOUT, false);

        this.startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_FILTERS) {
            ((HivRegisterFragment) mBaseFragment).onFiltersUpdated(requestCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
 