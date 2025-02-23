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
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(requireContext(), "Location is required to mark attendance.", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        checkGPS();
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
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                String locationString = "Location: " + location.getLatitude() + ", " + location.getLongitude();
                locationText.setText(locationString);
                Toast.makeText(requireContext(), "Attendance Marked!", Toast.LENGTH_SHORT).show();
            } else {
                requestNewLocation();
            }
        });
    }

    private void requestNewLocation() {
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

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission is required to mark attendance", Toast.LENGTH_LONG).show();
            }
        }
    }
}
