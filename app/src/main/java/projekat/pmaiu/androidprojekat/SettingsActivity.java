package projekat.pmaiu.androidprojekat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.Set;

import static projekat.pmaiu.androidprojekat.R.xml.preferences;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefTheme = getApplicationContext().getSharedPreferences("ThemePref", 0);
        if(!prefTheme.getBoolean("dark_mode", false)){
            setTheme(R.style.AppThemeLight);
        }else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_settings);
        addPreferencesFromResource(R.xml.preferences);

        final CheckBoxPreference darkMode = (CheckBoxPreference) getPreferenceManager().findPreference("dark_mode");

        darkMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
               // Toast.makeText(getApplicationContext(),"dark, " + newValue.toString(), Toast.LENGTH_SHORT).show();

                SharedPreferences pref = getApplicationContext().getSharedPreferences("ThemePref", 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("dark_mode");
                editor.putBoolean("dark_mode", (Boolean) newValue);
                editor.commit();

                startActivity(new Intent(SettingsActivity.this, EmailsActivity.class));

                startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
                finish();
                finishAffinity();


                return true;
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
