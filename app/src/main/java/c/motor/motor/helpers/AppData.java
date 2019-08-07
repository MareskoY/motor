package c.motor.motor.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import c.motor.motor.R;


public class AppData {

    public static String getPreference(Context context, String preferanceKey){
        SharedPreferences settings = context.getSharedPreferences(String.valueOf(R.string.preference), 0);
        String result = settings.getString(preferanceKey, "country");
        return result;
    }
    public static void setPreference(Context context, String preferanceKey, String Value){
        SharedPreferences settings = context.getSharedPreferences(String.valueOf(R.string.preference), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(preferanceKey, Value);
        editor.apply();
    }

}
