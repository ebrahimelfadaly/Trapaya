package com.example.tripplanner.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tripplanner.Home.Fragment.History_view;
import com.example.tripplanner.Home.Fragment.Profile_view;
import com.example.tripplanner.Home.Fragment.Upcoming_view;


public class ViewerPageAdapter extends FragmentStateAdapter {


    public ViewerPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Upcoming_view();
            case 1:
                return new History_view();
            default:
                return new Profile_view();
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
