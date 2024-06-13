package org.smartregister.chw.hf.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.AncLibrary;
import org.smartregister.chw.anc.dao.HomeVisitDao;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.gbv.GbvLibrary;
import org.smartregister.chw.gbv.dao.GbvDao;
import org.smartregister.chw.gbv.domain.Visit;
import org.smartregister.chw.gbv.repository.VisitDetailsRepository;
import org.smartregister.chw.gbv.repository.VisitRepository;
import org.smartregister.chw.gbv.util.VisitUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.immunization.ImmunizationLibrary;
import org.smartregister.repository.AllSharedPreferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class GbvVisitUtils extends VisitUtils {
    public static void processVisits() throws Exception {
        processVisits(GbvLibrary.getInstance().visitRepository(), GbvLibrary.getInstance().visitDetailsRepository());
    }

    public static void processVisits(VisitRepository visitRepository, VisitDetailsRepository visitDetailsRepository) throws Exception {
        List<Visit> visits = visitRepository.getAllUnSynced();
        List<Visit> gbvVisits = new ArrayList<>();

        for (Visit v : visits) {
            Date visitDate = new Date(v.getDate().getTime());
            int daysDiff = TimeUtils.getElapsedDays(visitDate);
            if (daysDiff >= 1 && v.getVisitType().equalsIgnoreCase(org.smartregister.chw.gbv.util.Constants.EVENT_TYPE.GBV_FOLLOW_UP_VISIT) && isVisitComplete(v)) {
                gbvVisits.add(v);
            }
        }

        if (!gbvVisits.isEmpty()) {
            processVisits(gbvVisits, visitRepository, visitDetailsRepository);
        }
    }

    public static boolean isVisitComplete(Visit v) {
        try {
            JSONObject jsonObject = new JSONObject(v.getJson());
            JSONArray obs = jsonObject.getJSONArray("obs");
            List<Boolean> checks = new ArrayList<Boolean>();

            boolean isVisitTypeComplete = computeCompletionStatus(obs, "visit_status");
            checks.add(isVisitTypeComplete);

            if (canManageCase(v)) {
                boolean isConsentComplete = computeCompletionStatus(obs, "client_consent");
                checks.add(isConsentComplete);
                if (hasFollowupConsent(v)) {
                    boolean isConsentFollowupComplete = computeCompletionStatus(obs, "client_consent_after_counseling");
                    checks.add(isConsentFollowupComplete);
                    if (shouldProceedWithOtherChecks(v)) {
                        otherAdditionalChecks(v, checks, obs);
                    }
                } else {
                    otherAdditionalChecks(v, checks, obs);
                }
            }

            if (!checks.contains(false)) {
                return true;
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return false;
    }

    public static void otherAdditionalChecks(Visit v, List<Boolean> checks, JSONArray obs) throws JSONException {
        boolean isHistoryCollectionComplete = computeCompletionStatus(obs, "assault_date");
        checks.add(isHistoryCollectionComplete);

        boolean isMedicalExaminationComplete = computeCompletionStatus(obs, "clients_mental_state");
        checks.add(isMedicalExaminationComplete);

        boolean isPhysicalExaminationComplete = computeCompletionStatus(obs, "systolic");
        checks.add(isPhysicalExaminationComplete);

        boolean isForensicExaminationComplete = computeCompletionStatus(obs, "forensic_examination_done");
        checks.add(isForensicExaminationComplete);

        if (shouldProvideLabInvestigation(v)) {
            boolean isProvideTreatmentComplete = computeCompletionStatus(obs, "did_violence_cause_disability");
            checks.add(isProvideTreatmentComplete);
        }

        boolean isProvideTreatmentComplete = computeCompletionStatus(obs, "was_police_legal_and_social_services_required");
        checks.add(isProvideTreatmentComplete);

        boolean isEducationAndCounsellingComplete = computeCompletionStatus(obs, "was_the_survivor_educated_on_the_violence_that_occurred");
        checks.add(isEducationAndCounsellingComplete);

        if (GbvDao.getMember(v.getBaseEntityId()).getAge() > 7) {
            boolean isSafetyPlanComplete = computeCompletionStatus(obs, "has_safety_plan_been_done");
            checks.add(isSafetyPlanComplete);
        }

        boolean isLinkedToOtherServicesComplete = computeCompletionStatus(obs, "was_the_client_linked_to_other_services");
        checks.add(isLinkedToOtherServicesComplete);

        boolean doesTheClientRequireFollowupVisit = computeCompletionStatus(obs, "does_the_client_require_a_followup_visit");
        checks.add(doesTheClientRequireFollowupVisit);
    }

    public static boolean hasFollowupConsent(Visit visit) {
        boolean hasFollowupConsent = false;
        try {
            JSONObject jsonObject = new JSONObject(visit.getJson());
            JSONArray obs = jsonObject.getJSONArray("obs");
            int size = obs.length();
            for (int i = 0; i < size; i++) {
                JSONObject checkObj = obs.getJSONObject(i);
                if (checkObj.getString("fieldCode").equalsIgnoreCase("client_consent")) {
                    JSONArray values = checkObj.getJSONArray("values");
                    if ((values.getString(0).equalsIgnoreCase("no"))) {
                        hasFollowupConsent = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return hasFollowupConsent;
    }

    public static boolean canManageCase(Visit visit) {
        boolean canManageCase = false;
        try {
            JSONObject jsonObject = new JSONObject(visit.getJson());
            JSONArray obs = jsonObject.getJSONArray("obs");
            int size = obs.length();
            for (int i = 0; i < size; i++) {
                JSONObject checkObj = obs.getJSONObject(i);
                if (checkObj.getString("fieldCode").equalsIgnoreCase("can_manage_case")) {
                    JSONArray values = checkObj.getJSONArray("values");
                    if ((values.getString(0).equalsIgnoreCase("yes"))) {
                        canManageCase = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return canManageCase;
    }

    public static boolean shouldProceedWithOtherChecks(Visit visit) {
        boolean canManageCase = false;
        try {
            JSONObject jsonObject = new JSONObject(visit.getJson());
            JSONArray obs = jsonObject.getJSONArray("obs");
            int size = obs.length();
            for (int i = 0; i < size; i++) {
                JSONObject checkObj = obs.getJSONObject(i);
                if (checkObj.getString("fieldCode").equalsIgnoreCase("client_consent_after_counseling")) {
                    JSONArray values = checkObj.getJSONArray("values");
                    if ((values.getString(0).equalsIgnoreCase("yes"))) {
                        canManageCase = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return canManageCase;
    }

    public static boolean shouldProvideLabInvestigation(Visit visit) {
        boolean canManageCase = false;
        try {
            JSONObject jsonObject = new JSONObject(visit.getJson());
            JSONArray obs = jsonObject.getJSONArray("obs");
            int size = obs.length();
            for (int i = 0; i < size; i++) {
                JSONObject checkObj = obs.getJSONObject(i);
                if (checkObj.getString("fieldCode").equalsIgnoreCase("does_the_client_need_lab_investigation")) {
                    JSONArray values = checkObj.getJSONArray("values");
                    if ((values.getString(0).equalsIgnoreCase("yes"))) {
                        canManageCase = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return canManageCase;
    }


    public static void manualProcessVisit(Visit visit) throws Exception {
        List<Visit> manualProcessedVisits = new ArrayList<>();
        VisitDetailsRepository visitDetailsRepository = GbvLibrary.getInstance().visitDetailsRepository();
        VisitRepository visitRepository = GbvLibrary.getInstance().visitRepository();

        if (isVisitComplete(visit)) {
            manualProcessedVisits.add(visit);
        }

        if (manualProcessedVisits.size() > 0) {
            processVisits(manualProcessedVisits, visitRepository, visitDetailsRepository);
        }
    }

    public static boolean computeCompletionStatus(JSONArray obs, String checkString) throws JSONException {
        int size = obs.length();
        for (int i = 0; i < size; i++) {
            JSONObject checkObj = obs.getJSONObject(i);
            if (checkObj.getString("fieldCode").equalsIgnoreCase(checkString)) {
                return true;
            }
        }
        return false;
    }

    public static void deleteSavedEvent(AllSharedPreferences allSharedPreferences, String baseEntityId, String eventId, String formSubmissionId, String type) {
        Event event = (Event) new Event()
                .withBaseEntityId(baseEntityId)
                .withEventDate(new Date())
                .withEventType(org.smartregister.chw.anc.util.Constants.EVENT_TYPE.DELETE_EVENT)
                .withLocationId(org.smartregister.chw.anc.util.JsonFormUtils.locationId(allSharedPreferences))
                .withProviderId(allSharedPreferences.fetchRegisteredANM())
                .withEntityType(type)
                .withFormSubmissionId(UUID.randomUUID().toString())
                .withDateCreated(new Date());

        event.addDetails(org.smartregister.chw.anc.util.Constants.JSON_FORM_EXTRA.DELETE_EVENT_ID, eventId);
        event.addDetails(org.smartregister.chw.anc.util.Constants.JSON_FORM_EXTRA.DELETE_FORM_SUBMISSION_ID, formSubmissionId);

        try {
            NCUtils.processEvent(event.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(event)));
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static void deleteProcessedVisit(String visitID, String baseEntityId) {
        // check if the event
        AllSharedPreferences allSharedPreferences = ImmunizationLibrary.getInstance().context().allSharedPreferences();
        org.smartregister.chw.anc.domain.Visit visit = AncLibrary.getInstance().visitRepository().getVisitByVisitId(visitID);
        if (visit == null || !visit.getProcessed()) return;

        Event processedEvent = HomeVisitDao.getEventByFormSubmissionId(visit.getFormSubmissionId());
        if (processedEvent == null) return;

        GbvVisitUtils.deleteSavedEvent(allSharedPreferences, baseEntityId, processedEvent.getEventId(), processedEvent.getFormSubmissionId(), "event");
        AncLibrary.getInstance().visitRepository().deleteVisit(visitID);
    }

}
