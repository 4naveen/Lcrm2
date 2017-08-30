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
import com.project.lorvent.lcrm.activities.AboutUsActivity;
import com.project.lorvent.lcrm.activities.HelpActivity;
import com.project.lorvent.lcrm.activities.MailActivity;
import com.project.lorvent.lcrm.utils.AppSession;
import com.project.lorvent.lcrm.utils.Appconfig;
import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.activities.user_profile.UserProfile1Activity;
import com.project.lorvent.lcrm.fragments.customers.ContractsFragment;
import com.project.lorvent.lcrm.fragments.customers.DashboardFragment;
import com.project.lorvent.lcrm.fragments.customers.InvoicesFragment;
import com.project.lorvent.lcrm.fragments.customers.QuoatationFragment;
import com.project.lorvent.lcrm.fragments.customers.SalesOrderFragment;
import com.project.lorvent.lcrm.utils.Convertor;
import com.project.lorvent.lcrm.utils.MyVolleySingleton;

import de.hdodenhof.circleimageview.CircleImageView;


public class Home1Activity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ImageView profile;
    TextView user_name,email;
    NavigationView navigationView;
    CircleImageView profile_image;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Appconfig.home1Activity=Home1Activity.this;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        Fragment fragment1=new DashboardFragment();
        FragmentTransaction trans1=getSupportFragmentManager().beginTransaction();
        trans1.replace(R.id.frame,fragment1);
        trans1.addToBackStack(null);
        trans1.commit();
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Home1Activity.this, UserProfile1Activity.class);
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
        if (item.getItemId()==R.id.mail)
        {
            Intent i=new Intent(Home1Activity.this,MailActivity.class);
            startActivity(i);
        }

        if (item.getItemId()==R.id.help)
        {
            Intent i=new Intent(Home1Activity.this,HelpActivity.class);
            startActivity(i);
        }
        if (item.getItemId()==R.id.about)
        {
            Intent i=new Intent(Home1Activity.this,AboutUsActivity.class);
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
        MyVolleySingleton.getInstance(Home1Activity.this).getRequestQueue().add(ir);

    }
}