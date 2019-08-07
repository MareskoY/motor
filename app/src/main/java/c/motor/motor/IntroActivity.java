package c.motor.motor;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import c.motor.motor.helpers.AppData;
import c.motor.motor.helpers.Helpers;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;

import static android.provider.Settings.Global.getString;

public class IntroActivity extends AppCompatActivity {

    private ImageView logo;

    private Boolean isReferal = false;

    private String documentName;
    private String countryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Initialize the Branch object
        //Branch.getAutoInstance(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(IntroActivity.this).clearDiskCache();
            }
        }).start();

        logo = (ImageView) findViewById(R.id.intro_logo);

        Animation introAnimation = AnimationUtils.loadAnimation(this,R.anim.intro_animation);
        logo.startAnimation(introAnimation);
        final Intent mainIntent = new Intent(IntroActivity.this, MainActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if(isReferal){
                        Intent advertActivity = new Intent(IntroActivity.this, AdvertActivity.class);
                        advertActivity.putExtra("documentName", documentName);
                        advertActivity.putExtra("country", countryName);
                        startActivity(mainIntent);
                        startActivity(advertActivity);
                    }else {
                        startActivity(mainIntent);
                    }
                    finish();
                }
            }
        };
        timer.start();
    }


    @Override
    public void onStart() {
        super.onStart();
        Branch branch = Branch.getInstance();

        // Branch init
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked -> was re-directed to this app
                    // params will be empty if no data found
                    // ... insert custom logic here ...
                    Log.i("BRANCH SDK 1", referringParams.toString());
                    if(referringParams != null){
                        try {
                            documentName = referringParams.get("documentName").toString();
                            countryName = referringParams.get("countryName").toString();

                            if( documentName != null && countryName != null){
                                isReferal = true;
//                                Intent advertActivity = new Intent(IntroActivity.this, AdvertActivity.class);
//                                advertActivity.putExtra("documentName", documentName);
//                                advertActivity.putExtra("country", countryName);
//                                startActivity(advertActivity);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.i("BRANCH SDK", error.getMessage());
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }



}
