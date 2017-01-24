package com.project.naveen.lcrm.menu.fragment.opportunity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;


import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.adapters.subadapter.OpportunityCallAdapter;
import com.project.naveen.lcrm.models.submodels.OppCall;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import static com.project.naveen.lcrm.adapters.subadapter.OpportunityCallAdapter.DATE_PICKER_ID_EDIT;



public class CallActivity extends AppCompatActivity {
    ArrayList<OppCall> oppCallArrayList;
    RecyclerView rv;
    String token;
    OpportunityCallAdapter mAdapter;
    String lead_id;
    static final int DATE_PICKER_ID=111;
    public static int  current_year,current_month,current_day;
    public static String current_month_text;
    SharedPreferences spf;
    SharedPreferences.Editor et;
    EditText date;
    BetterSpinner company,responsible;
    ArrayList<String>companyList,responsibleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        oppCallArrayList =new ArrayList<>();
        companyList=new ArrayList<>();
        responsibleList=new ArrayList<>();
        spf=getSharedPreferences("opp_call_pref", Context.MODE_PRIVATE);
        et=spf.edit();
        token= Appconfig.TOKEN;
        actionBar.setTitle("Opportunity Call");

        rv=(RecyclerView)findViewById(R.id.rv);
        for (int i=0;i<15;i++)
        {
            OppCall oppCall=new OppCall();
            oppCall.setDate("Febuary 26,2016");
            oppCall.setCall_summary("vcxvvcvbcbvfvcbcvbbvgbbgfbgbbbfbbfbzvbb");
            oppCall.setCompany("gfvgvdfy");
            oppCall.setResponsible("dvfbfddbffgf");
            oppCall.setCall_id(3);
            oppCallArrayList.add(oppCall);
        }
        rv.setItemAnimator(new DefaultItemAnimator());
         // mAdapter=new OpportunityCallAdapter(oppCallArrayList,CallActivity.this);
        // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
        RecyclerView.LayoutManager lmanager=new LinearLayoutManager(CallActivity.this, LinearLayoutManager.VERTICAL,false);
        //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);

        rv.setLayoutManager(lmanager);
               rv.setAdapter(mAdapter);

        final com.getbase.floatingactionbutton.FloatingActionButton actionA = (com.getbase.floatingactionbutton.FloatingActionButton)findViewById(R.id.action_a);
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent i=new Intent(LeadCallActivity.this,AddLeadActivity.class);
                //startActivity(i);
                //Toast.makeText(getActivity(), " Action A is clicked",Toast.LENGTH_SHORT).show();
                final Dialog dialog = new Dialog(CallActivity.this);
                dialog.setContentView(R.layout.opp_call_add_dialog);

                date=(EditText)dialog.findViewById(R.id.date);
                final EditText call_summary=(EditText)dialog.findViewById(R.id.call_sumaary);
                company=(BetterSpinner)dialog.findViewById(R.id.company);
                responsible=(BetterSpinner)dialog.findViewById(R.id.responsible);
                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(date.getWindowToken(), 0);
                        showDialog(DATE_PICKER_ID);
                      /*  spf=getSharedPreferences("opp_add_code", Context.MODE_PRIVATE);
                        et=spf.edit();
                        et.putInt("add",1);
                        et.commit();*/
                        Log.i("date1--",current_month_text+""+current_day+""+current_year);


                    }
                });

                for (int i=0;i<5;i++)
                {
                    companyList.add("gdfhghgfh");
                    responsibleList.add("Service");
                }
                ArrayAdapter<String> companyArrayAdapter=new ArrayAdapter<String>(CallActivity.this,android.R.layout.simple_spinner_item,companyList);
                companyArrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
                company.setAdapter(companyArrayAdapter);
                ArrayAdapter<String> responsibleArrayAdapter=new ArrayAdapter<String>(CallActivity.this,android.R.layout.simple_spinner_item,responsibleList);
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

          /*      add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String c_name=company_name.getText().toString();
                        String c_email=company_email.getText().toString();
                        new AddCompanyTask().execute(token,c_name,c_email);
                    }
                });*/
                dialog.setCancelable(false);
                // dialog.setTitle("Lead Details");
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                dialog.show();


            }
        });
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id==DATE_PICKER_ID)
        {
            return new DatePickerDialog(CallActivity.this, date_listener,current_year,current_month,current_day);
        }
         if (id==DATE_PICKER_ID_EDIT){
             return new DatePickerDialog(CallActivity.this, edit_date_listener,current_year,current_month,current_day);

         }
        return null;
    }
    final DatePickerDialog.OnDateSetListener date_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            current_year=year;
            current_month=month;
            current_day=dayOfMonth;
            current_month_text=new DateFormatSymbols().getMonths()[current_month];
            /*spf=getSharedPreferences("opp_call_pref", Context.MODE_PRIVATE);
                Log.i("date3--",current_month_text+""+current_day+""+current_year);
            *//* et.putString("current_month_text",current_month_text);
            et.putInt("current_day",current_day);
            et.putInt("current_year",current_year);
            et.commit();*//*
          *//*  if (spf.getInt("add",0)==1)
           {       Log.i("add code--", String.valueOf(spf.getInt("add",0)));

           }*/

            date.setText(current_month_text+current_day+","+current_year);
            date.setSelection(date.getText().length());
            date.clearFocus();
        }
    };
    final DatePickerDialog.OnDateSetListener edit_date_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            current_year=year;
            current_month=month;
            current_day=dayOfMonth;
            current_month_text=new DateFormatSymbols().getMonths()[current_month];
            Log.i("date3--",current_month_text+""+current_day+""+current_year);
       /*     edit_date.setText(current_month_text+current_day+","+current_year);
            edit_date.setSelection(edit_date.getText().length());
            edit_date.clearFocus();*/

        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
