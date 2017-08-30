package com.project.lorvent.lcrm.adapters.subadapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.models.Company;
import com.project.lorvent.lcrm.models.Staff;
import com.project.lorvent.lcrm.models.submodels.OppCall;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by Guest on 11/18/2016.
 */

public class OpportunityCallAdapter extends RecyclerSwipeAdapter<OpportunityCallAdapter.SimpleViewHolder> {
    private ArrayList<OppCall> oppCalls;
    private Context context;
    private String token,date_format,opportunity_id,checkResponse;
    private int callIdPosition;
    private String check="down";
    private SimpleViewHolder svHolder;
    public static final int DATE_PICKER_ID_EDIT=112;
    private ViewGroup mViewGroup;
    private EditText editTextDate,editText_call_summary;
    int call_id;
    private BetterSpinner company,responsible;
    private ArrayList<String>companyNameList,responsibleList;
    public static int current_year, current_month, current_day;
    public static String current_month_text;
    ArrayList<Company>companyArrayList;
    ArrayList<Staff>responsibleStaffList;
    public static Dialog mdialog;
    TextInputLayout input_date, input_call_summary, input_customer, input_responsible;

    public OpportunityCallAdapter(ArrayList<OppCall> oppCalls, Context context,String opportunityId) {
        this.oppCalls = oppCalls;
        this.context = context;
        opportunity_id=opportunityId;
        token=Appconfig.TOKEN;
        companyNameList=new ArrayList<>();
        responsibleList=new ArrayList<>();
        companyArrayList=new ArrayList<>();
        responsibleStaffList=new ArrayList<>();
    }

    @Override
    public OpportunityCallAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.opp_call_indi_view, parent, false);
              mViewGroup=parent;
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OpportunityCallAdapter.SimpleViewHolder viewHolder, final int position) {
        final TextView call_summary_title = new TextView(context);
        final TextView call_summary= new TextView(context);
        final TextView call_date= new TextView(context);
        current_year = Calendar.getInstance().get(Calendar.YEAR);
        current_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        current_month = Calendar.getInstance().get(Calendar.MONTH);
        final OppCall oppCall=oppCalls.get(position);
        final int call_id=oppCall.getCall_id();
       /* //Log.i("callid-", String.valueOf(call_id));
        for (int i = 0; i <oppCalls.size() ; i++) {
            System.out.println("opplist--"+oppCalls.get(i));
        }*/

        getCompanyList(Appconfig.TOKEN);
        getResponsibleStaffList(Appconfig.TOKEN);
        svHolder=viewHolder;
        viewHolder.tvCompany.setText(oppCall.getCompany());
        viewHolder.tvResponsible.setText(oppCall.getResponsible());
        //iewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper));

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

                if (check.equals("down"))
                {check="up";
                    viewHolder.arrow1.setImageResource(R.mipmap.up);
                    call_summary_title.setText("Call Summary");
                    call_summary_title.setTextColor(Color.parseColor("#000000"));
                    call_summary.setText(oppCall.getCall_summary());
                    call_date.setText(oppCall.getDate());
                    for(int i = 0; i<viewHolder.layout_call_summary_title.getChildCount(); i++){
                        if(viewHolder.layout_call_summary_title.getChildAt(i) instanceof TextView){
                            viewHolder.layout_call_summary_title.removeView(viewHolder.layout_call_summary_title.getChildAt(i));
                        }
                    }
                    for(int i = 0; i<viewHolder.layout_call_summary.getChildCount(); i++){
                        if(viewHolder.layout_call_summary.getChildAt(i) instanceof TextView){
                            viewHolder.layout_call_summary.removeView(viewHolder.layout_call_summary.getChildAt(i));
                        }
                    }
                    for(int i = 0; i<viewHolder.layout_date.getChildCount(); i++){
                        if(viewHolder.layout_date.getChildAt(i) instanceof TextView){
                            viewHolder.layout_date.removeView(viewHolder.layout_date.getChildAt(i));
                        }
                    }
                    viewHolder.layout_call_summary_title.addView(call_summary_title);
                    viewHolder.layout_call_summary.addView(call_summary);
                    viewHolder.layout_date.addView(call_date);
                }
                else {
                    check="down";
                    viewHolder.arrow1.setImageResource(R.mipmap.down);
                    for(int i = 0; i<viewHolder.layout_call_summary_title.getChildCount(); i++){
                        if(viewHolder.layout_call_summary_title.getChildAt(i) instanceof TextView){
                            viewHolder.layout_call_summary_title.removeView(viewHolder.layout_call_summary_title.getChildAt(i));
                        }
                    }
                    for(int i = 0; i<viewHolder.layout_call_summary.getChildCount(); i++){
                        if(viewHolder.layout_call_summary.getChildAt(i) instanceof TextView){
                            viewHolder.layout_call_summary.removeView(viewHolder.layout_call_summary.getChildAt(i));
                        }
                    }
                    for(int i = 0; i<viewHolder.layout_date.getChildCount(); i++){
                        if(viewHolder.layout_date.getChildAt(i) instanceof TextView){
                            viewHolder.layout_date.removeView(viewHolder.layout_date.getChildAt(i));
                        }
                    }
                    viewHolder.layout_call_summary_title.removeView(call_summary_title);
                    viewHolder.layout_call_summary.removeView(call_summary);
                    viewHolder.layout_call_summary.removeView(call_date);
                    // viewHolder.layout_call_summary.invalidate();
                    //viewHolder.layout_date.invalidate();
                }}
        });

        viewHolder.layout_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callIdPosition=viewHolder.getLayoutPosition();

                getDateSettings(Appconfig.TOKEN);
                getOpportunityCall(opportunity_id,String.valueOf(call_id));
                MaterialDialog dialog1=new MaterialDialog.Builder(context)
                        .title("Edit Opportunity Call")
                        .autoDismiss(false)
                        .customView(R.layout.opp_call_edit_dialog, true)
                        .positiveText("SAVE")
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                if (editTextDate.getText().toString().isEmpty()) {
                                    input_date.setError("please enter date");
                                    return;
                                }  else if (company.getText().toString().isEmpty()) {
                                    input_customer.setError("please select company");
                                    return;
                                } else if (responsible.getText().toString().isEmpty()) {
                                    input_responsible.setError("please select user");
                                    return;
                                }
                                else if (editText_call_summary.getText().toString().isEmpty()) {
                                    input_call_summary.setError("please enter description");
                                    return;
                                }else {
                                    dialog.dismiss();
                                }
                                int companyId=companyArrayList.get(companyNameList.indexOf(company.getText().toString())).getId();
                                Log.i("companyId-", String.valueOf(companyId));
                                int responsibleId=responsibleStaffList.get(responsibleList.indexOf(responsible.getText().toString())).getId();
                                Log.i("responsibleId-", String.valueOf(responsibleId));

                                new EditOppCallTask().execute(token,opportunity_id,String.valueOf(call_id),editTextDate.getText().toString(),editText_call_summary.getText().toString(),
                                        String.valueOf(companyId),String.valueOf(responsibleId));
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

                // MaterialDialog dialog1 = new MaterialDialog.Builder(getActivity()).build();
                View view = dialog1.getCustomView();
                if (view != null) {
                    editTextDate=(EditText)dialog1.getCustomView().findViewById(R.id.date);
                    editText_call_summary = (EditText)dialog1.getCustomView().findViewById(R.id.call_sumaary);
                    company = (BetterSpinner)dialog1.getCustomView().findViewById(R.id.company);
                    responsible = (BetterSpinner)dialog1.getCustomView().findViewById(R.id.responsible);
                    input_date = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_date);
                    input_call_summary = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_call_sumaary);
                    input_customer = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_customer);
                    input_responsible = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_responsible);
                    company.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            input_customer.setError("");
                        }
                    });
                    responsible.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            input_responsible.setError("");
                        }
                    });
                    editText_call_summary.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_call_summary.setError("");

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                }
                editTextDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editTextDate.getWindowToken(), 0);
                        DatePickerDialog d = new DatePickerDialog(context, mDateSetListener, current_year, current_month, current_day);
                        d.show();
                    }
                });

                ArrayAdapter<String> companyArrayAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,companyNameList);
                companyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                company.setAdapter(companyArrayAdapter);

                ArrayAdapter<String> responsibleArrayAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,responsibleList);
                responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                responsible.setAdapter(responsibleArrayAdapter);

            }
        });

        viewHolder.layout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callIdPosition=viewHolder.getLayoutPosition();

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
                                new DeleteOpportunityCall().execute(token,String.valueOf(call_id));

                                sweetAlertDialog.cancel();

                            }
                        })
                        .show();
                mItemManger.closeAllItems();

            }

        });

        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return oppCalls.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView tvCompany;
        TextView tvResponsible;
        ImageView arrow;
        ImageView arrow1;
        TextView tvDelete;
        TextView tvEdit;
        LinearLayout layout_date,layout_edit,layout_delete;
        LinearLayout layout_call_summary;
        LinearLayout layout_call_summary_title;

        public SimpleViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            arrow=(ImageView)itemView.findViewById(R.id.arrow);
            arrow1=(ImageView)itemView.findViewById(R.id.arrow1);
            tvResponsible = (TextView) itemView.findViewById(R.id.responsible);
            tvCompany = (TextView) itemView.findViewById(R.id.company);
            tvDelete = (TextView) itemView.findViewById(R.id.tvDelete);
            tvEdit = (TextView) itemView.findViewById(R.id.tvEdit);
            layout_edit=(LinearLayout)itemView.findViewById(R.id.layout_edit);
            layout_delete=(LinearLayout)itemView.findViewById(R.id.layout_delete);
            layout_call_summary=(LinearLayout)itemView.findViewById(R.id.layout_call_sumaary);
            layout_date=(LinearLayout)itemView.findViewById(R.id.layout_date);
            layout_call_summary_title=(LinearLayout)itemView.findViewById(R.id.layout_call_sumaary_title);
        }
    }
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            current_year = year;
            current_month = monthOfYear;
            current_day = dayOfMonth;
       /*     SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
            Date date=new Date(year-1900,monthOfYear,dayOfMonth);
            String text_date=simpleDateFormat.format(date);
            Log.i("text_date",text_date);

            editTextDate.setText(text_date);
            editTextDate.clearFocus();*/

            Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
            editTextDate.setText(Connection.getFormatedDate(date));
            editTextDate.clearFocus();
        }
    };
    private class EditOppCallTask extends AsyncTask<String,Void,String>
    {
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
            String opportunityId = params[1];
            String callId=params[2];
            String callDate = params[3];
            String callSummary = params[4];
            String companyId = params[5];
            String responsibleId=params[6];

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("opportunity_id", opportunityId);
                jsonObject.put("call_id",callId);
                jsonObject.put("date", callDate);
                jsonObject.put("call_summary", callSummary);
                jsonObject.put("company_id", companyId);
                jsonObject.put("resp_staff_id", responsibleId);

                SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String edit_url;
                if (text_url != null) {
                    edit_url = text_url + "/user/edit_opportunity_call?token=";
                } else {
                    edit_url = Appconfig.EDIT_OPPPORTUNITY_CALL_URL;
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
                Log.d("jsonobject lines", jsonObject.toString());
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
                    String line ="";
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
            if (result.equals("success"))
            {            getOpportunityCall(opportunity_id);

                Toast.makeText(context,"Updated Succesfully!",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(context,"Item not updated! Try Again",Toast.LENGTH_LONG).show();
            }
        }
    }
    private void getOpportunityCall(String opportunityId,String callId) {
        SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/opportunity_call?token=";
        } else {
            get_url = Appconfig.DETAILS_OPPPORTUNITY_CALL_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+Appconfig.TOKEN+"&opportunity_id="+opportunityId+"&call_id="+callId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("response--",response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("calls");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject object=jsonArray.getJSONObject(i);
                                editTextDate.setText(object.getString("date"));
                                editText_call_summary.setText(object.getString("call_summary"));
                                company.setText(object.getString("company"));
                                responsible.setText(object.getString("responsible"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);

                        }

                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("response--", String.valueOf(error));
            }
        }) ;
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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, get_url+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            // Log.i("response--", String.valueOf(response));

                            JSONObject jsonObject=new JSONObject(response);
                            JSONObject pre_settings=jsonObject.getJSONObject("settings");
                            date_format=(pre_settings.getString("date_format"));

                            Log.i("date--",date_format);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);

                        }


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

                return params;
            }

        } ;
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);


    }

    private void getCompanyList(String token)
    {  SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/companies?token=";
        } else {
            get_url = Appconfig.COMPANY_URL;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        // Log.i("response--", String.valueOf(response));

                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("companies");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Company company=new Company();
                            company.setId(object.getInt("id"));
                            company.setName(object.getString("name"));
                            companyNameList.add(object.getString("name"));
                            companyArrayList.add(company);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);

                    }


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

            return params;
        }

    } ;
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);
    }

    private void getResponsibleStaffList(String token)
    {
        SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/staffs?token=";
        } else {
            get_url = Appconfig.STAFF_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        // Log.i("response--", String.valueOf(response));

                        JSONObject jsonObject=new JSONObject(response);
                        /*JSONArray jsonArray=jsonObject.getJSONArray("staffs");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Staff staff=new Staff();
                            staff.setId(object.getInt("id"));
                            staff.setName(object.getString("full_name"));
                            responsibleList.add(object.getString("full_name"));
                            responsibleStaffList.add(staff);
                        }*/

                        JSONObject  jsonObject1=jsonObject.getJSONObject("staffs");
                        Iterator<String> iter = jsonObject1.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();

                            try {
                                Object value = jsonObject1.get(key);
                                JSONObject jsonObject2= (JSONObject) jsonObject1.get(key);
                                Staff staff =new Staff();
                                staff.setName(jsonObject2.getString("full_name"));
                                staff.setId(jsonObject2.getInt("id"));
                                responsibleList.add(jsonObject2.getString("full_name"));
                                responsibleStaffList.add(staff);

                            } catch (JSONException e) {
                                Crashlytics.logException(e);

                            }}

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);

                    }


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

            return params;
        }

    } ;
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);


    }

    private class DeleteOpportunityCall extends AsyncTask<String,Void,String>
    {
        // ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            String response="",jsonresponse="";
            JSONObject json;
            BufferedReader bufferedReader;
            String tok=params[0];
            String call_id=params[1];
            URL url;
            try {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("call_id",call_id);
                SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String delete_url;
                if (text_url != null) {
                    delete_url = text_url + "/user/delete_opportunity_call?token=";
                } else {
                    delete_url = Appconfig.DELETE_OPPORTUNITY_CALL_URL;
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
                int responseCode=conn.getResponseCode();
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

                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    Log.i("response",response);
                    json = new JSONObject(response);
                    jsonresponse=json.getString("error");
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
            checkResponse=result;
            if (result!=null)
            {
                if (result.equals("success"))
                {

                    oppCalls.remove(callIdPosition);
                    notifyItemRemoved(callIdPosition);
                    notifyItemRangeChanged(callIdPosition,oppCalls.size());

                    Log.i("Res--",result);
                }
                if (result.equals("not_valid_data"))
                {
                    Log.i("message--","not deleted");
                }
            }

        }

    }
    public void setDialog(Dialog dialog) {
        mdialog = dialog;
    }
    private void getOpportunityCall(final String opportunityId) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Loading, please wait");
        dialog.setTitle("Connecting server");
        dialog.show();
        dialog.setCancelable(false);

        SharedPreferences preferences = context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/opportunity_calls?token=";
        } else {
            get_url = Appconfig.OPPORTUNITY_CALL_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+ Appconfig.TOKEN + "&opportunity_id=" + opportunityId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("calls");
                            oppCalls.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                OppCall oppCall = new OppCall();
                                oppCall.setDate(object.getString("date"));
                                oppCall.setCall_summary(object.getString("call_summary"));
                                oppCall.setCompany(object.getString("company"));
                                oppCall.setResponsible(object.getString("responsible"));
                                oppCall.setCall_id(object.getInt("id"));
                                oppCalls.add(oppCall);
                            }
                            notifyItemChanged(callIdPosition);
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
