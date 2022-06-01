package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MyComplaintActivity extends AppCompatActivity implements ComplaintCAdapter.ComplaintCClickInterface{

    private RecyclerView complaintRV;
    private TextView txtlatitude,txtlongitude,txtaddress;
//    private FloatingActionButton btn_getDetails;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ArrayList<Complaint> complaintArrayList;
    private RelativeLayout bottomSheetRL;
    private ComplaintCAdapter complaintCAdapter;

    String userID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_complaint);

        complaintRV = findViewById(R.id.complaint_RV);
        complaintArrayList = new ArrayList<>();
        complaintCAdapter = new ComplaintCAdapter(complaintArrayList,this,this);
        complaintRV.setLayoutManager(new LinearLayoutManager(this));
        complaintRV.setAdapter(complaintCAdapter);
        getCitizenComplaint();

    }

    private  void getCitizenComplaint(){
        complaintArrayList.clear();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Complaints");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                userID = snapshot.child("userID").getValue(String.class);
                String citizen_status = snapshot.child("citizenStatus").getValue(String.class);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                if(uid.equals(userID) && !citizen_status.equals("Resolved")){
                    complaintArrayList.add(snapshot.getValue(Complaint.class));
                    complaintCAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                complaintCAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                complaintCAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                complaintCAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onComplaintCClick(int position) {

    }
}