package com.example.temukembali;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

public class formkehilangan extends AppCompatActivity {

    private EditText editNamaBarang, editDeskripsi, editLokasi, editTanggal, editFoto, editKontak;
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
        editFoto = findViewById(R.id.editFotoHilang);
        spinnerStatus = findViewById(R.id.statusHilang);
        databaseKehilangan = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("kehilangan");


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
        String foto = editFoto.getText().toString();
        String kontak = editKontak.getText().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        if(!TextUtils.isEmpty(namaBarang) && !TextUtils.isEmpty(deskripsi) && !TextUtils.isEmpty(lokasi) && !TextUtils.isEmpty(tanggal) && !TextUtils.isEmpty(kontak) && !TextUtils.isEmpty(foto) && !TextUtils.isEmpty(status)){
            String id = databaseKehilangan.push().getKey();
            BarangHilang barangHilang = new BarangHilang(id, namaBarang, deskripsi, lokasi, tanggal, foto, kontak, status);
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
        }else {
            Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
        }
    }
}