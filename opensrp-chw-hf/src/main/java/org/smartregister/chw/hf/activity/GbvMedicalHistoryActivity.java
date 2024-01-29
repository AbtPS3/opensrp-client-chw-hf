package org.smartregister.chw.hf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONObject;
import org.smartregister.chw.anc.domain.Visit;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.presenter.BaseAncMedicalHistoryPresenter;
import org.smartregister.chw.core.activity.CoreAncMedicalHistoryActivity;
import org.smartregister.chw.core.activity.DefaultAncMedicalHistoryActivityFlv;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.gbv.domain.MemberObject;
import org.smartregister.chw.hf.R;
import org.smartregister.chw.hf.interactor.GbvMedicalHistoryInteractor;
import org.smartregister.family.util.Utils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class GbvMedicalHistoryActivity extends CoreAncMedicalHistoryActivity {
    private static MemberObject gbvMemberObject;

    private final Flavor flavor = new FpMedicalHistoryActivityFlv();

    private ProgressBar progressBar;

    public static void startMe(Activity activity, MemberObject memberObject) {
        Intent intent = new Intent(activity, GbvMedicalHistoryActivity.class);
        gbvMemberObject = memberObject;
        activity.startActivity(intent);
    }

    @Override
    public void initializePresenter() {
        presenter = new BaseAncMedicalHistoryPresenter(new GbvMedicalHistoryInteractor(), this, gbvMemberObject.getBaseEntityId());
    }

    @Override
    public void setUpView() {
        linearLayout = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.linearLayoutMedicalHistory);
        progressBar = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.progressBarMedicalHistory);

        TextView tvTitle = findViewById(org.smartregister.chw.opensrp_chw_anc.R.id.tvTitle);
        tvTitle.setText(getString(org.smartregister.chw.opensrp_chw_anc.R.string.back_to, gbvMemberObject.getFullName()));

        ((TextView) findViewById(R.id.medical_history)).setText(getString(R.string.visits_history));
    }

    @Override
    public View renderView(List<Visit> visits) {
        super.renderView(visits);
        View view = flavor.bindViews(this);
        displayLoadingState(true);
        flavor.processViewData(visits, this);
        displayLoadingState(false);
        TextView agywVisitTitle = view.findViewById(org.smartregister.chw.core.R.id.customFontTextViewHealthFacilityVisitTitle);
        agywVisitTitle.setText(R.string.visits_history);
        return view;
    }

    @Override
    public void displayLoadingState(boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private class FpMedicalHistoryActivityFlv extends DefaultAncMedicalHistoryActivityFlv {
        private final StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);

        @Override
        protected void processAncCard(String has_card, Context context) {
            // super.processAncCard(has_card, context);
            linearLayoutAncCard.setVisibility(View.GONE);
        }

        @Override
        protected void processHealthFacilityVisit(List<Map<String, String>> hf_visits, Context context) {
            //super.processHealthFacilityVisit(hf_visits, context);
        }

        @Override
        public void processViewData(List<Visit> visits, Context context) {

            if (visits.size() > 0) {
                int days = 0;
                List<LinkedHashMap<String, String>> hf_visits = new ArrayList<>();

                int x = 0;
                while (x < visits.size()) {
                    LinkedHashMap<String, String> visitDetails = new LinkedHashMap<>();

                    // the first object in this list is the days difference
                    if (x == 0) {
                        days = Days.daysBetween(new DateTime(visits.get(visits.size() - 1).getDate()), new DateTime()).getDays();
                    }

                    String[] params = {
                            "visit_status",
                            "can_manage_case",
                            "referral_type",
                            "other_services",
                            "client_consent",
                            "client_consent_after_counseling",
                            "was_social_welfare_officer_involved",
                            "no_of_witnesses",
                            "assault_date",
                            "assault_time",
                            "place_of_assault",
                            "no_of_assailant",
                            "alleged_assailants",
                            "type_of_assault",
                            "presenting_signs",
                            "evidence_of_penetration",
                            "how",
                            "where",
                            "what_was_used",
                            "did_the_assailant_use_condom",
                            "did_the_survivor_have_a_bath",
                            "did_the_survivor_vomit_after_the_assault",
                            "did_the_survivor_go_to_the_toilet_after_the_assault",
                            "was_the_incident_reported_to_the_police",
                            "police_station",
                            "lnmp",
                            "gravida",
                            "para",
                            "history_of_sexual_intercourse_prior_to_incidence",
                            "history_of_pregnancy_prior_to_incidence",
                            "current_pregnancy_status",
                            "cause_of_pregnancy",
                            "history_of_contraception",
                            "type_of_contraceptives",
                            "last_consensual_sexual_intercourse_date",
                            "history_of_current_sexual_relationship",
                            "hiv_status",
                            "clients_mental_state",
                            "systolic",
                            "diastolic",
                            "pulse_rate",
                            "respiratory_rate",
                            "temperature",
                            "weight",
                            "height",
                            "did_the_survivor_change_clothes",
                            "where_the_clothes_were_taken",
                            "state_of_the_clothes",
                            "any_visible_injuries",
                            "area_of_injuries",
                            "comment_on_general_condition_of_the_survivor",
                            "external_genitalia",
                            "vaginal_hymen",
                            "cervix",
                            "digital_rectal_examination",
                            "other_orifices",
                            "virginal_swab",
                            "blood_specimen",
                            "pluck_pubic_hair",
                            "collected_urine_for_lab_investigation",
                            "collected_blood_sample_for_dna",
                            "collected_nails_from_survivors",
                            "collected_hair_root",
                            "collected_stained_clothes",
                            "other_obtained_materials",
                            "does_the_client_need_lab_investigation",
                            "upt_test_results",
                            "hiv_test_results",
                            "sti_test_results",
                            "hepb_test_results",
                            "pregnancy_status_after_incident",
                            "did_violence_cause_disability",
                            "was_the_survivor_provided_et",
                            "was_the_survivor_provided_sti_preventive_treatment",
                            "was_the_survivor_provided_ec",
                            "was_the_survivor_provided_pep",
                            "was_the_survivor_vaccinated_for_td",
                            "was_the_survivor_vaccinated_for_hepb",
                            "was_the_survivor_provided_surgical_treatment",
                            "was_the_survivor_provided_antibiotics",
                            "was_the_survivor_provided_fp",
                            "was_police_legal_and_social_services_required",
                            "which_services_were_provided",
                            "was_the_survivor_educated_on_the_violence_that_occurred",
                            "medical_counseling_provided",
                            "other_medical_counseling_provided",
                            "was_mental_and_psychosocial_support_provided",
                            "signs_of_partner_behaviour_that_alert_for_possibility_of_violence",
                            "can_the_client_get_out_of_the_house_before_the_violence_starts",
                            "can_the_client_send_a_message_for_help",
                            "are_there_neighbours_who_could_help_in_an_emergency_situation",
                            "is_there_a_way_the_client_can_communicate_to_alert_neighbours_for_help",
                            "can_the_client_move_into_a_room_to_escape",
                            "are_there_weapons_in_the_clients_house",
                            "weapons_location_in_the_house",
                            "can_the_client_move_the_weapons",
                            "does_the_client_have_places_where_they_can_go_in_an_emergency",
                            "can_the_client_hide_a_bag_with_supplies_for_emergencies",
                            "was_the_client_linked_to_other_services",
                            "services_the_client_has_been_linked_to",
                            "other_services_the_client_has_been_linked_to",
                            "does_the_client_require_a_followup_visit",
                            "next_appointment_date"
                    };
                    extractVisitDetails(visits, params, visitDetails, x, context);


                    hf_visits.add(visitDetails);

                    x++;
                }

                processLastVisit(days, context);
                processVisit(hf_visits, context, visits);
            }
        }

        private void extractVisitDetails(List<Visit> sourceVisits, String[] hf_params, LinkedHashMap<String, String> visitDetailsMap, int iteration, Context context) {
            // get the hf details
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            for (String param : hf_params) {
                try {
                    List<VisitDetail> details = sourceVisits.get(iteration).getVisitDetails().get(param);
                    map.put(param, getTexts(context, details));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            visitDetailsMap.putAll(map);
        }


        private void processLastVisit(int days, Context context) {
            linearLayoutLastVisit.setVisibility(View.VISIBLE);
            if (days < 1) {
                customFontTextViewLastVisit.setText(org.smartregister.chw.core.R.string.less_than_twenty_four);
            } else {
                customFontTextViewLastVisit.setText(StringUtils.capitalize(MessageFormat.format(context.getString(org.smartregister.chw.core.R.string.days_ago), String.valueOf(days))));
            }
        }

        public void startFormActivity(JSONObject jsonForm, Context context) {
            Intent intent = new Intent(context, Utils.metadata().familyMemberFormActivity);
            intent.putExtra(FamilyPlanningConstants.JSON_FORM_EXTRA.JSON, jsonForm.toString());

            startActivityForResult(intent, FamilyPlanningConstants.REQUEST_CODE_GET_JSON);
        }


        protected void processVisit(List<LinkedHashMap<String, String>> community_visits, Context context, List<Visit> visits) {
            if (community_visits != null && community_visits.size() > 0) {
                linearLayoutHealthFacilityVisit.setVisibility(View.VISIBLE);

                int x = 0;
                for (LinkedHashMap<String, String> vals : community_visits) {
                    View view = inflater.inflate(R.layout.medical_history_visit, null);
                    TextView tvTitle = view.findViewById(R.id.title);
                    View edit = view.findViewById(R.id.textview_edit);
                    LinearLayout visitDetailsLayout = view.findViewById(R.id.visit_details_layout);

                    tvTitle.setText(visits.get(x).getVisitType() + " " + visits.get(x).getDate());

                    if (x == visits.size() - 1) {
                        int position = x;
                        edit.setVisibility(View.VISIBLE);
                        edit.setOnClickListener(view1 -> {
                            try {
                                GbvVisitActivity.startMe((Activity) context, visits.get(position).getBaseEntityId(), true);
                            } catch (Exception e) {
                                Timber.e(e);
                            }
                        });
                    }


                    for (LinkedHashMap.Entry<String, String> entry : vals.entrySet()) {
                        TextView visitDetailTv = new TextView(context);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                                ((int) LinearLayout.LayoutParams.MATCH_PARENT, (int) LinearLayout.LayoutParams.WRAP_CONTENT);

                        visitDetailTv.setLayoutParams(params);
                        float scale = context.getResources().getDisplayMetrics().density;
                        int dpAsPixels = (int) (10 * scale + 0.5f);
                        visitDetailTv.setPadding(dpAsPixels, 0, 0, 0);
                        visitDetailsLayout.addView(visitDetailTv);


                        try {
                            int resource = context.getResources().getIdentifier("gbv_" + entry.getKey(), "string", context.getPackageName());
                            evaluateView(context, vals, visitDetailTv, entry.getKey(), resource, "gbv_");
                        } catch (Exception e) {
                            Timber.e(e);
                        }
                    }
                    linearLayoutHealthFacilityVisitDetails.addView(view, 0);

                    x++;
                }
            }
        }

        private void evaluateView(Context context, Map<String, String> vals, TextView tv, String valueKey, int viewTitleStringResource, String valuePrefixInStringResources) {
            if (StringUtils.isNotBlank(getMapValue(vals, valueKey))) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                spannableStringBuilder.append(context.getString(viewTitleStringResource), boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE).append("\n");

                String stringValue = getMapValue(vals, valueKey);
                String[] stringValueArray;
                if (stringValue.contains(",")) {
                    stringValueArray = stringValue.split(",");
                    for (String value : stringValueArray) {
                        spannableStringBuilder.append(getStringResource(context, valuePrefixInStringResources, value.trim()) + "\n", new BulletSpan(10), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } else {
                    spannableStringBuilder.append(getStringResource(context, valuePrefixInStringResources, stringValue)).append("\n");
                }
                tv.setText(spannableStringBuilder);
            } else {
                tv.setVisibility(View.GONE);
            }
        }


        private String getMapValue(Map<String, String> map, String key) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
            return "";
        }

        private String getStringResource(Context context, String prefix, String resourceName) {
            int resourceId = context.getResources().
                    getIdentifier(prefix + resourceName.trim(), "string", context.getPackageName());
            try {
                return context.getString(resourceId);
            } catch (Exception e) {
                Timber.e(e);
                return resourceName;
            }
        }
    }
}
