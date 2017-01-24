package com.project.naveen.lcrm.addactivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.StaffAdapter;
import com.project.naveen.lcrm.models.Staff;


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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AddStaffActivity extends AppCompatActivity {
    EditText firstNasme, lastName, email, password,confirm_password;
    Button submit;
    TextInputLayout fname, lname, mail, pwd,confirm_pwd;
    String token;
    ImageView upload;
    ProgressDialog progressDialog;
    CoordinatorLayout coordinator;
    Bitmap bitmap;
    ArrayList<String>permissions;
    CheckBox salesteam_read,salesteam_write,salesteam_delete,lead_read,lead_write,lead_delete,opp_read,opp_write,opp_delete,loggedcall_read,
            loggedcall_write,loggedcall_delete,meeting_read,meeting_write,meeting_delete,products_read,products_write,products_delete,
            quotation_read,quotation_write,quotation_delete,salesorder_read,salesorder_write,salesorder_delete,invoices_read,invoices_write,invoices_delete,
            contracts_read,contracts_write,contracts_delete,staff_read,staff_write,staff_delete,contacts_read,contacts_write,contacts_delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);
        coordinator=(CoordinatorLayout)findViewById(R.id.staff_coordinator);

        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("Add Staff");
        fname = (TextInputLayout) findViewById(R.id.input_layout_firstName);
        lname = (TextInputLayout) findViewById(R.id.input_layout_lastName);
        mail = (TextInputLayout) findViewById(R.id.input_layout_email);
        pwd = (TextInputLayout) findViewById(R.id.input_layout_password);
        confirm_pwd = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);

        salesteam_read=(CheckBox)findViewById(R.id.salesteam_read);
        salesteam_write=(CheckBox)findViewById(R.id.salesteam_read);
        salesteam_delete=(CheckBox)findViewById(R.id.salesteam_read);
        lead_read=(CheckBox)findViewById(R.id.leads_read);
        lead_write=(CheckBox)findViewById(R.id.leads_write);
        lead_delete=(CheckBox)findViewById(R.id.leads_delete);
        opp_read=(CheckBox)findViewById(R.id.opp_read);
        opp_write=(CheckBox)findViewById(R.id.opp_write);
        opp_delete=(CheckBox)findViewById(R.id.opp_delete);
        loggedcall_read=(CheckBox)findViewById(R.id.loggedCalls_read);
        loggedcall_write=(CheckBox)findViewById(R.id.loggedCalls_write);
        loggedcall_delete=(CheckBox)findViewById(R.id.loggedCalls_delete);
        quotation_read=(CheckBox)findViewById(R.id.quotation_read);
        quotation_write=(CheckBox)findViewById(R.id.quotation_write);
        quotation_delete=(CheckBox)findViewById(R.id.quotation_delete);
        salesorder_read=(CheckBox)findViewById(R.id.salesorder_read);
        salesorder_write=(CheckBox)findViewById(R.id.salesteam_read);
        salesorder_delete=(CheckBox)findViewById(R.id.salesteam_read);
        meeting_read=(CheckBox)findViewById(R.id.meeting_read);
        meeting_write=(CheckBox)findViewById(R.id.meeting_write);
        meeting_delete=(CheckBox)findViewById(R.id.meeting_delete);
        products_read=(CheckBox)findViewById(R.id.products_read);
        products_write=(CheckBox)findViewById(R.id.products_write);
        products_delete=(CheckBox)findViewById(R.id.products_delete);
        invoices_read=(CheckBox)findViewById(R.id.invoices_read);
        invoices_write=(CheckBox)findViewById(R.id.invoices_write);
        invoices_delete=(CheckBox)findViewById(R.id.invoices_delete);
        contracts_read=(CheckBox)findViewById(R.id.contracts_read);
        contracts_write=(CheckBox)findViewById(R.id.contracts_write);
        contracts_delete=(CheckBox)findViewById(R.id.contracts_delete);
        staff_read=(CheckBox)findViewById(R.id.staff_read);
        staff_write=(CheckBox)findViewById(R.id.staff_write);
        staff_delete=(CheckBox)findViewById(R.id.staff_delete);
        contacts_read=(CheckBox)findViewById(R.id.contacts_read);
        contacts_write=(CheckBox)findViewById(R.id.contacts_write);
        contacts_delete=(CheckBox)findViewById(R.id.contacts_delete);
        permissions=new ArrayList<>();
        token= Appconfig.TOKEN;
        upload=(ImageView)findViewById(R.id.upload);
        firstNasme = (EditText) findViewById(R.id.fname);
        lastName = (EditText) findViewById(R.id.lname);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirm_password= (EditText) findViewById(R.id.confirm_password);

        submit = (Button) findViewById(R.id.submit);
           upload.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent i=new Intent();
                   i.setType("image/*");
                   i.setAction(Intent.ACTION_GET_CONTENT);
                   startActivityForResult(i.createChooser(i,"select pic"),101);
               }
           });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                if (firstNasme.getText().toString().trim().isEmpty()) {
                    fname.setError("please enter opptornity");

                    return;
                }
                if (email.getText().toString().trim().isEmpty()) {
                    mail.setError("please enter email");
                    return;
                }
                if (lastName.getText().toString().trim().isEmpty()) {
                    lname.setError("please enter customer_id");
                    return;
                }
                if (password.getText().toString().trim().isEmpty()) {
                    pwd.setError("please enter  sales team id");
                    return;
                }
                if (password.getText().toString().compareTo(confirm_password.getText().toString())!=0)
                {
                    confirm_pwd.setError("Password do not match");
                    return;
                }
                if (salesteam_read.isChecked())
                {
                    permissions.add("sales_team.read");
                }
                if (salesteam_write.isChecked())
                {
                    permissions.add("sales_team.write");
                }
                if (salesteam_delete.isChecked())
                {
                    permissions.add("sales_team.delete");

                }
                if (lead_read.isChecked())
                {
                    permissions.add("leads.read");
                }
                if (lead_write.isChecked())
                {
                    permissions.add("leads.write");
                }
                if (lead_delete.isChecked())
                {
                    permissions.add("leads.delete");
                }
                if (opp_read.isChecked())
                {
                    permissions.add("opportunities.read");
                }
                if (opp_write.isChecked())
                {
                    permissions.add("opportunities.write");
                }
                if (opp_delete.isChecked())
                {
                    permissions.add("opportunities.delete");
                }
                //addStaff(token);
                new MytaskaddStaff().execute(token,firstNasme.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString());

                // new MytaskaddStaff().execute(token,firstNasme.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString());

            }
        });
    }

/*
    private void addStaff(String token) {
            progressDialog = new ProgressDialog(AddStaffActivity.this);
            progressDialog.setMessage("Loading, please wait");
            progressDialog.setTitle("Connecting server");
            progressDialog.show();
            progressDialog.setCancelable(false);
            StringRequest stringRequest = new StringRequest(Request.Method.POST,Appconfig.POST_STAFF_URL+token,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            final Snackbar snackbar=Snackbar.make(coordinator, "Added new Staff Successfully",Snackbar.LENGTH_LONG);
                            View v=snackbar.getView();
                              v.setMinimumWidth(1000);

                            TextView tv=(TextView)v.findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTextColor(Color.RED);
                            snackbar.show();
                            progressDialog.dismiss();

                        }
                    },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.i("response--", String.valueOf(error));
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("first_name",firstNasme.getText().toString());
                    params.put("last_name",lastName.getText().toString());
                    params.put("email",email.getText().toString());
                    params.put("password", password.getText().toString());
                    return params;
                }

            } ;
            MyVolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(stringRequest);

//       image upload to server--------
       */
/* ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("image", encodedImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "upload_url", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("Message from server", jsonObject.toString());
                        Toast.makeText(getApplication(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Message from server", volleyError.toString());

            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyVolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(jsonObjectRequest);

*//*

    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101&&resultCode== Activity.RESULT_OK)
        {
            Uri uri=data.getData();

            try {

                 bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                 Log.d("image--", String.valueOf(bitmap));

                 upload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();

            }
        }
        return super.onOptionsItemSelected(item);
    }


    class MytaskaddStaff extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddStaffActivity.this);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
           StringBuffer permission;

            BufferedReader bufferedReader;
            String tok=params[0];
            String first_name=params[1];
            String last_name=params[2];
            String email=params[3];
            String password=params[4];

            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                JSONArray jsonArray=new JSONArray();
                jsonObject.put("first_name",first_name);
                jsonObject.put("last_name",last_name);
                jsonObject.put("email",email);

                jsonObject.put("password",password);
                permission=new StringBuffer();
                //  jsonObject.put("permission[]",permission);
                // JSONObject jsonObject1=new JSONObject();
                for (int i = 1; i <=permissions.size() ; i++) {
                    // jsonObject.put("permissions["+i+"]",permissions.get(i));
                   /* JSONObject object=new JSONObject();
                    object.put("permission",permissions.get(i));
                    jsonArray.put(object);*/
                    //jsonArray.put(permissions.get(i));
                    if (i<permissions.size())
                    {
                        permission.append(permissions.get(i-1)+",");
                    }
                    if (i==permissions.size())
                    {permission.append(permissions.get(i-1));}
//                   permission=permission+permissions.get(i)+",";
                }

                //jsonObject.put("permission",jsonArray);
                jsonObject.put("permissions",permission);

                // jsonObject.put("permissions[]",String.valueOf(permissions).substring(1,(String.valueOf(permissions).length()-1)));
                Log.i("permissions--",permissions.toString());
                Log.i("permission--", String.valueOf(permission));

                url = new URL(Appconfig.POST_STAFF_URL+tok);
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
                Log.i("jsonObject",jsonObject.toString());
//                Log.i("jsonObject1",jsonObject1.toString());

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
            dialog.dismiss();
            if (result.equals("success"))
            {               getAllStaff(token);

                final Snackbar snackbar = Snackbar.make(coordinator, "Added 1 item Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
            else {
                final Snackbar snackbar = Snackbar.make(coordinator, "Item not added! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
           // new StaffAdapter(getApplicationContext(),StaffAdapter.staffs).notifyItemInserted(StaffAdapter.staffs.size()+1);
//            finish();

        }
    }

    private void getAllStaff(String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,Appconfig.STAFF_URL+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("response--",response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("staffs");
                            AppSession.staffArrayList.clear();
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject object=jsonArray.getJSONObject(i);
                                Staff staff =new Staff();
                                staff.setName(object.getString("full_name"));
                                staff.setEmail(object.getString("email"));
                                staff.setId(object.getInt("id"));
                                AppSession.staffArrayList.add(staff);
                            }
                            StaffAdapter staffAdapter=new StaffAdapter(getApplicationContext(),AppSession.staffArrayList);
                             // staffAdapter.notifyItemInserted(StaffAdapter.staffs.size()+1);
                            //new staffAdapter=new StaffAdapter(getActivity(),AppSession.staffArrayList);
                            // AppSession.staffArrayListGlobal=staffArrayList;
                            AppSession.staff_recyclerView.setAdapter(staffAdapter);
                            AppSession.staff_recyclerView.setItemAnimator(new DefaultItemAnimator());
                            // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                            RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);
                            //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

                            AppSession.staff_recyclerView.setLayoutManager(lmanager);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.i("response--", String.valueOf(error));
            }
        }) ;
        MyVolleySingleton.getInstance(AddStaffActivity.this).getRequestQueue().add(stringRequest);

    }

}