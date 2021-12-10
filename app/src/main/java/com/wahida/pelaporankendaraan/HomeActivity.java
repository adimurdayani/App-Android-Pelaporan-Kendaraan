package com.wahida.pelaporankendaraan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.wahida.pelaporankendaraan.ui.home.HomeFragment;
import com.wahida.pelaporankendaraan.ui.laporan.AddFragment;
import com.wahida.pelaporankendaraan.ui.profile.ProfileFragment;

public class HomeActivity extends AppCompatActivity {
    private BottomNavigationView btm_navigasi;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frm_home_layout, new HomeFragment()).commit();
        init();
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", MODE_PRIVATE);
        btm_navigasi = findViewById(R.id.btm_navigasi);
        btm_navigasi.setOnNavigationItemSelectedListener(navigasi);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigasi =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            fragment = new HomeFragment();
                            break;
                        case R.id.nav_add:
                            fragment = new AddFragment();
                            break;
                        case R.id.nav_user:
                            fragment = new ProfileFragment();
                            break;
                    }
                    assert fragment != null;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frm_home_layout, fragment)
                            .commit();
                    return true;
                }
            };
}