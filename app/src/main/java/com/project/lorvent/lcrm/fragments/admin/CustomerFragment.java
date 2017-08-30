package com.project.lorvent.lcrm.fragments.admin;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.fragments.admin.customerfrag.CompanyFragment;
import com.project.lorvent.lcrm.fragments.admin.customerfrag.ContactPersonFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerFragment extends Fragment {
    Adapter adapter;
    TabLayout tabLayout;
    public CustomerFragment() {
        // Required empty public constructor
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getChildFragmentManager(),getActivity());
        adapter.addFragment(new CompanyFragment(), "Company");
        adapter.addFragment(new ContactPersonFragment(), "Contact Person");
        viewPager.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_customer, container, false);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Customer");
        }
        tabLayout = (TabLayout)v.findViewById(R.id.tab_layout);
        final ViewPager viewPager = (ViewPager)v.findViewById(R.id.pager);

        setupViewPager(viewPager);
        //tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        NavigationView navigationView=(NavigationView) getActivity().findViewById(R.id.nav_view);
                        navigationView.getMenu().findItem(R.id.dashboard).setChecked(true);
                        Fragment fragment1 = new DashboardFragment();
                        FragmentTransaction trans1 = getFragmentManager().beginTransaction();
                        trans1.replace(R.id.frame, fragment1);
                        trans1.addToBackStack(null);
                        trans1.commit();
                        return true;
                    }
                }
                return false;
            }
        });
        return v;
    }
    public static  class Adapter extends FragmentStatePagerAdapter {
        //int mNumOfTabs;

        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        Context context;

        Adapter(FragmentManager fm, Context context) {
            super(fm);
            this.context=context;

        }

        void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragments.get(position);
        }
        @Override
        public int getCount() {
            return mFragments.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {

            int[]tabIcon={R.mipmap.ic_assistant_photo_white_24dp,R.mipmap.ic_contact_phone_white_24dp};

            Drawable image = context.getResources().getDrawable(tabIcon[position]);

            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString("        "+mFragmentTitles.get(position));
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
            //return mFragmentTitles.get(position);
        }


    }
}
