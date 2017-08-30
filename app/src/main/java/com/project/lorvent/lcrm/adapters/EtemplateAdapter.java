package com.project.lorvent.lcrm.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Etemplate;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 5/27/2017.
 */

public class EtemplateAdapter extends RecyclerSwipeAdapter<EtemplateAdapter.ViewHolder> {

    private ArrayList<Etemplate> etemplateArrayList;
    private Context mContext;
    EditText title,text;
    TextInputLayout input_title, input_text;
    ViewHolder svHolder;
    private int etemplateIdPosition;

    public EtemplateAdapter(ArrayList<Etemplate> etemplateArrayList, Context mContext) {
        this.etemplateArrayList = etemplateArrayList;
        this.mContext = mContext;
    }

    @Override
    public EtemplateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_etemplate, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final EtemplateAdapter.ViewHolder viewHolder, int position) {
         Etemplate etemplate=etemplateArrayList.get(position);
         viewHolder.tvtitle.setText(etemplate.getTitle());
         viewHolder.tvtext.setText(etemplate.getText());
        final int etemplate_id=etemplate.getId();
        svHolder = viewHolder;

        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                viewHolder.arrow.setImageResource(R.mipmap.right);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                viewHolder.arrow.setImageResource(R.mipmap.left);
            }

            @Override
            public void onClose(SwipeLayout layout) {
                viewHolder.arrow.setImageResource(R.mipmap.left);
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                // viewHolder.arrow.setImageResource(R.drawable.arrow);
            }
        });
          viewHolder.layout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEtemplateDetail(String.valueOf(etemplate_id));
                MaterialDialog dialog1=new MaterialDialog.Builder(mContext)
                        .title("Edit Email Template")
                        .customView(R.layout.etemplate_add_dialog, true)
                        .positiveText("save")
                        .autoDismiss(false)
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (title.getText().toString().isEmpty()) {
                                    input_title.setError("please enter title");
                                    return;
                                }
                                else if (text.getText().toString().isEmpty()) {
                                    input_text.setError("please enter text");
                                    return;
                                }

                                else {
                                    dialog.dismiss();
                                }
                                new EditEmailTemplate().execute(Appconfig.TOKEN,title.getText().toString(),text.getText().toString(), String.valueOf(etemplate_id));

                            }
                        })
                        .negativeColorRes(R.color.colorPrimary)
                        .negativeText("CANCEL")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();

                            }
                        })
                        .show();

                View view = dialog1.getCustomView();
                if (view != null) {
                    title=(EditText)dialog1.getCustomView().findViewById(R.id.title);
                    text=(EditText)dialog1.getCustomView().findViewById(R.id.text);
                    input_title = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_title);
                    input_text = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_text);
                    title.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_title.setError("");

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    text.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_text.setError("");

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                }

            }
        });

        viewHolder.layout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etemplateIdPosition=viewHolder.getLayoutPosition();
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this file!")
                        .setCancelText("No,cancel plx!")
                        .setConfirmText("Yes,delete it!")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                new DeleteEtemplate().execute(Appconfig.TOKEN, String.valueOf(etemplate_id));
                                sweetAlertDialog.cancel();

                            }
                        })
                        .show();
                mItemManger.closeAllItems();


            }

        });

        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return etemplateArrayList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return  R.id.swipe;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView tvtitle;
        TextView tvtext;
        LinearLayout layout_edit,layout_delete;
        ImageView arrow;
        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            tvtitle = (TextView) itemView.findViewById(R.id.title);
            tvtext = (TextView) itemView.findViewById(R.id.text);
            layout_edit=(LinearLayout)itemView.findViewById(R.id.layout_edit);
            layout_delete=(LinearLayout)itemView.findViewById(R.id.layout_delete);
            arrow=(ImageView)itemView.findViewById(R.id.arrow);

        }
    }

    private class DeleteEtemplate extends AsyncTask<String,Void,String>
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
                jsonObject.put("email_template_id",etemplate_id);
                url = new URL(Appconfig.ETEMPLATES_DELETE_URL+tok);
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
                 /*   AppSession.etemplateArrayList.remove(svHolder.getAdapterPosition());
                    notifyItemRemoved(svHolder.getAdapterPosition());*/
                   /* oppCalls.remove(callIdPosition);
                    notifyItemRemoved(callIdPosition);
                    notifyItemRangeChanged(callIdPosition,oppCalls.size());
*/
                    etemplateArrayList.remove(etemplateIdPosition);
                    notifyItemRemoved(etemplateIdPosition);
                    notifyItemRangeChanged(etemplateIdPosition,etemplateArrayList.size());
                    Log.i("Res--",result);
                }
                if (result.equals("not_valid_data"))
                {
                    Log.i("message--","not deleted");
                }
            }

        }

    }


    private class EditEmailTemplate extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "", jsonresponse = "";
            BufferedReader bufferedReader ;
            JSONObject json;
            JSONObject jsonObject ;
            String tok = params[0];
            String title =params[1] ;
            String text =params[2] ;
            String etemplate_id=params[3];
            URL url;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("email_template_id", etemplate_id);
                jsonObject.put("title", title);
                jsonObject.put("text", text);
                url = new URL(Appconfig.ETEMPLATE_EDIT_URL+tok);
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
                int responseCode = conn.getResponseCode();
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

                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line ;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    Log.i("response", response);
                    json = new JSONObject(response);
                    jsonresponse = json.getString("error");
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

            dialog.dismiss();
            if (result.equals("success"))
            {
                new GetAllEmailTemplates().execute(Appconfig.TOKEN);

                Toast.makeText(mContext,"Updated Succesfully!",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(mContext,"Item not updated! Try Again",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void getEtemplateDetail(String etemplate_id) {

        Log.i("taskid--", String.valueOf(etemplate_id));
        final ProgressDialog dialog = new ProgressDialog(mContext);
        dialog.setMessage("Loading, please wait...");
        dialog.setTitle("Connecting server");
        dialog.show();
        dialog.setCancelable(false);
        SharedPreferences preferences = mContext.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String detail_url;
        if (text_url != null) {
            detail_url = text_url + "/user/email_template?token=";
        } else {
            detail_url = Appconfig.ETEMPLATES__DETAILS_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,detail_url+Appconfig.TOKEN+"&email_template_id="+etemplate_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("response--",response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONObject object=jsonObject.getJSONObject("email_template");
                            title.setText(object.getString("title"));
                            text.setText(object.getString("text"));
                            dialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }) ;
        MyVolleySingleton.getInstance(mContext).getRequestQueue().add(stringRequest);
    }
    class GetAllEmailTemplates extends AsyncTask<String,Void,String>
    {   String response;
         ProgressDialog  dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            try {

                SharedPreferences preferences = mContext.getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/email_templates?token=";
                } else {
                    get_url = Appconfig.ETEMPLATES_URL;
                }
                url = new URL(get_url+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            if (dialog!=null&&dialog.isShowing()){dialog.dismiss();}
            etemplateArrayList.clear();

            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("email_templates");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Etemplate etemplate=new Etemplate();
                    etemplate.setId(object.getInt("id"));
                    etemplate.setTitle(object.getString("title"));
                    etemplate.setText(object.getString("text"));
                    etemplateArrayList.add(etemplate);
                    AppSession.etemplateArrayList.add(etemplate);


                }
   /*             etemplateAdapter = new EtemplateAdapter(etemplateArrayList, getActivity());
                etemplateAdapter.setMode(Attributes.Mode.Single);
                rv.setAdapter(etemplateAdapter);
                rv.setItemAnimator(new DefaultItemAnimator());
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                rv.setLayoutManager(lmanager);*/

                notifyItemChanged(etemplateIdPosition);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}
}
