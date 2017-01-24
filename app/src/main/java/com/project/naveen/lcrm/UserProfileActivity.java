package com.project.naveen.lcrm;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    EditText firsName,last_name,user_email,phone,role;
//    TextView edit,save;
    Holder holder;
    Bitmap bitmap;
    int check;
    CircleImageView profile_image;
    OnClickListener clickListener;
    OnItemClickListener itemClickListener;
    OnDismissListener dismissListener;
    OnCancelListener cancelListener;
    Configuration config;

    ToggleButton toggleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            setContentView(R.layout.activity_user_profile);
        }
        else
        {
            setContentView(R.layout.activity_user_profile_mob);
        }
//        setContentView(R.layout.activity_user_profile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Profile");
        holder = new ViewHolder(R.layout.content);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toggleButton=(ToggleButton)findViewById(R.id.togglebutton);
        profile_image=(CircleImageView)findViewById(R.id.profile_image);
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

         clickListener = new OnClickListener() {
            @Override
            public void onClick(DialogPlus dialog, View view) {
               // dialog.dismiss();
                if (view.getId()==R.id.footer_exit_button)
                {
                    finish();
                    dialog.dismiss();
                }
                if (view.getId()==R.id.footer_close_button)
                {

                    dialog.dismiss();
                }
            }
        };
        itemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
               // TextView textView = (TextView) view.findViewById(R.id.text_view);

               // String clickedAppName = textView.getText().toString();
                //        dialog.dismiss();
                //        Toast.makeText(MainActivity.this, clickedAppName + " clicked", Toast.LENGTH_LONG).show();
            }
        };

         dismissListener = new OnDismissListener() {
            @Override
            public void onDismiss(DialogPlus dialog) {
                //        Toast.makeText(MainActivity.this, "dismiss listener invoked!", Toast.LENGTH_SHORT).show();

                WindowManager.LayoutParams layout = getWindow().getAttributes();
                layout.screenBrightness = 2F;
                getWindow().setAttributes(layout);
            }
        };

         cancelListener = new OnCancelListener() {
            @Override
            public void onCancel(DialogPlus dialog) {
                //        Toast.makeText(MainActivity.this, "cancel listener invoked!", Toast.LENGTH_SHORT).show();
            }
        };


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
  /* edit.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {

          *//* KeyListener variable;
           variable =firsName .getKeyListener();
           firsName.setKeyListener(variable);*//*
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

           role.setClickable(true);
           role.setFocusable(true);
           role.setFocusableInTouchMode(true);
           role.setBackgroundResource(R.drawable.edittext_bottom_back);


       }
   });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover this file!")
                        .setCancelText("No,cancel plx!")
                        .setConfirmText("Yes,Save it!")
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

                                sweetAlertDialog.cancel();
                            }
                        })

                        .show();

            }
        });*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==101&&resultCode== Activity.RESULT_OK)
        {
            Uri uri=data.getData();

            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("image--", String.valueOf(bitmap));

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
                    final DialogPlus dialog = DialogPlus.newDialog(this)
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
                    dialog.show();

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

            role.setClickable(true);
            role.setFocusable(true);
            role.setFocusableInTouchMode(true);
            role.setBackgroundResource(R.drawable.edittext_bottom_back);

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
                            firsName.setText(firsName.getText());
                            last_name.setText(last_name.getText());
                            user_email.setText(user_email.getText());
                            phone.setText(phone.getText());
                            role.setText(role.getText());
                            sweetAlertDialog.cancel();
                        }
                    })

                    .show();
        }
    }
}
