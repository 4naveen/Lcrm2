package com.project.naveen.lcrm;

import android.content.Intent;
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

import com.project.naveen.lcrm.menu.customer.fragment.ContractsFragment;
import com.project.naveen.lcrm.menu.customer.fragment.DashboardFragment;
import com.project.naveen.lcrm.menu.customer.fragment.InvoicesFragment;
import com.project.naveen.lcrm.menu.customer.fragment.QuoatationFragment;
import com.project.naveen.lcrm.menu.customer.fragment.SalesOrderFragment;


public class Home1Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button profile;
    TextView user_name,email;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setLogo(R.drawable.logo);
        Appconfig.home1Activity=Home1Activity.this;
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
                Intent i = new Intent(Home1Activity.this, UserProfile1Activity.class);
                startActivity(i);
                finish();
                Log.i("header--", "onclick");
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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction2=getSupportFragmentManager().beginTransaction();
        Fragment fragment = null;
        navigationView.getMenu().findItem(item.getItemId()).setChecked(true);
        switch (item.getItemId()) {
            case R.id.dashboard: {

                fragment = new DashboardFragment();

                break;
            }
            case R.id.invoices: {

                fragment = new InvoicesFragment();

                break;
            }
            case R.id.quotation: {

                fragment = new QuoatationFragment();

                break;
            }
            case R.id.salesorder: {

                fragment = new SalesOrderFragment();

                break;
            }
            case R.id.contracts: {

                fragment = new ContractsFragment();

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
}