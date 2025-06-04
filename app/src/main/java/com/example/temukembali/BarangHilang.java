package com.example.temukembali;

public class BarangHilang {
    public String id;
    public String namaBarang;
    public String deskripsi;
    public String lokasi;
    public String tanggal;
    public String foto;
    public String kontak;
    public String status;

    public BarangHilang() {
    //diperlukan firebase
    }

    public BarangHilang(String id, String namaBarang, String deskripsi, String lokasi, String tanggal, String foto, String kontak, String status) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.tanggal = tanggal;
        this.foto = foto;
        this.kontak = kontak;
        this.status = status;
    }

    // Getter dan Setter (boleh pakai lombok jika ingin otomatis)
}
