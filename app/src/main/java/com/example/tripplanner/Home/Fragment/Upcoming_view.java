package com.example.tripplanner.Home.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tripplanner.Adapter.UpcomingRecycleAdapter;
import com.example.tripplanner.Home.Activity.AddActivity;
import com.example.tripplanner.Home.Activity.Home_Activity;
import com.example.tripplanner.R;
import com.example.tripplanner.TripData.Final;
import com.example.tripplanner.TripData.Trip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;


public class Upcoming_view extends Fragment {
    //reference fButton and recycle view
    FloatingActionButton floatingBtnAdd;
    RecyclerView tripRecycleView;
    //reference to adapter
    private UpcomingRecycleAdapter upcomingRecycleAdapter;
    private List tripsList = new ArrayList<Trip>();


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tripRecycleView = view.findViewById(R.id.trip_recycleView);
        upcomingRecycleAdapter = new UpcomingRecycleAdapter(tripsList, getContext(), getActivity());
        floatingBtnAdd = view.findViewById(R.id.add_flout_btn);
        addTrip();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upcomingtrips, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        new addTripRoom().execute();
    }

    private void addTrip() {

        floatingBtnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("KEY", 1);
            bundle.putInt("ID", -1);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }



    private class addTripRoom extends AsyncTask<Void, Void, List<Trip>> {


        @Override
        protected List<Trip> doInBackground(Void...voids) {
            return Home_Activity.database.tripDAO().selectUpcomingTrip(Home_Activity.fireBaseUserId, Final.UPCOMING_TRIP_STATUS);
        }

        @Override
        protected void onPostExecute(List<Trip> trips) {
            super.onPostExecute(trips);
            tripsList = trips;
            upcomingRecycleAdapter = new UpcomingRecycleAdapter(tripsList, getContext(), getActivity());
            tripRecycleView.setAdapter(upcomingRecycleAdapter);
        }
    }
}
