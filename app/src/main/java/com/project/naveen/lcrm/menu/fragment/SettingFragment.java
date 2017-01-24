package com.project.naveen.lcrm.menu.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.naveen.lcrm.R;
import com.project.naveen.lcrm.menu.fragment.settings.BackupFragment;
import com.project.naveen.lcrm.menu.fragment.settings.GeneralFragment;
import com.project.naveen.lcrm.menu.fragment.settings.KeyConfigFragment;
import com.project.naveen.lcrm.menu.fragment.settings.PaymentFragment;
import com.project.naveen.lcrm.menu.fragment.settings.StartNumFragment;
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
        View v=inflater.inflate(R.layout.fragment_setting,container, false);
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

        return v;

    }

}
