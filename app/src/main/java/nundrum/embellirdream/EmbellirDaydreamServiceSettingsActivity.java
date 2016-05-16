package nundrum.embellirdream;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

import android.support.v4.app.ActivityCompat;

/**
 * A settings Activity for {@link EmbellirDaydreamService}.
 * <p/>
 * A DreamService can only be used on devices with API v17+, so it is safe
 * for us to use a {@link PreferenceFragment} here.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class EmbellirDaydreamServiceSettingsActivity extends PreferenceActivity {

	private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String TAG = "EmbellirDreamSettings";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"onCreate()");

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new DreamPreferenceFragment()).commit();
    }
	public void onAttachedToWindow() {
		Log.v(TAG,"onAttachedToWindow()");
		ActivityCompat.requestPermissions(this,
				new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
				MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
	}

    public static class DreamPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.embellir_daydream_prefs);
        }



    }


}
