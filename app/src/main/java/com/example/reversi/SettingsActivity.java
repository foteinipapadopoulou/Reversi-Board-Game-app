package com.example.reversi;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static int count;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
//      Setting the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings_name));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10.f);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        prefs.registerOnSharedPreferenceChangeListener(this);
//        If it's not the first time running the app , set count = 4 so when a setting change , display thw snackbar
        if (prefs.getBoolean("first_time", true)) count = 0;
        else count = 4;

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//        Count > 3 means that we have already run the app so the default values has been initialized
        if (count > 3) {
            if (!(s.equals("first_time"))) {
                displayMessageSnackbar(s);
            }
            changeFirstTimePref();
        } else {
            count++;
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    /*
     *Display a snackbar for the settings activity and preferences
     * */
    public void displayMessageSnackbar(String s) {
        Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content), "You change " + s, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    /*
     * Change first_time preference to false
     * */
    public void changeFirstTimePref() {
        SharedPreferences.Editor editor = prefs.edit();
        count++;
        editor.putBoolean("first_time", false);
        editor.commit();
    }
}