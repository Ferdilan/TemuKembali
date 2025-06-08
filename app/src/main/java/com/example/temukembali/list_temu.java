package com.example.temukembali;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class list_temu extends AppCompatActivity {

    private ListView listtemu;
    private List<BarangDitemukan> listBarangDitemukan;
    private DatabaseReference databaseBarangDitemukan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_temu);

        listtemu = findViewById(R.id.listtemu);
        listBarangDitemukan = new ArrayList<>();
        databaseBarangDitemukan = FirebaseDatabase.getInstance().getReference("ditemukan");

        listtemu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BarangDitemukan barangDitemukan = listBarangDitemukan.get(position);
//                String BarangHilangid = barangHilang.getId();

                Intent intent = new Intent(list_temu.this, DetailBarangDitemukan.class);
                intent.putExtra("id", barangDitemukan.getId());
                intent.putExtra("namaBarang", barangDitemukan.namaBarang);
                intent.putExtra("deskripsi", barangDitemukan.deskripsi);
                intent.putExtra("lokasi", barangDitemukan.lokasi);
                intent.putExtra("tanggal", barangDitemukan.tanggal);
                intent.putExtra("kontak", barangDitemukan.kontak);
                intent.putExtra("status", barangDitemukan.status);

                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        databaseBarangDitemukan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBarangDitemukan.clear();
                List<String> namaBarangList = new ArrayList<>();
                List<BarangDitemukan> tempList = new ArrayList<>();

                AtomicInteger totalData = new AtomicInteger(0);
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    BarangDitemukan barangDitemukan = postSnapshot.getValue(BarangDitemukan.class);
                    if (barangDitemukan != null && barangDitemukan.uid != null) {
                        totalData.incrementAndGet();
                    }
                }

                if (totalData.get() == 0) {
                    Toast.makeText(list_temu.this, "Tidak ada data ditemukan", Toast.LENGTH_SHORT).show();
                    return;
                }

                final int[] counter = {0};  // untuk menghitung jumlah respons yang selesai

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    BarangDitemukan barangDitemukan = postSnapshot.getValue(BarangDitemukan.class);

                    if (barangDitemukan != null && barangDitemukan.uid != null) {
                        FirebaseDatabase.getInstance().getReference("pengguna").child(barangDitemukan.uid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                        String nama = userSnapshot.child("nama").getValue(String.class);
                                        String nim  = userSnapshot.child("nim").getValue(String.class);

                                        barangDitemukan.nama = nama;
                                        barangDitemukan.nim = nim;

                                        tempList.add(barangDitemukan);
                                        namaBarangList.add(barangDitemukan.namaBarang + " - " + nama + " (" + nim + ")");

                                        counter[0]++;
                                        if (counter[0] == totalData.get()) {
                                            listBarangDitemukan.clear();
                                            listBarangDitemukan.addAll(tempList);

                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                                    list_temu.this,
                                                    android.R.layout.simple_list_item_1,
                                                    namaBarangList
                                            );
                                            listtemu.setAdapter(adapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(list_temu.this, "Gagal ambil data user", Toast.LENGTH_SHORT).show();
                                        counter[0]++;
                                    }
                                });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(list_temu.this, "Data gagal diambil", Toast.LENGTH_SHORT).show();
            }
        });
    }
}