package org.smartregister.chw.hf.utils;

import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.assetManager;
import static org.smartregister.chw.core.utils.CoreConstants.JSON_FORM.locale;

import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.Utils;

public class Constants extends CoreConstants {
    public static String DEFAULT_LOCATION_NAME = "default_location_name";
    public static String pregnancyOutcome = "preg_outcome";
    public static String LOST_TO_FOLLOWUP = "lost_to_followup";
    public static String REFERRAL_TASK_FOCUS = "referral_task_focus";

    public static String FILTER_APPOINTMENT_DATE = "FILTER_APPOINTMENT_DATE";
    public static String FILTER_IS_REFERRED = "FILTER_IS_REFERRED";
    public static String FILTER_HIV_STATUS = "FILTER_HIV_STATUS";
    public static String FILTERS_ENABLED = "FILTERS_ENABLED";
    public static String ENABLE_HIV_STATUS_FILTER = "ENABLE_HIV_STATUS_FILTER";

    public static int REQUEST_FILTERS = 2004;

    public enum FamilyRegisterOptionsUtil {Miscarriage, Other}

    public interface ScheduleGroups {
        String FACILITY_VISIT = "FACILITY_VISIT";
    }

    public interface PregnancyConfirmationGroups {
        String PREGNANCY_CONFIRMATION = "Pregnancy Confirmation";
    }

    public interface PartnerRegistrationConstants {
        int EXISTING_PARTNER_REQUEST_CODE = 12344;
        int NEW_PARTNER_REQUEST_CODE = 12345;
        String INTENT_BASE_ENTITY_ID = "BASE_ENTITY_ID";
        String PARTNER_BASE_ENTITY_ID = "partner_base_entity_id";
    }

    public static final class FOCUS {
        public static final String LOST_TO_FOLLOWUP_FOCUS = "LTFU";
        public static final String REFERRALS = "REFERRALS";
        public static final String LD_EMERGENCY = "Labour And Delivery Emergency";
        public static final String VMMC_REFERRALS = "Vmmc Referral";
        public static final String LD_CHILD_EMERGENCY = "Labour And Delivery Child Emergency";
    }

    public static final class Events {
        public static final String ANC_FIRST_FACILITY_VISIT = "ANC First Facility Visit";

        public static final String ANC_RECURRING_FACILITY_VISIT = "ANC Recurring Facility Visit";

        public static final String ANC_FACILITY_VISIT_NOT_DONE = "ANC Facility Visit Not Done";

        public static final String ANC_FACILITY_VISIT_NOT_DONE_UNDO = "ANC Facility Visit Not Done Undo";

        public static final String PMTCT_FIRST_EAC_VISIT = "PMTCT EAC First Visit";

        public static final String PMTCT_SECOND_EAC_VISIT = "PMTCT EAC Second Visit";

        public static final String PMTCT_EAC_VISIT = "PMTCT EAC Visit";

        public static final String UPDATE_HIV_INDEX_TESTING_FOLLOWUP = "Update HIV Index Contact Testing Followup";

        public static final String PARTNER_REGISTRATION_EVENT = "Partner Registration";

        public static final String HEI_REGISTRATION = "HEI Registration";

        public static final String HEI_FOLLOWUP = "HEI Followup";

        public static final String HEI_POSITIVE_INFANT = "HEI Positive Infant";

        public static final String HEI_NEGATIVE_INFANT = "HEI Negative Infant";

        public static final String PMTCT_CLOSE_VISITS = "PMTCT Close Visits";

        public static final String PNC_VISIT = "PNC VISIT";

        public static final String PNC_NO_MOTHER_REGISTRATION = "PNC No Mother Registration";

        public static final String CLOSE_PNC_VISITS = "Close PNC Visits";

        public static final String PNC_CHILD_FOLLOWUP = "PNC Child Followup";

        public static final String MOTHER_CHAMPION_COMMUNITY_SERVICES_REFERRAL = "Mother Champion Community Services Referral";

        public static final String MARK_PMTCT_CLIENT_AS_LTF = "Mark PMTCT Client As LTF";

        public static final String MARK_HEI_CLIENT_AS_LTF = "Mark HEI Client As LTF";

        public static final String HEI_COMMUNITY_FOLLOWUP = "HEI Community Followup";

        public static final String HEI_NUMBER_REGISTRATION = "HEI Number Registration";

        public static final String EDIT_HEI_NUMBER = "Edit HEI Number";

        public static final String LD_REGISTRATION = "LD Registration";

        public static final String LD_PARTOGRAPHY = "LD Partograph";

        public static final String LD_GENERAL_EXAMINATION = "LD General Examination";

        public static final String LD_ACTIVE_MANAGEMENT_OF_3RD_STAGE_OF_LABOUR = "LD Active Management of 3rd Stage Of Labour";

        public static final String LD_POST_DELIVERY_MOTHER_MANAGEMENT = "Post Delivery Mother Management";

        public static final String CLOSE_LD = "Close LD";

        public static final String PMTCT_POST_PNC_REGISTRATION = "PMTCT Post PNC Registration";

        public static final String FP_ECP_SCREENING = "FP ECP Screening";
    }

    public static final class TableName {
        public static final String ANC_FIRST_FACILITY_VISIT = "ec_anc_first_facility_visit";

        public static final String ANC_RECURRING_FACILITY_VISIT = "ec_anc_recurring_facility_visit";

        public static final String PMTCT_EAC_VISIT = "ec_pmtct_eac_visit";

        public static final String HEI = "ec_hei";

        public static final String HEI_HIV_RESULTS = "ec_hei_hiv_results";

        public static final String HEI_FOLLOWUP = "ec_hei_followup";

        public static final String PNC_FOLLOWUP = "ec_pnc_followup";

        public static final String PMTCT_FOLLOWUP = "ec_pmtct_followup";

        public static final String NO_MOTHER_PNC = "ec_no_mother_pnc";

        public static final String FP_ECP_REGISTER = "ec_fp_ecp_register";
    }

    public static final class Visits {
        public static final String TERMINATED = "Terminated";
        public static final String FIRST_EAC = "First Eac";
        public static final String SECOND_EAC = "Second Eac";
        public static final String PMTCT_VISIT = "Pmtct";
        public static final String HEI_VISIT = "HEI";
        public static final String LD_GENERAL_VISIT = "L&D Examination";
        public static final String LD_PARTOGRAPH_VISIT = "L&D Partograph";
        public static final String LD_MANAGEMENT_OF_3rd_STAGE_OF_LABOUR_VISIT = "L&D Management of 3rd Stage Of Labour";
        public static final String LD_IMMEDIATE_POSTPARTUM_CARE = "L&D Immediate Postpartum Care";
    }

    public static final class ActionList {
        public static final String FOLLOWUP = "Followup_action";
    }

    public static final class JsonForm {
        public static final String HIV_REGISTRATION = "hiv_registration";
        public static final String HVL_TEST_RESULTS = "pmtct_hvl_test_results";
        public static final String CD4_TEST_RESULTS = "pmtct_cd4_test_results";
        public static final String EAC_VISITS_FORM = "pmtct_eac_visits";
        public static final String HVL_SUPPRESSION_FORM_AFTER_EAC_1 = "pmtct_hvl_suppression_after_eac_1";
        public static final String HVL_SUPPRESSION_FORM_AFTER_EAC_2 = "pmtct_hvl_suppression_after_eac_2";
        public static final String HVL_CLINICIAN_DETAILS_FORM = "pmtct_hvl_sample_collection";
        public static final String MARK_CLIENT_AS_DECEASED = "mark_client_as_deceased";
        public static final String MARK_CHILD_AS_DECEASED = "mark_child_as_deceased";
        public static final String HEI_COMMUNITY_FOLLOWUP_REFERRAL = "hei_community_followup_referral";
        //TODO: cleanup
        private static final String NEXT_FACILITY_VISIT_FORM = "next_facility_visit_date_form";
        private static final String PMTCT_REGISTRATION = "pmtct_registration";
        private static final String PMTCT_REGISTRATION_FOR_CLIENTS_KNOWN_ON_ART_FORM = "pmtct_registration_for_clients_known_on_art";
        private static final String VMMC_REGISTRATION_FOR_CLIENTS_KNOWN_ON_ART_FORM = "vmmc_registration_for_clients_known_on_art";

        private static final String COUNSELLING = "anc_counselling";
        private static final String HIV_INDEX_CONTACT_CTC_ENROLLMENT = "hiv_index_contact_ctc_enrollment";
        private static final String PARTNER_REGISTRATION_FORM = "male_partner_registration_form";
        private static final String PMTCT_COUNSELLING = "pmtct_fv_counselling";
        private static final String PMTCT_BASELINE_INVESTIGATION = "pmtct_fv_baseline_investigation";
        private static final String PMTCT_CD4_SAMPLE_COLLECTION = "pmtct_cd4_sample_collection";
        private static final String PMTCT_CLINICAL_STAGING_OF_DISEASE = "pmtct_clinical_staging_of_disease";
        private static final String PMTCT_TB_SCREENING = "pmtct_tb_screening";
        private static final String PMTCT_ARV_LINE = "pmtct_prescription_line_selection";
        private static final String HEI_DNA_PCR_SAMPLE_COLLECTION = "hei_dna_pcr_sample_collection";
        private static final String HEI_ARV_PRESCRIPTION_HIGH_RISK_INFANT = "hei_arv_prescription_high_risk_infant";
        private static final String HEI_ARV_PRESCRIPTION_HIGH_OR_LOW_RISK_INFANT = "hei_arv_prescription_high_or_low_risk_infant";
        private static final String HEI_CTX_PRESCRIPTION = "hei_rv_ctx";
        private static final String HEI_HIV_TEST_RESULTS = "hei_hiv_test_results";
        private static final String HEI_NUMBER_REGISTRATION = "hei_number_registration";
        private static final String EDIT_HEI_NUMBER = "hei_number_edit";
        private static final String HEI_BASLINE_INVESTIGATION = "hei_baseline_investigation";
        private static final String HIV_CLIENT_UPDATE_CTC_NUMBER = "hiv_client_update_ctc_number";
        private static final String PNC_MOTHER_GENERAL_EXAMINATION = "pnc_mother_general_examination";
        private static final String PNC_CHILD_GENERAL_EXAMINATION = "pnc_child_general_examination";
        private static final String PNC_FAMILY_PLANNING_SERVICES = "pnc_family_planning_services";
        private static final String PNC_IMMUNIZATION = "pnc_immunization";
        private static final String PNC_HIV_TEST_RESULTS = "pnc_hiv_test_results";
        private static final String PNC_NUTRITIONAL_SUPPLEMENT = "pnc_nutritional_supplement";
        private static final String PMTCT_FOLLOWUP_STATUS = "pmtct_followup_status";
        private static final String LD_REGISTRATION = "labour_and_delivery_registration";
        private static final String LD_CHILD_REGISTRATION = "ld_child_registration";
        private static final String LD_HEI_FIRST_VISIT = "ld_hei_first_visit";
        private static final String LTFU_REFERRAL_FORM = "referrals/ltfu_referral_form";
        private static final String LD_EMERGENCY_REFERRAL_FORM = "referrals/labour_and_delivery_emergency_referral";
        private static final String LD_CHILD_EMERGENCY_REFERRAL_FORM = "referrals/labour_and_delivery_child_emergency_referral";
        private static final String PMTCT_REGISTRATION_FOR_CLIENTS_POST_PNC = "pmtct_registration_for_clients_post_pnc";
        private static final String VMMC_REFERRAL= "referrals/vmmc_referral_form";


        private static final String FP_ECP_SCREENING = "fp_ecp_screening";

        private static final String FP_ECP_PROVISION = "fp_provision_of_ecp";

        public static String getNextFacilityVisitForm() {
            return NEXT_FACILITY_VISIT_FORM;
        }

        public static String getHeiCommunityFollowupReferral() {
            return HEI_COMMUNITY_FOLLOWUP_REFERRAL;
        }

        public static String getLtfuReferralForm() {
            return LTFU_REFERRAL_FORM;
        }

        public static String getVmmcReferralForm() {
            return VMMC_REFERRAL;
        }

        public static String getLdEmergencyReferralForm() {
            return LD_EMERGENCY_REFERRAL_FORM;
        }

        public static String getLdChildEmergencyReferralForm() {
            return LD_CHILD_EMERGENCY_REFERRAL_FORM;
        }

        public static String getHeiNumberRegistration() {
            return HEI_NUMBER_REGISTRATION;
        }

        public static String getEditHeiNumber() {
            return EDIT_HEI_NUMBER;
        }

        public static String getPncChildGeneralExamination() {
            return PNC_CHILD_GENERAL_EXAMINATION;
        }

        public static String getPncFamilyPlanningServices() {
            return PNC_FAMILY_PLANNING_SERVICES;
        }

        public static String getPncImmunization() {
            return PNC_IMMUNIZATION;
        }

        public static String getPncHivTestResults() {
            return PNC_HIV_TEST_RESULTS;
        }

        public static String getPncNutritionalSupplement() {
            return PNC_NUTRITIONAL_SUPPLEMENT;
        }

        public static String getPncMotherGeneralExamination() {
            return PNC_MOTHER_GENERAL_EXAMINATION;
        }

        public static String getHeiCtxPrescription() {
            return HEI_CTX_PRESCRIPTION;
        }

        public static String getHeiHivTestResults() {
            return HEI_HIV_TEST_RESULTS;
        }

        public static String getHeiArvPrescriptionHighOrLowRiskInfant() {
            return HEI_ARV_PRESCRIPTION_HIGH_OR_LOW_RISK_INFANT;
        }

        public static String getHeiDnaPcrSampleCollection() {
            return HEI_DNA_PCR_SAMPLE_COLLECTION;
        }

        public static String getHeiArvPrescriptionHighRiskInfant() {
            return HEI_ARV_PRESCRIPTION_HIGH_RISK_INFANT;
        }

        public static String getHeiBaselineInvestigation() {
            return HEI_BASLINE_INVESTIGATION;
        }

        public static String getPmtctCounselling() {
            return PMTCT_COUNSELLING;
        }

        public static String getPmtctBaselineInvestigation() {
            return PMTCT_BASELINE_INVESTIGATION;
        }

        public static String getPmtctCd4SampleCollection() {
            return PMTCT_CD4_SAMPLE_COLLECTION;
        }

        public static String getPmtctClinicalStagingOfDisease() {
            return PMTCT_CLINICAL_STAGING_OF_DISEASE;
        }

        public static String getEacVisitsForm() {
            return EAC_VISITS_FORM;
        }

        public static String getPmtctTbScreening() {
            return PMTCT_TB_SCREENING;
        }

        public static String getPmtctArvLine() {
            return PMTCT_ARV_LINE;
        }

        public static String getPartnerRegistrationForm() {
            return Utils.getLocalForm(PARTNER_REGISTRATION_FORM, locale, assetManager);
        }

        public static String getCounselling() {
            return COUNSELLING;
        }

        public static String getPmtctRegistration() {
            return PMTCT_REGISTRATION;
        }

        public static String getPmtctRegistrationForClientsPostPnc() {
            return PMTCT_REGISTRATION_FOR_CLIENTS_POST_PNC;
        }

        public static String getPmtctRegistrationForClientsKnownOnArtForm() {
            return PMTCT_REGISTRATION_FOR_CLIENTS_KNOWN_ON_ART_FORM;
        }

        public static String getVmmcRegistrationForClientsKnownOnArtForm() {
            return VMMC_REGISTRATION_FOR_CLIENTS_KNOWN_ON_ART_FORM;
        }

        public static String getHvlTestResultsForm() {
            return HVL_TEST_RESULTS;
        }

        public static String getCd4TestResultsForm() {
            return CD4_TEST_RESULTS;
        }

        public static String getHvlSuppressionFormAfterEac1() {
            return HVL_SUPPRESSION_FORM_AFTER_EAC_1;
        }

        public static String getHivIndexContactCtcEnrollment() {
            return HIV_INDEX_CONTACT_CTC_ENROLLMENT;
        }

        public static String getHvlSuppressionFormAfterEac2() {
            return HVL_SUPPRESSION_FORM_AFTER_EAC_2;
        }

        public static String getHivRegistration() {
            return Utils.getLocalForm(HIV_REGISTRATION, locale, assetManager);
        }

        public static String getMarkClientAsDeceased() {
            return Utils.getLocalForm(MARK_CLIENT_AS_DECEASED, locale, assetManager);
        }

        public static String getMarkChildAsDeceased() {
            return Utils.getLocalForm(MARK_CHILD_AS_DECEASED, locale, assetManager);
        }

        public static String getPmtctFollowupStatus() {
            return PMTCT_FOLLOWUP_STATUS;
        }

        public static String getHvlClinicianDetailsForm() {
            return HVL_CLINICIAN_DETAILS_FORM;
        }

        public static String getHivClientUpdateCtcNumber() {
            return HIV_CLIENT_UPDATE_CTC_NUMBER;
        }

        public static String getLdRegistration() {
            return LD_REGISTRATION;
        }

        public static String getLdChildRegistration() {
            return LD_CHILD_REGISTRATION;
        }

        public static String getLdHeiFirstVisit() {
            return LD_HEI_FIRST_VISIT;
        }

        public static String getFPEcpScreening(){
            return FP_ECP_SCREENING;
        }

        public static String getFpEcpProvision(){
            return FP_ECP_PROVISION;
        }



        public static class EacVisits {
            public static final String PMTCT_EAC_VISIT = "pmtct_eac_visits";

            public static String getPmtctEacVisit() {
                return PMTCT_EAC_VISIT;
            }
        }

        public static class AncFirstVisit {
            public static final String OBSTETRIC_EXAMINATION = "anc_fv_obstetric_examination";
            public static final String TT_VACCINATION = "anc_fv_tt_vaccination";
            private static final String MEDICAL_AND_SURGICAL_HISTORY = "anc_fv_medical_and_surgical_history";
            private static final String BASELINE_INVESTIGATION = "anc_fv_baseline_investigation";
            private static final String TB_SCREENING = "anc_fv_tb_screening";
            private static final String MALARIA_INVESTIGATION = "anc_fv_malaria_investigation_form";

            public static String getMedicalAndSurgicalHistory() {
                return Utils.getLocalForm(MEDICAL_AND_SURGICAL_HISTORY, locale, assetManager);
            }

            public static String getBaselineInvestigation() {
                return Utils.getLocalForm(BASELINE_INVESTIGATION, locale, assetManager);
            }

            public static String getObstetricExamination() {
                return Utils.getLocalForm(OBSTETRIC_EXAMINATION, locale, assetManager);
            }


            public static String getTtVaccination() {
                return Utils.getLocalForm(TT_VACCINATION, locale, assetManager);
            }

            public static String getTbScreening() {
                return Utils.getLocalForm(TB_SCREENING, locale, assetManager);
            }

            public static String getMalariaInvestigation() {
                return Utils.getLocalForm(MALARIA_INVESTIGATION, locale, assetManager);
            }


        }

        public static class AncRecurringVisit {
            public static final String TRIAGE = "anc_rv_triage";
            public static final String CONSULTATION = "anc_rv_consultation";
            public static final String LAB_TESTS = "anc_rv_lab_test";
            public static final String BIRTH_REVIEW_AND_EMERGENCY_PLAN = "anc_rv_birth_review_and_emergency_plan";
            private static final String PHARMACY = "anc_rv_pharmacy";
            private static final String PREGNANCY_STATUS = "anc_rv_pregnancy_status";
            private static final String MALARIA_INVESTIGATION = "anc_rv_malaria_investigation_form";

            public static String getTriage() {
                return Utils.getLocalForm(TRIAGE, locale, assetManager);
            }

            public static String getConsultation() {
                return Utils.getLocalForm(CONSULTATION, locale, assetManager);
            }

            public static String getLabTests() {
                return Utils.getLocalForm(LAB_TESTS, locale, assetManager);
            }

            public static String getPharmacy() {
                return Utils.getLocalForm(PHARMACY, locale, assetManager);
            }

            public static String getPregnancyStatus() {
                return Utils.getLocalForm(PREGNANCY_STATUS, locale, assetManager);
            }

            public static String getBirthReviewAndEmergencyPlan() {
                return Utils.getLocalForm(BIRTH_REVIEW_AND_EMERGENCY_PLAN, locale, assetManager);
            }

            public static String getMalariaInvestigation() {
                return Utils.getLocalForm(MALARIA_INVESTIGATION, locale, assetManager);
            }
        }

        public static class LabourAndDeliveryRegistration {
            public static final String LABOUR_AND_DELIVERY_REGISTRATION_TRIAGE = "labour_and_delivery_registration_triage";
            public static final String LABOUR_AND_DELIVERY_REGISTRATION_TRUE_LABOUR_CONFIRMATION = "labour_and_delivery_registration_true_labour_confirmation";
            public static final String LABOUR_AND_DELIVERY_ADMISSION_INFORMATION = "labour_and_delivery_registration_admission_information";
            public static final String LABOUR_AND_DELIVERY_OBSTETRIC_HISTORY = "labour_and_delivery_registration_obstetric_history";
            public static final String LABOUR_AND_DELIVERY_ANC_CLINIC_FINDINGS = "labour_and_delivery_registration_anc_clinic_findings";
            public static final String LABOUR_AND_DELIVERY_CURRENT_LABOUR = "labour_and_delivery_registration_current_labour";
            public static final String LABOUR_AND_DELIVERY_LABOUR_STAGE = "labour_and_delivery_labour_stage";
            public static final String LABOUR_AND_DELIVERY_CERVIX_DILATION_MONITORING = "labour_and_delivery_cervix_dilation_monitoring";
            public static final String LABOUR_AND_DELIVERY_MODE_OF_DELIVERY = "labour_and_delivery_mode_of_delivery";
            public static final String LABOUR_AND_DELIVERY_PAST_OBSTETRIC_HISTORY = "labour_and_delivery_past_obstetric_history";

            public static String getLabourAndDeliveryRegistrationTriage() {
                return LABOUR_AND_DELIVERY_REGISTRATION_TRIAGE;
            }

            public static String getLabourAndDeliveryRegistrationTrueLabourConfirmation() {
                return LABOUR_AND_DELIVERY_REGISTRATION_TRUE_LABOUR_CONFIRMATION;
            }

            public static String getLabourAndDeliveryAdmissionInformation() {
                return LABOUR_AND_DELIVERY_ADMISSION_INFORMATION;
            }

            public static String getLabourAndDeliveryObstetricHistory() {
                return LABOUR_AND_DELIVERY_OBSTETRIC_HISTORY;
            }

            public static String getLabourAndDeliveryAncClinicFindings() {
                return LABOUR_AND_DELIVERY_ANC_CLINIC_FINDINGS;
            }

            public static String getLabourAndDeliveryCurrentLabour() {
                return LABOUR_AND_DELIVERY_CURRENT_LABOUR;
            }

            public static String getLabourAndDeliveryLabourStage() {
                return LABOUR_AND_DELIVERY_LABOUR_STAGE;
            }

            public static String getLabourAndDeliveryCervixDilationMonitoring() {
                return LABOUR_AND_DELIVERY_CERVIX_DILATION_MONITORING;
            }

            public static String getLabourAndDeliveryModeOfDelivery() {
                return LABOUR_AND_DELIVERY_MODE_OF_DELIVERY;
            }

            public static String getLabourAndDeliveryPastObstetricHistory() {
                return LABOUR_AND_DELIVERY_PAST_OBSTETRIC_HISTORY;
            }
        }

        public static class LabourAndDeliveryPartograph {
            public static final String PARTOGRAPH_FETAL_WELLBEING = "labour_and_delivery_fetal_well_being";
            public static final String PARTOGRAPH_MOTHER_WELLBEING = "labour_and_delivery_mother_well_being";
            public static final String PARTOGRAPH_PROGRESS_OF_LABOUR = "labour_and_delivery_labour_progress";
            public static final String PARTOGRAPH_TREATMENT_DURING_LABOUR = "labour_and_delivery_treatment_during_labour";
            public static final String PARTOGRAPH_TIME = "labour_and_delivery_partograph_time";

            public static String getFetalWellBingForm() {
                return PARTOGRAPH_FETAL_WELLBEING;
            }

            public static String getMotherWellBeingForm() {
                return PARTOGRAPH_MOTHER_WELLBEING;
            }

            public static String getProgressOfLabourForm() {
                return PARTOGRAPH_PROGRESS_OF_LABOUR;
            }

            public static String getTreatmentDuringLabourForm() {
                return PARTOGRAPH_TREATMENT_DURING_LABOUR;
            }

            public static String getPartographTimeForm() {
                return PARTOGRAPH_TIME;
            }

        }

        public static class LDVisit {

            public static final String LD_GENERAL_EXAMINATION = "labour_and_delivery_general_examination";
            public static final String LD_VAGINAL_EXAMINATION = "labour_and_delivery_vaginal_examination";
            public static final String LD_HIV_TEST = "labour_and_delivery_hiv_test";
            public static final String LD_BLOOD_GROUP_TEST = "labour_and_delivery_blood_group_test";
            public static final String LD_HB_TEST_FORM = "labour_and_delivery_hb_test_form";
            public static final String LD_SYPHILIS_TEST_FORM = "labour_and_delivery_syphilis_test";
            public static final String LD_MALARIA_TEST_FORM = "labour_and_delivery_malaria_test";

            public static String getLdGeneralExamination() {
                return Utils.getLocalForm(LD_GENERAL_EXAMINATION);
            }

            public static String getLdVaginalExamination() {
                return Utils.getLocalForm(LD_VAGINAL_EXAMINATION);
            }

            public static String getLdHivTest() {
                return Utils.getLocalForm(LD_HIV_TEST);
            }

            public static String getLdBloodGroupTest() {
                return Utils.getLocalForm(LD_BLOOD_GROUP_TEST);
            }

            public static String getLdHBTestForm() {
                return Utils.getLocalForm(LD_HB_TEST_FORM);
            }

            public static String getSyphilisTestForm() {
                return Utils.getLocalForm(LD_SYPHILIS_TEST_FORM);
            }

            public static String getLdMalariaTestForm() {
                return Utils.getLocalForm(LD_MALARIA_TEST_FORM);
            }

        }

        public static class LDActiveManagement {
            public static final String LD_ACTIVE_MANAGEMENT_UTERONICS = "labour_and_delivery_uterotonic";
            public static final String LD_ACTIVE_MANAGEMENT_EXPULSION_PLACENTA = "labour_and_delivery_placenta_and_membrane";
            public static final String LD_ACTIVE_MANAGEMENT_MASSAGE_UTERUS = "labour_and_delivery_uterus";
            public static final String LD_ACTIVE_ECLAMPSIA_MANAGEMENT= "labour_and_delivery_eclampsia_management_form";

            public static String getLDActiveManagementUteronics() {
                return Utils.getLocalForm(LD_ACTIVE_MANAGEMENT_UTERONICS);
            }

            public static String getLdActiveManagementExpulsionPlacenta() {
                return Utils.getLocalForm(LD_ACTIVE_MANAGEMENT_EXPULSION_PLACENTA);
            }

            public static String getLdActiveManagementMassageUterus() {
                return Utils.getLocalForm(LD_ACTIVE_MANAGEMENT_MASSAGE_UTERUS);
            }

            public static String getLdActiveEclampsiaManagement() {
                return Utils.getLocalForm(LD_ACTIVE_ECLAMPSIA_MANAGEMENT);
            }

        }

        public static class LDPostDeliveryMotherManagement {

            public static final String LD_POST_DELIVERY_MANAGEMENT_MOTHER_STATUS = "labour_and_delivery_mother_status";
            public static final String LD_POST_DELIVERY_MOTHER_OBSERVATION = "labour_and_delivery_mother_observation";
            public static final String LD_POST_DELIVERY_MATERNAL_COMPLICATIONS = "labour_and_delivery_maternal_complications";
            public static final String LD_NEW_BORN_STATUS = "labour_and_delivery_stage4_newborn";
            public static final String LD_POST_DELIVERY_FAMILY_PLANNING = "labour_and_delivery_stage4_family_planning";

            public static String getLdPostDeliveryManagementMotherStatus() {
                return Utils.getLocalForm(LD_POST_DELIVERY_MANAGEMENT_MOTHER_STATUS);
            }

            public static String getLdPostDeliveryMotherObservation() {
                return Utils.getLocalForm(LD_POST_DELIVERY_MOTHER_OBSERVATION);
            }

            public static String getLdPostDeliveryMaternalComplications() {
                return Utils.getLocalForm(LD_POST_DELIVERY_MATERNAL_COMPLICATIONS);
            }

            public static String getLdNewBornStatus() {
                return Utils.getLocalForm(LD_NEW_BORN_STATUS);
            }

            public static String getLdPostDeliveryFamilyPlanning() {
                return Utils.getLocalForm(LD_POST_DELIVERY_FAMILY_PLANNING);
            }

        }

        public static class VmmcVisit {

            public static final String FOLLOWUPVISIT = "vmmc_followup_visit";

            public static String getFollowupvisit() {
                return Utils.getLocalForm(FOLLOWUPVISIT, locale, assetManager);
            }

        }

    }

    public static final class JsonFormConstants {
        public static final String NAME_OF_HF = "name_of_hf";
        public static final String STEP1 = "step1";
    }

    public static final class DBConstants {
        public static final String CHW_REFERRAL_SERVICE = "chw_referral_service";
        public static final String ANC_MRDT_FOR_MALARIA = "mRDT_for_malaria";
        public static final String ANC_HIV = "hiv";
        public static final String ANC_SYPHILIS = "syphilis";
        public static final String ANC_HEPATITIS = "hepatitis";
        public static final String TASK_ID = "task_id";
        public static final String HEI_FOLLOWUP_FORM_SUBMISSION_ID = "hei_followup_form_submission_id";
        public static final String HEI_HIV_SAMPLE_ID = "sample_id";
        public static final String HEI_HIV_TEST_RESULT = "hiv_test_result";
        public static final String HEI_HIV_SAMPLE_COLLECTION_DATE = "sample_collection_date";
        public static final String HEI_HIV_RESULT_DATE = "hiv_test_result_date";
        public static final String HEI_HIV_TYPE_OF_TEST = "type_of_hiv_test";
        public static final String HIV_REGISTRATION_DATE = "hiv_registration_date";
        public static final String CAREGIVER_NAME = "caregiver_name";
        public static final String CAREGIVER_PHONE_NUMBER = "caregiver_phone_number";
    }

    public static final class EacVisitTypes {
        public static final String EAC_FIRST_VISIT = "EAC FIRST VISIT";
        public static final String EAC_SECOND_VISIT = "EAC SECOND VISIT";
    }

    public static final class JSON_FORM_EXTRA {
        public static final String RISK_CATEGORY = "risk_category";
        public static final String HIV_STATUS = "hiv";
    }

    public static final class HIV_STATUS {
        public static final String POSITIVE = "positive";
        public static final String NEGATIVE = "negative";
    }

    public static final class HeiHIVTestAtAge {
        public static final String AT_BIRTH = "At Birth";
        public static final String AT_6_WEEKS = "6 Weeks";
        public static final String AT_9_MONTHS = "9 Months";
        public static final String AT_15_MONTHS = "15 Months";
        public static final String AT_18_MONTHS = "18 Months";
    }

    public static final class FormConstants {

        public interface FormSubmissionFields {
            String VISIT_NUMBER = "visit_number";
            String FOLLOWUP_VISIT_DATE = "followup_visit_date";
            String FOLLOWUP_STATUS = "followup_status";
            String TYPE_OF_HIV_TEST = "type_of_hiv_test";
            String HIV_TEST_RESULT = "hiv_test_result";
            String HIV_TEST_RESULT_DATE = "hiv_test_result_date";
            String CTC_NUMBER = "ctc_number";
        }

        public interface ClinicFindings{

            interface Syphilis {
                String SYPHILIS_RESULT_POSITIVE = "positive";
                String SYPHILIS_RESULT_NEGATIVE = "negative";
                String SYPHILIS_TEST_NOT_DONE = "test_not_conducted";
            }

            interface Malaria {
                String MALARIA_RESULT_POSITIVE = "positive";
                String MALARIA_RESULT_NEGATIVE = "negative";
                String MALARIA_TEST_NOT_DONE = "test_not_conducted";
            }
        }

    }

    public static final class ReportConstants {
        public interface PMTCTReportKeys {
            String THREE_MONTHS = "three_months";
            String TWELVE_MONTHS = "twelve_months";
            String TWENTY_FOUR_MONTHS = "twenty_four_months";
            String EID_MONTHLY = "eid_monthly";
        }

        public interface CDPReportKeys {
            String ISSUING_AT_THE_FACILITY_REPORTS = "issuing_at_the_facility_reports";
            String ISSUING_FROM_THE_FACILITY_REPORTS = "issuing_from_the_facility_reports";
            String RECEIVING_REPORTS = "receiving_reports";
        }

        public interface VmmcKeys {
            String VMMC_REPORT = "vmmc_reports";

            String VMMC_SERVICE_REGISTER = "vmmc_service_register";

            String VMMC_THEATRE_REGISTER = "vmmc_theatre_register";
        }

        public interface ReportTypes {
            String PMTCT_REPORT = "pmtct_report";

            String ANC_REPORT = "anc_report";

            String PNC_REPORT = "pnc_report";

            String CBHS_REPORT = "cbhs_report";

            String LTFU_SUMMARY = "ltfu_report";

            String LD_REPORT = "ld_report";

            String MOTHER_CHAMPION_REPORT = "mother_champion_report";

            String SELF_TESTING_REPORT = "self_testing_report";

            String CONDOM_DISTRIBUTION_REPORT = "condom_distribution_report";

            String KVP_REPORT = "kvp_report";

            String VMMC_REPORT = "vmmc_report";

            String FP_REPORT = "fp_report";

            String SBC_REPORT = "sbc_report";

            String GBV_REPORT = "gbv_report";
        }

        public interface ReportPaths {
            String ANC_REPORT_PATH = "anc-taarifa-ya-mwezi";

            String PMTCT_3_MONTHS_REPORT_PATH = "pmtct-reports/taarifa-ya-miezi-3";

            String PMTCT_12_MONTHS_REPORT_PATH = "pmtct-reports/taarifa-ya-miezi-12";

            String PMTCT_24_MONTHS_REPORT_PATH = "pmtct-reports/taarifa-ya-miezi-24";

            String PMTCT_EID_MONTHLY_REPORT_PATH = "pmtct-reports/taarifa-cross-sectional";

            String PNC_REPORT_PATH = "pnc-taarifa-ya-mwezi";

            String CBHS_REPORT_PATH = "cbhs-taarifa-ya-mwezi";

            String LTFU_REPORT_PATH = "ltfu-summary-report";

            String LD_REPORT_PATH = "labour-delivery-taarifa-ya-mwezi";

            String MOTHER_CHAMPION_REPORT_PATH = "mother-champion-report";

            String SELF_TESTING_REPORT_PATH = "self-testing-report";

            String CONDOM_DISTRIBUTION_ISSUING_AT_THE_FACILITY_REPORT_PATH = "condom-distribution-issuing-report-at-the-facility";

            String CONDOM_DISTRIBUTION_ISSUING_FROM_THE_FACILITY_REPORT_PATH = "condom-distribution-issuing-report-from-the-facility";

            String CONDOM_DISTRIBUTION_RECEIVING_REPORT_PATH = "condom-distribution-receiving-report";

            String KVP_REPORT_PATH = "kvp-report";

            String VMMC_REPORT_PATH = "vmmc-report";

            String VMMC_REGISTER_PATH = "vmmc-service-register";

            String VMMC_THEATRE_REGISTER_PATH = "vmmc-theatre-register";

            String FP_REPORT_PATH = "fp-report";

            String SBC_REPORT_PATH = "sbc-report";

            String GBV_REPORT_PATH = "gbv-fac-report";
        }
    }

    public static final class DB {
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
    }

    public static final class LDFormFields {
        public interface GeneralExamination {

        }
        public interface VaginalExamination {
            String VAGINAL_EXAMINATION_DATE = "vaginal_exam_date";
            String VAGINAL_EXAMINATION_TIME = "vaginal_exam_time";
            String PRESENTING_PART = "presenting_part";
            String OCCIPUT_POSITION = "occiput_position";
            String MENTO_POSITION = "mento_position";
            String SACRO_POSITION = "sacro_position";
            String DORSO_POSITION = "dorso_position";
            String MOULDING = "moulding";
        }
    }

    public interface GENDER {
        String MALE = "male";
        String FEMALE = "female";
    }

}
