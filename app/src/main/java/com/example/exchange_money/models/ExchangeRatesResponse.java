package com.example.exchange_money.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ExchangeRatesResponse {
    private String result;
    private String documentation;
    @SerializedName("terms_of_use")
    private String termsOfUse;
    @SerializedName("time_last_update_unix")
    private long timeLastUpdateUnix;
    @SerializedName("time_last_update_utc")
    private String timeLastUpdateUtc;
    @SerializedName("time_next_update_unix")
    private long timeNextUpdateUnix;
    @SerializedName("time_next_update_utc")
    private String timeNextUpdateUtc;
    @SerializedName("base_code")
    private String baseCode;
    @SerializedName("conversion_rates")
    private Map<String, Double> conversionRates;

    // Getter and Setter methods
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getTermsOfUse() {
        return termsOfUse;
    }

    public void setTermsOfUse(String termsOfUse) {
        this.termsOfUse = termsOfUse;
    }

    public long getTimeLastUpdateUnix() {
        return timeLastUpdateUnix;
    }

    public void setTimeLastUpdateUnix(long timeLastUpdateUnix) {
        this.timeLastUpdateUnix = timeLastUpdateUnix;
    }

    public String getTimeLastUpdateUtc() {
        return timeLastUpdateUtc;
    }

    public void setTimeLastUpdateUtc(String timeLastUpdateUtc) {
        this.timeLastUpdateUtc = timeLastUpdateUtc;
    }

    public long getTimeNextUpdateUnix() {
        return timeNextUpdateUnix;
    }

    public void setTimeNextUpdateUnix(long timeNextUpdateUnix) {
        this.timeNextUpdateUnix = timeNextUpdateUnix;
    }

    public String getTimeNextUpdateUtc() {
        return timeNextUpdateUtc;
    }

    public void setTimeNextUpdateUtc(String timeNextUpdateUtc) {
        this.timeNextUpdateUtc = timeNextUpdateUtc;
    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public Map<String, Double> getConversionRates() {
        return conversionRates;
    }

    public void setConversionRates(Map<String, Double> conversionRates) {
        this.conversionRates = conversionRates;
    }
}
