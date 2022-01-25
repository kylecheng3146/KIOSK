//package com.lafresh.kiosk.adapter;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.lafresh.kiosk.R;
//import com.lafresh.kiosk.activity.ProductActivity;
//import com.lafresh.kiosk.httprequest.ApiRequest;
//import com.lafresh.kiosk.shoppingcart.ShoppingCartManager;
//import com.lafresh.kiosk.shoppingcart.model.ProductCate;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder> {
//    private List<ProductCate> productCateList;
//
//    public ProductCategoryAdapter(List<ProductCate> productCateList) {
//        this.productCateList = productCateList;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.v_prodcate, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        final ProductCate productCate = productCateList.get(position);
//        holder.button.setText(productCate.getSubject());
//        String url = productCate.getImgfile2();
//        if (url != null) {
//            Glide.with(holder.imageView.getContext())
//                    .load(ApiRequest.CATEGORY_PICTURE_URL + productCate.getImgfile2()).into(holder.imageView);
//        }
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.button.performClick();
//                ShoppingCartManager.getInstance().setCatePosition(productCateList.indexOf(productCate));
//                Intent intent = new Intent(holder.itemView.getContext(), ProductActivity.class);
//                Bundle bundle = new Bundle();
//                ArrayList<ProductCate> list =  new ArrayList<>(productCateList);
//                bundle.putString("depId", productCate.getSerno());
//                bundle.putSerializable("productCateList", list);
//                intent.putExtras(bundle);
//                holder.itemView.getContext().startActivity(intent);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return productCateList.size();
//    }
//
//    class ViewHolder extends RecyclerView.ViewHolder {
//        private ImageView imageView;
//        private Button button;
//
//        ViewHolder(View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.iv_product);
//            button = itemView.findViewById(R.id.btn_name);
//        }
//    }
//}
