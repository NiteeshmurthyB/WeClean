package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddDriverToComplaintActivity extends AppCompatActivity{

    FirebaseAuth mAuth;
    private Spinner spinner_drivername;
    private DatabaseReference databaseReference;
    private ArrayList<String> driverIDArrayList = new ArrayList<>();
    private ArrayList<String> workingDriverIDArrayList = new ArrayList<>();
    Complaint complaint;
    String address,adminStatus,citizenStatus,latitude,longitude,userID,driverID;
    Button btnaddDriver;
    private TextView txtdrivername,txtmobilenumber,txtactivecomplaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver_to_complaint);

        txtdrivername = (TextView) findViewById(R.id.txt_drivername);
        txtmobilenumber = (TextView) findViewById(R.id.txt_mobilenumber);
        txtactivecomplaint = (TextView) findViewById(R.id.txt_activeComplaint);
        mAuth = FirebaseAuth.getInstance();

        spinner_drivername = findViewById(R.id.spinner_drivername);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getSpinnerItems();

        spinner_drivername.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinner_driverID = spinner_drivername.getSelectedItem().toString();
                getNumberofActiveComplaints(spinner_driverID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        complaint = getIntent().getParcelableExtra("complaints");

        if(complaint != null){
            address = complaint.getAddress();
            adminStatus = complaint.getAdminStatus();
            citizenStatus = complaint.getCitizenStatus();
            latitude = complaint.getLattitude();
            longitude = complaint.getLongitude();
            userID = complaint.getUserID();
            driverID = complaint.getDriverID();
        }

        btnaddDriver = (Button) findViewById(R.id.btn_addDriver);
        btnaddDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driverID =  spinner_drivername.getSelectedItem().toString();
                adminStatus = "Driver Added";
                citizenStatus = "Processing";

                Map<String,Object> map = new HashMap<>();
                map.put("address",address);
                map.put("adminStatus",adminStatus);
                map.put("citizenStatus",citizenStatus);
                map.put("driverID",driverID);
                map.put("lattitude",latitude);
                map.put("longitude",longitude);
                map.put("userID",userID);

                String complaintKey = "";
                complaintKey = address + ' ' +latitude.replace('.',',') + ' ' + longitude.replace('.',',');
                databaseReference = FirebaseDatabase.getInstance().getReference("Complaints");
                databaseReference.child(complaintKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AddDriverToComplaintActivity.this,"Driver has been added successfully",Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(AddDriverToComplaintActivity.this, AdminHomeActivity.class);
                        it.putExtra("fragmentId",2);
                        startActivity(it);
                        finish();
                    }
                });


//                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        databaseReference.updateChildren(map);
//                        Toast.makeText(AddDriverToComplaintActivity.this,"Driver has been added successfully",Toast.LENGTH_SHORT).show();
//                        Intent it = new Intent(AddDriverToComplaintActivity.this,AdminHomeActivity.class);
//                        startActivity(it);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });

//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        databaseReference.updateChildren(map);
//                        Toast.makeText(AddDriverToComplaintActivity.this,"Driver has been added successfully",Toast.LENGTH_SHORT).show();
//                        Intent it = new Intent(AddDriverToComplaintActivity.this,AdminHomeActivity.class);
//                        startActivity(it);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
            }
        });

    }

    private void getNumberofActiveComplaints(String spinner_driverID) {

        databaseReference.child("Complaints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberofActiveComplaints = 0;
                for (DataSnapshot complaints:snapshot.getChildren()){
                    String complaintAdminStatus = complaints.child("adminStatus").getValue(String.class);
                    if(complaintAdminStatus.equals("Driver Added") || complaintAdminStatus.equals("Cleaning Done")){
                        String complaintDriverID = complaints.child("driverID").getValue(String.class);
                        if (complaintDriverID.equals(spinner_driverID)){
                            numberofActiveComplaints += 1;
                        }

                    }
                }
                txtactivecomplaint.setText("Active Complaints : " + numberofActiveComplaints);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot users: snapshot.getChildren()){
                    String userID = users.getKey();
                    if (userID.equals(spinner_driverID)){
                        txtdrivername.setText("Driver Name : " + users.child("username").getValue(String.class));
                        txtmobilenumber.setText("Mobile Number : " + users.child("mobile").getValue(String.class));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSpinnerItems() {
        getAllWorkingDriverId();
        getAllDriverId();
    }

    private void getAllDriverId() {
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                driverIDArrayList.clear();
                for (DataSnapshot items: snapshot.getChildren()){
                    String item = items.child("category").getValue(String.class);
                    if(item.equals("Driver")){
                        driverIDArrayList.add(items.getKey());
                    }
                }
//                removeWorkingDriverId(driverIDArrayList,workingDriverIDArrayList);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddDriverToComplaintActivity.this,
                        R.layout.style_spinner_adddriver, driverIDArrayList);
                spinner_drivername.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllWorkingDriverId() {
        databaseReference.child("Complaints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workingDriverIDArrayList.clear();
                for (DataSnapshot complaints:snapshot.getChildren()){
                    String complaintAdminStatus = complaints.child("adminStatus").getValue(String.class);
                    if(complaintAdminStatus.equals("Driver Added") || complaintAdminStatus.equals("Cleaning Done")){
                        String complaintDriverID = complaints.child("driverID").getValue(String.class);
                        workingDriverIDArrayList.add(complaintDriverID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void removeWorkingDriverId(ArrayList<String> driverIDArrayList, ArrayList<String> workingDriverIDArrayList) {
        for (String driverId:workingDriverIDArrayList){
            driverIDArrayList.remove(driverId);
        }
    }

    // Code for Logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.logout:
                Toast.makeText(AddDriverToComplaintActivity.this,"User logged out",Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent it = new Intent(AddDriverToComplaintActivity.this,LoginActivity.class);
                startActivity(it);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}