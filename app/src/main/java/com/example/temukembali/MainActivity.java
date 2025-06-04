//Karena setiap kehilangan pantas untuk ditemukan kembali
//Barang yang hilang tidak selalu harus ditemukan oleh kebetulan.
//Terkadang, ia hanya butuh sistem yang benar untuk menghubungkan dua niat baik.
package com.example.temukembali;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void barangHilang(View view) {
        Intent intent =  new Intent(this, formkehilangan.class);
        startActivity(intent);
    }

    public void barangTemu(View view) {
        Intent intent =  new Intent(this, formpenemuan.class);
        startActivity(intent);
    }

    public void lihatHilang(View view) {
        Intent intent =  new Intent(this, list_hilang.class);
        startActivity(intent);
    }

    public void lihatTemu(View view) {
        Intent intent =  new Intent(this, list_temu.class);
        startActivity(intent);
    }
}