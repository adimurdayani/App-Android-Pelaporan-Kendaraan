package com.wahida.pelaporankendaraan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.wahida.pelaporankendaraan.ui.auth.SignFragment;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportFragmentManager().beginTransaction().replace(R.id.fm_in, new SignFragment()).commit();
    }
}