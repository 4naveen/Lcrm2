package com.project.naveen.lcrm.menu.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.naveen.lcrm.AppSession;
import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.RecyclerTouchListener;
import com.project.naveen.lcrm.adapters.TasksAdapter;
import com.project.naveen.lcrm.models.Staff;
import com.project.naveen.lcrm.models.Tasks;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskFragment extends Fragment {
    MaterialSearchView searchView;
    public static int  current_year,current_month,current_day;
    SimpleDateFormat simpleDateFormat;
    public static Dialog mdialog;
    RecyclerView rv;
    String token;
    View v;
    TasksAdapter mAdapter;
    EditText date,description;
    BetterSpinner user;
    FrameLayout frameLayout;
    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_task, container, false);
        setHasOptionsMenu(true);
        AppSession.tasksArrayList =new ArrayList<>();
        frameLayout=(FrameLayout)v.findViewById(R.id.layout);
        token= Appconfig.TOKEN;
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("My Tasks");
        }
        //Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        searchView = (MaterialSearchView)getActivity().findViewById(R.id.search_view);
        rv=(RecyclerView)v.findViewById(R.id.rv);
        current_year = Calendar.getInstance().get(Calendar.YEAR);
        current_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        current_month = Calendar.getInstance().get(Calendar.MONTH);

        new GetAllTask().execute(Appconfig.TOKEN);
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent i=new Intent(getActivity(),AddLeadActivity.class);
                getActivity().startActivity(i);*/
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.task_add_dialog);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                date=(EditText)dialog.findViewById(R.id.deadline);

                user=(BetterSpinner)dialog.findViewById(R.id.user);
                description=(EditText)dialog.findViewById(R.id.description);
                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
                       // getActivity().showDialog(DATE_PICKER_ID);
                        DatePickerDialog d = new DatePickerDialog(getActivity(), mDateSetListener, current_year, current_month, current_day);
                        d.show();

                    }
                });

                date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        InputMethodManager imm = (InputMethodManager)getActivity(). getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
                        // getActivity().showDialog(DATE_PICKER_ID);
                        DatePickerDialog d = new DatePickerDialog(getActivity(), mDateSetListener, current_year, current_month, current_day);
                        d.show();
                    }
                });
                ArrayAdapter<String> companyArrayAdapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,AppSession.sales_personNameList);
                companyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                user.setAdapter(companyArrayAdapter);

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
                        int userId = AppSession.salesPersonList.get(AppSession.sales_personNameList.indexOf(user.getText().toString())).getId();
                        new AddTask().execute(Appconfig.TOKEN,String.valueOf(userId),description.getText().toString(),date.getText().toString());
                        dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                dialog.setTitle("New task");
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.75);
                int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.50);
                dialog.getWindow().setLayout(width, height);
                setDialog(dialog);
                dialog.show();
            }
        });

        return v;
    }


    public void setDialog(Dialog dialog) {
        mdialog = dialog;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_staff,menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        searchView.setMenuItem(menuItem);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Tasks> subtaskArrayList=new ArrayList<>();
                Log.i("ontypecomplete", newText);
                Log.i("list data--", String.valueOf(AppSession.tasksArrayList.size()));
                for (int i=0;i<AppSession.tasksArrayList.size();i++)
                {
                    if (AppSession.tasksArrayList.get(i).getDescription().contains(newText))
                    {
                        Tasks tasks=new Tasks();
                        tasks.setDescription(AppSession.tasksArrayList.get(i).getDescription());
                        tasks.setDeadline(AppSession.tasksArrayList.get(i).getDeadline());
                        tasks.setDeadline(AppSession.tasksArrayList.get(i).getUser());
                        subtaskArrayList.add(tasks);
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                rv.setAdapter(new TasksAdapter(getActivity(),subtaskArrayList));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            current_year = year;
            current_month = monthOfYear;
            current_day = dayOfMonth;
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace('Y', 'y').replace('m', 'M'), Locale.ENGLISH);
            Date current_date = new Date(year - 1900, monthOfYear, dayOfMonth);
            String text_date = simpleDateFormat.format(current_date);
            date.setText(text_date);
            date.clearFocus();
        }
    };

    class GetAllTask extends AsyncTask<String,Void,String>
    {   String response;
        ProgressDialog dialog;

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
            URL url;
            HttpURLConnection connection;
            try {
                url = new URL(Appconfig.TASK_URL+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();
                //  Log.i("response in d",response);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            dialog.dismiss();
            try {
                Log.i("response--",response);
                JSONObject jsonObject=new JSONObject(response);
                JSONArray jsonArray=jsonObject.getJSONArray("tasks");
                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject object=jsonArray.getJSONObject(i);
                    Tasks tasks=new Tasks();
                    tasks.setId(object.getInt("id"));
                    tasks.setUser(object.getString("task_from"));
                    tasks.setDescription(object.getString("task_description"));
                    tasks.setDeadline(object.getString("task_deadline"));
                    AppSession.tasksArrayList.add(tasks);
                    //  Log.i("leadslist--",lead.getName());
                }

                mAdapter = new TasksAdapter(getActivity(),AppSession.tasksArrayList);
                mAdapter.setMode(Attributes.Mode.Single);
                rv.setAdapter(mAdapter);
                rv.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
                RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
                rv.setLayoutManager(lmanager);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }}

    class AddTask extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
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
            String user_id = params[1];
            String description =params[2] ;
            String deadline =params[3] ;

            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", user_id);
                jsonObject.put("task_description", description);
                jsonObject.put("task_deadline", deadline);


                url = new URL(Appconfig.POST_TASK_URL+tok);
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

            if (result.equals("success"))
            {
                final Snackbar snackbar = Snackbar.make(frameLayout, "Added 1 item Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
            else {
                final Snackbar snackbar = Snackbar.make(frameLayout, "Item not added! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
            dialog.dismiss();

        }
    }
}
