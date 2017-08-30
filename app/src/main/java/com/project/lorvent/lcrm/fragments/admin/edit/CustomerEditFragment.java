package com.project.lorvent.lcrm.fragments.admin.edit;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.fragments.admin.details.CustomerDetailsFragment;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.squareup.picasso.Picasso;
import com.weiwangcn.betterspinner.library.BetterSpinner;

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
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerEditFragment extends Fragment {
    EditText first_name,last_name,email,password,password_confirm,phone;
    Button submit;
    ImageView upload;
    AppCompatCheckBox main_contact_person;
    String customer_id;
    LinearLayout linearLayout;
    BetterSpinner salesTeam,company;
    Bitmap bitmap;
    public CustomerEditFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_edit12, container, false);
        Connection.getCustomerList(Appconfig.TOKEN,getActivity());
        Connection.getSalesTeamList(Appconfig.TOKEN,getActivity());
        linearLayout = (LinearLayout) v.findViewById(R.id.layout);
        customer_id = getArguments().getString("customer_id");
        salesTeam=(BetterSpinner)v.findViewById(R.id.salesteam);
        company=(BetterSpinner)v.findViewById(R.id.company);
        first_name=(EditText)v.findViewById(R.id.first_name);
        email=(EditText)v.findViewById(R.id.email);
        last_name=(EditText)v.findViewById(R.id.last_name);
        password=(EditText)v.findViewById(R.id.password);
        password_confirm=(EditText)v.findViewById(R.id.password_confirm);
        phone=(EditText)v.findViewById(R.id.phone);
        main_contact_person=(AppCompatCheckBox)v.findViewById(R.id.mcp);
        submit=(Button)v.findViewById(R.id.submit);
        upload=(ImageView)v.findViewById(R.id.upload);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle("Edit Contacts");
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i.createChooser(i,"select pic"),101);
            }
        });
        ArrayAdapter<String> customerArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item, AppSession.companyNameList);
        customerArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        company.setAdapter(customerArrayAdapter);

        ArrayAdapter<String> sales_teamArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.sales_teamNameList);
        sales_teamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        salesTeam.setAdapter(sales_teamArrayAdapter);
        new ContactsDetails().execute(Appconfig.TOKEN,customer_id);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String encoded_image=getStringImage(bitmap);

                int companyId= AppSession.companyList.get(AppSession.companyNameList.indexOf(company.getText().toString())).getId();
                int salesTeamId = AppSession.salesTeamList.get(AppSession.sales_teamNameList.indexOf(salesTeam.getText().toString())).getId();
                new EditContacts().execute(Appconfig.TOKEN,first_name.getText().toString(),last_name.getText().toString(),
                        email.getText().toString(),password.getText().toString(), password_confirm.getText().toString(),
                        phone.getText().toString(),String.valueOf(salesTeamId),String.valueOf(companyId),customer_id,encoded_image);


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
               // Log.i("image length", String.valueOf(lengthbmp));
                if (lengthbmp>512000)
                {
                    Toast.makeText(getActivity(),"image size should be less than 500kb",Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    upload.setImageBitmap(bitmap);
                }
               // upload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class EditContacts extends AsyncTask<String,Void,String>
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
            String response = "", jsonresponse = "";
            BufferedReader bufferedReader = null;
            JSONObject json = null;
            JSONObject jsonObject = null;
            String tok = params[0];
            String first_name = params[1];
            String last_name =params[2];
            String email = params[3];
            String password = params[4];
            String password_confirmation=params[5];
            String phone=params[6];
            String sales_team_id=params[7];
            String company_id=params[8];
            String customer_id=params[9];
            String image=params[10];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("first_name", first_name);
                jsonObject.put("last_name", last_name);
                jsonObject.put("email", email);
               // jsonObject.put("password", password);
               // jsonObject.put("password_confirmation", password_confirmation);
                jsonObject.put("phone_number", phone);
                jsonObject.put("sales_team_id", sales_team_id);
                jsonObject.put("company_id", company_id);
                jsonObject.put("customer_id", customer_id);
                jsonObject.put("avatar",image);

                Log.i("jsonobject--",jsonObject.toString());

                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String edit_url;
                if (text_url != null) {
                    edit_url= text_url + "/user/edit_customer?token=";
                } else {
                    edit_url= Appconfig.CONTACTS_EDIT_URL;
                }
                url = new URL(edit_url+ tok);
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
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonresponse = json.getString("success");

                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line ="";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonresponse = json.getString("error");

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            if (result.equals("success")) {

                final Snackbar snackbar = Snackbar.make(linearLayout, "Updated Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
                Bundle bundle=new Bundle();
                bundle.putString("customer_id", customer_id);
                Fragment fragment1 = new CustomerDetailsFragment();
                FragmentTransaction trans1 = getFragmentManager().beginTransaction();
                fragment1.setArguments(bundle);
                trans1.replace(R.id.frame, fragment1);
                trans1.addToBackStack(null);
                trans1.commit();
            } else {
                final Snackbar snackbar = Snackbar.make(linearLayout, "Item not updated! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }
    private class ContactsDetails extends AsyncTask<String,Void,String>
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
            String customer_id=params[1];
            URL url;
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url= text_url + "/user/company?token=";
                } else {
                    detail_url= Appconfig.CUSTOMER_DETAILS_URL;
                }
                url = new URL(detail_url+tok+"&customer_id="+customer_id);
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
            }
            return jsonResponse;
        }
        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
           // Log.i("response",result);
            JSONObject jsonObject;
            String[]names;
            try {
                jsonObject = new JSONObject(result);
                names =new String[2];
                JSONArray jsonArray=jsonObject.getJSONArray("customer");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    StringTokenizer tokenizer=new StringTokenizer(jsonObject1.getString("full_name"));

                    while (tokenizer.hasMoreTokens())
                    {

                        names[0]=tokenizer.nextToken();
                        names[1]=tokenizer.nextToken();

                    }
                    first_name.setText(names[0]);
                    last_name.setText(names[1]);
                    email.setText(jsonObject1.getString("email"));
                    phone.setText(jsonObject1.getString("mobile"));
                    company.setText(jsonObject1.getString("company"));
                    salesTeam.setText(jsonObject1.getString("salesteam"));
                    if(!jsonObject1.getString("avatar").equalsIgnoreCase("null")){

                        Picasso.with(getActivity())
                                .load(jsonObject1.getString("avatar"))
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(upload);                    }
                }

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

}
