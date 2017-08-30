package com.project.lorvent.lcrm.activities.user_profile;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.home.Home1Activity;
import com.project.lorvent.lcrm.utils.Convertor;
import com.project.lorvent.lcrm.utils.LogoutService;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile1Activity extends AppCompatActivity {
    TextView firsName,last_name,user_email,phone,role;
    CircleImageView profile_image;

    Configuration config;
    ToggleButton toggleButton;
    LinearLayout linearLayout;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    byte[] images_bytes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        showHelpForFirstLaunch();
        config = getResources().getConfiguration();
        linearLayout=(LinearLayout)findViewById(R.id.layout);
        images_bytes=getIntent().getByteArrayExtra("image_bytes");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Profile");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toggleButton=(ToggleButton)findViewById(R.id.togglebutton);
        toggleButton.setVisibility(View.INVISIBLE);// till api is not generated

        profile_image=(CircleImageView)findViewById(R.id.profile_image);
        profile_image.setImageBitmap(Convertor.getImage(images_bytes));
        firsName=(TextView)findViewById(R.id.first_name);
        last_name=(TextView)findViewById(R.id.last_name);
        user_email=(TextView)findViewById(R.id.email);
        phone=(TextView)findViewById(R.id.phone);
        role=(TextView)findViewById(R.id.role);
        firsName.setText(AppSession.first_name);
        last_name.setText(AppSession.last_name);
        user_email.setText(AppSession.user_email);
        phone.setText(AppSession.phone);
        role.setText(AppSession.role);



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
                Intent i=new Intent(UserProfile1Activity.this,Home1Activity.class);
                startActivity(i);
                finish();
                break;
            }
            case R.id.logout:
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

                                    Intent i = new Intent(UserProfile1Activity.this, LogoutService.class);
                                    stopService(i);
                                    sweetAlertDialog.cancel();
                                    finish();
                                }
                            })
                            .show();

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
            //firsName.setSelection(firsName.getText().length());
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
/*
            role.setClickable(true);
            role.setFocusable(true);
            role.setFocusableInTouchMode(true);
            role.setBackgroundResource(R.drawable.edittext_bottom_back);*/

        } else {
            // handle toggle off
            new SweetAlertDialog(UserProfile1Activity.this, SweetAlertDialog.WARNING_TYPE)
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
                            /*role.setClickable(false);
                            role.setFocusable(false);
                            role.setFocusableInTouchMode(false);
                            role.setBackground(getResources().getDrawable(R.color.textColorPrimary));*/
                            sDialog.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            firsName.setText(firsName.getText());
                            last_name.setText(last_name.getText());
                            user_email.setText(user_email.getText());
                            phone.setText(phone.getText());
                            role.setText(role.getText());
                            sweetAlertDialog.cancel();

                     /*       if (true)
                            {
                                final Snackbar snackbar = Snackbar.make(linearLayout, "Updated Succesfully!", Snackbar.LENGTH_LONG);
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
                            }*/
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
}
