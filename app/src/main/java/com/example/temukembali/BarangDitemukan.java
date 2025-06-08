package com.example.temukembali;

public class BarangDitemukan {
    public String id;
    public String namaBarang;
    public String deskripsi;
    public String lokasi;
    public String tanggal;
    public String kontak;
    public String status;
    public String uid;
    public String nim;
    public String nama;

    public BarangDitemukan() {
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getKontak() {
        return kontak;
    }

    public String getUid() {
        return uid;
    }

    public String getNim() {
        return nim;
    }

    public String getNama() {
        return nama;
    }

    public BarangDitemukan(String id, String namaBarang, String deskripsi, String lokasi, String tanggal, String kontak, String status, String uid) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.tanggal = tanggal;
        this.kontak = kontak;
        this.status = status;
        this.uid = uid;
    }
}
