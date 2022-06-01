package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ComplaintDriverAdapter extends RecyclerView.Adapter<ComplaintDriverAdapter.ViewHolder> {
    private ArrayList<Complaint> complaintArrayList;
    private Context context;
    int lastpos = -1;
    private ComplaintDriverClickInterface complaintDriverClickInterface;

    public ComplaintDriverAdapter(ArrayList<Complaint> complaintArrayList, Context context, ComplaintDriverClickInterface complaintDriverClickInterface) {
        this.complaintArrayList = complaintArrayList;
        this.context = context;
        this.complaintDriverClickInterface = complaintDriverClickInterface;
    }

    @NonNull
    @Override
    public ComplaintDriverAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_driver_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintDriverAdapter.ViewHolder holder, int position) {
        Complaint complaint = complaintArrayList.get(holder.getAdapterPosition());
        holder.txtaddress.setText("Address: "+complaint.getAddress());
        holder.txtlatitide.setText("Latitude "+complaint.getLattitude());
        holder.txtlongitude.setText("Longitude "+ complaint.getLongitude());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complaintDriverClickInterface.onComplaintDriverClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return complaintArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtaddress,txtlatitide,txtlongitude;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtaddress = itemView.findViewById(R.id.txt_complaintaddress);
            txtlatitide = itemView.findViewById(R.id.txt_latitute);
            txtlongitude = itemView.findViewById(R.id.txt_longitude);

        }
    }

    public interface ComplaintDriverClickInterface{
        void onComplaintDriverClick(int position);
    }
}
