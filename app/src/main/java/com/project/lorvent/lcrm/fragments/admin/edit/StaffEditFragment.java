package com.project.lorvent.lcrm.fragments.admin.edit;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
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
import android.widget.Toast;

import com.project.lorvent.lcrm.fragments.admin.details.StaffDetailFragment;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffEditFragment extends Fragment {
    EditText firstNasme, lastName, email, password,confirm_password;
    Button submit;
    TextInputLayout fname, lname, mail, pwd,confirm_pwd;
    String token;
    ImageView upload;
    ProgressDialog progressDialog;
    CoordinatorLayout coordinator;
    Bitmap bitmap;
    String staff_id;
    View v;
    ArrayList<String> permissions;
    CheckBox salesteam_read,salesteam_write,salesteam_delete,lead_read,lead_write,lead_delete,opp_read,opp_write,opp_delete,loggedcall_read,
            loggedcall_write,loggedcall_delete,meeting_read,meeting_write,meeting_delete,products_read,products_write,products_delete,
            quotation_read,quotation_write,quotation_delete,salesorder_read,salesorder_write,salesorder_delete,invoices_read,invoices_write,invoices_delete,
            contracts_read,contracts_write,contracts_delete,staff_read,staff_write,staff_delete,contacts_read,contacts_write,contacts_delete;
    public StaffEditFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            v=inflater.inflate(R.layout.fragment_edit6, container, false);
        }
        else
        {
            v=inflater.inflate(R.layout.fragment_edit6_mob, container, false);
        }
        coordinator=(CoordinatorLayout)v.findViewById(R.id.staff_coordinator);
        staff_id=getArguments().getString("staffId");
          new StaffDetailsTask().execute(Appconfig.TOKEN,staff_id);
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
        salesteam_write=(CheckBox)v.findViewById(R.id.salesteam_write);
        salesteam_delete=(CheckBox)v.findViewById(R.id.salesteam_delete);
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
        salesorder_write=(CheckBox)v.findViewById(R.id.salesorder_write);
        salesorder_delete=(CheckBox)v.findViewById(R.id.salesorder_delete);
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
                String encoded_image=getStringImage(bitmap);

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
                new EditStaff().execute(token,firstNasme.getText().toString(), lastName.getText().toString(), email.getText().toString(), password.getText().toString(),staff_id,encoded_image);


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
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                long lengthbmp = imageInByte.length;
                //.i("image length", String.valueOf(lengthbmp));
                if (lengthbmp>512000)
                {
                    Toast.makeText(getActivity(),"image size should be less than 500kb",Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    upload.setImageBitmap(bitmap);
                }
                //upload.setImageBitmap(bitmap);
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


    private class EditStaff extends AsyncTask<String,Void,String>
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
            String image=params[6];

            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                JSONArray jsonArray=new JSONArray();
                jsonObject.put("first_name",first_name);
                jsonObject.put("last_name",last_name);
                //jsonObject.put("email",email);
                jsonObject.put("staff_id",staff_id);
                jsonObject.put("password",password);
                jsonObject.put("avatar",image);
                permission=new StringBuffer();

                for (int i = 1; i <=permissions.size() ; i++) {

                    if (i<permissions.size())
                    {
                        permission.append(permissions.get(i-1)+",");
                    }
                    if (i==permissions.size())
                    {permission.append(permissions.get(i-1));}
                }
              // jsonObject.put("permissions",permission);

                //Log.i("json object request", String.valueOf(jsonObject));
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String edit_url;
                if (text_url != null) {
                    edit_url= text_url + "/user/edit_staff?token=";
                } else {
                    edit_url= Appconfig.Edit_STAFF_URL;
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
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonresponse = json.getString("success");

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonresponse=json.getString("error");

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
                Bundle bundle=new Bundle();
                bundle.putString("staffId",staff_id);
                FragmentTransaction trans1=getFragmentManager().beginTransaction();
                Fragment fragment1=new StaffDetailFragment();
                fragment1.setArguments(bundle);
                trans1.replace(R.id.frame,fragment1);
                trans1.addToBackStack(null);
                trans1.commit();
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
    private class StaffDetailsTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonResponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String staffId=params[1];
            URL url;
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url= text_url + "/user/staff?token=";
                } else {
                    detail_url= Appconfig.STAFF_DETAILS_URL;
                }
                url = new URL(detail_url+tok+"&staff_id="+staffId);
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

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonResponse;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("staff");
                firstNasme.setText(jsonObject1.getString("first_name"));
                lastName.setText(jsonObject1.getString("last_name"));
                email.setText(jsonObject1.getString("last_name"));
                //getImage(jsonObject1.getString("avatar"));
                Picasso.with(getActivity())
                        .load(jsonObject1.getString("avatar"))
                        .placeholder(R.drawable.user)
                        .error(R.drawable.user)
                        .into(upload);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }
    private void getImage(String image_url)
    {
       /* ImageRequest ir = new ImageRequest(image_url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                upload.setImageBitmap(response);
                bitmap= response;

            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                upload.setImageResource(R.drawable.upload);
                bitmap=((BitmapDrawable)getResources().getDrawable(R.drawable.upload)).getBitmap();
            }
        });
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(ir);*/

    }
}
