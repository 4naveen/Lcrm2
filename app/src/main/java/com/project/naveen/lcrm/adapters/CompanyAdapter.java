package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Company;

import java.util.ArrayList;

/**
 * Created by Guest on 11/2/2016.
 */

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.SimpleViewHolder> {
    private Context mContext;
    private ArrayList<Company>companyArrayList;
    private int companyIdPosition;
    private int companyId;
    private TextView name,email,address,website,phone,mobile,fax,created_at;
    private Button ok;
    private String token;
    View v;
    public CompanyAdapter(Context context, ArrayList<Company>companies) {
        this.mContext=context;
        companyArrayList=companies;
            token= Appconfig.TOKEN;
    }
    @Override
    public CompanyAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Configuration config = mContext.getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_company,parent,false);
        }
        else
        {
            v=LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_company_mob,parent,false);

        }
//        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_company,parent,false);
        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CompanyAdapter.SimpleViewHolder holder, int position) {
        Company company=companyArrayList.get(position);
        companyId=company.getId();
        Log.i("companyId-", String.valueOf(companyId));
        //Log.i("company--",company.getName()+""+company.getCustomer());
        holder.name.setText(company.getName());
        holder.customer.setText(company.getCustomer());
        //holder.phone.setText(company.getPhone());
        String name=company.getName();
        //Log.i("position-", String.valueOf(position)+" name--"+lead.getName()+"number--"+lead.getNumber()+"id--"+lead.getId());
        String ch= String.valueOf(name.charAt(0));

        //ColorGenerator generator=ColorGenerator.MATERIAL;
        // int color=generator.getRandomColor();
        TextDrawable.builder().beginConfig().fontSize(20).width(10).height(10).endConfig();
        TextDrawable drawable= TextDrawable.builder().buildRound(ch.toUpperCase(), Color.rgb(97,107,192));
        holder.letter.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return companyArrayList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView name,customer,phone;
        ImageView options,letter;
        SimpleViewHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.name);
            customer=(TextView)itemView.findViewById(R.id.customer);
           // phone=(TextView)itemView.findViewById(R.id.phone);
            options=(ImageView)itemView.findViewById(R.id.options);
            letter=(ImageView)itemView.findViewById(R.id.letter);

        }
    }

}
