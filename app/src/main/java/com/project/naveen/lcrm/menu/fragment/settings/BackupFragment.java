package com.project.naveen.lcrm.menu.fragment.settings;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class BackupFragment extends Fragment {
    CoordinatorLayout coordinator;
    Spinner betterSpinnerBackup;
    ArrayList <String>backupList;
    EditText editTextDropBoxKey,editTextDropBoxSecret,editTextDropBoxToken,editDropBoxTextApp;
    EditText editTextAwsKey,editTextAwsSecret,editTextAwsBucket,editTextAwsRegion;
    TextView textViewDropbox,textViewAws;
    LinearLayout layout,layoutDropBox,layoutAmazon;

    public BackupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View v=inflater.inflate(R.layout.fragment_backup, container, false);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        coordinator=(CoordinatorLayout)v.findViewById(R.id.coordinator);
        betterSpinnerBackup=(Spinner) v.findViewById(R.id.backup);
        layout=(LinearLayout)v.findViewById(R.id.layout);
        layoutDropBox=(LinearLayout)v.findViewById(R.id.layout_dropbox);
        layoutAmazon=(LinearLayout)v.findViewById(R.id.layout_amazon);
        editTextDropBoxKey=(EditText)v.findViewById(R.id.key);
        editTextDropBoxSecret=(EditText)v.findViewById(R.id.secret);
        editTextDropBoxToken=(EditText)v.findViewById(R.id.key);
        editDropBoxTextApp=(EditText)v.findViewById(R.id.app);

        editTextAwsKey=(EditText)v.findViewById(R.id.aws_key);
        editTextAwsSecret=(EditText)v.findViewById(R.id.aws_secret);
        editTextAwsBucket=(EditText)v.findViewById(R.id.aws_bucket);
        editTextAwsRegion=(EditText)v.findViewById(R.id.aws_region);

        textViewDropbox=new TextView(getActivity());
        textViewAws=new TextView(getActivity());

        backupList=new ArrayList();
        backupList.add("Local");
        backupList.add("DropBox");
        backupList.add("Amazon S3");
        ArrayAdapter<String> currencyArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,backupList);
        currencyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        betterSpinnerBackup.setAdapter(currencyArrayAdapter);
        if (actionBar != null) {
            actionBar.setTitle("Back up configuration");
        }
        setHasOptionsMenu(true);
//        Log.i("spinner--",betterSpinnerBackup.getText().toString());


 betterSpinnerBackup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
     @Override
     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
         Log.i("pos--", String.valueOf(parent.getItemAtPosition(position)));

         switch (position)
         {
             case 0:
             {  layoutDropBox.setVisibility(View.GONE);
                 layoutAmazon.setVisibility(View.GONE);
                 break;
             }
             case 1:

             {
                 layoutDropBox.setVisibility(View.VISIBLE);
                 layoutAmazon.setVisibility(View.GONE);

break;
             }
             case 2:

             {layoutAmazon.setVisibility(View.VISIBLE);
                 layoutDropBox.setVisibility(View.GONE);


            break;
             }
         }
     }

     @Override
     public void onNothingSelected(AdapterView<?> parent) {

     }
 });
 /*       betterSpinnerBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("spinner--",betterSpinnerBackup.getText().toString());
            }
        });*/

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

            updateSetting(Appconfig.TOKEN);

        }        return super.onOptionsItemSelected(item);

    }
   private void updateSetting(String token) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Appconfig.UPDATE_SETTINGS_URL+token,
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
                 params.put("disk_dbox_key",editTextDropBoxKey.getText().toString());
                params.put("disk_dbox_secret",editTextDropBoxSecret.getText().toString());
                params.put("disk_dbox_token",editTextDropBoxToken.getText().toString());
                params.put("disk_dbox_app",editDropBoxTextApp.getText().toString());

                params.put("disk_aws_secret",editTextAwsSecret.getText().toString());
                params.put("disk_aws_bucket",editTextAwsBucket.getText().toString());
                params.put("disk_aws_region",editTextAwsRegion.getText().toString());
                params.put("disk_aws_key",editTextAwsKey.getText().toString());


                return params;
            }
        } ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);



    }

}

