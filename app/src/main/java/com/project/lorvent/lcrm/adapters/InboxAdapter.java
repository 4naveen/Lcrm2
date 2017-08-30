package com.project.lorvent.lcrm.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.InboxDetailActivity;
import com.project.lorvent.lcrm.models.Inbox;
import com.project.lorvent.lcrm.utils.Appconfig;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder> {
    private Context context;
    private ArrayList<Inbox> inboxArrayList;
    private int email_id;
    private int emailIdPosition;
    public InboxAdapter(Context context, ArrayList<Inbox> inboxArrayList) {
        this.context = context;
        this.inboxArrayList = inboxArrayList;
    }
    public void remove(int position)
    {
        emailIdPosition=position;
        Inbox inbox=inboxArrayList.get(position);
        int  email_id=inbox.getId();
        new DeleteEmail().execute(Appconfig.TOKEN, String.valueOf(email_id));


    /*    inboxArrayList.remove(position);
        notifyItemRemoved(position);*/
    }
    @Override
    public InboxAdapter.InboxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Configuration config = context.getResources().getConfiguration();
        View v;
        if (config.smallestScreenWidthDp >= 600) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_inbox_item,parent,false);
        }
        else
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_inbox_item_mob,parent,false);
        }
        return new InboxViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InboxAdapter.InboxViewHolder holder, int position) {
     Inbox inbox=inboxArrayList.get(position);
        ArrayList<String>date=new ArrayList<>();
        holder.title.setText(inbox.getTitle());
       // email_id=inbox.getId();
        holder.subtitle.setText(inbox.getSubtitle());
        holder.description.setText(inbox.getDescription());
        String name=inbox.getTitle();
        //Log.i("position-", String.valueOf(position)+" name--"+lead.getName()+"number--"+lead.getNumber()+"id--"+lead.getId());
        String ch= String.valueOf(name.charAt(0));

        ColorGenerator generator= ColorGenerator.MATERIAL;
        int color=generator.getRandomColor();
        TextDrawable.builder().beginConfig().fontSize(20).width(10).height(10).endConfig();
        //Color.rgb(97,107,192);
        TextDrawable drawable= TextDrawable.builder().buildRound(ch.toUpperCase(),color );
        holder.letter.setImageDrawable(drawable);
        StringTokenizer tk1=new StringTokenizer(inbox.getDate());
        while (tk1.hasMoreTokens()) {
            date.add(tk1.nextToken());
        }
        String date1[]=date.get(0).split("-");
        if (date1[1].equals("01"))
        {
            String text_date=date1[2]+"Jan";
           // Log.i("date",text_date);
            holder.date.setText(text_date);

        }
        else if (date1[1].equals("02"))
        {
            String text_date=date1[2]+"Feb";
           // Log.i("date",text_date);
            holder.date.setText(text_date);

        }
        else if (date1[1].equals("03"))
        {
            String text_date=date1[2]+"March";
           /// Log.i("date",text_date);
            holder.date.setText(text_date);

        }
        else if (date1[1].equals("04"))
        {
            String text_date=date1[2]+"April";
           // Log.i("date",text_date);
            holder.date.setText(text_date);

        }
        else if (date1[1].equals("05"))
        {
            String text_date=date1[2]+"May";
           // Log.i("date",text_date);
            holder.date.setText(text_date);


        }
        else if (date1[1].equals("06"))
        {
            String text_date=date1[2]+"Jun";
           // Log.i("date",text_date);
            holder.date.setText(text_date);


        }
        else if (date1[1].equals("07"))
        {
            String text_date=date1[2]+"July";
           // Log.i("date",text_date);
            holder.date.setText(text_date);


        }
        else if (date1[1].equals("08"))
        {
            String text_date=date1[2]+"Aug";
           // Log.i("date",text_date);
            holder.date.setText(text_date);


        }
        else if (date1[1].equals("09"))
        {
            String text_date=date1[2]+"Sept";
            //Log.i("date",text_date);
            holder.date.setText(text_date);


        }
        else if (date1[1].equals("10"))
        {
            String text_date=date1[2]+"Oct";
           // Log.i("date",text_date);
            holder.date.setText(text_date);


        }
        else if (date1[1].equals("11"))
        {
            String text_date=date1[2]+"Nov";
           // Log.i("date",text_date);
            holder.date.setText(text_date);

        }
        else {
            String text_date=date1[2]+"Dec";
           // Log.i("date",text_date);
            holder.date.setText(text_date);

        }
    }

    @Override
    public int getItemCount() {
        return inboxArrayList.size();
    }

    class InboxViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title,subtitle,description,date;
        ImageView letter;

        InboxViewHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.mail_title);
            date=(TextView)itemView.findViewById(R.id.date);
            subtitle=(TextView)itemView.findViewById(R.id.subtitle);
            description=(TextView)itemView.findViewById(R.id.description);
            letter=(ImageView)itemView.findViewById(R.id.letter);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Inbox inbox=inboxArrayList.get(getAdapterPosition());
            email_id=inbox.getId();
            Intent intent=new Intent(context,InboxDetailActivity.class);
            intent.putExtra("email_id",email_id);
            context.startActivity(intent);
        }
    }

    private class DeleteEmail extends AsyncTask<String,Void,String>
    {
        // ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String etemplate_id=params[1];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("email_id",etemplate_id);

                SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String delete_url;
                if (text_url != null) {
                    delete_url = text_url + "/delete_email?token=";
                } else {
                    delete_url = Appconfig.DELETE_EMAIL_URL;
                }
                url = new URL(delete_url+tok);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
                //Log.i("res code--",""+conn.getResponseCode());
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));

                    jsonresponse = json.getString("success");

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    Log.i("response",response);
                    json = new JSONObject(response);
                    jsonresponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {
            //  dialog.dismiss();
            //checkResponse=result;
            if (result!=null)
            {
                if (result.equals("success"))
                {
                    inboxArrayList.remove(emailIdPosition);
                    notifyItemRemoved(emailIdPosition);
                    Log.i("Res--",result);
                }
                if (result.equals("not_valid_data"))
                {
                    Log.i("message--","not deleted");
                }
            }

        }

    }
}
