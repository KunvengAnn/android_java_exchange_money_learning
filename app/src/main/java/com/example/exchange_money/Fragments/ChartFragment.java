package com.example.exchange_money.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.exchange_money.Componets.LoadingDialogUtils;
import com.example.exchange_money.R;
import com.example.exchange_money.Services.ApiClient;
import com.example.exchange_money.Services.ApiService;
import com.example.exchange_money.models.ExchangeRatesResponse;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChartFragment extends Fragment {

    private LineChart lineChart;
    private final Map<String, Double> rates = new HashMap<>();
    private final String baseCurrency = "USD";
    private String valueBaseCurrency = "";
    private final String[] targetCurrencies = {"KHR", "USD", "VND"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        lineChart = view.findViewById(R.id.lineChart);

        setupChart();
        fetchCurrentExchangeRates(baseCurrency);
        return view;
    }

    private void setupChart() {
        lineChart.setDescription(null);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setDrawGridLines(false);


        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String currency = rates.keySet().toArray(new String[0])[(int) e.getX()];
                double rate = e.getY();
                String message = valueBaseCurrency + " " + baseCurrency + ": " + rate + " " + currency;
                new AlertDialog.Builder(getContext())
                        .setTitle("Exchange Rate Details")
                        .setMessage(message)
                        .setPositiveButton("OK", null)
                        .show();
            }

            @Override
            public void onNothingSelected() {
                // Do nothing
            }
        });
    }

    private void fetchCurrentExchangeRates(String baseCurrency) {
        LoadingDialogUtils.showCustomLoadingDialog(getContext());
        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ExchangeRatesResponse> call = api.getExchangeRates(baseCurrency);

        call.enqueue(new Callback<ExchangeRatesResponse>() {
            @Override
            public void onResponse(@NonNull Call<ExchangeRatesResponse> call, @NonNull Response<ExchangeRatesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rates.clear();

                    for (String currency : targetCurrencies) {
                        Double exchangeRate = response.body().getConversionRates().get(currency);
                        if (exchangeRate != null) {
                            rates.put(currency, exchangeRate);
                        } else {
                            showMessage("No exchange rate available for " + currency);
                        }
                    }

                    valueBaseCurrency = String.valueOf(response.body().getConversionRates().get(baseCurrency));
                    updateChart(rates);

                    LoadingDialogUtils.dismissCustomLoadingDialog();
                } else {
                    showMessage("Failed to fetch current exchange rates");
                    LoadingDialogUtils.dismissCustomLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<ExchangeRatesResponse> call, Throwable t) {
                showMessage("Error: " + t.getMessage());
                LoadingDialogUtils.dismissCustomLoadingDialog();
            }
        });
    }

    private void updateChart(Map<String, Double> rates) {
        ArrayList<Entry> entries = new ArrayList<>();

        for (int index = 0; index < targetCurrencies.length; index++) {
            String currency = targetCurrencies[index];
            Double rate = rates.get(currency);
            if (rate != null) {
                entries.add(new Entry(index, rate.floatValue()));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Exchange Rates");
        dataSet.setColor(getResources().getColor(R.color.blue));
        dataSet.setValueTextColor(getResources().getColor(R.color.black));
        dataSet.setLineWidth(2f);
        dataSet.setValueTextSize(10f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    private void showMessage(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
