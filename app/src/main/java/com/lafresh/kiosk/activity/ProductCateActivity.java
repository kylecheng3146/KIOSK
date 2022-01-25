//package com.lafresh.kiosk.activity;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.lafresh.kiosk.Config;
//import com.lafresh.kiosk.R;
//import com.lafresh.kiosk.adapter.ProductCategoryAdapter;
//import com.lafresh.kiosk.httprequest.ApiRequest;
//import com.lafresh.kiosk.httprequest.GetProdCateApiRequest;
//import com.lafresh.kiosk.httprequest.model.GetProdCateRes;
//import com.lafresh.kiosk.shoppingcart.model.ProductCate;
//import com.lafresh.kiosk.utils.CommonUtils;
//import com.lafresh.kiosk.utils.Json;
//
//import java.util.List;
//
//public class ProductCateActivity extends AppCompatActivity {
//    private ImageView ivAd;
//    private ProgressBar pgbCate;
//    private RecyclerView rvCate;
//    private Button btnBack;
//
//    private List<ProductCate> cateList;
//    private ProductCategoryAdapter productCategoryAdapter;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_category);
//
//        findViews();
//        setButton();
//        getProdCate();
//    }
//
//    private void findViews() {
//        ivAd = findViewById(R.id.ivAd);
//        if (Config.bannerImg != null) {
//            Glide.with(this).load(Config.bannerImg).into(ivAd);
//        }
//        pgbCate = findViewById(R.id.pgbCate);
//        rvCate = findViewById(R.id.rvCategory);
//        btnBack = findViewById(R.id.btnBack);
//    }
//
//    private void setButton() {
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//    }
//
//    private void getProdCate() {
//        pgbCate.setVisibility(View.VISIBLE);
//        GetProdCateApiRequest getProdCateApiRequest = new GetProdCateApiRequest();
//        ApiRequest.ApiListener listener = new ApiRequest.ApiListener() {
//            @Override
//            public void onSuccess(ApiRequest apiRequest, String body) {
//                GetProdCateRes res = Json.fromJson(body, GetProdCateRes.class);
//                if (res.getCode() == 0) {
//                    cateList = res.getData();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            initRecyclerView();
//                            pgbCate.setVisibility(View.GONE);
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
//                        pgbCate.setVisibility(View.GONE);
//                        CommonUtils.showMessage(ProductCateActivity.this,getString(R.string.connectionError));
//                    }
//                });
//            }
//        };
//        getProdCateApiRequest.setApiListener(listener).go();
//    }
//
//    private void initRecyclerView() {
//        productCategoryAdapter = new ProductCategoryAdapter(cateList);
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
//        rvCate.setLayoutManager(layoutManager);
//        rvCate.setAdapter(productCategoryAdapter);
//    }
//}
