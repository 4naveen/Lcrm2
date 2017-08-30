package com.project.lorvent.lcrm.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.project.lorvent.lcrm.activities.home.Home1Activity;
import com.project.lorvent.lcrm.activities.home.HomeActivity;

/**
 * Created by Guest on 9/19/2016.
 */
public class Appconfig {
    private Context _context;

   // public static final  String BASE_URL = "http://54.lcrm.in/api/";
    public static final  String BASE_URL = "http://demo.lcrmapp.com/api/";

    public static final  String LOGIN_URL = BASE_URL+"login";
    public static final  String DASHBOARD_URL = BASE_URL+"user/dashboard?token=";
    public static final  String DASHBOARD_CUSTOMER_URL = BASE_URL+"customer/dashboard?token=";
    public static final  String EDIT_PROFILE_URL = BASE_URL+"edit_profile?token=";
    public static final  String PASSWORD_RECOVERY_URL = BASE_URL+"password_recovery?token=";

    //    All Opportunity url
    public static final  String OPPORTUNITY_URL = BASE_URL+"user/opportunities?token=";
    public static final String POST_OPPPORTUNITY_URL=BASE_URL+"user/post_opportunity?token=";
    public static final String EDIT_OPPPORTUNITY_URL=BASE_URL+"user/edit_opportunity?token=";
    public static final  String DELETE_OPPORTUNITY_URL = BASE_URL+"user/delete_opportunity?token=";
    public static final  String OPPORTUNITY_DETAILS_URL = BASE_URL+"user/opportunity?token=";
    public static final  String OPPORTUNITY_TO_QUOTATION_URL = BASE_URL+"user/convert_opportunity_to_quotation?token=";

    public static final  String OPPORTUNITY_CALL_URL = BASE_URL+"user/opportunity_calls?token=";
    public static final String POST_OPPPORTUNITY_CALL_URL=BASE_URL+"user/post_opportunity_call?token=";
    public static final String EDIT_OPPPORTUNITY_CALL_URL=BASE_URL+"user/edit_opportunity_call?token=";
    public static final String DETAILS_OPPPORTUNITY_CALL_URL=BASE_URL+"user/opportunity_call?token=";
    public static final  String DELETE_OPPORTUNITY_CALL_URL = BASE_URL+"user/delete_opportunity_call?token=";


    public static final  String OPPORTUNITY_MEETING_URL = BASE_URL+"user/opportunity_meetings?token=";
    public static final String POST_OPPPORTUNITY_MEETING_URL=BASE_URL+"user/post_opportunity_meeting?token=";
    public static final String EDIT_OPPPORTUNITY_MEETING_URL=BASE_URL+"user/edit_opportunity_meeting?token=";
    public static final String DELETE_OPPORTUNITY_MEETING_URL = BASE_URL+"user/delete_opportunity_meeting?token=";
    public static final String DETAILS_OPPPORTUNITY_MEETING_URL=BASE_URL+"user/opportunity_meeting?token=";

    //    All lead url
    public static final  String LEADS_URL = BASE_URL+"user/leads?token=";
    public static final  String POST_LEAD_URL = BASE_URL+"user/post_lead?token=";
    public static final  String EDIT_LEAD_URL = BASE_URL+"user/edit_lead?token=";
    public static final String DELETE_LEAD_URL=BASE_URL+"user/delete_lead?token=";
    public static final String LEAD_DETAILS_URL=BASE_URL+"user/lead?token=";

    public static final String CALL_LEAD_DELETE_URL=BASE_URL+"user/delete_lead_call?token=";
    public static final String POST_CALL_LEAD_URL=BASE_URL+"user/post_lead_call?token=";
    public static final String EDIT_CALL_LEAD_URL=BASE_URL+"user/edit_lead_call?token=";
    public static final String DETAILS_LEAD_CALL_URL=BASE_URL+"user/call?token=";
    public static final String CALL_LEAD_URL=BASE_URL+"user/lead_calls?token=";

    //All countries List--
    public static final  String COUNTRY_URL = BASE_URL+"user/countries?token=";

    //    All  Quotation url
    public static final String QUOTATION_URL=BASE_URL+"user/quotations?token=";
    public static final String QUOTATION_DETAILS_URL=BASE_URL+"user/quotation?token=";
    public static final String QUOTATION_DELETE_URL=BASE_URL+"user/delete_quotation?token=";
    public static final String POST_QUOTATION_URL=BASE_URL+"user/post_quotation?token=";
    public static final String EDIT_QUOTATION_URL=BASE_URL+"user/edit_quotation?token=";
    public static final  String QUOTATION__TO_SALESORDER_URL = BASE_URL+"user/convert_quotation_to_sale_order?token=";
    public static final String SEND_QUOTATION_URL=BASE_URL+"user/send_quotation?token=";



    //    All  Invoices url
    public static final String INVOICES_URL=BASE_URL+"user/invoices?token=";
    public static final String INVOICES_DETAILS_URL=BASE_URL+"user/invoice?token=";
    public static final String POST_INVOICE_URL=BASE_URL+"user/post_invoice?token=";
    public static final String EDIT_INVOICE_URL=BASE_URL+"user/edit_invoice?token=";
    public static final String INVOICES_DELETE_URL=BASE_URL+"user/delete_invoice?token=";

    //    All  PaymentLog url
    public static final String PAYLOG_URL=BASE_URL+"user/invoice_payments?token=";
    public static final String DETAILS_PAYLOG_URL=BASE_URL+"user/invoice_payment?token=";
    public static final String POST_PAYLOG_URL=BASE_URL+"user/post_invoice_payment?token=";

    //    All  Salesteams url--
    public static final String SALESTEAM_URL=BASE_URL+"user/salesteams?token=";
    public static final String SALESTEAM_DETAILS_URL=BASE_URL+"user/salesteam?token=";
    public static final String SALESTEAM_DELETE_URL=BASE_URL+"user/delete_salesteam?token=";
    public static final String SALESTEAM_ADD_URL=BASE_URL+"user/post_salesteam?token=";
    public static final String SALESTEAM_EDIT_URL=BASE_URL+"user/edit_salesteam?token=";

    //All Logged call url
    public static final String CALL_URL=BASE_URL+"user/calls?token=";
    public static final String POST_CALL_URL=BASE_URL+"user/post_call?token=";
    public static final String EDIT_CALL_URL=BASE_URL+"user/edit_call?token=";
    public static final String DELETE_CALL_URL=BASE_URL+"user/delete_call?token=";
    public static final String DETAILS_CALL_URL=BASE_URL+"user/call?token=";


    //All SalesOrder url--
    public static final String SALESORDER_URL=BASE_URL+"user/sales_orders?token=";
    public static final String SALESORDER_DETAILS_URL=BASE_URL+"user/sales_order?token=";
    public static final String SALESORDER_DELETE_URL=BASE_URL+"user/delete_sales_order?token=";
    public static final String SALESORDER_ADD_URL=BASE_URL+"user/post_sales_order?token=";
    public static final String SALESORDER_EDIT_URL=BASE_URL+"user/edit_sales_order?token=";
    public static final  String SALESORDER__TO_INVOICE__URL = BASE_URL+"user/convert_sale_order_to_invoice?token=";

    //All Products  url--
    public static final String PRODUCTS_URL=BASE_URL+"user/products?token=";
    public static final String DELETE_PRODUCTS_URL=BASE_URL+"user/delete_product?token=";
    public static final String PRODUCTS_DETAILS_URL=BASE_URL+"user/product?token=";
    public static final String PRODUCTS_ADD_URL=BASE_URL+"user/post_product?token=";
    public static final String PRODUCTS_EDIT_URL=BASE_URL+"user/edit_product?token=";


    //All Category  url--
    public static final String CATEGORY_URL=BASE_URL+"user/categories?token=";
    public static final String CATEGORY_ADD_URL=BASE_URL+"user/post_category?token=";
    public static final String CATEGORY_DETAILS_URL=BASE_URL+"user/category?token=";
    public static final String CATEGORY_DElETE_URL=BASE_URL+"user/delete_category?token=";
    public static final String CATEGORY_EDIT_URL=BASE_URL+"user/edit_category?token=";


    //All Calendar url--

    public static final String CALENDAR_URL=BASE_URL+"user/calendar?token=";

    //All company & contact person--
    public static final String COMPANY_URL=BASE_URL+"user/companies?token=";
    public static final String COMPANY_DELETE_URL=BASE_URL+"user/delete_company?token=";
    public static final String COMPANY_DETAILS_URL=BASE_URL+"user/company?token=";
    public static final String COMPANY_EDIT_URL=BASE_URL+"user/edit_company?token=";
    public static final String COMPANY_POST_URL=BASE_URL+"user/post_company?token=";
    public static final String CUSTOMER_DETAILS_URL=BASE_URL+"user/customer?token=";
    public static final String CUSTOMER_URL=BASE_URL+"user/customers?token=";
    public static final String CONTACTS_POST_URL=BASE_URL+"user/post_customer?token=";
    public static final String CONTACTS_EDIT_URL=BASE_URL+"user/edit_customer?token=";
    public static final String CONTACT_DELETE_URL=BASE_URL+"user/delete_customer?token=";

    // All meeting url--
    public static final String MEETINGS_URL=BASE_URL+"user/meetings?token=";
    public static final String MEETING_DETAILS_URL=BASE_URL+"user/meeting?token=";
    public static final String MEETING_DELETE_URL=BASE_URL+"user/delete_meeting?token=";

    public static final String MEETING_POST_URL=BASE_URL+"user/post_meeting?token=";
    public static final String MEETING_EDIT_URL=BASE_URL+"user/edit_meeting?token=";


    //All Tasks url--
    public static final String TASK_URL=BASE_URL+"user/tasks?token=";
        public static final String TASK_DETAILS_URL=BASE_URL+"user/task?token=";
    public static final String POST_TASK_URL=BASE_URL+"user/post_task?token=";
    public static final String EDIT_TASK_URL=BASE_URL+"user/edit_task?token=";
    public static final String DELETE_TASK_URL=BASE_URL+"user/delete_task?token=";


    //All contracts url--
    public static final String CONTRACT_URL=BASE_URL+"user/contracts?token=";
    public static final String CONTRACT_DETAIL_URL=BASE_URL+"user/contract?token=";
    public static final String POST_CONTRACT_URL=BASE_URL+"user/post_contract?token=";
    public static final String EDIT_CONTRACT_URL=BASE_URL+"user/edit_contract?token=";
    public static final String DELETE_CONTRACT_URL=BASE_URL+"user/delete_contract?token=";

    //All stafffs url--
    public static final  String STAFF_URL = BASE_URL+"user/staffs?token=";
    public static final  String STAFF_DETAILS_URL = BASE_URL+"user/staff?token=";
    public static final  String POST_STAFF_URL =BASE_URL+"user/post_staff?token=";
    public static final String DELETE_STAFF_URL=BASE_URL+"user/delete_staff?token=";
    public static final String Edit_STAFF_URL=BASE_URL+"user/edit_staff?token=";
    public static final String INVITE_STAFF_URL=BASE_URL+"user/invite_staff?token=";

//All quotation template url--

    public static final String QTEMPLATE_URL=BASE_URL+"user/qtemplates?token=";
    public static final String QTEMPLATE_DETAILS_URL=BASE_URL+"user/qtemplate?token=";
    public static final String QTEMPLATE_DELETE_URL=BASE_URL+"user/delete_qtemplate?token=";
    public static final String QTEMPLATE_POST_URL=BASE_URL+"user/post_qtemplate?token=";
    public static final String QTEMPLATE_EDIT_URL=BASE_URL+"user/edit_qtemplate?token=";

//All email_templates url

     public static final String ETEMPLATES_URL=BASE_URL+"user/email_templates?token=";
    public static final String ETEMPLATES__DETAILS_URL=BASE_URL+"user/email_template?token=";
    public static final String ETEMPLATES_DELETE_URL=BASE_URL+"user/delete_email_template?token=";
    public static final String ETEMPLATES_POST_URL=BASE_URL+"user/post_email_template?token=";
    public static final String ETEMPLATE_EDIT_URL=BASE_URL+"user/edit_email_template?token=";



    //All Email url
    public static final String EMAILS_URL=BASE_URL+"emails?token=";
    public static final String EMAIL_URL=BASE_URL+"email?token=";
    public static final String POST_EMAIL_URL=BASE_URL+"post_email?token=";
    public static final String DELETE_EMAIL_URL=BASE_URL+"delete_email?token=";
    public static final String REPLY_EMAIL_URL=BASE_URL+"replay_email?token=";



    //All Settings url---
        public static final  String SETTINGS_URL = BASE_URL+"user/settings?token=";
    public static final  String UPDATE_SETTINGS_URL = BASE_URL+"user/update_settings?token=";

    // All member variable--

    public static String TOKEN;
    public static HomeActivity homeActivity;
    public static Home1Activity home1Activity;

    //All Customer role url---
    public static final String CUSTOMER_QUOTATION_URL=BASE_URL+"customer/quotations?token=";
    public static final String CUSTOMER_DETAIL_QUOTATION_URL=BASE_URL+"customer/quotation?token=";
    public static final String CUSTOMER_CONTRACT_URL=BASE_URL+"customer/contract?token=";
    public static final String CUSTOMER_STAFF_URL=BASE_URL+"customer/contacts?token=";
    public static final String CUSTOMER_INVOICES_URL=BASE_URL+"customer/invoices?token=";
    public static final String CUSTOMER_DETAIL_INVOICES_URL=BASE_URL+"customer/invoice?token=";
    public static final String CUSTOMER_SALESORDER_URL=BASE_URL+"customer/sales_orders?token=";
    public static final String CUSTOMER_DETAIL_SALESORDER_URL=BASE_URL+"customer/sales_order?token=";

    // All feedback url

    public static final String HELP_URL = "https://docs.google.com/a/lorventindia.com/forms/d/e/1FAIpQLSdSgqmQL5r51zeoQocov5a0zGLUcrn5Igo2AnQ5DYt4KCaRiw/formResponse";
    public static final String CONTENT_URL = "https://docs.google.com/a/lorventindia.com/forms/d/e/1FAIpQLSclk3c3AP3SvTOS_Dc68tCxjfyc6P-xyeIQeuCJjV7JeS3nGg/formResponse";
    public static final String FEATURE_URL = "https://docs.google.com/a/lorventindia.com/forms/d/e/1FAIpQLSfCqoXsDr6XvNDUz1QL2bFU3NOSqVqHqZX_l0vhmI69g0Rd2g/formResponse";
    public static final String OTHER_URL = "https://docs.google.com/a/lorventindia.com/forms/d/e/1FAIpQLSdHknDjYpyRc5QUxFjtU5xbNcUdFQQ-OJd24TuXniUE1O9jSw/formResponse";
    public static final  String TECHNICAL_URL = "https://docs.google.com/a/lorventindia.com/forms/d/e/1FAIpQLSdTERAYVw1w7tpq81-jvXSb7-l2yTCOHZ2hS5FcsQ1A4dJxGA/formResponse";
    public static final String  UI_URL = "https://docs.google.com/a/lorventindia.com/forms/d/e/1FAIpQLSfwa2KDZybOAc0lawnm9wFsFnTP6Hrh-JRWVAkc5AAZPh8SIg/formResponse";


/*    public static  String getUrl(Context _context,String endpoints) {
        SharedPreferences preferences = _context.getSharedPreferences("pref",0);
       // SharedPreferences.Editor editor = pref.edit();
        String text_url = preferences.getString("url", null);
        String get_url;
        if (text_url != null) {
            get_url = text_url + "/user/staffs?token=";
        } else {
            get_url = Appconfig.STAFF_URL;
        }

    }*/

}