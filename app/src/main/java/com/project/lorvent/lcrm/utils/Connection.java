package com.project.lorvent.lcrm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.lorvent.lcrm.models.Company;
import com.project.lorvent.lcrm.models.Contacts;
import com.project.lorvent.lcrm.models.OppLeads;
import com.project.lorvent.lcrm.models.Products;
import com.project.lorvent.lcrm.models.Qtemplate;
import com.project.lorvent.lcrm.models.SalesTeam;
import com.project.lorvent.lcrm.models.Staff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by User on 3/30/2017.
 */

public class Connection {
    public static SimpleDateFormat simpleDateFormat;
    public static void getDateSettings(String token, Context context) {
        SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url= text_url + "/user/settings?token=";
        } else {
            get_url= Appconfig.SETTINGS_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           // Log.i("response--", String.valueOf(response));
                            JSONObject jsonObject=new JSONObject(response);
                            JSONObject pre_settings=jsonObject.getJSONObject("settings");
                            AppSession.date_format=pre_settings.getString("date_format");
                            AppSession.time_format=pre_settings.getString("time_format");
                            AppSession.quotation_prefix=pre_settings.getString("quotation_prefix");
                            AppSession.quotation_start_number=pre_settings.getString("quotation_start_number");
                            AppSession.sales_prefix=pre_settings.getString("sales_prefix");
                            AppSession.sales_start_number=pre_settings.getString("sales_start_number");
                            AppSession.invoice_prefix=pre_settings.getString("invoice_prefix");
                            AppSession.invoice_start_number=pre_settings.getString("invoice_start_number");
                          /*  Log.i("date--",AppSession.date_format+""+AppSession.time_format+""+AppSession.quotation_prefix+""+AppSession.quotation_start_number
                                    +""+AppSession.sales_prefix+""+AppSession.sales_start_number);*/
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
                Map<String, String> params = new HashMap<>();

                return params;
            }

        } ;
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);
    }
    public static void getStaffList(String token,Context context)
    {   SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url= text_url + "/user/staffs?token=";
        } else {
            get_url= Appconfig.STAFF_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (AppSession.sales_personNameList.size()!=0)

                    AppSession.sales_personNameList.clear();
                    AppSession.teamLeaderNameList.clear();
                    AppSession.teamMemberNameList.clear();
                    AppSession.responsibleNameList.clear();
                    AppSession.salesPersonList.clear();
                    AppSession.teamLeaderList.clear();
                    AppSession.teamMemberList.clear();
                    AppSession.responsibleList.clear();
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        // Log.i("response--", String.valueOf(response));

                        /*
                        JSONArray jsonArray=jsonObject.getJSONArray("staffs");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            Staff staff=new Staff();
                            staff.setId(object.getInt("id"));
                            staff.setName(object.getString("full_name"));
                            AppSession.sales_personNameList.add(object.getString("full_name"));
                            AppSession.salesPersonList.add(staff);
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
                                AppSession.sales_personNameList.add(jsonObject2.getString("full_name"));
                                AppSession.teamLeaderNameList.add(jsonObject2.getString("full_name"));
                                AppSession.teamMemberNameList.add(jsonObject2.getString("full_name"));
                                AppSession.responsibleNameList.add(jsonObject2.getString("full_name"));

                                AppSession.salesPersonList.add(staff);
                                AppSession.teamLeaderList.add(staff);
                                AppSession.teamMemberList.add(staff);
                                AppSession.responsibleList.add(staff);

                            } catch (JSONException e) {
                                // Something went wrong!
                            }
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
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);


    }
    public static void getCustomerList(String token,Context context)
    {  SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url= text_url + "/user/companies?token=";
        } else {
            get_url= Appconfig.COMPANY_URL;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    AppSession.companyNameList.clear();
                    AppSession.companyList.clear();
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
            Map<String, String> params = new HashMap<>();

            return params;
        }

    } ;
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);
    }
    public static void  getSalesTeamList(String token,Context context)
    {   SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url= text_url + "/user/salesteams?token=";
        } else {
            get_url= Appconfig.SALESTEAM_URL;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppSession.sales_teamNameList.clear();
                        AppSession.salesTeamList.clear();
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
                Map<String, String> params = new HashMap<>();

                return params;
            }

        } ;
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);

    }
    public static void getContactList(String token,Context context)
    {   SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url= text_url + "/user/customers?token=";
        } else {
            get_url= Appconfig.CUSTOMER_URL;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET,get_url+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    AppSession.customerNameList.clear();
                    AppSession.customerList.clear();
                    try {
                        Log.i("response--", String.valueOf(response));
                        JSONObject jsonObject=new JSONObject(response);
                       /* JSONArray jsonArray=jsonObject.getJSONArray("customers");
                        for (int i=0;i<jsonArray.length();i++)
                        {
                            JSONObject object=jsonArray.getJSONObject(i);
                            main_contact_personList.add(object.getString("full_name"));
                        }
*/
                        JSONObject  jsonObject1=jsonObject.getJSONObject("customers");
                        Iterator<String> iter = jsonObject1.keys();
                        while (iter.hasNext()) {
                            String key = iter.next();

                            try {
                                Object value = jsonObject1.get(key);
                                JSONObject jsonObject2 = (JSONObject) jsonObject1.get(key);
                                Contacts contacts=new Contacts();
                                contacts.setName(jsonObject2.getString("full_name"));
                                contacts.setId(jsonObject2.getInt("customer_id"));
                                AppSession.customerNameList.add(jsonObject2.getString("full_name"));
                                AppSession.customerList.add(contacts);

                            } catch (JSONException e) {
                                // Something went wrong!
                            }
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
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);

    }
    public static void getProductList(String token,Context context) {
        SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url= text_url + "/user/products?token=";
        } else {
            get_url= Appconfig.PRODUCTS_URL;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, get_url+ token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        AppSession.productNameList.clear();
                        AppSession.productsArrayList.clear();
                        try {
                            Log.i("response--", String.valueOf(response));
                          //  AppSession.productNameList.add("Please select");

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("products");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Products products = new Products();
                                products.setId(object.getInt("id"));
                                products.setProduct_name(object.getString("product_name"));
                                AppSession.productNameList.add(object.getString("product_name"));
                                AppSession.productsArrayList.add(products);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.i("response--", String.valueOf(error));
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
    public static void getQtemplateList(String token,Context context)
    {
        SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url= text_url + "/user/qtemplates?token=";
        } else {
            get_url= Appconfig.QTEMPLATE_URL;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, get_url+token,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    AppSession.quotation_templateNameList.clear();
                    AppSession.qTemplateList.clear();
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
        MyVolleySingleton.getInstance(context).getRequestQueue().add(stringRequest);

    }
    public  static String getFormatedDate(Date date)
    {
        String text_date=null;
        if (AppSession.date_format.equals("Y-d-m")) {
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
            text_date = simpleDateFormat.format(date);
            Log.i("text_date", text_date);
        }
        if (AppSession.date_format.equals("F j,Y")) {
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("F", "MMMM").replace("j", "d"), Locale.ENGLISH);
            text_date = simpleDateFormat.format(date);
            Log.i("text_date", text_date);
        }
        if (AppSession.date_format.equals("d.m.Y.")) {
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
             text_date = simpleDateFormat.format(date);
            Log.i("text_date", text_date);

        }
        if (AppSession.date_format.equals("d.m.Y")) {
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
            text_date = simpleDateFormat.format(date);
            Log.i("text_date", text_date);

        }
        if (AppSession.date_format.equals("d/m/Y")) {
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
            text_date = simpleDateFormat.format(date);
            Log.i("text_date", text_date);
        }
        if (AppSession.date_format.equals("m/d/Y")) {
            simpleDateFormat = new SimpleDateFormat(AppSession.date_format.replace("Y", "y").replace("m", "MM").replace("d", "dd"), Locale.ENGLISH);
             text_date = simpleDateFormat.format(date);
            Log.i("text_date", text_date);

        }
        return text_date;
    }
    public static void getDashboard(String token,Context context) {
        SharedPreferences preferences =context.getSharedPreferences("pref", MODE_PRIVATE);
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url= text_url + "/user/dashboard?token=";
        } else {
            get_url= Appconfig.DASHBOARD_URL;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, get_url+ token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.i("response--", String.valueOf(response));
                            AppSession.dashboardArrayList1.clear();
                            AppSession.dashboardArrayList2.clear();
                            JSONObject jsonObject=new JSONObject(response);
                            AppSession.dashboardArrayList1.add(jsonObject.getInt("contracts"));
                            AppSession.dashboardArrayList1.add(jsonObject.getInt("products"));
                            AppSession.dashboardArrayList1.add(jsonObject.getInt("opportunities"));
                            AppSession.dashboardArrayList1.add(jsonObject.getInt("customers"));

                            JSONArray jsonArray=jsonObject.getJSONArray("opportunity_leads");
                            for (int i=0;i<jsonArray.length();i++)
                            {    OppLeads oppLeads=new OppLeads();
                                JSONObject object=jsonArray.getJSONObject(i);
                                oppLeads.setMonth(object.getString("month"));
                                oppLeads.setYear(object.getString("year"));
                                oppLeads.setOpp(object.getInt("opportunity"));
                                oppLeads.setLeads(object.getInt("leads"));
                                AppSession.dashboardArrayList2.add(oppLeads);
                            }
                  /*          AppSession.dashboardArrayList1.clear();
                            AppSession.dashboardArrayList2.clear();
                            AppSession.dashboardArrayList1.addAll(dashboardArrayList1);
                            AppSession.dashboardArrayList2.addAll(dashboardArrayList2);
                            for (int i=0;i<AppSession.dashboardArrayList1.size();i++)
                            {
                                Log.i("dash1in login", String.valueOf(AppSession.dashboardArrayList1.get(i)));
                            }*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Log.i("response--", String.valueOf(error));
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
}
