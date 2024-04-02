package org.smartregister.chw.hf.interactor;

import static org.smartregister.chw.hf.utils.HfHivFormUtils.saveRegisterHivIndexEvent;

import org.smartregister.chw.kvp.interactor.BaseKvpProfileInteractor;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.pojo.RegisterParams;

import java.util.List;

public class KvpProfileInteractor extends BaseKvpProfileInteractor {

    public void saveRegistration(final List<OpdEventClient> opdEventClientList, final String jsonString,
                                 final RegisterParams registerParams, final String baseEntityId, final OpdRegisterActivityContract.InteractorCallBack callBack) {
        for (OpdEventClient opdEventClient : opdEventClientList) {
            if (!opdEventClient.getClient().getIdentifier("opensrp_id").contains("family")) {
                saveRegisterHivIndexEvent(opdEventClient, baseEntityId, opdEventClient.getClient().getBaseEntityId(), opdEventClient.getEvent().getLocationId());
            }
        }

        HfAllClientsRegisterInteractor hfAllClientsRegisterInteractor = new HfAllClientsRegisterInteractor();
        hfAllClientsRegisterInteractor.saveRegistration(opdEventClientList, jsonString, registerParams, callBack);
    }


}
