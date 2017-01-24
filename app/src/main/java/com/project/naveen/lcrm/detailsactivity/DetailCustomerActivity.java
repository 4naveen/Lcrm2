package com.project.naveen.lcrm.detailsactivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.joaquimley.faboptions.FabOptions;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.customers.DetailsFragment;
import com.project.naveen.lcrm.customers.EditFragment;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailCustomerActivity extends AppCompatActivity {
    FabOptions fabOptions;
    int quotation_id_position;
    String contact_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_customer);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Customer Details");
        }
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }



        FragmentTransaction trans1=getSupportFragmentManager().beginTransaction();
        Fragment fragment1=new DetailsFragment();
//        fragment1.setArguments(bundle);
        trans1.replace(R.id.frame,fragment1);
        trans1.addToBackStack(null);
        trans1.commit();
        fabOptions = (FabOptions) findViewById(R.id.fab_options);
        fabOptions.setButtonsMenu(this, R.menu.quotation_menu);

        fabOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                Fragment fragment=null;
                switch (v.getId()) {
                    case R.id.edit:
                    {
                        fragment=new EditFragment();

                        transaction.replace(R.id.frame,fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        break;
                    }


                    case R.id.delete:
                    {
                        new SweetAlertDialog(DetailCustomerActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Are you sure?")
                                .setContentText("Won't be able to recover this file!")
                                .setCancelText("No,cancel plx!")
                                .setConfirmText("Yes,delete it!")
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
                                        // new DeleteQuotationTask().execute(Appconfig.TOKEN,quotation_id);
                                        Log.i("quo_id-pos", String.valueOf(quotation_id_position));

                                        sweetAlertDialog.cancel();
                                    }
                                })
                                .show();

                        break;
                    }
                    default:
                        // no-op


                }
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
