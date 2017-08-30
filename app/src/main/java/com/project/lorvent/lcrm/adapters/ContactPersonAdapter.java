package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.details.admin.DetailCustomerActivity;
import com.project.lorvent.lcrm.models.Contacts;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactPersonAdapter extends RecyclerView.Adapter<ContactPersonAdapter.CpersonViewHolder> {
    private Context context;
    private ArrayList<Contacts>contactsArrayList;
    View v;
    public ContactPersonAdapter(Context context, ArrayList<Contacts> contactsArrayList) {
        this.context = context;
        this.contactsArrayList = contactsArrayList;
    }

    @Override
    public CpersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_contact_person, parent, false);
        return new CpersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ContactPersonAdapter.CpersonViewHolder viewHolder, int position) {
                Contacts contacts=contactsArrayList.get(position);
                viewHolder.name.setText(contacts.getName());
                viewHolder.email.setText(contacts.getEmail());
                Picasso.with(context)
                .load(contacts.getAvatar())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(viewHolder.contact_image);
    }

    @Override
    public int getItemCount() {
        return contactsArrayList.size();
    }

    class CpersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name,email;
        CircleImageView contact_image;
        ImageView arrow;
        CpersonViewHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.name);
            email=(TextView)itemView.findViewById(R.id.email);
            contact_image=(CircleImageView)itemView.findViewById(R.id.contact_image);
            arrow=(ImageView)itemView.findViewById(R.id.arrow);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int customer_id=contactsArrayList.get(getAdapterPosition()).getId();
            Intent i=new Intent(context, DetailCustomerActivity.class);
            i.putExtra("customer_id",String.valueOf(customer_id));
            i.putExtra("customer_id_position",getAdapterPosition());
            // Log.i("company_id_position", String.valueOf(position));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
