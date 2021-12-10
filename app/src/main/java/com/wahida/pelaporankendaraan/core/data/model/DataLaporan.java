package com.wahida.pelaporankendaraan.core.data.model;

public class DataLaporan {

    private int id, id_kecamatan, id_kelurahan, no_kk, no_ktp, no_ken, nama_pelapor;
    private String kelamin, updated_at, keterangan, nama, status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getNama_pelapor() {
        return nama_pelapor;
    }

    public void setNama_pelapor(int nama_pelapor) {
        this.nama_pelapor = nama_pelapor;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_kecamatan() {
        return id_kecamatan;
    }

    public void setId_kecamatan(int id_kecamatan) {
        this.id_kecamatan = id_kecamatan;
    }

    public int getId_kelurahan() {
        return id_kelurahan;
    }

    public void setId_kelurahan(int id_kelurahan) {
        this.id_kelurahan = id_kelurahan;
    }

    public int getNo_kk() {
        return no_kk;
    }

    public void setNo_kk(int no_kk) {
        this.no_kk = no_kk;
    }

    public int getNo_ktp() {
        return no_ktp;
    }

    public void setNo_ktp(int no_ktp) {
        this.no_ktp = no_ktp;
    }

    public int getNo_ken() {
        return no_ken;
    }

    public void setNo_ken(int no_ken) {
        this.no_ken = no_ken;
    }

    public String getKelamin() {
        return kelamin;
    }

    public void setKelamin(String kelamin) {
        this.kelamin = kelamin;
    }
}
