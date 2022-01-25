//package com.lafresh.kiosk.activity;
//
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.lafresh.kiosk.Config;
//import com.lafresh.kiosk.R;
//import com.lafresh.kiosk.adapter.TopCategoryAdapter;
//import com.lafresh.kiosk.dialog.LoadingDialog;
//import com.lafresh.kiosk.httprequest.ApiRequest;
//import com.lafresh.kiosk.httprequest.GetProductApiRequest;
//import com.lafresh.kiosk.httprequest.model.GetProductRes;
//import com.lafresh.kiosk.shoppingcart.ShoppingCartManager;
//import com.lafresh.kiosk.shoppingcart.model.Product;
//import com.lafresh.kiosk.shoppingcart.model.ProductCate;
//import com.lafresh.kiosk.utils.Json;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProductActivity extends AppCompatActivity {
//    private ImageView ivAd;
//    private TextView tvProdTitle;
//    private TextView tvPriceTitle;
//    private TextView tvPrice;
//    private RecyclerView rvCate;
//    private RecyclerView rvProduct;
//    private Button btnRightArrow;
//    private Button btnCheckout;
//    private Button btnRestart;
//    private Button btnUnfold;
//    private Button btnFold;
//
//    private String depId;
//    private ArrayList<ProductCate> cateList;
//    private List<Product> productList;
//    private TopCategoryAdapter topCategoryAdapter;
////    private ProductAdapter productAdapter;
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_product);
//
//        Bundle bundle = getIntent().getExtras();
//        depId = bundle.getString("depId");
//        cateList = (ArrayList<ProductCate>) bundle.getSerializable("productCateList");
//
//        findViews();
//        setUI();
//        setButton();
//        updateProduct(depId);
//    }
//
//    private void findViews() {
//        ivAd = findViewById(R.id.ivAd);
//        tvProdTitle = findViewById(R.id.tvProdTitle);
//        tvPriceTitle = findViewById(R.id.tvPriceTitle);
//        tvPrice = findViewById(R.id.tvPrice);
//        rvCate = findViewById(R.id.rvProdCate);
//        rvProduct = findViewById(R.id.rvProduct);
//        btnRightArrow = findViewById(R.id.btnRightArrow);
//        btnCheckout = findViewById(R.id.btnCheckout);
//        btnRestart = findViewById(R.id.btnRestart);
//        btnUnfold = findViewById(R.id.btnUnfold);
//        btnFold = findViewById(R.id.btnFold);
//    }
//
//    private void setUI() {
//        if (Config.bannerImg != null) {
//            Glide.with(this).load(Config.bannerImg).into(ivAd);
//        }
//
//        topCategoryAdapter = new TopCategoryAdapter(this, cateList);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
//        rvCate.setLayoutManager(linearLayoutManager);
//        rvCate.setAdapter(topCategoryAdapter);
//        rvCate.smoothScrollToPosition(ShoppingCartManager.getInstance().getCatePosition());
//
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
//        rvProduct.setLayoutManager(gridLayoutManager);
//
//        String title = getString(R.string.select) + cateList.get(ShoppingCartManager.getInstance().getCatePosition()).getSubject();
//        setTitle(title);
//
//        String selectedOrderTitle = getString(R.string.selectedOrders);
//        String totalTitle = getString(R.string.totalAmount);
//        String pricePrefix = getString(R.string.pricePrefix);
//        tvPriceTitle.setText(selectedOrderTitle + " / " + totalTitle + "  " + pricePrefix + " ");
//    }
//
//    private void setButton() {
//
//    }
//
//    public void updateProduct(String depId) {
//        final LoadingDialog loading = new LoadingDialog(this);
//        loading.show();
//        this.depId = depId;
//        GetProductApiRequest getProductApiRequest = new GetProductApiRequest(depId);
//        ApiRequest.ApiListener listener = new ApiRequest.ApiListener() {
//            @Override
//            public void onSuccess(ApiRequest apiRequest, String body) {
//                GetProductRes res = Json.fromJson(body, GetProductRes.class);
//                if (res.getCode() == 0) {
//                    if (res.getDatacnt() == 0) {
//                        onFail();
//                        return;
//                    }
//                    productList = res.getProduct();
//                    productAdapter = new ProductAdapter(ProductActivity.this, productList);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            loading.dismiss();
//                            rvProduct.setAdapter(productAdapter);
//                        }
//                    });
//                } else {
//                    onFail();
//                }
//            }
//
//            @Override
//            public void onFail() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        loading.dismiss();
//                        tvProdTitle.setText(getString(R.string.noProductInThisCate));
//                        rvProduct.setAdapter(null);
//                    }
//                });
//            }
//        };
//        getProductApiRequest.setApiListener(listener).go();
//    }
//
//    public void setTitle(String title) {
//        tvProdTitle.setText(title);
//    }
//
//    public void addBottomOrder() {
//
//    }
//}
