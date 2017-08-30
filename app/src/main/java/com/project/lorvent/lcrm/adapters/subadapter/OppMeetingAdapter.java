package com.project.lorvent.lcrm.adapters.subadapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Staff;
import com.project.lorvent.lcrm.models.submodels.OppMeeting;
import com.weiwangcn.betterspinner.library.BetterSpinner;


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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Guest on 11/18/2016.
 */

public class OppMeetingAdapter extends RecyclerSwipeAdapter<OppMeetingAdapter.SimpleViewHolder> {
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
    private EditText meeting_sub, editText_start_date, editText_end_date, start_time, end_time;
    public static Dialog mdialog;
    private int start_day, start_month, start_year, end_day, end_month, end_year;
    private int start_hr, start_min, end_hr, end_min;
    private SimpleDateFormat simpleDateFormat;
    private ArrayList<Staff> responsibleStaffList;
    private ArrayList<String> responsibleList;
    private BetterSpinner responsible;
    private TextInputLayout input_meeting_subject, input_start_date, inpur_end_date, input_start_time, input_end_time, input_responsible;

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
        start_year = Calendar.getInstance().get(Calendar.YEAR);
        end_year = Calendar.getInstance().get(Calendar.YEAR);
        start_month = Calendar.getInstance().get(Calendar.MONTH);
        start_hr = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        start_min = Calendar.getInstance().get(Calendar.MINUTE);

        end_month = Calendar.getInstance().get(Calendar.MONTH);
        start_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        end_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        end_hr = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        end_min = Calendar.getInstance().get(Calendar.MINUTE);

        responsibleList = new ArrayList<>();
        responsibleStaffList = new ArrayList<>();
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

        viewHolder.layout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingIdPosition = viewHolder.getLayoutPosition();
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
                mItemManger.closeAllItems();

            }
        });

        viewHolder.layout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingIdPosition = viewHolder.getLayoutPosition();
                getResponsibleStaffList(Appconfig.TOKEN);
                getDateSettings(Appconfig.TOKEN);
                getOpportunityMeeting(opportunity_id, String.valueOf(meeting_id));
                MaterialDialog dialog1 = new MaterialDialog.Builder(context)
                        .title("Edit Qpportunity Meeting")
                        .customView(R.layout.opp_meeting_edit_dialog, true)
                        .positiveText("SUBMIT")
                        .autoDismiss(false)
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (editText_start_date.getText().toString().isEmpty()) {
                                    input_start_date.setError("please enter start date");
                                    return;
                                } else if (start_time.getText().toString().isEmpty()) {
                                    input_start_time.setError("please enter start time");
                                    return;
                                } else if (editText_end_date.getText().toString().isEmpty()) {
                                    inpur_end_date.setError("please select end date");
                                    return;
                                } else if (end_time.getText().toString().isEmpty()) {
                                    input_end_time.setError("please select end time");
                                    return;
                                } else if (responsible.getText().toString().isEmpty()) {
                                    input_responsible.setError("please select responsible staff");
                                    return;
                                } else if (meeting_sub.getText().toString().isEmpty()) {
                                    input_meeting_subject.setError("please enter meeting subject");
                                    return;
                                } else {
                                    dialog.dismiss();
                                }
                                String starting_date = editText_start_date.getText().toString() + "" + start_time.getText().toString();
                                String ending_date = editText_end_date.getText().toString() + "" + end_time.getText().toString();
                                int responsibleId = responsibleStaffList.get(responsibleList.indexOf(responsible.getText().toString())).getId();

                                new EditOppMeeting().execute(token, String.valueOf(meeting_id), String.valueOf(opportunity_id), starting_date, ending_date,
                                        meeting_sub.getText().toString(), String.valueOf(responsibleId));
                            }
                        })
                        .negativeColorRes(R.color.colorPrimary)
                        .negativeText("CANCEL")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();

                            }
                        })
                        .show();

                View view = dialog1.getCustomView();
                if (view != null) {
                    start_time = (EditText) dialog1.getCustomView().findViewById(R.id.start_time);
                    end_time = (EditText) dialog1.getCustomView().findViewById(R.id.end_time);
                    editText_start_date = (EditText) dialog1.getCustomView().findViewById(R.id.start_date);
                    editText_end_date = (EditText) dialog1.getCustomView().findViewById(R.id.end_date);
                    meeting_sub = (EditText) dialog1.getCustomView().findViewById(R.id.meeting_subject);
                    responsible = (BetterSpinner) dialog1.getCustomView().findViewById(R.id.responsible);
                    input_start_date = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.text_start_date);
                    inpur_end_date = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_end_date);
                    input_start_time = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.text_start_time);
                    input_end_time = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_end_time);
                    input_meeting_subject = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_meeting_subject);
                    input_responsible = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_responsible);
                    responsible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            input_responsible.setError("");
                        }
                    });
                    meeting_sub.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_meeting_subject.setError("");

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        start_time.setShowSoftInputOnFocus(false);
                        end_time.setShowSoftInputOnFocus(false);
                        start_date.setShowSoftInputOnFocus(false);
                        end_date.setShowSoftInputOnFocus(false);
                    }
                }

                editText_start_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText_start_date.getWindowToken(), 0);
                        DatePickerDialog d = new DatePickerDialog(context, startDateSetListener, start_year, start_month, start_day);
                        d.show();
                    }
                });
                editText_end_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText_end_date.getWindowToken(), 0);
                        DatePickerDialog d = new DatePickerDialog(context, endDateSetListener, end_year, end_month, end_day);
                        d.show();

                    }
                });
                start_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(start_time.getWindowToken(), 0);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context, start_time_listener, start_hr, start_min, true);
                        timePickerDialog.show();
                    }
                });
                end_time.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(end_time.getWindowToken(), 0);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context, end_time_listener, end_hr, end_min, true);
                        timePickerDialog.show();
                    }
                });
                responsible.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        ArrayAdapter<String> responsibleArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, responsibleList);
                        responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                        responsible.setAdapter(responsibleArrayAdapter);
                    }
                });
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
        LinearLayout layout_edit, layout_delete;
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
            layout_edit = (LinearLayout) itemView.findViewById(R.id.layout_edit);
            layout_delete = (LinearLayout) itemView.findViewById(R.id.layout_delete);

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
            start_year = year;
            start_month = monthOfYear;
            start_day = dayOfMonth;
            Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
            editText_start_date.setText(Connection.getFormatedDate(date));
            editText_start_date.clearFocus();
        }
    };
    DatePickerDialog.OnDateSetListener endDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            end_year = year;
            end_month = monthOfYear;
            end_day = dayOfMonth;
            Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
            editText_end_date.setText(Connection.getFormatedDate(date));
            editText_end_date.clearFocus();
        }
    };
    final TimePickerDialog.OnTimeSetListener start_time_listener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            start_hr = hourOfDay;
            start_min = minute;
            if (AppSession.time_format.equals("g:i a"))
            {
                Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DATE),start_hr,start_min);
                String strDateFormat = "H:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                start_time.setText(sdf.format(date).toLowerCase());
            }
            if (AppSession.time_format.equals("g:i A"))
            {  Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DATE),start_hr,start_min);
                String strDateFormat = "H:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                start_time.setText(sdf.format(date));

            }
            if (AppSession.time_format.equals("H:i"))
            {
                Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DATE),start_hr,start_min);
                String strDateFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                start_time.setText(sdf.format(date));

            }
          //  start_time.setText(start_hr + ":" + start_min);
        }
    };
    final TimePickerDialog.OnTimeSetListener end_time_listener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            end_hr = hourOfDay;
            end_min = minute;
            if (AppSession.time_format.equals("g:i a"))
            {
                Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DATE),end_hr,end_min);
                String strDateFormat = "H:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                end_time.setText(sdf.format(date));
            }
            if (AppSession.time_format.equals("g:i A"))
            {    Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DATE),end_hr,end_min);
                String strDateFormat = "H:mm a";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                end_time.setText(sdf.format(date));

            }
            if (AppSession.time_format.equals("H:i"))
            {
                Date date=new Date(Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DATE),end_hr,end_min);
                String strDateFormat = "HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat,Locale.ENGLISH);
                end_time.setText(sdf.format(date));

            }
           // end_time.setText(end_hr + ":" + end_min);
        }
    };

    private void getOpportunityMeeting(String opportunityId, String meetingId) {
        //Log.i("oppId--", String.valueOf(opportunityId));

        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading, please wait");
        dialog.setTitle("Connecting server");
        dialog.show();
        dialog.setCancelable(false);

        SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/opportunity_meeting?token=";
        } else {
            get_url = Appconfig.DETAILS_OPPPORTUNITY_MEETING_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, get_url+ Appconfig.TOKEN + "&opportunity_id=" + opportunityId + "&meeting_id=" + meetingId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           // Log.i("response--", response);
                            ArrayList<String>start_date_time=new ArrayList<>();
                            ArrayList<String>end_date_time=new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("meetings");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                meeting_sub.setText(object.getString("meeting_subject"));
                                responsible.setText(object.getString("responsible"));
                                StringTokenizer tk1=new StringTokenizer(object.getString("starting_date"));
                                StringTokenizer tk2=new StringTokenizer(object.getString("ending_date"));
                                while (tk1.hasMoreTokens())
                                {
                                    //Log.i("token",tk.nextToken());
                                    start_date_time.add(tk1.nextToken());
                                }
                                while (tk2.hasMoreTokens())
                                {
                                    //Log.i("token",tk.nextToken());
                                    end_date_time.add(tk2.nextToken());
                                }
                                editText_start_date.setText(start_date_time.get(0)+""+start_date_time.get(1));
                                start_time.setText(start_date_time.get(2));
                                editText_end_date.setText(end_date_time.get(0)+""+end_date_time.get(1));
                                end_time.setText(end_date_time.get(2));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);

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
        SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/settings?token=";
        } else {
            get_url = Appconfig.SETTINGS_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, get_url+ token,
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
                            Crashlytics.logException(e);

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
            String responsible_id = params[6];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("meeting_id", meetingId);
                jsonObject.put("opportunity_id", opportunityId);
                jsonObject.put("meeting_subject", meeting);
                jsonObject.put("starting_date", startDate);
                jsonObject.put("ending_date", endDate);
                jsonObject.put("responsible_id", responsible_id);

                SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String edit_url;
                if (text_url != null) {
                    edit_url = text_url + "/user/edit_opportunity_meeting?token=";
                } else {
                    edit_url = Appconfig.EDIT_OPPPORTUNITY_MEETING_URL;
                }
                url = new URL(edit_url+tok);
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
                Crashlytics.logException(e);

            }
            return jsonresponse;
        }

        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            if (result.equals("success")) {
                getOpportunityMeeting(opportunity_id);
                Toast.makeText(context, "Updated Succesfully!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Item not updated! Try Again", Toast.LENGTH_LONG).show();
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
                SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String delete_url;
                if (text_url != null) {
                    delete_url = text_url + "/user/delete_opportunity_meeting?token=";
                } else {
                    delete_url = Appconfig.DELETE_OPPORTUNITY_MEETING_URL;
                }
                url = new URL(delete_url+tok);
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
                Crashlytics.logException(e);

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

    private void getResponsibleStaffList(String token) {
        SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/staffs?token=";
        } else {
            get_url = Appconfig.STAFF_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+ token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            // Log.i("response--", String.valueOf(response));

                            JSONObject jsonObject = new JSONObject(response);
                            /*JSONArray jsonArray = jsonObject.getJSONArray("staffs");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Staff staff = new Staff();
                                staff.setId(object.getInt("id"));
                                staff.setName(object.getString("full_name"));
                                responsibleList.add(object.getString("full_name"));
                                responsibleStaffList.add(staff);
                            }*/

                            JSONObject jsonObject1 = jsonObject.getJSONObject("staffs");
                            Iterator<String> iter = jsonObject1.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();

                                try {
                                    Object value = jsonObject1.get(key);
                                    JSONObject jsonObject2 = (JSONObject) jsonObject1.get(key);
                                    Staff staff = new Staff();
                                    staff.setName(jsonObject2.getString("full_name"));
                                    staff.setId(jsonObject2.getInt("id"));
                                    responsibleList.add(jsonObject2.getString("full_name"));
                                    responsibleStaffList.add(staff);

                                } catch (JSONException e) {
                                    Crashlytics.logException(e);

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);

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

    private void getOpportunityMeeting(String opportunityId) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading, please wait");
        dialog.setTitle("Connecting server");
        dialog.show();
        dialog.setCancelable(false);
        SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/opportunity_meetings?token=";
        } else {
            get_url = Appconfig.OPPORTUNITY_MEETING_URL;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+ Appconfig.TOKEN + "&opportunity_id=" + opportunityId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("meetings");
                            oppMeetings.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                OppMeeting oppMeeting = new OppMeeting();
                                oppMeeting.setStart_date(object.getString("starting_date"));
                                oppMeeting.setEnd_date(object.getString("ending_date"));
                                oppMeeting.setMeeting_subject(object.getString("meeting_subject"));
                                oppMeeting.setResponsible(object.getString("responsible"));
                                oppMeeting.setMeeting_id(object.getInt("id"));
                                oppMeetings.add(oppMeeting);

                            }
                            notifyItemChanged(meetingIdPosition);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);

                        }

                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);

    }
}