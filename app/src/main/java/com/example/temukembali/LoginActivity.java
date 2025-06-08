package com.example.temukembali;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etNama, etNim, etPassword;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etNama = findViewById(R.id.et_nama);
        etNim = findViewById(R.id.et_nim);
        etPassword = findViewById(R.id.et_password);
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference("pengguna");

        findViewById(R.id.btn_login).setOnClickListener(v -> login());
        findViewById(R.id.btn_register).setOnClickListener(v -> register());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String buildEmail(String nim) {
        return nim + "@polinema.ac.id";
    }

    private void login() {
        String nim = etNim.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = buildEmail(nim);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Login gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void register() {
        String nim = etNim.getText().toString().trim();
        String nama = etNama.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = buildEmail(nim);

        if (nim.isEmpty() || nama.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Lengkapi semua data. Password ≥ 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = mAuth.getCurrentUser().getUid();

                    Map<String, Object> biodata = new HashMap<>();
                    biodata.put("nim", nim);
                    biodata.put("nama", nama);
                    biodata.put("waktuDaftar", getWaktuSekarang());

                    dbRef.child(uid).setValue(biodata)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Registrasi dan simpan data berhasil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "Gagal simpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Registrasi gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private String getWaktuSekarang() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }

}