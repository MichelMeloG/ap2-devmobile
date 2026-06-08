package com.example.appmobile.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8000"; // Emulador Android
    // Para dispositivo físico, use: http://SEU_IP_LOCAL:8000
    
    private static Retrofit retrofit;
    
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    
    public static GearheadApiService getApiService() {
        return getRetrofitInstance().create(GearheadApiService.class);
    }
}
