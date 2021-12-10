package com.wahida.pelaporankendaraan.core.data.model;

public class Kelurahan {
    private int id, id_kecamatan;
    private String kelurahan;

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

    public String getKelurahan() {
        return kelurahan;
    }

    public void setKelurahan(String kelurahan) {
        this.kelurahan = kelurahan;
    }
}
