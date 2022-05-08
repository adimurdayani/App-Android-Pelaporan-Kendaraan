package com.wahida.pelaporankendaraan;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wahida.pelaporankendaraan.core.data.network.APIUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditProfile extends AppCompatActivity {

    ImageView btn_back;
    TextInputLayout l_nama, l_email, l_username, l_phone;
    TextInputEditText edt_nama, edt_email, edt_username, edt_phone;
    LinearLayout btn_simpan;
    String nama, email, username, phone;
    StringRequest simpan;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        setinit();
        setbutton();
        chekvalidasi();
    }

    private void setbutton() {
        btn_back.setOnClickListener(v -> {
            onBackPressed();
        });
        btn_simpan.setOnClickListener(v -> {
            if (validasi()) {
                setSimpan();
            }
        });
    }

    private void setSimpan() {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitleText("Loading");
        dialog.setContentText("Mohon tunggu sebentar");
        dialog.setCancelable(false);
        dialog.show();
        simpan = new StringRequest(Request.Method.POST, APIUrl.POST_UBAH_PROFILE, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    showDialog("Profile berhasil disimpan!");
                } else {
                    koneksiError(object.getString("message"));
                }
            } catch (JSONException e) {
                koneksiError(e.toString());
            }
            dialog.dismissWithAnimation();
        }, error -> {
            dialog.dismissWithAnimation();
            koneksiError(error.toString());
            Log.d("Response", "Error: " + error.networkResponse);
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = String.valueOf(preferences.getInt("id", 0));
                HashMap<String, String> map = new HashMap<>();
                map.put("id", id);
                map.put("nama", nama);
                map.put("username", username);
                map.put("email", email);
                map.put("phone", phone);
                return map;
            }
        };
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(simpan);
    }

    private void koneksiError(String pesan) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(pesan)
                .setConfirmText("OK")
                .show();
    }

    private void showDialog(String pesan) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses")
                .setContentText(pesan)
                .setConfirmText("OK")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .show();
    }

    private void setinit() {
        btn_back = findViewById(R.id.btn_back);
        l_nama = findViewById(R.id.l_nama);
        l_email = findViewById(R.id.l_email);
        l_username = findViewById(R.id.l_username);
        l_phone = findViewById(R.id.l_phone);
        edt_nama = findViewById(R.id.edt_nama);
        edt_email = findViewById(R.id.edt_email);
        edt_username = findViewById(R.id.edt_username);
        edt_phone = findViewById(R.id.edt_phone);
        btn_simpan = findViewById(R.id.btn_simpan);
    }

    public void getTextInput() {
        nama = edt_nama.getText().toString().trim();
        email = edt_email.getText().toString().trim();
        username = edt_username.getText().toString().trim();
        phone = edt_phone.getText().toString().trim();
    }

    private void chekvalidasi() {
        getTextInput();
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

        edt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email.isEmpty()) {
                    l_email.setErrorEnabled(false);
                } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    l_email.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (username.isEmpty()) {
                    l_username.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (phone.isEmpty()) {
                    l_phone.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validasi() {
        getTextInput();

        if (nama.isEmpty()) {
            l_nama.setErrorEnabled(true);
            l_nama.setError("Nama tidak boleh kosong!");
            return false;
        }

        if (email.isEmpty()) {
            l_email.setErrorEnabled(true);
            l_email.setError("Email tidak boleh kosong!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            l_email.setErrorEnabled(true);
            l_email.setError("Format email salah!");
            return false;
        }

        if (username.isEmpty()) {
            l_username.setErrorEnabled(true);
            l_username.setError("Username tidak boleh kosong!");
            return false;
        }

        if (phone.isEmpty()) {
            l_phone.setErrorEnabled(true);
            l_phone.setError("Phone tidak boleh kosong!");
            return false;
        }
        return true;
    }
}