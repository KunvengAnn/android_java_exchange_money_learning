package com.example.exchange_money.Fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.exchange_money.R;

import com.example.exchange_money.Services.ApiClient;
import com.example.exchange_money.Services.ApiService;
import com.example.exchange_money.Utils.NetworkUtils;
import com.example.exchange_money.models.ExchangeRatesResponse;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConvertFragment extends Fragment {

    private NetworkUtils.NetworkChangeListener networkChangeListener;
    private String baseCurrency = "USD"; // Default base currency

    private TextView textViewShowCurrentValueCurrency;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_convert, container, false);

        textViewShowCurrentValueCurrency = view.findViewById(R.id.id_text_showCurrentValueMoney);
        textViewShowCurrentValueCurrency.setText("");

        setUpForEditText1(view);
        setUpForEditText2(view);
        return view;
    }
    // end onCreateView

    private void CheckInternetConnection() {
        // Register network change listener
        networkChangeListener = new NetworkUtils.NetworkChangeListener() {
            @Override
            public void onNetworkChanged(boolean isConnected) {
                if (isConnected) {
                    // Internet is connected, fetch exchange rates for the selected base currency
                    fetchExchangeRates(baseCurrency); // Fetch rates for the selected currency
                } else {
                    showMessage(getContext(), "No internet connection");
                }
            }
        };
        NetworkUtils.registerNetworkChangeReceiver(Objects.requireNonNull(requireContext()), networkChangeListener);
    }

    private void showMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void fetchExchangeRates(String base) {
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
                    } else {
                        Toast.makeText(getContext(), "Response body is null", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch exchange rates", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ExchangeRatesResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Unregister the network change listener when fragment view is destroyed
        if (networkChangeListener != null) {
            NetworkUtils.unregisterNetworkChangeReceiver(getContext(), (BroadcastReceiver) networkChangeListener);
        }
    }

    // base user i want exchange from
    private void setUpForEditText1(View view) {
        ImageView flagImage = view.findViewById(R.id.flag_image);
        EditText moneyAmount = view.findViewById(R.id.money_amount);

        // Set initial flag image and other logic
        flagImage.setImageResource(R.drawable.country_world);

        // Implement flag selection logic
        flagImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountrySelectionDialog(flagImage);
            }
        });
    }

    // this for show the value of the exchanged
    private void setUpForEditText2(View view) {
        ImageView flagImageExchanged = view.findViewById(R.id.flag_image_exchange2);
        EditText moneyExchangedAmount = view.findViewById(R.id.money_exchanged_amount);

        // Set initial flag image to English flag
        flagImageExchanged.setImageResource(R.drawable.united_kingdom);

        // Implement flag selection logic
        flagImageExchanged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountrySelectionDialog(flagImageExchanged);
            }
        });
    }

    private void showCountrySelectionDialog(final ImageView flagImage) {
        final String[] countries = {"Cambodia", "Vietnam", "United Kingdom"};
        final int[] flags = {R.drawable.cambodia_flag, R.drawable.vietnam, R.drawable.united_kingdom};

        final String[] currencies = {"KHR", "VND", "USD"};

        com.example.exchange_money.Fragments.CountryAdapter adapter = new com.example.exchange_money.Fragments.CountryAdapter(getContext(), countries, flags);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Country");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update the flag ImageView with the selected country's flag
                flagImage.setImageResource(flags[which]);

                baseCurrency = currencies[which]; // Update baseCurrency with selected currency code

                CheckInternetConnection();
            }
        });
        builder.show();
    }
}
