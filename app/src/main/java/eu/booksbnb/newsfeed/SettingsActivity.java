package eu.booksbnb.newsfeed;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsfeedPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);
            Preference keyword = findPreference(getString(R.string.settings_keyword_key));
            Preference section = findPreference(getString(R.string.settings_filter_category_key));
            bindPreferenceSummaryToValue(keyword);
            bindPreferenceSummaryToValue(section);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            //Update displayed preference summary after change
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPref = (ListPreference) preference;
                int prefIndex = listPref.findIndexOfValue(stringValue);
                if (prefIndex >=0) {
                    CharSequence[] labels = listPref.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        public void bindPreferenceSummaryToValue(Preference pref) {
            pref.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(pref.getContext());
            String prefString = preferences.getString(pref.getKey(), " ");
            onPreferenceChange(pref, prefString);
        }

    }
}
