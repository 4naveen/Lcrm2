package com.project.lorvent.lcrm.activities.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.project.lorvent.lcrm.activities.AboutUsActivity;
import com.project.lorvent.lcrm.activities.HelpActivity;
import com.project.lorvent.lcrm.activities.MailActivity;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.user_profile.UserProfile2Activity;
import com.project.lorvent.lcrm.fragments.admin.CalendarFragment;
import com.project.lorvent.lcrm.fragments.admin.ContractsFragment;
import com.project.lorvent.lcrm.fragments.admin.CustomerFragment;
import com.project.lorvent.lcrm.fragments.admin.DashboardFragment;
import com.project.lorvent.lcrm.fragments.admin.InvoiceFragment;
import com.project.lorvent.lcrm.fragments.admin.LeadsFragment;
import com.project.lorvent.lcrm.fragments.admin.LoggedCallsFragment;
import com.project.lorvent.lcrm.fragments.admin.MeetingFragment;
import com.project.lorvent.lcrm.fragments.admin.OpportnityFragment;
import com.project.lorvent.lcrm.fragments.admin.ProductFragment;
import com.project.lorvent.lcrm.fragments.admin.QuotationFragment;
import com.project.lorvent.lcrm.fragments.admin.SalesOrderFragment;
import com.project.lorvent.lcrm.fragments.admin.SalesTeamFragment;
import com.project.lorvent.lcrm.fragments.admin.StaffFragment;
import com.project.lorvent.lcrm.fragments.admin.TaskFragment;
import com.project.lorvent.lcrm.utils.Convertor;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home2Activity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    ImageView profile;
    NavigationView navigationView;
    String calling_from;
    TextView user_name,email;
    CircleImageView profile_image;
    Bitmap bitmap;
    static String salesteam_read,salesteam_write,salesteam_delete,lead_read,lead_write,lead_delete,opp_read,opp_write,opp_delete,loggedcall_read,
            loggedcall_write,loggedcall_delete,meeting_read,meeting_write,meeting_delete,products_read,products_write,products_delete,
            quotation_read,quotation_write,quotation_delete,salesorder_read,salesorder_write,salesorder_delete,invoices_read,invoices_write,invoices_delete,
            contracts_read,contracts_write,contracts_delete,staff_read,staff_write,staff_delete,contacts_read,contacts_write,contacts_delete;;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        calling_from = getIntent().getStringExtra("calling from");

        if (calling_from.equals("login")) {

        salesteam_read = getIntent().getStringExtra("salesteam_read");
        salesteam_write = getIntent().getStringExtra("salesteam_write");
        salesteam_delete = getIntent().getStringExtra("salesteam_delete");
        lead_read = getIntent().getStringExtra("leads_read");
        lead_write = getIntent().getStringExtra("leads_write");
        lead_delete = getIntent().getStringExtra("leads_delete");
        opp_read = getIntent().getStringExtra("opp_read");
        opp_write = getIntent().getStringExtra("opp_write");
        opp_delete = getIntent().getStringExtra("opp_delete");
        loggedcall_read = getIntent().getStringExtra("loggedcall_read");
        loggedcall_write = getIntent().getStringExtra("loggedcall");
        loggedcall_delete = getIntent().getStringExtra("loggedcall");
        meeting_read = getIntent().getStringExtra("meeting_read");
        meeting_write = getIntent().getStringExtra("meeting_write");
        meeting_delete = getIntent().getStringExtra("meeting_delete");
        products_read = getIntent().getStringExtra("products_read");
        products_write = getIntent().getStringExtra("products_write");
        products_delete = getIntent().getStringExtra("products_delete");
        quotation_read = getIntent().getStringExtra("quotation_read");
        quotation_write = getIntent().getStringExtra("quotation_write");
        quotation_delete = getIntent().getStringExtra("quotation_delete");
        salesorder_read = getIntent().getStringExtra("salesorder_read");
        salesorder_write = getIntent().getStringExtra("salesorder_write");
        salesorder_delete = getIntent().getStringExtra("salesorder_delete");
        invoices_read = getIntent().getStringExtra("invoices_read");
        invoices_write = getIntent().getStringExtra("invoices_write");
        invoices_delete = getIntent().getStringExtra("invoices_delete");
        contracts_read = getIntent().getStringExtra("contracts_read");
        contracts_write = getIntent().getStringExtra("contracts_write");
        contracts_delete = getIntent().getStringExtra("contracts_delete");
        staff_read = getIntent().getStringExtra("staff_read");
        staff_write = getIntent().getStringExtra("staff_write");
        staff_delete = getIntent().getStringExtra("staff_delete");
        contacts_read = getIntent().getStringExtra("contacts_read");
        contacts_write = getIntent().getStringExtra("contacts_write");
        contacts_delete = getIntent().getStringExtra("contacts_delete");


        if (salesteam_read.equals("true")) {
            AppSession.salesteam_read = 1;
        } else {
            AppSession.salesteam_read = 0;
        }
        if (salesteam_write.equals("true")) {
            AppSession.salesteam_write = 1;
        } else {
            AppSession.salesteam_write = 0;
        }
        if (salesteam_delete.equals("true")) {
            AppSession.salesteam_delete = 1;
        } else {
            AppSession.salesteam_delete = 0;
        }

        if (lead_read.equals("true")) {
            AppSession.lead_read = 1;
        } else {
            AppSession.lead_read = 0;
        }
        if (lead_write.equals("true")) {
            AppSession.lead_write = 1;
        } else {
            AppSession.lead_write = 0;
        }
        if (lead_delete.equals("true")) {
            AppSession.lead_delete = 1;
        } else {
            AppSession.lead_delete = 0;
        }

        if (opp_read.equals("true")) {
            AppSession.opp_read = 1;
        } else {
            AppSession.opp_read = 0;
        }
        if (opp_write.equals("true")) {
            AppSession.opp_write = 1;
        } else {
            AppSession.opp_write = 0;
        }
        if (opp_delete.equals("true")) {
            AppSession.opp_delete = 1;
        } else {
            AppSession.opp_delete = 0;
        }

        if (loggedcall_read.equals("true")) {
            AppSession.loggedcall_read = 1;
        } else {
            AppSession.loggedcall_read = 0;
        }
        if (loggedcall_write.equals("true")) {
            AppSession.loggedcall_write = 1;
        } else {
            AppSession.loggedcall_write = 0;
        }
        if (loggedcall_delete.equals("true")) {
            AppSession.loggedcall_delete = 1;
        } else {
            AppSession.loggedcall_delete = 0;
        }

        if (meeting_read.equals("true")) {
            AppSession.meeting_read = 1;
        } else {
            AppSession.meeting_read = 0;
        }
        if (meeting_write.equals("true")) {
            AppSession.meeting_write = 1;
        } else {
            AppSession.meeting_write = 0;
        }
        if (meeting_delete.equals("true")) {
            AppSession.meeting_delete = 1;
        } else {
            AppSession.meeting_delete = 0;
        }

        if (products_read.equals("true")) {
            AppSession.products_read = 1;
        } else {
            AppSession.products_read = 1;
        }
        if (products_write.equals("true")) {
            AppSession.products_write = 1;
        } else {
            AppSession.products_write = 1;
        }
        if (products_delete.equals("true")) {
            AppSession.products_delete = 1;
        } else {
            AppSession.products_delete = 1;
        }


        if (quotation_read.equals("true")) {
            AppSession.quotation_read = 1;
        } else {
            AppSession.quotation_read = 0;
        }
        if (quotation_write.equals("true")) {
            AppSession.quotation_write = 1;
        } else {
            AppSession.quotation_write = 0;
        }
        if (quotation_delete.equals("true")) {
            AppSession.quotation_delete = 1;
        } else {
            AppSession.quotation_delete = 0;
        }


        if (salesorder_read.equals("true")) {
            AppSession.salesorder_read = 1;
        } else {
            AppSession.salesorder_read = 0;
        }
        if (salesorder_write.equals("true")) {
            AppSession.salesorder_write = 1;
        } else {
            AppSession.salesorder_write = 0;
        }
        if (salesorder_delete.equals("true")) {
            AppSession.salesorder_delete = 1;
        } else {
            AppSession.salesorder_delete = 0;
        }

        if (invoices_read.equals("true")) {
            AppSession.invoices_read = 1;
        } else {
            AppSession.invoices_read = 0;
        }
        if (invoices_write.equals("true")) {
            AppSession.invoices_write = 1;
        } else {
            AppSession.invoices_write = 0;
        }
        if (invoices_delete.equals("true")) {
            AppSession.invoices_delete = 1;
        } else {
            AppSession.invoices_delete = 0;
        }

        if (contracts_read.equals("true")) {
            AppSession.contracts_read = 1;
        } else {
            AppSession.contracts_read = 0;
        }
        if (contracts_write.equals("true")) {
            AppSession.contracts_write = 1;
        } else {
            AppSession.contracts_write = 0;
        }
        if (contracts_delete.equals("true")) {
            AppSession.contracts_delete = 1;
        } else {
            AppSession.contracts_delete = 0;
        }

        if (staff_read.equals("true")) {
            AppSession.staff_read = 1;
        } else {
            AppSession.staff_read = 0;
        }
        if (staff_write.equals("true")) {
            AppSession.staff_write = 1;
        } else {
            AppSession.staff_write = 0;
        }
        if (staff_delete.equals("true")) {
            AppSession.staff_delete = 1;
        } else {
            AppSession.staff_delete = 0;
        }

        if (contacts_read.equals("true")) {
            AppSession.contacts_read = 1;
        } else {
            AppSession.contacts_read = 0;
        }
        if (contacts_write.equals("true")) {
            AppSession.contacts_write = 1;
        } else {
            AppSession.contacts_write = 0;
        }
        if (contacts_delete.equals("true")) {
            AppSession.contacts_delete = 1;
        } else {
            AppSession.contacts_delete = 0;
        }

    }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (salesteam_read.equals("true")||salesteam_write.equals("true")||salesteam_delete.equals("true"))
        {
            //Log.i("salesteam", String.valueOf(salesteam_read));
            navigationView.getMenu().findItem(R.id.salesteam).setVisible(true);

//            menu.add("SalesTeam");
        }
        if (lead_read.equals("true")||lead_write.equals("true")||lead_delete.equals("true"))
        {
            //Log.i("salesteam", String.valueOf(lead_read));
            navigationView.getMenu().findItem(R.id.leads).setVisible(true);
        }
        if (opp_read.equals("true")||opp_write.equals("true")||opp_delete.equals("true"))
        {
            navigationView.getMenu().findItem(R.id.opportnity).setVisible(true);
        }
        if (loggedcall_read.equals("true")||loggedcall_write.equals("true")||loggedcall_delete.equals("true"))
        {
            navigationView.getMenu().findItem(R.id.loggedCalls).setVisible(true);
        }
        if (meeting_read.equals("true")||meeting_write.equals("true")||meeting_delete.equals("true"))
        {
            navigationView.getMenu().findItem(R.id.meetings).setVisible(true);
        }
        if (products_read.equals("true")||products_write.equals("true")||products_delete.equals("true"))
        {
            navigationView.getMenu().findItem(R.id.products).setVisible(true);
        }
        if (quotation_read.equals("true")||quotation_write.equals("true")||quotation_delete.equals("true"))
        {
            navigationView.getMenu().findItem(R.id.quotation).setVisible(true);
        }
        if (salesorder_read.equals("true")||salesorder_write.equals("true")||salesorder_delete.equals("true"))
        {
            navigationView.getMenu().findItem(R.id.salesorder).setVisible(true);
        }
        if (invoices_read.equals("true")||invoices_write.equals("true")||invoices_delete.equals("true"))
        {
            navigationView.getMenu().findItem(R.id.invoices).setVisible(true);
        }
        if (contracts_read.equals("true")||contracts_write.equals("true")||contracts_delete.equals("true"))
        {
            navigationView.getMenu().findItem(R.id.contracts).setVisible(true);
        }
        if (staff_read.equals("true")||staff_write.equals("true")||staff_delete.equals("true"))
        {
            navigationView.getMenu().findItem(R.id.staff).setVisible(true);
        }
        if (contacts_read.equals("true")||contacts_write.equals("true")||contacts_delete.equals("true"))
        {
            navigationView.getMenu().findItem(R.id.customer).setVisible(true);
        }
        View v1=navigationView.inflateHeaderView(R.layout.nav_header_home);
        profile=(ImageView) v1.findViewById(R.id.profile);
        user_name=(TextView)v1.findViewById(R.id.uname);
        email=(TextView)v1.findViewById(R.id.email);
        user_name.setText(AppSession.first_name);
        email.setText(AppSession.user_email);
        profile_image=(CircleImageView)v1.findViewById(R.id.profile_image);
        getImage(AppSession.image_url);
        Fragment fragment1=new DashboardFragment();
        FragmentTransaction trans1=getSupportFragmentManager().beginTransaction();
        trans1.replace(R.id.frame,fragment1);
        trans1.addToBackStack(null);
        trans1.commit();
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Home2Activity.this,UserProfile2Activity.class);
                i.putExtra("image_bytes", Convertor.getImageBytes(bitmap));
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
        finish();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction2=getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        navigationView.getMenu().findItem(item.getItemId()).setChecked(true);

        Bundle bundle=new Bundle();
        if (item.getItemId()==R.id.dashboard)
          {
              fragment=new DashboardFragment();

          }
        else if(item.getItemId()==R.id.tasks)
          {
              fragment=new TaskFragment();

          }
        else if(item.getItemId()==R.id.calender)
          {
              fragment=new CalendarFragment();

          }
         else if (navigationView.getMenu().findItem(R.id.opportnity).isVisible()&&item.getItemId()==R.id.opportnity)
         {
             fragment=new OpportnityFragment();
         }
          else if (navigationView.getMenu().findItem(R.id.leads).isVisible()&&item.getItemId()==R.id.leads)
          {     fragment=new LeadsFragment();
             // bundle.putString("leads_write", String.valueOf(lead_write));
            //  fragment.setArguments(bundle);
          }
        else if (navigationView.getMenu().findItem(R.id.salesteam).isVisible()&&item.getItemId()==R.id.salesteam)
        {
            fragment=new SalesTeamFragment();
            //bundle.putString("salesteam_read", String.valueOf(salesteam_read));
           // fragment.setArguments(bundle);
        }
        else if (navigationView.getMenu().findItem(R.id.loggedCalls).isVisible()&&item.getItemId()==R.id.loggedCalls)
        {
            fragment=new LoggedCallsFragment();

        }
        else if (navigationView.getMenu().findItem(R.id.meetings).isVisible()&&item.getItemId()==R.id.meetings)
        {
            fragment=new MeetingFragment();

        }
        else if (navigationView.getMenu().findItem(R.id.products).isVisible()&&item.getItemId()==R.id.products)
        {
            fragment=new ProductFragment();
        }
        else if (navigationView.getMenu().findItem(R.id.quotation).isVisible()&&item.getItemId()==R.id.quotation)
        {
            fragment=new QuotationFragment();

        }
        else if (navigationView.getMenu().findItem(R.id.salesorder).isVisible()&&item.getItemId()==R.id.salesorder)
        {
            fragment=new SalesOrderFragment();

        }
        else if (navigationView.getMenu().findItem(R.id.invoices).isVisible()&&item.getItemId()==R.id.invoices)
        {
            fragment= new InvoiceFragment();

        }
        else if (navigationView.getMenu().findItem(R.id.contracts).isVisible()&&item.getItemId()==R.id.contracts)
        {
            fragment=new ContractsFragment();

        }
        else if (navigationView.getMenu().findItem(R.id.staff).isVisible()&&item.getItemId()==R.id.staff)
        {
            fragment=new StaffFragment();

        }
        else if (navigationView.getMenu().findItem(R.id.customer).isVisible()&&item.getItemId()==R.id.customer)
        {
            fragment=new CustomerFragment();
        }
        else
        {
            ///do nothing
        }
        transaction2.replace(R.id.frame,fragment);
        transaction2.addToBackStack(null);
        transaction2.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
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
        MyVolleySingleton.getInstance(Home2Activity.this).getRequestQueue().add(ir);
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
            Intent i=new Intent(Home2Activity.this,MailActivity.class);
            startActivity(i);
        }

        if (item.getItemId()==R.id.help)
        {
            Intent i=new Intent(Home2Activity.this,HelpActivity.class);
            startActivity(i);
        }
        if (item.getItemId()==R.id.about)
        {
            Intent i=new Intent(Home2Activity.this,AboutUsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);

    }
}
