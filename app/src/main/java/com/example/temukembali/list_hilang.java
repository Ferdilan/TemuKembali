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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

public class list_hilang extends AppCompatActivity {

    private ListView listhilang;
    private List<BarangHilang> listBarangHilang;
    private DatabaseReference databaseBarangHilang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_hilang);

        listhilang = findViewById(R.id.listhilang);
        listBarangHilang = new ArrayList<>();
        databaseBarangHilang = FirebaseDatabase.getInstance().getReference("kehilangan");

        listhilang.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BarangHilang barangHilang = listBarangHilang.get(position);
//                String BarangHilangid = barangHilang.getId();

                Intent intent = new Intent(list_hilang.this, DetailBarangHilang.class);
                intent.putExtra("id", barangHilang.getId());
                intent.putExtra("namaBarang", barangHilang.namaBarang);
                intent.putExtra("deskripsi", barangHilang.deskripsi);
                intent.putExtra("lokasi", barangHilang.lokasi);
                intent.putExtra("tanggal", barangHilang.tanggal);
                intent.putExtra("kontak", barangHilang.kontak);
                intent.putExtra("status", barangHilang.status);

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
        databaseBarangHilang.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listBarangHilang.clear();
                List<String> namaBarangList = new ArrayList<>();
                List<BarangHilang> tempList = new ArrayList<>();

                AtomicInteger totalData = new AtomicInteger(0);
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    BarangHilang barangHilang = postSnapshot.getValue(BarangHilang.class);
                    if (barangHilang != null && barangHilang.uid != null) {
                        totalData.incrementAndGet();
                    }
                }

                if (totalData.get() == 0) {
                    Toast.makeText(list_hilang.this, "Tidak ada data ditemukan", Toast.LENGTH_SHORT).show();
                    return;
                }

                final int[] counter = {0};  // untuk menghitung jumlah respons yang selesai

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    BarangHilang barangHilang = postSnapshot.getValue(BarangHilang.class);

                    if (barangHilang != null && barangHilang.uid != null) {
                        FirebaseDatabase.getInstance().getReference("pengguna").child(barangHilang.uid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                        String nama = userSnapshot.child("nama").getValue(String.class);
                                        String nim  = userSnapshot.child("nim").getValue(String.class);

                                        barangHilang.nama = nama;
                                        barangHilang.nim = nim;

                                        tempList.add(barangHilang);
                                        namaBarangList.add(barangHilang.namaBarang + " - " + nama + " (" + nim + ")");

                                        counter[0]++;
                                        if (counter[0] == totalData.get()) {
                                            listBarangHilang.clear();
                                            listBarangHilang.addAll(tempList);

                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                                    list_hilang.this,
                                                    android.R.layout.simple_list_item_1,
                                                    namaBarangList
                                            );
                                            listhilang.setAdapter(adapter);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(list_hilang.this, "Gagal ambil data user", Toast.LENGTH_SHORT).show();
                                        counter[0]++;
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(list_hilang.this, "Data gagal diambil", Toast.LENGTH_SHORT).show();
            }
        });
    }


//    @Override
//    protected void onStart(){
//        super.onStart();
//        databaseBarangHilang.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                listBarangHilang.clear();
//                List<String> namaBarangList = new ArrayList<>();
//                //daftar semestara untuk menunggu seluruh data user selesai diambil
//                List<BarangHilang> tempList = new ArrayList<>();
//
//                int totalData = 0;
//                //perulangan untuk mengambil data dari firebase
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    //setiap data akan ditampung di biodataSnapshot
//                    BarangHilang barangHilang = postSnapshot.getValue(BarangHilang.class);
//                    if (barangHilang != null && barangHilang.uid != null){
//                        //ambil data user dari users/id
//                        FirebaseDatabase.getInstance().getReference("users").child(barangHilang.uid)
//                                .addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
//                                        String nama = userSnapshot.child("nama").getValue(String.class);
//                                        String nim  = userSnapshot.child("nim").getValue(String.class);
//
//                                        barangHilang.nama = nama;
//                                        barangHilang.nim = nim;
//
//                                        tempList.add(barangHilang);
//                                        namaBarangList.add(barangHilang.namaBarang + " - " + nama + " (" + nim + ")");
////                                        listBarangHilang.add(barangHilang);
//
//                                        // Jika semua data sudah diambil, set adapter
//                                        if (tempList.size() == snapshot.getChildrenCount()) {
//                                            listBarangHilang.clear();
//                                            listBarangHilang.addAll(tempList);
//
//                                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                                                    list_hilang.this,
//                                                    android.R.layout.simple_list_item_1,
//                                                    namaBarangList
//                                            );
//                                            listhilang.setAdapter(adapter);
//                                        }
//                                    }
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//                                        Toast.makeText(list_hilang.this, "Gagal ambil data user", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(list_hilang.this, "Data gagal diambil", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}