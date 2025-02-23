package com.example.app006.employee;

import android.app.DatePickerDialog;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app006.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ReportsFragment extends Fragment {

    private TextView fromDateText, toDateText;
    private Button fromDateButton, toDateButton, generateReportButton;
    private RecyclerView recyclerView;
    private DatabaseReference attendanceRef;
    private String fromDate = "", toDate = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.employee_reports, container, false);

        fromDateText = view.findViewById(R.id.fromDateText);
        toDateText = view.findViewById(R.id.toDateText);
        fromDateButton = view.findViewById(R.id.fromDateButton);
        toDateButton = view.findViewById(R.id.toDateButton);
        generateReportButton = view.findViewById(R.id.generateReportButton);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        attendanceRef = FirebaseDatabase.getInstance().getReference("AttendanceRecords");

        fromDateButton.setOnClickListener(v -> showDatePicker(true));
        toDateButton.setOnClickListener(v -> showDatePicker(false));
        generateReportButton.setOnClickListener(v -> generatePDFReport());

        return view;
    }

    private void showDatePicker(boolean isFromDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    if (isFromDate) {
                        fromDate = selectedDate;
                        fromDateText.setText("From Date: " + fromDate);
                    } else {
                        toDate = selectedDate;
                        toDateText.setText("To Date: " + toDate);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    private void generatePDFReport() {
        if (fromDate.isEmpty() || toDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select both dates", Toast.LENGTH_SHORT).show();
            return;
        }

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        page.getCanvas().drawText("Attendance Report", 10, 20, null);
        page.getCanvas().drawText("From: " + fromDate, 10, 40, null);
        page.getCanvas().drawText("To: " + toDate, 10, 60, null);

        pdfDocument.finishPage(page);

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "AttendanceReport.pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();
            Toast.makeText(getContext(), "PDF Report Saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
