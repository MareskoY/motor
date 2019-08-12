package c.motor.motor.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import c.motor.motor.R;

public class SharedPreferenceUtils {
    public static void setItem(Context context, String key, String value) {
        SharedPreferences settings = getSharedPreference(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getItem(Context context, String key, String defaultValue) {
        SharedPreferences settings = getSharedPreference(context);
        return settings.getString(key, defaultValue);
    }


    public static SharedPreferences getSharedPreference(Context context) {
        String preferenceKey = context.getString(R.string.preference);
        SharedPreferences sharedPreference = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE);
        return sharedPreference;
    }

    public static Of of(Context context) {
        return new Of(context);
    }

    public static class Of {
        private Context _context;
        Of(Context context) {
            _context = context;
        }

        public void setItem(String key, String value) {
            SharedPreferenceUtils.setItem(this._context, key, value);
        }

        public String getItem(String key, String defaultValue) {
            return SharedPreferenceUtils.getItem(this._context, key, defaultValue);
        }
    }
}
