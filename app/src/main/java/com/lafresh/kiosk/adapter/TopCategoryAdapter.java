//package com.lafresh.kiosk.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.lafresh.kiosk.R;
//import com.lafresh.kiosk.activity.ProductActivity;
//import com.lafresh.kiosk.shoppingcart.ShoppingCartManager;
//import com.lafresh.kiosk.shoppingcart.model.ProductCate;
//
//import java.util.List;
//
//public class TopCategoryAdapter extends RecyclerView.Adapter<TopCategoryAdapter.ViewHolder> {
//    private ProductActivity activity;
//    private List<ProductCate> productCateList;
//
//    public TopCategoryAdapter(ProductActivity activity, List<ProductCate> productCateList) {
//        this.activity = activity;
//        this.productCateList = productCateList;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.v_prodcate_top, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
//        final ProductCate productCate = productCateList.get(position);
//        holder.textView.setText(productCate.getSubject());
//        if (position == ShoppingCartManager.getInstance().getCatePosition()) {
//            holder.imageView.setImageDrawable(activity.getDrawable(R.drawable.shop_catebg_select));
//        } else {
//            holder.imageView.setImageDrawable(activity.getDrawable(R.drawable.shop_catebg));
//        }
//
//        holder.button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShoppingCartManager.getInstance().setCatePosition(productCateList.indexOf(productCate));
//                String title = activity.getString(R.string.select) + productCate.getSubject();
//                activity.setTitle(title);
//                activity.updateProduct(productCate.getSerno());
//                notifyDataSetChanged();
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
//        private TextView textView;
//        private Button button;
//
//        ViewHolder(View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.iv_bg);
//            textView = itemView.findViewById(R.id.tv_subject);
//            button = itemView.findViewById(R.id.btn_prodcate);
//        }
//    }
//}
