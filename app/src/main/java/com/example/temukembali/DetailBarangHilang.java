package com.example.temukembali;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DetailBarangHilang extends AppCompatActivity {

    private TextView tvNama, tvDeskripsi, tvLokasi, tvTanggal, tvKontak, tvStatus, tvPenemu;
    private Button buttonWhatsApp, buttonKonfirmasi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_barang_hilang);

        tvNama = findViewById(R.id.tvNamaBarang);
        tvDeskripsi = findViewById(R.id.tvDeskripsi);
        tvLokasi = findViewById(R.id.tvLokasi);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvKontak = findViewById(R.id.tvKontak);
        tvStatus = findViewById(R.id.tvStatus);
        tvPenemu = findViewById(R.id.tvPenemu);
        buttonWhatsApp = findViewById(R.id.button_whatsapp);
        buttonKonfirmasi = findViewById(R.id.btnKonfirmasi);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uidSekarang = auth.getCurrentUser().getUid();
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("kehilangan").child();

        // Ambil data dari intent
        String barangId = getIntent().getStringExtra("id");
        String nama = getIntent().getStringExtra("namaBarang");
        String deskripsi = getIntent().getStringExtra("deskripsi");
        String lokasi = getIntent().getStringExtra("lokasi");
        String tanggal = getIntent().getStringExtra("tanggal");
        String kontak = getIntent().getStringExtra("kontak");
        String status = getIntent().getStringExtra("status");

        if (barangId == null || barangId.isEmpty()) {
            Toast.makeText(this, "ID barang tidak ditemukan.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tampilkan
        tvNama.setText(nama);
        tvDeskripsi.setText(deskripsi);
        tvLokasi.setText(lokasi);
        tvTanggal.setText(tanggal);
        tvKontak.setText(kontak);
        tvStatus.setText(status);

        // Sembunyikan tombol jika status sudah "ditemukan"
        if (status != null && status.equalsIgnoreCase("ditemukan")) {
            buttonKonfirmasi.setVisibility(View.GONE);
        }

        // Jika status ditemukan, tampilkan data penemu
        if (status != null && status.equalsIgnoreCase("ditemukan")) {
            DatabaseReference db = FirebaseDatabase.getInstance()
                    .getReference("kehilangan")
                    .child(barangId);

            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String penemuNama = snapshot.child("penemu_nama").getValue(String.class);
                        String penemuNim = snapshot.child("penemu_nim").getValue(String.class);

                        if (penemuNama != null && penemuNim != null) {
                            tvPenemu.setText("Ditemukan oleh: " + penemuNama + " (" + penemuNim + ")");
                            tvPenemu.setVisibility(View.VISIBLE);
                        } else {
                            tvPenemu.setText("Penemu belum teridentifikasi");
                            tvPenemu.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(DetailBarangHilang.this, "Gagal memuat data penemu", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tvPenemu.setVisibility(View.GONE); // disembunyikan jika belum ditemukan
        }


        // Set listener tombol konfirmasi
        buttonKonfirmasi.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            new AlertDialog.Builder(DetailBarangHilang.this)
                    .setTitle("Konfirmasi")
                    .setMessage("Apakah Anda yakin barang ini sudah ditemukan dan dikembalikan?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        DatabaseReference userRef = FirebaseDatabase.getInstance()
                                .getReference("pengguna").child(uid);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    String nimPenemu = snapshot.child("nim").getValue(String.class);
                                    String namaPenemu = snapshot.child("nama").getValue(String.class);

                                    if (nimPenemu != null && namaPenemu != null){
                                        //tampilkan di ui
                                        tvPenemu.setText("Ditemukan oleh: " + namaPenemu + " (" + nimPenemu + ")");
                                        tvPenemu.setVisibility(View.VISIBLE);

                                        // Update ke database
                                        DatabaseReference db = FirebaseDatabase.getInstance()
                                                .getReference("kehilangan")
                                                .child(barangId);


                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("status", "ditemukan");
                                        updates.put("penemu_nim", nimPenemu);
                                        updates.put("penemu_nama", namaPenemu);

                                        db.updateChildren(updates).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                tvStatus.setText("ditemukan");
                                                buttonKonfirmasi.setVisibility(View.GONE);
                                                Toast.makeText(DetailBarangHilang.this,
                                                        "Barang dikonfirmasi ditemukan oleh " + namaPenemu,
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(DetailBarangHilang.this,
                                                        "Gagal mengonfirmasi barang ditemukan", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(DetailBarangHilang.this,
                                                "Data penemu tidak lengkap", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(DetailBarangHilang.this, "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(DetailBarangHilang.this, "Gagal mengambil data pengguna", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void buttonWhatsApp(View view) {
        String nomor = tvKontak.getText().toString();

        if (nomor != null && !nomor.isEmpty()){
            // Ubah awalan 0 menjadi 62 (kode negara Indonesia)
            String noWa = nomor.replaceFirst("^0", "62");

            //Tulis pesan otomatis
            String pesan = "Halo, saya ingin bertanya tentang barang yang Anda laporkan di aplikasi TemuKembali.";

            //format url wa
            String url = "https://wa.me/" + noWa + "?text=" + Uri.encode(pesan);

            //intent untuk membuka wa
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));

            try {
                startActivity(intent);
            }catch (Exception e){
                Toast.makeText(this, "WhatsApp tidak terpasang di perangkat Anda", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Nomor WhatsApp Tidak Tersedia", Toast.LENGTH_SHORT).show();
        }
    }




}