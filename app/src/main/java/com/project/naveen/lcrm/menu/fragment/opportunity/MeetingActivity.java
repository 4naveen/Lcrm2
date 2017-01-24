package com.project.naveen.lcrm.menu.fragment.opportunity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.project.naveen.lcrm.Appconfig;
import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.adapters.subadapter.OppMeetingAdapter;
import com.project.naveen.lcrm.models.submodels.OppMeeting;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import static com.project.naveen.lcrm.adapters.subadapter.OppMeetingAdapter.EDIT_END_DATE_PICKER_ID;
import static com.project.naveen.lcrm.adapters.subadapter.OppMeetingAdapter.EDIT_START_DATE_PICKER_ID;



public class MeetingActivity extends AppCompatActivity {
    ArrayList<OppMeeting> oppMeetingArrayList;
    RecyclerView rv;
    String token;
    OppMeetingAdapter mAdapter;
    String lead_id;
    static final int START_DATE_PICKER_ID=111;
    static final int END_DATE_PICKER_ID=112;
    public static int  current_year,current_month,current_day;
    public static String current_month_text;
    EditText start_date,end_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting2);

        ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        oppMeetingArrayList =new ArrayList<>();
        token= Appconfig.TOKEN;
        actionBar.setTitle("Opportunity Meeting");

        rv=(RecyclerView)findViewById(R.id.rv);

        for (int i=0;i<15;i++)
        {
            OppMeeting oppMeeting=new OppMeeting();
            oppMeeting.setStart_date("Febuary 26,2016");
            oppMeeting.setEnd_date("Febuary 27,2016");
            oppMeeting.setMeeting_subject("gfvgvdfy");
            oppMeeting.setResponsible("dvfbfddbffgf");
            oppMeetingArrayList.add(oppMeeting);
        }

        rv.setItemAnimator(new DefaultItemAnimator());
      //  mAdapter=new OppMeetingAdapter(oppMeetingArrayList, MeetingActivity.this);
        // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
        RecyclerView.LayoutManager lmanager=new LinearLayoutManager(MeetingActivity.this, LinearLayoutManager.VERTICAL,false);
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
                final Dialog dialog = new Dialog(MeetingActivity.this);
                dialog.setContentView(R.layout.opp_meeting_add_dialog);

                start_date=(EditText)dialog.findViewById(R.id.start_date);
                start_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(start_date.getWindowToken(), 0);
                        showDialog(START_DATE_PICKER_ID);

                    }
                });

                end_date=(EditText)dialog.findViewById(R.id.end_date);
                end_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(end_date.getWindowToken(), 0);
                        showDialog(END_DATE_PICKER_ID);

                    }
                });
                final EditText meeting=(EditText)dialog.findViewById(R.id.meeting);

                final EditText responsible=(EditText)dialog.findViewById(R.id.responsible);

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
        if (id==START_DATE_PICKER_ID)
        {
            return new DatePickerDialog(MeetingActivity.this, start_date_listener,current_year,current_month,current_day);
        }
        if (id==END_DATE_PICKER_ID)
        {
            return new DatePickerDialog(MeetingActivity.this, end_date_listener,current_year,current_month,current_day);
        }
        if (id==EDIT_START_DATE_PICKER_ID)
        {
            return new DatePickerDialog(MeetingActivity.this, edit_start_date_listener,current_year,current_month,current_day);
        }
        if (id==EDIT_END_DATE_PICKER_ID)
        {
            return new DatePickerDialog(MeetingActivity.this, edit_end_date_listener,current_year,current_month,current_day);
        }

        return null;
    }
    final DatePickerDialog.OnDateSetListener start_date_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            current_year=year;
            current_month=month;
            current_month_text=new DateFormatSymbols().getMonths()[current_month];
            current_day=dayOfMonth;
            start_date.setText(current_month_text+current_day+","+current_year);
            start_date.setSelection(start_date.getText().length());
            start_date.clearFocus();
        }
    };
    final DatePickerDialog.OnDateSetListener end_date_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            current_year=year;
            current_month=month;
            current_month_text=new DateFormatSymbols().getMonths()[current_month];
            current_day=dayOfMonth;
            end_date.setText(current_month_text+current_day+","+current_year);
            end_date.setSelection(end_date.getText().length());
            end_date.clearFocus();
        }
    };
    final DatePickerDialog.OnDateSetListener edit_start_date_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            current_year=year;
            current_month=month;
            current_month_text=new DateFormatSymbols().getMonths()[current_month];
            current_day=dayOfMonth;
       /*     edit_start_date.setText(current_month_text+current_day+","+current_year);
            edit_start_date.setSelection(start_date.getText().length());
            edit_start_date.clearFocus();*/
        }
    };
    final DatePickerDialog.OnDateSetListener edit_end_date_listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            current_year=year;
            current_month=month;
            current_month_text=new DateFormatSymbols().getMonths()[current_month];
            current_day=dayOfMonth;
          /*  edit_end_date.setText(current_month_text+current_day+","+current_year);
            edit_end_date.setSelection(start_date.getText().length());
            edit_end_date.clearFocus();*/
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
