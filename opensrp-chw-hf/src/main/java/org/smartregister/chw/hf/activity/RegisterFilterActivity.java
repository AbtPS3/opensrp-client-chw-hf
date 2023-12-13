package org.smartregister.chw.hf.activity;

import static org.smartregister.chw.hf.utils.Constants.ENABLE_APPOINT_DATE_FILTER;
import static org.smartregister.chw.hf.utils.Constants.ENABLE_HIV_STATUS_FILTER;
import static org.smartregister.chw.hf.utils.Constants.ENABLE_INDEX_CONTACTS_ELICITATION_STATUS_FILTER;
import static org.smartregister.chw.hf.utils.Constants.FILTERS_ENABLED;
import static org.smartregister.chw.hf.utils.Constants.FILTER_APPOINTMENT_DATE;
import static org.smartregister.chw.hf.utils.Constants.FILTER_HIV_STATUS;
import static org.smartregister.chw.hf.utils.Constants.FILTER_INDEX_CONTACTS_ELICITATION_STATUS;
import static org.smartregister.chw.hf.utils.Constants.FILTER_IS_REFERRED;
import static org.smartregister.chw.hf.utils.Constants.REQUEST_FILTERS;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import org.joda.time.DateTime;
import org.smartregister.chw.hf.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegisterFilterActivity extends AppCompatActivity {
    private final Calendar mCalendar = Calendar.getInstance();
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private SwitchCompat enableFilter;
    private TextView summaryAppointmentDate;
    private SwitchCompat referredFromCommunityFilter;
    private Spinner hivStatusFilter;
    private List<String> hivFilterOptions;

    private Spinner indexContactsElicitationStatusFilter;

    private List<String> indexContactsElicitationStatusFilterOptions;

    private boolean enableHivStatusFilter;

    private boolean enableAppointmentDateFilter;

    private boolean enableIndexContactsElicitationStatusFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_filter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Filter");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = getResources().getDrawable(org.smartregister.chw.core.R.drawable.ic_arrow_back_white_24dp);
            actionBar.setHomeAsUpIndicator(upArrow);
            actionBar.setElevation(0);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
        hivFilterOptions = Arrays.asList(getResources().getStringArray(R.array.hiv_status_filter_options));
        indexContactsElicitationStatusFilterOptions = Arrays.asList(getResources().getStringArray(R.array.index_contacts_elicitation_status_filter_options));
        enableHivStatusFilter = getIntent().getBooleanExtra(ENABLE_HIV_STATUS_FILTER, true);
        enableAppointmentDateFilter = getIntent().getBooleanExtra(ENABLE_APPOINT_DATE_FILTER, true);
        enableIndexContactsElicitationStatusFilter = getIntent().getBooleanExtra(ENABLE_INDEX_CONTACTS_ELICITATION_STATUS_FILTER, false);

        setupViews();
        boolean filterEnabled = getIntent().getBooleanExtra(FILTERS_ENABLED, false);
        boolean filterIsReferred = getIntent().getBooleanExtra(FILTER_IS_REFERRED, false);
        String filterHivStatus = getIntent().getStringExtra(FILTER_HIV_STATUS);
        String filterIndexContactsElicitationStatus = getIntent().getStringExtra(FILTER_INDEX_CONTACTS_ELICITATION_STATUS);
        String appointmentDate = getIntent().getStringExtra(FILTER_APPOINTMENT_DATE);
        if (filterEnabled) {
            enableFilter.setChecked(true);
            if (filterIsReferred) {
                referredFromCommunityFilter.setChecked(true);
            }

            if (appointmentDate != null) {
                summaryAppointmentDate.setText(appointmentDate);
            }

            if (filterHivStatus != null) {
                if (hivFilterOptions.contains(filterHivStatus)) {
                    hivStatusFilter.setSelection(hivFilterOptions.indexOf(filterHivStatus));
                }
            }

            if (filterIndexContactsElicitationStatus != null) {
                if (indexContactsElicitationStatusFilterOptions.contains(filterIndexContactsElicitationStatus)) {
                    indexContactsElicitationStatusFilter.setSelection(indexContactsElicitationStatusFilterOptions.indexOf(filterIndexContactsElicitationStatus));
                }
            }
        }
    }

    public void setupViews() {
        enableFilter = findViewById(R.id.enable_filter_switch);
        TextView filterStatus = findViewById(R.id.filter_status);
        summaryAppointmentDate = findViewById(R.id.summary_appointment_date);
        LinearLayout filterList = findViewById(R.id.filters_list);
        hivStatusFilter = findViewById(R.id.hiv_status_filter);
        indexContactsElicitationStatusFilter = findViewById(R.id.index_contacts_elicitation_status_filter);
        referredFromCommunityFilter = findViewById(R.id.switch_for_referred);
        LinearLayout nextAppointmentDate = findViewById(R.id.next_appointment_date);

        if (enableFilter.isChecked()) {
            filterList.setVisibility(View.VISIBLE);
        } else {
            filterList.setVisibility(View.GONE);
        }

        enableFilter.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                filterStatus.setText(R.string.filter_status_on);
                filterList.setVisibility(View.VISIBLE);
            } else {
                filterStatus.setText(R.string.filter_status_off);
                filterList.setVisibility(View.GONE);
                summaryAppointmentDate.setText(R.string.none);
                hivStatusFilter.setSelection(0);
                indexContactsElicitationStatusFilter.setSelection(0);
                referredFromCommunityFilter.setChecked(false);
            }
        });

        if (enableHivStatusFilter) {
            findViewById(R.id.hiv_status_filter_rl).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.hiv_status_filter_rl).setVisibility(View.GONE);
        }

        if (enableAppointmentDateFilter) {
            findViewById(R.id.next_appointment_date).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.next_appointment_date).setVisibility(View.GONE);
        }

        if (enableIndexContactsElicitationStatusFilter) {
            findViewById(R.id.index_contacts_elicitation_status_filter_rl).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.index_contacts_elicitation_status_filter_rl).setVisibility(View.GONE);
        }

        DatePickerDialog.OnDateSetListener datePickerListener = (mView, year, monthOfYear, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            summaryAppointmentDate.setText(mDateFormat.format(mCalendar.getTime()));
        };

        nextAppointmentDate.setOnClickListener(view -> {
            DatePickerDialog dialog = new DatePickerDialog(RegisterFilterActivity.this, datePickerListener, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH));

            dialog.getDatePicker().setSpinnersShown(true);
            dialog.getDatePicker().setMinDate(new Date().getTime());
            dialog.getDatePicker().setMaxDate(new DateTime().plusMonths(6).toDate().getTime());

            dialog.show();
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            Intent intent = new Intent();
            intent.putExtra(FILTERS_ENABLED, enableFilter.isChecked());
            if (enableFilter.isChecked()) {
                intent.putExtra(FILTER_APPOINTMENT_DATE, summaryAppointmentDate.getText());
                intent.putExtra(FILTER_IS_REFERRED, referredFromCommunityFilter.isChecked());
                intent.putExtra(FILTER_HIV_STATUS, hivFilterOptions.get(hivStatusFilter.getSelectedItemPosition()));
                intent.putExtra(FILTER_INDEX_CONTACTS_ELICITATION_STATUS, indexContactsElicitationStatusFilterOptions.get(indexContactsElicitationStatusFilter.getSelectedItemPosition()));
            }

            setResult(REQUEST_FILTERS, intent);
            setResult(RESULT_OK, intent);
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}