package com.example.gymesticapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gymesticapp.Fragment.DietFragment;
import com.example.gymesticapp.Fragment.HomeFragment;
import com.example.gymesticapp.Fragment.PlanFragment;
import com.example.gymesticapp.Fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottomNavView);
        frameLayout=findViewById(R.id.frameLayout);


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId=item.getItemId();
                if (itemId==R.id.navHome){
                    loadFragment(new HomeFragment(),false);
                } else if (itemId==R.id.navFood) {
                    loadFragment(new DietFragment(),false);
                } else if (itemId==R.id.navPlan) {
                    loadFragment(new PlanFragment(),false);
                }else if (itemId==R.id.navProfile){
                    loadFragment(new ProfileFragment(),false);
                }
                return true;
            }
        });
        loadFragment(new HomeFragment(),true);

    }
    private void loadFragment(Fragment fragment,boolean isAppInitialized){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
        if (isAppInitialized){
            fragmentTransaction.add(R.id.frameLayout,fragment);
        }else{
            fragmentTransaction.replace(R.id.frameLayout,fragment);
        }
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
}