package org.smartregister.chw.hf.fragment;

import static org.smartregister.chw.hf.utils.Constants.FILTERS_ENABLED;
import static org.smartregister.chw.hf.utils.Constants.FILTER_APPOINTMENT_DATE;
import static org.smartregister.chw.hf.utils.Constants.FILTER_HIV_STATUS;
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

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.util.DBConstants;
import org.smartregister.chw.core.fragment.CorePncRegisterFragment;
import org.smartregister.chw.core.provider.ChwPncRegisterProvider;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.QueryBuilder;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.activity.RegisterFilterActivity;
import org.smartregister.chw.hf.activity.PncMemberProfileActivity;
import org.smartregister.chw.hf.model.HfPncRegisterFragmentModel;
import org.smartregister.chw.hf.presenter.PncRegisterFragmentPresenter;
import org.smartregister.chw.hf.provider.HfPncRegisterProvider;
import org.smartregister.chw.hf.utils.HfReferralUtils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class PncRegisterFragment extends CorePncRegisterFragment implements android.view.View.OnClickListener {
    private String filterHivStatus;
    private boolean filterIsReferred = false;
    private String appointmentDate;
    private boolean filterEnabled = false;
    private TextView filterSortTextView;

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


    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawablesRelative()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    @Override
    public void onClick(android.view.View view) {
        if (view.getId() == R.id.filter_sort_layout) {
            Intent intent = new Intent(getContext(), RegisterFilterActivity.class);
            intent.putExtra(FILTERS_ENABLED, filterEnabled);
            intent.putExtra(FILTER_HIV_STATUS, filterHivStatus);
            intent.putExtra(FILTER_IS_REFERRED, filterIsReferred);
            intent.putExtra(FILTER_APPOINTMENT_DATE, appointmentDate);
            ((Activity) getContext()).startActivityForResult(intent, REQUEST_FILTERS);
        }

    }

    public void onFiltersUpdated(int requestCode, @Nullable Intent data) {
        if (requestCode == REQUEST_FILTERS) {
            if (data != null) {
                filterEnabled = data.getBooleanExtra(FILTERS_ENABLED, false);
                if (filterEnabled) {
                    setTextViewDrawableColor(filterSortTextView, R.color.hf_accent_yellow);
                    filterSortTextView.setText(R.string.filter_applied);
                    filterHivStatus = data.getStringExtra(FILTER_HIV_STATUS);
                    filterIsReferred = data.getBooleanExtra(FILTER_IS_REFERRED, false);
                    appointmentDate = data.getStringExtra(FILTER_APPOINTMENT_DATE);

                    filter(searchText(), "", ((PncRegisterFragmentPresenter) presenter()).getDueFilterCondition(filterHivStatus, appointmentDate, filterIsReferred, getContext()));
                } else {
                    setTextViewDrawableColor(filterSortTextView, R.color.grey);
                    filterSortTextView.setText(R.string.filter);
                }
            }
        }
    }

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
            customFilter.append(((PncRegisterFragmentPresenter) presenter()).getDueFilterCondition(filterHivStatus, appointmentDate, filterIsReferred, getContext()));
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

    private String searchText() {
        String searchTextInput;
        if (this.getSearchView() == null) {
            searchTextInput = "";
        } else {
            searchTextInput = this.getSearchView().getText().toString();
        }
        return searchTextInput;
    }

    @Override
    public String getDueCondition() {
        return CoreConstants.TABLE_NAME.PNC_MEMBER + ".base_entity_id in ("
                + HfReferralUtils.getReferralDueFilter(CoreConstants.TABLE_NAME.PNC_MEMBER, CoreConstants.TASKS_FOCUS.PNC_DANGER_SIGNS)
                + ")";
    }

    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        ChwPncRegisterProvider provider = new HfPncRegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, provider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        presenter = new PncRegisterFragmentPresenter(this, new HfPncRegisterFragmentModel(), null);
    }

    @Override
    protected void openHomeVisit(CommonPersonObjectClient client) {
        //Overridden
    }

    @Override
    protected void openPncMemberProfile(CommonPersonObjectClient client) {
        MemberObject memberObject = new MemberObject(client);
        PncMemberProfileActivity.startMe(getActivity(), memberObject.getBaseEntityId(), memberObject);
    }
}
