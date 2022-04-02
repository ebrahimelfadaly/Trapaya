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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tripplanner.Adapter.HistoryTripAdapter;
import com.example.tripplanner.Home.Activity.Home_Activity;
import com.example.tripplanner.Home.Activity.MapActivity;
import com.example.tripplanner.R;
import com.example.tripplanner.TripData.Final;
import com.example.tripplanner.TripData.Trip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.List;


public class History_view extends Fragment {
    RecyclerView recyclerView;
    HistoryTripAdapter adapter;
    FloatingActionButton map;
    int finishedTripsNum;
    List tripList=new ArrayList<Trip>();

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView=view.findViewById(R.id.recyclehistory);
        adapter=new HistoryTripAdapter(tripList,getContext(),getActivity());
        LinearLayoutManager manager=new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        map=view.findViewById(R.id.map_float_btn);
        map.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapActivity.class);
            startActivity(intent);
        });
    }

  
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_history, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();
        new addHistoryRoom().execute();
    }

    private class addHistoryRoom extends AsyncTask<Void, Void, List<Trip>> {
        @Override
        protected List<Trip> doInBackground(Void...voids) {
            finishedTripsNum=Home_Activity.database.tripDAO().getCountTripType(Home_Activity.fireBaseUserId, Final.FINISHED_TRIP_STATUS);
            return Home_Activity.database.tripDAO().selectHistoryTrip(Home_Activity.fireBaseUserId,
                    Final.CANCEL_TRIP_STATUS,Final.FINISHED_TRIP_STATUS,Final.MISSED_TRIP_STATUS);

        }

        @Override
        protected void onPostExecute(List<Trip> trips) {
            super.onPostExecute(trips);
            tripList = trips;

            adapter=new HistoryTripAdapter(tripList,getContext(),getActivity());
            recyclerView.setAdapter(adapter);

            //TODO card show in history after program Killed

        }

        }

    }

