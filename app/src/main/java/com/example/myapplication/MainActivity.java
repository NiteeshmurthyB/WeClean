package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    EditText txtusername,txtpassword,txtcnfpass,txtmobileno;
    Button btnregister, btnlogin;
    String username,password,cnfpass,mobileno,category="Citizen";

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private  FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtusername = (EditText) findViewById(R.id.txt_emailAddress);
        txtpassword = (EditText) findViewById(R.id.txt_password);
        txtcnfpass = (EditText) findViewById(R.id.txt_cnfpassword);
        txtmobileno = (EditText) findViewById(R.id.editTextPhone);
        btnregister = (Button) findViewById(R.id.btn_signup);
        btnlogin = (Button) findViewById(R.id.btn_login);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Working with Date


        // Button click events
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(it);
            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    username = txtusername.getText().toString().trim();
                    password = txtpassword.getText().toString().trim();
                    cnfpass = txtcnfpass.getText().toString().trim();
                    mobileno = txtmobileno.getText().toString().trim();

                    if(!username.isEmpty() && !password.isEmpty() && password.length()>=6
                            && !mobileno.isEmpty() && password.equals(cnfpass)){

                        mAuth.createUserWithEmailAndPassword(username,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){

                                            User user = new User(
                                                    username,mobileno,category
                                            );

                                            FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful())
                                                    {
                                                        Toast.makeText(MainActivity.this,"User Registered Successfully" , Toast.LENGTH_SHORT).show();
                                                        Intent it = new Intent(MainActivity.this,LoginActivity.class);
                                                        startActivity(it);
                                                    }
                                                    else{
                                                        Toast.makeText(MainActivity.this,"Not able to register",Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if (e instanceof FirebaseAuthInvalidUserException) {
                                    Toast.makeText(MainActivity.this,"Incorrect Email Address",Toast.LENGTH_SHORT).show();
                                    txtusername.setError("Invalid Email");
                                    txtusername.requestFocus();
                                }else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(MainActivity.this,"Incorrect Password",Toast.LENGTH_SHORT).show();
                                    txtpassword.setError("Invalid Password");
                                    txtpassword.requestFocus();
                                }else if (e instanceof FirebaseAuthWeakPasswordException) {
                                    Toast.makeText(MainActivity.this,"Weak Password",Toast.LENGTH_SHORT).show();
                                    txtpassword.setError("Weak password");
                                    txtpassword.requestFocus();
                                    e.getMessage();
                                }else if (e instanceof FirebaseAuthUserCollisionException) {
                                    Toast.makeText(MainActivity.this,"Email address already in use",Toast.LENGTH_SHORT).show();
                                    txtusername.setError("Already in use");
                                    txtusername.requestFocus();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(MainActivity.this,"Incorrect Input",Toast.LENGTH_SHORT).show();
                        String mobile_pattern = "[0-9]{10}";
                        if(password.length()<6){
                            txtpassword.setError("min length 6");
                        }if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
                            txtusername.setError("Enter valid email");
                        }if(!mobileno.matches(mobile_pattern)){
                            txtmobileno.setError("Enter valid mobile number");
                        }if(!cnfpass.equals(password)){
                            txtcnfpass.setError("Passwords are different");
                        }
                    }
                }catch (NullPointerException e){
                    txtusername.setError("One or more Fields are empty");
                }
            }

        });
    }
}