package com.wahida.pelaporankendaraan.core.data.network;

public class APIUrl {
    //ip connection wifi 192.168.43.68 ip localhost 10.0.2.2
    public static final String BASE_URL = "http://pelaporan-kendaraan.my.id/api/";
    public static final String POST_LOGIN = BASE_URL + "auth/login";
    public static final String GET_LOGOUT = BASE_URL + "auth/logout";
    public static final String POST_REGIST = BASE_URL + "auth/register";
    public static final String GET_LAPORAN = BASE_URL + "laporan";
    public static final String GET_LAPORAN_ID = BASE_URL + "laporan?id=";
    public static final String GET_KECAMATAN = BASE_URL + "kecamatan";
    public static final String GET_KELURAHAN_ID = BASE_URL + "kelurahan?id_kecamatan=";
    public static final String POST_USER = BASE_URL + "user";
    public static final String GET_USER_ID = BASE_URL + "user?id=";
    public static final String GET_LAPORAN_ID_USER = BASE_URL + "laporan/laporanuser?nama_pelapor=";
    public static final String GET_LAPORAN_TERKIRIM = BASE_URL + "laporan/terkirim?status=";
    public static final String GET_LAPORAN_DALAM_PROSES = BASE_URL + "laporan/dalamproses?status=";
    public static final String POST_UPLOAD_IMAGE = BASE_URL + "user/upload";
    public static final String POST_UBAH_PROFILE = BASE_URL + "user/ubahprofile";
    public static final String GET_TOTALSELESAI = BASE_URL + "laporan/total?nama_pelapor=";
    public static final String GET_TOTALMENUNGGU = BASE_URL + "laporan/totalmenunggu?nama_pelapor=";
}
