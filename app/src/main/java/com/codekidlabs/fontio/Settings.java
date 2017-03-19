package com.codekidlabs.fontio;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;

import com.codekidlabs.bruno.services.CacheService;
import com.codekidlabs.bruno.services.TtfService;

public class Settings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction().replace(R.id.about_container, new AboutPreference()).commit();
        getFragmentManager().beginTransaction().replace(R.id.config_container, new ConfigPreference()).commit();
    }

    public static class AboutPreference extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_about);

            PreferenceScreen preferenceScreen = getPreferenceScreen();

            Preference changeLogPref = preferenceScreen.findPreference("changelog_pref");

            changeLogPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showChangelogDialog();
                    return true;
                }
            });
        }

        private void showChangelogDialog() {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            WebView changesView = new WebView(getActivity());
            changesView.loadUrl("https://github.com/codekidX/Fontio/blob/master/CHANGES.md");
            changesView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);

                    return true;
                }
            });

            alert.setView(changesView);
            alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            alert.show();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            View rootView = getView();
            ListView list = (ListView) rootView.findViewById(android.R.id.list);
            list.setDivider(null);
        }
    }

    public static class ConfigPreference extends PreferenceFragment {

        Context context;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_config);
            context = getActivity().getApplicationContext();

            PreferenceScreen preferenceScreen = getPreferenceScreen();

            SwitchPreference cacheSwitch = (SwitchPreference) preferenceScreen.findPreference("cache_switch");
            SwitchPreference installSwitch = (SwitchPreference) preferenceScreen.findPreference("autoinstall_switch");
            final Preference resetPref = preferenceScreen.findPreference("reset_cache");

            invalidateSwitch(resetPref);


            cacheSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean switchState = (boolean) newValue;

                    if(switchState) {
                        resetPref.setEnabled(true);
                    } else {
                        resetPref.setEnabled(false);
                    }
                    return true;
                }
            });

            resetPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle("Reset Cache ?")
                            .setMessage("You are about to reset font cache")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new TtfService().resetCache(context);
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create();
                    alertDialog.show();
                    return true;
                }
            });
        }

        private void invalidateSwitch(Preference resetPref) {
            if(!PreferenceManager.getDefaultSharedPreferences(context).contains("cache_switch")) {
                resetPref.setEnabled(false);
            }

            if(new CacheService(context).isLimitReached()) {
                resetPref.setSummary("Cache Limit Reached.");
            }
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            View rootView = getView();
            ListView list = (ListView) rootView.findViewById(android.R.id.list);
            list.setDivider(null);
        }
    }
}
