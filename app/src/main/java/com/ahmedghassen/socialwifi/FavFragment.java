package com.ahmedghassen.socialwifi;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FavFragment extends Fragment {

    public FavFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fav, container, false);;

        TabLayout tabLayout = (TabLayout) (view).findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Online Map"));
        tabLayout.addTab(tabLayout.newTab().setText("Offline Map"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        FragmentManager manager = getActivity().getSupportFragmentManager();

        final ViewPager viewPager = (ViewPager) (view).findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (manager, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).isSelected();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                viewPager.clearOnPageChangeListeners();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }
}
