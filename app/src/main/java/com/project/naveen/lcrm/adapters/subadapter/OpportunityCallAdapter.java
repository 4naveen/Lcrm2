package com.project.naveen.lcrm.adapters.subadapter;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.MyVolleySingleton;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.models.Company;
import com.project.naveen.lcrm.models.Staff;
import com.project.naveen.lcrm.models.submodels.OppCall;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;


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

        final OppCall oppCall=oppCalls.get(position);
         call_id=oppCall.getCall_id();
        Log.i("callid-", String.valueOf(call_id));
        for (int i = 0; i <oppCalls.size() ; i++) {
            System.out.println("opplist--"+oppCalls.get(i));
        }

        getCompanyList(Appconfig.TOKEN);
        getResponsibleStaffList(Appconfig.TOKEN);
        svHolder=viewHolder;
        callIdPosition=viewHolder.getLayoutPosition();
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

        viewHolder.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.opp_call_edit_dialog);
                editTextDate=(EditText)dialog.findViewById(R.id.date);
                getDateSettings(Appconfig.TOKEN);
                getOpportunityCall(opportunity_id,String.valueOf(call_id));

                editText_call_summary=(EditText)dialog.findViewById(R.id.call_sumaary);
                company=(BetterSpinner)dialog.findViewById(R.id.company);
                responsible=(BetterSpinner)dialog.findViewById(R.id.responsible);
                editTextDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editTextDate.getWindowToken(), 0);
                        DatePickerDialog d = new DatePickerDialog(context, mDateSetListener, current_year, current_month, current_day);
                        d.show();
                       /* String current_month_text=spf.getString("current_month_text","");
                        int current_day=spf.getInt("current_day",0);
                        int current_year=spf.getInt("current_year",0);*/
                        Log.i("date3--",current_month_text+""+current_day+""+current_year);

                    }
                });

                ArrayAdapter<String> companyArrayAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,companyNameList);
                companyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                company.setAdapter(companyArrayAdapter);
                ArrayAdapter<String> responsibleArrayAdapter=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,responsibleList);
                responsibleArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                responsible.setAdapter(responsibleArrayAdapter);

                Button cancel=(Button) dialog.findViewById(R.id.cancel);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                Button add=(Button) dialog.findViewById(R.id.submit);

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int companyId=companyArrayList.get(companyNameList.indexOf(company.getText().toString())).getId();
                        Log.i("companyId-", String.valueOf(companyId));
                        int responsibleId=responsibleStaffList.get(responsibleList.indexOf(responsible.getText().toString())).getId();
                        Log.i("responsibleId-", String.valueOf(responsibleId));

                        new EditOppCallTask().execute(token,opportunity_id,String.valueOf(call_id),editTextDate.getText().toString(),editText_call_summary.getText().toString(),
                                String.valueOf(companyId),String.valueOf(responsibleId));
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
                                new DeleteOpportunityCall().execute(token,String.valueOf(call_id));

                                sweetAlertDialog.cancel();

                            }
                        })
                        .show();

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
        LinearLayout layout_date;
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
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(date_format.replace('Y','y').replace('m','M'), Locale.ENGLISH);
            Date date=new Date(year-1900,monthOfYear,dayOfMonth);
            String text_date=simpleDateFormat.format(date);
            Log.i("text_date",text_date);

            editTextDate.setText(text_date);
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
                url = new URL(Appconfig.EDIT_OPPPORTUNITY_CALL_URL+tok);
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
            }
            return jsonresponse;
        }
        @Override
        protected void onPostExecute(String result) {

            dialog.dismiss();
            if (result.equals("success"))
            {
                /*//oppCalls.clear();
                getOpportunityCall(opportunity_id,String.valueOf(call_id));
                 Log.i("result--",result);*/
            }
        }
    }
    private void getOpportunityCall(String opportunityId,String callId) {
        Log.i("oppId--", String.valueOf(opportunityId));

        StringRequest stringRequest = new StringRequest(Request.Method.GET,Appconfig.DETAILS_OPPPORTUNITY_CALL_URL+Appconfig.TOKEN+"&opportunity_id="+opportunityId+"&call_id="+callId,
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

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.SETTINGS_URL+token,
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
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.COMPANY_URL+token,
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
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.STAFF_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        // Log.i("response--", String.valueOf(response));

                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("staffs");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Staff staff=new Staff();
                            staff.setId(object.getInt("id"));
                            staff.setName(object.getString("full_name"));
                            responsibleList.add(object.getString("full_name"));
                            responsibleStaffList.add(staff);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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

                url = new URL(Appconfig.DELETE_OPPORTUNITY_CALL_URL+tok);
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

}
