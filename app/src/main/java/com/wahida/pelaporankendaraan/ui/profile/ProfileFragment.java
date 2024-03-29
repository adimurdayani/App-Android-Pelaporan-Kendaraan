package com.wahida.pelaporankendaraan.ui.profile;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.wahida.pelaporankendaraan.EditPassword;
import com.wahida.pelaporankendaraan.EditProfile;
import com.wahida.pelaporankendaraan.LoginActivity;
import com.wahida.pelaporankendaraan.R;
import com.wahida.pelaporankendaraan.UploadActivity;
import com.wahida.pelaporankendaraan.core.data.network.APIUrl;
import com.wahida.pelaporankendaraan.ui.home.HomeFragment;
import com.wahida.pelaporankendaraan.util.UrlImage;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ProfileFragment extends Fragment {
    private View view;
    private TextView nama, email, txt_jmlterkirim, txt_jmldiproses;
    private CardView btn_logout, btn_edit;
    private SharedPreferences preferences;
    CircularImageView img_user;
    private StringRequest logout;
    private StringRequest getDataLaporan;
    ImageView btn_editprofile;

    public ProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        init();
        setButton();
        setDisplay();
        return view;
    }

    private void setDisplay() {
        nama.setText(preferences.getString("nama", ""));
        email.setText(preferences.getString("email", ""));
        txt_jmlterkirim.setText(String.valueOf(preferences.getInt("jml_terkirim", 0)));
        txt_jmldiproses.setText(String.valueOf(preferences.getInt("jml", 0)));
    }

    private void setButton() {
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        btn_edit.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditPassword.class));
        });

        img_user.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), UploadActivity.class));
        });
        btn_editprofile.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditProfile.class));
        });
    }

    private void showDialog() {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Ingin logout dari aplikasi!")
                .setConfirmText("Logout")
                .setConfirmClickListener(sweetAlertDialog -> {
                    logout();
                })
                .setCancelText("Tidak")
                .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                .show();
    }

    private void getImage() {
        int id = preferences.getInt("id", 0);
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
            } catch (JSONException e) {
                koneksiError(e.toString());
            }
        }, Throwable::printStackTrace);
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getDataLaporan);
    }

    @SuppressLint("SetTextI18n")
    private void getTotalselesai() {
        int id = preferences.getInt("id", 0);
        getDataLaporan = new StringRequest(Request.Method.GET, APIUrl.GET_TOTALSELESAI + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    object.getInt("data");
                    Log.d("Response", "data " + object.getInt("data"));
                    txt_jmldiproses.setText("" + object.getInt("data"));
                } else {
                    koneksiError(object.getString("message"));
                }
            } catch (JSONException e) {
                koneksiError(e.toString());
            }
        }, Throwable::printStackTrace);
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getDataLaporan);
    }

    @SuppressLint("SetTextI18n")
    private void getTotalmenunggu() {
        int id = preferences.getInt("id", 0);
        getDataLaporan = new StringRequest(Request.Method.GET, APIUrl.GET_TOTALMENUNGGU + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    object.getInt("data");
                    Log.d("Response", "data " + object.getInt("data"));
                    txt_jmlterkirim.setText("" + object.getInt("data"));

                } else {
                    koneksiError(object.getString("message"));
                }
            } catch (JSONException e) {
                koneksiError(e.toString());
            }
        }, Throwable::printStackTrace);
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getDataLaporan);
    }

    private void logout() {
        logout = new StringRequest(Request.Method.GET, APIUrl.GET_LOGOUT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("token");
                    editor.putBoolean("isLoggedIn", false);
                    editor.clear();
                    editor.apply();
                    showDialogsukses();
                } else {
                    koneksiError("Logout gagal!");
                }
            } catch (JSONException e) {
                koneksiError(e.toString());
            }
        }, error -> {
            koneksiError(error.toString());
        });
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(logout);
    }

    private void setPolice() {
        logout.setRetryPolicy(new RetryPolicy() {
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

    private void showDialogsukses() {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Logout sukses!")
                .setConfirmText("Oke")
                .setConfirmClickListener(sweetAlertDialog -> {
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                    requireActivity().finish();
                })
                .show();
    }

    private void koneksiError(String pesan) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(pesan)
                .setConfirmText("OK")
                .show();
    }

    private void init() {
        nama = view.findViewById(R.id.nama);
        email = view.findViewById(R.id.email);
        btn_logout = view.findViewById(R.id.btn_logout);
        txt_jmldiproses = view.findViewById(R.id.jmlh_diproses);
        txt_jmlterkirim = view.findViewById(R.id.jmlh_terkirim);
        btn_edit = view.findViewById(R.id.btn_edit);
        img_user = view.findViewById(R.id.img_user);
        btn_editprofile = view.findViewById(R.id.btn_editprofile);
    }

    @Override
    public void onResume() {
        super.onResume();
        getImage();
        getTotalselesai();
        getTotalmenunggu();
    }
}
