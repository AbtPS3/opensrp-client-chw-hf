package org.smartregister.chw.hf.domain;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.hf.dao.ReportDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GbvMonthlyReportObject extends ReportObject {

    private final List<String> indicatorCodesWithAgeGroups = new ArrayList<>();

    private final String[] indicatorCodes = new String[]{"gbv-1", "gbv-2", "gbv-3a", "gbv-3b", "gbv-3c", "gbv-3d", "gbv-3e", "gbv-3f", "gbv-3g", "gbv-4", "gbv-5", "gbv-6", "gbv-7", "gbv-8", "gbv-9", "gbv-10", "gbv-11", "gbv-12", "gbv-13"};

    private final String[] clientSex = new String[]{"F", "M"};

    private final String[] indicatorAgeGroups = new String[]{"0-4", "5-9", "10-14", "15-17", "18-19", "20-24", "25-29", "30-34", "35-above"};

    private final Date reportDate;

    public GbvMonthlyReportObject(Date reportDate) {
        super(reportDate);
        this.reportDate = reportDate;
        setIndicatorCodesWithAgeGroups(indicatorCodesWithAgeGroups);
    }

    public static int calculateGbvSpecificTotal(HashMap<String, Integer> indicators, String specificKey) {
        int total = 0;

        for (Map.Entry<String, Integer> entry : indicators.entrySet()) {
            String key = entry.getKey().toLowerCase();
            Integer value = entry.getValue();

            // Create a Pattern object
            Pattern regexPattern = Pattern.compile(specificKey);

            // Create a Matcher object
            Matcher matcher = regexPattern.matcher(key);

            if (matcher.find()) {
                total += value;
            }
        }

        return total;
    }

    public void setIndicatorCodesWithAgeGroups(List<String> indicatorCodesWithAgeGroups) {
        for (String indicatorCode : indicatorCodes) {
            for (String indicatorKey : indicatorAgeGroups) {
                for (String clientType : clientSex) {
                    indicatorCodesWithAgeGroups.add(indicatorCode + "-" + indicatorKey + "-" + clientType);
                }
            }
        }

    }

    @Override
    public JSONObject getIndicatorData() throws JSONException {
        HashMap<String, Integer> indicatorsValues = new HashMap<>();
        JSONObject indicatorDataObject = new JSONObject();
        for (String indicatorCode : indicatorCodesWithAgeGroups) {
            int value = ReportDao.getReportPerIndicatorCode(indicatorCode, reportDate);
            indicatorsValues.put(indicatorCode, value);
            indicatorDataObject.put(indicatorCode, value);
        }

        // Calculate and add total values for "totals"
        for (String indicatorCode : indicatorCodes) {
            for (String sex : clientSex) {
                if (sex.equalsIgnoreCase("male")) {
                    indicatorDataObject.put(indicatorCode + "-totalMale", calculateGbvSpecificTotal(indicatorsValues, indicatorCode + "-" + sex));
                } else {
                    indicatorDataObject.put(indicatorCode + "-totalFemale", calculateGbvSpecificTotal(indicatorsValues, indicatorCode + "-" + sex));
                }
            }
        }

        return indicatorDataObject;
    }

}
