package com.example.protectpet.fragment;

import com.example.protectpet.Notifications.MyResponse;
import com.example.protectpet.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA-iMKrW4:APA91bFCrRb4HEpnRPANhBQLeVt3Iv4J4DVb44bieGTTLMjvaYqE8Sx0ErKcbNZSFd2tkjPA2GzhvN-v2ClTVj76l5hOeKVGQLTsXQ1G3eOJ5WcAWs5DpvCc51F8P6qCm8GbtmZkH0ZT"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
