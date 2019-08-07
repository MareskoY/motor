package c.motor.motor.privateActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.Menu;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import c.motor.motor.CreateAdvertActivity;
import c.motor.motor.HomeFragment;
import c.motor.motor.IntroActivity;
import c.motor.motor.MainActivity;
import c.motor.motor.R;
import c.motor.motor.SearchFragment;
import c.motor.motor.addAdvertActivity.AddAdvertActivity;
import c.motor.motor.helpers.AppData;
import c.motor.motor.model.User;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class PrivateMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseUser user;

    private String userId;
    private User currentUser;
    private String userName, userEmail;
    private String titleAdvert;
    private int menuId;

    private View header;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;

    private FrameLayout frameLayout;
    private String currentCountry, currentLanguage;

    Dialog settingsDialog;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent != null){
            titleAdvert = intent.getStringExtra("title");
            menuId = intent.getIntExtra("id",1);
        }

        SharedPreferences currentPreferenceCountry = getApplicationContext().getSharedPreferences(String.valueOf(R.string.preference), 0);
        currentLanguage = currentPreferenceCountry.getString("language", null);
        currentCountry = AppData.getPreference(getApplicationContext(), "country");
        setContentView(R.layout.activity_private_main);

        drawer = findViewById(R.id.private_drawer_layout);

        //setup nav menu
        toolbar = findViewById(R.id.private_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(titleAdvert);
        toolbar.setTitleTextColor(R.color.colorGray);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        toolbar.setNavigationIcon(R.drawable.ic_menu_blue);
        navigationView = findViewById(R.id.private_nav_view);

        navigationView.getMenu().clear();

        navigationView.inflateMenu(R.menu.navigation_with_login);

        header = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(menuId).setChecked(true);

        final TextView userNameTextView = header.findViewById(R.id.private_nav_header_user_name);
        final TextView userEmailTextView = header.findViewById(R.id.private_nav_header_user_email);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        userId = user.getUid();
        firebaseFirestore.collection("USERS").document(userId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        DocumentSnapshot userDoc = task.getResult();

                        currentUser = User.documentSnapshotToUser(userDoc);
                        userEmail = currentUser.getEmail();
                        userName = currentUser.getUsername();

                        userNameTextView.setText(userName);
                        userEmailTextView.setText(userEmail);
                    } else {
                        userNameTextView.setText("Your account is broken");
                        userEmailTextView.setText("Your account is broken");
                    }
                } else {
                    userNameTextView.setText("Your account is broken");
                    userEmailTextView.setText("Your account is broken");
                }
            }
        });



        frameLayout = findViewById(R.id.private_main_frameLayout);
        if(menuId == 1){
            setupRecycleViewFavoriteFragment();
        }else{
            setupRecycleViewMyFragment();
        }

        settingsDialog = new Dialog(this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.private_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.private_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_search) {
            Intent addIntent = new Intent(PrivateMainActivity.this , MainActivity.class);
            startActivity(addIntent);
            PrivateMainActivity.this.finish();

        } else if (id == R.id.nav_saved_advert) {
            toolbar.setTitle("Favorite");
            setupRecycleViewFavoriteFragment();

        } else if (id == R.id.nav_my_adverts) {
            toolbar.setTitle("My adverts");
            setupRecycleViewMyFragment();
        } else if (id == R.id.nav_add_advert) {
            Intent addIntent = new Intent(PrivateMainActivity.this , AddAdvertActivity.class);
            startActivity(addIntent);
        } else if (id == R.id.nav_user_settings) {
            ShowSettingsPopup(getCurrentFocus());
        } else if (id == R.id.nav_Logout){
            mAuth.signOut();
            Intent addIntent = new Intent(PrivateMainActivity.this, IntroActivity.class);
            startActivity(addIntent);
            PrivateMainActivity.this.finish();
        }

        DrawerLayout drawer = findViewById(R.id.private_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupRecycleViewMyFragment(){
        MyAdvertsFragment fragment = new MyAdvertsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("country", currentCountry);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }

    private void setupRecycleViewFavoriteFragment(){
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle bundle = new Bundle();
        bundle.putString("country", currentCountry);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();

    }


    public void ShowSettingsPopup (View v){
        //ImageButton closeBtn;
        final TextView countryInput;
        final TextView languageInput;
        Button saveButton;

        ArrayList<String> countryItems = new ArrayList<>();
        ArrayList<String> languageItems = new ArrayList<>();

        settingsDialog.setContentView(R.layout.popup_settings_layout);
        //closeBtn =  settingsDialog.findViewById(R.id.setup_user_close_btn);
        countryInput = settingsDialog.findViewById(R.id.setup_user_country_input);
        languageInput = settingsDialog.findViewById(R.id.setup_user_language_input);
        saveButton = settingsDialog.findViewById(R.id.setup_user_save);

        initArray(countryItems, getResources().getStringArray(R.array.countryNameEng));
        initArray(languageItems, getResources().getStringArray(R.array.languageNameEng));
        countryInput.setText(currentCountry);
        languageInput.setText(currentLanguage);

        final SpinnerDialog spinnerDialogCountry = new SpinnerDialog(PrivateMainActivity.this,countryItems,"Choose country","close");
        spinnerDialogCountry.bindOnSpinerListener(new OnSpinerItemClick() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(String item, int position) {
                countryInput.setText(item);
            }
        });

        countryInput.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                spinnerDialogCountry.showSpinerDialog();
                countryInput.setTextColor(R.color.colorGray);

            }
        });


        final SpinnerDialog spinnerDialogLanguage = new SpinnerDialog(PrivateMainActivity.this,languageItems,"Choose language","close");
        spinnerDialogLanguage.bindOnSpinerListener(new OnSpinerItemClick() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(String item, int position) {
                languageInput.setText(item);
            }
        });

        languageInput.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                spinnerDialogLanguage.showSpinerDialog();
                languageInput.setTextColor(R.color.colorGray);

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getApplicationContext().getSharedPreferences(String.valueOf(R.string.preference), 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("country", countryInput.getText().toString());
                editor.putString("language", languageInput.getText().toString());
                editor.apply();


                Intent intent = new Intent(PrivateMainActivity.this , IntroActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                settingsDialog.cancel();
            }
        });


        settingsDialog.show();
    }

    private void initArray(ArrayList<String> list, String[] values){
        list.clear();
        for(int i = 0; i <values.length; i++){
            list.add(values[i]);
        }
    }

}
