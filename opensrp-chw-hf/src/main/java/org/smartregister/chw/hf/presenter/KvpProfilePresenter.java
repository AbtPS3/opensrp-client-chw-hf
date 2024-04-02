package org.smartregister.chw.hf.presenter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.Context;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.activity.KvpProfileActivity;
import org.smartregister.chw.hf.interactor.KvpProfileInteractor;
import org.smartregister.chw.hf.model.HfAllClientsRegisterModel;
import org.smartregister.chw.kvp.contract.KvpProfileContract;
import org.smartregister.chw.kvp.domain.MemberObject;
import org.smartregister.chw.kvp.presenter.BaseKvpProfilePresenter;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.domain.UniqueId;
import org.smartregister.family.contract.FamilyProfileContract;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojo.OpdDiagnosisAndTreatmentForm;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.pojo.RegisterParams;

import java.util.List;

import timber.log.Timber;

public class KvpProfilePresenter extends BaseKvpProfilePresenter implements FamilyProfileContract.InteractorCallBack, OpdRegisterActivityContract.InteractorCallBack {
    private android.content.Context context;
    private HfAllClientsRegisterModel model;

    public KvpProfilePresenter(KvpProfileContract.View view, KvpProfileContract.Interactor interactor, MemberObject memberObject) {
        super(view, interactor, memberObject);
        context = (android.content.Context) view;
        this.model = new HfAllClientsRegisterModel(context);
    }

    public void startIndexContactRegistrationForm(String locationId) {
        Triple<String, String, String> triple = Triple.of(CoreConstants.JSON_FORM.getHivIndexClientsContactsRegistrationForm(), null, locationId);
        UniqueId uniqueId = Context.getInstance().getUniqueIdRepository().getNextUniqueId();
        final String entityId = uniqueId != null ? uniqueId.getOpenmrsId() : "";
        if (StringUtils.isBlank(entityId)) {
            ((KvpProfileActivity) context).displayToast(org.smartregister.family.R.string.no_unique_id);
        } else {
            try {
                ((KvpProfileActivity) context).startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight());
            } catch (Exception e) {
                Timber.e(e);
                ((KvpProfileActivity) context).displayToast(org.smartregister.family.R.string.error_unable_to_start_form);
            }
        }
    }

    public void saveForm(String jsonString, @NonNull RegisterParams registerParams) {
        try {
            List<OpdEventClient> opdEventClientList = model.processRegistration(jsonString, registerParams.getFormTag());
            if (opdEventClientList == null || opdEventClientList.isEmpty()) {
                return;
            }
            ((KvpProfileInteractor) interactor).saveRegistration(opdEventClientList, jsonString, registerParams, memberObject.getBaseEntityId(), this);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void startFormForEdit(CommonPersonObjectClient commonPersonObjectClient) {

    }

    @Override
    public void refreshProfileTopSection(CommonPersonObjectClient commonPersonObjectClient) {

    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String s) {

    }

    @Override
    public void onRegistrationSaved(boolean b) {
        //Calling client processor to start processing events in the background
        CoreChildUtils.processClientProcessInBackground();
    }

    @Override
    public void onEventSaved() {

    }

    @Override
    public void onFetchedSavedDiagnosisAndTreatmentForm(@Nullable OpdDiagnosisAndTreatmentForm opdDiagnosisAndTreatmentForm, @NonNull String s, @Nullable String s1) {

    }

    @Override
    public void onNoUniqueId() {

    }

    @Override
    public void onRegistrationSaved(boolean b, boolean b1, FamilyEventClient familyEventClient) {
        //Calling client processor to start processing events in the background
        CoreChildUtils.processClientProcessInBackground();
    }
}
