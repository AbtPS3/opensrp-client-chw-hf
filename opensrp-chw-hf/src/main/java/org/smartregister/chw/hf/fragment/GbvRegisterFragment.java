package org.smartregister.chw.hf.fragment;

import org.smartregister.chw.core.fragment.CoreGbvRegisterFragment;
import org.smartregister.chw.gbv.presenter.BaseGbvRegisterFragmentPresenter;
import org.smartregister.chw.hf.activity.GbvMemberProfileActivity;
import org.smartregister.chw.hf.model.GbvRegisterFragmentModel;

public class GbvRegisterFragment extends CoreGbvRegisterFragment {
    @Override
    protected void openProfile(String baseEntityId) {
        GbvMemberProfileActivity.startMe(getActivity(), baseEntityId);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new BaseGbvRegisterFragmentPresenter(this, new GbvRegisterFragmentModel(), null);
    }

}
