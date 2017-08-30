package com.project.lorvent.lcrm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;


import com.project.lorvent.lcrm.models.Company;
import com.project.lorvent.lcrm.models.Contacts;
import com.project.lorvent.lcrm.models.Contracts;
import com.project.lorvent.lcrm.models.Etemplate;
import com.project.lorvent.lcrm.models.Events;
import com.project.lorvent.lcrm.models.Invoice;
import com.project.lorvent.lcrm.models.Leads;
import com.project.lorvent.lcrm.models.LoggedCalls;
import com.project.lorvent.lcrm.models.Meeting;
import com.project.lorvent.lcrm.models.OppLeads;
import com.project.lorvent.lcrm.models.Opportunity;
import com.project.lorvent.lcrm.models.Payment;
import com.project.lorvent.lcrm.models.Products;
import com.project.lorvent.lcrm.models.Qtemplate;
import com.project.lorvent.lcrm.models.Quotation;
import com.project.lorvent.lcrm.models.SalesOrder;
import com.project.lorvent.lcrm.models.SalesTeam;
import com.project.lorvent.lcrm.models.Staff;
import com.project.lorvent.lcrm.models.Tasks;
import com.project.lorvent.lcrm.models.customer.InvoiceByMonth;

import java.util.ArrayList;

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
    public static ArrayList<Contacts>  contactArrayList;
    public static ArrayList<Contracts>  contractArrayList;
    public static ArrayList<Qtemplate>  qtemplateArrayList;
    public static ArrayList<SalesTeam>salesTeamArrayList;
    public static ArrayList<SalesOrder>salesOrderArrayList;
    public static ArrayList<Events>eventsArrayList;
    public static ArrayList<Tasks>tasksArrayList;
    public static ArrayList<Etemplate>etemplateArrayList;
    public static ArrayList<Meeting>meetingArrayList;
    public static ArrayList<String> customerNameList,companyNameList,
            quotation_templateNameList,sales_teamNameList,teamLeaderNameList,teamMemberNameList,
            sales_personNameList,statusList,pay_termList,productNameList,responsibleNameList;
    public static ArrayList<Qtemplate>qTemplateList;
    public static ArrayList<SalesTeam>salesTeamList;
    public static ArrayList<Staff>salesPersonList;
    public static ArrayList<Staff>teamLeaderList;
    public static ArrayList<Staff>teamMemberList;
    public static ArrayList<Staff>responsibleList;

    public static ArrayList<Contacts>customerList;
    public static ArrayList<Company>companyList;

    public static  ArrayList<Integer> dashboardArrayList1;
    public static  ArrayList<OppLeads> dashboardArrayList2;
    public  static ArrayList<InvoiceByMonth>byMonthArrayList;

    public static RecyclerView opportunity_recyclerView;
    public static RecyclerView lead_recyclerView;
    public static RecyclerView quotation_recyclerView;
    public static RecyclerView salesTeam_recyclerView;
    public static RecyclerView loggedCall_recyclerView;
    public static RecyclerView invoices_recyclerView;
    public static RecyclerView payLog_recyclerView;
    public static RecyclerView products_recyclerView;
    public static RecyclerView company_recyclerView;
    public static RecyclerView customer_recyclerView;
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
    public static  String date_format1 = null;

    public static  String image_url = null;

    public static  String date_format,time_format,quotation_prefix,quotation_start_number,sales_prefix,sales_start_number,
            invoice_prefix,invoice_start_number;

    private  static boolean NETWORK_STATUS;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    // shared pref mode
    int PRIVATE_MODE = 0;
    // Shared preferences file name
    private static final String PREF_NAME = "lcrm-welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    public static  String description,unit_price,selected_unit_price ,selected_description;
    public static boolean isFirstTime=false;
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
