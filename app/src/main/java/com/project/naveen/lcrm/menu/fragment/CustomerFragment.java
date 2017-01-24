package com.project.naveen.lcrm.menu.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.menu.fragment.customerfrag.CompanyFragment;
import com.project.naveen.lcrm.menu.fragment.customerfrag.ContactPersonFragment;

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
        adapter = new Adapter(getChildFragmentManager());
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

        return v;
    }
    public static  class Adapter extends FragmentStatePagerAdapter {
        //int mNumOfTabs;

        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        Adapter(FragmentManager fm) {
            super(fm);

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


            return mFragmentTitles.get(position);
        }


    }
}
