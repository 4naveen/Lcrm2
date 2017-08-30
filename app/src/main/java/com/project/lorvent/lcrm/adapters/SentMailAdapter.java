package com.project.lorvent.lcrm.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.SentMail;
import com.project.lorvent.lcrm.utils.Appconfig;


import org.json.JSONException;
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


public class SentMailAdapter extends RecyclerView.Adapter<SentMailAdapter.SentMailViewHolder> {
    private Context context;
    private ArrayList<SentMail> sentMailArrayList;
    private TextView title1,date1,text1;
    private  int email_id;
    ProgressDialog progressDialog;
    private ProgressBar spinner;
    private int emailIdPosition;

    public SentMailAdapter(Context context, ArrayList<SentMail> sentMailArrayList) {
        this.context = context;
        this.sentMailArrayList = sentMailArrayList;
    }
    public void remove(int position)
    {
        emailIdPosition=position;
        SentMail sentMail=sentMailArrayList.get(position);
        int  email_id=sentMail.getId();
        new DeleteEmail().execute(Appconfig.TOKEN, String.valueOf(email_id));
    }
    @Override
    public SentMailAdapter.SentMailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Configuration config = context.getResources().getConfiguration();
        View v;
        if (config.smallestScreenWidthDp >= 600) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_inbox_item,parent,false);
        }
        else
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_inbox_item_mob,parent,false);

        }
        return new SentMailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SentMailAdapter.SentMailViewHolder holder, int position) {
        ArrayList<String>date=new ArrayList<>();
        SentMail sentMail=sentMailArrayList.get(position);
       // email_id=sentMail.getId();
        holder.title.setText(sentMail.getTitle());
        holder.subtitle.setText(sentMail.getSubtitle());
        holder.description.setText(sentMail.getDescription());
        String name=sentMail.getTitle();
        //Log.i("position-", String.valueOf(position)+" name--"+lead.getName()+"number--"+lead.getNumber()+"id--"+lead.getId());
        String ch= String.valueOf(name.charAt(0));

        ColorGenerator generator= ColorGenerator.MATERIAL;
        int color=generator.getRandomColor();
        TextDrawable.builder().beginConfig().fontSize(20).width(10).height(10).endConfig();
        //Color.rgb(97,107,192);
        TextDrawable drawable= TextDrawable.builder().buildRound(ch.toUpperCase(),color );
        holder.letter.setImageDrawable(drawable);
        StringTokenizer tk1=new StringTokenizer(sentMail.getDate());
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
            //Log.i("date",text_date);
            holder.date.setText(text_date);

        }
        else if (date1[1].equals("03"))
        {
            String text_date=date1[2]+"March";
           // Log.i("date",text_date);
            holder.date.setText(text_date);

        }
        else if (date1[1].equals("04"))
        {
            String text_date=date1[2]+"April";
            //Log.i("date",text_date);
            holder.date.setText(text_date);

        }
        else if (date1[1].equals("05"))
        {
            String text_date=date1[2]+"May";
            //Log.i("date",text_date);
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
           // Log.i("date",text_date);
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
            //Log.i("date",text_date);
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
        return sentMailArrayList.size();
    }

    class SentMailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView title,subtitle,description,date;
        ImageView letter;
        SentMailViewHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.mail_title);
            subtitle=(TextView)itemView.findViewById(R.id.subtitle);
            description=(TextView)itemView.findViewById(R.id.description);
            letter=(ImageView)itemView.findViewById(R.id.letter);
            date=(TextView)itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            SentMail sentMail=sentMailArrayList.get(getAdapterPosition());
            email_id=sentMail.getId();

            MaterialDialog dialog1=new MaterialDialog.Builder(context)
                    .title("Details")
                    .customView(R.layout.details_dialog, true)
                    .positiveText("ok")
                    .autoDismiss(false)
                    .positiveColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            dialog.dismiss();
                        }
                    })
                    .show();

            View view = dialog1.getCustomView();
            if (view != null) {

                title1=(TextView)dialog1.getCustomView().findViewById(R.id.title);
                date1=(TextView)dialog1.getCustomView().findViewById(R.id.date);
                text1=(TextView)dialog1.getCustomView().findViewById(R.id.text);
                spinner = (ProgressBar)dialog1.findViewById(R.id.pbHeaderProgress);
                spinner.setVisibility(View.GONE);
                new GetEmail().execute(Appconfig.TOKEN, String.valueOf(email_id));


            }
        }
    }

    private class GetEmail extends AsyncTask<String,Void,String>
    {
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
       /*     progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading, please wait");
            progressDialog.setTitle("Connecting server");
            progressDialog.show();
            progressDialog.setCancelable(false);*/

            spinner.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonResponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String email_id=params[1];
            URL url;
            try {

                SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url = text_url + "/email?token=";
                } else {
                    detail_url = Appconfig.EMAIL_URL;
                }
                url = new URL(detail_url+tok+"&email_id="+email_id);
                conn = (HttpURLConnection) url.openConnection();
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));
                    jsonResponse=response;

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonResponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
            return jsonResponse;
        }
        @Override
        protected void onPostExecute(String result) {
           // progressDialog.dismiss();
            spinner.setVisibility(View.GONE);

            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONObject object=jsonObject.getJSONObject("email");
                title1.setText(object.getString("subject"));
                text1.setText(object.getString("message"));
                date1.setText(object.getString("created_at"));
            } catch (JSONException e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
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

                SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
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
                    sentMailArrayList.remove(emailIdPosition);
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
