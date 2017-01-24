package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Products;


import java.util.ArrayList;

/**
 * Created by Guest on 10/29/2016.
 */

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

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvCategory;

        ImageView arrow;
        SimpleViewHolder(View itemView) {
            super(itemView);
            tvCategory = (TextView) itemView.findViewById(R.id.category);
            tvProductName = (TextView) itemView.findViewById(R.id.products_name);
            arrow=(ImageView)itemView.findViewById(R.id.arrow);
        }
    }


}
