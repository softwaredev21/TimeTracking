package net.gsantner.webappwithlogin.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import net.gsantner.opoc.preference.GsPreferenceFragmentCompat;
import net.gsantner.opoc.preference.SharedPreferencesPropertyBackend;
import net.gsantner.webappwithlogin.util.AppSettings;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.live.gdev.timetracker.R;

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.settings_appbar)
    protected AppBarLayout appBarLayout;
    @BindView(R.id.settings_toolbar)
    protected Toolbar toolbar;


    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.settings__activity);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.title_activity_settings);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SettingsActivity.this.onBackPressed();
            }
        });
        showFragment(SettingsFragmentMaster.TAG, false);
    }

    protected void showFragment(String tag, boolean addToBackStack) {
        GsPreferenceFragmentCompat fragment = (GsPreferenceFragmentCompat) getSupportFragmentManager().findFragmentByTag(tag);
        Integer profileFragmentNrToShow = null;
        if (fragment == null) {
            switch (tag) {
                case SettingsFragmentProfile.TAG0:
                    profileFragmentNrToShow = 0;
                    break;
                case SettingsFragmentProfile.TAG1:
                    profileFragmentNrToShow = 1;
                    break;
                case SettingsFragmentProfile.TAG2:
                    profileFragmentNrToShow = 2;
                    break;
                case SettingsFragmentMaster.TAG:
                default:
                    fragment = new SettingsFragmentMaster();
                    toolbar.setTitle(R.string.title_activity_settings);
                    break;
            }
        }
        if (profileFragmentNrToShow != null) {
            fragment = new SettingsFragmentProfile();
            Bundle bundle = new Bundle();
            bundle.putInt(SettingsFragmentProfile.TAG, profileFragmentNrToShow);
            fragment.setArguments(bundle);
            toolbar.setTitle(getResources().getStringArray(R.array.entries__profiles)[profileFragmentNrToShow]);
        }

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            t.addToBackStack(tag);
        }
        t.replace(R.id.settings_fragment_container, fragment, tag).commit();
    }

    public static class SettingsFragmentMaster extends GsPreferenceFragmentCompat {
        public static final String TAG = "settings.SettingsFragmentMaster";

        @Override
        public int getPreferenceResourceForInflation() {
            return R.xml.preferences_master;
        }

        @Override
        public String getFragmentTag() {
            return TAG;
        }

        @Override
        protected SharedPreferencesPropertyBackend getAppSettings(Context context) {
            return AppSettings.get();
        }

        @Override
        public Boolean onPreferenceClicked(Preference preference) {
            if (isAdded() && preference.hasKey()) {
                AppSettings settings = AppSettings.get();
                String key = preference.getKey();
                if (settings.isKeyEqual(key, R.string.profile_1)) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentProfile.TAG0, true);
                    return true;
                } else if (settings.isKeyEqual(key, R.string.profile_2)) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentProfile.TAG1, true);
                    return true;
                }
                if (settings.isKeyEqual(key, R.string.profile_3)) {
                    ((SettingsActivity) getActivity()).showFragment(SettingsFragmentProfile.TAG2, true);
                    return true;
                }
            }
            return false;
        }

        public void updateSummaries() {
            AppSettings appSettings = AppSettings.get();
            String selectedProfile = getResources().getStringArray(R.array.entries__profiles)[appSettings.getSelectedProfileNr()];

            findPreference(getString(R.string.pref_key__app_selected_profile)).setSummary(selectedProfile);

            // Load summaries
            int[] ids = new int[]{R.string.profile_1, R.string.profile_2, R.string.profile_3};
            for (int i = 0; i < ids.length; i++) {
                appSettings.loadProfile(i);
                Preference pref = findPreference(getString(ids[i]));
                if (pref != null) {
                    pref.setSummary(appSettings.getProfileSummary());
                }
            }
            appSettings.loadProfile(appSettings.getSelectedProfileNr());
        }


        @Override
        protected void onPreferenceScreenChanged(PreferenceFragmentCompat preferenceFragmentCompat, PreferenceScreen preferenceScreen) {
            super.onPreferenceScreenChanged(preferenceFragmentCompat, preferenceScreen);
            if (!TextUtils.isEmpty(preferenceScreen.getTitle())) {
                SettingsActivity a = (SettingsActivity) getActivity();
                if (a != null) {
                    a.toolbar.setTitle(preferenceScreen.getTitle());
                }
            }
        }
    }


    public static class SettingsFragmentProfile extends GsPreferenceFragmentCompat {
        public static final String TAG = "settings.SettingsFragmentProfile";
        public static final String TAG0 = "settings.SettingsFragmentProfile0";
        public static final String TAG1 = "settings.SettingsFragmentProfile1";
        public static final String TAG2 = "settings.SettingsFragmentProfile2";

        private AppSettings _appSettings;
        private int _profileNr = 0;

        @Override
        public int getPreferenceResourceForInflation() {
            return R.xml.preferences_profile;
        }

        @Override
        public String getFragmentTag() {
            return TAG + _profileNr;
        }

        @Override
        public String getSharedPreferencesName() {
            return "Profile" + getArguments().getInt(TAG, 0);
        }

        @Override
        protected SharedPreferencesPropertyBackend getAppSettings(Context context) {
            return AppSettings.get();
        }


        @Override
        protected void afterOnCreate(Bundle savedInstances, Context context) {
            super.afterOnCreate(savedInstances, context);
            if (getArguments() != null) {
                _profileNr = getArguments().getInt(TAG, 0);
            }
            _appSettings = AppSettings.get();
            _appSettings.loadProfile(_profileNr);
        }

        @Override
        protected void onPreferenceChanged(SharedPreferences prefs, String key) {
            super.onPreferenceChanged(prefs, key);
            _appSettings.setReloadRequired(true);
        }

        public void updateSummaries() {
            findPreference(getString(R.string.pref_key__profile_path_domain_and_directory)).setSummary(_appSettings.isProfileEmpty() ? "" : _appSettings.getProfilePathDomainAndDirectory());
            findPreference(getString(R.string.pref_key__profile_path_filename)).setSummary(_appSettings.getProfilePathFilename());
            findPreference(getString(R.string.pref_key__profile_login_username)).setSummary(_appSettings.getProfileLoginUsername());
        }
    }
}