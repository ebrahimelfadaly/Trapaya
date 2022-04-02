package com.example.tripplanner.Home.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.tripplanner.Home.Activity.Home_Activity;
import com.example.tripplanner.R;
import com.example.tripplanner.TripData.Final;
import com.example.tripplanner.TripData.Trip;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsFragment extends Fragment {
    private GoogleMap mMap;
    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;
            new getFinishedTrips().execute();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    public void drawTrips(double startPointLatitude, double startPointLongitude, double endPointLatitude, double endPointLongitude, String startPoint, String endPoint) {
        String sPoint = startPointLatitude + "," + startPointLongitude;
        String ePoint = endPointLatitude + "," + endPointLongitude;
        //get first point location
        LatLng startPointName = new LatLng(startPointLatitude, startPointLongitude);
        mMap.addMarker(new MarkerOptions().position(startPointName).title(startPoint));
        //get Second point location
        LatLng endPointName = new LatLng(endPointLatitude, endPointLongitude);
        mMap.addMarker(new MarkerOptions().position(endPointName).title(endPoint));
        //Define list to get all latLng for the route
        List<LatLng> path = new ArrayList();

        //center
        LatLng center = new LatLng(30.0434, 31.2468);

        // Directions API request
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(Final.API_KEY)
                .build();

        DirectionsApiRequest req = DirectionsApi.getDirections(context, sPoint, ePoint);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polyline of each step
            if (res.routes!= null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];
                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> cords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng cord1 : cords1) {
                                                path.add(new LatLng(cord1.lat, cord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> cords = points.decodePath();
                                        for (com.google.maps.model.LatLng cord : cords) {
                                            path.add(new LatLng(cord.lat, cord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }

        //for random color
        Random rnd = new Random();
        int randomColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(randomColor).width(8);
            mMap.addPolyline(opts);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 6));
    }

    class getFinishedTrips extends AsyncTask<Void, Void, List<Trip>> {

        @Override
        protected List<Trip> doInBackground(Void... voids) {
            return Home_Activity.database.tripDAO().selectUpcomingTrip(Home_Activity.fireBaseUserId, Final.FINISHED_TRIP_STATUS);
        }

        @Override
        protected void onPostExecute(List<Trip> trips) {
            super.onPostExecute(trips);
            if (!trips.isEmpty()) {
                for (int i = 0; i < trips.size(); i++) {
                    drawTrips(trips.get(i).getStartPointLatitude(),trips.get(i).getStartPointLongitude(),
                            trips.get(i).getEndPointLatitude(),trips.get(i).getEndPointLongitude(),
                            trips.get(i).getStartPoint(),trips.get(i).getEndPoint());
                }
            }
        }
    }
}