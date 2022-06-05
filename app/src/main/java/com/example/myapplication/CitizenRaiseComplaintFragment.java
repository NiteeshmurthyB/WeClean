package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.myapplication.ml.Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CitizenRaiseComplaintFragment extends Fragment implements View.OnClickListener {

    TextView txtlattitude,txtlongitude,txtaddress;
    //Button btntakePicture;
    //Button btnsubmit;

    //
    Button picture;
    int imageSize = 224;
    int flag =0;
    int b=0;
    File bitimg=null;
    int ppp;

    private String[] PREMISSIONS;

    String fff;
    Uri uuu;
    String f = "False";
    int complaintFlag = 7;

    public static final int CAMERA_PERM_CODE = 201;
    public static final int CAMERA_REQUEST_CODE = 202;
    //ImageView selectedImage;
    String currentPhotoPath;
    StorageReference storageReference;
    ProgressDialog progressDialog ;

    //

    FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;

    SupportMapFragment supportMapFragment;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CitizenRaiseComplaintFragment() {
        // Required empty public constructor
    }

    public static CitizenRaiseComplaintFragment newInstance(String param1, String param2) {
        CitizenRaiseComplaintFragment fragment = new CitizenRaiseComplaintFragment();
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
        View view = inflater.inflate(R.layout.fragment_citizen_raise_complaint, container, false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_framelayout);


        txtlattitude = (TextView) view.findViewById(R.id.txt_lattitute);
        txtlongitude = (TextView) view.findViewById(R.id.txt_longitude);
        txtaddress = (TextView) view.findViewById(R.id.txt_address);
        //btntakePicture = (Button) view.findViewById(R.id.btn_takePicture);
        //btntakePicture.setOnClickListener(this);
        //btnsubmit = (Button) view.findViewById(R.id.btn_submit);
        //btnsubmit.setOnClickListener(this);
        PREMISSIONS = new String[] {
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
/*
    <com.google.android.material.button.MaterialButton
        android:id="@+id/btn_submit"
        android:layout_width="331dp"
        android:layout_height="60dp"
        android:backgroundTint="#F0D64040"
        android:text="Submit Complaint"
        android:textAllCaps="false"
        android:textColor="@color/white"
        android:textSize="18dp"
        app:cornerRadius="20dp"
        app:icon="@drawable/ic_baseline_send_24"
        app:iconGravity="textEnd"
        app:iconTint="@color/white"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/btn_takePicture"
        app:layout_constraintVertical_bias="0.305" />
 */
        //
        picture = (Button) view.findViewById(R.id.btn_takePicture);
        picture.setOnClickListener(this);
        storageReference = FirebaseStorage.getInstance().getReference();
//Nandu
        //btnsubmit.setEnabled(false);
        progressDialog = new ProgressDialog(getContext());// context name as
        // Nandu
        //
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Complaints");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        if(flag==1) {
            Toast.makeText(getContext(), "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
        }
        if(ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ){
            getCurrentLocation();
        }else{
            ActivityCompat.requestPermissions((Activity) getContext(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,},100);
        }

        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            int abc=0;
            /*
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(getContext(), "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }

             */
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    abc=1;
            }else{
                abc=0;
                Toast.makeText(getContext(), "Camera Permission is Required to Use camera.", Toast.LENGTH_SHORT).show();
            }
            if(grantResults[1]==PackageManager.PERMISSION_GRANTED){
                    abc=1;
            }else{
                abc=0;
                Toast.makeText(getContext(), "Storage Permission is Required", Toast.LENGTH_SHORT).show();
            }
            if(abc==1){
                dispatchTakePictureIntent();
            }
        }
        if (requestCode == 100 && grantResults.length > 0 &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            Toast.makeText(getContext(), "Permission denied123.", Toast.LENGTH_SHORT).show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
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
                Bitmap image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(bitimg));
                int dimension = Math.min(image.getWidth(), image.getHeight());
                image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
                //imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                ppp = classifyImage(image);

                if(ppp==1)
                {
                    //btnsubmit.setEnabled(true);

                    Toast.makeText(getContext(), "Garbage Detected", Toast.LENGTH_SHORT).show();

                    File f = new File(currentPhotoPath);
                    //selectedImage.setImageURI(Uri.fromFile(f));
                    Log.d("tag", "Absolute Url of Image is " + Uri.fromFile(f));

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    getActivity().sendBroadcast(mediaScanIntent);
                    //Toast.makeText(getApplicationContext(), "Image Uploading in progress", Toast.LENGTH_SHORT).show();
                    //nameuri(f.getName(),contentUri);
                    uploadImageToFirebase(f.getName(), contentUri);

                    //fff=f.getName();
                    //uuu=contentUri;
                    //ppp++;


                }
                else if(ppp==0){
                    Toast.makeText(getContext(), "Can't Find garbage, try again", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e)
            {
                //handle exception
            }

        }
    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    private void uploadImageToFirebase(String name, Uri contentUri) {
        //Extra
        progressDialog.setTitle("Uploading Image...");
        progressDialog.show();
        //

        firebaseDatabase.getReference().child("Complaints").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try {
                    if (complaintFlag == 7){

                        // new complaint
                        try {
                            final StorageReference image = storageReference.child("pictures/" + name);
                            image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //Toast.makeText(getContext(), "Image Is Uploaded.", Toast.LENGTH_SHORT).show();
                                    image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d("tag", "onSuccess: Uploaded Image URl is " + uri.toString());
                                            //Toast.makeText(getContext(),"Complaint Recieved",Toast.LENGTH_SHORT).show();
                                            //Extra
                                            progressDialog.dismiss();
                                            //
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Upload Failed.", Toast.LENGTH_SHORT).show();

                                    //Extra
                                    progressDialog.dismiss();
                                }
                            });

                            String lat = txtlattitude.getText().toString();
                            String llong = txtlongitude.getText().toString();
                            String Address = txtaddress.getText().toString();
                            String citizenStatus = "Complaint Registered";
                            String adminStatus = "No Actions Taken";
                            String driverID = "123";
                            String citizenImageFilename = name;
                            String driverImageFilename = "123";
                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            calendar = Calendar.getInstance();
                            dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            String today = dateFormat.format(calendar.getTime());


                            if(!lat.isEmpty() && !llong.isEmpty() && !Address.isEmpty()){
                                Complaint complaint = new Complaint(userID,driverID,adminStatus,citizenStatus,
                                        Address,lat,llong,today,citizenImageFilename,driverImageFilename);

                                databaseReference.child(Address).setValue(complaint);
                            }else{
                                Toast.makeText(getContext(),"Click GetLocation Button",Toast.LENGTH_SHORT).show();
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public int classifyImage(Bitmap image){
        try {

            Model model = Model.newInstance(getContext());

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
                Toast.makeText(getContext(), "Garbage detected, Raise complaint", Toast.LENGTH_LONG).show();
                b=1;
            }
            else
            {
                Toast.makeText(getContext(), "Can't Find garbage, try again", Toast.LENGTH_LONG).show();
                b=0;
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

    //private void askCameraPermissions() {
        /*
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
            }
        }
        */
    /*
        for(String permission:PREMISSIONS) {
            if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, CAMERA_PERM_CODE);
            } else {
                dispatchTakePictureIntent();
            }
        }
    }
    */
    private boolean hasPermission(Context context, String... PREMISSIONS) {
        if(context!=null && PREMISSIONS!=null){
            for(String permission:PREMISSIONS) {
                if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    //



    @SuppressLint("MissingPermission")
    private void getCurrentLocation()  {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(
                Context.LOCATION_SERVICE
        );
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ){

            // Old code
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    try {
                        Location location = task.getResult();
                        if(location.getAccuracy() < 5){

                            try {
                                Geocoder geocoder = new Geocoder(getContext(),
                                        Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(), location.getLongitude(), 1
                                );
                                txtaddress.setText(String.valueOf(addresses.get(0).getAddressLine(0)));
                                txtlattitude.setText(String.valueOf(location.getLatitude()));
                                txtlongitude.setText(String.valueOf(location.getLongitude()));

                                // google map code
                                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(@NonNull GoogleMap googleMap) {
                                        LatLng latLng = new LatLng(location.getLatitude()
                                                ,location.getLongitude());

                                        MarkerOptions markerOptions = new  MarkerOptions().position(latLng)
                                                .title(txtaddress.getText().toString());

                                        googleMap.clear();
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                                        googleMap.addMarker(markerOptions);
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
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
                                        Geocoder geocoder = new Geocoder(getContext(),
                                                Locale.getDefault());
                                        List<Address> addresses = geocoder.getFromLocation(
                                                location1.getLatitude(), location1.getLongitude(), 1
                                        );
                                        txtaddress.setText(String.valueOf(addresses.get(0).getAddressLine(0)));

                                        // google map code
                                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                                LatLng latLng = new LatLng(location.getLatitude()
                                                        ,location.getLongitude());

                                                MarkerOptions markerOptions = new  MarkerOptions().position(latLng)
                                                        .title(txtaddress.getText().toString());

                                                googleMap.clear();
                                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                                                googleMap.addMarker(markerOptions);
                                            }
                                        });
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (NullPointerException e){
                                        e.printStackTrace();
                                    }
                                }
                            };
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest
                                    ,locationCallback, Looper.myLooper());
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });


        }else{
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.equals(picture)){

            firebaseDatabase.getReference().child("Complaints").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            Double compaintLatitude = Double.parseDouble(ds.child("lattitude").getValue(String.class));
                            Double complaintLongitude = Double.parseDouble(ds.child("longitude").getValue(String.class));
                            Double citizenlatitude = Double.parseDouble(txtlattitude.getText().toString());
                            Double citizenlongitude = Double.parseDouble(txtlongitude.getText().toString());
                            Double errorRate = 0.002;

                            if (( compaintLatitude + errorRate) > citizenlatitude &&
                                    (compaintLatitude - errorRate) < citizenlatitude &&
                                    (complaintLongitude + errorRate) > citizenlongitude &&
                                    (complaintLongitude - errorRate) < citizenlongitude){
                                complaintFlag = 1;
                                Toast.makeText(getContext(),"complaint already registered",Toast.LENGTH_SHORT).show();
                                //Extra
                                //progressDialog.dismiss();
                                break;
                            }
                        }

                        if (complaintFlag == 7){
                            // Launch camera if we have permission
                            /*
                            if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                                    && ActivityCompat.checkSelfPermission(getContext(),
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERM_CODE);
                            }
                            else {
                                askCameraPermissions();
                                //dispatchTakePictureIntent();
                            }
                            */
                            if(!hasPermission(getContext(),PREMISSIONS)){
                                requestPermissions(PREMISSIONS, CAMERA_PERM_CODE);

                            }else {
                                //askCameraPermissions();
                                dispatchTakePictureIntent();
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
         //else if (view.equals(btnsubmit)){
            //uploadImageToFirebase(fff, uuu);

            // Repeated Comaplints Code
            /*
            String sss=uuu.toString();
            if(sss.equals("")) {
                Toast.makeText(getContext(), "enu ila" , Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), sss, Toast.LENGTH_SHORT).show();
                //uploadImageToFirebase(fname, uuu);
            }

             */
            //Toast.makeText(getContext(), "Image uploaded", Toast.LENGTH_SHORT).show();





        //}
    }
}