package com.example.temukembali;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.Locale;

public class formpenemuan extends AppCompatActivity {

    private EditText editNamaBarang, editDeskripsi, editLokasi, editTanggal, editKontak;
    private Spinner spinnerStatus;
    //DatabaseReference sebagai referensi data dari firebase
    private DatabaseReference databasePenemuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formpenemuan);

        //inisialisasi komponen
        editNamaBarang = findViewById(R.id.editnamaBarangDitemukan);
        editDeskripsi = findViewById(R.id.editDeskripsiDitemukan);
        editLokasi = findViewById(R.id.editLokasiDitemukan);
        editTanggal = findViewById(R.id.editTanggalDitemukan);
        editKontak = findViewById(R.id.editKontakDitemukan);
        spinnerStatus = findViewById(R.id.statusDitemukan);
        databasePenemuan = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("ditemukan");

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

    public void addDitemukan(View view) {
        //ambil data dari EditText dan Spinner
        String namaBarang = editNamaBarang.getText().toString();
        String deskripsi = editDeskripsi.getText().toString();
        String lokasi = editLokasi.getText().toString();
        String tanggal = editTanggal.getText().toString();
        String kontak = editKontak.getText().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        // Validasi isian
        if (!TextUtils.isEmpty(namaBarang) && !TextUtils.isEmpty(deskripsi)  && !TextUtils.isEmpty(lokasi) && !TextUtils.isEmpty(tanggal) && !TextUtils.isEmpty(kontak) && !TextUtils.isEmpty(status)) {
            String id = databasePenemuan.push().getKey();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                BarangDitemukan barangDitemukan = new BarangDitemukan(id, namaBarang, deskripsi, lokasi, tanggal, kontak, status, uid);
                databasePenemuan.child(id).setValue(barangDitemukan)
                        .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                editNamaBarang.setText("");
                                editDeskripsi.setText("");
                                editLokasi.setText("");
                                editTanggal.setText("");
                                editKontak.setText("");
                                spinnerStatus.setSelection(0);
                                Toast.makeText(formpenemuan.this, "Data Penemuan Barang Berhasil ditambahkan", Toast.LENGTH_SHORT).show();
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