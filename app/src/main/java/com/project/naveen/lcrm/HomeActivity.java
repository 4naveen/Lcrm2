package com.project.naveen.lcrm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.naveen.lcrm.addactivity.AddQuotationActivity;
import com.project.naveen.lcrm.menu.fragment.CalendarFragment;
import com.project.naveen.lcrm.menu.fragment.ContractsFragment;
import com.project.naveen.lcrm.menu.fragment.CustomerFragment;
import com.project.naveen.lcrm.menu.fragment.DashboardFragment;
import com.project.naveen.lcrm.menu.fragment.InvoiceFragment;
import com.project.naveen.lcrm.menu.fragment.LeadsFragment;
import com.project.naveen.lcrm.menu.fragment.LoggedCallsFragment;
import com.project.naveen.lcrm.menu.fragment.MeetingFragment;
import com.project.naveen.lcrm.menu.fragment.OpportnityFragment;
import com.project.naveen.lcrm.menu.fragment.ProductFragment;
import com.project.naveen.lcrm.menu.fragment.QtemplateFragment;
import com.project.naveen.lcrm.menu.fragment.QuotationFragment;
import com.project.naveen.lcrm.menu.fragment.SalesOrderFragment;
import com.project.naveen.lcrm.menu.fragment.SalesTeamFragment;
import com.project.naveen.lcrm.menu.fragment.SettingFragment;
import com.project.naveen.lcrm.menu.fragment.StaffFragment;
import com.project.naveen.lcrm.menu.fragment.TaskFragment;
import com.project.naveen.lcrm.models.Company;
import com.project.naveen.lcrm.models.Qtemplate;
import com.project.naveen.lcrm.models.SalesTeam;
import com.project.naveen.lcrm.models.Staff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import static com.project.naveen.lcrm.AppSession.date_format;
import static com.project.naveen.lcrm.AppSession.quotation_prefix;
import static com.project.naveen.lcrm.AppSession.quotation_start_number;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
   // int mNavItemId;
    Button profile;
    TextView user_name,email;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
          //toolbar.setLogo(R.drawable.logo);
          Appconfig.homeActivity=HomeActivity.this;
        AppSession.customerNameList = new ArrayList<>();
        AppSession.companyNameList = new ArrayList<>();
        AppSession.quotation_templateNameList = new ArrayList<>();
        AppSession.sales_teamNameList = new ArrayList<>();
        AppSession.sales_personNameList = new ArrayList<>();
        AppSession.qTemplateList = new ArrayList<>();
        AppSession.salesTeamList = new ArrayList<>();
        AppSession.salesPersonList = new ArrayList<>();
        AppSession.customerList = new ArrayList<>();
        AppSession.companyList = new ArrayList<>();
        AppSession.statusList = new ArrayList<>();
        AppSession.pay_termList = new ArrayList<>();
        AppSession.salesteam_read=1; AppSession.salesteam_write=1;AppSession.salesteam_delete=1;
        AppSession.lead_read=1; AppSession.lead_write=1;AppSession.lead_delete=1;
        AppSession.opp_read=1;AppSession.opp_write = 1;AppSession.opp_delete=1;
        AppSession.loggedcall_read = 1;AppSession.loggedcall_write = 1;AppSession.loggedcall_delete = 1;
        AppSession.meeting_read=1;AppSession.meeting_write=1;AppSession.meeting_delete=1;
        AppSession.products_read = 1;AppSession.products_write = 1;AppSession.products_delete = 1;
        AppSession.quotation_read=1;AppSession.quotation_write=1;AppSession.quotation_delete=1;
        AppSession.salesorder_read = 1;AppSession.salesorder_write= 1;AppSession.salesorder_delete= 1;
        AppSession.invoices_read=1;AppSession.invoices_write=1;AppSession.invoices_delete=1;
        AppSession.contracts_read = 1;AppSession.contracts_write = 1;AppSession.contracts_delete = 1;
        AppSession.staff_read=1;AppSession.staff_write=1;AppSession.staff_delete=1;
        AppSession.contacts_read = 1;AppSession.contacts_write = 1;AppSession.contacts_delete = 1;

        AppSession.statusList.add("Draft Quotation");
        AppSession.statusList.add("Quotation Sent");
        AppSession.pay_termList.add("1 days");
        AppSession.pay_termList.add("Immediate Payment");

        getDateSettings(Appconfig.TOKEN);
        getSalesTeamList(Appconfig.TOKEN);
        getSalesPersonList(Appconfig.TOKEN);
        getQtemplateList(Appconfig.TOKEN);
        getCustomerList(Appconfig.TOKEN);
        new GetAllContacts().execute(Appconfig.TOKEN);
//        getCustomerList(Appconfig.TOKEN);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View v1=navigationView.inflateHeaderView(R.layout.nav_header_home);
        profile=(Button)v1.findViewById(R.id.profile);
            user_name=(TextView)v1.findViewById(R.id.uname);
            email=(TextView)v1.findViewById(R.id.email);
            user_name.setText(AppSession.first_name);
            email.setText(AppSession.user_email);
        Fragment fragment1=new DashboardFragment();
        FragmentTransaction trans1=getSupportFragmentManager().beginTransaction();
        trans1.replace(R.id.frame,fragment1);
        trans1.addToBackStack(null);
        trans1.commit();
        profile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
         Intent i=new Intent(HomeActivity.this,UserProfileActivity.class);
        startActivity(i);
        finish();
        Log.i("header--","onclick");
    }
});
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction2=getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        navigationView.getMenu().findItem(item.getItemId()).setChecked(true);

        switch (item.getItemId())
        {
            case R.id.dashboard:
            {

                 fragment=new DashboardFragment();

                break;
            }
            case R.id.opportnity:
            {

                fragment=new OpportnityFragment();

                break;

            }
            case R.id.leads:
            {

                fragment=new LeadsFragment();

                break;
            }

            case R.id.quotation:
            {
             fragment=new QuotationFragment();
             break;
            }
            case R.id.Invoices:
            {
            fragment= new InvoiceFragment();


                break;
            }
            case R.id.salesteam:
            {
              fragment=new SalesTeamFragment();
                break;

            }
            case R.id.LoggedCalls:
            {fragment=new LoggedCallsFragment();
                break;

            }
            case R.id.salesorder:
            {fragment=new SalesOrderFragment();
                break;
            }
            case R.id.products:
            {
              fragment=new ProductFragment();
                break;
            }
            case R.id.calender:
            {
                fragment=new CalendarFragment();
                break;
            }
            case R.id.customer:
            {
                fragment=new CustomerFragment();
                break;
            }
            case R.id.meetings:
            {
               //Intent i=new Intent(HomeActivity.this,AddMeetingActivity.class);
                fragment=new MeetingFragment();
                //startActivity(i);
                break;
            }
            case R.id.tasks:
            {
                fragment=new TaskFragment();

                break;
            }

            case R.id.contracts:
            {
                fragment=new ContractsFragment();
                break;
            }
            case R.id.Staff:
            {
                fragment=new StaffFragment();
                 break;

            }
            case R.id.quotation_template:
            {
                fragment=new QtemplateFragment();
                break;
            }
            case R.id.settings:
            {
                fragment=new SettingFragment();
                break;
            }
        }
        transaction2.replace(R.id.frame,fragment);
        transaction2.addToBackStack(null);
        transaction2.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }


   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.home_actionbar,menu);
       // this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);

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
                            date_format=pre_settings.getString("date_format");
                            quotation_prefix=pre_settings.getString("quotation_prefix");
                            quotation_start_number=pre_settings.getString("quotation_start_number");
                            AppSession.sales_prefix=pre_settings.getString("sales_prefix");
                            AppSession.sales_start_number=pre_settings.getString("sales_start_number");
                            AppSession.invoice_prefix=pre_settings.getString("invoice_prefix");
                            AppSession.invoice_start_number=pre_settings.getString("invoice_start_number");
                            Log.i("date--",date_format);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

               // Log.i("response--", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        } ;
        MyVolleySingleton.getInstance(HomeActivity.this).getRequestQueue().add(stringRequest);


    }

    public  void  getSalesTeamList(String token)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.SALESTEAM_URL+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                           // Log.i("response--", String.valueOf(response));

                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("salesteams");
                            for (int i=0;i<jsonArray.length();i++)
                            {
                                JSONObject object=jsonArray.getJSONObject(i);
                                SalesTeam salesTeam=new SalesTeam();
                                salesTeam.setId(object.getInt("id"));
                                salesTeam.setSalesteam(object.getString("salesteam"));
                                AppSession.sales_teamNameList.add(object.getString("salesteam"));
                                AppSession.salesTeamList.add(salesTeam);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

               // Log.i("response--", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }

        } ;
        MyVolleySingleton.getInstance(HomeActivity.this).getRequestQueue().add(stringRequest);

    }
    public void getSalesPersonList(String token)
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
                            AppSession.sales_personNameList.add(object.getString("full_name"));
                            AppSession.salesPersonList.add(staff);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            },new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

           // Log.i("response--", String.valueOf(error));
        }
    }){
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();

            return params;
        }

    } ;
        MyVolleySingleton.getInstance(HomeActivity.this).getRequestQueue().add(stringRequest);


    }
    public void getQtemplateList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.QTEMPLATE_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                       // Log.i("response--", String.valueOf(response));
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("qtemplates");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Qtemplate qtemplate=new Qtemplate();
                            qtemplate.setQuatation_template(object.getString("quotation_template"));
                            qtemplate.setId(object.getInt("id"));
                            AppSession.quotation_templateNameList.add(object.getString("quotation_template"));
                            AppSession.qTemplateList.add(qtemplate);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

           // Log.i("response--", String.valueOf(error));
        }
    }){
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();

            return params;
        }

    } ;
        MyVolleySingleton.getInstance(HomeActivity.this).getRequestQueue().add(stringRequest);

    }
    public void getCustomerList(String token)
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
                            AppSession.companyNameList.add(object.getString("name"));
                            AppSession.companyList.add(company);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            },new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

           // Log.i("response--", String.valueOf(error));
        }
    }){
        @Override
        protected Map<String, String> getParams() {
            Map<String, String> params = new HashMap<String, String>();

            return params;
        }

    } ;
        MyVolleySingleton.getInstance(HomeActivity.this).getRequestQueue().add(stringRequest);

    }
    class GetAllContacts extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(HomeActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            String response="";
            HttpURLConnection connection ;
            BufferedReader bufferedReader;
            StringBuffer  buffer;
            String tok=params[0];
            try {
                url = new URL(Appconfig.CUSTOMER_URL+tok);
                connection = (HttpURLConnection) url.openConnection();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    buffer=new StringBuffer();
                    //Log.d("Output",br.toString());
                    while ((line = bufferedReader.readLine()) != null) {
                        // response += line;
                        buffer.append(line);
                        Log.d("output lines", line);
                    }
                    response=buffer.toString();
                    //Get Values from JSONobject
                    //System.out.println("success=" + json.get("success"));
                    Log.i("response in customer", response);
                }
                else {
                    InputStreamReader inputStreamReader = new InputStreamReader(connection.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    buffer=new StringBuffer();
                    String line ="";
                    while ((line = bufferedReader.readLine()) != null) {
                        //  response += line;
                        buffer.append(line);
                        Log.d("output lines", line);
                    }
                    response=buffer.toString();
                    Log.i("response in customer", response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }
        @Override
        protected void onPostExecute(String response) {
            dialog.dismiss();
            Log.i("responsein onpost--",response);

           /* try {
                JSONObject jsonObject=new JSONObject(response);
              //  JSONArray jsonArray=jsonObject.getJSONArray("companies");
                JSONObject  jsonObject1=jsonObject.getJSONObject("customers");
                Iterator<String> iter = jsonObject1.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        Object value = jsonObject1.get(key);
                        Log.i("jsonobjectvalue1--",value.toString());
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }

               // mAdapter = new CompanyAdapter(getActivity(), AppSession.companyArrayList);

               // AppSession.company_recyclerView.setAdapter(mAdapter);
                //AppSession.company_recyclerView.setItemAnimator(new DefaultItemAnimator());
                // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
               // RecyclerView.LayoutManager lmanager=new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
                //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

               // AppSession.company_recyclerView.setLayoutManager(lmanager);

            } catch (JSONException e) {
                e.printStackTrace();
            }
*/
        }

    }
     /*  public void getCustomerList(String token)
    { StringRequest stringRequest = new StringRequest(Request.Method.GET, Appconfig.CUSTOMER_URL+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        Log.i("response--", String.valueOf(response));
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("customers");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            main_contact_personList.add(object.getString("full_name"));
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
        MyVolleySingleton.getInstance(AddQuotationActivity.this).getRequestQueue().add(stringRequest);

    }*/
}
