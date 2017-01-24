package com.project.naveen.lcrm.adapters.subadapter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.submodels.OppMeeting;
import com.project.naveen.lcrm.opportunities.MeetingFragment;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Guest on 11/18/2016.
 */

public  class OppMeetingAdapter extends RecyclerSwipeAdapter<OppMeetingAdapter.SimpleViewHolder> {

    private ArrayList<OppMeeting> oppMeetings;
    private Context context;
    private String token, date_format, opportunity_id;
    private int meetingIdPosition;
    private String check = "down";
    private SimpleViewHolder svHolder;
    public static int current_year, current_month, current_day;
    public static String current_month_text;
    public static final int EDIT_START_DATE_PICKER_ID = 101;
    public static final int EDIT_END_DATE_PICKER_ID = 102;
    ViewGroup mViewGroup;
    private EditText meeting_sub,editText_start_date,editText_end_date,start_time,end_time;
    public static Dialog mdialog;
    private int start_day,start_month,start_year,end_day,end_month,end_year;
    private int start_hr,start_min,end_hr,end_min;
    private SimpleDateFormat simpleDateFormat;

    public OppMeetingAdapter(ArrayList<OppMeeting> oppMeetings, Context context, String opportunity_id) {
        this.oppMeetings = oppMeetings;
        this.context = context;
        this.opportunity_id = opportunity_id;
        token = Appconfig.TOKEN;

    }

    @Override
    public OppMeetingAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opp_meeting_indi_view, parent, false);

        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OppMeetingAdapter.SimpleViewHolder viewHolder, final int position) {
        final TextView start_date_title = new TextView(context);
        final TextView start_date = new TextView(context);
        final TextView end_date_title = new TextView(context);
        final TextView end_date = new TextView(context);

        final OppMeeting oppMeeting = oppMeetings.get(position);
        meetingIdPosition = viewHolder.getLayoutPosition();
        start_year= Calendar.getInstance().get(Calendar.YEAR);
        end_year= Calendar.getInstance().get(Calendar.YEAR);
        start_month=Calendar.getInstance().get(Calendar.MONTH);
        start_hr=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        start_min=Calendar.getInstance().get(Calendar.MINUTE);

        end_month=Calendar.getInstance().get(Calendar.MONTH);
        start_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        end_day=Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        end_hr=Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        end_min=Calendar.getInstance().get(Calendar.MINUTE);
        final int meeting_id = oppMeeting.getMeeting_id();
        Log.i("meetingId-", String.valueOf(meeting_id));
        viewHolder.tvMeeting.setText(oppMeeting.getMeeting_subject());
        viewHolder.tvResponsible.setText(oppMeeting.getResponsible());
        viewHolder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                viewHolder.arrow.setImageResource(R.mipmap.right);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                viewHolder.arrow.setImageResource(R.mipmap.left);
            }

            @Override
            public void onClose(SwipeLayout layout) {
                viewHolder.arrow.setImageResource(R.mipmap.left);
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                // viewHolder.arrow.setImageResource(R.drawable.arrow);
            }
        });
        viewHolder.arrow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (check.equals("down")) {
                    check = "up";
                    viewHolder.arrow1.setImageResource(R.mipmap.up);
                    start_date_title.setText("Start Date");
                    start_date_title.setTextColor(Color.parseColor("#000000"));
                    start_date.setText(oppMeeting.getStart_date());
                    end_date_title.setText("End Date");
                    end_date_title.setTextColor(Color.parseColor("#000000"));
                    end_date.setText(oppMeeting.getEnd_date());
                    for (int i = 0; i < viewHolder.layout_start_date_title.getChildCount(); i++) {
                        if (viewHolder.layout_start_date_title.getChildAt(i) instanceof TextView) {
                            viewHolder.layout_start_date_title.removeView(viewHolder.layout_start_date_title.getChildAt(i));
                        }
                    }
                    for (int i = 0; i < viewHolder.layout_start_date.getChildCount(); i++) {
                        if (viewHolder.layout_start_date.getChildAt(i) instanceof TextView) {
                            viewHolder.layout_start_date.removeView(viewHolder.layout_start_date.getChildAt(i));
                        }
                    }
                    for (int i = 0; i < viewHolder.layout_end_date_title.getChildCount(); i++) {
                        if (viewHolder.layout_end_date_title.getChildAt(i) instanceof TextView) {
                            viewHolder.layout_end_date_title.removeView(viewHolder.layout_end_date_title.getChildAt(i));
                        }
                    }
                    for (int i = 0; i < viewHolder.layout_end_date.getChildCount(); i++) {
                        if (viewHolder.layout_end_date.getChildAt(i) instanceof TextView) {
                            viewHolder.layout_end_date.removeView(viewHolder.layout_end_date.getChildAt(i));
                        }
                    }
                    viewHolder.layout_start_date_title.addView(start_date_title);
                    viewHolder.layout_start_date.addView(start_date);
                    viewHolder.layout_end_date_title.addView(end_date_title);
                    viewHolder.layout_end_date.addView(end_date);
                } else {
                    check = "down";
                    viewHolder.arrow1.setImageResource(R.mipmap.down);
                    for (int i = 0; i < viewHolder.layout_start_date_title.getChildCount(); i++) {
                        if (viewHolder.layout_start_date_title.getChildAt(i) instanceof TextView) {
                            viewHolder.layout_start_date_title.removeView(viewHolder.layout_start_date_title.getChildAt(i));
                        }
                    }
                    for (int i = 0; i < viewHolder.layout_start_date.getChildCount(); i++) {
                        if (viewHolder.layout_start_date.getChildAt(i) instanceof TextView) {
                            viewHolder.layout_start_date.removeView(viewHolder.layout_start_date.getChildAt(i));
                        }
                    }
                    for (int i = 0; i < viewHolder.layout_end_date_title.getChildCount(); i++) {
                        if (viewHolder.layout_end_date_title.getChildAt(i) instanceof TextView) {
                            viewHolder.layout_end_date_title.removeView(viewHolder.layout_end_date_title.getChildAt(i));
                        }
                    }
                    for (int i = 0; i < viewHolder.layout_end_date.getChildCount(); i++) {
                        if (viewHolder.layout_end_date.getChildAt(i) instanceof TextView) {
                            viewHolder.layout_end_date.removeView(viewHolder.layout_end_date.getChildAt(i));
                        }
                    }
                    viewHolder.layout_start_date_title.removeView(start_date_title);
                    viewHolder.layout_start_date.removeView(start_date);
                    viewHolder.layout_end_date_title.removeView(end_date_title);
                    viewHolder.layout_end_date.removeView(end_date);
                    // viewHolder.layout_call_summary.invalidate();
                    //viewHolder.layout_date.invalidate();
                }
            }
        });

        viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
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
                                new DeleteOpportunityMeeting().execute(token, String.valueOf(meeting_id));

                                sweetAlertDialog.cancel();
                            }
                        })
                        .show();
            }
        });

        viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.opp_meeting_edit_dialog);
                meeting_sub=(EditText)dialog.findViewById(R.id.meeting_subject);
                start_time=(EditText)dialog.findViewById(R.id.start_time);
                end_time=(EditText)dialog.findViewById(R.id.end_time);
                editText_start_date=(EditText)dialog.findViewById(R.id.start_date);
                editText_end_date=(EditText)dialog.findViewById(R.id.end_date);
                getDateSettings(Appconfig.TOKEN);
                   getOpportunityMeeting(opportunity_id,String.valueOf(meeting_id));
                editText_start_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                        DatePickerDialog d = new DatePickerDialog(context, startDateSetListener, start_year,start_month,start_day);
                        d.show();
                    }
                });

                editText_end_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                        DatePickerDialog d = new DatePickerDialog(context, endDateSetListener, end_year,end_month,end_day);
                        d.show();

                    }
                });
                start_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(start_time.getWindowToken(), 0);
                        TimePickerDialog timePickerDialog=new TimePickerDialog(context,start_time_listener,start_hr,start_min,true);
                        timePickerDialog.show();
                    }
                });
                end_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(end_time.getWindowToken(), 0);
                        TimePickerDialog timePickerDialog=new TimePickerDialog(context,end_time_listener,end_hr,end_min,true);
                        timePickerDialog.show();
                    }
                });

//                final EditText responsible=(EditText)dialog.findViewById(R.id.responsible);

                Button cancel=(Button) dialog.findViewById(R.id.cancel);


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button add = (Button) dialog.findViewById(R.id.submit);

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String starting_date=editText_start_date.getText().toString()+" "+start_time.getText().toString();
                        String ending_date=editText_end_date.getText().toString()+" "+end_time.getText().toString();
                        new EditOppMeeting().execute(token, String.valueOf(meeting_id), String.valueOf(opportunity_id),starting_date,ending_date,
                                meeting_sub.getText().toString());
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                // dialog.setTitle("Lead Details");
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.75);
                int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.50);
                dialog.getWindow().setLayout(width, height);
                setDialog(dialog);
                dialog.show();


            }
        });

        mItemManger.bindView(viewHolder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return oppMeetings.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView tvMeeting;
        TextView tvResponsible;
        ImageView arrow;
        ImageView arrow1;
        TextView tvDelete;
        TextView tvEdit;
        LinearLayout layout_start_date_title;
        LinearLayout layout_start_date;
        LinearLayout layout_end_date_title;
        LinearLayout layout_end_date;


        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            arrow1 = (ImageView) itemView.findViewById(R.id.arrow1);
            // tvDate= (TextView) itemView.findViewById(R.id.date);
            // tvCallSummary = (TextView) itemView.findViewById(R.id.call_summary);
            tvResponsible = (TextView) itemView.findViewById(R.id.responsible);
            tvMeeting = (TextView) itemView.findViewById(R.id.meeting_subject);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            tvEdit = (TextView) itemView.findViewById(R.id.tvEdit);
            layout_start_date_title = (LinearLayout) itemView.findViewById(R.id.layout_start_date_title);
            layout_start_date = (LinearLayout) itemView.findViewById(R.id.layout_start_date);
            layout_end_date_title = (LinearLayout) itemView.findViewById(R.id.layout_end_date_title);
            layout_end_date = (LinearLayout) itemView.findViewById(R.id.layout_end_date);
        }
    }

    DatePickerDialog.OnDateSetListener startDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            start_year=year;
            start_month=monthOfYear;
            start_day=dayOfMonth;
            simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
            Date date=new Date(year-1900,monthOfYear,dayOfMonth);
            String text_date=simpleDateFormat.format(date);
            editText_start_date.setText(text_date);
            editText_start_date.clearFocus();
        }
    };
    DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            end_year=year;
            end_month=monthOfYear;
            end_day=dayOfMonth;
            simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
            Date date=new Date(year-1900,monthOfYear,dayOfMonth);
            String text_date=simpleDateFormat.format(date);
            editText_end_date.setText(text_date);
            editText_end_date.clearFocus();
        }
    };
    final TimePickerDialog.OnTimeSetListener start_time_listener=new TimePickerDialog.OnTimeSetListener()
    {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            start_hr=hourOfDay;
            start_min=minute;
            start_time.setText(start_hr+":"+start_min);
        }
    };
    final TimePickerDialog.OnTimeSetListener end_time_listener=new TimePickerDialog.OnTimeSetListener()
    {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            end_hr=hourOfDay;
            end_min=minute;
            end_time.setText(end_hr+":"+end_min);
        }
    };

    private void getOpportunityMeeting(String opportunityId, String meetingId) {
        Log.i("oppId--", String.valueOf(opportunityId));
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading, please wait");
        dialog.setTitle("Connecting server");
        dialog.show();
        dialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.DETAILS_OPPPORTUNITY_MEETING_URL + Appconfig.TOKEN + "&opportunity_id=" + opportunityId + "&meeting_id=" + meetingId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("response--", response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("meetings");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                meeting_sub.setText(object.getString("meeting_subject"));
                                editText_start_date.setText(object.getString("starting_date"));
                                editText_end_date.setText(object.getString("ending_date"));

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        });
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);


    }

    private void getDateSettings(String token) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.SETTINGS_URL + token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            // Log.i("response--", String.valueOf(response));

                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject pre_settings = jsonObject.getJSONObject("settings");
                            date_format = (pre_settings.getString("date_format"));

                            Log.i("date--", date_format);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);


    }

    private class EditOppMeeting extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "", jsonresponse = "";
            BufferedReader bufferedReader = null;
            JSONObject json = null;
            JSONObject jsonObject = null;
            String tok = params[0];
            String meetingId = params[1];
            String opportunityId = params[2];
            String startDate = params[3];
            String endDate = params[4];
            String meeting = params[5];
//            String responsibleId=params[5];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("meeting_id", meetingId);
                jsonObject.put("opportunity_id", opportunityId);
                jsonObject.put("meeting_subject", meeting);
                jsonObject.put("starting_date", startDate);
                jsonObject.put("ending_date", endDate);
//                jsonObject.put("resp_staff_id", responsibleId);
                url = new URL(Appconfig.EDIT_OPPPORTUNITY_MEETING_URL + tok);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                Log.d("jsonobject line", jsonObject.toString());

                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));

                    jsonresponse = json.getString("success");

                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    Log.i("response", response);
                    json = new JSONObject(response);
                    jsonresponse = json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonresponse;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            if (result != null) {
                Toast.makeText(context, "" + result, Toast.LENGTH_LONG).show();
                //finish();
            }
        }
    }


    private class DeleteOpportunityMeeting extends AsyncTask<String, Void, String> {
        // ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String response = "", jsonresponse = "";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok = params[0];
            String meeting_id = params[1];
            URL url;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("meeting_id", meeting_id);

                url = new URL(Appconfig.DELETE_OPPORTUNITY_MEETING_URL + tok);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
                //Log.i("res code--",""+conn.getResponseCode());
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));

                    jsonresponse = json.getString("success");

                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    Log.i("response", response);
                    json = new JSONObject(response);
                    jsonresponse = json.getString("error");
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonresponse;
        }

        @Override
        protected void onPostExecute(String result) {
            //  dialog.dismiss();
            if (result != null) {
                if (result.equals("success")) {

                    oppMeetings.remove(meetingIdPosition);
                    notifyItemRemoved(meetingIdPosition);
                    notifyItemRangeChanged(meetingIdPosition, oppMeetings.size());

                    Log.i("Res--", result);
                }
                if (result.equals("not_valid_data")) {
                    Log.i("message--", "not deleted");
                }
            }

        }


    }
    public void setDialog(Dialog dialog) {
        mdialog = dialog;
    }

}