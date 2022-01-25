package com.lafresh.kiosk.httprequest.restfulapi

import com.lafresh.kiosk.easycard.model.*
import com.lafresh.kiosk.httprequest.model.*
import com.lafresh.kiosk.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Kevin on 2020/11/9.
 */

interface ApiServices {
    @POST("/v2/auth/login")
    fun getToken(@Body authParameter: AuthParameter): Call<Auth>

    @GET("/v1/banners")
    fun getBanners(
        @Header("Authorization") token: String,
        @Query("app_type") appType: String,
        @Query("store_id") storeId: String
    ): Call<Banners>

    @POST("webservice/line/pay.php")
    fun linePay(@Body parameter: LinePayParameter): Call<LinePayRes>

    @POST("/v1/payment/taiwan/pay")
    fun taiwanPay(
        @Header("Authorization") token: String,
        @Body parameter: TaiwanPayReq
    ): Call<TaiwanPayRes>

    @PATCH("/v1/member_card/login")
    fun memberCardLogin(
        @Header("Authorization") token: String,
        @Body memberCardLoginParams: MemberCardLoginParams
    ): Call<Member>

    @GET("/v1/user")
    fun memberData(@Header("Authorization") token: String): Call<Member>

    @GET("/v1/member_status")
    fun memberStatus(
        @Header("Authorization") token: String,
        @Query("phone_number") phoneNumber: String
    ): Call<Unit>

    @POST("/v1/guest/login")
    fun guestLogin(
        @Header("Authorization") token: String,
        @Body guestLoginParams: GuestLoginParams
    ): Call<GuestLoginRes>

    @POST("/v1/order")
    fun createNewOrder(
        @Header("Authorization") token: String,
        @Body order: Order
    ): Call<CreateOrderRes>

    @GET("/v1/order/standard/{order_id}")
    fun getOrderDetail(
        @Header("Authorization") token: String,
        @Path("order_id") orderId: String
    ): Call<Order>

    @PATCH("/v1/order/confirm")
    fun confirmOrder(
        @Header("Authorization") token: String,
        @Body parameter: ConfirmOrderParameter
    ): Call<Response<Void>>

    @GET("/v1/batch_number")
    fun getBatchNumber(
        @Header("Authorization") token: String,
        @Query("store_id") shopId: String,
        @Query("pos_id") kioskId: String
    ): Call<BatchNumber>

    @GET("/v1/sale_method")
    fun getSaleMethod(
        @Header("Authorization") token: String,
        @Query("store_id") shopId: String,
        @Query("client_device") clientDevice: String
    ): Call<SaleMethods>

    @GET("/v1/user/login/{card_no}")
    fun getUserInfo(
        @Header("Authorization") token: String,
        @Path(value = "card_no") cardNo: String
    ): Call<CardInfo>

    @GET("/v1/ec_settle3")
    fun getEcSettle3(
        @Header("Authorization") token: String,
        @Query("store_id") shopId: String,
        @Query("pos_id") kioskId: String,
        @Query("batch_number") batchNumber: String
    ): Call<EcSettle3>

    @POST("/v1/ec_stmc3")
    fun setEcStmc3(
        @Header("Authorization") token: String,
        @Body parameter: EcStmc3Parameter
    ): Call<Response<Void>>

    @PATCH("/v1/ec_settle3")
    fun setEcSettle3(
        @Header("Authorization") token: String,
        @Body parameter: EcSettle3Parameter
    ): Call<Response<Void>>

    @GET("/v1/unuploaded_batch_number")
    fun getUnUploadBatchNumber(
        @Header("Authorization") token: String,
        @Query("store_id") shopId: String,
        @Query("pos_id") kioskId: String
    ): Call<BatchNumbers>

    @GET("/v1/payments")
    fun getPayments(
        @Header("Authorization") token: String,
        @Query("shop_id") shopId: String,
        @Query("source_type") sourceType: String
    ): Call<Payments>

    @GET("/v1/tickets")
    fun getTickets(
        @Header("Authorization") token: String,
        @Query("type") type: String
    ): Call<Tickets>

    @GET("/v1/recommend_products")
    fun getRecommendProducts(
        @Header("Authorization") token: String,
        @Query("store_id") shopId: String
    ): Call<Products>

    @GET("/v1/basic/settings")
    fun getBasicSetting(
        @Header("Authorization") token: String,
        @Query("store_id") shopId: String
    ): Call<BasicSettings>

    @GET("/v1/member/key")
    fun getMemberKey(
        @Header("Authorization") token: String,
        @Query("phone_number") phoneNumber: String,
        @Query("type") type: String
    ): Call<MemberKey>

    @PUT("/v1/payment/easycard/checkout/{shop_id}")
    fun updateEasycardDailyCheckout(
        @Header("Authorization") token: String,
        @Path(value = "shop_id") shop_id: String,
        @Body parameter: EasycardDailyCheckout
    ): Call<Response<Void>>

    @POST("product/route_product")
    fun getProducts(
        @Body getProductsParams: GetProductsParams
    ): Call<GetProducts>

    @POST("product/route_product")
    fun getProductCategory(
        @Body getProductCategoryParams: GetProductCategoryParams
    ): Call<GetProductCategory>

    @POST("/v1/basic/kiosk/login")
    fun login(
        @Header("Authorization") token: String,
        @Body loginParams: LoginParams
    ): Call<Response<Void>>

    @POST("/v1/basic/log")
    fun setLog(
        @Header("Authorization") token: String,
        @Body loginParams: LogParams
    ): Call<Response<Void>>

    @PATCH("/v1/user/device")
    fun setUserDevice(
        @Header("Authorization") token: String,
        @Body memberCardLoginParams: UserDeviceParams
    ): Call<Response<Void>>

    @Streaming
    @GET
    fun downloadFile(@Url fileUrl: String?): Call<ResponseBody?>?

    @GET("/v1/basic/pickup_method")
    fun getPickupMethod(
        @Header("Authorization") token: String,
        @Query("store_id") storeID: String
    ): Call<PickupMethods>

    @GET("/v1/report/accounting_information")
    fun getAccountingInformation(
        @Header("Authorization") token: String,
        @Query("store_id") storeID: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("machine_id") machineID: String
    ): Call<GetAccountingInformation>
}
