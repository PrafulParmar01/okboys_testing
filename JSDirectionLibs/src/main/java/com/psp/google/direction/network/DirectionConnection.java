package com.psp.google.direction.network;

import com.psp.google.direction.config.GoogleDirectionConfiguration;
import com.psp.google.direction.constant.DirectionUrl;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DirectionConnection {
    private static DirectionConnection connection;

    public static DirectionConnection getInstance() {
        if (connection == null) {
            connection = new DirectionConnection();
        }
        return connection;
    }

    private DirectionService service;

    public DirectionService createService() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(getClient())
                    .baseUrl(DirectionUrl.MAPS_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(DirectionService.class);
        }
        return service;
    }

    private OkHttpClient getClient() {
        OkHttpClient client = GoogleDirectionConfiguration.getInstance().getCustomClient();
        if (client != null) {
            return client;
        }
        return createDefaultClient();
    }

    private OkHttpClient createDefaultClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (GoogleDirectionConfiguration.getInstance().isLogEnabled()) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }
}
