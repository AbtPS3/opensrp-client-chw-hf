package org.smartregister.chw.hf.fragment;

import static org.smartregister.chw.hf.utils.Constants.ENABLE_APPOINT_DATE_FILTER;
import static org.smartregister.chw.hf.utils.Constants.ENABLE_HIV_STATUS_FILTER;
import static org.smartregister.chw.hf.utils.Constants.ENABLE_INDEX_CONTACTS_ELICITATION_STATUS_FILTER;
import static org.smartregister.chw.hf.utils.Constants.FILTERS_ENABLED;
import static org.smartregister.chw.hf.utils.Constants.FILTER_APPOINTMENT_DATE;
import static org.smartregister.chw.hf.utils.Constants.FILTER_HIV_STATUS;
import static org.smartregister.chw.hf.utils.Constants.FILTER_INDEX_CONTACTS_ELICITATION_STATUS;
import static org.smartregister.chw.hf.utils.Constants.FILTER_IS_REFERRED;
import static org.smartregister.chw.hf.utils.Constants.REQUEST_FILTERS;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.fragment.CoreHivRegisterFragment;
import org.smartregister.chw.core.provider.CoreHivProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.QueryBuilder;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.activity.HivProfileActivity;
import org.smartregister.chw.hf.activity.HivRegisterActivity;
import org.smartregister.chw.hf.activity.RegisterFilterActivity;
import org.smartregister.chw.hf.model.HivRegisterFragmentModel;
import org.smartregister.chw.hf.presenter.HivRegisterFragmentPresenter;
import org.smartregister.chw.hf.provider.HfHivRegisterProvider;
import org.smartregister.chw.hiv.dao.HivDao;
import org.smartregister.chw.hiv.domain.HivMemberObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import timber.log.Timber;

public class HivRegisterFragment extends CoreHivRegisterFragment implements android.view.View.OnClickListener {

    private String appointmentDate;

    private String filterHivStatus;

    private String filterIndexContactsElicitationStatus;

    private boolean filterIsReferred = false;

    private boolean filterEnabled = false;

    private TextView filterSortTextView;

    @Override
    public void initializeAdapter(@Nullable Set<? extends View> visibleColumns) {
        CoreHivProvider hivRegisterProvider = new HfHivRegisterProvider(getActivity(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, hivRegisterProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = null;
        try {
            viewConfigurationIdentifier = ((HivRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
        presenter = new HivRegisterFragmentPresenter(this, new HivRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    protected void openProfile(CommonPersonObjectClient client) {
        if (getActivity() != null)
            HivProfileActivity.startHivProfileActivity(getActivity(), Objects.requireNonNull(HivDao.getMember(client.getCaseId())));
    }


    @Override
    protected void openFollowUpVisit(@Nullable HivMemberObject hivMemberObject) {

    }

    @Override
    public void setupViews(android.view.View view) {
        super.setupViews(view);

        android.view.View sortFilterBarLayout = view.findViewById(org.smartregister.chw.core.R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(android.view.View.GONE);

        android.view.View dueOnlyLayout = view.findViewById(org.smartregister.chw.core.R.id.due_only_layout);
        dueOnlyLayout.setVisibility(android.view.View.GONE);

        android.view.View filterSortLayout = view.findViewById(org.smartregister.chw.core.R.id.filter_sort_layout);
        filterSortTextView = view.findViewById(org.smartregister.chw.core.R.id.filter_text_view);
        filterSortTextView.setText(R.string.filter);

        filterSortLayout.setVisibility(android.view.View.VISIBLE);
        filterSortLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(android.view.View view) {
        if (view.getId() == R.id.filter_sort_layout) {
            Intent intent = new Intent(getContext(), RegisterFilterActivity.class);
            intent.putExtra(FILTERS_ENABLED, filterEnabled);
            intent.putExtra(ENABLE_HIV_STATUS_FILTER, false);
            intent.putExtra(ENABLE_APPOINT_DATE_FILTER, false);
            intent.putExtra(ENABLE_INDEX_CONTACTS_ELICITATION_STATUS_FILTER, true);
            intent.putExtra(FILTER_HIV_STATUS, filterHivStatus);
            intent.putExtra(FILTER_INDEX_CONTACTS_ELICITATION_STATUS, filterIndexContactsElicitationStatus);
            intent.putExtra(FILTER_IS_REFERRED, filterIsReferred);
            intent.putExtra(FILTER_APPOINTMENT_DATE, appointmentDate);
            ((Activity) getContext()).startActivityForResult(intent, REQUEST_FILTERS);
        }
    }

    public void onFiltersUpdated(int requestCode, @androidx.annotation.Nullable Intent data) {
        if (requestCode == REQUEST_FILTERS && data != null) {
            filterEnabled = data.getBooleanExtra(FILTERS_ENABLED, false);
            if (filterEnabled) {
                setTextViewDrawableColor(filterSortTextView, R.color.hf_accent_yellow);
                filterSortTextView.setText(R.string.filter_applied);
                filterHivStatus = data.getStringExtra(FILTER_HIV_STATUS);
                filterIndexContactsElicitationStatus = data.getStringExtra(FILTER_INDEX_CONTACTS_ELICITATION_STATUS);
                filterIsReferred = data.getBooleanExtra(FILTER_IS_REFERRED, false);
                appointmentDate = data.getStringExtra(FILTER_APPOINTMENT_DATE);
                filter(searchText(), "", ((HivRegisterFragmentPresenter) presenter()).getDueFilterCondition(filterIsReferred, filterIndexContactsElicitationStatus, getContext()), false);
            } else {
                setTextViewDrawableColor(filterSortTextView, R.color.grey);
                filterSortTextView.setText(R.string.filter);
            }
        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawablesRelative()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        if (id == LOADER_ID) {
            return new CursorLoader(getActivity()) {
                @Override
                public Cursor loadInBackground() {
                    // Count query
                    final String COUNT = "count_execute";
                    if (args != null && args.getBoolean(COUNT)) {
                        countExecute();
                    }
                    String query = defaultFilterAndSortQuery();
                    return commonRepository().rawCustomQueryForAdapter(query);
                }
            };
        }
        return super.onCreateLoader(id, args);
    }

    private String defaultFilterAndSortQuery() {
        SmartRegisterQueryBuilder sqb = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        StringBuilder customFilter = new StringBuilder();
        if (StringUtils.isNotBlank(filters)) {
            customFilter.append(MessageFormat.format(" and ( {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.FIRST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.LAST_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.MIDDLE_NAME, filters));
            customFilter.append(MessageFormat.format(" or {0}.{1} like ''%{2}%'' ) ", CoreConstants.TABLE_NAME.FAMILY_MEMBER, DBConstants.KEY.UNIQUE_ID, filters));

        }
        if (filterEnabled) {
            customFilter.append(((HivRegisterFragmentPresenter) presenter()).getDueFilterCondition(filterIsReferred, filterIndexContactsElicitationStatus, getContext()));
        }
        try {
            if (isValidFilterForFts(commonRepository())) {

                String myquery = QueryBuilder.getQuery(joinTables, mainCondition, tablename, customFilter.toString(), clientAdapter, Sortqueries);
                List<String> ids = commonRepository().findSearchIds(myquery);
                query = sqb.toStringFts(ids, tablename, CommonRepository.ID_COLUMN,
                        Sortqueries);
                query = sqb.Endquery(query);
            } else {
                sqb.addCondition(customFilter.toString());
                query = sqb.orderbyCondition(Sortqueries);
                query = sqb.Endquery(sqb.addlimitandOffset(query, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset()));

            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }
}


