package com.lafresh.kiosk.manager;

import android.content.Context;

import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.cache.DataCache;
import com.lafresh.kiosk.httprequest.ApiRequest;
import com.lafresh.kiosk.httprequest.GetBannerApiRequest;
import com.lafresh.kiosk.httprequest.model.Banners;
import com.lafresh.kiosk.httprequest.model.GetBannerRes;
import com.lafresh.kiosk.httprequest.restfulapi.ApiServices;
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerManager {
    private static BannerManager manager;
    private GetBannerRes bannerRes;
    private BannerListener listener = null;
    private List<Banners.Banner> bannerList = new ArrayList<>();


    public interface BannerListener {
        void onSuccess();

        void onFail();
    }

    public void setListener(BannerListener l) {
        listener = l;
    }

    private BannerManager(Context context) {
        DataCache<GetBannerRes> cache = new DataCache<GetBannerRes>();
        bannerRes = cache.get(context, GetBannerRes.class);
    }

    public GetBannerRes getBannerRes() {
        return bannerRes;
    }

    public List<Banners.Banner> getBannerList() {
        return bannerList;
    }

    public static BannerManager getInstance(Context context) {
        if (manager == null) {
            manager = new BannerManager(context);

        }
        return manager;
    }

    public void getBanners() {
        ApiServices apiServices = RetrofitManager.getInstance().getApiServices(Config.cacheUrl);
        Call<Banners> call = apiServices.getBanners(Config.token, "KIOSK", Config.shop_id);
        call.enqueue(new Callback<Banners>() {
            @Override
            public void onResponse(Call<Banners> call, Response<Banners> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        bannerList = response.body().getBannerList();
                    }
                    if (listener != null) {
                        listener.onSuccess();
                    }
                } else {
                    if (listener != null) {
                        listener.onSuccess();
                    }
                }
            }

            @Override
            public void onFailure(Call<Banners> call, Throwable t) {
                if (listener != null) {
                    listener.onSuccess();
                }
            }
        });
    }

    public void requestBannerFromServer(final Context context) {
        final ApiRequest.ApiListener apiListener = new ApiRequest.ApiListener() {
            @Override
            public void onSuccess(ApiRequest apiRequest, String body) {
                try {
                    DataCache<GetBannerRes> cache = new DataCache<GetBannerRes>();
                    cache.set(context, GetBannerRes.class, body);
                    bannerRes = cache.get(context, GetBannerRes.class);
                    if (listener != null)
                        listener.onSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null)
                        listener.onFail();
                }
            }

            @Override
            public void onFail() {
                if (listener != null)
                    listener.onFail();
            }
        };
        GetBannerApiRequest getBannerApiRequest = new GetBannerApiRequest();
        getBannerApiRequest.setApiListener(apiListener).go();
    }
}
