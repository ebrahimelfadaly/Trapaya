package com.example.tripplanner.Home.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import com.example.tripplanner.databinding.ActivityMainRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class MainRegister extends AppCompatActivity {

 ActivityMainRegisterBinding binding;
 FirebaseAuth firebaseAuth;
 FirebaseUser user;
  DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();

        binding.tvRegisterAlreadyaccount.setOnClickListener(view -> startActivity(new Intent(MainRegister.this, MainLogin.class)));
        binding.btnRegister.setOnClickListener(view -> checkUserDetail());
    }

    private void checkUserDetail() {
        String username = binding.edRegisterUsername.getText().toString();
        String email = binding.edRegisterEmail.getText().toString();
        String password = binding.edRegisterpassword.getText().toString();
        String conPassword = binding.edRegisterConfirmpassword.getText().toString();

        if (username.isEmpty() || username.length() < 7) {
            showError(binding.edRegisterUsername, "your username is not valid");
        } else if (email.isEmpty() || !email.contains("@")) {
            showError(binding.edRegisterEmail, "your Email is not valid");
        } else if (password.isEmpty() || password.length() < 7) {
            showError(binding.edRegisterpassword, "your password is not valid");
        } else if (conPassword.isEmpty() || !conPassword.equals(password)) {
            showError(binding.edRegisterConfirmpassword, "password not match");
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    //onSuccess
                    user = firebaseAuth.getCurrentUser();
                    assert user != null;
                    String userId = user.getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("username", username);
                    hashMap.put("Email", email);
                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(getApplicationContext(), Home_Activity.class));
                            finish();

                        }
                    });

                }




            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();

                }
            });
        }

    }

    private void showError(EditText text, String s) {
        text.setError(s);
        text.requestFocus();
    }

}