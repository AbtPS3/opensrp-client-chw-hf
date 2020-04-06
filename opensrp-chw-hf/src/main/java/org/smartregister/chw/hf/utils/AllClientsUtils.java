package org.smartregister.chw.hf.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.dao.AncDao;
import org.smartregister.chw.core.dao.PNCDao;
import org.smartregister.chw.core.utils.CoreChildUtils;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.activity.AboveFiveChildProfileActivity;
import org.smartregister.chw.hf.activity.AncMemberProfileActivity;
import org.smartregister.chw.hf.activity.ChildProfileActivity;
import org.smartregister.chw.hf.activity.FamilyOtherMemberProfileActivity;
import org.smartregister.chw.hf.activity.PncMemberProfileActivity;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.family.util.Constants;
import org.smartregister.family.util.DBConstants;
import org.smartregister.family.util.Utils;
import org.smartregister.opd.utils.OpdDbConstants;

public class AllClientsUtils {

    public static void goToAdultMemberProfile(Activity activity, CommonPersonObjectClient commonPersonObjectClient, Bundle bundle) {
        if (AncDao.isANCMember(commonPersonObjectClient.entityId())) {
            goToAncProfile(activity, commonPersonObjectClient, bundle);
        } else if (PNCDao.isPNCMember(commonPersonObjectClient.entityId())) {
            gotToPncProfile(activity, commonPersonObjectClient, bundle);
        } else {
            goToOtherMemberProfile(activity, commonPersonObjectClient, bundle, "", "");
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
        intent.putExtra(CoreConstants.INTENT_KEY.IS_COMES_FROM_FAMILY, false);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.MEMBER_PROFILE_OBJECT, new MemberObject(patient));
        activity.startActivity(intent);
    }

    public static void goToOtherMemberProfile(Activity activity, CommonPersonObjectClient patient,
                                              Bundle bundle, String familyHead, String primaryCaregiver) {
        Intent intent = new Intent(activity, FamilyOtherMemberProfileActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        intent.putExtra(CoreConstants.INTENT_KEY.CHILD_COMMON_PERSON, patient);
        intent.putExtra(Constants.INTENT_KEY.FAMILY_HEAD, familyHead);
        intent.putExtra(Constants.INTENT_KEY.PRIMARY_CAREGIVER, primaryCaregiver);
        intent.putExtra(Constants.INTENT_KEY.VILLAGE_TOWN, patient.getDetails().get(OpdDbConstants.KEY.HOME_ADDRESS));
        activity.startActivity(intent);
    }

    private static void gotToPncProfile(Activity activity, CommonPersonObjectClient patient, Bundle bundle) {
        patient.getColumnmaps().putAll(CoreChwApplication.pncRegisterRepository().getPncCommonPersonObject(patient.entityId()).getColumnmaps());
        activity.startActivity(initProfileActivityIntent(activity, patient, bundle, PncMemberProfileActivity.class));
    }

    private static void goToAncProfile(Activity activity, CommonPersonObjectClient patient, Bundle bundle) {
        patient.getColumnmaps().putAll(CoreChwApplication.ancRegisterRepository().getAncCommonPersonObject(patient.entityId()).getColumnmaps());
        activity.startActivity(initProfileActivityIntent(activity, patient, bundle, AncMemberProfileActivity.class));
    }

    private static Intent initProfileActivityIntent(Activity activity, CommonPersonObjectClient patient, Bundle bundle, Class clazz) {
        Intent intent = new Intent(activity, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.BASE_ENTITY_ID, patient.entityId());
        intent.putExtra(CoreConstants.INTENT_KEY.CLIENT, patient);
        intent.putExtra(org.smartregister.chw.anc.util.Constants.ANC_MEMBER_OBJECTS.TITLE_VIEW_TEXT, R.string.return_to_all_client);
        return intent;
    }
}
