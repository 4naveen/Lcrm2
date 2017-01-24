package com.project.naveen.lcrm.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Contacts;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Guest on 12/2/2016.
 */

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

    }

    @Override
    public int getItemCount() {
        return contactsArrayList.size();
    }

    class CpersonViewHolder extends RecyclerView.ViewHolder {
        TextView name,email;
        CircleImageView contact_image;
        ImageView arrow;
        CpersonViewHolder(View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.name);
            email=(TextView)itemView.findViewById(R.id.email);
            contact_image=(CircleImageView)itemView.findViewById(R.id.contact_image);
            arrow=(ImageView)itemView.findViewById(R.id.arrow);
        }
    }
}
