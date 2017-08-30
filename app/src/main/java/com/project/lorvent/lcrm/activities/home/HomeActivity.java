package com.project.lorvent.lcrm.activities.home;

import android.content.Intent;
import android.graphics.Bitmap;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.AboutUsActivity;
import com.project.lorvent.lcrm.activities.HelpActivity;
import com.project.lorvent.lcrm.activities.MailActivity;
import com.project.lorvent.lcrm.activities.user_profile.UserProfileActivity;
import com.project.lorvent.lcrm.fragments.admin.CalendarFragment;
import com.project.lorvent.lcrm.fragments.admin.ContractsFragment;
import com.project.lorvent.lcrm.fragments.admin.CustomerFragment;
import com.project.lorvent.lcrm.fragments.admin.DashboardFragment;
import com.project.lorvent.lcrm.fragments.admin.EtemplateFragment;
import com.project.lorvent.lcrm.fragments.admin.InvoiceFragment;
import com.project.lorvent.lcrm.fragments.admin.LeadsFragment;
import com.project.lorvent.lcrm.fragments.admin.LoggedCallsFragment;
import com.project.lorvent.lcrm.fragments.admin.MeetingFragment;
import com.project.lorvent.lcrm.fragments.admin.OpportnityFragment;
import com.project.lorvent.lcrm.fragments.admin.ProductFragment;
import com.project.lorvent.lcrm.fragments.admin.QtemplateFragment;
import com.project.lorvent.lcrm.fragments.admin.QuotationFragment;
import com.project.lorvent.lcrm.fragments.admin.SalesOrderFragment;
import com.project.lorvent.lcrm.fragments.admin.SalesTeamFragment;
import com.project.lorvent.lcrm.fragments.admin.SettingFragment;
import com.project.lorvent.lcrm.fragments.admin.StaffFragment;
import com.project.lorvent.lcrm.fragments.admin.TaskFragment;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.utils.Connection;
import com.project.lorvent.lcrm.utils.Convertor;
import com.project.lorvent.lcrm.utils.LogoutService;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView user_name,email;
    ImageView profile;
    NavigationView navigationView;
    DrawerLayout drawer;
    CircleImageView profile_image;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Connection.getDateSettings(Appconfig.TOKEN,HomeActivity.this);
        Appconfig.homeActivity=HomeActivity.this;
        AppSession.customerNameList = new ArrayList<>();
        AppSession.productNameList = new ArrayList<>();
        AppSession.companyNameList = new ArrayList<>();
        AppSession.quotation_templateNameList = new ArrayList<>();
        AppSession.sales_teamNameList = new ArrayList<>();
        AppSession.sales_personNameList = new ArrayList<>();
        AppSession.teamLeaderNameList = new ArrayList<>();
        AppSession.teamMemberNameList = new ArrayList<>();
        AppSession.qTemplateList = new ArrayList<>();
        AppSession.salesTeamList = new ArrayList<>();
        AppSession.salesPersonList = new ArrayList<>();
        AppSession.customerList = new ArrayList<>();
        AppSession.companyList = new ArrayList<>();
        AppSession.teamLeaderList = new ArrayList<>();
        AppSession.teamMemberList = new ArrayList<>();
        AppSession.responsibleList = new ArrayList<>();
        AppSession.responsibleNameList = new ArrayList<>();

        AppSession.productsArrayList = new ArrayList<>();

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
        Connection.getDateSettings(Appconfig.TOKEN,HomeActivity.this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v1=navigationView.inflateHeaderView(R.layout.nav_header_home);
             profile=(ImageView) v1.findViewById(R.id.profile);
            user_name=(TextView)v1.findViewById(R.id.uname);
            email=(TextView)v1.findViewById(R.id.email);
            user_name.setText(AppSession.first_name);
            email.setText(AppSession.user_email);
            profile_image=(CircleImageView)v1.findViewById(R.id.profile_image);
              getImage(AppSession.image_url);
        Bundle bundle=new Bundle();
       // Log.i("date_format",AppSession.date_format);
        Fragment fragment1=new DashboardFragment();
        FragmentTransaction trans1=getSupportFragmentManager().beginTransaction();
        trans1.replace(R.id.frame,fragment1);
        trans1.addToBackStack(null);
        trans1.commit();
        profile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
         Intent i=new Intent(HomeActivity.this,UserProfileActivity.class);
        i.putExtra("image_bytes",Convertor.getImageBytes(bitmap));

        startActivity(i);
        finish();
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
        Intent i = new Intent(HomeActivity.this, LogoutService.class);
        stopService(i);
        finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getImage(AppSession.image_url);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getImage(AppSession.image_url);

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
            {
                fragment=new LoggedCallsFragment();
                break;

            }
            case R.id.salesorder:
            {
                fragment=new SalesOrderFragment();
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
            case R.id.email_template:
            {
                fragment=new EtemplateFragment();
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
        if (item.getItemId()==R.id.mail)
        {
            Intent i=new Intent(HomeActivity.this,MailActivity.class);
            startActivity(i);
        }

        if (item.getItemId()==R.id.help)
        {
            Intent i=new Intent(HomeActivity.this,HelpActivity.class);
            startActivity(i);
        }
        if (item.getItemId()==R.id.about)
        {
            Intent i=new Intent(HomeActivity.this,AboutUsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);

    }
    private void getImage(String image_url)
    {
        ImageRequest ir = new ImageRequest(image_url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                profile_image.setImageBitmap(response);
                bitmap=response;
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                profile_image.setImageResource(R.drawable.upload);

            }
        });
        MyVolleySingleton.getInstance(HomeActivity.this).getRequestQueue().add(ir);

    }

}
