package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ComplaintCAdapter extends RecyclerView.Adapter<ComplaintCAdapter.ViewHolder> {
    private ArrayList<Complaint> complaintArrayList;
    private Context context;
    int lastpos = -1;
    private ComplaintCClickInterface complaintCClickInterface;

    public ComplaintCAdapter(ArrayList<Complaint> complaintArrayList, Context context, ComplaintCClickInterface complaintCClickInterface) {
        this.complaintArrayList = complaintArrayList;
        this.context = context;
        this.complaintCClickInterface = complaintCClickInterface;
    }

    @NonNull
    @Override
    public ComplaintCAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_citizen_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintCAdapter.ViewHolder holder, int position) {
        Complaint complaint = complaintArrayList.get(holder.getAdapterPosition());
        holder.txtaddress.setText("Address: "+complaint.getAddress());
        holder.txtlatitide.setText("Latitude "+complaint.getLattitude());
        holder.txtlongitude.setText("Longitude "+ complaint.getLongitude());
        holder.txtcitizenStatus.setText("Status "+complaint.getCitizenStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complaintCClickInterface.onComplaintCClick(holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return complaintArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtaddress,txtlatitide,txtlongitude,txtcitizenStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtaddress = itemView.findViewById(R.id.txt_complaintaddress);
            txtlatitide = itemView.findViewById(R.id.txt_latitute);
            txtlongitude = itemView.findViewById(R.id.txt_longitude);
            txtcitizenStatus = itemView.findViewById(R.id.txt_citizenStatus);
        }
    }

    public interface ComplaintCClickInterface{
        void onComplaintCClick(int position);
    }
}
