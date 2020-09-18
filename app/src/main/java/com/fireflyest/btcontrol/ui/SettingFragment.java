package com.fireflyest.btcontrol.ui;


import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.fireflyest.btcontrol.R;

public class SettingFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference_left, rootKey);

    }

}
