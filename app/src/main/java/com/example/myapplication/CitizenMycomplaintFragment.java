package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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


public class CitizenMycomplaintFragment extends Fragment implements ComplaintCAdapter.ComplaintCClickInterface {

    private RecyclerView complaintRV;
    private TextView txtlatitude,txtlongitude,txtaddress;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private ArrayList<Complaint> complaintArrayList;
    private RelativeLayout bottomSheetRL;
    private ComplaintCAdapter complaintCAdapter;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    String userID = "";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CitizenMycomplaintFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CitizenMycomplaintFragment newInstance(String param1, String param2) {
        CitizenMycomplaintFragment fragment = new CitizenMycomplaintFragment();
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
        View view =  inflater.inflate(R.layout.fragment_citizen_mycomplaint, container, false);
        complaintRV = view.findViewById(R.id.complaint_RV);
        complaintArrayList = new ArrayList<>();
        complaintCAdapter = new ComplaintCAdapter(complaintArrayList,getContext(),this);
        complaintRV.setLayoutManager(new LinearLayoutManager(getContext()));
        complaintRV.setAdapter(complaintCAdapter);
        getCitizenComplaint();

        return view;
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
                complaintCAdapter.notifyDataSetChanged();
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