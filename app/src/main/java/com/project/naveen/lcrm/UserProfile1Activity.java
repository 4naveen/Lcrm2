package com.project.naveen.lcrm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.Holder;
import com.orhanobut.dialogplus.OnCancelListener;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnDismissListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

public class UserProfile1Activity extends AppCompatActivity {
    TextView first_name,last_name,user_email,phone,role;
    Holder holder;
    OnClickListener clickListener;
    OnItemClickListener itemClickListener;
    OnDismissListener dismissListener;
    OnCancelListener cancelListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile1);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Profile");
        holder = new ViewHolder(R.layout.content);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        first_name=(TextView)findViewById(R.id.first_name);
        last_name=(TextView)findViewById(R.id.last_name);
        user_email=(TextView)findViewById(R.id.email);
        phone=(TextView)findViewById(R.id.phone);
        role=(TextView)findViewById(R.id.role);
        first_name.setText(AppSession.first_name);
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
                final DialogPlus dialog = DialogPlus.newDialog(this)
                        .setContentHolder(holder)
                        .setHeader(R.layout.header)
                        .setFooter(R.layout.footer)
                        .setCancelable(true)
                        .setInAnimation(R.anim.translate_left)
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
                layout.screenBrightness = 0.2F;
                getWindow().setAttributes(layout);
                dialog.show();

            }
        }
        return super.onOptionsItemSelected(item);
    }
}
