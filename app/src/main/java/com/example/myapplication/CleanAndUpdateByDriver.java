package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.ml.Model;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CleanAndUpdateByDriver extends AppCompatActivity {

    TextView txtlattitude,txtlongitude,txtaddress;
    Button btngetlocation,btnsubmit,btnmap;
    FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Complaint driverComplaint;
    String address,adminStatus,citizenStatus,latitude,longitude,userID,driverID,driverImageFilename;

    //
    Button picture;
    int imageSize = 224;
    int flag =0;
    int b=0;
    File bitimg=null;
    int ppp;

    String fff;
    Uri uuu;
    String f = "False";
    int complaintFlag = 7;

    private String[] PREMISSIONS;
    String sSource="";
    String sDestination="";
    public static final int CAMERA_PERM_CODE = 201;
    public static final int CAMERA_REQUEST_CODE = 202;
    //ImageView selectedImage;
    String currentPhotoPath;
    StorageReference storageReference;
    ProgressDialog progressDialog ;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_and_update_by_driver);

        txtlattitude = (TextView) findViewById(R.id.txt_lattitute);
        txtlongitude = (TextView) findViewById(R.id.txt_longitude);
        txtaddress = (TextView) findViewById(R.id.txt_address);
        btngetlocation = (Button) findViewById(R.id.btn_getlocation);
        btnsubmit = (Button) findViewById(R.id.btn_submit);
        btnmap = (Button) findViewById(R.id.btn_maps);

        btnmap.setEnabled(false);
        //
        PREMISSIONS = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        //progressDialog = new ProgressDialog(getApplicationContext());
        databaseReference = FirebaseDatabase.getInstance().getReference("Images");
        storageReference = FirebaseStorage.getInstance().getReference();

        //
        driverComplaint = getIntent().getParcelableExtra("driverComplaint");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Complaints");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                CleanAndUpdateByDriver.this);

        btngetlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ActivityCompat.checkSelfPermission(CleanAndUpdateByDriver.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(CleanAndUpdateByDriver.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
                    getCurrentLocation();
                }else{
                    ActivityCompat.requestPermissions(CleanAndUpdateByDriver.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION,}
                            ,100);
                }
            }
        });
        btnmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get source and destination
                //check condition
                if(sSource.equals("") && sDestination.equals("")){
                    Toast.makeText(getApplicationContext(), "Location not defined", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Toast.makeText(getApplicationContext(), sDestination, Toast.LENGTH_SHORT).show();
                    openmap(sSource,sDestination);
                }
            }
        });

        if(driverComplaint != null){
            address = driverComplaint.getAddress();
            sDestination=address;
            adminStatus = driverComplaint.getAdminStatus();
            citizenStatus = driverComplaint.getCitizenStatus();
            latitude = driverComplaint.getLattitude();
            longitude = driverComplaint.getLongitude();
            userID = driverComplaint.getUserID();
            driverID = driverComplaint.getDriverID();
            driverImageFilename = driverComplaint.getDriverImageFilename();
        }

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                String driver_lat = txtlattitude.getText().toString();
                String driver_long = txtlongitude.getText().toString();
                String driver_add = txtaddress.getText().toString();

                if(driver_lat.isEmpty() || driver_long.isEmpty() || driver_add.isEmpty()){
                    Toast.makeText(CleanAndUpdateByDriver.this,"Get current location first",Toast.LENGTH_SHORT).show();
                }
                else if(!driver_lat.isEmpty() || !driver_long.isEmpty() || !driver_add.isEmpty()) {

                    Double driver_latitude = Double.parseDouble(driver_lat);
                    Double driver_longitude = Double.parseDouble(driver_long);
                    Double citizen_latitude = Double.parseDouble(latitude);
                    Double citizen_longitude = Double.parseDouble(longitude);
                    Double errorRate = 0.02;


                    if ((driver_latitude + errorRate) > citizen_latitude &&
                            (driver_latitude - errorRate) < citizen_latitude &&
                            (driver_longitude + errorRate) > citizen_longitude &&
                            (driver_longitude - errorRate) < citizen_longitude) {

                        if(!hasPermission(getApplicationContext(),PREMISSIONS)){
                            requestPermissions(PREMISSIONS, CAMERA_PERM_CODE);

                        }else {
                            //askCameraPermissions();
                            dispatchTakePictureIntent();
                        }
                        /*
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                            && (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                        {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
                        } else {
                            dispatchTakePictureIntent();

                        }

                         */

                    } else {
                        Toast.makeText(CleanAndUpdateByDriver.this, "Failed,Please check your location.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void openmap(String sSource, String sDestination) {
        //If the device does not have a map installed, then redirect it to play store
        try{
            //When google map is installed
            //Initilize uri
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + sSource + "/" + sDestination);
            //Initilize intent with action view
            Intent imap = new Intent(Intent.ACTION_VIEW,uri);
            //Set package
            imap.setPackage("com.google.android.apps.maps");
            //Set flag
            imap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //start intent
            startActivity(imap);
        }catch (ActivityNotFoundException e){
            //When google map is not installed
            //Initilize uri
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            //Initilize intent with action view
            Intent imap = new Intent(Intent.ACTION_VIEW,uri);
            //Set package
            imap.setPackage("com.google.android.apps.maps");
            //Set flag
            imap.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //start intent
            startActivity(imap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            /*
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
             */
            int abc=0;
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                abc=1;
            }else{
                abc=0;
                Toast.makeText(getApplicationContext(), "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
            if(grantResults[1]==PackageManager.PERMISSION_GRANTED){
                abc=1;
            }else{
                abc=0;
                Toast.makeText(getApplicationContext(), "Storage Permission is Required", Toast.LENGTH_SHORT).show();
            }
            if(abc==1){
                dispatchTakePictureIntent();
            }

        }
        if (requestCode == 100 && grantResults.length > 0 &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toast.makeText(getApplicationContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }
//
private boolean hasPermission(Context context, String... PREMISSIONS) {
    if(context!=null && PREMISSIONS!=null){
        for(String permission:PREMISSIONS) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
    }
    return true;
}
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.myapplication.android.fileprovider",
                        photoFile);
                bitimg= photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(bitimg));
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                //imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                ppp = classifyImage(image);

                if(ppp==1)
                    {
                        //btnsubmit.setEnabled(true);

                        Toast.makeText(getApplicationContext(), "Garbage free", Toast.LENGTH_SHORT).show();

                        File f = new File(currentPhotoPath);
                        //selectedImage.setImageURI(Uri.fromFile(f));
                        Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(f));

                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri contentUri = Uri.fromFile(f);
                        mediaScanIntent.setData(contentUri);
                        sendBroadcast(mediaScanIntent);
                        uploadImageToFirebase(f.getName(), contentUri);

                        fff=f.getName();
                        uuu=contentUri;
                        //ppp++;


                    }
                else if(ppp==0){
                    Toast.makeText(getApplicationContext(), "Operation is still pending, try again", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e)
            {
                //handle exception
            }

        }
    }

    public int classifyImage(Bitmap image){
        try {

            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for(int i = 0; i < confidences.length; i++){
                if(confidences[i] > maxConfidence){
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"Garbage","Clean"};

            if(classes[maxPos].equals("Garbage"))
            {
                //Toast.makeText(getApplicationContext(), "Operation is still pending, try again", Toast.LENGTH_LONG).show();
                b=0;
            }
            else
            {
                //Toast.makeText(getApplicationContext(), "Garbage has been removed successfully", Toast.LENGTH_LONG).show();
                b=1;
            }

            String s = "";
            for(int i = 0; i < classes.length; i++){
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }
            //confidence.setText(s);


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
        return  b;
    }
    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }
    private void uploadImageToFirebase(String name, Uri contentUri) {
        //Extra
        //progressDialog.setTitle("Uploading image...");
        //progressDialog.show();
        driverImageFilename = name;
        try {
            //progressDialog.setTitle("Image is Uploading...");
            //progressDialog.show();
            Toast.makeText(getApplicationContext(), "Uploading.....", Toast.LENGTH_LONG).show();
            final StorageReference image = storageReference.child("driver/" + name);
            image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                            //progressDialog.dismiss();

                            adminStatus = "Cleaning Done";
                            Map<String, Object> map = new HashMap<>();
                            map.put("address", address);
                            map.put("adminStatus", adminStatus);
                            map.put("citizenStatus", citizenStatus);
                            map.put("driverID", driverID);
                            map.put("lattitude", latitude);
                            map.put("longitude", longitude);
                            map.put("userID", userID);
                            map.put("driverImageFilename",driverImageFilename);

                            databaseReference = FirebaseDatabase.getInstance().getReference("Complaints");
                            databaseReference.child(address).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    //Toast.makeText(CleanAndUpdateByDriver.this, "Successful", Toast.LENGTH_SHORT).show();
                                    //Intent it = new Intent(CleanAndUpdateByDriver.this, DriverHomeActivity.class);
                                    //startActivity(it);
                                    //finish();
                                }
                            });

                            Toast.makeText(CleanAndUpdateByDriver.this, "Successful", Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(CleanAndUpdateByDriver.this, DriverHomeActivity.class);
                            startActivity(it);
                            finish();
                            //Extra
                            //progressDialog.dismiss();

                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //progressDialog.dismiss();
                    Toast.makeText(CleanAndUpdateByDriver.this, "Upload Failed.", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(CleanAndUpdateByDriver.this, DriverHomeActivity.class);
                    startActivity(it);
                    finish();
                     //progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //

    @SuppressLint("MissingPermission")
    private void getCurrentLocation()  {

        btnmap.setEnabled(true);

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
                            Geocoder geocoder = new Geocoder(CleanAndUpdateByDriver.this,
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
                                //
                                double lat1 = location1.getLatitude();
                                double lon1 = location1.getLongitude();
                                String slat1= String.valueOf(lat1);
                                String slon1= String.valueOf(lon1);
                                sSource=slat1+","+slon1;
                                //
                                txtlattitude.setText(String.valueOf(location1.getLatitude()));
                                txtlongitude.setText(String.valueOf(location1.getLongitude()));

                                try {
                                    Geocoder geocoder = new Geocoder(CleanAndUpdateByDriver.this,
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