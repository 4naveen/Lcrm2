package com.project.lorvent.lcrm.activities;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.project.lorvent.lcrm.R;

public class NetworkErrorActivity extends AppCompatActivity {
    Button cancel,retry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_error);
        cancel=(Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
       /* retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()){
                    //do nothing
                }
                if (isConnected()){
                    Intent i = new Intent(NetworkErrorActivity.this, DetailsActivity.class);

                    // i.putExtra("movie_id",movie_id);
                    startActivity(i);}

            }
        });*/
    }
    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
