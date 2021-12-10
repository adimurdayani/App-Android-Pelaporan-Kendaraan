package com.wahida.pelaporankendaraan.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.wahida.pelaporankendaraan.R;
import com.wahida.pelaporankendaraan.core.data.adapter.LaporanAdapter;
import com.wahida.pelaporankendaraan.core.data.model.DataLaporan;
import com.wahida.pelaporankendaraan.core.data.network.APIUrl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class HomeFragment extends Fragment {
    private View view;
    private TextView txt_nama, txt_tanggal;
    private ImageView btn_profile;
    private ShimmerFrameLayout frameLayout;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private SharedPreferences preferences;
    private RecyclerView.LayoutManager layoutManager;
    private LaporanAdapter adapter;
    public static ArrayList<DataLaporan> listLaporan;
    private StringRequest getDataLaporan;
    private SearchView searchView;

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        init();
        setDisplay();
        return view;
    }

    private void setDisplay() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint( "SimpleDateFormat" )
        SimpleDateFormat formatTanggal = new SimpleDateFormat("EEE, MMM yyyy");
        String tgl = formatTanggal.format(calendar.getTime());
        txt_tanggal.setText(tgl);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        txt_nama.setText(preferences.getString("nama", ""));
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataLaporan();
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
                getDataLaporan();
                return false;
            }
        });
    }

    private void getDataLaporan() {
        listLaporan = new ArrayList<>();
        getDataLaporan = new StringRequest(Request.Method.GET, APIUrl.GET_LAPORAN, response -> {
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
                    recyclerView.setAdapter(adapter);
                } else {
                    koneksiError(object.getString("message"));
                }
                frameLayout.stopShimmerAnimation();
                frameLayout.setVisibility(View.GONE);
            } catch (JSONException e) {
                koneksiError(e.toString());
            }
            refreshLayout.setRefreshing(false);
            frameLayout.stopShimmerAnimation();
            frameLayout.setVisibility(View.GONE);
        }, error -> {
            koneksiError("Terjadi kesalahan koneksi!");
            refreshLayout.setRefreshing(false);
            frameLayout.stopShimmerAnimation();
            frameLayout.setVisibility(View.GONE);

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

    private void init() {
        btn_profile = view.findViewById(R.id.btn_profile);
        txt_nama = view.findViewById(R.id.nama);
        frameLayout = view.findViewById(R.id.shimmer_layout_laporan_baru);
        recyclerView = view.findViewById(R.id.rc_data_laporan_baru);
        refreshLayout = view.findViewById(R.id.sw_data_laporan_baru);
        txt_tanggal = view.findViewById(R.id.tanggal);
        searchView = view.findViewById(R.id.searchData);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.setRefreshing(true);
        frameLayout.startShimmerAnimation();
        getDataLaporan();
    }

    @Override
    public void onPause() {
        refreshLayout.setRefreshing(false);
        frameLayout.stopShimmerAnimation();
        super.onPause();
    }
}
