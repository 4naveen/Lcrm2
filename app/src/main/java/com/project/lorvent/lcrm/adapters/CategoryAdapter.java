package com.project.lorvent.lcrm.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Category;

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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.SimpleViewHolder> {
    private Context context;
    private static ArrayList<Category> categoryArrayList;
    private TextView category_name,created_at;
    private SimpleViewHolder svHolder;
    private Button ok;
    private int categoryIdPosition;
    private String token;

    public CategoryAdapter(Context context,ArrayList<Category>categories) {
        this.context = context;
        token= Appconfig.TOKEN;
        categoryArrayList=categories;
    }

    @Override
    public CategoryAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_category_item,parent,false);

        return new SimpleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CategoryAdapter.SimpleViewHolder holder, int position) {
        Category category=categoryArrayList.get(position);
        svHolder=holder;
        final int categoryId=category.getId();

        Log.i("list size--", String.valueOf(categoryArrayList.size()));
        holder.tvCategoryName.setText(category.getName());
     /*   holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               *//* final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.category_details_dialog);
                // dialog.setCancelable(false);
                category_name=(TextView)dialog.findViewById(R.id.name);
                created_at=(TextView)dialog.findViewById(R.id.created);
                ok=(Button)dialog.findViewById(R.id.ok);
                new CategoryDetailsTask().execute(token, String.valueOf(categoryId));*//*

            }
        });*/
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryIdPosition=holder.getLayoutPosition();
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
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
                                new DeleteCategoryTask().execute(token, String.valueOf(categoryId));
                                sweetAlertDialog.cancel();
                            }
                        })
                        .show();
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryIdPosition=holder.getLayoutPosition();

                MaterialDialog dialog1=new MaterialDialog.Builder(context)
                        .title("EditCategory")
                        .customView(R.layout.category_add_dialog, true)
                        .positiveText("SAVE")
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                String c_name=category_name.getText().toString();
                                new EditCategoryTask().execute(token, String.valueOf(categoryId),c_name,String.valueOf(categoryIdPosition));
                            }
                        })
                        .negativeColorRes(R.color.colorPrimary)
                        .negativeText("CANCEL")
                        .show();
                View view = dialog1.getCustomView();
                if (view != null) {
                    category_name=(EditText)dialog1.getCustomView().findViewById(R.id.category_name);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName,tvCreated;
        ImageView delete,details,edit;
          SimpleViewHolder(View itemView) {
            super(itemView);
            tvCategoryName=(TextView)itemView.findViewById(R.id.category_name);
              tvCreated=(TextView)itemView.findViewById(R.id.created);
              delete=(ImageView)itemView.findViewById(R.id.delete);
            edit=(ImageView)itemView.findViewById(R.id.edit);
/*
            details=(ImageView)itemView.findViewById(R.id.details);
*/
        }
    }

    private class DeleteCategoryTask extends AsyncTask<String,Void,String>
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
            String categoryId=params[1];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("category_id",categoryId);
                Log.i("json", String.valueOf(jsonObject));

                SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String delete_url;
                if (text_url != null) {
                    delete_url = text_url + "/user/delete_category?token=";
                } else {
                    delete_url = Appconfig.CATEGORY_DElETE_URL;
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
            if (result!=null)
            {
                if (result.equals("success"))
                { categoryArrayList.remove(categoryIdPosition);
                    notifyItemRemoved(categoryIdPosition);
                    notifyItemRangeChanged(categoryIdPosition,categoryArrayList.size());
                    Log.i("Res--",result);
                }
                if (result.equals("not_valid_data"))
                {
                    Log.i("message--","not deleted");
                }
            }

        }

    }



    private class EditCategoryTask extends AsyncTask<String,Void,String>
    {
        // ProgressDialog dialog;
        HttpURLConnection conn;
        String category_id_position;
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
            String categoryId=params[1];
            String category_name=params[2];
            category_id_position=params[3];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("category_id",categoryId);
                jsonObject.put("name",category_name);
                Log.i("json", String.valueOf(jsonObject));

                SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String edit_url;
                if (text_url != null) {
                    edit_url = text_url + "/user/edit_category?token=";
                } else {
                    edit_url = Appconfig.CATEGORY_EDIT_URL;
                }
                url = new URL(edit_url+tok);
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
            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {
            //  dialog.dismiss();
            if (result!=null)
            {
                if (result.equals("success"))
                {
                    new GetAllCategoryTask().execute(token,category_id_position);

                    Toast.makeText(context,"Updated Succesfully!",Toast.LENGTH_LONG).show();
                }
                if (result.equals("not_valid_data"))
                {
                    Toast.makeText(context,"Item not updated! Try Again",Toast.LENGTH_LONG).show();
                }
            }

        }

    }

    private class GetAllCategoryTask extends AsyncTask<String,Void,String>
    {   String response;
        ProgressDialog dialog;
        String category_id_position;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            HttpURLConnection connection;
            category_id_position=params[1];
            try {
                SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/categories?token=";
                } else {
                    get_url = Appconfig.CATEGORY_URL;
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
            try {
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("categories");
                categoryArrayList.clear();
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Category category=new Category();
                    category.setId(object.getInt("id"));
                    category.setName(object.getString("name"));
                    categoryArrayList.add(category);
                }
                notifyItemChanged(Integer.parseInt(category_id_position));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}
}
