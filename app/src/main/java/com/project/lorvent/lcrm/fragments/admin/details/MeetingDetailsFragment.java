package com.project.lorvent.lcrm.fragments.admin.details;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeetingDetailsFragment extends Fragment {
    int meeting_id_position;
    String token,meeting_id;
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8;
    View v;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    public MeetingDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_details6, container, false);
         meeting_id=getArguments().getString("meetingId");
        showHelpForFirstLaunch();

        token= Appconfig.TOKEN;
        tv1=(TextView)v.findViewById(R.id.meeting_subject);
        tv2=(TextView)v.findViewById(R.id.attendies);
        tv3=(TextView)v.findViewById(R.id.responsible);
        tv4=(TextView)v.findViewById(R.id.start_date);
        tv5=(TextView)v.findViewById(R.id.end_date);
        tv6=(TextView)v.findViewById(R.id.location);
        tv7=(TextView)v.findViewById(R.id.duration);
        tv8=(TextView)v.findViewById(R.id.description);
        new MeetingDetailsTask().execute(token,meeting_id);
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
    private class MeetingDetailsTask extends AsyncTask<String,Void,String>
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
            String meetingId=params[1];
            URL url;
            try {
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String detail_url;
                if (text_url != null) {
                    detail_url = text_url + "/user/meeting?token=";
                } else {
                    detail_url= Appconfig.MEETING_DETAILS_URL;
                }
                url = new URL(detail_url+tok+"&meeting_id="+meetingId);
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
                JSONArray meetingJsonArray = jsonObject.getJSONArray("meeting");
                for (int i = 0; i < meetingJsonArray.length(); i++) {
                    JSONObject jsonObject1 = meetingJsonArray.getJSONObject(i);
                    tv1.setText(jsonObject1.getString("meeting_subject"));
                    tv3.setText(jsonObject1.getString("responsible"));
                    tv4.setText(jsonObject1.getString("starting_date"));
                    tv5.setText(jsonObject1.getString("ending_date"));
                    tv6.setText(jsonObject1.getString("location"));
                    tv7.setText(jsonObject1.getString("duration"));
                    tv8.setText(jsonObject1.getString("meeting_description"));
                }
                JSONArray attendiesJsonArray = jsonObject.getJSONArray("attendees");

                for (int i=0;i<attendiesJsonArray.length();i++)
                {
                    JSONArray JsonArray = attendiesJsonArray.getJSONArray(i);
                    for (int j=0;j<=JsonArray.length();j++)
                    {
                        tv2.setText(tv2.getText()+"  "+JsonArray.getString(j));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

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
