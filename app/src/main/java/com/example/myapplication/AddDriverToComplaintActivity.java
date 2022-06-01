package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    private ArrayList<String>  arrayList = new ArrayList<>();
    Complaint complaint;
    String address,adminStatus,citizenStatus,latitude,longitude,userID,driverID;
    Button btnaddDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver_to_complaint);

        spinner_drivername = findViewById(R.id.spinner_drivername);
        getSpinnerItems();

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

                databaseReference = FirebaseDatabase.getInstance().getReference("Complaints");
                databaseReference.child(address).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void getSpinnerItems() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot items: snapshot.getChildren()){
                    String item = items.child("category").getValue(String.class);
                    if(item.equals("Driver")){
                        arrayList.add(items.getKey());
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AddDriverToComplaintActivity.this,
                        R.layout.style_spinner_adddriver,arrayList);
                spinner_drivername.setAdapter(arrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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