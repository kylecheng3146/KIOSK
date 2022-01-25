package com.lafresh.kiosk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.creditcardlib.Session;
import com.lafresh.kiosk.creditcardlib.SessionManager;
import com.lafresh.kiosk.creditcardlib.model.Code;
import com.lafresh.kiosk.creditcardlib.model.Device;
import com.lafresh.kiosk.creditcardlib.model.Request;
import com.lafresh.kiosk.creditcardlib.model.Response;
import com.lafresh.kiosk.creditcardlib.nccc.NCCCTransDataBean;
import com.lafresh.kiosk.httprequest.restfulapi.ApiServices;
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager;
import com.lafresh.kiosk.manager.OrderManager;
import com.lafresh.kiosk.model.LogParams;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Kevin on 2019-12-09.
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class NcccUtils {

    private static final String NCCC_FILE_NAME = "nccc";
    private static final String KEY_NCCC_CHECKOUT_DATE = "nccc_checkout_date";

    //限制只有一條執行緒在執行信用卡機
    private static ThreadPoolExecutor threadPoolExecutor =
            (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public static Response execute(final Request request, ProgressListener listener) {
        if (threadPoolExecutor.getActiveCount() > 1) {
            //若有執行緒在執行，直接回傳佔用結果
            return Response.errResponse(Code.LOCKED);
        }
        Callable<Response> creditCardTask = new Callable<Response>() {
            @Override
            public Response call() throws Exception {
                Session session = SessionManager.createSession(Device.NCCC);
                return session.execute(request);
            }
        };

        Future<Response> future = threadPoolExecutor.submit(creditCardTask);
        return waitAndGetNcccResponse(future, listener);
    }

    //會阻塞呼叫的執行緒
    private static Response waitAndGetNcccResponse(Future<Response> future, ProgressListener listener) {
        int seconds = 80;
        while (seconds > 0) {
            try {
                seconds--;
                return future.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                listener.progress(seconds);
            }
        }
        return Response.errResponse(Code.INTERRUPTED);
    }

    private static void addNcccRequestLog(Request request) {
        String requestLog = "NCCC Request = " + Json.toJson(request) + "\n" +
                Config.getAllConfig();
        Log.wtf("ReqLog", requestLog);
        OrderManager manager = OrderManager.getInstance();
        addLog(new
                LogParams(
                "kiosk",
                Config.shop_id,
                "order",
                manager.getOrderId(),
                "KIOSK",
                "",
                "INFO",
                requestLog,
                ""
        ));
        LogUtil.writeLogToLocalFile(requestLog);
    }

    private static void addLog(LogParams logParams) {
        RetrofitManager manager = RetrofitManager.getInstance();
        ApiServices apiServices = manager.getApiServices(Config.cacheUrl);
        apiServices.setLog(Config.token, logParams).enqueue(new Callback<retrofit2.Response<Void>>() {
            @Override
            public void onResponse(Call<retrofit2.Response<Void>> call, retrofit2.Response<retrofit2.Response<Void>> response) {
                if (response.isSuccessful()) {
                } else {
                }
            }

            @Override
            public void onFailure(Call<retrofit2.Response<Void>> call, Throwable t) {
            }
        });
    }

    private static void addNcccResponseLog(Response response) {
        NCCCTransDataBean dataBean = NCCCTransDataBean.generateRes(response.getData());
        String jsonString = Json.toJson(dataBean);
        String message = "NCCC Response = " + Json.toJson(response) + "\n" +
                "NCCC JsonString = " + jsonString + "\n";
        String configString = Config.getAllConfig();
        Log.wtf("ResLog", message + configString);
        OrderManager manager = OrderManager.getInstance();
        addLog(new
                LogParams(
                "kiosk",
                Config.shop_id,
                "order",
                manager.getOrderId(),
                "KIOSK",
                "",
                "INFO",
                message,
                ""
        ));
        LogUtil.writeLogToLocalFile(message + configString);
    }

    public static Runnable checkoutTask(NcccTaskListener checkListener, ProgressListener progressListener) {
        return ncccTask(getNcccCheckoutRequest(), checkListener, progressListener);
    }

    public static Runnable ncccTask(final Request request, final NcccTaskListener checkListener, final ProgressListener progressListener) {
        return new Runnable() {
            @Override
            public void run() {
                addNcccRequestLog(request);
                Response checkoutResponse = NcccUtils.execute(request, progressListener);
                addNcccResponseLog(checkoutResponse);
                checkListener.onFinish(checkoutResponse);
            }
        };
    }

    private static Request getNcccCheckoutRequest() {
        String dateString = TimeUtil.getNowTime("yyMMdd");
        String timeString = TimeUtil.getNowTime("HHmmss");
        String checkoutString = NCCCTransDataBean.getSettleData(dateString, timeString);
        return new Request(Config.creditCardComportPath, Config.creditCardBaudRate, checkoutString);
    }

    public static boolean isNotNcccCheckoutToday(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        final String checkoutDate = sp.getString(KEY_NCCC_CHECKOUT_DATE, "");
        String today = TimeUtil.getToday();
        return !today.equals(checkoutDate);
    }

    public static boolean isUseNccc() {
//        return true;
        return Config.useNcccByKiosk;
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(NCCC_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void updateCheckoutDate(Context context) {
        SharedPreferences sp = getSharedPreferences(context);
        sp.edit().putString(KEY_NCCC_CHECKOUT_DATE, TimeUtil.getToday()).apply();
    }

    public interface NcccTaskListener {
        void onFinish(Response response);
    }

    public interface ProgressListener {
        void progress(int second);
    }
}
