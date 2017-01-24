package com.project.naveen.lcrm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
   EditText email,pwd;
    Button login;
    TextInputLayout input_email,input_pwd;
    String first_namev,last_namev,user_emailv,phonev,rolev;
    String tokenv;
    ProgressDialog progressDialog;
    String salesteam_read,salesteam_write,salesteam_delete,lead_read,lead_write,lead_delete,opp_read,opp_write,opp_delete,loggedcall_read,
    loggedcall_write,loggedcall_delete,meeting_read,meeting_write,meeting_delete,products_read,products_write,products_delete,
    quotation_read,quotation_write,quotation_delete,salesorder_read,salesorder_write,salesorder_delete,invoices_read,invoices_write,invoices_delete,
    contracts_read,contracts_write,contracts_delete,staff_read,staff_write,staff_delete,contacts_read,contacts_write,contacts_delete;
    ArrayList<String>permissions;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
     CheckBox remember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            setContentView(R.layout.activity_login);

        }
        else
        {
            setContentView(R.layout.activity_login_mob);
        }
//        setContentView(R.layout.activity_login);
       // Log.i("salesread--",salesteam_read);
        email=(EditText)findViewById(R.id.email);
        pwd=(EditText)findViewById(R.id.password);
        remember=(CheckBox)findViewById(R.id.remember);
        salesteam_read="false";salesteam_write="false";salesteam_delete="false";lead_read="false";lead_write="false";
        lead_delete="false";opp_read="false";opp_write="false";opp_delete="false";loggedcall_read="false";loggedcall_write="false";
        loggedcall_delete="false";meeting_read="false";meeting_write="false";meeting_delete="false";products_read="false";products_write="false";
        products_delete="false";quotation_read="false";quotation_write="false";quotation_delete="false";salesorder_read="false";salesorder_write="false";
        salesorder_delete="false";invoices_read="false";invoices_write="false";invoices_delete="false";
        contracts_read="false";contracts_write="false";contracts_delete="false";staff_read="false";staff_write="false";
        staff_delete="false";contacts_read="false";contacts_write="false";contacts_delete="false";
        input_email=(TextInputLayout)findViewById(R.id.input_email);
        input_pwd=(TextInputLayout)findViewById(R.id.input_pass);
        changeStatusBarColor();

        login=(Button)findViewById(R.id.login);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 if (email.getText().toString().isEmpty()&&pwd.getText().toString().isEmpty())
                 {email.requestFocus();
                     if (email.getText().toString().isEmpty())
                     { email.requestFocus();
                         input_email.setError("please enter your  valid email or password");

                     }
                    if (pwd.getText().toString().isEmpty()){
                        pwd.requestFocus();
                        input_pwd.setError("please enter your  valid email or password");

                     }

                 }

                loginUser();

                if (remember.isChecked())
                {
                    preferences=getSharedPreferences("login",MODE_PRIVATE);
                    editor=preferences.edit();
                    editor.putString("email",email.getText().toString());
                    editor.putString("password",pwd.getText().toString());
                    editor.commit();
                }

            }
        });
        SharedPreferences preferences1=getSharedPreferences("login",MODE_PRIVATE);
        email.setText(preferences1.getString("email",""));
        pwd.setText(preferences1.getString("password",""));
    }

    private void loginUser() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(false);
        permissions=new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Appconfig.LOGIN_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonobject= null;
                            try {
                                jsonobject = new JSONObject(response);
                                tokenv=jsonobject.getString("token");
                                rolev=jsonobject.getString("role");
                                JSONObject user=jsonobject.getJSONObject("user");
                                first_namev=user.getString("first_name");
                                last_namev=user.getString("last_name");
                                user_emailv=user.getString("email");
                                phonev=user.getString("phone_number");
                                JSONObject permission=jsonobject.getJSONObject("permissions");
                                Iterator<String> iter=permission.keys();
                           /*     Toast.makeText(getApplicationContext(),"url-"+Appconfig.LOGIN_URL,Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(),"url-"+response,Toast.LENGTH_LONG).show();*/
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    permissions.add(key);
                                    Log.i("key value--",key);
                                }

                                for (int i=0;i<permissions.size();i++)
                                {
                                    if (permissions.get(i).equals("sales_team.read"))
                                    {
                                        salesteam_read ="true";
                                        Log.i("sales read--",salesteam_read);

                                    }
                                    else if (permissions.get(i).equals("sales_team.write"))
                                    {
                                        salesteam_write ="true";
                                        Log.i("sales write--",salesteam_write);

                                    }
                                    else if (permissions.get(i).equals("sales_team.delete"))
                                    {
                                        salesteam_delete ="true";
                                        Log.i("sales delete--",salesteam_delete);

                                    }
                                    else if (permissions.get(i).equals("leads.read"))
                                    {
                                        lead_read ="true";

                                    }
                                    else if (permissions.get(i).equals("leads.write"))
                                    {
                                        lead_write ="true";

                                    }
                                    else if (permissions.get(i).equals("leads.delete"))
                                    {
                                        lead_delete ="true";

                                    }
                                    else if (permissions.get(i).equals("opportunities.read"))
                                    {
                                        opp_read ="true";

                                    }
                                    else if (permissions.get(i).equals("opportunities.write"))
                                    {
                                        opp_write="true";

                                    }
                                    else if (permissions.get(i).equals("opportunities.delete"))
                                    {
                                        opp_delete="true";

                                    }
                                    else if (permissions.get(i).equals("logged_calls.read"))
                                    {
                                        loggedcall_read="true";

                                    }
                                    else if (permissions.get(i).equals("logged_calls.write"))
                                    {
                                        loggedcall_write ="true";

                                    }
                                    else if (permissions.get(i).equals("logged_calls.delete"))
                                    {
                                        loggedcall_delete ="true";

                                    }
                                    else if (permissions.get(i).equals("meetings.read"))
                                    {
                                        meeting_read="true";

                                    }
                                    else if (permissions.get(i).equals("meetings.write"))
                                    {
                                        meeting_write ="true";

                                    }
                                    else if (permissions.get(i).equals("meetings.delete"))
                                    {
                                        meeting_delete ="true";
                                    }
                                    else if (permissions.get(i).equals("products.read"))
                                    {
                                        products_read ="true";

                                    }
                                    else if (permissions.get(i).equals("products.write"))
                                    {
                                        products_write ="true";

                                    }
                                    else if (permissions.get(i).equals("products.delete"))
                                    {
                                        products_delete ="true";

                                    }
                                    else if (permissions.get(i).equals("quotations.read"))
                                    {
                                        quotation_read ="true";

                                    }
                                    else if (permissions.get(i).equals("quotations.write"))
                                    {
                                        quotation_write ="true";

                                    }
                                    else if (permissions.get(i).equals("quotations.delete"))
                                    {
                                        quotation_delete ="true";

                                    }
                                    else if (permissions.get(i).equals("sales_orders.read"))
                                    {
                                        salesorder_read="true";

                                    }
                                    else if (permissions.get(i).equals("sales_orders.write"))
                                    {
                                        salesteam_write ="true";

                                    }
                                    else if (permissions.get(i).equals("sales_orders.delete"))
                                    {
                                        salesorder_delete ="true";

                                    }
                                    else if (permissions.get(i).equals("invoices.read"))
                                    {
                                        invoices_read =String.valueOf(permission.getBoolean("invoices.read"));

                                    }
                                    else if (permissions.get(i).equals("invoices.write"))
                                    {
                                        invoices_write ="true";

                                    }
                                    else if (permissions.get(i).equals("invoices.delete"))
                                    {
                                        invoices_delete ="true";

                                    }
                                    else if (permissions.get(i).equals("contracts.read"))
                                    {
                                        contacts_read ="true";

                                    }
                                    else if (permissions.get(i).equals("contracts.write"))
                                    {
                                        contracts_write ="true";

                                    }
                                    else if (permissions.get(i).equals("contracts.delete"))
                                    {
                                        contracts_delete ="true";
                                    }
                                    else if (permissions.get(i).equals("staff.read"))
                                    {
                                        staff_read ="true";
                                    }
                                    else if (permissions.get(i).equals("staff.write"))
                                    {
                                        staff_write ="true";

                                    }
                                    else if (permissions.get(i).equals("staff.delete"))
                                    {
                                        staff_delete ="true";

                                    }
                                    else if (permissions.get(i).equals("contacts.read"))
                                    {
                                        contacts_read ="true";

                                    }
                                    else if (permissions.get(i).equals("contacts.write"))
                                    {
                                        contracts_write ="true";

                                    }
                                    else if (permissions.get(i).equals("contacts.delete"))
                                    {
                                        contacts_delete ="true";

                                    }
                                    else {
                                        //do nothing
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Appconfig.TOKEN=tokenv;
                            AppSession.first_name=first_namev;
                            AppSession.last_name=last_namev;
                            AppSession.user_email=user_emailv;
                            AppSession.phone=phonev;
                            AppSession.role=rolev;
                            Log.i("token value--",tokenv);

                            if (rolev.equals("admin"))
                            {
                                Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                                startActivity(i);

                            }

                             if (rolev.equals("customer"))
                            {
                                Intent i=new Intent(LoginActivity.this,Home1Activity.class);
                                startActivity(i);
                            }
                            if (rolev.equals("staff"))
                            {

                                Intent i=new Intent(LoginActivity.this,Home2Activity.class);
                                i.putExtra("calling from","login");
                                i.putExtra("salesteam_read",salesteam_read);
                                i.putExtra("salesteam_write",salesteam_write);
                                i.putExtra("salesteam_delete",salesteam_delete);
                                i.putExtra("leads_read",lead_read);
                                i.putExtra("leads_write",lead_write);
                                i.putExtra("leads_delete",lead_delete);
                                i.putExtra("opp_read",opp_read);
                                i.putExtra("opp_write",opp_write);
                                i.putExtra("opp_delete",opp_delete);
                                i.putExtra("loggedcall_read",loggedcall_read);
                                i.putExtra("loggedcall",loggedcall_write);
                                i.putExtra("loggedcall",loggedcall_delete);
                                i.putExtra("meeting_read",meeting_read);
                                i.putExtra("meeting_write",meeting_write);
                                i.putExtra("meeting_delete",meeting_delete);
                                i.putExtra("products_read",products_read);
                                i.putExtra("products_write",products_write);
                                i.putExtra("products_delete",products_delete);
                                i.putExtra("quotation_read",quotation_read);
                                i.putExtra("quotation_write",quotation_write);
                                i.putExtra("quotation_delete",quotation_delete);
                                i.putExtra("salesorder_read",salesorder_read);
                                i.putExtra("salesorder_write",salesorder_write);
                                i.putExtra("salesorder_delete",salesorder_delete);
                                i.putExtra("invoices_read",invoices_read);
                                i.putExtra("invoices_write",invoices_write);
                                i.putExtra("invoices_delete",invoices_delete);
                                i.putExtra("contracts_read",contracts_read);
                                i.putExtra("contracts_write",contracts_write);
                                i.putExtra("contracts_delete",contracts_delete);
                                i.putExtra("staff_read",staff_read);
                                i.putExtra("staff_write",staff_write);
                                i.putExtra("staff_delete",staff_delete);
                                i.putExtra("contacts_read",contacts_read);
                                i.putExtra("contacts_write",contacts_write);
                                i.putExtra("contacts_delete",contacts_delete);
                                startActivity(i);
                            }
                            Log.i("response--",response);
                            progressDialog.dismiss();

                        }
                    },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (String.valueOf(error)!=null)
                    {
                        Toast.makeText(getApplicationContext(),"Invalid Credentials!",Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                    //Log.i("response--", String.valueOf(error));
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email",email.getText().toString());
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MyVolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(stringRequest);



    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(104,159,56));
        }
    }


}
