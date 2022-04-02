package com.example.tripplanner.Home.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.tripplanner.R;
import com.example.tripplanner.TripData.Final;
import com.example.tripplanner.TripData.Trip;
import com.example.tripplanner.TripData.TripDatabase;
import com.example.tripplanner.databinding.ActivityMainLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainLogin extends AppCompatActivity {
    private static final int RC_SIGN_IN =123 ;
    ActivityMainLoginBinding binding;
    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    AlertDialog.Builder resetalert;
    List<Trip> tripsl;
    private FirebaseUser user;
    LayoutInflater inflater;
    private TripDatabase database;
    private DatabaseReference databaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();
        database = Room.databaseBuilder(this, TripDatabase.class, "tripDB").build();
        databaseRef = FirebaseDatabase.getInstance().getReference();
        resetalert= new AlertDialog.Builder(MainLogin.this);
        inflater=this.getLayoutInflater();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.tvLoginSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainLogin.this, MainRegister.class));
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkcrededentails();
            }
        });



        binding.tvLoginForgitpasss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Rest password?");
                passwordResetDialog.setMessage("Enter your mail to receive reset link");
                passwordResetDialog.setView(resetMail);
                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //resetemail
                        String mail = resetMail.getText().toString();
                        auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Check your Email", Toast.LENGTH_LONG).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Erorr ! resend email agin" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.create().show();

            }
        });
        binding.btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void checkcrededentails() {

        String email = binding.edLoginEmail.getText().toString();
        String password = binding.edLoginPassword.getText().toString();

        if (email.isEmpty() || !email.contains("@")) {
            showErorr(binding.edLoginEmail, "your Email is not valid");
        } else if (password.isEmpty() || password.length() < 7) {
            showErorr(binding.edLoginPassword, "your password is not valid");
        } else {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    readOnFireBase();
                    new check().execute();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account=task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch (ApiException e)
            {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();

            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(getApplicationContext(),"signInWithCredential:success",Toast.LENGTH_LONG).show();

                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();

                            updateUI(null);
                        }
                    }
                });
    }



    private void showErorr(EditText text, String s) {
        text.setError(s);
        text.requestFocus();
    }
    private void updateUI(FirebaseUser account)
    {
        startActivity(new Intent(getApplicationContext(), Home_Activity.class));
        finish();

    }
    private void readOnFireBase() {

        tripsl=new ArrayList<>();
        databaseRef.child("TripReminder").child("userID").child(FirebaseAuth.getInstance().getUid()).child("trips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.i("TAG", "onDataChange: ");
                    //     Trip[] tripList = new Trip[(int) dataSnapshot.getChildrenCount()];
                    int i = 0;
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        Trip trip = postSnapshot.getValue(Trip.class);
                        //           tripList[i] = trip;
                        tripsl.add(trip);
                        i++;
                    }
                    //         Log.i(TAG, "onDataChange: " + tripList.length);
                }
                insertTripsINRoom(tripsl);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });
    }

    public void insertTripsINRoom(List<Trip> trips) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("TAG", "insertTripsINRoom: " + trips.size());
                for (int i = 0; i < trips.size(); i++) {
                    if(Calendar.getInstance().getTimeInMillis() > trips.get(i).getCalendar() && trips.get(i).getTripStatus().equals(Final.UPCOMING_TRIP_STATUS)){
                        trips.get(i).setTripStatus(Final.MISSED_TRIP_STATUS);
                    }
                    Trip trip = new Trip(trips.get(i).getUserID(), trips.get(i).getTripName(), trips.get(i).getStartPoint(),
                            trips.get(i).getStartPointLatitude(),trips.get(i).getStartPointLongitude(), trips.get(i).getEndPoint(),
                            trips.get(i).getEndPointLatitude(), trips.get(i).getEndPointLongitude(),trips.get(i).getDate(), trips.get(i).getTime(),
                            trips.get(i).getTripStatus(),trips.get(i).getCalendar(),trips.get(i).getNotes());


                   database.tripDAO().insert(trip);
                    if(trips.get(i).getTripStatus().equals(Final.UPCOMING_TRIP_STATUS)){
                        //initAlarm(trips.get(i));
                    }
                    Log.i("TAG", "insertTripsINRoom: " + trip.getTripName());
                }
            }
        }).start();
    }
    private class check extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            database.tripDAO().clear();
            //    readOnFireBase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startActivity(new Intent(MainLogin.this, Home_Activity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), Home_Activity.class));
            finish();
        }
    }
}