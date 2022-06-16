package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DriverHomeActivity extends AppCompatActivity implements ComplaintDriverAdapter.ComplaintDriverClickInterface {

    private RecyclerView drivercomplaintRV;
    private TextView txtlatitude,txtlongitude,txtaddress;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ArrayList<Complaint> complaintArrayList;
    private RelativeLayout bottomSheetRL;
    private ComplaintDriverAdapter complaintDriverAdapter;
    private int cnt = 0;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);

        drivercomplaintRV = findViewById(R.id.drivercomplaint_RV);
        complaintArrayList = new ArrayList<>();
        bottomSheetRL = findViewById(R.id.idRLbottomSheet_driver);
        complaintDriverAdapter = new ComplaintDriverAdapter(complaintArrayList,this,this);
        drivercomplaintRV.setLayoutManager(new LinearLayoutManager(this));
        drivercomplaintRV.setAdapter(complaintDriverAdapter);
        mAuth = FirebaseAuth.getInstance();
        getDriverComplaint();

    }

    private void getDriverComplaint() {
        complaintArrayList.clear();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Complaints");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String driverID = snapshot.child("driverID").getValue(String.class);
                //adminStatus = snapshot.child("adminStatus").getValue(String.class);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(uid.equals(driverID)){
                    complaintArrayList.add(snapshot.getValue(Complaint.class));
                    cnt = cnt + 1;
                    complaintDriverAdapter.notifyDataSetChanged();
                }

                Collections.sort(complaintArrayList, new Comparator<Complaint>() {
                    @Override
                    public int compare(Complaint c1, Complaint c2) {
                        String c1DateofComplaint = c1.getDateofComplaint();
                        String c2DateofComplaint = c2.getDateofComplaint();
                        calendar = Calendar.getInstance();
                        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date1 = null;
                        Date date2 = null;

                        try {
                            date1 = dateFormat.parse(c1DateofComplaint);
                            date2 = dateFormat.parse(c2DateofComplaint);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return date1.compareTo(date2);
                    }
                });
                complaintDriverAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                complaintDriverAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                complaintDriverAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                complaintDriverAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onComplaintDriverClick(int position) {
        displayBottomSheetDriver(complaintArrayList.get(position));
    }

    private void displayBottomSheetDriver(Complaint complaint){
        final  BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dailog_driver,bottomSheetRL);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);

        TextView btxtaddress = layout.findViewById(R.id.txt_address);
        TextView btxtlatitude = layout.findViewById(R.id.txt_latitute);
        TextView btxtlongitude = layout.findViewById(R.id.txt_longitude);

        btxtaddress.setText(complaint.getAddress());
        btxtlatitude.setText(complaint.getLattitude());
        btxtlongitude.setText(complaint.getLongitude());

        Button btnupdate = layout.findViewById(R.id.btn_update);

        String adminStatus = complaint.getAdminStatus();
        if((adminStatus.equals("Cleaning Done")) || adminStatus.equals("Complaint Resolved")){
            btnupdate.setVisibility(View.INVISIBLE);
        }

        bottomSheetDialog.show();


        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(DriverHomeActivity.this,CleanAndUpdateByDriver.class);
                it.putExtra("driverComplaint",complaint);
                startActivity(it);
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
                Toast.makeText(DriverHomeActivity.this,"User logged out",Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent it = new Intent(DriverHomeActivity.this,LoginActivity.class);
                startActivity(it);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}