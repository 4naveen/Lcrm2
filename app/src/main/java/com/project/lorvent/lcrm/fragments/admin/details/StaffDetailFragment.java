package com.project.lorvent.lcrm.fragments.admin.details;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffDetailFragment extends Fragment {
    private TextView name,email,phone;
    CheckBox salesteam_read, salesteam_write, salesteam_delete, lead_read, lead_write, lead_delete, opp_read, opp_write, opp_delete, loggedcall_read,
            loggedcall_write, loggedcall_delete, meeting_read, meeting_write, meeting_delete, products_read, products_write, products_delete,
            quotation_read, quotation_write, quotation_delete, salesorder_read, salesorder_write, salesorder_delete, invoices_read, invoices_write, invoices_delete,
            contracts_read, contracts_write, contracts_delete, staff_read, staff_write, staff_delete, contacts_read, contacts_write, contacts_delete;
    CircleImageView staff_image;
    String staff_id;
    View v;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    ArrayList<String> permissions;

    public StaffDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.fragment_detail2, container, false);
        staff_id=getArguments().getString("staffId");
        showHelpForFirstLaunch();
        permissions = new ArrayList<>();

        staff_image=(CircleImageView) v.findViewById(R.id.staff_image);
        name=(TextView)v.findViewById(R.id.name);
        //last_Name=(TextView)v.findViewById(R.id.last_name);
        email=(TextView)v.findViewById(R.id.email);
        phone=(TextView)v.findViewById(R.id.phone);
        salesteam_read=(CheckBox)v.findViewById(R.id.salesteam_read);
        salesteam_write=(CheckBox)v.findViewById(R.id.salesteam_write);
        salesteam_delete=(CheckBox)v.findViewById(R.id.salesteam_delete);
        lead_read=(CheckBox)v.findViewById(R.id.leads_read);
        lead_write=(CheckBox)v.findViewById(R.id.leads_write);
        lead_delete=(CheckBox)v.findViewById(R.id.leads_delete);
        opp_read=(CheckBox)v.findViewById(R.id.opp_read);
        opp_write=(CheckBox)v.findViewById(R.id.opp_write);
        opp_delete=(CheckBox)v.findViewById(R.id.opp_delete);
        loggedcall_read=(CheckBox)v.findViewById(R.id.loggedCalls_read);
        loggedcall_write=(CheckBox)v.findViewById(R.id.loggedCalls_write);
        loggedcall_delete=(CheckBox)v.findViewById(R.id.loggedCalls_delete);
        meeting_read=(CheckBox)v.findViewById(R.id.meeting_read);
        meeting_write=(CheckBox)v.findViewById(R.id.meeting_write);
        meeting_delete=(CheckBox)v.findViewById(R.id.meeting_delete);
        products_read=(CheckBox)v.findViewById(R.id.products_read);
        products_write=(CheckBox)v.findViewById(R.id.products_write);
        products_delete=(CheckBox)v.findViewById(R.id.products_delete);
        quotation_read=(CheckBox)v.findViewById(R.id.quotation_read);
        quotation_write=(CheckBox)v.findViewById(R.id.quotation_write);
        quotation_delete=(CheckBox)v.findViewById(R.id.quotation_delete);
        salesorder_read=(CheckBox)v.findViewById(R.id.salesorder_read);
        salesorder_write=(CheckBox)v.findViewById(R.id.salesorder_write);
        salesorder_delete=(CheckBox)v.findViewById(R.id.salesorder_delete);
        invoices_read=(CheckBox)v.findViewById(R.id.invoices_read);
        invoices_write=(CheckBox)v.findViewById(R.id.invoices_write);
        invoices_delete=(CheckBox)v.findViewById(R.id.invoices_delete);
        contracts_read=(CheckBox)v.findViewById(R.id.contracts_read);
        contracts_write=(CheckBox)v.findViewById(R.id.contracts_write);
        contracts_delete=(CheckBox)v.findViewById(R.id.contracts_delete);
        staff_read=(CheckBox)v.findViewById(R.id.staff_read);
        staff_write=(CheckBox)v.findViewById(R.id.staff_write);
        staff_delete=(CheckBox)v.findViewById(R.id.staff_delete);
        contacts_read=(CheckBox)v.findViewById(R.id.contacts_read);
        contacts_write=(CheckBox)v.findViewById(R.id.contacts_write);
        contacts_delete=(CheckBox)v.findViewById(R.id.contacts_delete);


        // permissions=(TextView)v.findViewById(R.id.permissions);
        new StaffDetailsTask().execute(Appconfig.TOKEN, String.valueOf(staff_id));
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        getActivity().finish();

                        return true;
                    }
                }
                return false;
            }
        });

        return v;
    }
    private class StaffDetailsTask extends AsyncTask<String,Void,String>
    {
         ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonResponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String staffId=params[1];
            URL url;
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url= text_url + "/user/staff?token=";
                } else {
                    detail_url= Appconfig.STAFF_DETAILS_URL;
                }
                url = new URL(detail_url+tok+"&staff_id="+staffId);
                conn = (HttpURLConnection) url.openConnection();
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }

                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));

                    jsonResponse=response;

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    json = new JSONObject(response);
                    jsonResponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
            return jsonResponse;
        }
        @Override
        protected void onPostExecute(String result) {
             dialog.dismiss();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("staff");
                name.setText(jsonObject1.getString("first_name")+" "+jsonObject1.getString("last_name"));
               // last_Name.setText(jsonObject1.getString("last_name"));
                email.setText(jsonObject1.getString("email"));
                phone.setText(jsonObject1.getString("phone_number"));
                getImage(jsonObject1.getString("avatar"));

                JSONObject permission = jsonObject1.getJSONObject("permissions");
                Iterator<String> iter = permission.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    permissions.add(key);
                }
                for (int i = 0; i < permissions.size(); i++) {
                    if (permissions.get(i).equals("sales_team.read")) {
                       salesteam_read.setChecked(true);
                    } else if (permissions.get(i).equals("sales_team.write")) {
                        salesteam_write.setChecked(true);
                    } else if (permissions.get(i).equals("sales_team.delete")) {
                        salesteam_delete.setChecked(true);
                    } else if (permissions.get(i).equals("leads.read")) {
                          lead_read.setChecked(true);
                    } else if (permissions.get(i).equals("leads.write")) {
                        lead_write.setChecked(true);

                    } else if (permissions.get(i).equals("leads.delete")) {
                        lead_delete.setChecked(true);

                    } else if (permissions.get(i).equals("opportunities.read")) {
                         opp_read.setChecked(true);
                    } else if (permissions.get(i).equals("opportunities.write")) {
                        opp_write.setChecked(true);
                    } else if (permissions.get(i).equals("opportunities.delete")) {
                        opp_delete.setChecked(true);
                    } else if (permissions.get(i).equals("logged_calls.read")) {
                          loggedcall_read.setChecked(true);
                    } else if (permissions.get(i).equals("logged_calls.write")) {
                        loggedcall_write.setChecked(true);

                    } else if (permissions.get(i).equals("logged_calls.delete")) {
                        loggedcall_delete.setChecked(true);

                    } else if (permissions.get(i).equals("meetings.read")) {
                       meeting_read.setChecked(true);
                    } else if (permissions.get(i).equals("meetings.write")) {
                        meeting_write.setChecked(true);

                    } else if (permissions.get(i).equals("meetings.delete")) {
                        meeting_delete.setChecked(true);

                    } else if (permissions.get(i).equals("products.read")) {
                            products_read.setChecked(true);
                    } else if (permissions.get(i).equals("products.write")) {
                        products_write.setChecked(true);

                    } else if (permissions.get(i).equals("products.delete")) {
                        products_delete.setChecked(true);

                    } else if (permissions.get(i).equals("quotations.read")) {
                         quotation_read.setChecked(true);
                    } else if (permissions.get(i).equals("quotations.write")) {
                        quotation_write.setChecked(true);

                    } else if (permissions.get(i).equals("quotations.delete")) {
                        quotation_delete.setChecked(true);

                    } else if (permissions.get(i).equals("sales_orders.read")) {
                              salesorder_read.setChecked(true);
                    } else if (permissions.get(i).equals("sales_orders.write")) {
                        salesorder_write.setChecked(true);

                    } else if (permissions.get(i).equals("sales_orders.delete")) {
                        salesorder_delete.setChecked(true);

                    } else if (permissions.get(i).equals("invoices.read")) {
                        invoices_read.setChecked(true);

                    } else if (permissions.get(i).equals("invoices.write")) {
                        invoices_write.setChecked(true);

                    } else if (permissions.get(i).equals("invoices.delete")) {
                        invoices_delete.setChecked(true);

                    } else if (permissions.get(i).equals("contracts.read")) {
                             contracts_read.setChecked(true);
                    } else if (permissions.get(i).equals("contracts.write")) {
                        contracts_write.setChecked(true);
                    } else if (permissions.get(i).equals("contracts.delete")) {
                        contracts_delete.setChecked(true);

                    } else if (permissions.get(i).equals("staff.read")) {
                        staff_read.setChecked(true);
                    } else if (permissions.get(i).equals("staff.write")) {
                        staff_write.setChecked(true);

                    } else if (permissions.get(i).equals("staff.delete")) {
                        staff_delete.setChecked(true);

                    } else if (permissions.get(i).equals("contacts.read")) {
                            contacts_read.setChecked(true);
                    } else if (permissions.get(i).equals("contacts.write")) {
                        contacts_write.setChecked(true);
                    } else if (permissions.get(i).equals("contacts.delete")) {
                        contacts_delete.setChecked(true);
                    } else {
                        //do nothing
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
    private void getImage(String image_url)
    {
        ImageRequest ir = new ImageRequest(image_url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                staff_image.setImageBitmap(response);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                staff_image.setImageResource(R.drawable.upload);

            }
        });
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(ir);

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
        SharedPreferences preferences = getActivity().getSharedPreferences("pref2",MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }

    private void savePreference(String key, boolean value) {
        SharedPreferences preferences = getActivity().getSharedPreferences("pref2",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    private void showOverLay(){
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.overlay_view);
        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overlayLayout);
        ImageView image=(ImageView)dialog.findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.overlay7);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }

        });

        dialog.show();

    }
}
