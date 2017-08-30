package com.project.lorvent.lcrm.fragments.admin.settingsfrag;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.utils.NetworkStatus;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class GeneralFragment extends Fragment {
    ArrayList<String> currencyList,max_uploadList;
    BetterSpinner currency,max_upload;
    RadioGroup email_radioGroup,time_radioGroup,date_radioGroup;
    RadioButton date1,date2,date3,date4,date5,time1,time2,time3,mail,smtp;
    EditText site_name,address,site_email,phone,fax,allowed_ext,min_char_allowed;
    EditText host,port,user,password;
    ImageView upload;
    Bitmap bitmap;
    String token;
    String encoded_image;
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
        date1=(RadioButton)v.findViewById(R.id.date1);
        date2=(RadioButton)v.findViewById(R.id.date2);
        date3=(RadioButton)v.findViewById(R.id.date3);
        date4=(RadioButton)v.findViewById(R.id.date4);
        date5=(RadioButton)v.findViewById(R.id.date5);
        time1=(RadioButton)v.findViewById(R.id.time1);
        time2=(RadioButton)v.findViewById(R.id.time2);
        time3=(RadioButton)v.findViewById(R.id.time3);
        mail=(RadioButton)v.findViewById(R.id.mail);
        mail.setChecked(true);
        smtp=(RadioButton)v.findViewById(R.id.smtp);

        if (!NetworkStatus.isConnected(getActivity())){
            new MaterialDialog.Builder(getActivity())
                    .title("Please check your internet Connectivity !")
                    .titleColor(Color.RED)
                    .positiveText("Ok")
                    .positiveColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .negativeColorRes(R.color.colorPrimary)
                    .negativeText("")
                    .show();

        }

        if (NetworkStatus.isConnected(getActivity())){
            getSettings(token);
        }

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
          if(date_radioGroup.isSelected())
          {         date = ((RadioButton)v.findViewById(date_radioGroup.getCheckedRadioButtonId() )).getText().toString();
          }
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {

        }
        else
        {
               date4.setVisibility(View.GONE);
            date5.setVisibility(View.GONE);

        }
        date_radioGroup.clearCheck();
        time_radioGroup=(RadioGroup)v.findViewById(R.id.time);
        if (time_radioGroup.isSelected()){
            time = ((RadioButton)v.findViewById(time_radioGroup.getCheckedRadioButtonId() )).getText().toString();

        }

        time_radioGroup.clearCheck();
        int SelectedId=email_radioGroup.getCheckedRadioButtonId();
        int date_selectedId=date_radioGroup.getCheckedRadioButtonId();
        int time_selectedId=time_radioGroup.getCheckedRadioButtonId();
        upload=(ImageView)v.findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getActivity(),"Sorry! for some reason this function is disabled in app",Toast.LENGTH_LONG).show();

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
{
    encoded_image=getStringImage(bitmap);
    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
        SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url= text_url + "/user/settings?token=";
        } else {
            get_url= Appconfig.SETTINGS_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
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
                            String date_format=pre_settings.getString("date_format");
                            String time_format=pre_settings.getString("time_format");
                            site_email.setText(pre_settings.getString("site_email"));
                            address.setText(pre_settings.getString("address1"));
                            phone.setText(pre_settings.getString("phone"));
                            allowed_ext.setText(pre_settings.getString("allowed_extensions"));
                            min_char_allowed.setText(pre_settings.getString("minimum_characters"));
                            fax.setText(pre_settings.getString("fax"));
                            if (date_format.equals("F j,Y")) {
                                date1.setChecked(true);

                            }
                            if (date_format.equals("Y-d-m")) {
                               date2.setChecked(true);
                            }
                            if (date_format.equals("d.m.Y.")) {
                                date3.setChecked(true);
                            }
                            if (date_format.equals("d.m.Y.")) {
                                date4.setChecked(true);
                            }
                            if (date_format.equals("d.m.Y")) {
                                date5.setChecked(true);
                            }
                            if (time_format.equals("g:i a"))
                            {
                                time3.setChecked(true);
                            }
                            if (time_format.equals("g:i A"))
                            {
                                time2.setChecked(true);
                            }
                            if (time_format.equals("H:i"))
                            {
                                time1.setChecked(true);
                            }
                           // Log.i("pdf_logo",jsonObject.getString("logo"));
                          /*  Picasso.with(getActivity())
                                    .load(jsonObject.getString("logo"))
                                    .placeholder(R.drawable.user)
                                    .error(R.drawable.user)
                                    .into(upload);*/
                                 getSiteLogo(jsonObject.getString("logo"));
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
                Map<String, String> params = new HashMap<>();

                return params;
            }

        } ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);

    }

    private void getSiteLogo(String site_logo_url) {
        ImageRequest ir = new ImageRequest(site_logo_url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                bitmap=response;
                upload.setImageBitmap(response);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                upload.setImageResource(R.drawable.upload);

            }
        });
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(ir);

    }

    private void updateSetting(String token) {

       final ProgressDialog  progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Updating, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(true);
        SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String update_url;
        if (text_url != null) {
            update_url= text_url + "/user/update_settings?token=";
        } else {
            update_url= Appconfig.UPDATE_SETTINGS_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST,update_url+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // Log.i("response--", String.valueOf(response));
                        getSettings(Appconfig.TOKEN);
                        final Snackbar snackbar=Snackbar.make(coordinator,"Successfully updated ",Snackbar.LENGTH_LONG);
                        View v=snackbar.getView();
                        v.setMinimumWidth(1000);
                        TextView tv=(TextView)v.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextColor(Color.YELLOW);
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
                Map<String, String> params = new HashMap<>();
                String date_format="";
                String time_format="";
                params.put("site_name",site_name.getText().toString());
                params.put("site_email",site_email.getText().toString());
                params.put("logo",encoded_image);
                params.put("address1",address.getText().toString());
                params.put("phone",phone.getText().toString());
                params.put("fax",fax.getText().toString());
                if (date1.isChecked()) {
                    date_format="F j,Y";
                }
                if (date2.isChecked()) {

                    date_format="Y-d-m";
                }
                if (date3.isChecked()) {
                    date_format="d.m.Y.";
                }
                if (date4.isChecked()) {
                    date_format="d.m.Y.";
                }
                if (date5.isChecked()) {
                    date_format="d.m.Y";
                }
                if ( time3.isChecked())
                {
                    time_format=("g:i a");
                }
                if (time2.isChecked())
                {
                    time_format=("g:i A");
                }
                if (time1.isChecked())
                {
                    time_format=("H:i");
                }
                // Log.i("time format--",time);
                params.put("date_format",date_format);
                params.put("time_format",time_format);
                params.put("email_driver",mail_method);
                params.put("allowed_extensions",allowed_ext.getText().toString());
                params.put("minimum_characters",min_char_allowed.getText().toString());
                params.put("currency",currency.getText().toString());
              //  Log.i("currecy item--",currency.getText().toString());
                params.put("max_upload_file_size",max_upload.getText().toString());
                params.put("email_host",host.getText().toString());
                params.put("email_port",port.getText().toString());
                params.put("email_username",user.getText().toString());
                params.put("email_password",password.getText().toString());
                Log.i("params",params.toString());
                return params;
            }
        } ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}
