package c.motor.motor.helpers;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import c.motor.motor.R;

public class Helpers {
    public static String getUserCountry(Context context, String defaultCountry) {
        String country;
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) {
                if (simCountry.toLowerCase(Locale.US).equals("vn")){
                    country = "Vietnam";// SIM country code is available
                }else if (simCountry.toLowerCase(Locale.US).equals("ph")){
                    country = "Philippines";
                }else{
                    country = defaultCountry;
                }

                return country;
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    if (simCountry.toLowerCase(Locale.US).equals("vn")){
                        country = "Vietnam";// SIM country code is available
                    }else if (simCountry.toLowerCase(Locale.US).equals("ph")){
                        country = "Philippines";
                    }else{
                        country = defaultCountry;
                    }

                    return country;
                }
            }
        }
        catch (Exception e) { }
        return defaultCountry;
    }
    public static ArrayList<String> initArray(String[] values){
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i <values.length; i++){
            list.add(values[i]);
        }
        return list;
    }

    public static ArrayList<String> initArrayWithAll(String[] values){
        ArrayList<String> list = new ArrayList<>();
        list.add("All");
        for(int i = 0; i <values.length; i++){
            list.add(values[i]);
        }
        return list;
    }


    public static String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }


    public static int emptyCatImg(String item){
        int imgID = R.mipmap.empty_image;

        switch (item){
            case "bike":
                imgID = R.mipmap.bike;
                break;
            case "car":
                imgID = R.mipmap.car;
                break;
            case "motorcycle":
                imgID = R.mipmap.motorcycle;
                break;
            case "truck":
                imgID = R.mipmap.truck;
                break;
            case "cycle":
                imgID = R.mipmap.bicycle;
                break;
            case "other":
                imgID = R.mipmap.other;
                break;
            default:
                imgID = R.mipmap.empty_image;
        }
        return imgID;
    }

    public static int emptyCatIcon(String item){
        int imgID;

        switch (item){
            case "bike":
                imgID = R.drawable.ic_category_scooter;
                break;
            case "car":
                imgID = R.drawable.ic_category_small_car;
                break;
            case "motorcycle":
                imgID = R.drawable.ic_category_motorcycle;
                break;
            case "truck":
                imgID = R.drawable.ic_category_truck;
                break;
            case "cycle":
                imgID = R.drawable.ic_category_bicycle;
                break;
            case "other":
                imgID = R.drawable.ic_category_other;
                break;
            default:
                imgID = R.drawable.ic_category_other;
        }
        return imgID;
    }




}
