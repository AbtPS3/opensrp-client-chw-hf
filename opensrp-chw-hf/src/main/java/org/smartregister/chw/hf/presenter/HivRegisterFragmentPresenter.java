package org.smartregister.chw.hf.presenter;

import android.content.Context;

import org.jetbrains.annotations.NotNull;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hiv.contract.BaseHivRegisterFragmentContract;
import org.smartregister.chw.hiv.presenter.BaseHivRegisterFragmentPresenter;
import org.smartregister.chw.hiv.util.Constants.Tables;
import org.smartregister.chw.hiv.util.DBConstants;

import java.text.MessageFormat;

public class HivRegisterFragmentPresenter extends BaseHivRegisterFragmentPresenter {

    public HivRegisterFragmentPresenter(BaseHivRegisterFragmentContract.View view, BaseHivRegisterFragmentContract.Model model, String viewConfigurationIdentifier) {
        super(view, model, viewConfigurationIdentifier);
    }

    @Override
    @NotNull
    public String getMainCondition() {
        return " " + CoreConstants.TABLE_NAME.FAMILY_MEMBER + "." + DBConstants.Key.DATE_REMOVED + " is null " +
                "AND UPPER (" + DBConstants.Key.CLIENT_HIV_STATUS_AFTER_TESTING + ") LIKE UPPER('Positive') " +
                "AND " + Tables.HIV + "." + DBConstants.Key.IS_CLOSED + " = '0' ";

    }

    @Override
    @NotNull
    public String getDueFilterCondition() {
        return CoreConstants.TABLE_NAME.HIV_MEMBER + ".base_entity_id IN (SELECT for FROM task WHERE business_status = 'Referred')";
    }

    public String getDueFilterCondition(boolean isReferred, String elicitationStatus, Context context) {
        StringBuilder customFilter = new StringBuilder();

        if (isReferred) {
            customFilter.append(MessageFormat.format(" and {0}.{1} IN (SELECT for FROM task WHERE business_status = ''Referred'') ", getMainTable(), "base_entity_id"));
        }

        if (elicitationStatus != null && !elicitationStatus.equalsIgnoreCase("all")) {
            if (elicitationStatus.equalsIgnoreCase("Elicited"))
                customFilter.append(" and index_contact_base_entity_id IS NOT NULL ");
            else
                customFilter.append(" and index_contact_base_entity_id IS NULL ");
        }
        return customFilter.toString();
    }

    @Override
    public void processViewConfigurations() {
        super.processViewConfigurations();
        if (getConfig().getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(getView().getContext().getString(R.string.search_name_or_id));
        }
    }

    @Override
    public String getMainTable() {
        return Tables.HIV;
    }
}
