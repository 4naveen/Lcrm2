package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.lorvent.lcrm.activities.details.admin.DetailProductActivity;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Products;


import java.util.ArrayList;



public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.SimpleViewHolder> {
    private SimpleViewHolder svHolder;
    private static ArrayList<Products> productses;
    //private ViewGroup parent;
    private String token;
    private int productIdPosition;
    private Context mContext;
    private Button ok;

    public ProductsAdapter(Context mContext, ArrayList<Products>productsArrayList) {
        this.mContext = mContext;
        productses=productsArrayList;
        this.token= Appconfig.TOKEN;
    }

    @Override
    public ProductsAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.swipe_row_product_item,parent,false);
        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ProductsAdapter.SimpleViewHolder viewHolder, int position) {
        final Products products= productses.get(position);
        svHolder=viewHolder;
        viewHolder.tvCategory.setText(products.getName());
        viewHolder.tvProductName.setText(products.getProduct_name());
    }

    @Override
    public int getItemCount() {
        return productses.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvProductName;
        TextView tvCategory;

        ImageView arrow;
        SimpleViewHolder(View itemView) {
            super(itemView);
            tvCategory = (TextView) itemView.findViewById(R.id.category);
            tvProductName = (TextView) itemView.findViewById(R.id.products_name);
            arrow=(ImageView)itemView.findViewById(R.id.arrow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int product_id=productses.get(getAdapterPosition()).getId();
            Intent i=new Intent(mContext,DetailProductActivity.class);
            i.putExtra("product_id",String.valueOf(product_id));
            i.putExtra("product_id_position",getAdapterPosition());
            //Log.i("opp_position", String.valueOf(position));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        }
    }
}
