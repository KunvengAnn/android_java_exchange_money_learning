package com.example.exchange_money.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.exchange_money.Componets.LoadingDialogUtils;
import com.example.exchange_money.R;

import com.example.exchange_money.Services.ApiClient;
import com.example.exchange_money.Services.ApiService;
import com.example.exchange_money.Utils.NetworkUtils;
import com.example.exchange_money.models.ExchangeRatesResponse;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvertFragment extends Fragment {

    //private NetworkUtils.NetworkChangeListener networkChangeListener;
    private BroadcastReceiver networkChangeListener;

    private String baseCurrency = "USD"; // Default base currency

    private TextView textViewShowCurrentValueCurrency;
    private EditText EditText_Amount_Exchanged;
    private EditText money_amount;
    private Button btn_exchange ;

    private String CountryChangeTo = ""; // default
    private String CountryBase = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_convert, container, false);

        // initial
        btn_exchange = view.findViewById(R.id.btn_exchange);
        EditText_Amount_Exchanged = view.findViewById(R.id.money_exchanged_amount);
        money_amount = view.findViewById(R.id.money_amount);

        textViewShowCurrentValueCurrency = view.findViewById(R.id.id_text_showCurrentValueMoney);
        textViewShowCurrentValueCurrency.setText("");

        setUpForEditText1(view);
        setUpForEditText2(view);

        BtnExchangeMoney();
        return view;
    }
    // end onCreateView

    private void BtnExchangeMoney(){
        btn_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationEditTextValue();
            }
        });
    }

    private void validationEditTextValue(){
        if(CountryBase.isEmpty()){
            showMessage(getContext(),"Select Country");
            return;
        } else if(money_amount.getText().toString().trim().isEmpty()){
            showMessage(getContext(),"Input Money for convert");
            return;
        }else{
            CheckInternetConnection();
        }
    }

    private void CheckInternetConnection() {
        // Register network change listener
        networkChangeListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isConnected = NetworkUtils.isNetworkConnected(context);
                if (isConnected) {
                    fetchExchangeRates(baseCurrency);
                } else {
                    showMessage(getContext(), "No internet connection");
                }
            }
        };

        NetworkUtils.registerNetworkChangeReceiver(requireContext(), networkChangeListener);
    }


    private void showMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void fetchExchangeRates(String base) {
        LoadingDialogUtils.showCustomLoadingDialog(getContext());

        ApiService api = ApiClient.getClient().create(ApiService.class);
        Call<ExchangeRatesResponse> call = api.getExchangeRates(base);

        call.enqueue(new Callback<ExchangeRatesResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ExchangeRatesResponse> call, @NonNull Response<ExchangeRatesResponse> response) {
                if (response.isSuccessful()) {
                    ExchangeRatesResponse exchangeRates = response.body();
                    if (exchangeRates != null) {
                        // Process exchangeRates data here
                        Toast.makeText(getContext(), "Exchange rates fetched successfully", Toast.LENGTH_SHORT).show();
                        // Log all data from ExchangeRatesResponse
                        Log.d("ExchangeRates", "Base Code: " + exchangeRates.getBaseCode());
                        Log.d("ExchangeRates", "Time last update UTC: " + exchangeRates.getTimeLastUpdateUtc());
                        Log.d("ExchangeRates", "Conversion rates:");

                        double NumberExchangeRate = exchangeRates.getConversionRates().get(baseCurrency);

                        textViewShowCurrentValueCurrency.setText("Current Currency: " + NumberExchangeRate + " " + baseCurrency + " = ");

                        if(CountryChangeTo.isEmpty()){
                            CountryChangeTo = "USD";
                        }
                        Log.d("Country", "country item: "+CountryChangeTo);

                        double currencyWantChange =  exchangeRates.getConversionRates().get(CountryChangeTo);
                        double ResultExchanged = currencyWantChange * Double.parseDouble(money_amount.getText().toString().trim());
                        String ResultExchangedFormatted = formatNumber(ResultExchanged, "#,###.00");

                        // show result exchanged to the editText
                        EditText_Amount_Exchanged.setText(ResultExchangedFormatted);

                        // Iterate through specific currencies (VND, USD, KHR)
                        for (String currency : new String[]{"VND", "USD", "KHR"}) {
                            double exchangeRate = exchangeRates.getConversionRates().get(currency);

                            // Update UI with exchange rate for each specific currency
                            textViewShowCurrentValueCurrency.append("\n" + currency + ": " + exchangeRate);

                            Log.d("ExchangeRates", currency + ": " + exchangeRate);
                        }

                        for (String currency : exchangeRates.getConversionRates().keySet()) {
                            Log.d("ExchangeRates", currency + ": " + exchangeRates.getConversionRates().get(currency));
                        }
                        LoadingDialogUtils.dismissCustomLoadingDialog();
                    } else {
                        Toast.makeText(getContext(), "Response body is null", Toast.LENGTH_SHORT).show();
                        LoadingDialogUtils.dismissCustomLoadingDialog();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch exchange rates", Toast.LENGTH_SHORT).show();
                    LoadingDialogUtils.dismissCustomLoadingDialog();
                }
            }

            @Override
            public void onFailure(Call<ExchangeRatesResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                LoadingDialogUtils.dismissCustomLoadingDialog();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (networkChangeListener != null) {
            NetworkUtils.unregisterNetworkChangeReceiver(requireContext(), networkChangeListener);
        }
    }

    // base user i want exchange from
    private void setUpForEditText1(View view) {
        ImageView flagImage = view.findViewById(R.id.flag_image);

        // Set initial flag image and other logic
        flagImage.setImageResource(R.drawable.country_world);

        // Implement flag selection logic
        flagImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountrySelectionDialog1(flagImage);
            }
        });

        money_amount = view.findViewById(R.id.money_amount);
    }

    // this for show the value of the exchanged
    private void setUpForEditText2(View view) {
        ImageView flagImageExchanged = view.findViewById(R.id.flag_image_exchange2);

        // Set initial flag image to English flag
        flagImageExchanged.setImageResource(R.drawable.united_kingdom);

        // Implement flag selection logic
        flagImageExchanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountrySelectionDialog2(flagImageExchanged);
            }
        });
    }

    private void showCountrySelectionDialog1(final ImageView flagImage) {
        final String[] countries = {"Select","Cambodia", "Vietnam", "United Kingdom"};
        final int[] flags = {R.drawable.country_world,R.drawable.cambodia_flag, R.drawable.vietnam, R.drawable.united_kingdom};

        final String[] currencies = {"","KHR", "VND", "USD"};

        com.example.exchange_money.Fragments.CountryAdapter adapter = new com.example.exchange_money.Fragments.CountryAdapter(getContext(), countries, flags);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Country");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update the flag ImageView with the selected country's flag
                flagImage.setImageResource(flags[which]);

                CountryBase = countries[which];

                baseCurrency = currencies[which];

            }
        });
        builder.show();

    }

    private void showCountrySelectionDialog2(final ImageView flagImage) {
        final String[] countries = {"Cambodia", "Vietnam", "United Kingdom"};
        final int[] flags = {R.drawable.cambodia_flag, R.drawable.vietnam, R.drawable.united_kingdom};

        final String[] currencies = {"KHR", "VND", "USD"};

        com.example.exchange_money.Fragments.CountryAdapter adapter = new com.example.exchange_money.Fragments.CountryAdapter(getContext(), countries, flags);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Country");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CountryChangeTo = currencies[which];

                flagImage.setImageResource(flags[which]);
            }
        });
        builder.show();
    }

    // format number
    public String formatNumber(double number, String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        return decimalFormat.format(number);
    }
}
