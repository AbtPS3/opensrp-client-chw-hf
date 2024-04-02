package org.smartregister.chw.hf.utils;

import org.json.JSONObject;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.BuildConfig;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.family.FamilyLibrary;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;
import org.smartregister.sync.helper.ECSyncHelper;

import java.util.ArrayList;
import java.util.Date;

import timber.log.Timber;

public class HfHivFormUtils {
    public static void saveRegisterHivIndexEvent(OpdEventClient opdEventClient, String hivClientBaseEntityId, String contactClientBaseEntityId, String locationId) {
        try {
            AllSharedPreferences sharedPreferences = Utils.getAllSharedPreferences();
            ECSyncHelper syncHelper = FamilyLibrary.getInstance().getEcSyncHelper();
            Event baseEvent = (Event) new Event()
                    .withBaseEntityId(contactClientBaseEntityId)
                    .withEventDate(new Date())
                    .withEventType(CoreConstants.EventType.HIV_INDEX_CONTACT_REGISTRATION)
                    .withFormSubmissionId(org.smartregister.util.JsonFormUtils.generateRandomUUIDString())
                    .withEntityType(CoreConstants.TABLE_NAME.HIV_INDEX)
                    .withProviderId(sharedPreferences.fetchRegisteredANM())
                    .withLocationId(locationId)
                    .withTeamId(sharedPreferences.fetchDefaultTeamId(sharedPreferences.fetchRegisteredANM()))
                    .withTeam(sharedPreferences.fetchDefaultTeam(sharedPreferences.fetchRegisteredANM()))
                    .withClientDatabaseVersion(BuildConfig.DATABASE_VERSION)
                    .withClientApplicationVersion(BuildConfig.VERSION_CODE)
                    .withDateCreated(new Date());

            baseEvent.setObs(opdEventClient.getEvent().getObs());
            baseEvent.addObs((new Obs()).withFormSubmissionField(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.INDEX_CLIENT_BASE_ENTITY_ID).withValue(hivClientBaseEntityId)
                    .withFieldCode(CoreConstants.FORM_CONSTANTS.FORM_SUBMISSION_FIELD.INDEX_CLIENT_BASE_ENTITY_ID).withFieldType("formsubmissionField").withFieldDataType("text").withParentCode("").withHumanReadableValues(new ArrayList<>()));


            org.smartregister.chw.hf.utils.JsonFormUtils.tagSyncMetadata(Utils.context().allSharedPreferences(), baseEvent);// tag docs

            //setting the location uuid of the referral initiator so that to allow the event to sync back to the chw app since it sync data by location.
            baseEvent.setLocationId(locationId);

            JSONObject eventJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(baseEvent));
            syncHelper.addEvent(hivClientBaseEntityId, eventJson);
            long lastSyncTimeStamp = HealthFacilityApplication.getInstance().getContext().allSharedPreferences().fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);
            HealthFacilityApplication.getClientProcessor(HealthFacilityApplication.getInstance().getContext().applicationContext()).processClient(syncHelper.getEvents(lastSyncDate, BaseRepository.TYPE_Unprocessed));
            HealthFacilityApplication.getInstance().getContext().allSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e, "HfHivProfileInteractor --> saveRegisterIndexClientEvent");
        }

    }
}
