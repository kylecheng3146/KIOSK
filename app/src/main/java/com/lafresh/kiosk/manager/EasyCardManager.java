package com.lafresh.kiosk.manager;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.activity.EasyCardActivity;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.fragment.AlertDialogFragment;
import com.lafresh.kiosk.easycard.EasyCard;
import com.lafresh.kiosk.easycard.EasyCardPay;
import com.lafresh.kiosk.easycard.EcResTag;
import com.lafresh.kiosk.easycard.model.BatchNumber;
import com.lafresh.kiosk.easycard.model.BatchNumbers;
import com.lafresh.kiosk.easycard.model.EasyCardData;
import com.lafresh.kiosk.easycard.model.EcCheckoutData;
import com.lafresh.kiosk.easycard.model.EcSettle3;
import com.lafresh.kiosk.easycard.model.EcSettle3Parameter;
import com.lafresh.kiosk.httprequest.ApiRequest;
import com.lafresh.kiosk.httprequest.model.CardInfo;
import com.lafresh.kiosk.httprequest.restfulapi.ApiServices;
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager;
import com.lafresh.kiosk.model.EasycardDailyCheckout;
import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.utils.CommonUtils;
import com.lafresh.kiosk.utils.Json;
import com.lafresh.kiosk.utils.LogUtil;
import com.lafresh.kiosk.utils.ProgressUtils;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EasyCardManager {
    private static EasyCardManager manager;
    public static HashMap<String, Integer> easyCardTryTime = new HashMap<>();
    private static ApiServices apiServices;
    private EasyCardListener easyCardListener;
    private boolean isEasyCardCheckout = false;
    private boolean isLoopCheckout = false;
    private List<BatchNumber> batchNumberList;

    public EasyCardManager() {
        RetrofitManager manager = RetrofitManager.getInstance();
        apiServices = manager.getApiServices(Config.cacheUrl);
    }

    public static EasyCardManager getInstance() {
        if (manager == null) {
            manager = new EasyCardManager();
        }
        return manager;
    }

    //讀取悠遊卡參數
    public void initEcParameter(final Context context) {
        final EasyCardPay easyCardPay = new EasyCardPay((Activity) context);
        EasyCardPay.Listener listener = new EasyCardPay.Listener() {
            @Override
            public void onResult(String result) {
                getBatchNumber(context, easyCardPay);
            }

            @Override
            public void onNoICERINI() {
                if (easyCardListener != null) {
                    easyCardListener.onFailed();
                }
            }
        };
        easyCardPay.setListener(listener);
        easyCardPay.restoreFile();
    }

    private void getBatchNumber(Context context, EasyCardPay easyCardPay) {
        boolean hasLegacyData = EasyCard.hasOldData(easyCardPay.getEasyCardData());
        if (hasLegacyData) {
            EasyCard.getEasyCardData().batchNo = EasyCard.getBatchNo(easyCardPay.getEasyCardData());
            easyCardPay.deleteLocalEasyCardData();
            signOnEcDongle(easyCardPay, context);
        } else {
            Call<BatchNumber> call = apiServices.getBatchNumber(Config.token, Config.shop_id, Config.kiosk_id);
            call.enqueue(new Callback<BatchNumber>() {
                @Override
                public void onResponse(Call<BatchNumber> call, Response<BatchNumber> response) {
                    if (response.isSuccessful()) {
                        BatchNumber batchNumber = response.body();
                        EasyCard.getEasyCardData().batchNo = batchNumber.getBatchNumber();
                        EasyCard.getEasyCardData().isNewBatchNumber = batchNumber.isNewBatchNumber();
                        signOnEcDongle(easyCardPay, context);
                    } else {
                        easyCardListener.onFailed();
                    }
                }

                @Override
                public void onFailure(Call<BatchNumber> call, Throwable t) {
                    easyCardListener.onFailed();
                }
            });
        }
    }

    public void getUserInfo(String cardNo, EasyCardActivity.OnLoginListener loginListener) {
        Call<CardInfo> call = apiServices.getUserInfo(Config.token, cardNo);
        call.enqueue(new Callback<CardInfo>() {
            @Override
            public void onResponse(Call<CardInfo> call, Response<CardInfo> response) {
                if (response.isSuccessful()) {
                    loginListener.onSuccess(response.body());
                } else {
                    loginListener.onFailure();
                }
            }

            @Override
            public void onFailure(Call<CardInfo> call, Throwable t) {
                loginListener.onFailure();
            }
        });
    }


    private void signOnEcDongle(EasyCardPay easyCardPay, Context context) {
        easyCardPay.setListener(getSignOnListener(easyCardPay, context));
        easyCardPay.signOn();
    }

    private EasyCardPay.Listener getSignOnListener(final EasyCardPay easyCardPay, final Context context) {
        return new EasyCardPay.Listener() {
            @Override
            public void onResult(String result) {
                LogUtil.writeLogToLocalFile("SignOn回傳:" + result);
                String resultCode = easyCardPay.findTag(result, EcResTag.T3901);
                String t3904 = easyCardPay.findTag(result, EcResTag.T3904);
                boolean isSuccess = "0".equals(resultCode);
                boolean isBroken = t3904 != null && t3904.startsWith("66") && "-119".equals(resultCode);

                if (isSuccess) {
                    checkoutOrFinish(easyCardPay, context);
                } else if (isBroken) {
                    //請暫停使用悠遊卡交易，並請進行報修換機流程
                    CommonUtils.showMessage(context,context.getString(R.string.ezCardDongleError));
                    easyCardListener.onFailed();
                } else {
                    easyCardListener.onFailed();
                }
            }

            @Override
            public void onNoICERINI() {
                if (easyCardListener != null) {
                    easyCardListener.onFailed();
                }
            }
        };
    }

    private void checkoutOrFinish(EasyCardPay easyCardPay, Context context) {
        EasyCardData data = easyCardPay.getEasyCardData();
        if (data.isNewBatchNumber) {
            isEasyCardCheckout = true;
            easyCardPay.release();
            easyCardListener.onSuccess();
        } else {
            checkoutEasyCard(context, easyCardPay, data.batchNo);
        }
    }

    private void checkoutEasyCard(Context context, EasyCardPay easyCardPay, String batchNumber) {
        Call<EcSettle3> call = apiServices.getEcSettle3(
                Config.token, Config.shop_id, Config.kiosk_id, batchNumber);
        call.enqueue(new Callback<EcSettle3>() {
            @Override
            public void onResponse(Call<EcSettle3> call, Response<EcSettle3> response) {
                if (response.isSuccessful()) {
                    EcSettle3 ecSettle3 = response.body();
                    EcCheckoutData ecCheckoutData = EasyCard.parseCheckoutData(ecSettle3);
                    String totCount = String.valueOf(ecSettle3.getTotal_Txn_count());
                    String totAmt = String.valueOf(ecSettle3.getTotal_Txn_amt());
                    easyCardPay.setListener(getCheckoutListener(easyCardPay, ecCheckoutData, context));
                    easyCardPay.checkout(totCount, totAmt);
                } else {
                    showCheckoutFailDialog(context, easyCardPay, batchNumber);
                }
            }

            @Override
            public void onFailure(Call<EcSettle3> call, Throwable t) {
                showCheckoutFailDialog(context, easyCardPay, batchNumber);
            }
        });
    }

    private void showCheckoutFailDialog(Context context, EasyCardPay easyCardPay, String batchNumber) {
        String msg = context.getString(R.string.skipCheckoutMsg);
        if (isLoopCheckout) {
            msg = context.getString(R.string.loopCheckoutMsg);
        }
        View.OnClickListener listener = v -> {
            checkoutEasyCard(context, easyCardPay, batchNumber);
        };
        showAlertDialog((Activity) context, msg, listener, easyCardPay);
    }

    public ApiRequest.ApiListener getEcSettleDataListener(final EasyCardPay easyCardPay, final Context context) {
        return new ApiRequest.ApiListener() {
            @Override
            public void onSuccess(ApiRequest apiRequest, String body) {
                EcCheckoutData ecCheckoutData = Json.fromJson(body, EcCheckoutData.class);
                if (ecCheckoutData.getCode() == 0) {
                    String totCount = String.valueOf(ecCheckoutData.getTotal_Txn_count());
                    easyCardPay.setListener(getCheckoutListener(easyCardPay, ecCheckoutData, context));
                    easyCardPay.checkout(totCount, ecCheckoutData.getTotal_Txn_amt());
                } else {
                    onFail();
                }
            }

            @Override
            public void onFail() {
                ProgressUtils.getInstance().hideProgressDialog();
                easyCardListener.onFailed();
            }
        };
    }

    //悠遊卡結帳Callback
    private EasyCardPay.Listener getCheckoutListener(final EasyCardPay easyCardPay,
                                                     final EcCheckoutData ecCheckoutData,
                                                     final Context context) {
        return new EasyCardPay.Listener() {
            @Override
            public void onResult(String result) {
                LogUtil.writeLogToLocalFile("悠遊卡結帳結果\n卡機回傳:" + result);
                String resultCode = easyCardPay.findTag(result, EcResTag.T3901);
                String t3904 = easyCardPay.findTag(result, EcResTag.T3904);
                boolean isSuccess = "0".equals(resultCode);
                boolean isBroken = t3904 != null && t3904.startsWith("66") && "-119".equals(resultCode);

                if (isSuccess) {
                    EcCheckoutData resData = EasyCard.parseCheckoutData(result, easyCardPay, ecCheckoutData);
                    setEcSettle3(context, easyCardPay, ecCheckoutData);
                    if (!isLoopCheckout && KioskPrinter.getPrinter().isConnect()) {
                        KioskPrinter.getPrinter().printEasyCardCheckOutData(resData);
                    }
                } else if (isBroken) {
                    //請暫停使用悠遊卡交易，並請進行報修換機流程
                    CommonUtils.showMessage(context, context.getString(R.string.ezCardDongleError));
                    easyCardListener.onFailed();
                } else {
                    easyCardListener.onFailed();
                }
            }

            @Override
            public void onNoICERINI() {
                if (easyCardListener != null) {
                    easyCardListener.onFailed();
                }
            }
        };
    }

    private void setEcSettle3(Context context, EasyCardPay easyCardPay, EcCheckoutData ecCheckoutData) {
        EcSettle3Parameter parameter = EasyCard.parseEcSettle3Parameter(ecCheckoutData);
        Call<Response<Void>> call = apiServices.setEcSettle3(Config.token, parameter);
        call.enqueue(new Callback<Response<Void>>() {
            @Override
            public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
                if (response.isSuccessful()) {
                    isEasyCardCheckout = true;
                    String msg = String.format(
                            context.getString(R.string.easyCardCheckoutSuccess),
                            ecCheckoutData.getBatch_number());
                    easyCardListener.onProgress(msg);
                    if (isLoopCheckout) {
                        loopCheckout(context, easyCardPay);
                    } else {
                        getBatchNumber(context, easyCardPay);
                    }
                } else {
                    showCheckoutFailDialog(context, easyCardPay, ecCheckoutData.getBatch_number());
                }
            }

            @Override
            public void onFailure(Call<Response<Void>> call, Throwable t) {
                showCheckoutFailDialog(context, easyCardPay, ecCheckoutData.getBatch_number());
            }
        });
    }

    public void checkoutAllUnCheckout(Context context) {
        apiServices.getUnUploadBatchNumber(Config.token, Config.shop_id, Config.kiosk_id)
                .enqueue(new Callback<BatchNumbers>() {
                    @Override
                    public void onResponse(Call<BatchNumbers> call, Response<BatchNumbers> response) {
                        if (response.isSuccessful()) {
                            batchNumberList = response.body().getBatchNumberList();
                            if (batchNumberList == null) {
                                easyCardListener.onSuccess();
                                return;
                            }
                            String msg = String.format(
                                    context.getString(R.string.needCheckoutBatch),
                                    batchNumberList.size());
                            easyCardListener.onProgress(msg);
                            EasyCardPay easyCardPay = new EasyCardPay((Activity) context);
                            isLoopCheckout = true;
                            loopCheckout(context, easyCardPay);
                        } else {
                            //查無資料回傳錯誤代碼，故直接認為此步驟完成
                            updateEasycardDailyCheckout();
                            easyCardListener.onSuccess();
                        }
                    }

                    @Override
                    public void onFailure(Call<BatchNumbers> call, Throwable t) {
                        easyCardListener.onFailed();
                    }
                });
    }

    public void updateEasycardDailyCheckout() {
        apiServices.updateEasycardDailyCheckout(Config.token, Config.shop_id, new EasycardDailyCheckout(Config.kiosk_id,true))
                .enqueue(new Callback<Response<Void>>() {
                    @Override
                    public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
                        if (response.isSuccessful()) {
                        }else{
                        }
                    }

                    @Override
                    public void onFailure(Call<Response<Void>> call, Throwable t) {
                    }
                });
    }

    private void loopCheckout(Context context, EasyCardPay easyCardPay) {
        if (batchNumberList != null && batchNumberList.size() > 0) {
            String batchNumber = batchNumberList.get(0).getBatchNumber();
            batchNumberList.remove(0);
            EasyCardData checkoutData = new EasyCardData();
            checkoutData.batchNo = batchNumber;
            checkoutData.tradeNo = "990000";//避免與日常扣款重複
            easyCardPay.setEasyCardData(checkoutData);//替換當日使用的批號與流水號
            String msg = String.format(context.getString(R.string.proceedEasyCardCheckout), batchNumber);
            easyCardListener.onProgress(msg);
            checkoutEasyCard(context, easyCardPay, batchNumber);
        } else {
            easyCardPay.setEasyCardData(EasyCard.getEasyCardData());//換回當日使用的批號與流水號
            isLoopCheckout = false;
            batchNumberList = null;
            easyCardPay.release();
            easyCardListener.onSuccess();
        }
    }

    private void showAlertDialog(Activity activity, String msg,
                                 View.OnClickListener listener, EasyCardPay easyCardPay) {
        activity.runOnUiThread(() -> {
            AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
            alertDialogFragment
                    .setTitle(R.string.errorOccur)
                    .setMessage(msg)
                    .setConfirmButton(R.string.retry, v -> {
                        listener.onClick(v);
                        alertDialogFragment.dismiss();
                    })
                    .setCancelButton(R.string.skip, v -> {
                        if (!isLoopCheckout) {
                            isEasyCardCheckout = false;
                        } else {
                            //換回當日使用的批號與流水號
                            easyCardPay.setEasyCardData(EasyCard.getEasyCardData());
                            isLoopCheckout = false;
                        }
                        easyCardListener.onSuccess();
                        alertDialogFragment.dismiss();
                    })
                    .setUnCancelAble()
                    .show(activity.getFragmentManager(), AlertDialogFragment.MESSAGE_DIALOG);
        });
    }

    public boolean isEasyCardCheckout() {
        return isEasyCardCheckout;
    }

    public interface EasyCardListener {
        void onSuccess();

        void onFailed();

        void onProgress(String msg);
    }

    public void setEasyCardListener(EasyCardListener l) {
        easyCardListener = l;
    }
}
