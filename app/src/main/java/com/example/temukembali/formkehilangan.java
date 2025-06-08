package com.example.temukembali;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.BitmapCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class formkehilangan extends AppCompatActivity {

    private EditText editNamaBarang, editDeskripsi, editLokasi, editTanggal, editKontak;
    private Spinner spinnerStatus;
    //DatabaseReference sebagai referensi data dari firebase
    private DatabaseReference databaseKehilangan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formkehilangan);

        //inisialisasi komponen
        editNamaBarang = findViewById(R.id.editnamaBarangHilang);
        editDeskripsi = findViewById(R.id.editDeskripsiHilang);
        editLokasi = findViewById(R.id.editLokasiHilang);
        editTanggal = findViewById(R.id.editTanggalHilang);
        editKontak = findViewById(R.id.editKontakHilang);
        spinnerStatus = findViewById(R.id.statusHilang);
        databaseKehilangan = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("kehilangan");

        // Buat DatePicker muncul saat diklik
        editTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void addKehilangan(View view) {
        //ambil data dari EditText dan Spinner
        String namaBarang = editNamaBarang.getText().toString();
        String deskripsi = editDeskripsi.getText().toString();
        String lokasi = editLokasi.getText().toString();
        String tanggal = editTanggal.getText().toString();
        String kontak = editKontak.getText().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        // Validasi isian
        if (!TextUtils.isEmpty(namaBarang) && !TextUtils.isEmpty(deskripsi)  && !TextUtils.isEmpty(lokasi) && !TextUtils.isEmpty(tanggal) && !TextUtils.isEmpty(kontak) && !TextUtils.isEmpty(status)) {
            String id = databaseKehilangan.push().getKey();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                BarangHilang barangHilang = new BarangHilang(id, namaBarang, deskripsi, lokasi, tanggal, kontak, status, uid);
                databaseKehilangan.child(id).setValue(barangHilang)
                        .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                editNamaBarang.setText("");
                                editDeskripsi.setText("");
                                editLokasi.setText("");
                                editTanggal.setText("");
                                editKontak.setText("");
                                spinnerStatus.setSelection(0);
                                Toast.makeText(formkehilangan.this, "Data Kehilangan Barang Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        // Format tanggal jadi string yang enak dibaca
                        String tanggal = String.format(Locale.getDefault(), "%02d-%02d-%d",
                                selectedDay, selectedMonth + 1, selectedYear);
                        editTanggal.setText(tanggal);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}