package com.project.naveen.lcrm.menu.fragment.settings;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class KeyConfigFragment extends Fragment {
EditText editTextAppId,editTextKey,editTextSecret,editTextPublisherKey,editTextSecretKey;
    CoordinatorLayout coordinator;

    public KeyConfigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_key_config, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        editTextAppId=(EditText)v.findViewById(R.id.app_id);
        editTextKey=(EditText)v.findViewById(R.id.key);
        editTextSecret=(EditText)v.findViewById(R.id.secret);
        editTextPublisherKey=(EditText)v.findViewById(R.id.publisher_key);
        editTextSecretKey=(EditText)v.findViewById(R.id.secret_key);
        coordinator=(CoordinatorLayout)v.findViewById(R.id.coordinator);

        if (actionBar != null) {
            actionBar.setTitle("Pusher and Stripe setting");
        }
        setHasOptionsMenu(true);
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
        {InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            updateSetting(Appconfig.TOKEN);

        }
        return super.onOptionsItemSelected(item);
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
                params.put("pusher_app_id",editTextAppId.getText().toString());
                params.put("pusher_key",editTextKey.getText().toString());
                params.put("pusher_secret",editTextSecret.getText().toString());
                params.put("stripe_secret",editTextSecretKey.getText().toString());
                params.put("stripe_publishable",editTextPublisherKey.getText().toString());

                return params;
            }
        } ;
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);



    }

}

