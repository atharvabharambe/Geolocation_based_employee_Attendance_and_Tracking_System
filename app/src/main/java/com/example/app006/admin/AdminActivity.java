package com.example.app006.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.app006.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Debugging log to check if AdminActivity starts
        Log.d("AdminActivity", "AdminActivity started");

        // Fix: Set correct welcome message
        TextView welcomeText = findViewById(R.id.welcome_text);
        if (welcomeText != null) {
            welcomeText.setText("Welcome, Admin");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // Set default fragment when activity starts
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_fragment_container, new EmployeeLookupFragment()).commit();
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    if (item.getItemId() == R.id.nav_employee_lookup) {
                        selectedFragment = new EmployeeLookupFragment();
                    } else if (item.getItemId() == R.id.nav_reporting) {
                        selectedFragment = new ReportingFragment();
                    } else if (item.getItemId() == R.id.nav_attendance) {
                        selectedFragment = new AttendanceFragment();
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.admin_fragment_container, selectedFragment).commit();
                    }
                    return true;
                }
            };
}
