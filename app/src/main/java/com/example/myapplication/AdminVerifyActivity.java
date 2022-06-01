package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminVerifyActivity extends AppCompatActivity {

    private TextView txtaddress,txtcitizenlatitude,txtcitizenlongitude,txtdriverlatitude,txtdriverlongitude;
    private Button btnaccept,btnreject;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Complaint complaint;
    String address,adminStatus,citizenStatus,latitude,longitude,userID,driverID,citizenImageFilename,driverImageFilename;
    int fragmentID = 1;

    ImageView imageView;
    ImageView imageView1;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_verify);

        imageView=findViewById(R.id.imageView);
        imageView1=findViewById(R.id.imageView1);

        txtaddress = (TextView) findViewById(R.id.txt_address);
        txtcitizenlatitude = (TextView) findViewById(R.id.txt_citizenlatitude);
        txtcitizenlongitude = (TextView) findViewById(R.id.txt_citizenlongitude);
        btnaccept = (Button) findViewById(R.id.btn_accept);
        btnreject = (Button) findViewById(R.id.btn_reject);

        complaint = getIntent().getParcelableExtra("complaints");
//        txtdriverlatitude.setText(getIntent().getParcelableExtra("driverlatitude"));
//        txtdriverlongitude.setText(getIntent().getParcelableExtra("driverlongitude"));

        if(complaint != null){
            txtaddress.setText(complaint.getAddress());
            txtcitizenlatitude.setText(complaint.getLattitude());
            txtcitizenlongitude.setText(complaint.getLongitude());

            address = complaint.getAddress();
            adminStatus = complaint.getAdminStatus();
            citizenStatus = complaint.getCitizenStatus();
            latitude = complaint.getLattitude();
            longitude = complaint.getLongitude();
            userID = complaint.getUserID();
            driverID = complaint.getDriverID();

            citizenImageFilename = complaint.getCitizenImageFilename();
            driverImageFilename = complaint.getDriverImageFilename();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("pictures/"+citizenImageFilename);
            StorageReference storageReference1 = FirebaseStorage.getInstance().getReference("driver/"+driverImageFilename);
            progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Loading...");
            progressDialog.show();

            try {
                final File localFile = File.createTempFile("img1","jpg");
                storageReference.getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bitmap= BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                ((ImageView)findViewById(R.id.imageView)).setImageBitmap(bitmap);
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "illa", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!driverImageFilename.equals("123")){
                try {
                    final File localFile1 = File.createTempFile("img1","jpg");
                    storageReference1.getFile(localFile1)
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap bitmap= BitmapFactory.decodeFile(localFile1.getAbsolutePath());
                                    ((ImageView)findViewById(R.id.imageView1)).setImageBitmap(bitmap);
                                    progressDialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Complaints");

        btnaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adminStatus.equals("No Actions Taken")){
                    adminStatus = "Complaint Verified";
                    citizenStatus = "Complaint Verified";
                    fragmentID = 1;
                }else{
                    adminStatus = "Complaint Resolved";
                    citizenStatus = "Complaint Resolved";
                    fragmentID = 5;
                }
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
                        Toast.makeText(AdminVerifyActivity.this,adminStatus, Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(AdminVerifyActivity.this, AdminHomeActivity.class);
                        it.putExtra("fragmentId",fragmentID);
                        startActivity(it);
                        finish();
                    }
                });

//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        databaseReference.updateChildren(map);
//                        Toast.makeText(AdminVerifyActivity.this,"Complain has been Resolved", Toast.LENGTH_SHORT).show();
//                        Intent it = new Intent(AdminVerifyActivity.this,AdminHomeActivity.class);
//                        startActivity(it);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
            }
        });


        btnreject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (adminStatus.equals("No Actions Taken")){
                    databaseReference.child(address).removeValue();
                    Toast.makeText(AdminVerifyActivity.this,"Complaint Rejected",Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(AdminVerifyActivity.this, AdminHomeActivity.class);
                    it.putExtra("fragmentId",1);
                    startActivity(it);
                    finish();
                }else{
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
                    map.put("driverImageFilename","123");

                    databaseReference = FirebaseDatabase.getInstance().getReference("Complaints");
                    databaseReference.child(address).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AdminVerifyActivity.this,"Rejected", Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(AdminVerifyActivity.this, AdminHomeActivity.class);
                            it.putExtra("fragmentId",3);
                            startActivity(it);
                            finish();
                        }
                    });
                }

//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        databaseReference.updateChildren(map);
//                        Toast.makeText(AdminVerifyActivity.this,"Rejected", Toast.LENGTH_SHORT).show();
//                        Intent it = new Intent(AdminVerifyActivity.this,AdminHomeActivity.class);
//                        startActivity(it);
//                        finish();
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
}