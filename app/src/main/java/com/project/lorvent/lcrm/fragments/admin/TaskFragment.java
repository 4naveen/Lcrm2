package com.project.lorvent.lcrm.fragments.admin;


import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.swipe.util.Attributes;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.adapters.TasksAdapter;
import com.project.lorvent.lcrm.models.Tasks;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.NetworkStatus;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

import static android.content.Context.MODE_PRIVATE;

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
    Configuration config;
    ProgressDialog dialog;
    private boolean helpDisplayed = false;
    private static final String PREF_FIRSTLAUNCH_HELP = "helpDisplayed";
    TextInputLayout input_description, input_deadline,input_user;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_task, container, false);
        setHasOptionsMenu(true);

        Connection.getDateSettings(Appconfig.TOKEN,getActivity());
        Connection.getStaffList(Appconfig.TOKEN,getActivity());

        AppSession.tasksArrayList =new ArrayList<>();
        frameLayout=(FrameLayout)v.findViewById(R.id.layout);
        token= Appconfig.TOKEN;
        config = getResources().getConfiguration();

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
        if (!NetworkStatus.isConnected(getActivity())){
            new MaterialDialog.Builder(getActivity())
                    .title("Please check your internet Connectivity !")
                    .titleColor(Color.RED)
                    .positiveText("Ok")
                    .positiveColorRes(R.color.colorPrimary)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        }
                    })
                    .negativeColorRes(R.color.colorPrimary)
                    .negativeText("")
                    .show();
        }
        if (NetworkStatus.isConnected(getActivity())){
            new GetAllTask().execute(Appconfig.TOKEN);
        }
        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                MaterialDialog dialog1=new MaterialDialog.Builder(getActivity())
                        .title("New task")
                        .customView(R.layout.task_add_dialog, true)
                        .positiveText("ADD")
                        .autoDismiss(false)
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (description.getText().toString().isEmpty()) {
                                    input_description.setError("please enter description");
                                    return;
                                }
                                else if (date.getText().toString().isEmpty()) {
                                    input_deadline.setError("please enter date");
                                    return;
                                }
                                else if (user.getText().toString().isEmpty()) {
                                    input_user.setError("please select user");
                                    return;
                                }
                                else {
                                    dialog.dismiss();
                                }
                                int userId=AppSession.responsibleList.get(AppSession.responsibleNameList.indexOf(user.getText().toString())).getId();
                                new AddTask().execute(Appconfig.TOKEN,String.valueOf(userId),description.getText().toString(),date.getText().toString());

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
                    date=(EditText)dialog1.getCustomView().findViewById(R.id.deadline);
                    user=(BetterSpinner)dialog1.getCustomView().findViewById(R.id.user);
                    description=(EditText)dialog1.getCustomView().findViewById(R.id.description);
                    input_description = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_description);
                    input_deadline = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_deadline);
                    date.setShowSoftInputOnFocus(false);

                    input_user = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_user);
                    user.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            input_user.setError("");
                        }
                    });
                    description.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_description.setError("");

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    date.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_deadline.setError("");

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
                            // getActivity().showDialog(DATE_PICKER_ID);
                            DatePickerDialog d = new DatePickerDialog(getActivity(), mDateSetListener, current_year, current_month, current_day);
                            d.show();

                        }
                    });

                }

                ArrayAdapter<String> companyArrayAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_item,AppSession.responsibleNameList);
                companyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                user.setAdapter(companyArrayAdapter);
            }
        });
        if (v.findViewById(R.id.fab).getVisibility()==View.VISIBLE)
        {showHelpForFirstLaunch();}
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        NavigationView navigationView=(NavigationView) getActivity().findViewById(R.id.nav_view);
                        navigationView.getMenu().findItem(R.id.dashboard).setChecked(true);
                        Fragment fragment1 = new DashboardFragment();
                        FragmentTransaction trans1 = getFragmentManager().beginTransaction();
                        trans1.replace(R.id.frame, fragment1);
                        trans1.addToBackStack(null);
                        trans1.commit();
                        return true;
                    }
                }
                return false;
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
            Date date1 = new Date(year - 1900, monthOfYear, dayOfMonth);
            date.setText(Connection.getFormatedDate(date1));
            date.clearFocus();
        }
    };

    class GetAllTask extends AsyncTask<String,Void,String>
    {   String response;

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
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String get_url;
                if (text_url != null) {
                    get_url = text_url + "/user/tasks?token=";
                } else {
                    get_url = Appconfig.TASK_URL;
                }
                url = new URL(get_url+params[0]);
                connection = (HttpURLConnection) url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder buffer = new StringBuilder();
                String temp;
                while ((temp=br.readLine())!=null)
                {
                    buffer.append(temp);
                }
                response=buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            if (dialog!=null&&dialog.isShowing()){dialog.dismiss();}
            try {
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
            BufferedReader bufferedReader;
            JSONObject json;
            JSONObject jsonObject;
            String tok = params[0];
            String user_id = params[1];
            String description =params[2] ;
            String deadline =params[3] ;

            URL url;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_id", user_id);
                jsonObject.put("task_description", description);
                jsonObject.put("task_deadline", deadline);
                SharedPreferences preferences =getActivity().getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String post_url;
                if (text_url != null) {
                    post_url = text_url + "/user/post_task?token=";
                } else {
                    post_url = Appconfig.POST_TASK_URL;
                }

                url = new URL(post_url+tok);
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
                    while ((line = br.readLine()) != null) {
                        response += line;
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
                    }
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
    @Override
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
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
        SharedPreferences preferences = getActivity().getSharedPreferences("pref1",MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }

    private void savePreference(String key, boolean value) {
        SharedPreferences preferences = getActivity().getSharedPreferences("pref1",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    private void showOverLay(){
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.overlay_view);
        LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overlayLayout);
        ImageView image=(ImageView)dialog.findViewById(R.id.imageView1);
        image.setImageResource(R.drawable.overlay2);
        layout.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View arg0) {
                dialog.dismiss();
            }

        });
        dialog.show();

    }
}
