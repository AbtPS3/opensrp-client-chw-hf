package org.smartregister.chw.hf.interactor;

import android.content.Context;

import org.smartregister.chw.hf.utils.Constants;
import org.smartregister.chw.ld.LDLibrary;
import org.smartregister.chw.ld.contract.BaseLDVisitContract;
import org.smartregister.chw.ld.domain.MemberObject;
import org.smartregister.chw.ld.domain.Visit;
import org.smartregister.chw.ld.domain.VisitDetail;
import org.smartregister.chw.ld.model.BaseLDVisitAction;
import org.smartregister.chw.ld.util.VisitUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author issyzac 5/7/22
 */
public class LDPartographInteractorFlv implements LDPartographInteractor.Flavor {

    private String baseEntityId;

    LinkedHashMap<String, BaseLDVisitAction> actionList = new LinkedHashMap<>();

    public LDPartographInteractorFlv(String baseEntityId){
        this.baseEntityId = baseEntityId;
    }


    @Override
    public LinkedHashMap<String, BaseLDVisitAction> calculateActions(BaseLDVisitContract.View view, MemberObject memberObject, BaseLDVisitContract.InteractorCallBack callBack) throws BaseLDVisitAction.ValidationException {

        Context context = view.getContext();
        Map<String, List<VisitDetail>> details = null;

        //get preloaded data
        if (view.getEditMode()){
            Visit lastVisit = LDLibrary.getInstance().visitRepository().getLatestVisit(memberObject.getBaseEntityId(), Constants.Events.LD_PARTOGRAPHY);
            if (lastVisit != null){
                details = VisitUtils.getVisitGroups(LDLibrary.getInstance().visitDetailsRepository().getVisits(lastVisit.getVisitId()));
            }
        }

        evaluateFetalWellbeing(actionList, details, memberObject, context, callBack);
        evaluateMotherWellbeing(actionList, details, memberObject, context, callBack);
        evaluateProgressOfLabour(actionList, details, memberObject, context, callBack);
        evaluateTreatmentDuringLabor(actionList, details, memberObject, context, callBack);

        return actionList;
    }

    private void evaluateFetalWellbeing(LinkedHashMap<String, BaseLDVisitAction> actionList,
                                        Map<String, List<VisitDetail>> details,
                                        final MemberObject memberObject,
                                        final Context context,
                                        BaseLDVisitContract.InteractorCallBack callBack
    ) throws BaseLDVisitAction.ValidationException {

    }

    private void evaluateMotherWellbeing(LinkedHashMap<String, BaseLDVisitAction> actionList,
                                        Map<String, List<VisitDetail>> details,
                                        final MemberObject memberObject,
                                        final Context context,
                                        BaseLDVisitContract.InteractorCallBack callBack
    ) throws BaseLDVisitAction.ValidationException {

    }

    private void evaluateProgressOfLabour(LinkedHashMap<String, BaseLDVisitAction> actionList,
                                        Map<String, List<VisitDetail>> details,
                                        final MemberObject memberObject,
                                        final Context context,
                                        BaseLDVisitContract.InteractorCallBack callBack
    ) throws BaseLDVisitAction.ValidationException {

    }

    private void evaluateTreatmentDuringLabor(LinkedHashMap<String, BaseLDVisitAction> actionList,
                                        Map<String, List<VisitDetail>> details,
                                        final MemberObject memberObject,
                                        final Context context,
                                        BaseLDVisitContract.InteractorCallBack callBack
    ) throws BaseLDVisitAction.ValidationException {

    }

}
