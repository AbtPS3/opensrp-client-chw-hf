package org.smartregister.chw.hf.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.chw.anc.repository.VisitRepository;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.repository.CoreChwRepository;
import org.smartregister.chw.core.repository.StockUsageReportRepository;
import org.smartregister.chw.core.utils.ChwDBConstants;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.hf.BuildConfig;
import org.smartregister.chw.hf.HealthFacilityApplication;
import org.smartregister.chw.hf.dao.FamilyDao;
import org.smartregister.domain.db.Column;
import org.smartregister.family.util.DBConstants;
import org.smartregister.immunization.repository.RecurringServiceRecordRepository;
import org.smartregister.immunization.repository.VaccineRepository;
import org.smartregister.immunization.util.IMDatabaseUtils;
import org.smartregister.reporting.ReportingLibrary;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.util.DatabaseMigrationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import timber.log.Timber;

public class HfChwRepository extends CoreChwRepository {

    private static String appVersionCodePref = "APP_VERSION_CODE";

    private Context context;

    public HfChwRepository(Context context, org.smartregister.Context openSRPContext) {
        super(context, AllConstants.DATABASE_NAME, BuildConfig.DATABASE_VERSION, openSRPContext.session(), CoreChwApplication.createCommonFtsObject(), openSRPContext.sharedRepositoriesArray());
        this.context = context;
    }

    private static void upgradeToVersion2(Context context, SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_EVENT_ID_COL);
            db.execSQL(VaccineRepository.EVENT_ID_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_FORMSUBMISSION_ID_COL);
            db.execSQL(VaccineRepository.FORMSUBMISSION_INDEX);

            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_OUT_OF_AREA_COL_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_HIA2_STATUS_COL);

            IMDatabaseUtils.accessAssetsAndFillDataBaseForVaccineTypes(context, db);

        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion2 ");
        }

    }

    private static void upgradeToVersion3(SQLiteDatabase db) {
        try {
            Column[] columns = {EventClientRepository.event_column.formSubmissionId};
            EventClientRepository.createIndex(db, EventClientRepository.Table.event, columns);

            db.execSQL(VaccineRepository.ALTER_ADD_CREATED_AT_COLUMN);
            VaccineRepository.migrateCreatedAt(db);

            db.execSQL(RecurringServiceRecordRepository.ALTER_ADD_CREATED_AT_COLUMN);
            RecurringServiceRecordRepository.migrateCreatedAt(db);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion3 ");
        }
        try {
            Column[] columns = {EventClientRepository.event_column.formSubmissionId};
            EventClientRepository.createIndex(db, EventClientRepository.Table.event, columns);


        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion3 %s", e.getMessage());
        }
    }

    private static void upgradeToVersion4(SQLiteDatabase db) {
        try {
            db.execSQL(AlertRepository.ALTER_ADD_OFFLINE_COLUMN);
            db.execSQL(AlertRepository.OFFLINE_INDEX);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_COL);
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_TEAM_ID_COL);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion4 ");
        }

    }

    private static void upgradeToVersion5(SQLiteDatabase db) {
        try {
            db.execSQL(VaccineRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
            db.execSQL(RecurringServiceRecordRepository.UPDATE_TABLE_ADD_CHILD_LOCATION_ID_COL);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion5 ");
        }
    }

    private static void upgradeToVersion6(SQLiteDatabase db) {
        try {
            StockUsageReportRepository.createTable(db);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static boolean checkIfAppUpdated() {
        String savedAppVersion = ReportingLibrary.getInstance().getContext().allSharedPreferences().getPreference(appVersionCodePref);
        if (savedAppVersion.isEmpty()) {
            return true;
        } else {
            int savedVersion = Integer.parseInt(savedAppVersion);
            return (BuildConfig.VERSION_CODE > savedVersion);
        }
    }

    private static void upgradeToVersion7(SQLiteDatabase db) {
        try {
            ReportingLibrary reportingLibraryInstance = ReportingLibrary.getInstance();
            String indicatorDataInitialisedPref = "INDICATOR_DATA_INITIALISED";

            boolean indicatorDataInitialised = Boolean.parseBoolean(reportingLibraryInstance.getContext().allSharedPreferences().getPreference(indicatorDataInitialisedPref));
            boolean isUpdated = checkIfAppUpdated();
            if (!indicatorDataInitialised || isUpdated) {

                String indicatorsConfigFile = "config/indicator-definitions.yml";
                String ancIndicatorConfigFile = "config/anc-reporting-indicator-definitions.yml";
                String pmtctIndicatorConfigFile = "config/pmtct-reporting-indicator-definitions.yml";
                String pncIndicatorConfigFile = "config/pnc-reporting-indicator-definitions.yml";
                String cbhsReportingIndicatorConfigFile = "config/cbhs-reporting-indicator-definitions.yml";
                String ldReportingIndicatorConfigFile = "config/ld-reporting-indicator-definitions.yml";
                String motherChampionReportingIndicatorConfigFile = "config/mother_champion-reporting-indicator-definitions.yml";
                String selfTestingIndicatorConfigFile = "config/self-testing-monthly-report.yml";
                String vmmcIndicatorConfigFile = "config/vmmc-monthly-report.yml";
                String kvpTestingIndicatorConfigFile = "config/kvp-monthly-report.yml";
                String ltfuIndicatorConfigFile = "config/community-ltfu-summary.yml";

                for (String configFile : Collections.unmodifiableList(
                        Arrays.asList(indicatorsConfigFile, ancIndicatorConfigFile,
                                pmtctIndicatorConfigFile, pncIndicatorConfigFile,
                                cbhsReportingIndicatorConfigFile, ldReportingIndicatorConfigFile,
                                motherChampionReportingIndicatorConfigFile, vmmcIndicatorConfigFile, selfTestingIndicatorConfigFile, kvpTestingIndicatorConfigFile, ltfuIndicatorConfigFile))) {
                    reportingLibraryInstance.readConfigFile(configFile, db);
                }

                reportingLibraryInstance.initIndicatorData(indicatorsConfigFile, db); // This will persist the data in the DB
                reportingLibraryInstance.getContext().allSharedPreferences().savePreference(indicatorDataInitialisedPref, "true");
                reportingLibraryInstance.getContext().allSharedPreferences().savePreference(appVersionCodePref, String.valueOf(org.smartregister.chw.core.BuildConfig.VERSION_CODE));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion10(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_family ADD COLUMN event_date VARCHAR; ");
            // add missing columns
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion10 ");
        }

        try {
            db.execSQL("UPDATE ec_family SET event_date = (select min(eventDate) from event where event.baseEntityId = ec_family.base_entity_id and event.eventType = 'Family Registration') where event_date is null;");
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion10 ");
        }

    }

    private static void upgradeToVersion11(SQLiteDatabase db) {
        try {
            List<String> columns = new ArrayList<>();
            columns.add(DBConstants.KEY.VILLAGE_TOWN);
            columns.add(ChwDBConstants.NEAREST_HEALTH_FACILITY);
            DatabaseMigrationUtils.addFieldsToFTSTable(db, CoreChwApplication.createCommonFtsObject(), CoreConstants.TABLE_NAME.FAMILY, columns);

        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion11 ");
        }
    }

    private static void upgradeToVersion12(SQLiteDatabase db) {
        try {
            db.execSQL(VisitRepository.ADD_VISIT_GROUP_COLUMN);
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion12");
        }
    }

    private static void upgradeToVersion14(SQLiteDatabase db) {
        try {
            // add missing columns
            db.execSQL("ALTER TABLE ec_ld_confirmation ADD COLUMN blood_group TEXT NULL;");
            db.execSQL("ALTER TABLE ec_ld_confirmation ADD COLUMN rh_factor TEXT NULL;");
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion14");
        }
    }

    private static void upgradeToVersion15(SQLiteDatabase db) {
        try {
            // add missing columns
            db.execSQL("ALTER TABLE ec_anc_register ADD COLUMN next_facility_visit_date TEXT NULL;");

            db.execSQL("ALTER TABLE ec_pregnancy_outcome ADD COLUMN next_facility_visit_date TEXT NULL;");

            db.execSQL("ALTER TABLE ec_ld_confirmation ADD COLUMN blood_group TEXT NULL;");
            db.execSQL("ALTER TABLE ec_ld_confirmation ADD COLUMN rh_factor TEXT NULL;");

            db.execSQL("ALTER TABLE ec_cdp_orders ADD COLUMN receiving_order_facility TEXT NULL;");
            db.execSQL("ALTER TABLE ec_cdp_stock_log ADD COLUMN other_issuing_organization TEXT NULL;");
            db.execSQL("ALTER TABLE ec_cdp_stock_log ADD COLUMN condom_brand TEXT NULL;");

            db.execSQL("ALTER TABLE ec_kvp_register ADD COLUMN client_group TEXT NULL;");
            db.execSQL("ALTER TABLE ec_kvp_register ADD COLUMN prep_assessment TEXT NULL;");

            db.execSQL("ALTER TABLE ec_prep_register ADD COLUMN prep_status TEXT NULL;");
            db.execSQL("ALTER TABLE ec_prep_register ADD COLUMN prep_initiation_date TEXT NULL;");
            db.execSQL("ALTER TABLE ec_prep_register ADD COLUMN hbv_test_date TEXT NULL;");
            db.execSQL("ALTER TABLE ec_prep_register ADD COLUMN hcv_test_date TEXT NULL;");
            db.execSQL("ALTER TABLE ec_prep_register ADD COLUMN crcl_test_date TEXT NULL;");
            db.execSQL("ALTER TABLE ec_prep_register ADD COLUMN crcl_results TEXT NULL;");


            DatabaseMigrationUtils.createAddedECTables(db,
                    new HashSet<>(Arrays.asList("ec_cdp_issuing_hf", "ec_kvp_bio_medical_services", "ec_kvp_behavioral_services", "ec_kvp_structural_services", "ec_kvp_other_services", "ec_prep_followup")),
                    HealthFacilityApplication.createCommonFtsObject());
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion15");
        }
    }

    private static void upgradeToVersion16(SQLiteDatabase db) {
        try {
            // add missing columns
            db.execSQL("ALTER TABLE ec_cbhs_register ADD COLUMN provider_id TEXT NULL;");
            db.execSQL("ALTER TABLE ec_cdp_stock_log ADD COLUMN condom_brand TEXT NULL;");

        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion16");
        }
    }

    private static void upgradeToVersion17(SQLiteDatabase db) {
        try {
            refreshIndicatorQueries(db);
            db.execSQL("ALTER TABLE ec_kvp_register ADD COLUMN enrollment_date TEXT NULL;");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion18(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_prep_register ADD COLUMN next_visit_date TEXT NULL;");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion19(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_child ADD COLUMN child_hepatitis_b_vaccination TEXT NULL;");
            db.execSQL("ALTER TABLE ec_child ADD COLUMN child_vitamin_k_injection TEXT NULL;");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion20(SQLiteDatabase db) {
        try {
            refreshIndicatorQueries(db);
            db.execSQL("ALTER TABLE ec_ltfu_feedback ADD COLUMN last_appointment_date TEXT NULL;");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private static void upgradeToVersion21(SQLiteDatabase db) {
        try {
            DatabaseMigrationUtils.createAddedECTables(db,
                    new HashSet<>(Arrays.asList("ec_vmmc_enrollment", "ec_vmmc_services", "ec_vmmc_procedure", "ec_vmmc_post_op_and_discharge", "ec_vmmc_follow_up_visit", "ec_vmmc_notifiable_ae")),
                    HealthFacilityApplication.createCommonFtsObject());

            refreshIndicatorQueries(db);
            db.execSQL("ALTER TABLE ec_ltfu_feedback ADD COLUMN IF NOT EXISTS last_appointment_date TEXT NULL;");
            DatabaseMigrationUtils.createAddedECTables(db, new HashSet<>(Arrays.asList("ec_anc_partner_community_followup", "ec_sbc_register", "ec_sbc_visit","ec_sbc_mobilization_session","ec_kvp_prep_register")), HealthFacilityApplication.createCommonFtsObject());
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion21");
        }
    }

    private static void upgradeToVersion22(SQLiteDatabase db) {
        try {
            DatabaseMigrationUtils.createAddedECTables(db,
                    new HashSet<>(Collections.singletonList("ec_gbv_register")),
                    HealthFacilityApplication.createCommonFtsObject());
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion22");
        }
    }

    private static void upgradeToVersion10ForBaSouth(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_family_member ADD COLUMN reasons_for_registration TEXT NULL;");
            db.execSQL("ALTER TABLE ec_family_member ADD COLUMN has_primary_caregiver VARCHAR;");
            db.execSQL("ALTER TABLE ec_family_member ADD COLUMN primary_caregiver_name VARCHAR;");

            DatabaseMigrationUtils.createAddedECTables(db,
                    new HashSet<>(Arrays.asList("ec_hiv_register", "ec_hiv_outcome", "ec_hiv_community_followup", "ec_tb_register", "ec_tb_outcome", "ec_tb_community_followup", "ec_hiv_community_feedback", "ec_tb_community_feedback")),
                    HealthFacilityApplication.createCommonFtsObject());
        } catch (Exception e) {
            Timber.e(e, "upgradeToVersion10");
        }
    }

    private static void refreshIndicatorQueries(SQLiteDatabase db) {
        try {
            ReportingLibrary reportingLibraryInstance = ReportingLibrary.getInstance();
            String indicatorDataInitialisedPref = "INDICATOR_DATA_INITIALISED";

            boolean indicatorDataInitialised = Boolean.parseBoolean(reportingLibraryInstance.getContext().allSharedPreferences().getPreference(indicatorDataInitialisedPref));
            boolean isUpdated = checkIfAppUpdated();

            //Refreshing all indicator queries adding grouping to all indicator queries which is going to be utilized in synchronization of monthly tallies to the server
            if (!indicatorDataInitialised || isUpdated) {
                db.execSQL("DELETE FROM indicator_queries");
                db.execSQL("DELETE FROM indicators");

                String indicatorsConfigFile = "config/indicator-definitions.yml";
                String ancIndicatorConfigFile = "config/anc-reporting-indicator-definitions.yml";
                String pmtctIndicatorConfigFile = "config/pmtct-reporting-indicator-definitions.yml";
                String pncIndicatorConfigFile = "config/pnc-reporting-indicator-definitions.yml";
                String cbhsReportingIndicatorConfigFile = "config/cbhs-reporting-indicator-definitions.yml";
                String ldReportingIndicatorConfigFile = "config/ld-reporting-indicator-definitions.yml";
                String motherChampionReportingIndicatorConfigFile = "config/mother_champion-reporting-indicator-definitions.yml";
                String selfTestingIndicatorConfigFile = "config/self-testing-monthly-report.yml";
                String kvpTestingIndicatorConfigFile = "config/kvp-monthly-report.yml";
                String ltfuIndicatorConfigFile = "config/community-ltfu-summary.yml";
                String fpIndicatorConfigFile = "config/fp-reporting-indicator-definitions.yml";
                String vmmcIndicatorConfigFile = "config/vmmc-monthly-report.yml";


                for (String configFile : Collections.unmodifiableList(
                        Arrays.asList(indicatorsConfigFile, ancIndicatorConfigFile,
                                pmtctIndicatorConfigFile, pncIndicatorConfigFile,
                                cbhsReportingIndicatorConfigFile, ldReportingIndicatorConfigFile,
                                motherChampionReportingIndicatorConfigFile, selfTestingIndicatorConfigFile, kvpTestingIndicatorConfigFile, ltfuIndicatorConfigFile, vmmcIndicatorConfigFile, fpIndicatorConfigFile))) {
                    reportingLibraryInstance.readConfigFile(configFile, db);
                }

                reportingLibraryInstance.initIndicatorData(indicatorsConfigFile, db); // This will persist the data in the DB
                reportingLibraryInstance.getContext().allSharedPreferences().savePreference(indicatorDataInitialisedPref, "true");
                reportingLibraryInstance.getContext().allSharedPreferences().savePreference(appVersionCodePref, String.valueOf(BuildConfig.VERSION_CODE));
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Timber.w(HfChwRepository.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        int upgradeTo = oldVersion + 1;
        while (upgradeTo <= newVersion) {
            switch (upgradeTo) {
                case 2:
                    upgradeToVersion2(context, db);
                    break;
                case 3:
                    upgradeToVersion3(db);
                    break;
                case 4:
                    upgradeToVersion4(db);
                    break;
                case 5:
                    upgradeToVersion5(db);
                    break;
                case 6:
                    upgradeToVersion6(db);
                    break;
                case 7:
                    upgradeToVersion7(db);
                    break;
                case 8:
                    upgradeToVersion8(db);
                    break;
                case 9:
                    upgradeToVersion9(db);
                    break;
                case 10:
                    upgradeToVersion10(db);
                    upgradeToVersion10ForBaSouth(db);
                    break;
                case 11:
                    upgradeToVersion11(db);
                    break;
                case 12:
                    upgradeToVersion12(db);
                    break;
                case 13:
                    upgradeToVersion14(db);
                    break;
                case 14:
                    upgradeToVersion15(db);
                    break;
                case 16:
                    upgradeToVersion16(db);
                    break;
                case 17:
                    upgradeToVersion17(db);
                    break;
                case 18:
                    upgradeToVersion18(db);
                    break;
                case 19:
                    upgradeToVersion19(db);
                    break;
                case 20:
                    upgradeToVersion20(db);
                    break;
                case 21:
                    upgradeToVersion21(db);
                    break;
                case 22:
                    upgradeToVersion22(db);
                    break;
                default:
                    break;
            }
            upgradeTo++;
        }
    }

    private void upgradeToVersion8(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE ec_family ADD COLUMN entity_type VARCHAR; " +
                    "UPDATE ec_family SET entity_type = 'ec_family' WHERE id is not null;");

            List<String> columns = new ArrayList<>();
            columns.add(CoreConstants.DB_CONSTANTS.DETAILS);
            columns.add(DBConstants.KEY.ENTITY_TYPE);
            DatabaseMigrationUtils.addFieldsToFTSTable(db, CoreChwApplication.createCommonFtsObject(), CoreConstants.TABLE_NAME.FAMILY, columns);

        } catch (Exception e) {
            Timber.e(e, "commonUpgrade -> Failed to add column 'entity_type' and 'details' to ec_family_search ");
        }
    }

    private void upgradeToVersion9(SQLiteDatabase db) {
        try {
            FamilyDao.migrateAddLocationIdColSQLString(db);
            FamilyDao.migrateInsertLocationIDs(db);
        } catch (Exception ex) {
            Timber.e(ex, "Problems adding sync location ids");
        }
    }
}
