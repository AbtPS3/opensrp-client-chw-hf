package org.smartregister.chw.hf.interactor;

import static org.smartregister.chw.hf.utils.HfHivFormUtils.saveRegisterHivIndexEvent;

import android.content.Context;

import org.apache.commons.lang3.tuple.Triple;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.interactor.CoreHivProfileInteractor;
import org.smartregister.chw.core.repository.ChwTaskRepository;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.contract.HivProfileContract;
import org.smartregister.chw.hf.dao.HfFollowupFeedbackDao;
import org.smartregister.chw.hf.model.ChwFollowupFeedbackDetailsModel;
import org.smartregister.chw.hf.model.HivTbReferralTasksAndFollowupFeedbackModel;
import org.smartregister.chw.hiv.contract.BaseHivProfileContract;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.domain.Task;
import org.smartregister.opd.contract.OpdRegisterActivityContract;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.pojo.RegisterParams;
import org.smartregister.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HfHivProfileInteractor extends CoreHivProfileInteractor implements HivProfileContract.Interactor {

    private HfAllClientsRegisterInteractor hfAllClientsRegisterInteractor;

    public HfHivProfileInteractor(Context context) {
        super(context);
        hfAllClientsRegisterInteractor = new HfAllClientsRegisterInteractor();
    }

    @Override
    public void getReferralTasks(String planId, String baseEntityId, HivProfileContract.InteractorCallback callback) {
        List<HivTbReferralTasksAndFollowupFeedbackModel> tasksAndFollowupFeedbackModels = new ArrayList<>();
        TaskRepository taskRepository = CoreChwApplication.getInstance().getTaskRepository();
        Set<Task> taskList = ((ChwTaskRepository) taskRepository).getReferralTasksForClientByStatus(planId, baseEntityId, CoreConstants.BUSINESS_STATUS.REFERRED);

        for (Task task : taskList) {
            if (!task.getFocus().equalsIgnoreCase("LTFU")) {
                HivTbReferralTasksAndFollowupFeedbackModel tasksAndFollowupFeedbackModel = new HivTbReferralTasksAndFollowupFeedbackModel();
                tasksAndFollowupFeedbackModel.setTask(task);
                tasksAndFollowupFeedbackModel.setType("TASK");
                tasksAndFollowupFeedbackModels.add(tasksAndFollowupFeedbackModel);
            }
        }

        List<ChwFollowupFeedbackDetailsModel> followupFeedbackList = HfFollowupFeedbackDao.getHivFollowupFeedback(baseEntityId);

        for (ChwFollowupFeedbackDetailsModel followupFeedbackDetailsModel : followupFeedbackList) {
            HivTbReferralTasksAndFollowupFeedbackModel tasksAndFollowupFeedbackModel = new HivTbReferralTasksAndFollowupFeedbackModel();
            tasksAndFollowupFeedbackModel.setFollowupFeedbackDetailsModel(followupFeedbackDetailsModel);
            tasksAndFollowupFeedbackModel.setType("FOLLOWUP_FEEDBACK");
            tasksAndFollowupFeedbackModels.add(tasksAndFollowupFeedbackModel);
        }


        callback.updateReferralTasksAndFollowupFeedback(tasksAndFollowupFeedbackModels);
    }

    @Override
    public void getNextUniqueId(final Triple<String, String, String> triple, final OpdRegisterActivityContract.InteractorCallBack callBack) {
        hfAllClientsRegisterInteractor.getNextUniqueId(triple, callBack);
    }

    @Override
    public void saveRegistration(final List<OpdEventClient> opdEventClientList, final String jsonString,
                                 final RegisterParams registerParams, final HivMemberObject hivMemberObject, final OpdRegisterActivityContract.InteractorCallBack callBack) {
        for (OpdEventClient opdEventClient : opdEventClientList) {
            if (!opdEventClient.getClient().getIdentifier("opensrp_id").contains("family")) {
                saveRegisterHivIndexEvent(opdEventClient, hivMemberObject.getBaseEntityId(), opdEventClient.getClient().getBaseEntityId(), opdEventClient.getEvent().getLocationId());
            }
        }
        hfAllClientsRegisterInteractor.saveRegistration(opdEventClientList, jsonString, registerParams, callBack);
    }


    @Override
    public void updateProfileHivStatusInfo(HivMemberObject memberObject, BaseHivProfileContract.InteractorCallback callback) {
        //overriding updateProfileHivStatusInfo
    }
}
