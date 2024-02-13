package org.smartregister.chw.hf.utils;

import static org.smartregister.chw.core.utils.CoreConstants.INTENT_KEY.CLIENT;
import static org.smartregister.chw.core.utils.Utils.passToolbarTitle;
import static org.smartregister.opd.utils.OpdDbConstants.KEY.REGISTER_TYPE;
import static org.smartregister.util.Utils.showShortToast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.activity.AboveFiveChildProfileActivity;
import org.smartregister.chw.hf.activity.AllClientsMemberProfileActivity;
import org.smartregister.chw.hf.activity.AncMemberProfileActivity;
import org.smartregister.chw.hf.activity.ChildProfileActivity;
import org.smartregister.chw.hf.activity.FamilyOtherMemberProfileActivity;
import org.smartregister.chw.hf.activity.FpMemberProfileActivity;
import org.smartregister.chw.hf.activity.GbvMemberProfileActivity;
import org.smartregister.chw.hf.activity.HivProfileActivity;
import org.smartregister.chw.hf.activity.KvpProfileActivity;
import org.smartregister.chw.hf.activity.LDProfileActivity;
import org.smartregister.chw.hf.activity.MalariaProfileActivity;
import org.smartregister.chw.hf.activity.PncMemberProfileActivity;
import org.smartregister.chw.hf.activity.TbProfileActivity;
import org.smartregister.chw.hf.activity.VmmcProfileActivity;
import org.smartregister.chw.hf.dao.FamilyDao;
import org.smartregister.chw.hf.dao.HfHivDao;
import org.smartregister.chw.hf.dao.HfHtsDao;
import org.smartregister.chw.hf.model.FamilyDetailsModel;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.pmtct.dao.PmtctDao;
import org.smartregister.chw.tb.dao.TbDao;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.family.domain.FamilyEventClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.pojo.OpdEventClient;
import org.smartregister.opd.utils.OpdDbConstants;

import java.util.ArrayList;
import java.util.List;

public class AllClientsUtils {

    public static void goToClientProfile(Activity activity, @NonNull CommonPersonObjectClient commonPersonObjectClient) {
        String registerType = commonPersonObjectClient.getDetails().get(REGISTER_TYPE);

        Bundle bundle = new Bundle();
        FamilyDetailsModel familyDetailsModel = FamilyDao.getFamilyDetail(commonPersonObjectClient.entityId());

        if (familyDetailsModel != null) {
            bundle.putString(Constants.INTENT_KEY.FAMILY_BASE_ENTITY_ID, familyDetailsModel.getBaseEntityId());
            bundle.putString(Constants.INTENT_KEY.FAMILY_HEAD, familyDetailsModel.getFamilyHead());
            bundle.putString(Constants.INTENT_KEY.PRIMARY_CAREGIVER, familyDetailsModel.getPrimaryCareGiver());
            bundle.putString(Constants.INTENT_KEY.FAMILY_NAME, familyDetailsModel.getFamilyName());
            bundle.putString(Constants.INTENT_KEY.VILLAGE_TOWN, commonPersonObjectClient.getDetails().get(OpdDbConstants.KEY.HOME_ADDRESS));
        }

        if (registerType != null) {
            switch (registerType) {
                case CoreConstants.REGISTER_TYPE.CHILD:
                    AllClientsUtils.goToChildProfile(activity, commonPersonObjectClient, bundle);
                    break;
                case CoreConstants.REGISTER_TYPE.ANC:
                    AllClientsUtils.goToAncProfile(activity, commonPersonObjectClient);
                    break;
                case CoreConstants.REGISTER_TYPE.PNC:
                    AllClientsUtils.gotToPncProfile(activity, commonPersonObjectClient, bundle);
                    break;
                case CoreConstants.REGISTER_TYPE.MALARIA:
                    AllClientsUtils.gotToMalariaProfile(activity, commonPersonObjectClient);
                    break;
                case CoreConstants.REGISTER_TYPE.FAMILY_PLANNING:
                    AllClientsUtils.goToFamilyPlanningProfile(activity, commonPersonObjectClient);
                    break;
                case CoreConstants.REGISTER_TYPE.LD:
                    AllClientsUtils.goToLDProfile(activity, commonPersonObjectClient);
                    break;
                case CoreConstants.REGISTER_TYPE.KVP:
                    AllClientsUtils.goToKVPProfile(activity, commonPersonObjectClient);
                    break;
                case CoreConstants.REGISTER_TYPE.VMMC:
                    AllClientsUtils.goToVmmcProfile(activity, commonPersonObjectClient);
                    break;
                case CoreConstants.REGISTER_TYPE.GBV:
                    AllClientsUtils.goToGbvProfile(activity, commonPersonObjectClient);
                    break;
                default:
                    AllClientsUtils.goToOtherMemberProfile(activity, commonPersonObjectClient, bundle,
                            familyDetailsModel.getFamilyHead(), familyDetailsModel.getPrimaryCareGiver());
                    break;
            }
        } else {
            if (familyDetailsModel != null) {
                AllClientsUtils.goToOtherMemberProfile(activity, commonPersonObjectClient, bundle,
                        familyDetailsModel.getFamilyHead(), familyDetailsModel.getPrimaryCareGiver());
            }
        }
    }

    public static void goToChildProfile(Activity activity, CommonPersonObjectClient patient, Bundle bundle) {
        String dobString = Utils.getDuration(Utils.getValue(patient.getColumnmaps(), DBConstants.KEY.DOB, false));
        Integer yearOfBirth = CoreChildUtils.dobStringToYear(dobString);
        Intent intent;
        if (yearOfBirth != null && yearOfBirth >= 5) {
            intent = new Intent(activity, AboveFiveChildProfileActivity.class);
        } else {
            intent = new Intent(activity, ChildProfileActivity.class);
        }
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        passToolbarTitle(activity, intent);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, new MemberObject(patient));
        activity.startActivity(intent);
    }

    public static void gotToPncProfile(Activity activity, CommonPersonObjectClient patient, Bundle bundle) {
        patient.getColumnmaps().putAll(CoreChwApplication.pncRegisterRepository().getPncCommonPersonObject(patient.entityId()).getColumnmaps());
        activity.startActivity(initProfileActivityIntent(activity, patient, bundle, PncMemberProfileActivity.class));
    }

    public static void goToAncProfile(Activity activity, CommonPersonObjectClient patient) {
        AncMemberProfileActivity.startMe(activity, patient.getCaseId());
    }

    public static void gotToMalariaProfile(Activity activity, CommonPersonObjectClient patient) {
        MalariaProfileActivity.startMalariaActivity(activity, patient.getCaseId());
    }

    public static void goToFamilyPlanningProfile(Activity activity, CommonPersonObjectClient patient) {
        FpMemberProfileActivity.startFpMemberProfileActivity(activity, patient.getCaseId());
    }

    public static void goToLDProfile(Activity activity, CommonPersonObjectClient patient) {
        LDProfileActivity.startProfileActivity(activity, patient.getCaseId());
    }

    public static void goToKVPProfile(Activity activity, CommonPersonObjectClient patient) {
        KvpProfileActivity.startProfile(activity, patient.getCaseId());
    }

    public static void goToVmmcProfile(Activity activity, CommonPersonObjectClient patient) {
        VmmcProfileActivity.startVmmcActivity(activity, patient.getCaseId());
    }

    public static void goToGbvProfile(Activity activity, CommonPersonObjectClient client) {
        GbvMemberProfileActivity.startMe(activity, client.getCaseId());
    }

    private static Intent initProfileActivityIntent(Activity activity, CommonPersonObjectClient patient, Bundle bundle, Class clazz) {
        Intent intent = new Intent(activity, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.BASE_ENTITY_ID, patient.entityId());
        intent.putExtra(CLIENT, patient);
        passToolbarTitle(activity, intent);
        return intent;
    }

    public static void goToOtherMemberProfile(Activity activity, CommonPersonObjectClient patient,
                                              Bundle bundle, String familyHead, String primaryCaregiver) {

        if (StringUtils.isBlank(familyHead) && StringUtils.isBlank(primaryCaregiver)) {
            showShortToast(activity, activity.getString(R.string.error_opening_profile));
        } else {
            String registerType = patient.getDetails().get(REGISTER_TYPE);
            Intent intent;
            if (CoreConstants.REGISTER_TYPE.INDEPENDENT.equals(registerType)) {
                intent = new Intent(activity, AllClientsMemberProfileActivity.class);
            } else {
                intent = new Intent(activity, FamilyOtherMemberProfileActivity.class);
            }
            intent.putExtras(bundle != null ? bundle : new Bundle());
            intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
            intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
            intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, familyHead);
            intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, primaryCaregiver);
            intent.putExtra(Constants.INTENT_KEY.VILLAGE_TOWN, patient.getDetails().get(OpdDbConstants.KEY.HOME_ADDRESS));
            passToolbarTitle(activity, intent);
            activity.startActivity(intent);
        }
    }


    @NotNull
    public static List<OpdEventClient> getOpdEventClients(String jsonString) {
        List<OpdEventClient> allClientMemberEvents = new ArrayList<>();

        FamilyEventClient locationDetailsEvent = JsonFormUtils.processFamilyUpdateForm(
                Utils.context().allSharedPreferences(), jsonString);
        if (locationDetailsEvent == null) {
            return allClientMemberEvents;
        }

        FamilyEventClient clientDetailsEvent = JsonFormUtils.processFamilyHeadRegistrationForm(
                Utils.context().allSharedPreferences(), jsonString, locationDetailsEvent.getClient().getBaseEntityId());
        if (clientDetailsEvent == null) {
            return allClientMemberEvents;
        }

        if (clientDetailsEvent.getClient() != null && locationDetailsEvent.getClient() != null) {
            String headUniqueId = clientDetailsEvent.getClient().getIdentifier(Utils.metadata().uniqueIdentifierKey);
            if (StringUtils.isNotBlank(headUniqueId)) {
                String familyUniqueId = headUniqueId + Constants.IDENTIFIER.FAMILY_SUFFIX;
                locationDetailsEvent.getClient().addIdentifier(Utils.metadata().uniqueIdentifierKey, familyUniqueId);
            }
        }

        // Update the family head and primary caregiver
        Client familyClient = locationDetailsEvent.getClient();
        familyClient.addRelationship(Utils.metadata().familyRegister.familyHeadRelationKey, clientDetailsEvent.getClient().getBaseEntityId());
        familyClient.addRelationship(Utils.metadata().familyRegister.familyCareGiverRelationKey, clientDetailsEvent.getClient().getBaseEntityId());

        //Use different entity type for independent members
        locationDetailsEvent.getEvent().setEntityType(CoreConstants.TABLE_NAME.INDEPENDENT_CLIENT);
        clientDetailsEvent.getEvent().setEntityType(CoreConstants.TABLE_NAME.INDEPENDENT_CLIENT);

        allClientMemberEvents.add(new OpdEventClient(locationDetailsEvent.getClient(), locationDetailsEvent.getEvent()));
        allClientMemberEvents.add(new OpdEventClient(clientDetailsEvent.getClient(), clientDetailsEvent.getEvent()));
        return allClientMemberEvents;
    }

    public static void updateHivMenuItems(String baseEntityId, Menu menu) {
        if (HfHivDao.isHivMember(baseEntityId)) {
            menu.findItem(R.id.action_hiv_registration).setVisible(false);
        } else {
            menu.findItem(R.id.action_hiv_registration).setVisible(true);
        }
    }

    public static void updateTbMenuItems(String baseEntityId, Menu menu) {
        if (TbDao.isRegisteredForTb(baseEntityId)) {
            menu.findItem(R.id.action_tb_registration).setVisible(false);
        } else {
            menu.findItem(R.id.action_tb_registration).setVisible(true);
        }
    }

    public static void goToHivProfile(FragmentActivity activity, CommonPersonObjectClient hivClient) {
        HivProfileActivity.startHivProfileActivity(activity, HivDao.getMember(hivClient.getCaseId()));
    }

    public static void goToHTsProfile(FragmentActivity activity, CommonPersonObjectClient hivClient) {
        HivProfileActivity.startHivProfileActivity(activity, HfHtsDao.getMember(hivClient.getCaseId()));
    }

    public static void goToTbProfile(FragmentActivity activity, CommonPersonObjectClient tbClient) {
        TbProfileActivity.startTbProfileActivity(activity, TbDao.getMember(tbClient.getCaseId()));
    }

    public static void updatePmtctMenuItems(String baseEntityId, Menu menu) {
        menu.findItem(R.id.action_pmtct_register).setVisible(!PmtctDao.isRegisteredForPmtct(baseEntityId));
    }

    public static String getClientGender(String baseEntityId) {
        CommonRepository commonRepository = Utils.context().commonrepository(Utils.metadata().familyMemberRegister.tableName);

        final CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(baseEntityId);
        final CommonPersonObjectClient client = new CommonPersonObjectClient(commonPersonObject.getCaseId(), commonPersonObject.getDetails(), "");
        client.setColumnmaps(commonPersonObject.getColumnmaps());
        return Utils.getValue(commonPersonObject.getColumnmaps(), DBConstants.KEY.GENDER, false);
    }
}
