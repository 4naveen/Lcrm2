package com.project.lorvent.lcrm.fragments.admin;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.lorvent.lcrm.R;
import com.project.lorvent.lcrm.fragments.admin.settingsfrag.BackupFragment;
import com.project.lorvent.lcrm.fragments.admin.settingsfrag.GeneralFragment;
import com.project.lorvent.lcrm.fragments.admin.settingsfrag.KeyConfigFragment;
import com.project.lorvent.lcrm.fragments.admin.settingsfrag.PaymentFragment;
import com.project.lorvent.lcrm.fragments.admin.settingsfrag.StartNumFragment;
import com.ss.bottomnavigation.BottomNavigation;
import com.ss.bottomnavigation.events.OnSelectedItemChangeListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment {
    Fragment fragment;
    FragmentTransaction transaction;
    BottomNavigation bottomNavigation;

    public SettingFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_setting, container, false);

         bottomNavigation=(BottomNavigation)v.findViewById(R.id.bottom_navigation);

        setHasOptionsMenu(true);

//        bottomNavigation.setDefaultItem(1);
        bottomNavigation.setOnSelectedItemChangeListener(new OnSelectedItemChangeListener() {
            @Override
            public void onSelectedItemChanged(int i) {
                switch (i)
                {
                    case R.id.general:
                    { fragment=new GeneralFragment();

                        break;
                    }
                    case R.id.payment:
                    { fragment=new PaymentFragment();
                        break;
                    }

                    case R.id.start_number:
                   { fragment=new StartNumFragment();

                    break;
                   }
                    case R.id.pusher_Config:
                    { fragment=new KeyConfigFragment();
                        break;
                    }
                    case R.id.backup:
                    {

                        fragment=new BackupFragment();
                        break;
                    }

                }
                transaction=getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.frame,fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
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

}
