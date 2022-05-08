package com.wahida.pelaporankendaraan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wahida.pelaporankendaraan.core.data.network.APIUrl;
import com.wahida.pelaporankendaraan.ui.auth.SignFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditPassword extends AppCompatActivity {
    private ImageView btn_back;
    private TextInputLayout l_password, l_conf_pass;
    private TextInputEditText edt_password, edt_conf_pass;
    private LinearLayout btn_simpan;
    private String password, conf_pass;
    private SharedPreferences preferences;
    private StringRequest kirimData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        setInit();
        setButton();
        setCekValidasi();
    }

    private void setCekValidasi() {
        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_password.getText().toString().trim().isEmpty()) {
                    l_password.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_conf_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edt_conf_pass.getText().toString().trim().isEmpty()) {
                    l_conf_pass.setErrorEnabled(false);
                } else if (edt_conf_pass.getText().toString().trim().matches(edt_password.getText().toString().trim())) {
                    l_conf_pass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setButton() {
        btn_back.setOnClickListener(v -> {
            onBackPressed();
        });
        btn_simpan.setOnClickListener(v -> {
            if (validasi()) {
                setKirimData();
            }
        });
    }

    private void setKirimData() {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading");
        dialog.show();
        kirimData = new StringRequest(Request.Method.POST, APIUrl.POST_USER, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove("token");
                    editor.putBoolean("isLoggedIn", false);
                    editor.clear();
                    editor.apply();
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
            Log.d("Response", "Error: " + error.networkResponse);
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int id = preferences.getInt("id", 0);
                HashMap<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(id));
                map.put("password", password);
                return map;
            }
        };
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(kirimData);
    }

    private boolean validasi() {
        setInputText();
        if (password.isEmpty()) {
            l_password.setErrorEnabled(true);
            l_password.setError("Password tidak boleh kosong!");
            return false;
        }
        if (conf_pass.isEmpty()) {
            l_conf_pass.setErrorEnabled(true);
            l_conf_pass.setError("Konfirmasi password tidak boleh kosong!");
            return false;
        } else if (!conf_pass.matches(password)) {
            l_conf_pass.setErrorEnabled(true);
            l_conf_pass.setError("Konfirmasi password tidak sama!");
            return false;
        }
        return true;
    }

    private void setInputText() {
        password = edt_password.getText().toString().trim();
        conf_pass = edt_conf_pass.getText().toString().trim();
    }

    private void setInit() {
        btn_back = findViewById(R.id.btn_back);
        l_password = findViewById(R.id.l_password);
        l_conf_pass = findViewById(R.id.l_conf_pass);
        edt_password = findViewById(R.id.edt_password);
        edt_conf_pass = findViewById(R.id.edt_conf_pass);
        btn_simpan = findViewById(R.id.btn_simpan);
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
                .setTitleText("Ubah Password Sukses!")
                .setContentText("Klik ok untuk login!")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        startActivity(new Intent(EditPassword.this, LoginActivity.class));
                        finish();
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