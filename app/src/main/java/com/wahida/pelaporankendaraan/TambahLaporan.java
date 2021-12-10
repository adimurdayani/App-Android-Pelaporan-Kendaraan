package com.wahida.pelaporankendaraan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.wahida.pelaporankendaraan.core.data.model.Kecamatan;
import com.wahida.pelaporankendaraan.core.data.model.Kelurahan;
import com.wahida.pelaporankendaraan.core.data.network.APIUrl;
import com.wahida.pelaporankendaraan.ui.auth.SignFragment;
import com.wahida.pelaporankendaraan.util.VolleyMultipartRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

import static com.mapbox.mapboxsdk.style.layers.Property.NONE;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class TambahLaporan extends AppCompatActivity implements PermissionsListener, OnMapReadyCallback {
    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private ImageView hoveringMarker, image_stnk;
    private CardView btn_ambil_latlit, upload_stnk;
    private TextInputLayout l_no_kk, l_no_ktp, l_nama, l_latitude, l_longitude, l_keternagan, l_no_ken;
    private TextInputEditText edt_no_kk, edt_no_ktp, edt_nama, edt_latitude, edt_longitude, edt_keterangan, edt_no_ken;
    private SearchableSpinner edt_kelamin, edt_kecamatan, edt_kelurahan;
    private LinearLayout btn_kirim;
    private TextView text_tentukan_titik;
    private String no_kk, no_ktp, nama, latitude, longitude, keterangan, kecamatan, kelurahan, kelamin, no_ken;
    private StringRequest kirimData;
    private static final String DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID";
    private Layer droppedMarkerLayer;
    private ArrayList<String> listKecamatan;
    private ArrayList<Kecamatan> arrayList;
    private ArrayList<String> listKelurahan;
    private ArrayList<Kelurahan> arrayListKelurahan;
    private Uri imageUri;
    private final int kodeGallery = 1, kodeKamera = 0;
    private Bitmap bitmap = null;
    private VolleyMultipartRequest kirimDataLaporan;
    private SharedPreferences preferences;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_tambah_laporan);
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        mapView = findViewById(R.id.mapView);
        mapView.getMapAsync(this);
        setInit();
        setButton();
        setCekvalidasi();
        setDisplay();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == kodeKamera && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            image_stnk.setImageBitmap(bitmap);
            Log.d("Respon", "Img Url: " + bitmap);
        } else if (requestCode == kodeGallery && resultCode == RESULT_OK && data != null) {
            try {
                imageUri = data.getData();
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                image_stnk.setImageURI(imageUri);

                Log.d("Respon", "Img Url: " + bitmap);
            } catch (Exception e) {
                Log.d("Respon", "Error: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void setDisplay() {
        edt_nama.setText(preferences.getString("nama", ""));
        edt_kecamatan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kecamatan = String.valueOf(arrayList.get(position).getId());
                setKelurahan(arrayList.get(position).getId());
                Log.d("Response", "Kecamatan: " + kecamatan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Response", "Kecamataan tidak dipilih");
            }
        });

        edt_kelurahan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kelurahan = String.valueOf(arrayListKelurahan.get(position).getId());
                Log.d("Response", "Kelurahan: " + kelurahan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Response", "Kelurahan tidak dipilih");
            }
        });

        edt_kelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kelamin = parent.getSelectedItem().toString();
                Log.d("Response", "Kelamin: " + kelamin);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Response", "Kelamin tidak dipilih");
            }
        });
    }

    private void setInputText() {
        nama = String.valueOf(preferences.getInt("id", 0));
        Log.d("Response", "ID:  " + nama);
        no_kk = edt_no_kk.getText().toString().trim();
        no_ktp = edt_no_ktp.getText().toString().trim();
        latitude = edt_latitude.getText().toString().trim();
        longitude = edt_longitude.getText().toString().trim();
        keterangan = edt_keterangan.getText().toString().trim();
        no_ken = edt_no_ken.getText().toString().trim();
    }

    private void setCekvalidasi() {
        setInputText();
        edt_nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nama.isEmpty()) {
                    l_nama.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_no_kk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (no_kk.isEmpty()) {
                    l_no_kk.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_no_ktp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (no_ktp.isEmpty()) {
                    l_no_ktp.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_latitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (latitude.isEmpty()) {
                    l_latitude.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_longitude.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (longitude.isEmpty()) {
                    l_longitude.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_no_ken.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (no_ken.isEmpty()) {
                    l_no_ken.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_keterangan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (keterangan.isEmpty()) {
                    l_keternagan.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setButton() {
        btn_ambil_latlit.setOnClickListener(v -> {
            startActivity(new Intent(TambahLaporan.this, MapsActivity.class));
        });
        upload_stnk.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, kodeKamera);
            }
        });
        btn_kirim.setOnClickListener(v -> {
            if (validasi()) {
                setKirimData();
            }
        });
    }

    private void setKirimData() {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading");
        dialog.show();
        kirimDataLaporan = new VolleyMultipartRequest(Request.Method.POST, APIUrl.GET_LAPORAN, response -> {
            try {
                JSONObject object = new JSONObject(new String(response.data));
                if (object.getBoolean("status")) {
                    showDialog();
                } else {
                    koneksiError(object.getString("message"));
                }
            } catch (JSONException e) {
                koneksiError(e.toString());
            }
            dialog.dismissWithAnimation();
        }, error -> {
            dialog.dismissWithAnimation();
            error.printStackTrace();
            Log.d("Response", "Error: " + error.networkResponse);
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("nama_pelapor", nama);
                map.put("kelamin", kelamin);
                map.put("id_kelurahan", kelurahan);
                map.put("id_kecamatan", kecamatan);
                map.put("no_ktp", no_ktp);
                map.put("no_kk", no_kk);
                map.put("no_ken", no_ken);
                map.put("latitude", latitude);
                map.put("longitude", longitude);
                map.put("keterangan", keterangan);
                return map;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long namaGambar = System.currentTimeMillis();
                params.put("stnk", new DataPart(namaGambar + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(kirimDataLaporan);
    }

    private void setkecamatan() {
        arrayList = new ArrayList<>();
        listKecamatan = new ArrayList<>();
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading");
        dialog.show();
        kirimData = new StringRequest(Request.Method.GET, APIUrl.GET_KECAMATAN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    listKecamatan = new ArrayList<>();
                    JSONArray data = new JSONArray(object.getString("data"));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject getData = data.getJSONObject(i);
                        Kecamatan getkecamatan = new Kecamatan();
                        getkecamatan.setId(getData.getInt("id"));
                        getkecamatan.setKecamatan(getData.getString("kecamatan"));
                        arrayList.add(getkecamatan);
                    }
                    for (int i = 0; i < arrayList.size(); i++) {
                        listKecamatan.add(arrayList.get(i).getKecamatan());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item, listKecamatan);
                    edt_kecamatan.setAdapter(adapter);
                }
            } catch (JSONException e) {
                koneksiError(e.toString());
            }
            dialog.dismissWithAnimation();
        }, error -> {
            dialog.dismissWithAnimation();
            Log.d("Response", "Error: " + error.networkResponse);
        });
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(kirimData);
    }

    private void setKelurahan(int id) {
        if (id > 0) {
            edt_kelurahan.setVisibility(View.VISIBLE);
        } else {
            edt_kelurahan.setVisibility(View.GONE);
        }
        arrayListKelurahan = new ArrayList<>();
        listKelurahan = new ArrayList<>();
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading");
        dialog.show();
        kirimData = new StringRequest(Request.Method.GET, APIUrl.GET_KELURAHAN_ID + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    listKelurahan = new ArrayList<>();
                    JSONArray data = new JSONArray(object.getString("data"));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject getData = data.getJSONObject(i);
                        Kelurahan getkecamatan = new Kelurahan();
                        getkecamatan.setId(getData.getInt("id"));
                        getkecamatan.setKelurahan(getData.getString("kelurahan"));
                        arrayListKelurahan.add(getkecamatan);
                    }
                    for (int i = 0; i < arrayListKelurahan.size(); i++) {
                        listKelurahan.add(arrayListKelurahan.get(i).getKelurahan());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item, listKelurahan);
                    edt_kelurahan.setAdapter(adapter);
                }
            } catch (JSONException e) {
                koneksiError(e.toString());
            }
            dialog.dismissWithAnimation();
        }, error -> {
            dialog.dismissWithAnimation();
            Log.d("Response", "Error: " + error.networkResponse);
        });
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(kirimData);
    }

    private boolean validasi() {
        setInputText();
        if (nama.isEmpty()) {
            l_nama.setErrorEnabled(true);
            l_nama.setError("Nama tidak boleh kosong!");
            return false;
        }
        if (no_kk.isEmpty()) {
            l_no_kk.setErrorEnabled(true);
            l_no_kk.setError("Nomor KK tidak boleh kosong!");
            return false;
        }
        if (no_ktp.isEmpty()) {
            l_no_ktp.setErrorEnabled(true);
            l_no_ktp.setError("Nomor KTP tidak boleh kosong!");
            return false;
        }
        if (latitude.isEmpty()) {
            l_latitude.setErrorEnabled(true);
            l_latitude.setError("Latitude tidak boleh kosong!");
            return false;
        }
        if (longitude.isEmpty()) {
            l_longitude.setErrorEnabled(true);
            l_longitude.setError("Longitude tidak boleh kosong!");
            return false;
        }
        if (no_ken.isEmpty()) {
            l_no_ken.setErrorEnabled(true);
            l_no_ken.setError("Nomor kendaraan tidak boleh kosong!");
            return false;
        }
        if (keterangan.isEmpty()) {
            l_keternagan.setErrorEnabled(true);
            l_keternagan.setError("Keterangan tidak boleh kosong!");
            return false;
        }
        return true;
    }

    private void setInit() {
        btn_ambil_latlit = findViewById(R.id.btn_ambil_latlit);
        upload_stnk = findViewById(R.id.upload_stnk);
        l_no_kk = findViewById(R.id.l_no_kk);
        l_no_ktp = findViewById(R.id.l_no_ktp);
        l_nama = findViewById(R.id.l_nama);
        l_latitude = findViewById(R.id.l_latitude);
        l_longitude = findViewById(R.id.l_longitude);
        edt_no_kk = findViewById(R.id.edt_no_kk);
        edt_no_ktp = findViewById(R.id.edt_no_ktp);
        edt_nama = findViewById(R.id.edt_nama);
        edt_latitude = findViewById(R.id.edt_latitude);
        edt_longitude = findViewById(R.id.edt_longitude);
        edt_kelamin = findViewById(R.id.edt_kelamin);
        edt_kecamatan = findViewById(R.id.edt_kecamatan);
        edt_kelurahan = findViewById(R.id.edt_kelurahan);
        btn_kirim = findViewById(R.id.btn_kirim);
        l_keternagan = findViewById(R.id.l_keterangan);
        edt_keterangan = findViewById(R.id.edt_keterangan);
        text_tentukan_titik = findViewById(R.id.text_tentukan_titik);
        image_stnk = findViewById(R.id.image_stnk);
        l_no_ken = findViewById(R.id.l_no_ken);
        edt_no_ken = findViewById(R.id.edt_no_ken);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        TambahLaporan.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @SuppressLint( "SetTextI18n" )
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                enableLocationPlugin(style);
                Toast.makeText(TambahLaporan.this, "Drag untuk memilih lokasi", Toast.LENGTH_SHORT).show();
                hoveringMarker = new ImageView(getApplication());
                hoveringMarker.setImageResource(R.drawable.ic_marker);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
                );

                hoveringMarker.setLayoutParams(params);
                mapView.addView(hoveringMarker);
                initDroppedMarker(style);
                btn_ambil_latlit.setOnClickListener(v -> {
                    if (hoveringMarker.getVisibility() == View.VISIBLE) {
                        final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;
                        hoveringMarker.setVisibility(View.INVISIBLE);

                        btn_ambil_latlit.setBackground(ContextCompat.getDrawable(TambahLaporan.this, R.drawable.btn_sign));
                        text_tentukan_titik.setText("Select Location");

                        if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                            GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                            if (source != null) {
                                source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                                edt_latitude.setText("" + mapTargetLatLng.getLatitude());
                                edt_longitude.setText("" + mapTargetLatLng.getLongitude());
                                Log.d("Response", "Latitude: " + mapTargetLatLng.getLatitude());
                                Log.d("Response", "Longitude: " + mapTargetLatLng.getLongitude());
                            }
                            droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                            if (droppedMarkerLayer != null) {
                                droppedMarkerLayer.setProperties(visibility(VISIBLE));
                            }
                        }
                        reverseGeocode(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                    } else {
                        btn_ambil_latlit.setBackground(ContextCompat.getDrawable(TambahLaporan.this, R.drawable.btn_sign2));
                        text_tentukan_titik.setText("Select a location");
                        hoveringMarker.setVisibility(View.VISIBLE);

                        droppedMarkerLayer = style.getLayer(DROPPED_MARKER_LAYER_ID);
                        if (droppedMarkerLayer != null) {
                            droppedMarkerLayer.setProperties(visibility(NONE));
                        }
                    }
                });
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
                enableLocationPlugin(style);
            }
        } else {
            Toast.makeText(this, "User Lokasi tidak ditemukan", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initDroppedMarker(@NonNull Style loadedMapsStyle) {
        loadedMapsStyle.addSource(new GeoJsonSource("dropped-marker-source-id"));
        loadedMapsStyle.addLayer(new SymbolLayer(DROPPED_MARKER_LAYER_ID,
                "dropped-marker-source-id").withProperties(
                iconImage("dropped-marker-source-id"),
                visibility(VISIBLE),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        ));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        setkecamatan();
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

    private void reverseGeocode(final Point point) {
        try {
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(getString(R.string.mapbox_access_token))
                    .query(Point.fromLngLat(point.longitude(), point.latitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                    if (response.body() != null) {
                        List<CarmenFeature> result = response.body().features();
                        if (result.size() > 0) {
                            CarmenFeature feature = result.get(0);

                            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                                @Override
                                public void onStyleLoaded(@NonNull Style style) {
                                    if (style.getLayer(DROPPED_MARKER_LAYER_ID) != null) {
                                        Toast.makeText(TambahLaporan.this, "Lokasi di temukan" + feature.placeName(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(TambahLaporan.this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                    Log.e("Geocoding Failure: %s", t.getMessage());
                }
            });
        } catch (ServicesException serviceExeption) {
            Log.e("Error geocoding: %s", serviceExeption.toString());
            serviceExeption.printStackTrace();
        }
    }

    @SuppressWarnings( ("MissingPermission") )
    private void enableLocationPlugin(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);
            locationComponent.setRenderMode(RenderMode.NORMAL);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
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

    private void setPolice() {
        kirimData.setRetryPolicy(new RetryPolicy() {
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

    private void showDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Laporan Sukses!")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        onBackPressed();
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void koneksiError(String pesan) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(pesan)
                .setConfirmText("OK")
                .show();
    }
}