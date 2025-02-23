package com.example.app006.employee;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.app006.R;
import com.google.android.gms.location.*;

public class AttendanceFragment extends Fragment {

    private TextView locationText;
    private Button markAttendanceButton;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_attendance, container, false);

        locationText = view.findViewById(R.id.location_text);
        markAttendanceButton = view.findViewById(R.id.mark_attendance_button);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        markAttendanceButton.setOnClickListener(v -> getLocation());

        return view;
    }

    private void getLocation() {
        if (checkPermissions()) {
            checkGPS();
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void checkGPS() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new AlertDialog.Builder(requireContext())
                    .setMessage("GPS is required for attendance. Enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) ->
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("No", (dialog, which) -> dialog.cancel())
                    .show();
        } else {
            fetchLocation();
        }
    }

    private void fetchLocation() {
        if (!checkPermissions()) {
            Toast.makeText(requireContext(), "Location permission is required.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                if (location != null) {
                    String locationString = "Location: " + location.getLatitude() + ", " + location.getLongitude();
                    locationText.setText(locationString);
                    Toast.makeText(requireContext(), "Attendance Marked!", Toast.LENGTH_SHORT).show();

                    // Open the MapFragment with coordinates
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, MapFragment.newInstance(location.getLatitude(), location.getLongitude()))
                            .addToBackStack(null)
                            .commit();
                } else {
                    requestNewLocation();
                }
            });

        } catch (SecurityException e) {
            Toast.makeText(requireContext(), "Permission denied: Unable to fetch location.", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestNewLocation() {
        if (!checkPermissions()) return;

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(1000)
                .build();

        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    String locationString = "New Location: " + location.getLatitude() + ", " + location.getLongitude();
                    locationText.setText(locationString);
                    Toast.makeText(requireContext(), "Attendance Marked!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            }
        } catch (SecurityException e) {
            Toast.makeText(requireContext(), "Permission denied: Unable to request location updates.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission is required to mark attendance", Toast.LENGTH_LONG).show();
            }
        }
    }
}
