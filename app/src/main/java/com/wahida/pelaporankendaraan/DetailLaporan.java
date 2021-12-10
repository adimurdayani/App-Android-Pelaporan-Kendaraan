package com.wahida.pelaporankendaraan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.squareup.picasso.Picasso;
import com.wahida.pelaporankendaraan.core.data.adapter.LaporanAdapter;
import com.wahida.pelaporankendaraan.core.data.model.DataLaporan;
import com.wahida.pelaporankendaraan.core.data.network.APIUrl;
import com.wahida.pelaporankendaraan.util.UrlImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailLaporan extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {
    private ImageView btn_back, img_stnk;
    private TextView nama, no_ktp, no_kk, alamat;
    private String id;
    private StringRequest getData;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private ImageView hoveringMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_detail_laporan);
        id = getIntent().getStringExtra("id");
        mapView = findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        setInit();
    }

    private void setInit() {
        btn_back = findViewById(R.id.btn_back);
        nama = findViewById(R.id.nama);
        no_ktp = findViewById(R.id.no_ktp);
        no_kk = findViewById(R.id.no_kk);
        alamat = findViewById(R.id.alamat);
        img_stnk = findViewById(R.id.img_stnk);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @SuppressLint( "SetTextI18n" )
    private void getDataLaporan() {
        getData = new StringRequest(Request.Method.GET, APIUrl.GET_LAPORAN_ID + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    nama.setText(": " + data.getString("nama_pelapor"));
                    no_ktp.setText(": " + data.getString("no_ktp"));
                    no_kk.setText(": " + data.getString("no_kk"));
                    alamat.setText(": Kelurahan " + data.getString("kelurahan") + ", Kecamatan " + data.getString("kecamatan"));
                    Picasso.get()
                            .load(UrlImage.BASE_URL_IMAGE + data.getString("stnk"))
                            .into(img_stnk);
                } else {
                    koneksiError(object.getString("message"));
                }
            } catch (JSONException e) {
                koneksiError(e.toString());
            }
        }, error -> {
            koneksiError("Terjadi kesalahan koneksi!");
        });
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(getData);
    }

    private void setPolice() {
        getData.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                    koneksiError("Koneksi gagal");
                }
            }
        });
    }

    private void koneksiError(String pesan) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(pesan)
                .setConfirmText("OK")
                .show();
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        DetailLaporan.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
//                enableLocationPlugin(style);
                hoveringMarker = new ImageView(getApplication());
                hoveringMarker.setImageResource(R.drawable.ic_marker);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
                );

                hoveringMarker.setLayoutParams(params);
                mapView.addView(hoveringMarker);
            }
        });
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "User Lokasi", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted && mapboxMap != null) {
            Style style = mapboxMap.getStyle();
            if (style != null) {
//                enableLocationPlugin(style);
            }
        } else {
            Toast.makeText(this, "User Lokasi tidak ditemukan", Toast.LENGTH_LONG).show();
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDataLaporan();
        mapView.onResume();
    }

    @Override
    @SuppressWarnings( {"MissingPermission"} )
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int res = checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE);
            if (res != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, 123);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1002;
    @SuppressLint( "MissingSuperCall" )
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "READ_PHONE_STATE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}