package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    MeowBottomNavigation bottomNavigation;
    int fragmentID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        bottomNavigation = findViewById(R.id.bottom_nav);

        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_baseline_email_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_baseline_lock_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_baseline_email_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(4,R.drawable.ic_baseline_lock_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(5,R.drawable.ic_baseline_email_24));

        bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {

                Fragment fragment = null;
                switch (item.getId()){
                    case 1:
                        fragment = new AdminPendingComplaintFragment();
                        break;

                    case 2:
                        fragment = new AddDriverToComplaintFragment();
                        break;

                    case 3:
                        fragment = new AdminWorkInProgressFragment();
                        break;

                    case 4:
                        fragment = new AdminWorkCompletedFragment();
                        break;

                    case 5:
                        fragment = new AdminResolvedComplaintFragment();
                        break;
                }
                loadFragment(fragment);
            }
        });

        fragmentID = getIntent().getExtras().getInt("fragmentId");
        bottomNavigation.show(fragmentID,true);

        bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                if (item.getId() == 1){
                    Toast.makeText(AdminHomeActivity.this,"Pending Complaints",Toast.LENGTH_SHORT).show();
                }else if (item.getId() == 2){
                    Toast.makeText(AdminHomeActivity.this,"Add Driver",Toast.LENGTH_SHORT).show();
                }else if (item.getId() == 3){
                    Toast.makeText(AdminHomeActivity.this,"Work In Progress",Toast.LENGTH_SHORT).show();
                }else if (item.getId() == 4){
                    Toast.makeText(AdminHomeActivity.this,"Work Completed",Toast.LENGTH_SHORT).show();
                }else if (item.getId() == 5){
                    Toast.makeText(AdminHomeActivity.this,"Resolved Complaints",Toast.LENGTH_SHORT).show();
                }
            }
        });

        bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {

            }
        });
        mAuth = FirebaseAuth.getInstance();
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.id_framelayout,fragment)
                .commit();
    }

    // Code for Logout
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
                Toast.makeText(AdminHomeActivity.this,"User logged out",Toast.LENGTH_SHORT).show();
                mAuth.signOut();
                Intent it = new Intent(AdminHomeActivity.this,LoginActivity.class);
                startActivity(it);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}