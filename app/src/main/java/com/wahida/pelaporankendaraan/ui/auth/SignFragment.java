package com.wahida.pelaporankendaraan.ui.auth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.wahida.pelaporankendaraan.HomeActivity;
import com.wahida.pelaporankendaraan.LoginActivity;
import com.wahida.pelaporankendaraan.R;
import com.wahida.pelaporankendaraan.core.data.network.APIUrl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignFragment extends Fragment {
    private View view;
    private TextView txt_signup;
    private TextInputEditText txt_username, txt_password;
    private LinearLayout btn_signin;
    private TextInputLayout l_username, l_password;
    FragmentManager fragmentManager;
    private ProgressDialog dialog;
    private StringRequest loginUser;
    private SharedPreferences session_data;
    private SharedPreferences.Editor editor;
    public static int TIMEOUT_MS = 10000;

    public SignFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin, container, false);
        init();
        setButton();
        chekValidasi();
        return view;
    }

    private void setButton() {
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    userSignIn();
                }
            }
        });

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager = getFragmentManager();
                assert fragmentManager != null;
                fragmentManager.beginTransaction()
                        .replace(R.id.fm_in, new SignUpFragment())
                        .commit();
            }
        });
    }

    private void userSignIn() {
        SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading");
        dialog.show();
        loginUser = new StringRequest(Request.Method.POST, APIUrl.POST_LOGIN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    session_data = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                    editor = session_data.edit();
                    editor.putInt("id", data.getInt("id"));
                    editor.putString("nama", data.getString("nama"));
                    editor.putString("username", data.getString("username"));
                    editor.putString("email", data.getString("email"));
                    editor.putString("image", data.getString("image"));
                    editor.putInt("user_ID", data.getInt("user_id"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    startActivity(new Intent((LoginActivity) getContext(), HomeActivity.class));
                    ((LoginActivity) getContext()).finish();
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
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("username", txt_username.getText().toString().trim());
                map.put("password", txt_password.getText().toString().trim());
                return map;
            }
        };
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(loginUser);
    }

    private void setPolice() {
        loginUser.setRetryPolicy(new RetryPolicy() {
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

    private void chekValidasi() {

        txt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txt_username.getText().toString().trim().isEmpty()) {
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
                } else if (txt_password.getText().toString().trim().length() > 5) {
                    l_password.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate() {

        if (txt_username.getText().toString().trim().isEmpty()) {
            l_username.setErrorEnabled(true);
            l_username.setError("Username tidak boleh kosong");
            return false;
        }

        if (txt_password.getText().toString().trim().isEmpty()) {
            l_password.setErrorEnabled(true);
            l_password.setError("Password tidak boleh kosong");
            return false;
        } else if (txt_password.getText().toString().trim().length() < 6) {
            l_password.setErrorEnabled(true);
            l_password.setError("Password tidak boleh kurang dari 6 karakter");
            return false;
        }
        return true;
    }

    private void init() {
        txt_signup = view.findViewById(R.id.btn_signup);
        txt_username = view.findViewById(R.id.edt_username);
        txt_password = view.findViewById(R.id.edt_password);
        l_password = view.findViewById(R.id.l_password);
        l_username = view.findViewById(R.id.l_username);
        btn_signin = view.findViewById(R.id.btn_login);
    }
}
