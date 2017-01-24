package com.project.naveen.lcrm.staff;


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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.adapters.StaffAdapter;

import org.json.JSONArray;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    EditText firstNasme, lastName, email, password,confirm_password;
    Button submit;
    TextInputLayout fname, lname, mail, pwd,confirm_pwd;
    String token;
    ImageView upload;
    ProgressDialog progressDialog;
    CoordinatorLayout coordinator;
    Bitmap bitmap;
    String staff_id;
    ArrayList<String> permissions;
    CheckBox salesteam_read,salesteam_write,salesteam_delete,lead_read,lead_write,lead_delete,opp_read,opp_write,opp_delete,loggedcall_read,
            loggedcall_write,loggedcall_delete,meeting_read,meeting_write,meeting_delete,products_read,products_write,products_delete,
            quotation_read,quotation_write,quotation_delete,salesorder_read,salesorder_write,salesorder_delete,invoices_read,invoices_write,invoices_delete,
            contracts_read,contracts_write,contracts_delete,staff_read,staff_write,staff_delete,contacts_read,contacts_write,contacts_delete;
    public EditFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit6, container, false);
        coordinator=(CoordinatorLayout)v.findViewById(R.id.staff_coordinator);
        staff_id=getArguments().getString("staffId");

   /*     android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("Add Staff");*/
        fname = (TextInputLayout)v. findViewById(R.id.input_layout_firstName);
        lname = (TextInputLayout)v. findViewById(R.id.input_layout_lastName);
        mail = (TextInputLayout)v. findViewById(R.id.input_layout_email);
        pwd = (TextInputLayout)v. findViewById(R.id.input_layout_password);
        confirm_pwd = (TextInputLayout)v. findViewById(R.id.input_layout_confirm_password);

        salesteam_read=(CheckBox)v.findViewById(R.id.salesteam_read);
        salesteam_write=(CheckBox)v.findViewById(R.id.salesteam_read);
        salesteam_delete=(CheckBox)v.findViewById(R.id.salesteam_read);
        lead_read=(CheckBox)v.findViewById(R.id.leads_read);
        lead_write=(CheckBox)v.findViewById(R.id.leads_write);
        lead_delete=(CheckBox)v.findViewById(R.id.leads_delete);
        opp_read=(CheckBox)v.findViewById(R.id.opp_read);
        opp_write=(CheckBox)v.findViewById(R.id.opp_write);
        opp_delete=(CheckBox)v.findViewById(R.id.opp_delete);
        loggedcall_read=(CheckBox)v.findViewById(R.id.loggedCalls_read);
        loggedcall_write=(CheckBox)v.findViewById(R.id.loggedCalls_write);
        loggedcall_delete=(CheckBox)v.findViewById(R.id.loggedCalls_delete);
        quotation_read=(CheckBox)v.findViewById(R.id.quotation_read);
        quotation_write=(CheckBox)v.findViewById(R.id.quotation_write);
        quotation_delete=(CheckBox)v.findViewById(R.id.quotation_delete);
        salesorder_read=(CheckBox)v.findViewById(R.id.salesorder_read);
        salesorder_write=(CheckBox)v.findViewById(R.id.salesteam_read);
        salesorder_delete=(CheckBox)v.findViewById(R.id.salesteam_read);
        meeting_read=(CheckBox)v.findViewById(R.id.meeting_read);
        meeting_write=(CheckBox)v.findViewById(R.id.meeting_write);
        meeting_delete=(CheckBox)v.findViewById(R.id.meeting_delete);
        products_read=(CheckBox)v.findViewById(R.id.products_read);
        products_write=(CheckBox)v.findViewById(R.id.products_write);
        products_delete=(CheckBox)v.findViewById(R.id.products_delete);
        invoices_read=(CheckBox)v.findViewById(R.id.invoices_read);
        invoices_write=(CheckBox)v.findViewById(R.id.invoices_write);
        invoices_delete=(CheckBox)v.findViewById(R.id.invoices_delete);
        contracts_read=(CheckBox)v.findViewById(R.id.contracts_read);
        contracts_write=(CheckBox)v.findViewById(R.id.contracts_write);
        contracts_delete=(CheckBox)v.findViewById(R.id.contracts_delete);
        staff_read=(CheckBox)v.findViewById(R.id.staff_read);
        staff_write=(CheckBox)v.findViewById(R.id.staff_write);
        staff_delete=(CheckBox)v.findViewById(R.id.staff_delete);
        contacts_read=(CheckBox)v.findViewById(R.id.contacts_read);
        contacts_write=(CheckBox)v.findViewById(R.id.contacts_write);
        contacts_delete=(CheckBox)v.findViewById(R.id.contacts_delete);
        permissions=new ArrayList<>();
        token= Appconfig.TOKEN;
        upload=(ImageView)v.findViewById(R.id.upload);
        firstNasme = (EditText)v.findViewById(R.id.fname);
        lastName = (EditText)v.findViewById(R.id.lname);
        email = (EditText)v.findViewById(R.id.email);
        password = (EditText)v.findViewById(R.id.password);
        confirm_password= (EditText)v.findViewById(R.id.confirm_password);
        submit = (Button)v.findViewById(R.id.submit);
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
                InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
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
                new EditStaff().execute(token,firstNasme.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString(),staff_id);

                // new MytaskaddStaff().execute(token,firstNasme.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString());

            }
        });
        return v;

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101&&resultCode== Activity.RESULT_OK)
        {
            Uri uri=data.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
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
                getActivity().finish();

            }
        }
        return super.onOptionsItemSelected(item);
    }


    class EditStaff extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
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
            String staff_id=params[5];

            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                JSONArray jsonArray=new JSONArray();
                jsonObject.put("first_name",first_name);
                jsonObject.put("last_name",last_name);
                jsonObject.put("email",email);
                jsonObject.put("staff_id",staff_id);
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
            {
                final Snackbar snackbar = Snackbar.make(coordinator, "Updated Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
            else {
                final Snackbar snackbar = Snackbar.make(coordinator, "Item not updated! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
           // new StaffAdapter(getActivity(),StaffAdapter.staffs).notifyItemInserted(StaffAdapter.staffs.size()+1);
//            finish();

        }
    }
}
