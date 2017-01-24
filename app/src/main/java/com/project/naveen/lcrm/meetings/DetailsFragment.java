package com.project.naveen.lcrm.meetings;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joaquimley.faboptions.FabOptions;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.detailsactivity.DetailMeetingActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
    int meeting_id_position;
    String token,meeting_id;
    TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View v=inflater.inflate(R.layout.fragment_details6, container, false);
         meeting_id=getArguments().getString("meetingId");
        Log.i("meetingidindfgd--",meeting_id);

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
                url = new URL(Appconfig.MEETING_DETAILS_URL+tok+"&meeting_id="+meetingId);
                conn = (HttpURLConnection) url.openConnection();
                Log.i("meetingid--",meetingId);
                //Log.i("res code--",""+conn.getResponseCode());
                int responseCode=conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
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
                        //  Log.d("output lines", line);
                    }
                    Log.i("response",response);
                    json = new JSONObject(response);
                    jsonResponse=json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
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
                Log.i("attendiesJsonArray--",attendiesJsonArray.toString());

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
}
