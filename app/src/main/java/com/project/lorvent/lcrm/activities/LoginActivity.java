package com.project.lorvent.lcrm.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.home.Home1Activity;
import com.project.lorvent.lcrm.activities.home.Home2Activity;
import com.project.lorvent.lcrm.activities.home.HomeActivity;
import com.project.lorvent.lcrm.models.OppLeads;
import com.project.lorvent.lcrm.models.customer.InvoiceByMonth;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.LogoutService;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.project.lorvent.lcrm.R.id.token;

public class LoginActivity extends AppCompatActivity {
    EditText email, pwd, forget_email;
    Button login;
    TextInputLayout input_email, input_pwd, input_forget_email;
    String first_namev, last_namev, user_emailv, phonev, rolev, image_url, date_format;
    String tokenv;
    ProgressDialog progressDialog;
    String salesteam_read, salesteam_write, salesteam_delete, lead_read, lead_write, lead_delete, opp_read, opp_write, opp_delete, loggedcall_read,
            loggedcall_write, loggedcall_delete, meeting_read, meeting_write, meeting_delete, products_read, products_write, products_delete,
            quotation_read, quotation_write, quotation_delete, salesorder_read, salesorder_write, salesorder_delete, invoices_read, invoices_write, invoices_delete,
            contracts_read, contracts_write, contracts_delete, staff_read, staff_write, staff_delete, contacts_read, contacts_write, contacts_delete;
    ArrayList<String> permissions;
    SharedPreferences preferences,preferences1;
    SharedPreferences.Editor editor;
    CheckBox remember;
    LinearLayout linearLayout;
    TextView forget_password;
    public ArrayList<Integer> dashboardArrayList1;
    public ArrayList<OppLeads> dashboardArrayList2;
    public ArrayList<InvoiceByMonth> byMonthArrayList;
    ImageView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        linearLayout = (LinearLayout) findViewById(R.id.layout);
        AppSession.dashboardArrayList1 = new ArrayList<>();
        AppSession.dashboardArrayList2 = new ArrayList<>();
        AppSession.byMonthArrayList = new ArrayList<>();
        email = (EditText) findViewById(R.id.email);
        pwd = (EditText) findViewById(R.id.password);
        remember = (CheckBox) findViewById(R.id.remember);
        info = (ImageView) findViewById(R.id.info);
        salesteam_read = "false";
        salesteam_write = "false";
        salesteam_delete = "false";
        lead_read = "false";
        lead_write = "false";
        lead_delete = "false";
        opp_read = "false";
        opp_write = "false";
        opp_delete = "false";
        loggedcall_read = "false";
        loggedcall_write = "false";
        loggedcall_delete = "false";
        meeting_read = "false";
        meeting_write = "false";
        meeting_delete = "false";
        products_read = "false";
        products_write = "false";
        products_delete = "false";
        quotation_read = "false";
        quotation_write = "false";
        quotation_delete = "false";
        salesorder_read = "false";
        salesorder_write = "false";
        salesorder_delete = "false";
        invoices_read = "false";
        invoices_write = "false";
        invoices_delete = "false";
        contracts_read = "false";
        contracts_write = "false";
        contracts_delete = "false";
        staff_read = "false";
        staff_write = "false";
        staff_delete = "false";
        contacts_read = "false";
        contacts_write = "false";
        contacts_delete = "false";
        input_email = (TextInputLayout) findViewById(R.id.input_email);
        input_pwd = (TextInputLayout) findViewById(R.id.input_pass);

        changeStatusBarColor();
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(LoginActivity.this)
                        .title("Login Details!")
                        .content("Admin\n" +
                                "email: admin@admin.com password: lorvent\n" + "Customer\n" + "email:customer123@gmail.com password:lorvent\n" + "Staff\n" + "email:staff123@gmail.com password:lorvent\n\n" +
                                "Recommended H/w configuration:\n" + "Android 5.0v and above\n" + "5'inch device size and above")
                        .positiveText("Ok")
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            }
                        })
                        .show();

            }
        });
        login = (Button) findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (email.getText().toString().isEmpty() || !isValidEmail(email.getText().toString()) || pwd.getText().toString().isEmpty()) {
                    email.requestFocus();

                    if (email.getText().toString().isEmpty()) {
                        email.requestFocus();
                        input_email.setError("please enter your email");
                        return;
                    }
                    if (!isValidEmail(email.getText().toString())) {
                        input_email.setError("Please enter valid  email");
                        return;
                    }
                    if (pwd.getText().toString().isEmpty()) {
                        pwd.requestFocus();
                        input_pwd.setError("please enter your password");
                        return;
                    }

                }

                if (!isConnected()) {
                    Intent i = new Intent(LoginActivity.this, NetworkErrorActivity.class);
                    startActivity(i);
                }

                if (isConnected()) {

                    loginUser();
                }
                if (remember.isChecked()) {
                    preferences = getSharedPreferences("login", MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.putString("email", email.getText().toString());
                    editor.putString("password", pwd.getText().toString());
                    editor.commit();
                }

            }
        });
        forget_password = (TextView) findViewById(R.id.forget);
        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog1 = new MaterialDialog.Builder(LoginActivity.this)
                        .title("Password Recovery")
                        .customView(R.layout.forget_password_dialog, true)
                        .positiveText("RECOVER PASSWORD")
                        .autoDismiss(false)
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (forget_email.getText().toString().isEmpty() || !isValidEmail(forget_email.getText().toString())) {
                                    if (forget_email.getText().toString().isEmpty()) {
                                        input_forget_email.setError("please enter your email");
                                        return;
                                    }
                                    if (!isValidEmail(forget_email.getText().toString())) {
                                        input_forget_email.setError("Please enter valid  email");
                                        return;
                                    }
                                } else {
                                    dialog.dismiss();
                                }

                                getToken();

                            }
                        })
                        .show();
                View view = dialog1.getCustomView();
                if (view != null) {
                    forget_email = (EditText) dialog1.getCustomView().findViewById(R.id.email);
                    input_forget_email = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_forget_email);
                    forget_email.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_forget_email.setError("");
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                        }
                    });

                }
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_email.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_pwd.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        SharedPreferences preferences1 = getSharedPreferences("login", MODE_PRIVATE);
        email.setText(preferences1.getString("email", ""));
        pwd.setText(preferences1.getString("password", ""));
    }

    private void loginUser() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(false);
        permissions = new ArrayList<>();
         preferences1 = getSharedPreferences("pref", MODE_PRIVATE);
        String url = preferences1.getString("url", null);
        String login_url;
        if (url != null) {
            login_url = url + "/login";
        } else {
            login_url = Appconfig.LOGIN_URL;
        }
        System.out.println("login_url"+login_url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(response);
                            tokenv = jsonobject.getString("token");
                            rolev = jsonobject.getString("role");
                            date_format = jsonobject.getString("date_format");
                            JSONObject user = jsonobject.getJSONObject("user");
                            first_namev = user.getString("first_name");
                            last_namev = user.getString("last_name");
                            user_emailv = user.getString("email");
                            phonev = user.getString("phone_number");
                            image_url = user.getString("avatar");

                            if (jsonobject.getString("role").equalsIgnoreCase("staff")) {
                                JSONObject permission = jsonobject.getJSONObject("permissions");
                                Iterator<String> iter = permission.keys();
                           /*     Toast.makeText(getApplicationContext(),"url-"+Appconfig.LOGIN_URL,Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(),"url-"+response,Toast.LENGTH_LONG).show();*/
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    permissions.add(key);
                                }
                                for (int i = 0; i < permissions.size(); i++) {
                                    if (permissions.get(i).equals("sales_team.read")) {
                                        salesteam_read = "true";
                                        //Log.i("sales read--", salesteam_read);

                                    } else if (permissions.get(i).equals("sales_team.write")) {
                                        salesteam_write = "true";
                                        //Log.i("sales write--", salesteam_write);

                                    } else if (permissions.get(i).equals("sales_team.delete")) {
                                        salesteam_delete = "true";
                                        //Log.i("sales delete--", salesteam_delete);

                                    } else if (permissions.get(i).equals("leads.read")) {
                                        lead_read = "true";

                                    } else if (permissions.get(i).equals("leads.write")) {
                                        lead_write = "true";

                                    } else if (permissions.get(i).equals("leads.delete")) {
                                        lead_delete = "true";

                                    } else if (permissions.get(i).equals("opportunities.read")) {
                                        opp_read = "true";

                                    } else if (permissions.get(i).equals("opportunities.write")) {
                                        opp_write = "true";

                                    } else if (permissions.get(i).equals("opportunities.delete")) {
                                        opp_delete = "true";

                                    } else if (permissions.get(i).equals("logged_calls.read")) {
                                        loggedcall_read = "true";

                                    } else if (permissions.get(i).equals("logged_calls.write")) {
                                        loggedcall_write = "true";

                                    } else if (permissions.get(i).equals("logged_calls.delete")) {
                                        loggedcall_delete = "true";

                                    } else if (permissions.get(i).equals("meetings.read")) {
                                        meeting_read = "true";

                                    } else if (permissions.get(i).equals("meetings.write")) {
                                        meeting_write = "true";

                                    } else if (permissions.get(i).equals("meetings.delete")) {
                                        meeting_delete = "true";
                                    } else if (permissions.get(i).equals("products.read")) {
                                        products_read = "true";

                                    } else if (permissions.get(i).equals("products.write")) {
                                        products_write = "true";

                                    } else if (permissions.get(i).equals("products.delete")) {
                                        products_delete = "true";

                                    } else if (permissions.get(i).equals("quotations.read")) {
                                        quotation_read = "true";

                                    } else if (permissions.get(i).equals("quotations.write")) {
                                        quotation_write = "true";

                                    } else if (permissions.get(i).equals("quotations.delete")) {
                                        quotation_delete = "true";

                                    } else if (permissions.get(i).equals("sales_orders.read")) {
                                        salesorder_read = "true";

                                    } else if (permissions.get(i).equals("sales_orders.write")) {
                                        salesteam_write = "true";

                                    } else if (permissions.get(i).equals("sales_orders.delete")) {
                                        salesorder_delete = "true";

                                    } else if (permissions.get(i).equals("invoices.read")) {
                                        invoices_read = String.valueOf(permission.getBoolean("invoices.read"));

                                    } else if (permissions.get(i).equals("invoices.write")) {
                                        invoices_write = "true";

                                    } else if (permissions.get(i).equals("invoices.delete")) {
                                        invoices_delete = "true";

                                    } else if (permissions.get(i).equals("contracts.read")) {
                                        contacts_read = "true";

                                    } else if (permissions.get(i).equals("contracts.write")) {
                                        contracts_write = "true";

                                    } else if (permissions.get(i).equals("contracts.delete")) {
                                        contracts_delete = "true";
                                    } else if (permissions.get(i).equals("staff.read")) {
                                        staff_read = "true";
                                    } else if (permissions.get(i).equals("staff.write")) {
                                        staff_write = "true";

                                    } else if (permissions.get(i).equals("staff.delete")) {
                                        staff_delete = "true";

                                    } else if (permissions.get(i).equals("contacts.read")) {
                                        contacts_read = "true";

                                    } else if (permissions.get(i).equals("contacts.write")) {
                                        contracts_write = "true";

                                    } else if (permissions.get(i).equals("contacts.delete")) {
                                        contacts_delete = "true";
                                    } else {
                                        //do nothing
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Appconfig.TOKEN = tokenv;
                        AppSession.first_name = first_namev;
                        AppSession.last_name = last_namev;
                        AppSession.user_email = user_emailv;
                        AppSession.phone = phonev;
                        AppSession.role = rolev;
                        AppSession.date_format1 = date_format;
                        AppSession.image_url = image_url;

                        if (rolev.equals("admin")) {
                            getDashboard(Appconfig.TOKEN, "admin");


                        }
                        if (rolev.equals("customer")) {
                            getDashboardCustomer(Appconfig.TOKEN);

                           /* Intent i = new Intent(LoginActivity.this, Home1Activity.class);
                            startActivity(i);*/
                        }
                        if (rolev.equals("staff")) {
                            getDashboard(Appconfig.TOKEN, "staff");
                        }
                        progressDialog.dismiss();
                        Intent i = new Intent(LoginActivity.this, LogoutService.class);
                        startService(i);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (String.valueOf(error) != null) {
                    new MaterialDialog.Builder(LoginActivity.this)
                            .content("Invalid Credentials!Please try again")
                            .positiveText("Ok")
                            .positiveColorRes(R.color.colorPrimary)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .show();
                    progressDialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email.getText().toString());
                params.put("password", pwd.getText().toString());

                return params;
            }

       /*    @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String>header=new HashMap<>();
                header.put("Content-Type", "application/json");

               return header;
            }*/

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyVolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(stringRequest);

    }

    private void getToken() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Appconfig.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(response);
                            String token = jsonobject.getString("token");
                            new Password_recovery().execute(token, forget_email.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (String.valueOf(error) != null) {
                    progressDialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", "admin@admin.com");
                params.put("password", "lorvent");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyVolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(stringRequest);

    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // window.setStatusBarColor(Color.rgb(104, 159, 56));
            //  window.setStatusBarColor(Color.rgb(105, 168,241));
            window.setStatusBarColor(Color.rgb(44, 68, 102));
        }
    }

    public boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;

        }
    }

    public boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void getDashboard(String token, final String role) {
        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/dashboard?token=";
        } else {
            get_url = Appconfig.DASHBOARD_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+ token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dashboardArrayList1 = new ArrayList<>();
                            dashboardArrayList2 = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            dashboardArrayList1.add(jsonObject.getInt("contracts"));
                            dashboardArrayList1.add(jsonObject.getInt("products"));
                            dashboardArrayList1.add(jsonObject.getInt("opportunities"));
                            dashboardArrayList1.add(jsonObject.getInt("customers"));

                            JSONArray jsonArray = jsonObject.getJSONArray("opportunity_leads");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                OppLeads oppLeads = new OppLeads();
                                JSONObject object = jsonArray.getJSONObject(i);
                                oppLeads.setMonth(object.getString("month"));
                                oppLeads.setYear(object.getString("year"));
                                oppLeads.setOpp(object.getInt("opportunity"));
                                oppLeads.setLeads(object.getInt("leads"));
                                dashboardArrayList2.add(oppLeads);
                            }
                            AppSession.dashboardArrayList1.clear();
                            AppSession.dashboardArrayList2.clear();
                            AppSession.dashboardArrayList1.addAll(dashboardArrayList1);
                            AppSession.dashboardArrayList2.addAll(dashboardArrayList2);

                            if (role.equals("admin")) {
                                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(i);
                            }
                            if (role.equals("staff")) {
                                Intent i = new Intent(LoginActivity.this, Home2Activity.class);
                                i.putExtra("calling from", "login");
                                i.putExtra("salesteam_read", salesteam_read);
                                i.putExtra("salesteam_write", salesteam_write);
                                i.putExtra("salesteam_delete", salesteam_delete);
                                i.putExtra("leads_read", lead_read);
                                i.putExtra("leads_write", lead_write);
                                i.putExtra("leads_delete", lead_delete);
                                i.putExtra("opp_read", opp_read);
                                i.putExtra("opp_write", opp_write);
                                i.putExtra("opp_delete", opp_delete);
                                i.putExtra("loggedcall_read", loggedcall_read);
                                i.putExtra("loggedcall", loggedcall_write);
                                i.putExtra("loggedcall", loggedcall_delete);
                                i.putExtra("meeting_read", meeting_read);
                                i.putExtra("meeting_write", meeting_write);
                                i.putExtra("meeting_delete", meeting_delete);
                                i.putExtra("products_read", products_read);
                                i.putExtra("products_write", products_write);
                                i.putExtra("products_delete", products_delete);
                                i.putExtra("quotation_read", quotation_read);
                                i.putExtra("quotation_write", quotation_write);
                                i.putExtra("quotation_delete", quotation_delete);
                                i.putExtra("salesorder_read", salesorder_read);
                                i.putExtra("salesorder_write", salesorder_write);
                                i.putExtra("salesorder_delete", salesorder_delete);
                                i.putExtra("invoices_read", invoices_read);
                                i.putExtra("invoices_write", invoices_write);
                                i.putExtra("invoices_delete", invoices_delete);
                                i.putExtra("contracts_read", contracts_read);
                                i.putExtra("contracts_write", contracts_write);
                                i.putExtra("contracts_delete", contracts_delete);
                                i.putExtra("staff_read", staff_read);
                                i.putExtra("staff_write", staff_write);
                                i.putExtra("staff_delete", staff_delete);
                                i.putExtra("contacts_read", contacts_read);
                                i.putExtra("contacts_write", contacts_write);
                                i.putExtra("contacts_delete", contacts_delete);
                                startActivity(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        MyVolleySingleton.getInstance(LoginActivity.this).getRequestQueue().add(stringRequest);

    }

    public void getDashboardCustomer(String token) {
        SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/customer/dashboard?token=";
        } else {
            get_url = Appconfig.DASHBOARD_CUSTOMER_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+ token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            byMonthArrayList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("invoices_by_month");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                InvoiceByMonth invoiceByMonth = new InvoiceByMonth();
                                JSONObject object = jsonArray.getJSONObject(i);
                                invoiceByMonth.setMonth(object.getString("month"));
                                invoiceByMonth.setYear(object.getString("year"));
                                invoiceByMonth.setOpportunity(object.getInt("opportunity"));
                                invoiceByMonth.setLeads(object.getInt("leads"));
                                invoiceByMonth.setContracts(object.getInt("contracts"));
                                invoiceByMonth.setInvoices(object.getInt("invoices"));
                                byMonthArrayList.add(invoiceByMonth);

                            }
                            for (InvoiceByMonth byMonth : byMonthArrayList) {
                                //  Log.i("contracts", String.valueOf(byMonth.getInvoices()));
                                Log.i("contracts", String.valueOf(byMonth.getOpportunity()));
                                Log.i("contracts", String.valueOf(byMonth.getLeads()));

                            }
                            AppSession.byMonthArrayList.clear();
                            AppSession.byMonthArrayList.addAll(byMonthArrayList);
                            Intent i = new Intent(LoginActivity.this, Home1Activity.class);
                            startActivity(i);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        };
        MyVolleySingleton.getInstance(LoginActivity.this).getRequestQueue().add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(LoginActivity.this, LogoutService.class);
        stopService(i);
        finish();
    }

    private class Password_recovery extends AsyncTask<String, Void, String> {
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
            String email = params[1];
            URL url;
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", email);
                Log.i("json", tok);

                SharedPreferences preferences = getSharedPreferences("pref", MODE_PRIVATE);
                String text_url = preferences.getString("url", null);
                String pwd_recovery_url;
                if (text_url != null) {
                    pwd_recovery_url = text_url + "/password_recovery?token=";
                } else {
                    pwd_recovery_url = Appconfig.PASSWORD_RECOVERY_URL;
                }

                url = new URL(pwd_recovery_url+ tok);
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
            progressDialog.dismiss();
            if (result != null) {
                if (result.equals("success")) {
                    Toast.makeText(getApplicationContext(), "we have sent reset link to your email", Toast.LENGTH_LONG).show();
                }
                if (result.equals("not_valid_data")) {
                    Toast.makeText(getApplicationContext(), "error occured!Try Again", Toast.LENGTH_LONG).show();
                }
            }

        }

    }


}
