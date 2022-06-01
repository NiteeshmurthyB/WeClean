package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;

public class CitizenHomeActivity extends AppCompatActivity {

    Button raiseComplaint,mycomplaints;
    FirebaseAuth mAuth;
    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_home);

        bottomNavigation = findViewById(R.id.bottom_nav);

        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_email_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_lock_24));
//        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_baseline_arrow_back_24));

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {

                Fragment fragment = null;
                switch (item.getId()){
                    case 1:
                        fragment = new CitizenMycomplaintFragment();
                        break;

                    case 2:
                        fragment = new CitizenRaiseComplaintFragment();
                        break;
                }
                loadFragment(fragment);
            }
        });

        bottomNavigation.show(1,true);

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                if (item.getId() == 1){
                    Toast.makeText(CitizenHomeActivity.this,"MyComplaints",Toast.LENGTH_SHORT).show();
                }else if (item.getId() == 2){
                    Toast.makeText(CitizenHomeActivity.this,"RaiseComplaint",Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });

//        raiseComplaint = (Button) findViewById(R.id.btn_raisecomplaint);
//        mycomplaints = (Button) findViewById(R.id.btn_mycomplaints);
        mAuth = FirebaseAuth.getInstance();

//        raiseComplaint.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent it = new Intent(CitizenHomeActivity.this,RaisecomplaintActivity.class);
//                startActivity(it);
//            }
//        });
//
//        mycomplaints.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent it = new Intent(CitizenHomeActivity.this,MyComplaintActivity.class);
//                startActivity(it);
//            }
//        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.id_framelayout,fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.logout:
                Toast.makeText(CitizenHomeActivity.this,"User logged out",Toast.LENGTH_SHORT).show();
                 mAuth.signOut();
                 Intent it = new Intent(CitizenHomeActivity.this,LoginActivity.class);
                 startActivity(it);
                 this.finish();
                 return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}