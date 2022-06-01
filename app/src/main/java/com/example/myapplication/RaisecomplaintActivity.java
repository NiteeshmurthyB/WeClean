package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RaisecomplaintActivity extends AppCompatActivity {

    TextView txtlattitude,txtlongitude,txtaddress;
    Button btngetlocation,btnsubmit;
    FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raisecomplaint);

        txtlattitude = (TextView) findViewById(R.id.txt_lattitute);
        txtlongitude = (TextView) findViewById(R.id.txt_longitude);
        txtaddress = (TextView) findViewById(R.id.txt_address);
        btngetlocation = (Button) findViewById(R.id.btn_getlocation);
        btnsubmit = (Button) findViewById(R.id.btn_submit);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Complaints");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                RaisecomplaintActivity.this);

        btngetlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(RaisecomplaintActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(RaisecomplaintActivity.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                    getCurrentLocation();
                }else{
                    ActivityCompat.requestPermissions(RaisecomplaintActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,}
                            ,100);
                }
            }
        });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lat = txtlattitude.getText().toString();
                String llong = txtlongitude.getText().toString();
                String Address = txtaddress.getText().toString();
                String citizenStatus = "Complaint Registered";
                String adminStatus = "No Actions Taken";
                String driverID = "123";
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

//                if(!lat.isEmpty() && !llong.isEmpty() && !Address.isEmpty()){
//                    Complaint complaint = new Complaint(userID,driverID,adminStatus,citizenStatus,
//                            Address,lat,llong);
//
//                    databaseReference.child(Address).setValue(complaint);
//                    Toast.makeText(RaisecomplaintActivity.this,"Compliant recieved",Toast.LENGTH_SHORT).show();
//                }else{
//                    Toast.makeText(RaisecomplaintActivity.this,"Click GetLocation Button",Toast.LENGTH_SHORT).show();
//                }


//                databaseReference.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        databaseReference.child(Address).setValue(complaint);
//                        Toast.makeText(RaisecomplaintActivity.this,"Compliant recieved",Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toast.makeText(getApplicationContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation()  {


        LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE
        );
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ){

            // Old code
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                 @Override
                 public void onComplete(@NonNull Task<Location> task) {
                     Location location = task.getResult();
                     if(location.getAccuracy() < 5){

                         try {
                             Geocoder geocoder = new Geocoder(RaisecomplaintActivity.this,
                                     Locale.getDefault());
                             List<Address> addresses = geocoder.getFromLocation(
                                     location.getLatitude(), location.getLongitude(), 1
                             );
                             txtaddress.setText(String.valueOf(addresses.get(0).getAddressLine(0)));
                             txtlattitude.setText(String.valueOf(location.getLatitude()));
                             txtlongitude.setText(String.valueOf(location.getLongitude()));
                         } catch (IOException e) {
                             e.printStackTrace();
                         }


                     }else{
                         LocationRequest locationRequest = new LocationRequest()
                                 .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                 .setInterval(1000)
                                 .setFastestInterval(1000);

                         LocationCallback locationCallback = new LocationCallback() {
                             @Override
                             public void onLocationResult(@NonNull LocationResult locationResult) {
                                 Location location1 = locationResult.getLastLocation();
                                 txtlattitude.setText(String.valueOf(location1.getLatitude()));
                                 txtlongitude.setText(String.valueOf(location1.getLongitude()));

                                 try {
                                     Geocoder geocoder = new Geocoder(RaisecomplaintActivity.this,
                                             Locale.getDefault());
                                     List<Address> addresses = geocoder.getFromLocation(
                                             location1.getLatitude(), location1.getLongitude(), 1
                                     );
                                     txtaddress.setText(String.valueOf(addresses.get(0).getAddressLine(0)));
                                 } catch (IOException e) {
                                     e.printStackTrace();
                                 }
                             }
                         };
                         fusedLocationProviderClient.requestLocationUpdates(locationRequest
                         ,locationCallback, Looper.myLooper());
                     }
                 }
             });


        }else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }


}