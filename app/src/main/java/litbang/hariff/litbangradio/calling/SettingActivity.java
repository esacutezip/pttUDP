package litbang.hariff.litbangradio.calling;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.WindowManager;


public class SettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final SharedPreferences aPref = PreferenceManager.getDefaultSharedPreferences(this);
        addPreferencesFromResource(R.xml.preference);
        findPreference("ipsender").setSummary(aPref.getString("ipsender", "192.168.66.39"));
        findPreference("portsender").setSummary(aPref.getString("portsender", "50005"));
        findPreference("portreceive").setSummary(aPref.getString("portreceive", "50004"));
        findPreference("portdata").setSummary(aPref.getString("portdata", "50006"));
        findPreference("portdata2").setSummary(aPref.getString("portdata2", "50007"));
        findPreference("frameplay").setSummary(aPref.getString("frameplay", "20"));


    }

    @Override
    protected void onResume() {
        super.onResume();

        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
    public void onSharedPreferenceChanged(SharedPreferences aPref, String aKey) {

        //if (aKey.equalsIgnoreCase("pref_dir_ftp")) {
            //MainActivity.setPortbino(aPref.getString(aKey, "9090"));
       //}

    }

}
