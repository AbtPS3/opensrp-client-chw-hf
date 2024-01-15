package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.widget.LinearLayout;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.core.listener.OnClickFloatingMenu;
import org.smartregister.chw.gbv.GbvLibrary;
import org.smartregister.chw.gbv.activity.BaseGbvProfileActivity;
import org.smartregister.chw.gbv.domain.MemberObject;
import org.smartregister.chw.gbv.util.Constants;
import org.smartregister.chw.gbv.util.VisitUtils;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.custom_view.GbvFloatingMenu;

import timber.log.Timber;

public class GbvMemberProfileActivity extends BaseGbvProfileActivity {

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
            VisitUtils.processVisits(GbvLibrary.getInstance().visitRepository(), GbvLibrary.getInstance().visitDetailsRepository(), GbvMemberProfileActivity.this);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void openMedicalHistory() {

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
}
