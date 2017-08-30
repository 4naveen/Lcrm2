package com.project.lorvent.lcrm.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.project.lorvent.lcrm.R;

public class AboutUsActivity extends AppCompatActivity {
    TextView textViewTermsAndConditions,textViewNeedHelp,textViewProfile;
    String termsAndConditions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("About Us");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        textViewTermsAndConditions=(TextView)findViewById(R.id.tv_terms_and_conditions);
        textViewProfile=(TextView)findViewById(R.id.view_profile);
        textViewProfile.setPaintFlags(textViewProfile.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        termsAndConditions="The Intellectual Property disclosure will inform users that your website, logo and visuals and other content you created is your property and protected by copyright laws.\n" +
                "\n" +
                "A Termination clause will inform users that their accounts on your website (or their access to your website, if they don’t have an account) can be terminated in case of abuses.\n" +
                "\n" +
                "A Governing Law informs users which country’s laws governs the agreement. This is the country in which your company is headquartered or the country from which you operate your web site.\n" +
                "\n" +
                "A Links To Other Web Sites clause informs users that you are not responsible for any third party web sites that you link to from your website, and that users are responsible for reading these third parties’ own Terms and Conditions or Privacy Policies.\n"+
                "\n" +
                "Lcrm app is complete functional sales and crm system.Every buisness needs new customers and when it comes to sales growth ,many people focus on gaining new clients and customers rather than handling existing customers who knew about your company .\n" +
                "\n" +
                "This app concentrates on the needs of existing customers and attracts new customers with its impressive feature.";
        textViewTermsAndConditions.setText(termsAndConditions);

        textViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://codecanyon.net/user/jyostna"));
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();

        }
        return super.onOptionsItemSelected(item);

    }
}
