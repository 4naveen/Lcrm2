package com.project.lorvent.lcrm.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.project.lorvent.lcrm.activities.LoginActivity;

public class LogoutService extends Service {
    Snackbar snackbar;
    public LogoutService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(LogoutService.this, LoginActivity.class);
                startActivity(i);
            }
        }, 300000);*/
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(LogoutService.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                startActivity(i);

                Toast.makeText(getApplicationContext(),"Session timeout!Please Login",Toast.LENGTH_LONG).show();
            }
        },1680000);
    }


}
