package com.wahida.pelaporankendaraan.ui.laporan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.wahida.pelaporankendaraan.R;
import com.wahida.pelaporankendaraan.TambahLaporan;
import com.wahida.pelaporankendaraan.core.data.adapter.LaporanAdapter;
import com.wahida.pelaporankendaraan.core.data.model.DataLaporan;
import com.wahida.pelaporankendaraan.core.data.network.APIUrl;
import com.wahida.pelaporankendaraan.util.UrlImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AddFragment extends Fragment {
    private View view;
    private LinearLayout btn_tambah;
    private TextView txt_nama, jml_laporan;
    private SharedPreferences preferences;
    private ShimmerFrameLayout shimmer_layout;
    private StringRequest getDataLaporan;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    public static ArrayList<DataLaporan> listLaporan;
    private LaporanAdapter adapter;
    int id;
    private SearchView searchView;
    CircularImageView img_user;

    public AddFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_laporan, container, false);
        preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        id = preferences.getInt("id", 0);
        setinit();
        setButton();
        setDisplay();
        return view;
    }

    private void setDisplay() {
        txt_nama.setText(preferences.getString("nama", ""));
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getdata();
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getSearchData().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getdata();
                return false;
            }
        });
    }

    private void setButton() {
        btn_tambah.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), TambahLaporan.class));
        });
    }

    private void setinit() {
        btn_tambah = view.findViewById(R.id.btn_tambah);
        txt_nama = view.findViewById(R.id.nama);
        shimmer_layout = view.findViewById(R.id.shimmer_layout);
        recyclerView = view.findViewById(R.id.rc_data);
        refreshLayout = view.findViewById(R.id.sw_data);
        searchView = view.findViewById(R.id.search);
        jml_laporan = view.findViewById(R.id.jml_laporan);
        img_user = view.findViewById(R.id.img_user);
    }

    private void getImage() {
        int id = preferences.getInt("id", 0);
        refreshLayout.setRefreshing(true);
        getDataLaporan = new StringRequest(Request.Method.GET, APIUrl.GET_USER_ID + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    String image = data.getString("image");
                    Log.d("Response", "Image: " + image);
                    Picasso.get()
                            .load(UrlImage.BASE_URL_USERS + image)
                            .error(R.drawable.user_circle)
                            .placeholder(R.drawable.user_circle)
                            .into(img_user);
                } else {
                    koneksiError(object.getString("message"));
                }
                refreshLayout.setRefreshing(false);
            } catch (JSONException e) {
                koneksiError(e.toString());
            }
            refreshLayout.setRefreshing(false);
        }, error -> {
            refreshLayout.setRefreshing(false);
        });
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getDataLaporan);
    }

    @SuppressLint( "SetTextI18n" )
    private void getdata() {
        listLaporan = new ArrayList<>();
        refreshLayout.setRefreshing(true);
        getDataLaporan = new StringRequest(Request.Method.GET, APIUrl.GET_LAPORAN_ID_USER + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONArray data = new JSONArray(object.getString("data"));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject getData = data.getJSONObject(i);
                        DataLaporan getLaporan = new DataLaporan();
                        getLaporan.setId(getData.getInt("id"));
                        getLaporan.setNama(getData.getString("nama"));
                        getLaporan.setKeterangan(getData.getString("keterangan"));
                        getLaporan.setUpdated_at(getData.getString("updated_at"));
                        getLaporan.setStatus(getData.getString("status"));
                        listLaporan.add(getLaporan);
                    }
                    adapter = new LaporanAdapter(getContext(), listLaporan);
                    jml_laporan.setText(listLaporan.size() + " Laporan");
                    recyclerView.setAdapter(adapter);
                } else {
                    koneksiError(object.getString("message"));
                }
            } catch (JSONException e) {
                koneksiError(e.toString());
                e.printStackTrace();
            }
            refreshLayout.setRefreshing(false);
            shimmer_layout.stopShimmerAnimation();
            shimmer_layout.setVisibility(View.GONE);
        }, error -> {
            error.printStackTrace();
            refreshLayout.setRefreshing(false);
            shimmer_layout.stopShimmerAnimation();
            shimmer_layout.setVisibility(View.GONE);
        });
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getDataLaporan);
    }

    private void setPolice() {
        getDataLaporan.setRetryPolicy(new RetryPolicy() {
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
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(pesan)
                .setConfirmText("OK")
                .show();
    }


    @Override
    public void onResume() {
        super.onResume();
        getdata();
        getImage();
        shimmer_layout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        shimmer_layout.stopShimmerAnimation();
        super.onPause();
    }
}
