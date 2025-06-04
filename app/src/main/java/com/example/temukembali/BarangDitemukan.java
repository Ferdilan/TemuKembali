package com.example.temukembali;

public class BarangDitemukan {
    public String id;
    public String namaBarang;
    public String deskripsi;
    public String lokasi;
    public String tanggal;
    public String fotoUrl;
    public String kontak;
    public String status;

    public BarangDitemukan() {
    }

    public BarangDitemukan(String id, String namaBarang, String deskripsi, String lokasi, String tanggal, String fotoUrl, String kontak, String status) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.tanggal = tanggal;
        this.fotoUrl = fotoUrl;
        this.kontak = kontak;
        this.status = status;
    }
}
