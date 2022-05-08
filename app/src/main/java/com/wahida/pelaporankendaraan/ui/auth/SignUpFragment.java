package com.wahida.pelaporankendaraan.ui.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wahida.pelaporankendaraan.R;
import com.wahida.pelaporankendaraan.core.data.network.APIUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignUpFragment extends Fragment {

    private View view;
    private ImageView btn_back;
    private LinearLayout btn_register;
    private TextView txt_nama, txt_username, txt_email, txt_password, txt_confim;
    private TextInputLayout l_nama, l_username, l_email, l_password, l_confirm;
    private StringRequest userRegist;
    private String nama, email, username, password, conf_pass, token;
    private ProgressDialog dialog;

    public SignUpFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signup, container, false);
        init();
        return view;
    }

    public void init() {
        btn_back = view.findViewById(R.id.btn_back);
        btn_register = view.findViewById(R.id.btn_register);
        txt_confim = view.findViewById(R.id.edt_conf_pass);
        txt_username = view.findViewById(R.id.edt_username);
        txt_email = view.findViewById(R.id.edt_email);
        txt_nama = view.findViewById(R.id.edt_name);
        txt_password = view.findViewById(R.id.edt_password);
        l_nama = view.findViewById(R.id.l_name);
        l_username = view.findViewById(R.id.l_username);
        l_email = view.findViewById(R.id.l_email);
        l_password = view.findViewById(R.id.l_password);
        l_confirm = view.findViewById(R.id.l_conf_pass);

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validasi()) {
                    userSignUp();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                fragmentManager.beginTransaction()
                        .replace(R.id.fm_in, new SignFragment())
                        .commit();
            }
        });

        chekvalidasi();

    }

    public void getTextInput() {
        nama = txt_nama.getText().toString().trim();
        email = txt_email.getText().toString().trim();
        username = txt_username.getText().toString().trim();
        password = txt_password.getText().toString().trim();
        conf_pass = txt_confim.getText().toString().trim();
        token = FirebaseInstanceId.getInstance().getToken();
    }

    private void userSignUp() {
        SweetAlertDialog dialog = new SweetAlertDialog(requireContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading");
        dialog.show();
        userRegist = new StringRequest(Request.Method.POST, APIUrl.POST_REGIST, response -> {
            try {
                JSONObject object = new JSONObject(response);
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
            koneksiError(error.toString());
            Log.d("Response", "Error: " + error.networkResponse);
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("nama", nama);
                map.put("username", username);
                map.put("email", email);
                map.put("password", password);
                map.put("token", token);
                return map;
            }
        };
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(userRegist);
    }

    private void chekvalidasi() {

        txt_nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txt_nama.getText().toString().trim().isEmpty()) {
                    l_nama.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txt_email.getText().toString().isEmpty()) {
                    l_email.setErrorEnabled(false);
                } else if (Patterns.EMAIL_ADDRESS.matcher(txt_email.getText().toString().trim()).matches()) {
                    l_email.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txt_username.getText().toString().isEmpty()) {
                    l_username.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txt_password.getText().toString().trim().isEmpty()) {
                    l_password.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txt_confim.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txt_confim.getText().toString().trim().isEmpty()) {
                    l_confirm.setErrorEnabled(false);
                } else if (txt_confim.getText().toString().trim().matches(txt_password.getText().toString().trim())) {
                    l_confirm.setErrorEnabled(false);
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

        if (password.isEmpty()) {
            l_password.setErrorEnabled(true);
            l_password.setError("Password tidak boleh kosong!");
            return false;
        }

        if (conf_pass.isEmpty()) {
            l_confirm.setErrorEnabled(true);
            l_confirm.setError("Konfirmasi password tidak boleh kosong!");
            return false;
        } else if (!conf_pass.matches(password)) {
            l_confirm.setErrorEnabled(true);
            l_confirm.setError("Konfirmasi password tidak sama!");
            return false;
        }
        return true;
    }

    private void setPolice() {
        userRegist.setRetryPolicy(new RetryPolicy() {
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
        new SweetAlertDialog(requireContext(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Register Sukses!")
                .setContentText("Klik ok untuk login!")
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.fm_in, new SignFragment())
                                .commit();
                        sDialog.dismissWithAnimation();
                    }
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
}
