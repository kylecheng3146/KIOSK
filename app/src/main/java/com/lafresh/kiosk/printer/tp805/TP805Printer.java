package com.lafresh.kiosk.printer.tp805;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;

import com.lafresh.kiosk.Bill;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.creditcardlib.nccc.NCCCTransDataBean;
import com.lafresh.kiosk.dialog.PrinterErrorDialog;
import com.lafresh.kiosk.easycard.model.EcCheckoutData;
import com.lafresh.kiosk.easycard.model.EcPayData;
import com.lafresh.kiosk.httprequest.model.Invoice;
import com.lafresh.kiosk.httprequest.model.OrderResponse;
import com.lafresh.kiosk.httprequest.model.WebOrder01;
import com.lafresh.kiosk.httprequest.model.WebOrder02;
import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.printer.model.Ticket;
import com.lafresh.kiosk.utils.CommonUtils;
import com.lafresh.kiosk.utils.ComputationUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import print.Print;

/**
 * Created by Kevin on 2020/7/3.
 */

public class TP805Printer extends KioskPrinter {
    public static final int VENDOR_ID = 1900;
    public static final int PRODUCT_ID = 770;

    private Context context;
    private TP805BlueToothReceiver receiver;
    private boolean connected = true;
    public static final String receivedOrder = "received.order.refresh.page.list";
    IntentFilter mIntentFilter = new IntentFilter();

    public TP805Printer(Context context) {
        this.context = context;
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(receivedOrder);
//        setReceiver();
    }

    private void setReceiver() {
        receiver = new TP805BlueToothReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(receiver, filter);
    }

    @Override
    public void releaseAll(Context context) {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
        if (isConnect()) {
            disconnect();
        }
        context = null;
    }

    @Override
    public void requestPrinterPermission(Context context) {
        requestUsbPermission(context, VENDOR_ID, PRODUCT_ID);
    }

    @Override
    public boolean disconnect() {
        return true;
    }

    @Override
    public boolean isConnect() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    @Override
    public void requestUsbPermission(Context context, int vendorId, int productId) {
        super.requestUsbPermission(context, vendorId, productId);
    }

    @Override
    public int printEInvoice(Context context) {
        Invoice invoice = Bill.fromServer.getOrderResponse().getInvoice();
        String testGuiNo = context.getString(R.string.testGuiNo);
        String guiNo = invoice.getGUI_number();
        return super.printEInvoice(context);
    }

    @Override
    public int printPaymentDetail(Context context) {
        return super.printPaymentDetail(context);
    }

    @Override
    public void laterPrinterCheckFlow(final Context context, final PrinterErrorDialog.Listener dialogListener, final PrinterCheckFlowListener printerCheckFlowListener, int second) {
        new CountDownTimer(second * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                printerStatusCheck(context, dialogListener, printerCheckFlowListener, false);
            }

            @Override
            public void onFinish() {
            }
        }.start();
    }

    public void printerStatusCheck(final Context context, final PrinterErrorDialog.Listener dialogListener, final PrinterCheckFlowListener printerCheckFlowListener, final boolean beforePrint) {
        String msg = "";
        try {
            byte[] statusData4 = setPrinterCheckValue(4);

            for (byte b: statusData4){
                if(String.format("0x%20x", b).equals("0x                  72")){
                    msg = "偵測無紙";
                }
            }
            byte[] statusData2 = setPrinterCheckValue(2);
            for (byte b: statusData2){
                if(String.format("0x%20x", b).equals("0x                  16")){
                    msg = "上蓋開啟";
                }

                if(String.format("0x%20x", b).equals("0x                  32")){
                    msg = "偵測無紙";
                }
            }
            byte[] statusData1 = setPrinterCheckValue(1);
            for (byte b: statusData1){
                if(String.format("0x%20x", b).equals("0x                  1e")){
                    msg = "出單機錯誤";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!msg.equals("")){
            showPrinterErrorDialog(context, dialogListener, "偵測無紙");
        }else{
            printerCheckFlowListener.onFinish();
        }
    }

    @Override
    public int printReceipt(final Context context) {
        final OrderResponse orderResponse = Bill.fromServer.getOrderResponse();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Print.Initialize();
//					//设置编码
                    Print.LanguageEncode = "big5";
//					InputStream open = getResources().getAssets().open("test01.jpg");
//					Bitmap bitmap = BitmapFactory.decodeStream(open);
//					Print.PrintBitmap(bitmap,  (byte)1, (byte)0,200);
//                    PAct.BeforePrintAction();
                    Print.PrintAndReverseFeed(4);
                    Print.PrintText(context.getString(R.string.receipt)+"\n",0,0,17);
                    Print.Initialize();
                    Print.PrintText(CommonUtils.getTime()+"\n");
                    Print.PrintText(orderResponse.getShop_id() +"-"+orderResponse.getWeborder01().get(0).getWorder_id()+"\n");
                    Print.PrintText(orderResponse.getMethod_name()+"\n",0,0,17);
                    if(!TextUtils.isEmpty(orderResponse.getTakeno())) {
                        Print.PrintText(context.getString(R.string.takeMealNo) + ":" + orderResponse.getTakeno() + "\n", 0, 0, 17);
                    }
                    Print.Initialize();
                    Print.PrintText("--------------------------------"+"\n");
                    Print.PrintText(context.getString(R.string.product)
                            + "    " + context.getString(R.string.quantity)
                            + "    " + context.getString(R.string.originPrice)
                            + "      " + context.getString(R.string.subtoal)
                            +"\n");
                    Print.PrintText("--------------------------------"+"\n");
                    for (WebOrder01 webOrder01 : orderResponse.getWeborder01()) {
                        double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
                        int qty = (int) ComputationUtils.parseDouble(webOrder01.getQty());
                        int itemDisc = (int) ComputationUtils.parseDouble(webOrder01.getItem_disc());
                        Print.PrintText(webOrder01.getProd_name1()+"\n");
                        //判斷是否有加值項目
                        if (webOrder01.getTaste_memo() != null && webOrder01.getTaste_memo().length() > 0) {
                            Print.PrintText("  **" + webOrder01.getTaste_memo()+"\n");
                        }
                        Print.PrintText(
                                "         " + qty
                                        + "     " + salePrice * qty
                                        + "     " + salePrice * qty + itemDisc
                                        +"\n");
                    }
                    byte line = 6;
                    Print.PrintText("--------------------------------"+"\n");
                    Print.PrintText(context.getString(R.string.totalAmount) + "                   " + orderResponse.getTotal()+"\n");
                    Print.PrintText(context.getString(R.string.tailDiscount) + "                  " + orderResponse.getDiscount()+"\n");
                    Print.PrintText(context.getString(R.string.receive) + "                     " + orderResponse.getTot_sales()+"\n");
                    Print.PrintText(context.getString(R.string.pay) + "                     " + orderResponse.getTot_sales()+"\n");
                    // 付款方式
                    for (WebOrder02 webOrder02 : orderResponse.getWeborder02()) {
                        String payName = CommonUtils.getPayName(context, webOrder02.getPay_id());
                        String payAmount = CommonUtils.spaceIt(CommonUtils.removeDot(webOrder02.getAmount()), 8);
                        Print.PrintText(payName + ":       " + payAmount+"\n");
                        Print.PrintText("                    "+"\n");
                    }
//                    Print.PrintAndLineFeed();
//                    Print.PrintText(activity.getString(R.string.invNo) + "                  " +"\n");
//                    Print.PrintText(activity.getString(R.string.donateNo) + "                  " +"\n");
//                    Print.PrintText(activity.getString(R.string.invVehicle) + "                  " +"\n");
                    Print.PrintText("--------------------------------"+"\n");
                    Print.PrintText(context.getString(R.string.receiptBottomMsg)+"\n");
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintQRCode(orderResponse.getWeborder01().get(0).getWorder_id(),6,48,1);
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.CutPaper(1);
//                    PAct.AfterPrintAction();
                    //					InputStream open2 = getResources().getAssets().open("test02.png");
//					Bitmap bitmap2 = BitmapFactory.decodeStream(open2);
//					Print.PrintBitmap(bitmap2,  (byte)1, (byte)0,200);
//					HPRTPrinterHelper.SelectCharacterFont((byte) 1);
//					PublicFunction PFunz=new PublicFunction(Main4Activity.this);
//					String sLanguage="Iran"; String sLEncode="iso-8859-6";
//					int intLanguageNum=56; sLEncode=PFunz.getLanguageEncode(sLanguage);
//					intLanguageNum= PFunz.getCodePageIndex(sLanguage); HPRTPrinterHelper.SetCharacterSet((byte)intLanguageNum);
//					HPRTPrinterHelper.LanguageEncode=sLEncode;
                    //HPRTPrinterHelper.SetCharacterSet Returns -3 HPRTPrinterHelper.PrintText("این یک پیام برای تست میباشد.\r\n");
                    //SDK下发指令设置codepage
//					HPRTPrinterHelper.SetCharacterSet((byte)56);
//					//设置编码
//					HPRTPrinterHelper.LanguageEncode="iso-8859-6";
//					HPRTPrinterHelper.PrintText("این یک پیام برای تست میباشد.\r\n");

                }
                catch(Exception e) {
                    Log.e("Print", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
                }
            }
        });

        return super.printReceipt(context);
    }

    @Override
    public int printBill(final Context context) {
        final OrderResponse orderResponse = Bill.fromServer.getOrderResponse();

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Print.Initialize();
//					//设置编码
                    Print.LanguageEncode = "big5";
//					InputStream open = getResources().getAssets().open("test01.jpg");
//					Bitmap bitmap = BitmapFactory.decodeStream(open);
//					Print.PrintBitmap(bitmap,  (byte)1, (byte)0,200);
//                    PAct.BeforePrintAction();
                    Print.PrintAndReverseFeed(4);
                    Print.PrintText(context.getString(R.string.bill)+"\n",0,0,17);
                    Print.Initialize();
                    Print.PrintText(CommonUtils.getTime()+"\n");
                    Print.PrintText(Config.shop_id + "-" + Config.kiosk_id + "-" + Bill.fromServer.worder_id+"\n");
                    String inOut = "";
                    inOut = Bill.fromServer.getInOut_String(context);
                    if (orderResponse.getTable_no() != null && orderResponse.getTable_no().length() > 0) {
                        inOut += "/桌號-" + orderResponse.getTable_no() + "\n";
                    }
                    Print.PrintText( inOut, 0, 0, 17);
                    Print.Initialize();
                    Print.PrintText("--------------------------------"+"\n");
                    Print.PrintText(context.getString(R.string.product)
                            + "    " + context.getString(R.string.quantity)
                            + "    " + context.getString(R.string.originPrice)
                            + "      " + context.getString(R.string.subtoal)
                            +"\n");
                    Print.PrintText("--------------------------------"+"\n");
                    for (WebOrder01 webOrder01 : orderResponse.getWeborder01()) {
                        double salePrice = ComputationUtils.parseDouble(webOrder01.getSale_price());
                        int qty = (int) ComputationUtils.parseDouble(webOrder01.getQty());
                        int itemDisc = (int) ComputationUtils.parseDouble(webOrder01.getItem_disc());
                        Print.PrintText(webOrder01.getProd_name1()+"\n");
                        //判斷是否有加值項目
                        if (webOrder01.getTaste_memo() != null && webOrder01.getTaste_memo().length() > 0) {
                            Print.PrintText("  **" + webOrder01.getTaste_memo()+"\n");
                        }
                        Print.PrintText(
                                "         " + qty
                                        + "     " + salePrice * qty
                                        + "     " + salePrice * qty + itemDisc
                                        +"\n");
                    }
                    byte line = 6;
                    Print.PrintText("--------------------------------"+"\n");
                    Print.PrintText(context.getString(R.string.totalAmount) + "                   " + orderResponse.getTotal()+"\n");
//                    Print.PrintAndLineFeed();
//                    Print.PrintText(activity.getString(R.string.invNo) + "                  " +"\n");
//                    Print.PrintText(activity.getString(R.string.donateNo) + "                  " +"\n");
//                    Print.PrintText(activity.getString(R.string.invVehicle) + "                  " +"\n");
                    Print.PrintText("--------------------------------"+"\n");
                    //9.請至櫃檯結帳，謝謝您
                    Print.PrintText(context.getString(R.string.plsPayAtCounter)+"\n");
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintQRCode(orderResponse.getWeborder01().get(0).getWorder_id(),6,48,1);
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.PrintAndLineFeed();
                    Print.CutPaper(1);
//                    PAct.AfterPrintAction();
                    //					InputStream open2 = getResources().getAssets().open("test02.png");
//					Bitmap bitmap2 = BitmapFactory.decodeStream(open2);
//					Print.PrintBitmap(bitmap2,  (byte)1, (byte)0,200);
//					HPRTPrinterHelper.SelectCharacterFont((byte) 1);
//					PublicFunction PFunz=new PublicFunction(Main4Activity.this);
//					String sLanguage="Iran"; String sLEncode="iso-8859-6";
//					int intLanguageNum=56; sLEncode=PFunz.getLanguageEncode(sLanguage);
//					intLanguageNum= PFunz.getCodePageIndex(sLanguage); HPRTPrinterHelper.SetCharacterSet((byte)intLanguageNum);
//					HPRTPrinterHelper.LanguageEncode=sLEncode;
                    //HPRTPrinterHelper.SetCharacterSet Returns -3 HPRTPrinterHelper.PrintText("این یک پیام برای تست میباشد.\r\n");
                    //SDK下发指令设置codepage
//					HPRTPrinterHelper.SetCharacterSet((byte)56);
//					//设置编码
//					HPRTPrinterHelper.LanguageEncode="iso-8859-6";
//					HPRTPrinterHelper.PrintText("این یک پیام برای تست میباشد.\r\n");

                }
                catch(Exception e) {
                    Log.e("Print", (new StringBuilder("Activity_Main --> PrintSampleReceipt ")).append(e.getMessage()).toString());
                }
            }
        });

        return super.printBill(context);
    }

    private void printProductDetail(Context context, List<WebOrder01> webOrder01List) {
    }


    private void printPaymentData(Context context, List<WebOrder02> webOrder02List) {

    }

    @Override
    public int printEasyCardCheckOutData(EcCheckoutData ecCheckoutData) {
        return super.printEasyCardCheckOutData(ecCheckoutData);
    }

    @Override
    public int printTest(Context context, String data) {
        return super.printTest(context, data);
    }

    private void printTakeNumber(Context context, OrderResponse orderResponse) {

    }

    private void printEcSaleData(EcPayData ecPayData) {

    }

    private void printNCCCTransData(NCCCTransDataBean dataBean) {

    }

    @Override
    public int printCreditCardReceipt(Context context) {
        return super.printCreditCardReceipt(context);
    }

    @Override
    public void cutPaper() {
        super.cutPaper();
    }

    @Override
    public int printTicket(Context context, Ticket ticket) {
        return super.printTicket(context, ticket);
    }


    private String[] paddingRight(String str, int limit_byte_length) {
        String[] return_data = new String[2];
        return_data[0] = "";
        return_data[1] = "";
        int param_length;

        try {
            // 先取整個byte的長度，中文字2byte，英文1byte
            param_length = str.getBytes("BIG5").length;

            // 「字串長度」==「限定長度」
            if (param_length == limit_byte_length) {
                // 等於-回傳
                return_data[0] = str;
                return_data[1] = "";
                return return_data;
            }

            // 小於-填空白
            if (param_length < limit_byte_length) {

                return_data[0] = str;
                for (int i = 0; i < (limit_byte_length - param_length); i++) {
                    return_data[0] = return_data[0] + " ";
                }
                return_data[1] = "";
                return return_data;
            }

            int first_line_end_position = 0;
            // 大於-分兩行
            if (param_length > limit_byte_length) {
                int str_length = str.length();
                for (int i = 0; i < str_length; i++) {

                    String str_char = str.substring(i, i + 1);

                    int char_length = str_char.getBytes("BIG5").length;

                    int return_first_length = return_data[0].getBytes("BIG5").length;
                    int diff = limit_byte_length - return_first_length;

                    //小於單行最大值，剩下的大小剛好大等於一個英文或中文字，其他的字直接到第二行
                    if (return_first_length < limit_byte_length && diff >= char_length) {
                        return_data[0] = return_data[0] + str_char;
                    } else {
                        first_line_end_position = i;
                        break;
                    }
                }

                //剛好是缺1個字元填空
                if (return_data[0].getBytes("BIG5").length < limit_byte_length) {
                    return_data[0] = return_data[0] + " ";
                }

                return_data[1] = str.substring(first_line_end_position, str_length);

                return return_data;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return return_data;
    }

    private String[] paddingLeft(String str, int limit_byte_length) {
        String[] return_data = new String[2];
        int param_length;

        try {
            // 先取整個byte的長度，中文字2byte，英文1byte
            param_length = str.getBytes("BIG5").length;

            // 「字串長度」==「限定長度」
            if (param_length == limit_byte_length) {
                // 等於-回傳
                return_data[0] = str;
                return_data[1] = "";
                return return_data;
            }

            if (param_length < limit_byte_length) {
                // 小於-填空白
                return_data[0] = str;
                for (int i = 0; i < (limit_byte_length - param_length); i++) {
                    return_data[0] = " " + return_data[0];
                }

                return_data[1] = "";
                return return_data;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return return_data;
    }

    public static byte[] setPrinterCheckValue(int value) throws Exception {
        return Print.GetRealTimeStatus((byte)value);
    }

}

