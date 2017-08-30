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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.fragments.admin.details.CompanyDetailsFragment;
import com.project.lorvent.lcrm.models.SalesTeam;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.Util;
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
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyEditFragment extends Fragment {
    EditText company_name, phone, email, address;
    BetterSpinner main_contact_person, sales_team;
    ArrayList<String> main_contact_personList, salesTeamNameList;
    ArrayList<SalesTeam> salesTeamArrayList;
    Button submit;
    ImageView upload;
    TextInputLayout input_company_name, input_email, input_address, input_phone, input_main_contact_person, input_salesteam;
    LinearLayout linearLayout;
    String company_id;
    Bitmap bitmap;
    public CompanyEditFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit4, container, false);
        company_id = getArguments().getString("company_id");
        Connection.getContactList(Appconfig.TOKEN, getActivity());
        Connection.getSalesTeamList(Appconfig.TOKEN, getActivity());
        company_name = (EditText) v.findViewById(R.id.company_name);
        email = (EditText) v.findViewById(R.id.email);
        phone = (EditText) v.findViewById(R.id.phone);
        address = (EditText) v.findViewById(R.id.address);
        main_contact_person = (BetterSpinner) v.findViewById(R.id.mcp);
        sales_team = (BetterSpinner) v.findViewById(R.id.salesteam);
        linearLayout = (LinearLayout) v.findViewById(R.id.layout);

        input_company_name = (TextInputLayout) v.findViewById(R.id.input_layout_company_name);
        input_email = (TextInputLayout) v.findViewById(R.id.input_layout_email);
        input_address = (TextInputLayout) v.findViewById(R.id.input_layout_address);
        input_phone = (TextInputLayout) v.findViewById(R.id.input_layout_phone);
        input_main_contact_person = (TextInputLayout) v.findViewById(R.id.input_layout_main_contact_person);
        input_salesteam = (TextInputLayout) v.findViewById(R.id.input_layout_salesteam);
        main_contact_personList = new ArrayList<>();
        salesTeamNameList = new ArrayList<>();
        salesTeamArrayList = new ArrayList<>();

        submit = (Button) v.findViewById(R.id.submit);
        upload = (ImageView) v.findViewById(R.id.upload);
        ArrayAdapter<String> salesteamArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.sales_teamNameList);
        salesteamArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sales_team.setAdapter(salesteamArrayAdapter);

        ArrayAdapter<String> mcpArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, AppSession.customerNameList);
        mcpArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        main_contact_person.setAdapter(mcpArrayAdapter);
        company_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_company_name.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_phone.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_email.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_address.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        main_contact_person.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_main_contact_person.setError("");
            }
        });
        sales_team.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                input_salesteam.setError("");
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i.createChooser(i, "select pic"), 101);
            }
        });
        new CompanyDetails().execute(Appconfig.TOKEN,company_id);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String encoded_image=getStringImage(bitmap);

                if (company_name.getText().toString().isEmpty()) {
                    input_company_name.setError("Please enter opportunity name");

                    return;
                }          else if (email.getText().toString().isEmpty()||!Util.isValidEmail(email.getText().toString())) {
                    if (email.getText().toString().isEmpty()) {
                        input_email.setError("Please enter email");
                        return;
                    }
                    if (!Util.isValidEmail(email.getText().toString()))
                    {
                        input_email.setError("Please enter valid  email");
                        return;
                    }
                } else if (phone.getText().toString().isEmpty()) {
                    input_phone.setError("Please enter nextAction date");
                    return;
                } else if (address.getText().toString().isEmpty()) {
                    input_address.setError("Please enter expectedClosing date");
                    return;
                } else if (main_contact_person.getText().toString().isEmpty()) {
                    input_main_contact_person.setError("Please select customer name");
                    return;
                } else if (sales_team.getText().toString().isEmpty()) {
                    input_salesteam.setError("Please select salesTeam name");
                    return;
                } else {
                    //do nothing
                }
                int salesTeamId = AppSession.salesTeamList.get(AppSession.sales_teamNameList.indexOf(sales_team.getText().toString())).getId();
                new EditCompany().execute(Appconfig.TOKEN, company_name.getText().toString(),
                        email.getText().toString(), address.getText().toString(), String.valueOf(salesTeamId),
                        main_contact_person.getText().toString(), phone.getText().toString(), company_id,encoded_image);
            }
        });
        return v;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
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

    private class EditCompany extends AsyncTask<String, Void, String> {
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
            String name = params[1];
            String email = params[2];
            String address = params[3];
            String sales_team_id = params[4];
            String main_contact_person = params[5];
            String phone = params[6];
            String companyId = params[7];
            String image=params[8];
            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("name", name);
                jsonObject.put("email", email);
                jsonObject.put("address", address);
                jsonObject.put("sales_team_id", sales_team_id);
                jsonObject.put("main_contact_person", main_contact_person);
                jsonObject.put("phone", phone);
                jsonObject.put("company_id", companyId);
               // jsonObject.put("avatar",image);

               // Log.i("jsonobject",jsonObject.toString());

                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String edit_url;
                if (text_url != null) {
                    edit_url= text_url + "/user/edit_company?token=";
                } else {
                    edit_url= Appconfig.COMPANY_EDIT_URL;
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
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonresponse = json.getString("error");
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
            dialog.dismiss();
            if (result.equals("success")) {

            final Snackbar snackbar = Snackbar.make(linearLayout, "Updated Succesfully!", Snackbar.LENGTH_LONG);
            View v = snackbar.getView();
            v.setMinimumWidth(1000);
            TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(Color.YELLOW);
            snackbar.show();
            Bundle bundle=new Bundle();
            bundle.putString("company_id", String.valueOf(company_id));
            Fragment fragment1 = new CompanyDetailsFragment();
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
    private class CompanyDetails extends AsyncTask<String,Void,String>
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
            String company_id=params[1];
            URL url;
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url= text_url + "/user/company?token=";
                } else {
                    detail_url= Appconfig.COMPANY_DETAILS_URL;
                }
                url = new URL(detail_url+tok+"&company_id="+company_id);
                conn = (HttpURLConnection) url.openConnection();
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

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
            JSONObject jsonObject;
            dialog.dismiss();
            try {
                jsonObject = new JSONObject(result);
                //Log.i("response",result);
                JSONArray jsonArray=jsonObject.getJSONArray("company");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject1=jsonArray.getJSONObject(i);
                    company_name.setText(jsonObject1.getString("name"));
                    email.setText(jsonObject1.getString("email"));
                    phone.setText(jsonObject1.getString("phone"));
                    sales_team.setText(jsonObject1.getString("sales_team"));
                    main_contact_person.setText(jsonObject1.getString("main_contact_person"));
                    address.setText(jsonObject1.getString("address"));
                    if(!jsonObject1.getString("avatar").equalsIgnoreCase("null")){
                        Picasso.with(getActivity())
                                .load(jsonObject1.getString("avatar"))
                                .placeholder(R.drawable.user)
                                .error(R.drawable.user)
                                .into(upload);                       }

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
