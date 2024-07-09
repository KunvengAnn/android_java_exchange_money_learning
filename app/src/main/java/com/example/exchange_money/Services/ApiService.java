package com.example.exchange_money.Services;

import com.example.exchange_money.models.ExchangeRatesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    @GET("latest/{base}")
    Call<ExchangeRatesResponse> getExchangeRates(@Path("base") String base);
}
