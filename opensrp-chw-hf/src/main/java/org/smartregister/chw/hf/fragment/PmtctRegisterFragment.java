package org.smartregister.chw.hf.fragment;

import org.smartregister.chw.core.fragment.CorePmtctRegisterFragment;
import org.smartregister.chw.hf.activity.PmtctProfileActivity;
import org.smartregister.chw.hf.activity.PmtctRegisterActivity;
import org.smartregister.chw.hf.model.PmtctRegisterFragmentModel;
import org.smartregister.chw.hf.presenter.PmtctRegisterFragmentPresenter;

import timber.log.Timber;

public class PmtctRegisterFragment extends CorePmtctRegisterFragment {
    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = null;
        try {
            viewConfigurationIdentifier = ((PmtctRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        } catch (Exception e) {
            Timber.e(e);
        }

        presenter = new PmtctRegisterFragmentPresenter(this, new PmtctRegisterFragmentModel(), viewConfigurationIdentifier);

    }

    @Override
    protected void openProfile(String baseEntityId) {
       PmtctProfileActivity.startPmtctActivity(getActivity(), baseEntityId);
    }

    @Override
    protected void openFollowUpVisit(String baseEntityId) {
      //  PmtctFollowUpVisitActivity.startPmtctFollowUpActivity(getActivity(),baseEntityId);
    }
}
