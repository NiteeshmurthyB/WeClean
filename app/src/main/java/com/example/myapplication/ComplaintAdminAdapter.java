package com.example.myapplication;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ComplaintAdminAdapter extends RecyclerView.Adapter<ComplaintAdminAdapter.ViewHolder> {
    private ArrayList<Complaint> complaintArrayList;
    private Context context;
    int lastpos = -1;
    private ComplaintAdminClickInterface complaintAdminClickInterface;

    public ComplaintAdminAdapter(ArrayList<Complaint> complaintArrayList, Context context, ComplaintAdminClickInterface complaintAdminClickInterface) {
        this.complaintArrayList = complaintArrayList;
        this.context = context;
        this.complaintAdminClickInterface = complaintAdminClickInterface;
    }

    @NonNull
    @Override
    public ComplaintAdminAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.complaint_admin_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintAdminAdapter.ViewHolder holder, int position) {
        Complaint complaint = complaintArrayList.get(holder.getAdapterPosition());
        holder.txtaddress.setText("Address: " + complaint.getAddress());
        holder.txtlatitude.setText("Latitude " + complaint.getLattitude());
        holder.txtlongitude.setText("Longitude " + complaint.getLongitude());
        holder.txtadminStatus.setText("Status " + complaint.getAdminStatus());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                complaintAdminClickInterface.onComplaintAdminClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return complaintArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtaddress,txtlatitude,txtlongitude,txtadminStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtaddress = itemView.findViewById(R.id.txt_complaintaddress);
            txtlatitude = itemView.findViewById(R.id.txt_latitute);
            txtlongitude = itemView.findViewById(R.id.txt_longitude);
            txtadminStatus = itemView.findViewById(R.id.txt_adminStatus);
        }
    }

    public interface ComplaintAdminClickInterface {
        void onComplaintAdminClick(int position);
    }
}
