package com.lafresh.kiosk.httprequest.restfulapi;

import com.lafresh.kiosk.httprequest.model.Auth;
import com.lafresh.kiosk.httprequest.model.AuthParameter;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.hamcrest.core.IsNull.notNullValue;

public class AuthApiTest {
    MockWebServer mockWebServer = new MockWebServer();

    @Test
    public void getToken() throws IOException {
        enqueueResponse();
        ApiServices authApi = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiServices.class);
        Call<Auth> call = authApi.getToken(new AuthParameter());
        Response<Auth> response = call.execute();
        mockWebServer.shutdown();
        Assert.assertThat(response.body(), notNullValue());
        Assert.assertThat(response.body().getToken(), notNullValue());
    }

    private void enqueueResponse() throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("api-response/auth.json");
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        MockResponse mockResponse = new MockResponse();
        mockWebServer.enqueue(mockResponse.setBody(source.readString(StandardCharsets.UTF_8)));
    }
}
