package com.lafresh.kiosk.manager;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lafresh.kiosk.BuildConfig;
import com.lafresh.kiosk.CombItem;
import com.lafresh.kiosk.Config;
import com.lafresh.kiosk.Detail;
import com.lafresh.kiosk.R;
import com.lafresh.kiosk.Taste;
import com.lafresh.kiosk.creditcardlib.nccc.NCCCTransDataBean;
import com.lafresh.kiosk.dialog.BackHomeDialog;
import com.lafresh.kiosk.easycard.model.EcPayData;
import com.lafresh.kiosk.fragment.AlertDialogFragment;
import com.lafresh.kiosk.httprequest.model.ConfirmOrderParameter;
import com.lafresh.kiosk.httprequest.model.CreateOrderRes;
import com.lafresh.kiosk.httprequest.model.ErrorRes;
import com.lafresh.kiosk.httprequest.model.Member;
import com.lafresh.kiosk.httprequest.restfulapi.ApiServices;
import com.lafresh.kiosk.httprequest.restfulapi.RetrofitManager;
import com.lafresh.kiosk.model.Charges;
import com.lafresh.kiosk.model.Item;
import com.lafresh.kiosk.model.LostOrderInfo;
import com.lafresh.kiosk.model.Order;
import com.lafresh.kiosk.model.Payment;
import com.lafresh.kiosk.model.PickupMethod;
import com.lafresh.kiosk.model.Points;
import com.lafresh.kiosk.model.ReceiptData;
import com.lafresh.kiosk.model.SaleMethod;
import com.lafresh.kiosk.model.SaleType;
import com.lafresh.kiosk.model.StupidPrice;
import com.lafresh.kiosk.printer.DataTransformer;
import com.lafresh.kiosk.printer.KioskPrinter;
import com.lafresh.kiosk.printer.model.EReceipt;
import com.lafresh.kiosk.printer.model.Product;
import com.lafresh.kiosk.printer.model.ReceiptDetail;
import com.lafresh.kiosk.type.FlavorType;
import com.lafresh.kiosk.type.PaymentsType;
import com.lafresh.kiosk.utils.ClickUtil;
import com.lafresh.kiosk.utils.Json;
import com.lafresh.kiosk.utils.TimeUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.M)
public class OrderManager {
    private static OrderManager instance;
    private boolean isLogin;
    private Member member;
    private DatabaseReference databaseReference;
    private String orderId = "";
    private String orderTime;
    private String tableNumber = "";
    private PickupMethod pickup_method;
    private double total;
    private double discount;
    private double totalFee;
    private List<Item> itemList = new ArrayList<>();
    private List<Payment> paymentList = new ArrayList<>();
    private List<Order.TicketsBean> tickets = new ArrayList<>();
    private List<Order.TicketsBean> tempTickets = new ArrayList<>();
    private Payment tempPayment;
    private ReceiptData receiptData = new ReceiptData();//載具、捐贈碼、統編

    private NCCCTransDataBean dataBean;
    private EcPayData ecPayData;
    private Points points;
    private boolean isGuestLogin;
    private SaleMethod saleMethod;
    private List<SaleMethod> saleMethods;

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }

    public void addItem(com.lafresh.kiosk.Order order) {
        Item item = new Item();
        item.order = order;
        item.setId(order.product.getProductVO().getProd_id());
        item.setTitle(order.product.getProductVO().getProd_name1());
        item.setQuantity(order.count);
        item.setPrice(getItemStupidPrice(order.product.getUnitPrice(), order.product.total_get(),order.product.getProductVO().getUsePoint(),Integer.parseInt(order.product.getProductVO().getRedeem_point()) * order.count));
        itemList.add(item);
        addModifierGroup(item, order);
    }

    private void addModifierGroup(Item item, com.lafresh.kiosk.Order order) {
        List<Item.SelectedGroup> groupsBeanList = new ArrayList<>();
        if (order.product.comb_type == 0) {
            addSingleProductGroup(order, groupsBeanList);
        }

        if (order.product.comb_type == 1 && !BuildConfig.FLAVOR.equals(FlavorType.hongya.name())) {
            addComboProductGroup(order, groupsBeanList);
        }

        if (BuildConfig.FLAVOR.equals(FlavorType.hongya.name())) {
            addHongYaCustomGroupItem(order, groupsBeanList);
        }

        item.setSelected_modifier_groups(groupsBeanList);
    }

    private void addSingleProductGroup(com.lafresh.kiosk.Order order,
                                       List<Item.SelectedGroup> groupsBeanList) {
        for (Taste taste : order.product.al_taste) {
            for (Detail detail : taste.details) {
                if (detail.selected) {
                    Item.SelectedGroup groupsBean = new Item.SelectedGroup();
                    groupsBean.setId(taste.tasteId + "");
                    groupsBean.setTitle(taste.tasteName);
                    List<Item> list = new ArrayList<>();
                    Item bean = new Item();
                    bean.setId(detail.tasteSno + "");
                    bean.setTitle(detail.name);
                    bean.setQuantity(1);
                    bean.setPrice(getItemStupidPrice(detail.price, detail.price,order.product.getProductVO().getUsePoint(),Integer.parseInt(order.product.getProductVO().getRedeem_point())));
                    list.add(bean);
                    groupsBean.setSelected_items(list);
                    groupsBeanList.add(groupsBean);
                }
            }
        }
    }

    private void addHongYaCustomGroupItem(com.lafresh.kiosk.Order order,
                                      List<Item.SelectedGroup> groupsBeanList) {
        for (CombItem combItem : order.product.al_combItem) {
            Item.SelectedGroup selectedGroup = new Item.SelectedGroup();//套餐內的選擇
            selectedGroup.setTitle(combItem.subject);
            List<Item> selected_items = new ArrayList<>();//選擇的商品
            Item item = new Item();
            item.setId(combItem.productSelected.getProductVO().getProd_id());
            item.setTitle(combItem.productSelected.getProductVO().getProd_name1());
            item.setQuantity(combItem.productSelected.count);
            item.setPrice(getItemStupidPrice(
                    combItem.productSelected.getUnitPrice(),
                    (int) combItem.productSelected.total_get(),order.product.getProductVO().getUsePoint(),Integer.parseInt(order.product.getProductVO().getRedeem_point())));
            List<Item.SelectedGroup> selectedGroupList = new ArrayList<>();
            if(combItem.productSelected.prod_id.contains("-Clone")){
                for (Taste taste : order.product.al_taste) {
                    for (Detail detail : taste.details) {
                        if (detail.selected) {
                            Item.SelectedGroup groupsBean = new Item.SelectedGroup();
                            groupsBean.setId(taste.tasteId + "");
                            groupsBean.setTitle(taste.tasteName);
                            List<Item> list = new ArrayList<>();
                            Item bean = new Item();
                            bean.setId(detail.tasteSno + "");
                            bean.setTitle(detail.name);
                            bean.setQuantity(1);
                            bean.setPrice(getItemStupidPrice(detail.price, detail.price,order.product.getProductVO().getUsePoint(),Integer.parseInt(order.product.getProductVO().getRedeem_point())));
                            list.add(bean);
                            groupsBean.setSelected_items(list);
                            selectedGroupList.add(groupsBean);
                        }
                    }
                }
            }
            for (Taste taste : combItem.productSelected.al_taste) {
                for (Detail detail : taste.details) {
                    if (detail.selected) {
                        Item.SelectedGroup groupsBean = new Item.SelectedGroup();
                        groupsBean.setId(taste.tasteId + "");
                        groupsBean.setTitle(taste.tasteName);
                        List<Item> list = new ArrayList<>();
                        Item bean = new Item();
                        bean.setId(detail.tasteSno + "");
                        bean.setTitle(detail.name);
                        bean.setQuantity(1);
                        bean.setPrice(getItemStupidPrice(detail.price, detail.price,order.product.getProductVO().getUsePoint(),Integer.parseInt(order.product.getProductVO().getRedeem_point())));
                        list.add(bean);
                        groupsBean.setSelected_items(list);
                        selectedGroupList.add(groupsBean);
                    }
                }
            }
            item.setSelected_modifier_groups(selectedGroupList);
            selected_items.add(item);
            selectedGroup.setSelected_items(selected_items);
            groupsBeanList.add(selectedGroup);
        }
    }

    private void addComboProductGroup(com.lafresh.kiosk.Order order,
                                      List<Item.SelectedGroup> groupsBeanList) {
        for (CombItem combItem : order.product.al_combItem) {
            Item.SelectedGroup selectedGroup = new Item.SelectedGroup();//套餐內的選擇
            selectedGroup.setTitle(combItem.subject);
            List<Item> selected_items = new ArrayList<>();//選擇的商品
            Item item = new Item();
            item.setId(combItem.productSelected.getProductVO().getProd_id());
            item.setTitle(combItem.productSelected.getProductVO().getProd_name1());
            item.setQuantity(Integer.parseInt(combItem.productSelected.combQty));
            item.setPrice(getItemStupidPrice(
                    combItem.productSelected.getUnitPrice(),
                    (int) combItem.productSelected.total_get(),order.product.getProductVO().getUsePoint(),Integer.parseInt(order.product.getProductVO().getRedeem_point())));
            List<Item.SelectedGroup> selectedGroupList = new ArrayList<>();
            for (Taste taste : combItem.productSelected.al_taste) {
                for (Detail detail : taste.details) {
                    if (detail.selected) {
                        Item.SelectedGroup groupsBean = new Item.SelectedGroup();
                        groupsBean.setId(taste.tasteId + "");
                        groupsBean.setTitle(taste.tasteName);
                        List<Item> list = new ArrayList<>();
                        Item bean = new Item();
                        bean.setId(detail.tasteSno + "");
                        bean.setTitle(detail.name);
                        bean.setQuantity(1);
                        bean.setPrice(getItemStupidPrice(detail.price, detail.price,order.product.getProductVO().getUsePoint(),Integer.parseInt(order.product.getProductVO().getRedeem_point())));
                        list.add(bean);
                        groupsBean.setSelected_items(list);
                        selectedGroupList.add(groupsBean);
                    }
                }
            }
            item.setSelected_modifier_groups(selectedGroupList);
            selected_items.add(item);
            selectedGroup.setSelected_items(selected_items);
            groupsBeanList.add(selectedGroup);
        }
    }

    private Item.PriceBean getItemStupidPrice(double unitPrice, double totalPrice,boolean isUsePoint ,int spendPoints) {
        Item.PriceBean priceBean = new Item.PriceBean();
        priceBean.setUnit_price(getStupidPrice(unitPrice));
        priceBean.setTotal_price(getStupidPrice(totalPrice));
        checkUsePoint(isUsePoint, spendPoints, priceBean);
        return priceBean;
    }

    void checkUsePoint(boolean isUsePoint, int spendPoints, Item.PriceBean priceBean) {
        if(isUsePoint && isLogin){
            priceBean.setSpendPoints(spendPoints);
        }
    }

    public void removeItem(com.lafresh.kiosk.Order order) {
        for (Item item : itemList) {
            if (item.order.equals(order)) {
                itemList.remove(item);
                return;
            }
        }
    }

    public void createOrUpdateOrder(double totalPrice, OrderListener listener, Activity activity) {
        Order order = new Order();
        order.setId(orderId);
        if(BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())){
            order.setKioskType(Config.isRestaurant ? "RESTAURANT" : "SHOPPING");
        }
        order.setKioskId(Config.kiosk_id);
        order.setTable_number(tableNumber);
        order.setPickup_at(TimeUtil.get30MinLaterTime(TimeUtil.RECEIPT_PATTERN));
        order.setPlaced_at(TimeUtil.getNowTime(TimeUtil.RECEIPT_PATTERN));
        order.setClient_device("KIOSK");
        Order.StoreBean storeBean = new Order.StoreBean();
        storeBean.setId(Config.shop_id);
        if(BuildConfig.FLAVOR.equals("galileo")){
            order.setType(SaleType.DELIVERY.name());
            Order.Customer customer = new Order.Customer();
            if(!"".equals(Config.address)){
                customer.setAddress(Config.address);
                customer.setName("NO NAME");
                customer.setMobile(Config.phoneNumber);
                order.setCustomer(customer);
            }
        }else{
            //判斷若type包含 unknown 則濾除
            if(saleMethod.getType() != null && saleMethod.getType().contains("、") ){
                String[] saleType =  saleMethod.getType().split("、");
                for (String s : saleType) {
                    if(!s.equals("Unknown")){
                        order.setType(SaleType.valueOf(s).name());
                    }
                }
            }else{
                order.setType(SaleType.valueOf(saleMethod.getType()).name());
            }
        }
        order.setStore(storeBean);
        order.setCharges(getCharge(totalPrice));
        Order.Cart cart = new Order.Cart();
        cart.setItems(itemList);
        order.setCart(cart);
        order.setReceipt(receiptData);
        if (paymentList.size() > 0 || tempPayment != null) {
            List<Payment> payments = new ArrayList<>(paymentList);
            if(tempPayment != null){
                payments.add(tempPayment);
            }
            order.setPayments(payments);
        }

        //若佐登妮絲
        if(BuildConfig.FLAVOR.equals(FlavorType.jourdeness.name())){
            order.setPickupMethod(pickup_method);
        }
        setTicketsToOrder(order);
        callOrderApi(order, listener, activity);
    }

    public void setTicketsToOrder(Order order) {
        if (tickets.size() > 0) {
            order.setTickets(tickets);
        }else{
            order.setTickets(null);
        }
    }

    private void callOrderApi(Order order, OrderListener listener, Activity activity) {

        RetrofitManager manager = RetrofitManager.getInstance();
        ApiServices apiServcies = manager.getApiServices(Config.cacheUrl);
        apiServcies.createNewOrder(Config.token, order).enqueue(new Callback<CreateOrderRes>() {
            @Override
            public void onResponse(Call<CreateOrderRes> call, Response<CreateOrderRes> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response);
                } else {
                    showErrorDialog(response.errorBody(), listener, activity);
                }
            }

            @Override
            public void onFailure(Call<CreateOrderRes> call, Throwable t) {
                showErrorDialog(activity.getString(R.string.connectionError), listener, activity);
            }
        });
    }


    public void addTicketPayment(@NotNull Order order) {
        OrderManager manager = OrderManager.getInstance();
        for (Payment payment : order.getPayments()) {
            if (payment.getType().equals(PaymentsType.CASH_TICKET.name())) {
                manager.addPayment(payment);
            }
        }
    }

    public void confirmOrder(boolean isReceipt, OrderListener listener, Activity activity) {
        RetrofitManager manager = RetrofitManager.getInstance();
        ApiServices apiServices = manager.getApiServices(Config.cacheUrl);
        ConfirmOrderParameter parameter = new ConfirmOrderParameter();
        parameter.setId(orderId);
        parameter.setPayments(paymentList);
        //已經先預開發票了，在這裡就不要再帶一次，不然會開兩次發票 #13117
//        parameter.setReceipt(isReceipt);
        apiServices.confirmOrder(Config.token, parameter).enqueue(new Callback<Response<Void>>() {
            @Override
            public void onResponse(Call<Response<Void>> call, Response<Response<Void>> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response);
                } else {
                    String message = getErrorMessageFromErrorBody(response.errorBody());
                    if ("此筆訂單已確認，不需重複確認".equals(message)) {
                        listener.onSuccess(response);
                    } else {
                        showErrorDialog(message, listener, activity);
                    }
                }
            }

            @Override
            public void onFailure(Call<Response<Void>> call, Throwable t) {
                showErrorDialog(activity.getString(R.string.connectionError), listener, activity);
            }
        });
    }

    public void getOrderDetail(OrderListener listener, Activity activity) {
        RetrofitManager manager = RetrofitManager.getInstance();
        ApiServices apiServices = manager.getApiServices(Config.cacheUrl);
        apiServices.getOrderDetail(Config.token, orderId).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful()) {
                    listener.onSuccess(response);
                } else {
                    showErrorDialog(response.errorBody(), listener, activity);
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                showErrorDialog(activity.getString(R.string.connectionError), listener, activity);
            }
        });
    }

    public void printOnlyReceipt(Context context, Order order) {
        KioskPrinter printer = KioskPrinter.getPrinter();
        order.setId(OrderManager.getInstance().getOrderId());
        List<Product> productList = DataTransformer.generateProductList(order);
        ReceiptDetail detail = DataTransformer.generateReceiptDetail(order, productList);
        detail.setNcccTransDataBean(dataBean);
        detail.setEcPayData(ecPayData);
        printer.printReceipt(context, null, detail);
    }

    public void printReceipt(Context context, Order order) {
        KioskPrinter printer = KioskPrinter.getPrinter();
        if(printer != null) {
            order.setId(OrderManager.getInstance().getOrderId());
            order.setPickupMethod(pickup_method);
            List<Product> productList = DataTransformer.generateProductList(order);
            ReceiptDetail detail = DataTransformer.generateReceiptDetail(order, productList);
            detail.setNcccTransDataBean(dataBean);
            detail.setEcPayData(ecPayData);
            ReceiptData receiptData = order.getReceipt();
            EReceipt eReceipt = null;
            boolean printEReceipt = false;
            if (receiptData.getReceipt_number() != null && !receiptData.getReceipt_number().isEmpty() && !Config.disableReceiptModule) {
                receiptData.setTotalAmount((int) order.getCharges().getTotal_fee().getAmount());
                receiptData.setTakeNumber(!Config.disableReceiptModule);
                eReceipt = DataTransformer.generateEReceipt(receiptData, productList);
                String carrierNumber = eReceipt.getCarrierNumber();
                String loveCode = eReceipt.getLoveCode();
                boolean memberCarrier = eReceipt.getMemberCarrier();
                printEReceipt = (carrierNumber == null || carrierNumber.length() == 0) || !memberCarrier
                        && (loveCode == null || loveCode.length() == 0);
            }
            printer.printReceipt(context, eReceipt, detail);
            if (printEReceipt) {
                printer.printEInvoice(context, eReceipt, detail);
            }

            if (order.getActivityMessage() != null && !"".equals(order.getActivityMessage())) {
                printer.printActivityMessage(context, order.getActivityMessage());
            }
        }
    }

    private String getErrorMessageFromErrorBody(ResponseBody body) {
        ErrorRes error;
        try {
            error = Json.fromJson(Objects.requireNonNull(body).string(), ErrorRes.class);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return error.getMessage();
    }

    public void showErrorDialog(@Nullable ResponseBody body, OrderListener listener, Activity activity) {
        String message = activity.getString(R.string.connectionError);
        try {
            ErrorRes error = Json.fromJson(Objects.requireNonNull(body).string(), ErrorRes.class);
            message = error.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        showErrorDialog(message, listener, activity);
    }

    public void showErrorDialog(String message, OrderListener listener, Activity activity) {
        final AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
        alertDialogFragment.setTitle(R.string.connectionError)
                .setMessage(message)
                .setConfirmButton(R.string.retry, v -> {
                    if (!ClickUtil.isFastDoubleClick()) {
                        alertDialogFragment.dismiss();
                        listener.onRetry();
                    }
                })
                .setCancelButton(R.string.returnHomeButton, v -> {
                    if (!ClickUtil.isFastDoubleClick()) {
                        new BackHomeDialog(activity).show();
                    }
                })
                .setUnCancelAble()
                .show(activity.getFragmentManager(), AlertDialogFragment.MESSAGE_DIALOG);
    }

    private Charges getCharge(double totalPrice) {
        Charges chargesBean = new Charges();
        StupidPrice total = getStupidPrice(totalPrice);
        chargesBean.setTotal(total);
        chargesBean.setSub_total(total);
        chargesBean.setTotal_fee(total);
        chargesBean.setPoints(points);
        return chargesBean;
    }

    public void writeLostOrderInfo() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        LostOrderInfo lostOrderInfo = new LostOrderInfo(getOrderId(), getPayments());
        Map<String, Object> postValues = lostOrderInfo.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/lost_order_info/" + getOrderId(), postValues);
        databaseReference.child(Config.authKey).child(Config.shop_id).child(Config.kiosk_id.toUpperCase()).updateChildren(childUpdates);
    }

    public StupidPrice getStupidPrice(double price) {
        StupidPrice stupidPrice = new StupidPrice();
        stupidPrice.setAmount(price);
        stupidPrice.setCurrency_code("TWD");
        stupidPrice.setFormatted_amount(price + "");
        return stupidPrice;
    }

    public void setTempPayment(Payment tempPayment) {
        this.tempPayment = tempPayment;
    }

    public void addPayment(Payment payment) {
        paymentList.add(payment);
    }

    public void removeTicketPayment() {
        for (Payment payment : paymentList) {
            if (payment.getType().equals(PaymentsType.CASH_TICKET.name())){
                paymentList.remove(payment);
            }
        }
    }

    public void addTempTickets(Order.TicketsBean ticketsBean) {
        tempTickets.add(ticketsBean);
    }

    public void addTickets(Order.TicketsBean ticketsBean) {
        tickets.add(ticketsBean);
    }

    public void addAllTempTickets(List<Order.TicketsBean> ticket) {
        tempTickets.addAll(ticket);
    }

    public void addAllTickets(List<Order.TicketsBean> tempTickets) {
        tickets.addAll(tempTickets);
    }

    public void removeTempTickets(Order.TicketsBean ticketsBean) {
        for (Order.TicketsBean tempTicket : tempTickets) {
            if (tempTicket.getNumber().equals(ticketsBean.getNumber())) {
                tempTickets.remove(tempTicket);
                return;
            }
        }
    }

    public void removeAllTempTickets() {
        tempTickets.clear();
    }

    public void removeAllTickets() {
        tickets.clear();
    }

    public void removeTickets(Order.TicketsBean ticketsBean) {
        for (Order.TicketsBean ticket : tickets) {
            if (ticket.getNumber().equals(ticketsBean.getNumber())) {
                tickets.remove(ticket);
                return;
            }
        }
    }

    public List<Order.TicketsBean> getTickets() {
        return tickets;
    }

    public List<Order.TicketsBean> getTempTickets() {
        return tempTickets;
    }

    public List<Payment> getPayments() {
        return paymentList;
    }

    public String getOrderTime() {
        if (orderTime == null) {
            return TimeUtil.get30MinLaterTime(TimeUtil.RECEIPT_PATTERN);
        }
        return orderTime;
    }

    public PickupMethod getPickupMethod() {
        return pickup_method;
    }

    public void setPickupMethod(PickupMethod pickup_method) {
        this.pickup_method = pickup_method;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(double totalFee) {
        this.totalFee = totalFee;
    }

    public Points getPoints() {
        return points;
    }

    public void setPoints(Points points) {
        this.points = points;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    
    public void setSaleMethods(List<SaleMethod> saleMethodList){
        this.saleMethods = saleMethodList;
    }

    public List<SaleMethod> getSaleMethods(){
        return saleMethods;
    }

    public SaleMethod getSaleMethod() {
        return saleMethod;
    }

    public void setSaleMethod(SaleMethod saleMethod) {
        this.saleMethod = saleMethod;
    }

    public void setCarrier(String carrier) {//載具
        receiptData.setCarrier(carrier);
        receiptData.setNpoban(null);
        receiptData.setTax_ID_number(null);
        receiptData.setCarrier_type("3J0002");
    }

    public void setLoveCode(String loveCode) {//捐贈碼
        receiptData.setNpoban(loveCode);
        receiptData.setTax_ID_number(null);
        receiptData.setCarrier(null);
        receiptData.setCarrier_type(null);
    }

    public void setBusinessCode(String code) {//統編
        receiptData.setTax_ID_number(code);
        receiptData.setNpoban(null);
        receiptData.setCarrier(null);
        receiptData.setCarrier_type(null);
    }

    public void setMemberCarrier(boolean enabled) {//統編
        receiptData.setTax_ID_number(null);
        receiptData.setUseMemberCarrier(enabled);
        receiptData.setNpoban(null);
        receiptData.setCarrier(null);
        receiptData.setCarrier_type(null);
    }

    public void setNcccDataBean(NCCCTransDataBean dataBean) {
        this.dataBean = dataBean;
    }

    public void setEcPayData(EcPayData ecPayData) {
        this.ecPayData = ecPayData;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public boolean isGuestLogin() {
        return isGuestLogin;
    }

    public void setGuestLogin(boolean login) {
        isGuestLogin = login;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public ReceiptData getReceiptData() {
        return receiptData;
    }

    public void setReceiptData(ReceiptData receiptData) {
        this.receiptData = receiptData;
    }

    public void reset() {
        instance = null;
    }

    public boolean hasCartItem(){
        return itemList.size() > 0;
    }

    public interface OrderListener {
        void onSuccess(Response response);

        void onRetry();
    }
}
