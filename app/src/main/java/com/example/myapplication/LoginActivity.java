package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    private EditText txtusername,txtpassword;
    private Button btnlogin,btnsignUp;
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtusername = (EditText) findViewById(R.id.editTextTextEmailAddress);
        txtpassword = (EditText) findViewById(R.id.editTextTextPassword);
        btnlogin = (Button) findViewById(R.id.btn_login);
        btnsignUp = (Button) findViewById(R.id.btn_signUp);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        // calculate NO of days between 2 dates
//        firebaseDatabase.getReference().child("Complaints").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                try {
//                    for (DataSnapshot ds:snapshot.getChildren()){
//                        String dateofComplaint = ds.child("dateofComplaint").getValue(String.class);
//                        calendar = Calendar.getInstance();
//                        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//                        String today = dateFormat.format(calendar.getTime());
//
//                        Date date1 = dateFormat.parse(dateofComplaint);
//                        Date date2 = dateFormat.parse(today);
//
//                        long diff = (date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000);
//                        String nodays = Long.toString(diff);
//                        Toast.makeText(LoginActivity.this, nodays, Toast.LENGTH_SHORT).show();
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

        btnsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(it);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = txtusername.getText().toString().trim();
                String password = txtpassword.getText().toString().trim();

                if(!username.isEmpty() && !password.isEmpty()){
                    mAuth.signInWithEmailAndPassword(username,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){
                                        String uid = task.getResult().getUser().getUid();

                                        checkUserCategory(uid);

                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(LoginActivity.this,"Incorrect Email Address",Toast.LENGTH_SHORT).show();
                                txtusername.setError("Invalid Email");
                                txtusername.requestFocus();
                            }else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this,"Incorrect Password",Toast.LENGTH_SHORT).show();
                                txtpassword.setError("Invalid Password");
                                txtpassword.requestFocus();
                            }
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this,"Enter valid email and password",Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void checkUserCategory(String uid) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("Users").child(uid).child("category")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String usertype = snapshot.getValue(String.class);
                        if(usertype.equals("Citizen")){
                            Toast.makeText(LoginActivity.this,"Citizen",Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(LoginActivity.this,CitizenHomeActivity.class);
                            startActivity(it);
                            finish();

                        }else if(usertype.equals("Admin")){
                            Toast.makeText(LoginActivity.this,"Admin",Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(LoginActivity.this, AdminHomeActivity.class);
                            it.putExtra("fragmentId",1);
                            startActivity(it);
                            finish();

                        }else if(usertype.equals("Driver")){
                            Intent it = new Intent(LoginActivity.this, DriverHomeActivity.class);
                            startActivity(it);
                            Toast.makeText(LoginActivity.this,"Driver",Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            String uid = user.getUid();
            checkUserCategory(uid);
        }
    }
}