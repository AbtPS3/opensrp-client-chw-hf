package org.smartregister.chw.hf.utils;

import static org.smartregister.chw.hf.utils.Constants.ReportConstants.PMTCTReportKeys.EID_MONTHLY;
import static org.smartregister.chw.hf.utils.Constants.ReportConstants.PMTCTReportKeys.THREE_MONTHS;
import static org.smartregister.chw.hf.utils.Constants.ReportConstants.PMTCTReportKeys.TWELVE_MONTHS;
import static org.smartregister.chw.hf.utils.Constants.ReportConstants.PMTCTReportKeys.TWENTY_FOUR_MONTHS;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.webkit.WebViewAssetLoader;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.smartregister.chw.hf.domain.FpMonthlyReportObject;
import org.smartregister.chw.hf.domain.GbvMonthlyReportObject;
import org.smartregister.chw.hf.domain.anc_reports.AncMonthlyReportObject;
import org.smartregister.chw.hf.domain.cbhs_reports.CbhsMonthlyReportObject;
import org.smartregister.chw.hf.domain.cdp_reports.CdpIssuingAtFacilityReportObject;
import org.smartregister.chw.hf.domain.cdp_reports.CdpIssuingFromFacilityReportObject;
import org.smartregister.chw.hf.domain.cdp_reports.CdpReceivingReportObject;
import org.smartregister.chw.hf.domain.kvp_reports.KvpMonthlyReportObject;
import org.smartregister.chw.hf.domain.ld_reports.LdMonthlyReportObject;
import org.smartregister.chw.hf.domain.ltfu_summary.LTFUSummaryObject;
import org.smartregister.chw.hf.domain.mother_champion_repots.MotherChampionReportObject;
import org.smartregister.chw.hf.domain.pmtct_reports.Pmtct12MonthsReportObject;
import org.smartregister.chw.hf.domain.pmtct_reports.Pmtct24MonthsReportObject;
import org.smartregister.chw.hf.domain.pmtct_reports.Pmtct3MonthsReportObject;
import org.smartregister.chw.hf.domain.pmtct_reports.PmtctEIDMonthlyReportObject;
import org.smartregister.chw.hf.domain.pnc_reports.PncMonthlyReportObject;
import org.smartregister.chw.hf.domain.self_testing_reports.SelfTestingMonthlyReportObject;
import org.smartregister.chw.hf.domain.vmmc_reports.VmmcMonthlyReportObject;
import org.smartregister.chw.hf.domain.vmmc_reports.VmmcServiceRegisterObject;
import org.smartregister.chw.hf.domain.vmmc_reports.VmmcTheatreRegisterObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class ReportUtils {
    private static final int year = Calendar.getInstance().get(Calendar.YEAR);
    private static final int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
    public static String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"};
    private static String printJobName;
    private static String reportPeriod;

    public static String getDefaultReportPeriod() {
        String monthString = String.valueOf(month);
        if (month < 10) {
            monthString = "0" + monthString;
        }
        return monthString + "-" + year;
    }

    public static int getMonth() {
        return month;
    }

    public static int getYear() {
        return year;
    }

    public static String displayMonthAndYear(int month, int year) {
        return monthNames[month] + ", " + year;
    }

    public static String displayMonthAndYear() {
        return monthNames[getMonth() - 1] + ", " + getYear();
    }

    public static String getPrintJobName() {
        return printJobName;
    }

    public static void setPrintJobName(String printJobName) {
        ReportUtils.printJobName = printJobName;
    }

    public static Date getReportDate() {
        if (StringUtils.isNotBlank(reportPeriod)) {

            try {
                return new SimpleDateFormat("MM-yyyy", Locale.getDefault()).parse(reportPeriod);
            } catch (ParseException e) {
                Timber.e(e);
            }
        }

        return new Date();
    }

    public static String getReportPeriod() {
        return reportPeriod;
    }

    public static void setReportPeriod(String reportPeriod) {
        ReportUtils.reportPeriod = reportPeriod;
    }

    public static String getReportPeriodForCohortReport(String reportKey) {
        int minusPeriod;
        switch (reportKey) {
            case THREE_MONTHS:
                minusPeriod = 3;
                break;
            case TWELVE_MONTHS:
                minusPeriod = 12;
                break;
            case TWENTY_FOUR_MONTHS:
                minusPeriod = 24;
                break;
            case EID_MONTHLY:
                return reportPeriod;
            default:
                minusPeriod = 0;
                break;
        }
        return getReportPeriodWithStartingMonth(minusPeriod);
    }

    private static String getReportPeriodWithStartingMonth(int minusPeriod) {
        try {
            DateTime endTime = new DateTime(new SimpleDateFormat("MM-yyyy", Locale.getDefault()).parse(reportPeriod));
            DateTime startTime = endTime.minusMonths(minusPeriod);

            return "" + startTime.getMonthOfYear() + "-" + startTime.getYear() + " to " + endTime.getMonthOfYear() + "-" + endTime.getYear();

        } catch (ParseException e) {
            Timber.e(e);
        }
        return reportPeriod;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void printTheWebPage(WebView webView, Context context) {

        // Creating  PrintManager instance
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(getPrintJobName());

        // Create a print job with name and adapter instance
        assert printManager != null;
        printManager.print(getPrintJobName(), printAdapter,
                new PrintAttributes.Builder().build());
    }

    @SuppressLint("SetJavaScriptEnabled")
    public static void loadReportView(String reportPath, WebView mWebView, ProgressBar progressBar, Context context, String reportType) {
        progressBar.setVisibility(View.VISIBLE);
        mWebView.setVisibility(View.GONE);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(context))
                .build();
        mWebView.setWebViewClient(new LocalContentWebViewClient(assetLoader,mWebView,progressBar));
        mWebView.addJavascriptInterface(new HfWebAppInterface(context, reportType), "Android");

        if (reportType.equals(Constants.ReportConstants.ReportTypes.CONDOM_DISTRIBUTION_REPORT)){
            mWebView.loadUrl("https://appassets.androidplatform.net/assets/reports/cdp_reports/" + reportPath + ".html");
        } else if(reportType.equals(Constants.ReportConstants.ReportTypes.VMMC_REPORT)){
            mWebView.loadUrl("https://appassets.androidplatform.net/assets/reports/vmmc_reports/" + reportPath + ".html");
        }
        else {
            mWebView.loadUrl("https://appassets.androidplatform.net/assets/reports/" + reportPath + ".html");
        }

    }

    @SuppressLint("SetJavaScriptEnabled")  //overloaded
    public static void loadReportView(WebView mWebView) {
        mWebView.clearCache(true);
        mWebView.clearMatches();
        mWebView.reload();
        mWebView.refreshDrawableState();
    }

    public static class PMTCTReports {
        public static String computeThreeMonths(Date startDate) {
            Pmtct3MonthsReportObject pmtct3MonthsReportObject = new Pmtct3MonthsReportObject(startDate);
            try {
                return pmtct3MonthsReportObject.getIndicatorDataAsGson(pmtct3MonthsReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }

        public static String computeTwelveMonths(Date startDate) {
            Pmtct12MonthsReportObject pmtct12MonthsReportObject = new Pmtct12MonthsReportObject(startDate);
            try {
                return pmtct12MonthsReportObject.getIndicatorDataAsGson(pmtct12MonthsReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }

        public static String computeTwentyFourMonths(Date startDate) {
            Pmtct24MonthsReportObject pmtct24MonthsReportObject = new Pmtct24MonthsReportObject(startDate);
            try {
                return pmtct24MonthsReportObject.getIndicatorDataAsGson(pmtct24MonthsReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }

        public static String computeEIDMonthly(Date startDate) {
            PmtctEIDMonthlyReportObject pmtctEIDMonthlyReportObject = new PmtctEIDMonthlyReportObject(startDate);
            try {
                return pmtctEIDMonthlyReportObject.getIndicatorDataAsGson(pmtctEIDMonthlyReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }
    }

    public static class PNCReports {
        public static String computePncReport(Date now) {
            String report = "";
            PncMonthlyReportObject pncMonthlyReportObject = new PncMonthlyReportObject(now);
            try {
                report = pncMonthlyReportObject.getIndicatorDataAsGson(pncMonthlyReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class ANCReports {
        public static String computeAncReport(Date now) {
            String report = "";
            AncMonthlyReportObject ancMonthlyReportObject = new AncMonthlyReportObject(now);
            try {
                report = ancMonthlyReportObject.getIndicatorDataAsGson(ancMonthlyReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class CBHSReports {
        public static String computeCbhsReport(Date now) {
            String report = "";
            CbhsMonthlyReportObject cbhsMonthlyReportObject = new CbhsMonthlyReportObject(now);
            try {
                report = cbhsMonthlyReportObject.getIndicatorDataAsGson(cbhsMonthlyReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class LDReports {
        public static String computeLdReport(Date now) {
            String report = "";
            LdMonthlyReportObject ldMonthlyReportObject = new LdMonthlyReportObject(now);
            try {
                report = ldMonthlyReportObject.getIndicatorDataAsGson(ldMonthlyReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class MotherChampionReports {
        public static String computeMotherChampionReport(Date now) {
            String report = "";
            MotherChampionReportObject motherChampionReportObject = new MotherChampionReportObject(now);
            try {
                report = motherChampionReportObject.getIndicatorDataAsGson(motherChampionReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class LTFUReports {
        public static String computeLTFUReport(Date now) {
            String report = "";
            LTFUSummaryObject ltfuSummaryObject = new LTFUSummaryObject(now);
            try {
                report = ltfuSummaryObject.getIndicatorDataAsGson(ltfuSummaryObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class SelfTestingReport {
        public static String computeSelfTestingReportReport(Date now) {
            String report = "";
            SelfTestingMonthlyReportObject selfTestingMonthlyReportObject = new SelfTestingMonthlyReportObject(now);
            try {
                report = selfTestingMonthlyReportObject.getIndicatorDataAsGson(selfTestingMonthlyReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class KvpReport {
        public static String computeReport(Date now) {
            String report = "";
            KvpMonthlyReportObject kvpMonthlyReportObject = new KvpMonthlyReportObject(now);
            try {
                report = kvpMonthlyReportObject.getIndicatorDataAsGson(kvpMonthlyReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class VmmcReport {
        public static String computeReport(Date now) {
            String report = "";
            VmmcMonthlyReportObject vmmcMonthlyReportObject = new VmmcMonthlyReportObject(now);
            try {
                report = vmmcMonthlyReportObject.getIndicatorDataAsGson(vmmcMonthlyReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class VmmcServiceRegister {
        public static String computeReport(Date now) {
            String report = "";
            VmmcServiceRegisterObject vmmcServiceRegisterObject = new VmmcServiceRegisterObject(now);
            try {
                report = vmmcServiceRegisterObject.getIndicatorDataAsGson(vmmcServiceRegisterObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class VmmcTheatreRegister {
        public static String computeReport(Date now) {
            String report = "";
            VmmcTheatreRegisterObject vmmcTheatreRegisterObject = new VmmcTheatreRegisterObject(now);
            try {
                report = vmmcTheatreRegisterObject.getIndicatorDataAsGson(vmmcTheatreRegisterObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class CDPReports {
        public static String computeIssuingAtFacilityReports(Date startDate) {
            CdpIssuingAtFacilityReportObject cdpIssuingAtFacilityReportObject = new CdpIssuingAtFacilityReportObject(startDate);
            try {
                return cdpIssuingAtFacilityReportObject.getIndicatorDataAsGson(cdpIssuingAtFacilityReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }

        public static String computeIssuingFromFacilityReports(Date startDate) {
            CdpIssuingFromFacilityReportObject cdpIssuingFromFacilityReportObject = new CdpIssuingFromFacilityReportObject(startDate);
            try {
                return cdpIssuingFromFacilityReportObject.getIndicatorDataAsGson(cdpIssuingFromFacilityReportObject.getIndicatorData());
            } catch (JSONException e) {
                Timber.e(e);
            }
            return "";
        }
    }

    public static class CBHSReport {
        public static String computeReport(Date now) {
            String report = "";
            CdpReceivingReportObject cdpReceivingReportObject = new CdpReceivingReportObject(now);
            try {
                report = cdpReceivingReportObject.getIndicatorDataAsGson(cdpReceivingReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class FpReport {
        public static String computeReport(Date now) {
            String report = "";
            FpMonthlyReportObject fpMonthlyReportObject = new FpMonthlyReportObject(now);
            try {
                report = fpMonthlyReportObject.getIndicatorDataAsGson(fpMonthlyReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }

    public static class GbvReport {
        public static String computeReport(Date now) {
            String report = "";
            GbvMonthlyReportObject gbvMonthlyReportObject = new GbvMonthlyReportObject(now);
            try {
                report = gbvMonthlyReportObject.getIndicatorDataAsGson(gbvMonthlyReportObject.getIndicatorData());
            } catch (Exception e) {
                Timber.e(e);
            }
            return report;
        }
    }
}
