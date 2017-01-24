package com.project.naveen.lcrm;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;


import com.project.naveen.lcrm.models.Company;
import com.project.naveen.lcrm.models.Contacts;
import com.project.naveen.lcrm.models.Contracts;
import com.project.naveen.lcrm.models.Events;
import com.project.naveen.lcrm.models.Invoice;
import com.project.naveen.lcrm.models.Leads;
import com.project.naveen.lcrm.models.LoggedCalls;
import com.project.naveen.lcrm.models.Meeting;
import com.project.naveen.lcrm.models.Opportunity;
import com.project.naveen.lcrm.models.Payment;
import com.project.naveen.lcrm.models.Products;
import com.project.naveen.lcrm.models.Qtemplate;
import com.project.naveen.lcrm.models.Quotation;
import com.project.naveen.lcrm.models.SalesOrder;
import com.project.naveen.lcrm.models.SalesTeam;
import com.project.naveen.lcrm.models.Staff;
import com.project.naveen.lcrm.models.Tasks;

import java.util.ArrayList;

;


public class AppSession {
    public static ArrayList<Staff> staffArrayList;
    public static ArrayList<Opportunity> opportunityArrayList;
    public static ArrayList<Quotation> quotationArrayList;
    public static ArrayList<Leads> leadArrayList;
    public static ArrayList<LoggedCalls> loggedCallsArrayList;
    public static ArrayList<Products>  productsArrayList;
    public static ArrayList<Invoice>  invoicesArrayList;
    public static ArrayList<Payment>  paylogArrayList;
    public static ArrayList<Company>  companyArrayList;
    public static ArrayList<Contracts>  contractArrayList;
    public static ArrayList<Qtemplate>  qtemplateArrayList;
    public static ArrayList<SalesTeam>salesTeamArrayList;
    public static ArrayList<SalesOrder>salesOrderArrayList;
    public static ArrayList<Events>eventsArrayList;
    public static ArrayList<Tasks>tasksArrayList;
    public static ArrayList<Meeting>meetingArrayList;

    public static ArrayList<String> customerNameList,companyNameList, quotation_templateNameList, sales_teamNameList, sales_personNameList,statusList,pay_termList;
    public static ArrayList<Qtemplate>qTemplateList;
    public static ArrayList<SalesTeam>salesTeamList;
    public static ArrayList<Staff>salesPersonList;
    public static ArrayList<Contacts>customerList;
    public static ArrayList<Company>companyList;




    public static RecyclerView opportunity_recyclerView;
    public static RecyclerView lead_recyclerView;
    public static RecyclerView quotation_recyclerView;
    public static RecyclerView salesTeam_recyclerView;
    public static RecyclerView loggedCall_recyclerView;
    public static RecyclerView invoices_recyclerView;
    public static RecyclerView payLog_recyclerView;
    public static RecyclerView products_recyclerView;
    public static RecyclerView company_recyclerView;
    public static RecyclerView contract_recyclerView;
    public static RecyclerView staff_recyclerView;
    public static RecyclerView qtemplate_recyclerView;
    public static RecyclerView salesOrder_recyclerView;
    public static RecyclerView meeting_recyclerView;




    public static int salesteam_read,salesteam_write,salesteam_delete,lead_read,lead_write,lead_delete,opp_read,opp_write,opp_delete,loggedcall_read,
            loggedcall_write,loggedcall_delete,meeting_read,meeting_write,meeting_delete,products_read,products_write,products_delete,
            quotation_read,quotation_write,quotation_delete,salesorder_read,salesorder_write,salesorder_delete,invoices_read,invoices_write,invoices_delete,
            contracts_read,contracts_write,contracts_delete,staff_read,staff_write,staff_delete,contacts_read,contacts_write,contacts_delete;
    public static  String first_name = null;
    public static  String last_name = null;
    public static  String user_email = null;
    public static  String phone = null;
    public static  String role = null;
    public static  String date_format,quotation_prefix,quotation_start_number,sales_prefix,sales_start_number,
            invoice_prefix,invoice_start_number;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    // shared pref mode
    int PRIVATE_MODE = 0;
    // Shared preferences file name
    private static final String PREF_NAME = "lcrm-welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public AppSession(Context _context) {
        this._context = _context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
