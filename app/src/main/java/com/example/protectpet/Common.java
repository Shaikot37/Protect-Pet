package com.example.protectpet;

import com.example.protectpet.remotes.GoogleApiService;
import com.example.protectpet.remotes.RetrofitBuilder;

public class Common {
    private static final String BASE_URL = "https://maps.googleapis.com/";

    public static GoogleApiService getGoogleApiService() {
        return RetrofitBuilder.builder().create(GoogleApiService.class);
    }
}
