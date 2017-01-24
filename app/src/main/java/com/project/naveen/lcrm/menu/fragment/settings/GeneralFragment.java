package com.project.naveen.lcrm.menu.fragment.settings;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneralFragment extends Fragment {
    ArrayList<String> currencyList,max_uploadList;
    BetterSpinner currency,max_upload;
    RadioGroup email_radioGroup,time_radioGroup,date_radioGroup;
    EditText site_name,address,site_email,phone,fax,allowed_ext,min_char_allowed;
    EditText host,port,user,password;
    LinearLayout layout;
    ImageView upload;
    Bitmap bitmap;
    String token;
    CoordinatorLayout coordinator;
     String time,date,mail_method;
    public GeneralFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_general, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("General Settings");
        }
        setHasOptionsMenu(true);
        currencyList=new ArrayList<>();
        max_uploadList=new ArrayList<>();
        token=Appconfig.TOKEN;

        coordinator=(CoordinatorLayout)v.findViewById(R.id.coordinator);
        currency=(BetterSpinner)v.findViewById(R.id.currency);
        max_upload=(BetterSpinner)v.findViewById(R.id.max_upload_amt);
        site_name=(EditText)v.findViewById(R.id.site_name);
        address=(EditText)v.findViewById(R.id.address);
        site_email=(EditText)v.findViewById(R.id.site_email);
        phone=(EditText)v.findViewById(R.id.phone);
        min_char_allowed=(EditText)v.findViewById(R.id.min_character);
        allowed_ext=(EditText)v.findViewById(R.id.alloed_extension);

        fax=(EditText)v.findViewById(R.id.fax);
        host=(EditText)v.findViewById(R.id.host);
        port=(EditText)v.findViewById(R.id.port);
        user=(EditText)v.findViewById(R.id.user);
        password=(EditText)v.findViewById(R.id.password);

        getSettings(token);
        ArrayAdapter<String> currencyArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,currencyList);
        currencyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        currency.setAdapter(currencyArrayAdapter);
        ArrayAdapter<String> max_uploadArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,max_uploadList);
        max_uploadArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        max_upload.setAdapter(max_uploadArrayAdapter);
        email_radioGroup=(RadioGroup)v.findViewById(R.id.email_driver);
         mail_method = ((RadioButton)v.findViewById(email_radioGroup.getCheckedRadioButtonId() )).getText().toString();

        email_radioGroup.clearCheck();
        date_radioGroup=(RadioGroup)v.findViewById(R.id.date);
         date = ((RadioButton)v.findViewById(date_radioGroup.getCheckedRadioButtonId() )).getText().toString();

        date_radioGroup.clearCheck();
        time_radioGroup=(RadioGroup)v.findViewById(R.id.time);
        time = ((RadioButton)v.findViewById(time_radioGroup.getCheckedRadioButtonId() )).getText().toString();

        time_radioGroup.clearCheck();
        int SelectedId=email_radioGroup.getCheckedRadioButtonId();
        int date_selectedId=date_radioGroup.getCheckedRadioButtonId();
        int time_selectedId=time_radioGroup.getCheckedRadioButtonId();
        upload=(ImageView)v.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i.createChooser(i,"select pic"),101);
            }
        });

        email_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId==R.id.smtp)
        {
            host.setVisibility(View.VISIBLE);
            port.setVisibility(View.VISIBLE);
            user.setVisibility(View.VISIBLE);
            password.setVisibility(View.VISIBLE);
        }
        if (checkedId==R.id.mail)
        {
            host.setVisibility(View.INVISIBLE);
            port.setVisibility(View.INVISIBLE);
            user.setVisibility(View.INVISIBLE);
            password.setVisibility(View.INVISIBLE);
        }
    }
});
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit_ok,menu);
//       super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
   if (item.getItemId()==R.id.edit)
{ InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    updateSetting(token);

}
        return super.onOptionsItemSelected(item);
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

    private void getSettings(String token) {
       final ProgressDialog  progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.SETTINGS_URL+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                           // Log.i("response--", String.valueOf(response));

                            JSONObject jsonObject=new JSONObject(response);
                              JSONObject max_upload_file_size=jsonObject.getJSONObject("max_upload_file_size");
                            JSONObject currency=jsonObject.getJSONObject("currency");
                          JSONObject pre_settings=jsonObject.getJSONObject("settings");
                            max_uploadList.add(max_upload_file_size.getString("1000"));
                            max_uploadList.add(max_upload_file_size.getString("2000"));
                            max_uploadList.add(max_upload_file_size.getString("3000"));
                            max_uploadList.add(max_upload_file_size.getString("4000"));
                            max_uploadList.add(max_upload_file_size.getString("5000"));
                            max_uploadList.add(max_upload_file_size.getString("6000"));
                            max_uploadList.add(max_upload_file_size.getString("7000"));
                            max_uploadList.add(max_upload_file_size.getString("8000"));
                            max_uploadList.add(max_upload_file_size.getString("9000"));
                            max_uploadList.add(max_upload_file_size.getString("10000"));


                             currencyList.add(currency.getString("USD"));
                            currencyList.add(currency.getString("EUR"));

                             site_name.setText(pre_settings.getString("site_name"));
                            site_email.setText(pre_settings.getString("site_email"));
                                  //getSiteLogo(pre_settings.getString("site_logo"));
                          /*  Log.i("currency--",currencyList.toString());
                            Log.i("max file----",max_uploadList.toString());*/
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


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

                return params;
            }

        } ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);


    }

    /*private void getSiteLogo(String site_logo_url) {
        ImageRequest ir = new ImageRequest(site_logo_url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                upload.setImageBitmap(response);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                upload.setImageResource(R.drawable.upload);

            }
        });
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(ir);

    }*/

    private void updateSetting(String token) {

       final ProgressDialog  progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Appconfig.UPDATE_SETTINGS_URL+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("response--", String.valueOf(response));

                        final Snackbar snackbar=Snackbar.make(coordinator, " Successfully updated ",Snackbar.LENGTH_LONG);
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
                params.put("site_name",site_name.getText().toString());
                params.put("site_email",site_email.getText().toString());
                params.put("address1",address.getText().toString());
                params.put("phone",phone.getText().toString());
                params.put("fax",fax.getText().toString());
                params.put("time_format",time);
                Log.i("time format--",time);
                params.put("date_format",date);
                Log.i("date format--",date);
                params.put("email_driver",mail_method);
                params.put("allowed_extensions",allowed_ext.getText().toString());
                params.put("minimum_characters",min_char_allowed.getText().toString());
                params.put("currency",currency.getText().toString());
                Log.i("currecy item--",currency.getText().toString());
                params.put("max_upload_file_size",max_upload.getText().toString());
                params.put("email_host",host.getText().toString());
                params.put("email_port",port.getText().toString());
                params.put("email_username",user.getText().toString());
                params.put("email_password",password.getText().toString());

                return params;
            }
        } ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);


       /* ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("image", encodedImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://beta.lcrm.in/uploads/site/logo.png", jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("Message from server", jsonObject.toString());
//                        Toast.makeText(getApplication(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
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
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(jsonObjectRequest);
*/

    }
}
