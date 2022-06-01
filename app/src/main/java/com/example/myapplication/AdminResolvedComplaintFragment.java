package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class AdminResolvedComplaintFragment extends Fragment implements ComplaintAdminAdapter.ComplaintAdminClickInterface {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView admincomplaintRV;
    private TextView txtlatitude,txtlongitude,txtaddress;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ArrayList<Complaint> complaintArrayList;
    private RelativeLayout bottomSheetRL;
    private ComplaintAdminAdapter complaintAdminAdapter;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    public AdminResolvedComplaintFragment() {
        // Required empty public constructor
    }

    public static AdminResolvedComplaintFragment newInstance(String param1, String param2) {
        AdminResolvedComplaintFragment fragment = new AdminResolvedComplaintFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_resolved_complaint, container, false);
        admincomplaintRV =  view.findViewById(R.id.adminComplaint_RV);
        bottomSheetRL = view.findViewById(R.id.idRLbottomSheet);
        complaintArrayList = new ArrayList<>();;
        complaintAdminAdapter = new ComplaintAdminAdapter(complaintArrayList,getContext(),this);
        admincomplaintRV.setLayoutManager(new LinearLayoutManager(getContext()));
        admincomplaintRV.setAdapter(complaintAdminAdapter);
        getAdminComplaint();

        return view;
    }

    private void getAdminComplaint() {
        complaintArrayList.clear();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Complaints");

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                String admin_status = snapshot.child("adminStatus").getValue(String.class);
                if(admin_status.equals("Complaint Resolved")) {
                    complaintArrayList.add(snapshot.getValue(Complaint.class));
                    complaintAdminAdapter.notifyDataSetChanged();
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
                Collections.reverse(complaintArrayList);
                complaintAdminAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                complaintAdminAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                complaintAdminAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                complaintAdminAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onComplaintAdminClick(int position) {

    }

}