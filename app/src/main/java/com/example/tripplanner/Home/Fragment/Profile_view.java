package com.example.tripplanner.Home.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.tripplanner.Home.Activity.Home_Activity;
import com.example.tripplanner.Home.Activity.MainLogin;
import com.example.tripplanner.R;
import com.example.tripplanner.TripData.Trip;
import com.example.tripplanner.TripData.TripDatabase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Profile_view extends Fragment {
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    String userId;
    List<Trip>tripList;
    TextView tv_username,tv_email;
    Button btnLogout,btnSync;
    ImageView imgProfile;
    boolean isSuccess =false;
    boolean isSync =true;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        user= firebaseAuth.getCurrentUser();
        assert user != null;
        userId=user.getUid();
        

        tripList=new ArrayList<>();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_profile,container,false);
        firebaseAuth=FirebaseAuth.getInstance();
        tv_username=view.findViewById(R.id.tvUsernameprofile);
        tv_email=view.findViewById(R.id.tvEmailprofile);
        btnSync=view.findViewById(R.id.btnSync);
        imgProfile =view.findViewById(R.id.imageprofile);
        btnLogout =view.findViewById(R.id.buttonOut);
        btnSync=view.findViewById(R.id.btnSync);
        Userprofile(tv_username,tv_email);

        imgProfile.setImageURI(user.getPhotoUrl());

        btnLogout.setOnClickListener(v -> {
            isSync =true;
            new readFromDatabase().execute();
            writeOnFireBase(tripList);
            firebaseAuth.signOut();
               startActivity(new Intent(getContext(),MainLogin.class));
                requireActivity().finish();
      });

        btnSync.setOnClickListener(v -> {
            isSync =true;
            new readFromDatabase().execute();
            writeOnFireBase(tripList);
        });

        return view;
    }
    public void Userprofile(TextView tv_username, TextView tv_email){

        databaseReference= FirebaseDatabase.getInstance().getReference("users").child(userId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    HashMap<String,String> hashMap=(HashMap<String, String>)snapshot.getValue();
                    String userName=hashMap.get("username");
                    String Email=hashMap.get("Email");
                    tv_username.setText(userName);
                    tv_email.setText(user.getEmail());

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void writeOnFireBase(List<Trip>trips){
        if(isNetworking(getContext())) {
            Trip trip;
            databaseReference.child("TripReminder").child("userID").child(userId).child("trips").removeValue();

            for (int i = 0; i < trips.size(); i++) {
                trip = new Trip(trips.get(i).getUserID(),trips.get(i).getTripName(),trips.get(i).getStartPoint(),
                       trips.get(i).getStartPointLatitude(),trips.get(i).getStartPointLongitude(),trips.get(i).getEndPoint(),
                       trips.get(i).getEndPointLatitude(),trips.get(i).getEndPointLongitude(),trips.get(i).getDate(),trips.get(i).getTime(),
                        trips.get(i).getTripStatus(),trips.get(i).getCalendar(),trips.get(i).getNotes());



                Log.i("firebase", "writeOnFireBase: " + trip.getTripName() + trip.getId() + trip.getStartPoint()+trip.getNotes());
                databaseReference.child("TripReminder").child("userID").child(userId).child("trips").push().setValue(trip).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(isSync){
                                    isSuccess =true;
                                }
                            }
                        });
                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(!isSync){
                                    isSuccess =false;
                                }
                            }
                        });
                    }
                });
            }
            if(isSuccess){
                Toast.makeText(getContext(), "Synchronization is completed successfully", Toast.LENGTH_SHORT).show();
                isSync =false;
            }
        }
        else{
            Toast.makeText(getContext(), "No Internet ", Toast.LENGTH_SHORT).show();
        }
        Log.i("firebase", "writeOnFireBase: ");
    }


  /*  @Override
    public void onResume() {
        super.onResume();
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getContext());
        tv_username.setText(account.getDisplayName());
        tv_email.setText(account.getEmail());
        Glide.with(getContext()).load(account.getPhotoUrl()).into(imgProfile);
    }*/

     public static boolean isNetworking(Context context)
     {
         try {
             ConnectivityManager manager = (ConnectivityManager) context
                     .getSystemService(Context.CONNECTIVITY_SERVICE);
             NetworkInfo networkInfo = manager.getActiveNetworkInfo();
             if (networkInfo != null && networkInfo.isConnected()) {
                 return true;
             }

         }catch (Exception e)
         {
             e.getMessage();
         }
         return false;
     }
    private class readFromDatabase extends AsyncTask<Void, Void, List<Trip>> {
        @Override
        protected List<Trip> doInBackground(Void... voids) {
            return  Home_Activity.database.tripDAO().selectAll(userId);
        }
        @Override
        protected void onPostExecute(List<Trip> trips) {
            super.onPostExecute(tripList);
            Log.i("TAG", "onPostExecute: "+tripList);
            tripList=trips;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isSync =true;
        new readFromDatabase().execute();
        writeOnFireBase(tripList);
    }
}
