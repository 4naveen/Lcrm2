package com.project.lorvent.lcrm.activities.user_profile;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.home.HomeActivity;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Convertor;
import com.project.lorvent.lcrm.utils.LogoutService;

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

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    EditText firsName,last_name,user_email,phone,role;
    Bitmap bitmap;
    int check;
    CircleImageView profile_image;

    Configuration config;
    LinearLayout linearLayout;
    ToggleButton toggleButton;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    byte[] images_bytes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        showHelpForFirstLaunch();
        linearLayout=(LinearLayout)findViewById(R.id.layout);
        images_bytes=getIntent().getByteArrayExtra("image_bytes");
        config = getResources().getConfiguration();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Profile");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toggleButton=(ToggleButton)findViewById(R.id.togglebutton);
        profile_image=(CircleImageView)findViewById(R.id.profile_image);
        bitmap=Convertor.getImage(images_bytes);
        profile_image.setImageBitmap(bitmap);
       // getImage(AppSession.image_url);

        firsName=(EditText)findViewById(R.id.first_name);
        last_name=(EditText)findViewById(R.id.last_name);
        user_email=(EditText)findViewById(R.id.email);
        phone=(EditText)findViewById(R.id.phone);
        role=(EditText)findViewById(R.id.role);

      /*  edit=(TextView) findViewById(R.id.edit);
        save=(TextView) findViewById(R.id.save);*/
        firsName.setText(AppSession.first_name);
        last_name.setText(AppSession.last_name);
        user_email.setText(AppSession.user_email);
        phone.setText(AppSession.phone);
        role.setText(AppSession.role);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this file!")
                        .setCancelText("No,cancel plx!")
                        .setConfirmText("Yes,Change it!")
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
                                Intent i=new Intent();
                                i.setType("image/*");
                                i.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(i.createChooser(i,"select pic"),101);
                                sweetAlertDialog.cancel();
                            }
                        })
                        .show();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101&&resultCode== Activity.RESULT_OK)
        {
            Uri uri=data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
               // Log.d("image--", String.valueOf(bitmap));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                long lengthbmp = imageInByte.length;
                //Log.i("image length", String.valueOf(lengthbmp));
                if (lengthbmp>512000)
                {
                    Toast.makeText(getApplicationContext(),"image size should be less than 500kb",Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    profile_image.setImageBitmap(bitmap);
                }
                profile_image.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_logout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
        {
            Intent i=new Intent(UserProfileActivity.this,HomeActivity.class);
            startActivity(i);
            firsName.clearFocus();
            finish();
            break;
        }
            case R.id.logout:
            {
                if (config.smallestScreenWidthDp>=600)
                {
            /*        final DialogPlus dialog = DialogPlus.newDialog(this)
                            .setContentHolder(holder)
                            .setHeader(R.layout.header)
                            .setFooter(R.layout.footer)
                            .setCancelable(false)
                            .setGravity(Gravity.CENTER)
                            .setOnClickListener(clickListener)
                            .setOnItemClickListener(itemClickListener)
                            .setOnDismissListener(dismissListener)

//        .setContentWidth(800)

                            .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                            .setContentWidth(600)
                            .setOnCancelListener(cancelListener)

                            .setOverlayBackgroundResource(android.R.color.transparent)
                            .setContentBackgroundResource(R.color.dialogplus_card_shadow)
                            //                .setOutMostMargin(0, 100, 0, 0)
                            .create();
                    WindowManager.LayoutParams layout = getWindow().getAttributes();
                    layout.screenBrightness = 0.1F;
                    getWindow().setAttributes(layout);
                    dialog.show();*/
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("you want to exit!")
                            .setCancelText("cancel")
                            .setConfirmText("Yes")
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
                                    Intent i = new Intent(UserProfileActivity.this, LogoutService.class);
                                    stopService(i);
                                    sweetAlertDialog.cancel();
                                    finish();
                                }
                            })
                            .show();
                }
                else
                {
                    new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("you want to exit!")
                            .setCancelText("cancel")
                            .setConfirmText("Yes")
                            
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
                                   finish();
                                    // new DeleteLeadTask().execute(token,String.valueOf(lead_id));
                                    sweetAlertDialog.cancel();
                                }
                            })
                            .show();
                }
            }


        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onToggleClicked(View view) {
        if(((ToggleButton) view).isChecked()) {
            // handle toggle on

         toggleButton.setBackground(getResources().getDrawable(R.mipmap.ic_save_black_36dp));
            firsName.setClickable(true);
            firsName.setFocusable(true);
            firsName.setFocusableInTouchMode(true);
            firsName.setBackgroundResource(R.drawable.edittext_bottom_back);
            firsName.requestFocus();
            firsName.setSelection(firsName.getText().length());
            last_name.setClickable(true);
            last_name.setFocusable(true);
            last_name.setFocusableInTouchMode(true);
            last_name.setBackgroundResource(R.drawable.edittext_bottom_back);

            user_email.setClickable(true);
            user_email.setFocusable(true);
            user_email.setFocusableInTouchMode(true);
            user_email.setBackgroundResource(R.drawable.edittext_bottom_back);

            phone.setClickable(true);
            phone.setFocusable(true);
            phone.setFocusableInTouchMode(true);
            phone.setBackgroundResource(R.drawable.edittext_bottom_back);

        /*    role.setClickable(true);
            role.setFocusable(true);
            role.setFocusableInTouchMode(true);
            role.setBackgroundResource(R.drawable.edittext_bottom_back);*/

        } else {
            // handle toggle off
            new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Are you sure?")
                    .setContentText("Won't be able to recover this file!")
                    .setCancelText("No,cancel plx!")
                    .setConfirmText("Yes,Save it!")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            firsName.setText(AppSession.first_name);
                            last_name.setText(AppSession.last_name);
                            user_email.setText(AppSession.user_email);
                            phone.setText(AppSession.phone);
                            role.setText(AppSession.role);
                            toggleButton.setBackground(getResources().getDrawable(R.mipmap.ic_create_black_36dp));
                            firsName.setClickable(false);
                            firsName.setFocusable(false);
                            firsName.setFocusableInTouchMode(false);
                            firsName.setBackground(getResources().getDrawable(R.color.textColorPrimary));
                            last_name.setClickable(false);
                            last_name.setFocusable(false);
                            last_name.setFocusableInTouchMode(false);
                            last_name.setBackground(getResources().getDrawable(R.color.textColorPrimary));
                            user_email.setClickable(false);
                            user_email.setFocusable(false);
                            user_email.setFocusableInTouchMode(false);
                            user_email.setBackground(getResources().getDrawable(R.color.textColorPrimary));
                            phone.setClickable(false);
                            phone.setFocusable(false);
                            phone.setFocusableInTouchMode(false);
                            phone.setBackground(getResources().getDrawable(R.color.textColorPrimary));
                            role.setClickable(false);
                            role.setFocusable(false);
                            role.setFocusableInTouchMode(false);
                            role.setBackground(getResources().getDrawable(R.color.textColorPrimary));
                            sDialog.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            String encoded_image=getStringImage(bitmap);
                            firsName.setText(firsName.getText());
                            last_name.setText(last_name.getText());
                            user_email.setText(user_email.getText());
                            phone.setText(phone.getText());
                            role.setText(role.getText());
                            sweetAlertDialog.cancel();
                            new UpdateUser().execute(Appconfig.TOKEN,firsName.getText().toString(),last_name.getText().toString(),user_email.getText().toString(),phone.getText().toString(),encoded_image);

                        }
                    })
                    .show();
        }
    }
    private void showHelpForFirstLaunch() {
        if (helpDisplayed)
            return;
        helpDisplayed = getPreferenceValue(PREF_FIRSTLAUNCH_HELP, false);
        if (!helpDisplayed) {
            savePreference(PREF_FIRSTLAUNCH_HELP, true);
            showOverLay();
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Log.i("encodedImageString",encodedImage);
        return encodedImage;
    }

    private boolean getPreferenceValue(String key, boolean defaultValue) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }

    private void savePreference(String key, boolean value) {
        SharedPreferences preferences =getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    private void showOverLay(){

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);

        dialog.setContentView(R.layout.overlay_view);
        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overlayLayout);
        ImageView image=(ImageView)dialog.findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.overlay3);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View arg0) {

                dialog.dismiss();

            }

        });

        dialog.show();

    }
    class UpdateUser extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(UserProfileActivity.this);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String first_name=params[1];
            String last_name=params[2];
            String email=params[3];
            String phone=params[4];
           String image=params[5];

            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("first_name",first_name);
                jsonObject.put("last_name",last_name);
                jsonObject.put("email","admin@admin.com");
                jsonObject.put("phone_number",phone);
                //jsonObject.put("password","lorvent");
               // jsonObject.put("password_confirmation","lorvent");
                jsonObject.put("avatar",image);


                url = new URL(Appconfig.EDIT_PROFILE_URL+tok);
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
//
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
                    }
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
            {  final Snackbar snackbar = Snackbar.make(linearLayout, "Updated Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();

            }
            else {
                final Snackbar snackbar = Snackbar.make(linearLayout, "Item not updated! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
        }
    }

}}
